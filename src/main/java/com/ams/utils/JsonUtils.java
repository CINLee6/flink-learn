package com.ams.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Copyright (C), 2019-2022, 中冶赛迪重庆信息技术有限公司
 * Json转换工具类
 *
 * @author xin.d.li
 * @date 2021-10-09 17:12
 */
public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * ObjectMapper 是线程安全的
     */
    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        //存在未知字段是否反序列化失败
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //允许存在注释
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    private JsonUtils() {
    }

    /**
     * collectionToString
     *
     * @param collection 集合
     * @return json
     */
    public static String collectionToString(Collection<?> collection) {
        if (collection == null) {
            return null;
        }
        if (collection.isEmpty()) {
            return "[]";
        }
        try {
            return MAPPER.writeValueAsString(collection);
        } catch (JsonProcessingException e) {
            logger.error("JSON Convert Error,{}", e.getMessage());
            throw new IllegalArgumentException("json argument is error");
        }
    }

    /**
     * mapToString
     *
     * @param map map
     * @return json
     */
    public static String mapToString(Map<?, ?> map) {
        if (map == null) {
            return null;
        }
        if (map.isEmpty()) {
            return "{} ";
        }
        try {
            return MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            logger.error("JSON Convert Error,{}", e.getMessage());
            throw new IllegalArgumentException("json argument is error");
        }
    }

    /**
     * stringToList
     *
     * @param json 数据库
     * @return 实体类
     */
    public static <T> List<T> stringToList(String json, Class<? extends T> entryClass) {
        if (json == null) {
            return null;
        }
        if (json.trim().isEmpty()) {
            return new ArrayList<>(0);
        }
        try {
            JavaType type = MAPPER.getTypeFactory().constructCollectionType(List.class, entryClass);
            return MAPPER.readValue(json, type);
        } catch (IOException e) {
            logger.error("JSON Convert Error,{}", e.getMessage());
            throw new IllegalArgumentException("json argument is error");
        }
    }

    /**
     * stringToMap
     *
     * @param json       数据库json串
     * @param keyClass   map键类型
     * @param valueClass map值类型
     * @return 返回一个HashMap
     */
    public static <K, V> Map<K, V> stringToMap(String json, Class<? extends K> keyClass, Class<? extends V> valueClass) {
        if (json == null) {
            return new HashMap<>();
        }
        if (json.trim().isEmpty()) {
            return new HashMap<>(0);
        }
        try {
            JavaType type = MAPPER.getTypeFactory().constructParametricType(HashMap.class, keyClass, valueClass);
            return MAPPER.readValue(json, type);
        } catch (IOException e) {
            logger.error("JSON Convert Error,{}", e.getMessage());
            throw new IllegalArgumentException("json argument is error");
        }
    }

    /**
     * objectToString
     *
     * @return json
     */
    public static <T> String objectToString(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("JSON Convert Error,{}", e.getMessage());
            throw new IllegalArgumentException("json argument is error");
        }
    }

    /**
     * stringToObject
     *
     * @return json
     */
    public static <T> T stringToObject(String json, Class<T> clazz) {
        if (isEmpty(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            logger.error("JSON Convert Error,{}", e.getMessage());
            throw new IllegalArgumentException("json argument is error");
        }
    }

    /**
     * stringToObjectWithTypeReference
     *
     * @return json
     */
    public static <T> T stringToObjectWithTypeReference(String json, TypeReference<T> typeReference) {
        if (isEmpty(json)) {
            return null;
        }
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            logger.error("JSON Convert Error,{}", e.getMessage());
            throw new IllegalArgumentException("json argument is error");
        }
    }

    private static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }
}

