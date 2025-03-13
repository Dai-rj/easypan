package com.easypan.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期工具类，提供日期格式化和解析功能
 */
public class DateUtil {

    // 线程安全的锁对象
    private static final Object lockObj = new Object();

    // 存储不同日期格式的SimpleDateFormat实例的线程局部变量映射
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

    /**
     * 获取指定日期格式的SimpleDateFormat实例
     * 使用线程局部变量避免多线程环境下的格式化问题
     *
     * @param pattern 日期格式模式
     * @return SimpleDateFormat实例
     */
    private static SimpleDateFormat getSdf(final String pattern) {
        // 从映射中获取线程局部变量
        ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);
        if (tl == null) {
            // 同步块，确保线程安全
            synchronized (lockObj) {
                // 再次检查以确保在多线程环境下对象未被其他线程创建
                tl = sdfMap.get(pattern);
                if (tl == null) {
                    // 创建新的线程局部变量并存储到映射中
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }

        // 返回当前线程的SimpleDateFormat实例
        return tl.get();
    }

    /**
     * 格式化日期对象为指定格式的日期字符串
     *
     * @param date    日期对象
     * @param pattern 日期格式模式
     * @return 格式化后的日期字符串
     */
    public static String format(Date date, String pattern) {
        // 使用指定格式的SimpleDateFormat实例格式化日期
        return getSdf(pattern).format(date);
    }

    /**
     * 解析日期字符串为日期对象
     *
     * @param dateStr 日期字符串
     * @param pattern 日期格式模式
     * @return 解析后的日期对象，解析失败则返回当前日期
     */
    public static Date parse(String dateStr, String pattern) {
        try {
            // 使用指定格式的SimpleDateFormat实例解析日期字符串
            return getSdf(pattern).parse(dateStr);
        } catch (ParseException e) {
            // 解析异常时打印堆栈跟踪
            e.printStackTrace();
        }
        // 解析失败时返回当前日期
        return new Date();
    }
}
