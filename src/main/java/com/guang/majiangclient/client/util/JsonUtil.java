package com.guang.majiangclient.client.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @ClassName JsonUtil
 * @Description TODO
 * @Author guangmingdexin
 * @Date 2021/4/1 9:40
 * @Version 1.0
 **/
public final class JsonUtil {

    private JsonUtil() {
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String objToString(Object obj) {

        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            System.out.println("转化成 json 字符串失败！");
            e.printStackTrace();
        }
        return null;
    }

    public static Object stringToObj(String s, Class<?> clazz) {

        try {
            return mapper.readValue(s.getBytes(), clazz);
        } catch (IOException e) {
            System.out.println("json 字符串转换对象失败！");
            e.printStackTrace();
        }
        return null;
    }

    public static Object byteToObj(byte[] data, Class<?> clazz) {

        try {
            return mapper.readValue(data, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
