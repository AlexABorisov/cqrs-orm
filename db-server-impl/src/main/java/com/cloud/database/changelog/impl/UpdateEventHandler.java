package com.cloud.database.changelog.impl;

import com.cloud.database.changelog.ChangeLog;
import com.cloud.database.changelog.ChangelogDao;
import com.cloud.database.changelog.ChangelogPK;
import com.cloud.database.events.CreateObjectEvent;
import com.cloud.database.events.UpdateObjectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * Created by albo1013 on 04.12.2015.
 */
@Component
public class UpdateEventHandler implements ApplicationListener<UpdateObjectEvent> {
    @Autowired
    private ChangelogDao dao;


    @Override
    public void onApplicationEvent(UpdateObjectEvent updateObjectEvent) {
        ChangeLog changeLog = new ChangeLog();
        changeLog.setCommand(ChangeLog.Command.Update.name());
        ChangelogPK changelogPK = new ChangelogPK();
        changeLog.setId(changelogPK);
        changelogPK.setId(updateObjectEvent.getId());
        changelogPK.setTime_stamp(new Date(updateObjectEvent.getTimestamp()));
        changelogPK.setType(updateObjectEvent.getType());
        for (Map.Entry<String,Object> item : updateObjectEvent.getProperties().entrySet()){
            changeLog.getProperties().put(item.getKey(),item.getValue().toString());
        }
        dao.save(changeLog);
    }
}
