package com.cloud.web.database.rest;

import com.cloud.database.changelog.ChangelogDao;
import com.cloud.database.changelog.ChangeLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * Created by albo1013 on 24.11.2015.
 */
@Controller
@RequestMapping("changelog")
public class ChangelogController {
    @Autowired
    private ChangelogDao dao;

    @RequestMapping(path = "get/{type}/{id}",method = RequestMethod.GET)
    @ResponseBody public List<ChangeLog> getAllForType(@PathVariable String type,@PathVariable Integer id){
        return dao.getEventsForType(type,id);
    }

    @RequestMapping(path = "get/{type}/{id}/{date}",method = RequestMethod.GET)
    @ResponseBody public  List<ChangeLog> getAllForType(@PathVariable String type,@PathVariable Integer id, @PathVariable Long date){
        return dao.getEventsForType(type,id,new Date(date));
    }
}
