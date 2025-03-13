package com.easypan.entity.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 下载文件DTO类
 * 用于封装文件下载所需的信息
 */
@Setter
@Getter
public class DownloadFileDto {
    /**
     * 下载码
     * 用于验证下载请求的合法性
     */
    private String downloadCode;

    /**
     * 文件ID
     * 唯一标识一个文件
     */
    private String fileId;

    /**
     * 文件名
     * 文件的原始名称
     */
    private String fileName;

    /**
     * 文件路径
     * 文件在服务器上的存储路径
     */
    private String filePath;
}
