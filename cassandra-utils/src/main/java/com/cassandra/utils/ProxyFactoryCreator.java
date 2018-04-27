package com.cassandra.utils;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
* Created by albo1013 on 16.12.2015.
*/
public class ProxyFactoryCreator {
    private static Map<Class, Class> proxyMap = new HashMap<Class, Class>();

    public static <U extends PersistenceCapable> U createProxy(Object object, MethodHandler handler) {
        Object instance;
        try {
            Class currentClass = object.getClass();
            Class aClass = proxyMap.get(currentClass);
            if (aClass != null) {
                instance = aClass.newInstance();
            } else {
                ProxyFactory pf = new ProxyFactory();
                pf.setSuperclass(currentClass);
                pf.setInterfaces(new Class[]{PersistenceCapable.class});
                Class proxyClass = pf.createClass();
                proxyMap.put(currentClass, proxyClass);
                pf.setFilter(new MethodFilter() {
                    @Override
                    public boolean isHandled(Method m) {
                        return !m.getName().equals("toString");
                    }
                });
                instance = proxyClass.newInstance();
            }
            if (instance instanceof ProxyObject) {
                ((ProxyObject) instance).setHandler(handler);
            }
            if (instance instanceof PersistenceCapable) {
                ((PersistenceCapable) instance).init();
            }
            //BeanUtils.copyProperties(object, instance);
            return (U) instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
