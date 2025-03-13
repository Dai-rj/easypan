package com.easypan.entity.po;

import com.easypan.entity.enums.DateTimePatternEnum;
import com.easypan.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 文件信息
 */
@Setter
@Getter
public class FileInfo implements Serializable {


    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 文件MD5值
     */
    private String fileMd5;

    /**
     * 父级ID
     */
    private String filePid;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 封面
     */
    private String fileCover;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateTime;

    /**
     * 0：文件1：目录
     */
    private Integer folderType;

    /**
     * 文件分类 1：视频 2：音频 3：图片 4：文档 5：其他
     */
    private Integer fileCategory;

    /**
     * 1:视频 2:音频 3:图片 4:pdf 5:doc 6:excel 7:txt 8:code 9:zip 10:其他
     */
    private Integer fileType;

    /**
     * 0:转码中 1:转码失败 2:转码成功
     */
    private Integer status;

    /**
     * 进入回收站时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recoveryTime;

    /**
     * 标记删除 0:删除 1:回收站 2:正常
     */
    private Integer delFlag;


    @Override
    public String toString() {
        return "文件ID:" + (fileId == null ? "空" : fileId) + "，用户ID:" + (userId == null ? "空" : userId) + "，文件MD5值:" + (fileMd5 == null ? "空" : fileMd5) + "，父级ID:" + (filePid == null ? "空" : filePid) + "，文件大小:" + (fileSize == null ? "空" : fileSize) + "，文件名:" + (fileName == null ? "空" : fileName) + "，封面:" + (fileCover == null ? "空" : fileCover) + "，文件路径:" + (filePath == null ? "空" : filePath) + "，创建时间:" + (createTime == null ? "空" : DateUtil.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + "，最后更新时间:" + (lastUpdateTime == null ? "空" : DateUtil.format(lastUpdateTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + "，0：文件1：目录:" + (folderType == null ? "空" : folderType) + "，文件分类 1：视频 2：音频 3：图片 4：文档 5：其他:" + (fileCategory == null ? "空" : fileCategory) + "，1:视频 2:音频 3:图片 4:pdf 5:doc 6:excel 7:txt 8:code 9:zip 10:其他:" + (fileType == null ? "空" : fileType) + "，0:转码中 1:转码失败 2:转码成功:" + (status == null ? "空" : status) + "，进入回收站时间:" + (recoveryTime == null ? "空" : DateUtil.format(recoveryTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + "，标记删除 0:删除 1:回收站 2:正常:" + (delFlag == null ? "空" : delFlag);
    }
}
