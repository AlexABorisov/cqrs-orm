package com.cloud.database.generator;

import com.cassandra.utils.CassandraUtils;
import com.cassandra.utils.ClassToTableTraverser;
import com.cloud.utils.ClassUtils;
import com.cloud.classcache.ClassCache;
import com.cloud.classcache.ClassCacheServerMBean;
import com.cloud.database.metainfo.ClassInfo;
import com.cloud.database.metainfo.ClassInfoRepo;
import com.datastax.driver.core.schemabuilder.Create;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.repository.support.BasicMapId;
import org.springframework.stereotype.Component;


/**
 * Created by albo1013 on 01.12.2015.
 */
@Component
public class DataBaseSchemaGenerator implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(DataBaseSchemaGenerator.class);
    @Autowired
    private ClassCacheServerMBean mBean;

    @Autowired
    private CassandraOperations cassandraOperations;

    @Autowired
    private ClassInfoRepo classInfoRepo;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        for (String clazz : mBean.getAllClasses()){
            Class<?> tClass = null;
            try {
                tClass = Class.forName(clazz);
                if (!CassandraUtils.isCassandraObject(tClass))
                    continue;

                ClassInfo classInfo = classInfoRepo.findOne(BasicMapId.id().with("className", tClass.getName()));

                if (classInfo != null){
                    createDelta(classInfo,new ClassInfo(tClass));
                    ClassCache.addClassToCache(tClass);
                    continue;
                }


                Create create = SchemaBuilder.createTable(tClass.getSimpleName());
                ClassUtils.traverseClass(tClass, new ClassToTableTraverser(create));
                cassandraOperations.execute(create);
                classInfoRepo.save(new ClassInfo(tClass));
                ClassCache.addClassToCache(tClass);
            } catch (Exception e) {
                logger.warn("Unable to find class {}",tClass,e);
            }

        }

    }

    private void createDelta(ClassInfo original, ClassInfo newObject) {
        if (original.equals(newObject)) return;
        logger.warn("Update is not supported for {}  and {}",original,newObject);
        //TODO place code to alter table;
    }
}
