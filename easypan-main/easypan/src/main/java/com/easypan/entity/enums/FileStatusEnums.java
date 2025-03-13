package com.easypan.entity.enums;

import lombok.Getter;

@Getter
public enum FileStatusEnums {
    TRANSFER(0, "转码中"),
    TRANSFER_FAIL(1, "转码失败"),
    USING(2, "使用中");

    private final Integer status;
    private final String desc;

    FileStatusEnums(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

}
