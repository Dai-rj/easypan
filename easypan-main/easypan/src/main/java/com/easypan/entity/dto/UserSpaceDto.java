package com.easypan.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户空间信息传输对象
 * 用于封装用户使用的空间和总空间信息
 */
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSpaceDto {
    /**
     * 用户已使用的空间大小，单位为字节
     */
    public Long useSpace;

    /**
     * 用户的总空间大小，单位为字节
     */
    public Long totalSpace;

}
