package com.cloud.database.events;

import java.io.Serializable;

/**
 * Created by albo1013 on 20.11.2015.
 */
public class DeleteObjectEvent extends ObjectEvent<Integer> {

    public DeleteObjectEvent(String type, Integer id) {
        super(type, id);
    }
}
