package com.easypan.task;

import com.easypan.entity.enums.FileDelFlagEnums;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.service.FileInfoService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件清理任务类，负责定期清理回收站中的文件
 */
@Component
public class FileCleanTask {

    /**
     * 注入的文件信息服务，用于执行文件相关的操作
     */
    @Resource
    private FileInfoService fileInfoService;

    /**
     * 定时任务方法，用于定期清理回收站中的文件
     * 此方法固定延迟执行，每次执行完毕后等待指定时间再次执行
     */
    @Scheduled(fixedDelay = 1000 * 60 * 3)
    public void execute() {
        // 创建文件信息查询对象，设置查询参数
        FileInfoQuery fileInfoQuery = new FileInfoQuery();
        fileInfoQuery.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        fileInfoQuery.setQueryExpire(true);

        // 查询符合参数的文件信息列表
        List<FileInfo> fileInfoList = fileInfoService.findListByParam(fileInfoQuery);

        // 将文件信息列表按用户ID分组，以便后续批量删除
        Map<String, List<FileInfo>> fileInfoMap = fileInfoList.stream().collect(Collectors.groupingBy(FileInfo::getUserId));

        // 遍历分组后的文件信息，按用户ID批量删除文件
        for (Map.Entry<String, List<FileInfo>> entry : fileInfoMap.entrySet()) {
            List<String> fileIds = entry.getValue().stream().map(p -> p.getFileId()).collect(Collectors.toList());
            fileInfoService.delFileBatch(entry.getKey(), String.join(",", fileIds), false);
        }
    }
}
