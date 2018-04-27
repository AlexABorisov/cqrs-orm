package com.cloud.web.database.rest;

import com.cassandra.utils.CassandraUtils;
import com.cloud.classcache.ClassCache;
import com.cloud.database.api.DataAccessSingle;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * Created by albo1013 on 20.11.2015.
 */
@Controller
@RequestMapping("single")
public class DataBaseController implements ApplicationContextAware{

    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("cassandraDataAccess")
    private DataAccessSingle<Object,Integer> dataAccessSingle;

    @RequestMapping(path = "get/{type}/{id}",method = RequestMethod.GET)
    public @ResponseBody Object getObjectById(@PathVariable String type,@PathVariable Integer id) throws InstantiationException, IllegalAccessException {
        return dataAccessSingle.getObjectById(id, ClassCache.getByShortName(type));
    }

    @RequestMapping(path = "get/{type}/{id}",method = RequestMethod.GET,headers ="If-Modified-Since" )
    public @ResponseBody  Object getObjectById(@PathVariable String type,@PathVariable Integer id,@RequestHeader(name = "If-Modified-Since") Date date) {
        DataAccessSingle<Object, Integer> bean = getDataAccessBean(type);
        return bean.getObjectById(id,null );
    }


    @RequestMapping(path = "create/{type}",method = RequestMethod.POST)
    public @ResponseBody Object create(@PathVariable String type,@RequestBody Object object) throws Exception{
        Object instance = null;
        instance = ClassCache.createByShortName(type);
        CassandraUtils.traverseMapToPojo(instance, (Map) object);
        return dataAccessSingle.create(instance);
    }


    @RequestMapping(path = "update/{type}/{id}",method = RequestMethod.POST)
    public @ResponseBody void update(@PathVariable String type,@PathVariable Integer id, @RequestBody Object object) {
        Object instance = null;
        instance = ClassCache.createByShortName(type);
        CassandraUtils.traverseMapToPojo(instance, (Map) object);
        dataAccessSingle.update(id,instance);
    }


    @RequestMapping(path = "delete/{type}/{id}",method = RequestMethod.DELETE)
    public @ResponseBody void delete(@PathVariable String type,@PathVariable Integer id) throws InstantiationException, IllegalAccessException {
        Class byShortName = ClassCache.getByShortName(type);
        dataAccessSingle.delete(id,byShortName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private DataAccessSingle<Object, Integer> getDataAccessBean(String type) {
        return applicationContext.getBean(type, DataAccessSingle.class);
    }
}
