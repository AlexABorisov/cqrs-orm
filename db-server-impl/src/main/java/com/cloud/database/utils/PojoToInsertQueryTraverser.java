package com.cloud.database.utils;

import com.cassandra.utils.CassandraUtils;
import com.cloud.utils.ClassUtils;
import com.cloud.generator.IDGenerator;
import com.cloud.utils.Pair;
import com.datastax.driver.core.querybuilder.Insert;

import java.lang.reflect.Field;

/**
* Created by albo1013 on 11.12.2015.
*/
public class PojoToInsertQueryTraverser implements ClassUtils.ObjectFieldHandler {
    private final IDGenerator idGenerator;
    private Insert insert;
    private Integer id;

    public PojoToInsertQueryTraverser(Insert insert, IDGenerator idGenerator) {
        this.insert = insert;
        this.idGenerator = idGenerator;
    }

    @Override
    public Object onField(Object instance, Field field)  {
        field.setAccessible(true);
        try {
            Object value = field.get(instance);
            do{
                //object is primitive and pk
                boolean isPrimitive = ClassUtils.isPrimitive(field);
                boolean isId = CassandraUtils.ID.equals(field.getName());
                if (isPrimitive && isId){
                    if (value == null || (value instanceof Integer && (Integer)value == 0) ){
                        value = id = idGenerator.getNextId();
                    }
                    field.set(instance,id);
                    insert.value(field.getName(), value);
                    break;
                }
                if (isId && !isPrimitive){
                    return ClassUtils.traversePojo(value, this);
                }

                if (isPrimitive && !isId){
                    insert.value(field.getName(), value);
                    break;
                }
                Pair<Integer, Field> idField = CassandraUtils.getIdField(value);
                insert.value(field.getName(), idField.first);

                //Here we have non primitive pojo we have to extract id here;



            }while (false);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getId() {
        return id;
    }
}
