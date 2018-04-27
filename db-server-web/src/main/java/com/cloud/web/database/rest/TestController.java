package com.cloud.web.database.rest;

import com.cloud.database.changelog.ChangelogDao;
import com.cloud.database.changelog.ChangeLog;
import com.cloud.database.changelog.ChangelogPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by albo1013 on 24.11.2015.
 */
@Controller
@RequestMapping("test")
public class TestController {
    @Autowired
    private ChangelogDao dao;

    @RequestMapping(path = "init/{type}/{operation}/{start}/{stop}",method = RequestMethod.GET)
    @ResponseBody public String init( @PathVariable String type, @PathVariable Integer start,@PathVariable Integer stop,@PathVariable String operation){
        List<ChangeLog> toSave = new LinkedList<ChangeLog>();
        for (int i = start; i < stop ; i++){
            ChangeLog item = new ChangeLog();
            item.setId(new ChangelogPK(new Date(),i,type));
            item.setCommand(operation);
            item.getProperties().put(String.valueOf(i % ((stop-start)/2)),"value="+i);
            toSave.add(item);
        }

        dao.save(toSave);
        return "Done";
    }
}
