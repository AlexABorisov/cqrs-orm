package com.cloud.database.cassandra;

import com.cassandra.utils.*;
import com.cloud.database.events.CreateObjectEvent;
import com.cloud.database.events.ObjectEvent;
import com.cloud.database.events.UpdateObjectEvent;
import com.cloud.generator.IDGenerator;
import com.cloud.utils.ClassUtils;
import com.cloud.utils.Pair;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.querybuilder.*;
import com.fsm.EventHandler;
import com.fsm.StateDecorator;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.springframework.cassandra.core.ResultSetExtractor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.cassandra.core.CassandraOperations;
import pt.fsm.FSM;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by albo1013 on 22.12.2015.
 */
class ObjectFSM<STATES extends Enum<?>, EVENT> extends FSM<STATES, EVENT> {
    public static final String ID = "id";
    private String type;
    private Map<String, Object> propertyMap = new HashMap<>();
    private Map<String, ObjectFSM> fsmMap = new HashMap<>();
    private Batch batch = QueryBuilder.batch();
    private CassandraOperations cassandraOperations;
    private IDGenerator idGenerator;
    private Map<String, Object> parentMap = new HashMap<>();
    private List<ObjectEvent<Integer>> changeLog = new LinkedList<>();
    private String parentType;


    public ObjectFSM(String type) {
        super();
        this.type = type;
    }

    public List<ObjectEvent<Integer>> getChangeLog() {
        return changeLog;
    }

    public Batch getBatch() {
        return batch;
    }

    public ObjectFSM<STATES, EVENT> setBatch(Batch batch) {
        this.batch = batch;
        return this;
    }

    public ObjectFSM init(final Object object) {
        init(new StateDecorator(States.IDLE).addHandlerWithTransit(PersistenceCapable.Event.class, new EventHandler<PersistenceCapable.Event>() {
                    @Override
                    public int invoke(PersistenceCapable.Event evt) {

                        Select select = QueryBuilder.select().countAll().from(type);
                        Object value = propertyMap.get(ID);
                        if (value == null || ConverterUtils.to(value.toString(), Integer.class) == 0) {
                            propertyMap.remove(ID);
                            return 0;
                        }
                        select.where(QueryBuilder.eq(ID, value));
                        Long count = cassandraOperations.query(select, new ResultSetExtractor<Long>() {
                            @Override
                            public Long extractData(ResultSet rs) throws DriverException, DataAccessException {
                                Row row = rs.one();
                                return row == null ? 0 : row.getLong(0);
                            }
                        });
                        if (count == 0L) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                }, States.NEW, States.EXIST),
                //Объекта нет в базе и у него есть id
                new StateDecorator(States.NEW).addHandler(PersistenceCapable.Event.class, new EventHandler<PersistenceCapable.Event>() {
                    @Override
                    public int invoke(PersistenceCapable.Event evt) {
                        switch (evt) {
                            case Update:
                            case Create:
                                Insert insert = QueryBuilder.insertInto(type);
                                for (Map.Entry<String, Object> item : propertyMap.entrySet()) {
                                    insert.value(item.getKey(), item.getValue());
                                }
                                batch.add(insert);
                                changeLog.add(new CreateObjectEvent(type, (Integer) propertyMap.get(ID), propertyMap));
                                break;
                            case Delete:
                                //Невозможный кейс так как нельзя удалить того чего нет
                                break;
                        }
                        return 0;
                    }
                }).setEnterHandler(new EventHandler<PersistenceCapable.Event>() {
                    @Override
                    public int invoke(PersistenceCapable.Event evt) {
                        if (propertyMap.containsKey(ID))
                            return 0;
                        int nextId = idGenerator.getNextId();
                        propertyMap.put(ID, nextId);
                        if (parentType != null) {
                            parentMap.put(parentType, nextId);
                        }
                        Pair<Integer, Field> idField = CassandraUtils.getIdField(object);
                        idField.second.setAccessible(true);
                        try {
                            idField.second.set(object,nextId);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                }),
                new StateDecorator(States.EXIST).addHandler(PersistenceCapable.Event.class, new EventHandler<PersistenceCapable.Event>() {
                    @Override
                    public int invoke(PersistenceCapable.Event evt) {
                        switch (evt) {
                            case Update:
                            case Create:
                                Select select = QueryBuilder.select().all().from(type);
                                select.where(QueryBuilder.eq(ID, propertyMap.get(ID)));
                                Map<String, Object> result = cassandraOperations.query(select, new ResultSetExtractor<Map>() {

                                    @Override
                                    public Map extractData(ResultSet rs) throws DriverException, DataAccessException {
                                        Map<String, Object> result = new HashMap<String, Object>();

                                        for (Row row : rs.all()) {
                                            ColumnDefinitions columnDefinitions = row.getColumnDefinitions();
                                            for (ColumnDefinitions.Definition definition : columnDefinitions.asList()) {
                                                result.put(definition.getName(), ConverterUtils.to(new RowWrapper(row, definition.getName()), propertyMap.get(definition.getName()).getClass()));
                                            }
                                        }
                                        return result;
                                    }
                                });


                                MapDifference<String, Object> difference = Maps.difference(propertyMap, result);
                                if (difference.areEqual())
                                    break;
                                Update update = QueryBuilder.update(type);
                                update.where(QueryBuilder.eq(ID, propertyMap.get(ID)));
                                Map<String, MapDifference.ValueDifference<Object>> valueDifferenceMap = difference.entriesDiffering();
                                UpdateObjectEvent updateObjectEvent = new UpdateObjectEvent(type, (Integer) (propertyMap.get(ID)));
                                for (Map.Entry<String, MapDifference.ValueDifference<Object>> item : valueDifferenceMap.entrySet()) {
                                    update.with(QueryBuilder.set(item.getKey(), item.getValue().leftValue()));
                                    updateObjectEvent.getProperties().put(item.getKey(), item.getValue().leftValue());
                                }
                                batch.add(update);
                                changeLog.add(updateObjectEvent);
                                break;
                            case Delete:
                                Delete delete = QueryBuilder.delete().from(type).ifExists();
                                delete.where(QueryBuilder.eq(ID, propertyMap.get(ID)));
                                batch.add(delete);
                                break;
                        }
                        return 0;
                    }
                })
        );
        start();
        ClassUtils.traverseClass(object.getClass(), new ClassUtils.ClassFieldHandler() {
            @Override
            public void onField(Class clazz, Field field) {
                try {
                    onFieldCustom(clazz, field);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            public void onFieldCustom(Class clazz, Field field) throws IllegalAccessException {
                field.setAccessible(true);
                Object value = field.get(object);
                if (value == null)
                    return;
                if (ClassUtils.isPrimitive(field)) {
                    propertyMap.put(field.getName(), value);
                    if (ID.equals(field.getName()) && parentType != null){
                        parentMap.put(parentType,value);
                    }
                } else {
                    if (ID.equals(field.getName())){
                        ClassUtils.traverseClass(field.getType(),this);
                    }else{
                        fsmMap.put(field.getName(), new ObjectFSM(field.getType().getSimpleName()).
                                    setBatch(batch).
                                    setCassandraOperation(cassandraOperations).
                                    setIdGenerator(idGenerator).
                                    setParent(field.getName(),propertyMap).
                                    setChangeLog(changeLog).
                                    init(value)
                    );
                    }
                }
            }
        });
        return this;
    }

    public ObjectFSM<STATES, EVENT> setChangeLog(List<ObjectEvent<Integer>> changeLog) {
        this.changeLog = changeLog;
        return this;
    }

    @Override
    public void handleEvent(EVENT e) {
        super.handleEvent(e);
        for (Map.Entry<String, ObjectFSM> fsm : fsmMap.entrySet()) {
            fsm.getValue().handleEvent(e);
        }
    }

    public ObjectFSM<STATES, EVENT> setCassandraOperation(CassandraOperations cassandraOperation) {
        this.cassandraOperations = cassandraOperation;
        return this;
    }

    public ObjectFSM<STATES, EVENT> setIdGenerator(IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
        return this;
    }

    public ObjectFSM<STATES, EVENT> setParent(String name, Map<String, Object> parent) {
        this.parentMap = parent;
        this.parentType  = name;
        return this;
    }
}
