package com.cloud.utils;


import javassist.util.proxy.ProxyFactory;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by albo1013 on 26.11.2015.
 */
public abstract class ClassUtils {

    public static final String ID = "id";

    private static Set<Class> primitives = new HashSet<Class>();

    static {
        primitives.add(int.class);
        primitives.add(long.class);
        primitives.add(double.class);
        primitives.add(Integer.class);
        primitives.add(Long.class);
        primitives.add(Double.class);
        primitives.add(Date.class);
        primitives.add(Enum.class);
        primitives.add(String.class);
        primitives.add(boolean.class);
        primitives.add(Boolean.class);
    }




    public interface ObjectFieldHandler<T> {
        Object onField(T instance, Field field);
    }

    public interface ClassFieldHandler<T> {
        void onField(Class<T> clazz, Field field);
    }

    public static <T> T traversePojo(T instance, ObjectFieldHandler handler) throws IllegalAccessException {
        Class<T> tClass = (Class<T>) instance.getClass();
        LinkedList<Class<T>> stack = new LinkedList<Class<T>>();
        T currentInstance = instance;
        stack.add(tClass);

        while (!stack.isEmpty()) {
            tClass = stack.remove();
            if (!isProxy(tClass)) {
                for (Field field : tClass.getDeclaredFields()) {
                    Object fieldValue = handler.onField(currentInstance, field);
                    if (fieldValue != null) {
                        field.setAccessible(true);
                        field.set(currentInstance, fieldValue);
                    }
                }
            }
            tClass = (Class<T>) tClass.getSuperclass();
            if (tClass != Object.class) {
                stack.add(tClass);
            }
        }
        return instance;
    }






    public static boolean isProxy(Class tClass) {
        return ProxyFactory.isProxyClass(tClass);
    }


    public static <T> void traverseClass(Class<T> tClass, ClassFieldHandler handler) {
        LinkedList<Class<T>> stack = new LinkedList<Class<T>>();
        Class<T> currentClass = tClass;
        stack.add(tClass);
        while (!stack.isEmpty()) {
            tClass = stack.remove();
            if (!isProxy(tClass)) {
                for (Field field : tClass.getDeclaredFields()) {
                    handler.onField(currentClass, field);
                }
            }
            tClass = (Class<T>) tClass.getSuperclass();
            if (tClass != Object.class) {
                stack.add(tClass);
            }
        }
    }

    public static boolean isPrimitive(Field field) {
        return primitives.contains(field.getType());
    }



}


