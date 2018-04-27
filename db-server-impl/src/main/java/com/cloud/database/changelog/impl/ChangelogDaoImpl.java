package com.cloud.database.changelog.impl;

import com.cloud.database.changelog.ChangelogPK;
import com.cloud.database.changelog.repo.ChangelogRepository;
import com.cloud.database.changelog.ChangelogDao;
import com.cloud.database.changelog.ChangeLog;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.repository.MapId;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by albo1013 on 24.11.2015.
 */
@Component
public class ChangelogDaoImpl implements ChangelogDao {

    @Autowired
    private CassandraOperations cassandraTemplate;

    @Autowired
    private ChangelogRepository changeRepository;


    @Override
    public List<ChangeLog> getEventsForType(String type, Integer objectID, Date timestamp) {
        Select query = QueryBuilder.select().all().from("changelog").allowFiltering();
        query.where(QueryBuilder.gt("time_stamp", timestamp)).and(QueryBuilder.eq("id", objectID)).and(QueryBuilder.eq("type", type));
        ResultSet resultSet = cassandraTemplate.query(query);
        return getChangelogItems(resultSet);
    }

    @Override
    public List<ChangeLog> getEventsForType(String type, Integer objectID) {
        Select query = QueryBuilder.select().all().from("changelog").allowFiltering();
        query.where(QueryBuilder.eq("id", objectID)).and(QueryBuilder.eq("type", type));
        ResultSet resultSet = cassandraTemplate.query(query);
        return getChangelogItems(resultSet);
    }

    private List<ChangeLog> getChangelogItems(ResultSet resultSet) {
        List<ChangeLog> result = new ArrayList<ChangeLog>(100);
        for (Row row : resultSet.all()){
            ChangeLog changelogItem = new ChangeLog();
            ChangelogPK pk = new ChangelogPK();
            changelogItem.setId(pk);
            pk.setType(row.getString("type"));
            pk.setId(row.getInt("id"));
            pk.setTime_stamp(row.getDate("time_stamp"));
            changelogItem.getProperties().putAll(row.getMap("properties", String.class, String.class));
            changelogItem.setCommand(row.getString("command"));
            result.add(changelogItem);
        }

        return result;
    }

    public <S extends ChangeLog> S save(S s) {
        return changeRepository.save(s);
    }

    public <S extends ChangeLog> Iterable<S> save(Iterable<S> iterable) {
        return changeRepository.save(iterable);
    }

    public ChangeLog findOne(MapId mapId) {
        return changeRepository.findOne(mapId);
    }

    public boolean exists(MapId mapId) {
        return changeRepository.exists(mapId);
    }

    public Iterable<ChangeLog> findAll() {
        return changeRepository.findAll();
    }

    public Iterable<ChangeLog> findAll(Iterable<MapId> iterable) {
        return changeRepository.findAll(iterable);
    }

    public long count() {
        return changeRepository.count();
    }

    public void delete(MapId mapId) {
        changeRepository.delete(mapId);
    }

    public void delete(ChangeLog changelogItem) {
        changeRepository.delete(changelogItem);
    }

    public void delete(Iterable<? extends ChangeLog> iterable) {
        changeRepository.delete(iterable);
    }

    public void deleteAll() {
        changeRepository.deleteAll();
    }
}
