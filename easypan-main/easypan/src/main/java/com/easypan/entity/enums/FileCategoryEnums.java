package com.easypan.entity.enums;

import lombok.Getter;

@Getter
public enum FileCategoryEnums {
    VIDEO(1, "video", "视频"),
    MUSIC(2, "music", "音频"),
    IMAGE(3, "image", "图片"),
    DOC(4, "doc", "文档"),
    OTHERS(5, "others", "其他");
    private final Integer category;
    private final String code;
    private final String desc;

    FileCategoryEnums(Integer category, String code, String desc) {
        this.category = category;
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据代码获取文件类别的枚举
     *
     * @param code 文件类别代码
     * @return 对应的文件类别枚举，如果找不到则返回null
     */
    public static FileCategoryEnums getByCode(String code) {
        // 遍历文件类别枚举的所有值
        for (FileCategoryEnums item : FileCategoryEnums.values()) {
            // 比较枚举项的代码与输入代码是否相等
            if (item.getCode().equals(code)) {
                // 如果相等，则返回该枚举项
                return item;
            }
        }
        // 如果没有找到匹配的枚举项，则返回null
        return null;
    }

}
