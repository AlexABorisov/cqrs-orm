package com.cloud.database.changelog.repo;

import com.cloud.database.changelog.ChangeLog;
import org.springframework.data.cassandra.repository.CassandraRepository;


/**
 * Created by albo1013 on 23.11.2015.
 */
public interface ChangelogRepository extends CassandraRepository<ChangeLog> {
}
