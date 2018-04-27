package com.cloud.database.cassandra;

import com.cassandra.utils.CassandraUtils;
import com.cassandra.utils.PersistenceCapable;
import com.cloud.database.changelog.ChangelogDao;
import com.cloud.database.events.ObjectEvent;
import com.cloud.generator.IDGenerator;
import com.cloud.utils.Pair;
import javassist.util.proxy.MethodHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.data.cassandra.core.CassandraOperations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by albo1013 on 16.12.2015.
 */
public class PersistenceCapableHandler implements MethodHandler {

    private Pair<Integer, Field> idField;
    private ObjectFSM privateObjectFSM;
    private Object originalObject;
    private CassandraOperations cassandraOperation;
    private IDGenerator idGenerator;
    private ApplicationContext applicationContext;

    public PersistenceCapableHandler(Object privateObject) {
        this.originalObject = privateObject;
        this.privateObjectFSM = new ObjectFSM(privateObject.getClass().getSimpleName());
        idField = CassandraUtils.getIdField(privateObject);
    }

    @Override
    public Object invoke(Object obj, Method method, Method method1, Object[] objects) throws Throwable {
        if (method.getDeclaringClass().equals(PersistenceCapable.class) && method.getName().equals("getRealObjectId")) {
            return idField.first;
        }
        if (method.getDeclaringClass().equals(PersistenceCapable.class) && method.getName().equals("getRealObjectType")) {
            return obj.getClass().getSuperclass().getSimpleName();
        }
        if (method.getDeclaringClass().equals(PersistenceCapable.class) && method.getName().equals("getPropertyMap")) {
            return CassandraUtils.traversePojoToMap(obj);
        }

        if (method.getDeclaringClass().equals(PersistenceCapable.class) && method.getName().equals("getOriginalObject")) {
            return originalObject;
        }

        if (method.getDeclaringClass().equals(PersistenceCapable.class) && method.getName().equals("persist")) {
            return persist(PersistenceCapable.Event.class.cast(objects[0]));
        }
        if (method.getDeclaringClass().equals(PersistenceCapable.class) && method.getName().equals("init")) {
            init(originalObject);
            return null;
        }


        if (method.getName().equals("equals")) {
            return idField.first.equals(idField.second.get(objects[0]));
        }

        if (method.getName().equals("hashCode")) {
            return idField.first.hashCode();
        }

        //Here is default method
        return objects == null ? method.invoke(obj) : method.invoke(obj, objects);

    }

    private void init(Object obj) {
        privateObjectFSM.setCassandraOperation(cassandraOperation);
        privateObjectFSM.setIdGenerator(idGenerator);
        privateObjectFSM.init(obj);
        privateObjectFSM.handleEvent(PersistenceCapable.Event.Init);
    }

    private int persist(PersistenceCapable.Event event) {
        privateObjectFSM.handleEvent(event);
        cassandraOperation.execute(privateObjectFSM.getBatch());
        List<ObjectEvent<Integer>> changeLog = privateObjectFSM.getChangeLog();

        for (ObjectEvent<Integer> item : changeLog){
            applicationContext.publishEvent(item);
        }

        return 0;
    }

    public PersistenceCapableHandler setCassandraOperation(CassandraOperations cassandraOperation) {
        this.cassandraOperation = cassandraOperation;
        return this;
    }

    public PersistenceCapableHandler setIdGenerator(IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
        return this;
    }

    public PersistenceCapableHandler setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }
}

