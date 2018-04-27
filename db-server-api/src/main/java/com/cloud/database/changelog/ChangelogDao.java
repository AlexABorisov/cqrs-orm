package com.cloud.database.changelog;

import com.cloud.database.changelog.repo.ChangelogRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by albo1013 on 24.11.2015.
 */
public interface ChangelogDao extends ChangelogRepository {
    List<ChangeLog> getEventsForType(String type,Integer objectID,Date timestamp);
    List<ChangeLog> getEventsForType(String type,Integer objectID);
}
