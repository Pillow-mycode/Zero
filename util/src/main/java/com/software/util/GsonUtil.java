package com.software.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gson工具类，用于统一管理Gson实例和提供常用的序列化/反序列化方法
 */
public class GsonUtil {
    private static volatile Gson instance;
    
    /**
     * 获取Gson单例实例
     * @return Gson实例
     */
    public static Gson getInstance() {
        if (instance == null) {
            synchronized (GsonUtil.class) {
                if (instance == null) {
                    instance = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd HH:mm:ss")
                            .serializeNulls()
                            .create();
                }
            }
        }
        return instance;
    }
    
    /**
     * 将对象序列化为JSON字符串
     * @param object 待序列化的对象
     * @return JSON字符串
     */
    public static String toJson(Object object) {
        return getInstance().toJson(object);
    }
    
    /**
     * 将JSON字符串反序列化为指定类型的对象
     * @param json JSON字符串
     * @param classOfT 目标类型
     * @param <T> 泛型参数
     * @return 反序列化后的对象
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return getInstance().fromJson(json, classOfT);
    }
}