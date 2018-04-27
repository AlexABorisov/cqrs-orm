package com.cloud.classcache;


import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
* Created by albo1013 on 01.12.2015.
*/
public class ClassCacheServer implements ClassCacheServerMBean {
    final private Map<String,ByteLoader> classCache = new HashMap<String, ByteLoader>();

    @Override
    public void addClassToCache(final String className, final String uri) {
        if (uri.endsWith("jar")){
            classCache.put(className, new ByteLoader() {
                @Override
                public byte[] loadData() {
                    JarFile jarFile = null;
                    InputStream inputStream = null;
                    try {
                        jarFile = new JarFile(new File(new URI(uri)));
                        inputStream = jarFile.getInputStream(new JarEntry(className.replace('.', '/').concat(".class")));
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        int nRead;
                        byte[] data = new byte[1024];
                        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                            buffer.write(data, 0, nRead);
                        }
                        return buffer.toByteArray();
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }finally {
                        if (jarFile != null)
                            try {
                                jarFile.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        if (inputStream != null)
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                }


                @Override
                public String toString() {
                    return "location "+ uri;
                }
            });
        }else{
            classCache.put(className, new ByteLoader() {
                @Override
                public byte[] loadData() {
                    InputStream inputStream= null;
                    try {
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        int nRead;
                        byte[] data = new byte[1024];
                        inputStream = new FileInputStream(new File(new URI(uri)));
                        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                            buffer.write(data, 0, nRead);
                        }
                        return buffer.toByteArray();
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                    finally {
                        if (inputStream != null)
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                }
                @Override
                public String toString() {
                    return "location "+ uri;
                }
            });
        }

    }

    @Override
    public ByteLoader getClassFromCache(String clazz) {
        return classCache.get(clazz);
    }

    @Override
    public String allClasses() {
        return classCache.keySet().toString();
    }

    @Override
    public String allUrls() {
        return classCache.values().toString();
    }

    @Override
    public Set<String> getAllClasses() {
        return classCache.keySet();
    }

    @Override
    public void clearAll() {
        classCache.clear();
    }


}
