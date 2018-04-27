package com.cassandra.utils;

import com.cloud.utils.ClassUtils;
import com.cloud.utils.Pair;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.schemabuilder.Create;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cassandra.core.RowMapper;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by albo1013 on 27.11.2015.
 */
public class CassandraUtils {
    public static final String ID = "id";

    private static final String CQL = "select count(1) from %s where id = %s";

    static final Map<Class, DataType> classDataTypeMap = new HashMap<Class, DataType>();

    static {
        classDataTypeMap.put(String.class, DataType.ascii());
        classDataTypeMap.put(Long.class, DataType.bigint());
        classDataTypeMap.put(ByteBuffer.class, DataType.blob());
        classDataTypeMap.put(boolean.class, DataType.cboolean());
        classDataTypeMap.put(Boolean.class, DataType.cboolean());
        classDataTypeMap.put(long.class, DataType.cint());
        classDataTypeMap.put(Long.class, DataType.cint());
        classDataTypeMap.put(BigDecimal.class, DataType.decimal());
        classDataTypeMap.put(double.class, DataType.cdouble());
        classDataTypeMap.put(Double.class, DataType.cdouble());
        classDataTypeMap.put(Float.class, DataType.cfloat());
        classDataTypeMap.put(InetAddress.class, DataType.inet());
        classDataTypeMap.put(int.class, DataType.cint());
        classDataTypeMap.put(Integer.class, DataType.cint());
        classDataTypeMap.put(String.class, DataType.text());
        classDataTypeMap.put(Date.class, DataType.timestamp());
        classDataTypeMap.put(UUID.class, DataType.uuid());
        classDataTypeMap.put(String.class, DataType.varchar());
        classDataTypeMap.put(BigInteger.class, DataType.varint());
        classDataTypeMap.put(UUID.class, DataType.timeuuid());
        //classDataTypeMap.put( List.class,           DataType.list()      );
        //classDataTypeMap.put( Set.class,            DataType.set()       );
        //classDataTypeMap.put( Map.class,            DataType.map()       );
        //classDataTypeMap.put( UDTValue.class,       DataType.UDT       );

    }

    public static class ResultSetToPojoTraverser implements ClassUtils.ObjectFieldHandler {
        private static final Logger LOG = LoggerFactory.getLogger(ResultSetToPojoTraverser.class);
        private final CassandraOperations operations;
        private final Row row;

        public ResultSetToPojoTraverser(CassandraOperations operations, Row row) {
            this.operations = operations;
            this.row = row;
        }

        @Override
        public Object onField(Object instance, Field field) {
            Class<?> fieldType = field.getType();
            do {
                Object to = ConverterUtils.to(new RowWrapper(row, field.getName()), fieldType);
                if (to != null) {
                    return to;
                }
                try {
                    if (ID.equals(field.getName())) {
                        return ClassUtils.traversePojo(fieldType.newInstance(), this);
                    } else {
                        final Class<?> type = field.getType();
                        Select select = QueryBuilder.select().all().from(type.getSimpleName());
                        select.where(QueryBuilder.eq(ID, row.getInt(field.getName())));
                        select.limit(1);
                        return operations.queryForObject(select, new RowMapper<Object>() {
                            @Override
                            public Object mapRow(Row row, int rowNum) throws DriverException {
                                Object instance1 = null;
                                try {
                                    instance1 = type.newInstance();
                                    return ClassUtils.traversePojo(instance1, new ResultSetToPojoTraverser(operations, row));
                                } catch (Exception e) {
                                    LOG.warn("Exception during traverse object {} {} {}", type.getName(), type.toString(), instance1, e);
                                }
                                return null;
                            }
                        });
                    }
                } catch (Exception e) {
                    LOG.warn("Exception during traverse object {} {} {}", fieldType.getName(), fieldType.toString(), instance, e);
                }

            } while (false);
            return null;
        }
    }

    public static class PKTraverser implements ClassUtils.ClassFieldHandler {
        private final Create create;
        private boolean isFirst = true;

        public PKTraverser(Create create) {
            this.create = create;
        }

        @Override
        public void onField(Class clazz, Field field) {
            if (isFirst) {
                create.addPartitionKey(field.getName(), classDataTypeMap.get(field.getType()));
                isFirst = false;
            } else {
                create.addClusteringColumn(field.getName(), classDataTypeMap.get(field.getType()));
            }
        }
    }

    public static boolean isCassandraObject(Class clazz) {
        return ReflectionUtils.findField(clazz, new org.springframework.util.ReflectionUtils.FieldFilter() {
            @Override
            public boolean matches(Field field) {
                return field.getName().equals("id");
            }
        }) != null;
    }

    public static boolean isObjectExist(CassandraOperations cassandraOperations, Object object) {
        Pair<Integer, Field> idField = getIdField(object);
        return idField == null ? false : cassandraOperations.queryForObject(String.format(CQL, object.getClass().getSimpleName(), idField.first), new RowMapper<Long>() {
            @Override
            public Long mapRow(Row row, int rowNum) throws DriverException {
                return row.getLong(0);
            }
        }) != 0L;

    }

    public static <T> Map<T, Map<String, Object>> traversePojoToMap(T instance) {
        T currentInstance = instance;

        Class tClass = currentInstance.getClass();
        LinkedList<Class> stack = new LinkedList<Class>();
        Map<T, Map<String, Object>> result = new HashMap<T, Map<String, Object>>();
        stack.add(tClass);
        Map<String, Object> objectMap = new HashMap<String, Object>();
        while (!stack.isEmpty()) {
            tClass = stack.remove();
            if (!ClassUtils.isProxy(tClass)) {
                for (Field field : tClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = null;
                    try {
                        value = field.get(currentInstance);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    //Simple type to insert to map
                    if (ClassUtils.isPrimitive(field)) {
                        objectMap.put(field.getName(), value);
                    } else {
                        Set<Field> ids = org.reflections.ReflectionUtils.getFields(value.getClass(), new Predicate<Field>() {
                            @Override
                            public boolean apply(Field field) {
                                return field.getName().equals(ID);
                            }
                        });
                        if (ids != null && !ids.isEmpty()) {
                            try {
                                Field id = ids.iterator().next();
                                id.setAccessible(true);
                                objectMap.put(field.getName(), id.get(value));
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        result.putAll((Map<? extends T, ? extends Map<String, Object>>) traversePojoToMap(value));
                    }
                }
            }

            tClass = tClass.getSuperclass();
            if (tClass != Object.class) {
                stack.add(tClass);
            }
        }
        if (!objectMap.isEmpty()) {
            result.put(instance, objectMap);
        }
        return result;
    }

    public static void traverseMapToPojo(final Object instance, final Map<String, Object> object) {
        ClassUtils.traverseClass(instance.getClass(), new ClassUtils.ClassFieldHandler() {
            @Override
            public void onField(Class clazz, Field field) {
                field.setAccessible(true);
                try {
                    if (ClassUtils.isPrimitive(field)) {
                        Object to = ConverterUtils.to(object.get(field.getName()), field.getType());
                        if (to != null) {
                            field.set(instance, to);
                        }
                    } else {
                        Object o = field.getType().newInstance();
                        field.set(instance, o);
                        traverseMapToPojo(o, (Map<String, Object>) object.get(field.getName()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static Pair<Integer, Field> getIdField(Object obj) {
        Object idValue = null;
        Field idField = org.reflections.ReflectionUtils.getFields(obj.getClass(), new Predicate<Field>() {
            @Override
            public boolean apply(Field field) {
                return field.getName().equals(ID);
            }
        }).iterator().next();
        try {
            if (idField != null) {
                idField.setAccessible(true);
                if (!ClassUtils.isPrimitive(idField)) {
                    idValue = idField.get(obj);
                    idField = org.reflections.ReflectionUtils.getFields(obj.getClass(), new Predicate<Field>() {
                        @Override
                        public boolean apply(Field field) {
                            return field.getName().equals(ID);
                        }
                    }).iterator().next();
                    idValue = idField.get(idValue);
                } else {
                    idValue = idField.get(obj);
                }
                return Pair.create(Integer.valueOf(idValue.toString()), idField);
            } else {
                return null;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

    }

    static class PK {
        int pk = 9;
        int pk1 = 10;
    }

    static class A {

        int i = 5;
        int im = 6;
        int id = 1;
    }

    public static void main(String s[]) throws IllegalAccessException {
        A a = new A();
        Create create = SchemaBuilder.createTable(A.class.getSimpleName());
        ClassUtils.traverseClass(A.class, new ClassToTableTraverser(create));
        System.out.println(create.toString());


    }

}

