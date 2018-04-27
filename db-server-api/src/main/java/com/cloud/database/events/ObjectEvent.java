package com.cloud.database.events;

import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

/**
 * Created by albo1013 on 20.11.2015.
 */
public class ObjectEvent<T extends Serializable> extends ApplicationEvent implements Serializable {
    protected String type;
    protected T id;

    public ObjectEvent(String type, T id) {
        super(ObjectEvent.class);
        this.type = type;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public T getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjectEvent that = (ObjectEvent) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
