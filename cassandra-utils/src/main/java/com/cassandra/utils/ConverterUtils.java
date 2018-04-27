package com.cassandra.utils;

import com.cloud.utils.Converter;
import com.cloud.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
* Created by albo1013 on 11.12.2015.
*/
public class ConverterUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(ConverterUtils.class);

    private static Map<Pair<? extends Class,? extends Class>,Converter<?,?>> converterMap = new HashMap<>();
    static {
        converterMap.put(Pair.create(int.class, String.class), new Converter<Integer, String>() {
            @Override
            public String convert(Integer integer) {
                return integer.toString();
            }
        });

        converterMap.put(Pair.create(int.class, Integer.class), new Converter<Integer, Integer>() {
            @Override
            public Integer convert(Integer integer) {
                return integer;
            }
        });

        converterMap.put(Pair.create(Integer.class, int.class), new Converter<Integer, Integer>() {
            @Override
            public Integer convert(Integer integer) {
                return integer;
            }
        });

        converterMap.put(Pair.create(long.class, Long.class), new Converter<Long, Long>() {
            @Override
            public Long convert(Long value) {
                return value;
            }
        });

        converterMap.put(Pair.create(Long.class, Long.class), new Converter<Long, Long>() {
            @Override
            public Long convert(Long value) {
                return value;
            }
        });


        converterMap.put(Pair.create(Integer.class, String.class), new Converter<Integer, String>() {
            @Override
            public String convert(Integer integer) {
                return integer.toString();
            }
        });

        converterMap.put(Pair.create(String.class, Integer.class), new Converter<String,Integer>() {
            @Override
            public Integer convert(String string) {
                return Integer.valueOf(string);
            }
        });
        converterMap.put(Pair.create(String.class, int.class), new Converter<String,Integer>() {
            @Override
            public Integer convert(String string) {
                return Integer.valueOf(string);
            }
        });

        converterMap.put(Pair.create(RowWrapper.class, int.class), new Converter<RowWrapper,Integer>() {

            @Override
            public Integer convert(RowWrapper row) {
                return row.getInt();
            }
        });
        converterMap.put(Pair.create(RowWrapper.class, Integer.class), new Converter<RowWrapper,Integer>() {

            @Override
            public Integer convert(RowWrapper row) {
                return row.getInt();
            }
        });

        converterMap.put(Pair.create(RowWrapper.class, long.class), new Converter<RowWrapper,Long>() {

            @Override
            public Long convert(RowWrapper row) {
                return row.getLong();
            }
        });

        converterMap.put(Pair.create(RowWrapper.class, Long.class), new Converter<RowWrapper,Long>() {

            @Override
            public Long convert(RowWrapper row) {
                return row.getLong();
            }
        });

        converterMap.put(Pair.create(RowWrapper.class, String.class), new Converter<RowWrapper,String>() {

            @Override
            public String convert(RowWrapper row) {
                return row.getString();
            }
        });

        converterMap.put(Pair.create(RowWrapper.class, boolean.class), new Converter<RowWrapper,Boolean>() {

            @Override
            public Boolean convert(RowWrapper row) {
                return row.getBool();
            }
        });
        converterMap.put(Pair.create(RowWrapper.class, Boolean.class), new Converter<RowWrapper,Boolean>() {

            @Override
            public Boolean convert(RowWrapper row) {
                return row.getBool();
            }
        });

        converterMap.put(Pair.create(RowWrapper.class, double.class), new Converter<RowWrapper,Double>() {

            @Override
            public Double convert(RowWrapper row) {
                return row.getDouble();
            }
        });

        converterMap.put(Pair.create(RowWrapper.class, Double.class), new Converter<RowWrapper,Double>() {

            @Override
            public Double convert(RowWrapper row) {
                return row.getDouble();
            }
        });

        converterMap.put(Pair.create(RowWrapper.class, Date.class), new Converter<RowWrapper,Date>() {

            @Override
            public Date convert(RowWrapper row) {
                return row.getDate();
            }
        });

    }


    public static <FROM,TO> TO to (FROM from,Class<TO> toClass){
        if (from == null){
            return null;
        }
        if (from != null && from.getClass().equals(toClass)){
            return (TO) from;
        }
        Converter<FROM, TO> converter = (Converter<FROM, TO>) converterMap.get(Pair.create(from.getClass(), toClass));
        if (converter == null){
            LOGGER.warn("There are no converter for {} to {}",from.getClass(),toClass);
        }
        return converter == null ? null : converter.convert(from);
    }


}
