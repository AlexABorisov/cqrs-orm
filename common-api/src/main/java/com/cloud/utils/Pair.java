package com.cloud.utils;

/**
 * Created by albo1013 on 02.12.2015.
 */
public class Pair <FIRST,SECOND> {
    public FIRST first;
    public SECOND second;

    private Pair(){}

    private Pair(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
    }

    public static <FIRST,SECOND> Pair<FIRST,SECOND> create(FIRST first1,SECOND second1){
        return new Pair<FIRST, SECOND>(first1,second1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        if (first != null ? !first.equals(pair.first) : pair.first != null) return false;
        if (second != null ? !second.equals(pair.second) : pair.second != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
