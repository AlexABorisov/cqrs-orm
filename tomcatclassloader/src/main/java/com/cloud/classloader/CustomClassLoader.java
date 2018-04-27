package com.cloud.classloader;

import com.cloud.classcache.ByteLoader;
import com.cloud.classcache.ClassCacheServer;
import org.apache.catalina.LifecycleException;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by albo1013 on 26.11.2015.
 */
public class CustomClassLoader extends org.apache.catalina.loader.WebappClassLoader {

    private String virtualClasspath = "c:\\classcache";
    private ClassCacheServer mbeanServer;

    private ObjectName name;

    {
        try {
            mbeanServer = new ClassCacheServer();
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            name = new ObjectName("com.cloud.classloader.ClassCacheMbeanServer:type=ClassCache");
            server.registerMBean(mbeanServer, name);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public CustomClassLoader() {
    }

    public CustomClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void setVirtualClasspath(String virtualClasspath) {
        this.virtualClasspath = virtualClasspath;
    }

    @Override
    public void start() throws LifecycleException {
        super.start();
        if (virtualClasspath != null) {
            StringTokenizer tkn = new StringTokenizer(this.virtualClasspath, ";");
            File file = new File(tkn.nextToken());
            if (file.exists()){
                traverseFile(file);
            }
        }
    }

    private void traverseFile(File file) {
        LinkedList<File> stack = new LinkedList<File>();
        stack.add(file);
        File currentFile;
        while (!stack.isEmpty()) {
            currentFile = stack.remove();
            if (currentFile.isDirectory()) {
                if (currentFile.listFiles() != null){
                    stack.addAll(Arrays.asList(currentFile.listFiles()));
                }
                continue;
            }
            if (currentFile.isFile() && currentFile.getName().endsWith(".jar")){
                try {
                    JarFile jarFile = new JarFile(currentFile);
                    Enumeration<JarEntry> entries = jarFile.entries();
                    for (JarEntry item = entries.nextElement() ; entries.hasMoreElements(); item = entries.nextElement() ){
                        boolean isClass = item.getName().endsWith("class");
                        if (!item.isDirectory() && isClass)
                            mbeanServer.addClassToCache(isClass ? item.getName().replace('/', '.').replace(".class","") : item.getName(), currentFile.toURI().toString());
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
                continue;
            }
            String fullPath = currentFile.toURI().toString();
            if (fullPath.endsWith("class")) {
                String substring = fullPath.substring(file.toURI().toString().length());
                mbeanServer.addClassToCache(currentFile.getName().endsWith("class") ?
                                substring.replace('/', '.').replace(".class", "") :
                                substring,
                        currentFile.toURI().toString());
            }
        }
    }

    @Override
    public void stop() throws LifecycleException {
        super.stop();
        mbeanServer.clearAll();
        if (name != null) {
            try {
                MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                server.unregisterMBean(name);
            }catch (Exception e){
                throw new LifecycleException(e);
            }

        }
    }


    @Override
    public Class findClass(String name) throws ClassNotFoundException {
        ByteLoader classFromCache = mbeanServer.getClassFromCache(name);
        if (classFromCache == null ) {
            return super.findClass(name);
        }else
        {
            System.out.println("Loading from class cache " + name);
            byte[] bytes = classFromCache.loadData();
            return defineClass(name,bytes,0, bytes.length);
        }
    }


    @Override
    public void setJarPath(String jarPath) {
        super.setJarPath(jarPath);
    }

}
