package com.easypan.entity.enums;


import lombok.Getter;

@Getter
public enum ResponseCodeEnum {
    CODE_200(200, "请求成功"),
    CODE_404(404, "请求地址不存在"),
    CODE_600(600, "请求参数错误"),
    CODE_601(601, "信息已经存在"),
    CODE_500(500, "服务器返回错误，请联系管理员"),
    CODE_901(901, "登录超时，请重新登录"),
    CODE_904(904, "网盘空间不足，请扩容"),
    CODE_902(902, "提取码错误" );

    private final Integer code;

    private final String msg;

    /**
     * 构造函数
     *
     * @param code 返回码
     * @param msg  返回信息
     */
    ResponseCodeEnum(Integer code, String msg) {
        // 将传入的返回码赋值给类的成员变量code
        this.code = code;
        // 将传入的返回信息赋值给类的成员变量msg
        this.msg = msg;
    }
}
