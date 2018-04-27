package com.cloud.generator;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.querybuilder.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by albo1013 on 03.12.2015.
 */
@Component
public class IDGeneratorImpl implements IDGenerator{

    private AtomicInteger integer;
    private int step = 1000;
    private AtomicInteger currentValue = new AtomicInteger(0);

    @Autowired
    private CassandraOperations cassandraTemplate;

    @PostConstruct
    public void init(){
//        Select select = QueryBuilder.select().from("generator");
//        select.limit(1);
//        ResultSet query = cassandraTemplate.query(select);
//        integer = new AtomicInteger(query.all().iterator().next().getInt("id")*step);
    }

    @PreDestroy
    public void destroy(){
        updateDB();
    }

    private void updateDB() {
//        Update update = QueryBuilder.update("generator");
//        update.where(QueryBuilder.eq("id",integer.intValue()));
//        update.with(QueryBuilder.set("id",(integer.intValue()/step + 1) ));
//        cassandraTemplate.execute(update);
    }

    @Override
    public int getNextId() {
//        if (currentValue.intValue() >= integer.intValue() ){
//            updateDB();
//            int i = integer.incrementAndGet();
//            return i;
//        }
//        return currentValue.incrementAndGet();
        return (int) System.currentTimeMillis();
    }
}
