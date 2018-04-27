package com.cloud.database.api;

/**
 * Created by albo1013 on 17.11.2015.
 */

public interface DataAccessSingle<T,ID> {
    T getObjectById(ID id, Class<T> clazz);
    T create(T object);
    void update (ID id,T object);
    void delete(ID id, Class<T> clazz);
}
