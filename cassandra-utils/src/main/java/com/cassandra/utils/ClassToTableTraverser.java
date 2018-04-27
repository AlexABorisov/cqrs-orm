package com.cassandra.utils;

import com.cloud.utils.ClassUtils;
import com.datastax.driver.core.schemabuilder.Create;

import java.lang.reflect.Field;

/**
* Created by albo1013 on 14.12.2015.
*/
public class ClassToTableTraverser implements ClassUtils.ClassFieldHandler{
    final private Create create;

    public ClassToTableTraverser(Create create) {
        this.create = create;
        this.create.ifNotExists();
    }

    @Override
    public void onField(Class clazz, Field field) {
        do{
            if (CassandraUtils.ID.equals(field.getName()) && !ClassUtils.isPrimitive(field)){
                ClassUtils.traverseClass(field.getType(), new CassandraUtils.PKTraverser(create));
                break;
            }
            if (CassandraUtils.ID.equals(field.getName())){
                create.addPartitionKey(field.getName(), CassandraUtils.classDataTypeMap.get(field.getType()));
                break;
            }
            if (ClassUtils.isPrimitive(field)){
                create.addColumn(field.getName(), CassandraUtils.classDataTypeMap.get(field.getType()));
                break;
            }
            //Other complex types will be represented as integer
            create.addColumn(field.getName(), CassandraUtils.classDataTypeMap.get(Integer.class));
        }while (false);

    }
}
