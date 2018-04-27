package com.cloud.database.changelog.impl;

import com.cloud.database.changelog.ChangeLog;
import com.cloud.database.changelog.ChangelogDao;
import com.cloud.database.changelog.ChangelogPK;
import com.cloud.database.events.CreateObjectEvent;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.RowMapper;
import org.springframework.context.ApplicationListener;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * Created by albo1013 on 04.12.2015.
 */
@Component
public class CreateObjectEventHandler implements ApplicationListener<CreateObjectEvent> {
    @Autowired
    private ChangelogDao dao;

//    @Autowired
//    private CassandraOperations cassandraOperations;


    

    @Override
    public void onApplicationEvent(CreateObjectEvent createObjectEvent) {
        ChangeLog changeLog = new ChangeLog();
        changeLog.setCommand(ChangeLog.Command.Create.name());
        ChangelogPK changelogPK = new ChangelogPK();
        changeLog.setId(changelogPK);
        changelogPK.setId(createObjectEvent.getId());
        changelogPK.setTime_stamp(new Date(createObjectEvent.getTimestamp()));
        changelogPK.setType(createObjectEvent.getType());
        for (Map.Entry<String,Object> item : createObjectEvent.getProperties().entrySet()){
            changeLog.getProperties().put(item.getKey(),item.getValue().toString());
        }
        dao.save(changeLog);
    }
}
