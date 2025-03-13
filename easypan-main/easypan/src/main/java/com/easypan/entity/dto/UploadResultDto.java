/**
 * UploadResultDto类用于封装文件上传结果的相关信息
 * 它是一个数据传输对象（DTO），用于在不同层次或组件之间传递数据
 * 该类实现了Serializable接口，以便它可以被序列化和反序列化，通常用于网络通信或存储
 */
package com.easypan.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * UploadResultDto类，用于封装文件上传的结果信息
 * 
 * @author [Your Name]
 * @date [Current Date]
 */
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadResultDto implements Serializable {

    /**
     * 文件ID，用于唯一标识上传的文件
     */
    private String fileId;

    /**
     * 文件上传的状态，用于表示上传过程的成功与否
     */
    private String status;

}
