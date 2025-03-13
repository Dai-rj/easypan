package com.easypan.entity.query;


import lombok.Getter;
import lombok.Setter;

/**
 * 邮箱验证码参数
 */
@Setter
@Getter
public class EmailCodeQuery extends BaseParam {


    /**
     * 邮箱
     */
    private String email;

    private String emailFuzzy;

    /**
     * 编号
     */
    private String code;

    private String codeFuzzy;

    /**
     * 创建时间
     */
    private String createTime;

    private String createTimeStart;

    private String createTimeEnd;

    /**
     * 0:未使用 1:已使用
     */
    private Integer status;
}
