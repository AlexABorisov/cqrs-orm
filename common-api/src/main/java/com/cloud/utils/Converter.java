package com.cloud.utils;

/**
* Created by albo1013 on 11.12.2015.
*/
public interface Converter<FROM,TO>{
    TO convert(FROM from);
}
