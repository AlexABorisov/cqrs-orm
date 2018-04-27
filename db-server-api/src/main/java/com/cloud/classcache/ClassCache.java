package com.cloud.classcache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by albo1013 on 27.11.2015.
 */
public class ClassCache {
    private final static Map<String,Class> classMap = new HashMap<String, Class>();
    static {
        /* Place where we have to put static classes to class cache.
        *  In regular way it shave to be done by class loader during specification classes
        */
        ClassCache.addClassToCache(com.cloud.database.changelog.ChangeLog.class);
    }
    public static Object createByShortName(String className)  {
        Class aClass = classMap.get(className.toLowerCase());
        try {
            return aClass == null ? null : aClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Class getByShortName(String className) {
        return classMap.get(className.toLowerCase());
    }

    public static void addClassToCache(Class clazz){
        classMap.put(clazz.getSimpleName().toLowerCase(),clazz);
    }

}
