package com.cassandra.utils;

/**
 * Created by mich0913 on 08.12.2015.
 */
public class TestObjectClass {


    private int id;
    private int i = 2;
    private int j = 33;

    public TestObjectClass() {
        this(1);
    }

    public TestObjectClass(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    @Override
    public String toString() {
        return "A{" +
                "id=" + id +
                ", i=" + i +
                ", j=" + j +
                '}';
    }


}
