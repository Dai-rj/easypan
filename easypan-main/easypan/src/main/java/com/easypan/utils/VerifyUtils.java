/**
 * com.easypan.utils包下的验证工具类
 * 提供正则表达式验证功能
 */
package com.easypan.utils;

import com.easypan.entity.enums.VerifyRegexEnum;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证工具类
 * 提供静态方法来验证字符串是否符合给定的正则表达式
 */
public class VerifyUtils {

    /**
     * 使用给定的正则表达式验证字符串
     *
     * @param regex 正则表达式字符串
     * @param value 待验证的字符串
     * @return 如果字符串符合正则表达式则返回true，否则返回false
     */
    public static boolean verify(String regex, String value) {
        // 检查输入字符串是否为空，为空则直接返回false
        if (StringTools.isEmpty(value)) {
            return false;
        }
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 使用编译的正则表达式匹配输入字符串
        Matcher matcher = pattern.matcher(value);
        // 返回匹配结果
        return matcher.matches();
    }

    /**
     * 使用预定义的正则表达式枚举验证字符串
     *
     * @param regex 预定义的正则表达式枚举
     * @param value 待验证的字符串
     * @return 如果字符串符合预定义的正则表达式则返回true，否则返回false
     */
    public static boolean verify(VerifyRegexEnum regex, String value) {
        // 调用重载方法，使用枚举中的正则表达式字符串验证输入字符串
        return verify(regex.getRegex(), value);
    }
}
