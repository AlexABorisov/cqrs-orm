package com.cloud.database.events;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by albo1013 on 20.11.2015.
 */
public class UpdateObjectEvent extends ObjectEvent<Integer> {
    protected Map<String,Object> properties = new HashMap<String, Object>();

    public UpdateObjectEvent(String type, Integer id) {
        super(type, id);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
