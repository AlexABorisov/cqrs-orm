package com.fsm;

import org.apache.log4j.Logger;
import pt.fsm.State;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by albo1013 on 16.11.2015.
 */

public class StateDecorator<STATES extends Enum<?>, EVENT> extends State<STATES, EVENT> {
    protected Logger logger = Logger.getLogger(getClass());
    protected Map<Class, EventHandler<?>> handlers = new HashMap<Class, EventHandler<?>>(){
        @Override
        public EventHandler<?> put(Class key, EventHandler<?> value) {
            EventHandler<?> result = super.put(key, value);
            if (result != null){
                logger.warn("Duplicate handler detected for state "+getId()+ " duplication "+key);
            }
            return result;    //To change body of overridden methods use File | Settings | File Templates.
        }
    };
    protected EventHandler<?> enterHandler = null;
    protected EventHandler<?> exitHandler = null;

    public StateDecorator(STATES id) {
        super(id);
    }

    public <T> StateDecorator setEnterHandler(EventHandler<T> enterHandler) {
        this.enterHandler = enterHandler;
        return this;
    }

    public <T> StateDecorator setExitHandler(EventHandler<T> exitHandler) {
        this.exitHandler = exitHandler;
        return this;
    }


    public <OTHER_EVENT> StateDecorator addHandler(Class<OTHER_EVENT> clazz, EventHandler<OTHER_EVENT> handler) {
        handlers.put(clazz, handler);
        return this;
    }

    public <OTHER_EVENT> StateDecorator addHandlerWithTransit(Class<OTHER_EVENT> clazz, final EventHandler<OTHER_EVENT> handler, final STATES nextState) {
        handlers.put(clazz, new EventHandler<OTHER_EVENT>() {
            @Override
            public int invoke(OTHER_EVENT evt) {
                handler.invoke(evt);
                next(nextState);
                return 0;
            }
        });
        return this;
    }

    public <OTHER_EVENT> StateDecorator addHandlerWithTransit(Class<OTHER_EVENT> clazz, final EventHandler<OTHER_EVENT> handler, final STATES ... nextStates) {
        handlers.put(clazz, new EventHandler<OTHER_EVENT>() {
            @Override
            public int invoke(OTHER_EVENT evt) {
                int invoke = handler.invoke(evt);
                next(nextStates[invoke]);
                return invoke;
            }
        });
        return this;
    }



    @Override
    public void enter() {
        logger.debug("Enter " + getId());
        if (enterHandler != null) {
            enterHandler.invoke(null);
        }
        super.enter();
    }

    @Override
    public void exit() {
        logger.debug("Exit " + getId());
        if (exitHandler != null) {
            exitHandler.invoke(null);
        }
        super.exit();
    }

    @Override
    public void handleEvent() {
        Object value = null;
        try {
            EVENT evt = e();
            if (logger.isTraceEnabled()){
                logger.trace("processing event "+ evt);
            }
            EventHandler eventHandler = handlers.get(evt.getClass());
            if (eventHandler != null) {
                eventHandler.invoke(evt);
            } else {
                logger.warn("No handler for event " + evt.getClass() + " wrapped event " + value + "ignoring for state "+getId());
            }
        } catch (Exception e) {
            logger.warn("Exception in state " + getId() + " event " + e().getClass() + " wrapped event " + value, e);
        }
    }


}