package com.cloud.database.changelog.impl;

import com.cloud.database.changelog.ChangeLog;
import com.cloud.database.changelog.ChangelogDao;
import com.cloud.database.changelog.ChangelogPK;
import com.cloud.database.events.CreateObjectEvent;
import com.cloud.database.events.DeleteObjectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by albo1013 on 04.12.2015.
 */
@Component
public class DeleteObjectEventHandler implements ApplicationListener<DeleteObjectEvent> {
    @Autowired
    private ChangelogDao dao;


    @Override
    public void onApplicationEvent(DeleteObjectEvent deleteObjectEvent) {
        ChangeLog changeLog = new ChangeLog();
        changeLog.setCommand(ChangeLog.Command.Delete.name());
        ChangelogPK changelogPK = new ChangelogPK();
        changeLog.setId(changelogPK);
        changelogPK.setId(deleteObjectEvent.getId());
        changelogPK.setTime_stamp(new Date(deleteObjectEvent.getTimestamp()));
        changelogPK.setType(deleteObjectEvent.getType());
        dao.save(changeLog);
    }
}
