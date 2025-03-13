package com.easypan.entity.enums;

import lombok.Getter;

@Getter
public enum FileDelFlagEnums {
    DEL(0, "删除"),
    RECYCLE(1, "回收站"),
    USING(2, "使用中");
    private final Integer flag;
    private final String desc;

    FileDelFlagEnums(Integer flag, String desc) {
        this.flag = flag;
        this.desc = desc;
    }

}
