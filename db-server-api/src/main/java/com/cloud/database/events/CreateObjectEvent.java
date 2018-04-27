package com.cloud.database.events;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by albo1013 on 20.11.2015.
 */
public class CreateObjectEvent extends ObjectEvent<Integer> {
    private Map<String,Object> properties = new HashMap<String, Object>();

    public CreateObjectEvent(String type, Integer id, Map<String, Object> properties) {
        super(type, id);
        this.properties = properties;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
