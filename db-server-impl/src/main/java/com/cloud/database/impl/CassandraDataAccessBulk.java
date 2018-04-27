package com.cloud.database.impl;

import com.cloud.database.api.DataAccessBulk;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Created by albo1013 on 17.11.2015.
 */
@Component
public class CassandraDataAccessBulk <T> implements DataAccessBulk<T,Integer> {
    @Override
    public Collection<T> getObjectById(List<Integer> integers, Class<Collection<T>> clazz) {

        return null;
    }

    @Override
    public Collection<T> create(Collection<T> object) {
        return null;
    }

    @Override
    public void update(List<Integer> integers, Collection<T> object) {

    }

    @Override
    public void delete(List<Integer> integers, Class<Collection<T>> clazz) {

    }
}
