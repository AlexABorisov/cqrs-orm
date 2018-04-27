package com.cassandra.utils;

import org.springframework.data.cassandra.core.CassandraOperations;

import java.util.Map;

/**
* Created by albo1013 on 16.12.2015.
*/
public interface PersistenceCapable {
    public static enum Event{
        Init,Create,Update,Delete
    }


    Integer getRealObjectId();
    String getRealObjectType();
    Map<Object, Map<String, Object>> getPropertyMap();
    int persist(Event event);
    Object getOriginalObject();
    void init();
}
