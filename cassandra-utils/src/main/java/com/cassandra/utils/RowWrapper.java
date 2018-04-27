package com.cassandra.utils;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.Token;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;

/**
* Created by albo1013 on 11.12.2015.
*/
public class RowWrapper {
    private Row delegate;
    private String name;
    private Integer i = null;

    public RowWrapper(Row delegate, String name) {
        this.delegate = delegate;
        this.name = name;
        i = delegate.getColumnDefinitions().getIndexOf(name);
    }

    public RowWrapper(Row delegate, Integer i) {
        this.delegate = delegate;
        this.i = i;
        name = delegate.getColumnDefinitions().getName(i);
    }

    public InetAddress getInet() {
        return delegate.getInet(i);
    }

    public UUID getUUID() {
        return delegate.getUUID(i);
    }

    public int getInt() {
        return delegate.getInt(i);
    }

    public double getDouble() {
        return delegate.getDouble(i);
    }

    public Date getDate() {
        return delegate.getDate(i);
    }

    public boolean isNull() {
        return delegate.isNull(i);
    }

    public ByteBuffer getBytes() {
        return delegate.getBytes(i);
    }

    public <T> Set<T> getSet(Class<T> elementsClass) {
        return delegate.getSet(i, elementsClass);
    }

    public String getString() {
        return delegate.getString(i);
    }

    public BigInteger getVarint() {
        return delegate.getVarint(i);
    }

    public boolean getBool() {
        return delegate.getBool(i);
    }

    public float getFloat() {
        return delegate.getFloat(i);
    }

    public BigDecimal getDecimal() {
        return delegate.getDecimal(i);
    }

    public long getLong() {
        return delegate.getLong(i);
    }

    public Token getToken() {
        return delegate.getToken(i);
    }

    public <K, V> Map<K, V> getMap(Class<K> keysClass, Class<V> valuesClass) {
        return delegate.getMap(i, keysClass, valuesClass);
    }

    public ByteBuffer getBytesUnsafe() {
        return delegate.getBytesUnsafe(i);
    }

    public <T> List<T> getList(Class<T> elementsClass) {
        return delegate.getList(i, elementsClass);
    }
}
