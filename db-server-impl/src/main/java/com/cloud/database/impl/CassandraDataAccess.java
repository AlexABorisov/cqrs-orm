package com.cloud.database.impl;

import com.cassandra.utils.*;
import com.cloud.database.cassandra.PersistenceCapableHandler;
import com.cloud.database.api.DataAccessSingle;
import com.cloud.database.changelog.ChangelogDao;
import com.cloud.database.events.*;
import com.cloud.generator.IDGenerator;
import com.cloud.utils.ClassUtils;
import com.cloud.utils.Pair;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.querybuilder.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.RowMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * Created by albo1013 on 17.11.2015.
 */
@Component
public class CassandraDataAccess<T> implements DataAccessSingle<T, Integer>,ApplicationContextAware {
    private static final String ID = "id";
    private static final Logger LOG = LoggerFactory.getLogger(CassandraDataAccess.class);

    @Autowired
    private CassandraOperations cassandraTemplate;

    @Autowired
    private IDGenerator idGenerator;

    private ApplicationContext applicationContext;

    @Override
    public T getObjectById(Integer integer, final Class<T> clazz) {
        Select select = QueryBuilder.select().all().from(clazz.getSimpleName());
        select.where(QueryBuilder.eq(ID, integer));
        select.limit(1);
        return cassandraTemplate.queryForObject(select, new RowMapper<T>() {
                    @Override
                    public T mapRow(Row row, int rowNum) throws DriverException {
                        T instance = null;
                        try {
                            instance = clazz.newInstance();
                            return ClassUtils.traversePojo(instance, new CassandraUtils.ResultSetToPojoTraverser(cassandraTemplate, row));
                        } catch (Exception e) {
                            LOG.warn("Exception in mapper", e);
                            return null;
                        }
                    }
                }
        );
    }

    @Override
    public T create(T object) {
        PersistenceCapable proxy = ProxyFactoryCreator.createProxy(object,
                new PersistenceCapableHandler(object).
                        setCassandraOperation(cassandraTemplate).
                        setIdGenerator(idGenerator).
                        setApplicationContext(applicationContext)
        );

        int persist = proxy.persist(PersistenceCapable.Event.Create);
        return (T) proxy.getOriginalObject();

    }


    @Override
    public void update(Integer integer, T object) {
        PersistenceCapable proxy = ProxyFactoryCreator.createProxy(object,
                new PersistenceCapableHandler(object).
                        setCassandraOperation(cassandraTemplate).
                        setIdGenerator(idGenerator).
                        setApplicationContext(applicationContext)
        );
        int persist = proxy.persist(PersistenceCapable.Event.Update);


    }



    @Override
    public void delete(Integer integer, Class<T> clazz) {
        Delete delete = QueryBuilder.delete().from(clazz.getSimpleName());
        delete.ifExists().where(QueryBuilder.eq(ID, integer));
        cassandraTemplate.execute(delete);
        applicationContext.publishEvent(new DeleteObjectEvent(clazz.getSimpleName(),integer));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.applicationContext = applicationContext;
    }
}
