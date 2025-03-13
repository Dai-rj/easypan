package com.easypan.utils;

import com.easypan.entity.constants.Constants;
import com.easypan.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 字符串工具类
 */
public class StringTools {

    /**
     * 生成指定位数的随机数字字符串
     * @param count 随机字符串的长度
     * @return 生成的随机数字字符串
     */
    public static String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count, false, true);
    }

    /**
     * 生成指定位数的随机字符串，包含字母和数字
     * @param count 随机字符串的长度
     * @return 生成的随机字符串
     */
    public static String getRandomString(Integer count) {
        return RandomStringUtils.random(count, true, true);
    }

    /**
     * 检查参数对象中是否至少有一个非空字符串类型的字段
     * 用于多参数更新、删除操作，确保至少有一个非空条件
     * @param param 参数对象
     * @throws BusinessException 当参数校验失败时抛出
     */
    public static void checkParam(Object param) {
        try {
            Field[] fields = param.getClass().getDeclaredFields();
            boolean notEmpty = false;
            for (Field field : fields) {
                String methodName = "get" + StringTools.upperCaseFirstLetter(field.getName());
                Method method = param.getClass().getMethod(methodName);
                Object object = method.invoke(param);
                if (object instanceof String && !StringTools.isEmpty(object.toString())
                        || object != null && !(object instanceof java.lang.String)) {
                    notEmpty = true;
                    break;
                }
            }
            if (!notEmpty) {
                throw new BusinessException("多参数更新，删除，必须有非空条件");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("校验参数是否为空失败");
        }
    }

    /**
     * 将字段名的首字母大写
     * 用于构造get方法名
     * @param field 字段名
     * @return 首字母大写后的字段名
     */
    public static String upperCaseFirstLetter(String field) {
        if (isEmpty(field)) {
            return field;
        }
        //如果第二个字母是大写，第一个字母不大写
        if (field.length() > 1 && Character.isUpperCase(field.charAt(1))) {
            return field;
        }
        return field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    /**
     * 判断字符串是否为空
     * @param str 待判断的字符串
     * @return 如果字符串为空、"null"、"\u0000"或只包含空格，则返回true，否则返回false
     */
    public static boolean isEmpty(String str) {
        return str == null
                || str.isEmpty()
                || "null".equals(str)
                || "\u0000".equals(str)
                || str.trim().isEmpty();
    }

    /**
     * 使用MD5算法对原始字符串进行加密
     * @param originString 原始字符串
     * @return 加密后的字符串，如果原始字符串为空，则返回null
     */
    public static String encodeByMd5(String originString) {
        return isEmpty(originString) ? null : DigestUtils.md5Hex(originString);
    }

    /**
     * 检查路径是否安全，即路径中不包含向上跳转的目录
     * @param path 待检查的路径
     * @return 如果路径安全，则返回true，否则返回false
     */
    public static boolean pathIsOk(String path) {
        if (StringTools.isEmpty(path)) {
            return true;
        }
        return !path.contains("../") && !path.contains("..\\");
    }

    /**
     * 重命名文件名，在原文件名后加上随机字符串
     * @param fileName 原始文件名
     * @return 重命名后的文件名
     */
    public static String rename(String fileName) {
        String fileNameReal = getFileNameNoSuffix(fileName);
        String suffix = getFileSuffix(fileName);
        return fileNameReal + "_" + getRandomString(Constants.LENGTH_5) + suffix;
    }

    /**
     * 获取文件名不带后缀的部分
     * @param fileName 文件名
     * @return 不带后缀的文件名
     */
    public static String getFileNameNoSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return fileName;
        }
        fileName = fileName.substring(0, index);
        return fileName;
    }

    /**
     * 获取文件的后缀名
     * @param fileName 文件名
     * @return 文件的后缀名，如果文件没有后缀名，则返回空字符串
     */
    public static String getFileSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return fileName.substring(index);
    }
}
