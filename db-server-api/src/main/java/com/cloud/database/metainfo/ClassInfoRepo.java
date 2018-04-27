package com.cloud.database.metainfo;

import org.springframework.data.cassandra.repository.CassandraRepository;

/**
 * Created by albo1013 on 11.12.2015.
 */
public interface ClassInfoRepo extends CassandraRepository<ClassInfo> {
}
