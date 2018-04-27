package com.cloud.database.metainfo;

import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by albo1013 on 11.12.2015.
 */

@Table("ClassInfo")
public class ClassInfo {
    @PrimaryKey
    private String className;

    private Map<String,String> fields = new HashMap<>();

    private String parentClass;

    public ClassInfo() {
    }

    public ClassInfo(Class<?> tClass) {
        className = tClass.getName();
        parentClass = tClass.getSuperclass().getName();

        for (Field field : tClass.getDeclaredFields()){
            fields.put(field.getName(),field.getType().getName());
        }
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public String getParentClass() {
        return parentClass;
    }

    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassInfo classInfo = (ClassInfo) o;

        if (className != null ? !className.equals(classInfo.className) : classInfo.className != null) return false;
        if (fields != null ? !fields.equals(classInfo.fields) : classInfo.fields != null) return false;
        if (parentClass != null ? !parentClass.equals(classInfo.parentClass) : classInfo.parentClass != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = className != null ? className.hashCode() : 0;
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        result = 31 * result + (parentClass != null ? parentClass.hashCode() : 0);
        return result;
    }
}
