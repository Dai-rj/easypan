package com.easypan.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * CopyTools提供对象到对象的复制功能，主要用于简化对象之间的属性复制过程
 */
public class CopyTools {

    /**
     * 复制一个对象列表到另一个对象列表
     * 该方法主要用于在两个不同类型的列表之间进行属性复制
     *
     * @param sList  源列表，包含需要被复制的对象
     * @param classz 目标对象的类，用于创建新的对象实例
     * @param <T>    目标对象的类型
     * @param <S>    源对象的类型
     * @return 返回一个新列表，包含复制后的对象
     */
    public static <T, S> List<T> copyList(List<S> sList, Class<T> classz) {
        List<T> list = new ArrayList<T>();
        for (S s : sList) {
            T t = null;
            try {
                t = classz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            BeanUtils.copyProperties(s, t);
            list.add(t);
        }
        return list;
    }

    /**
     * 复制一个对象到另一个对象
     * 该方法主要用于在两个不同类型的对象之间进行属性复制
     *
     * @param s      源对象，包含需要被复制的属性
     * @param classz 目标对象的类，用于创建新的对象实例
     * @param <T>    目标对象的类型
     * @param <S>    源对象的类型
     * @return 返回一个新的对象，包含复制后的属性
     */
    public static <T, S> T copy(S s, Class<T> classz) {
        T t = null;
        try {
            t = classz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BeanUtils.copyProperties(s, t);
        return t;
    }
}
