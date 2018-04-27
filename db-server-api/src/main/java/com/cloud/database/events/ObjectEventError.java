package com.cloud.database.events;

import java.io.Serializable;

/**
 * Created by albo1013 on 03.12.2015.
 */
public class ObjectEventError extends ObjectEvent<Integer> {
    public static enum Operation{
        Create,Update,Delete
    }

    private Operation operation;

    protected Exception exception;

    public ObjectEventError(String type, Integer id, Operation operation) {
        super(type, id);
        this.operation = operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
