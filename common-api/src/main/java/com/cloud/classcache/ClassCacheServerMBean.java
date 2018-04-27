package com.cloud.classcache;

import java.util.List;
import java.util.Set;

/**
 * Created by albo1013 on 01.12.2015.
 */
public interface ClassCacheServerMBean {
    void addClassToCache(String className, String uri);
    ByteLoader getClassFromCache(String clazz);
    public String allClasses();
    public String allUrls();
    public Set<String> getAllClasses();
    void clearAll();

}
