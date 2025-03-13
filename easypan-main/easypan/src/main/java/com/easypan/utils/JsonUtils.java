package com.easypan.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * JsonUtils类提供了JSON与Java对象之间的转换功能
 */
public class JsonUtils {
    // 日志对象，用于记录日志信息
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * 将Java对象转换为JSON字符串
     *
     * @param obj 要转换的Java对象
     * @return 转换后的JSON字符串
     */
    public static String convertObj2Json(Object obj) {
        return JSON.toJSONString(obj);
    }

    /**
     * 将JSON字符串转换为指定类型的Java对象
     *
     * @param json   JSON字符串
     * @param classz Java对象的类类型
     * @param <T>    泛型参数，表示要转换的Java对象的类型
     * @return 转换后的Java对象
     */
    public static <T> T convertJson2Obj(String json, Class<T> classz) {
        return JSONObject.parseObject(json, classz);
    }

    /**
     * 将JSON数组字符串转换为指定类型的Java对象列表
     *
     * @param json   JSON数组字符串
     * @param classz 列表中Java对象的类类型
     * @param <T>    泛型参数，表示列表中要转换的Java对象的类型
     * @return 转换后的Java对象列表
     */
    public static <T> List<T> convertJsonArray2List(String json, Class<T> classz) {
        return JSONArray.parseArray(json, classz);
    }

    /**
     * 主方法，用于测试JsonUtils类的功能
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
    }
}
