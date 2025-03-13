package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.enums.FileDelFlagEnums;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVO;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.service.FileInfoService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("recycleController")
@RequestMapping("/recycle")
public class RecycleController extends ABaseController {

    @Resource
    private FileInfoService fileInfoService;

    /**
     * 根据条件分页查询回收站内的文件信息
     * 此方法用于从数据库中查询符合特定条件的、已被删除（即处于回收站中）的文件信息
     * 它根据用户的ID、排序方式以及删除标志来过滤结果，并以分页的形式返回查询到的数据
     *
     * @param session HttpSession对象，用于获取当前用户的会话信息
     * @param pageNo  页码，指示需要获取的页数
     * @param pageSize  每页大小，即每页包含的记录数
     * @return 返回一个包含查询结果的ResponseVO对象，其中包含了分页数据和文件信息
     */
    @RequestMapping("/loadRecycleList")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadRecycleList(HttpSession session, Integer pageNo, Integer pageSize) {
        // 创建FileInfoQuery对象，用于设置查询条件
        FileInfoQuery query = new FileInfoQuery();
        // 设置每页大小
        query.setPageSize(pageSize);
        // 设置页码
        query.setPageNo(pageNo);
        // 从会话中获取当前用户的ID
        query.setUserId(getUserInfoFromSession(session).getUserId());
        // 设置排序方式，按回收时间降序排列
        query.setOrderBy("recovery_time desc");
        // 设置删除标志，表示查询的文件应为已删除（回收站内）的文件
        query.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        // 调用服务层方法，根据设置的查询条件分页查询文件信息
        PaginationResultVO result = fileInfoService.findListByPage(query);
        // 将查询结果转换为PaginationVO，并封装到成功的ResponseVO中返回
        return getSuccessResponseVO(convert2PaginationVO(result, FileInfoVO.class));
    }

    /**
     * 恢复文件功能接口
     * 该方法用于批量恢复之前被回收的文件通过文件ID列表进行操作
     *
     * @param session HTTP会话对象，用于获取用户会话信息
     * @param fileIds 需要恢复的文件ID字符串，多个文件ID之间以特定分隔符连接
     * @return 返回操作结果的响应对象
     */
    @RequestMapping("/recoverFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO recoverFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        // 从会话中获取用户信息
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        // 调用服务层方法批量恢复文件
        fileInfoService.recoverFileBatch(webUserDto.getUserId(), fileIds);
        // 返回成功响应
        return getSuccessResponseVO(null);
    }

    /**
     * 删除文件接口
     * 该方法用于接收一个或多个文件ID，并执行批量删除操作
     *
     * @param session HttpSession对象，用于获取用户会话信息
     * @param fileIds 字符串类型的文件ID，多个ID以特定分隔符分隔，用于指定待删除的文件
     *                该参数标记为必须，意味着调用该接口时必须提供此参数
     * @return ResponseVO 返回一个响应对象，包含删除操作的结果信息
     */
    @RequestMapping("/delFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO delFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        // 从会话中获取用户信息
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        // 调用服务层方法执行批量删除文件操作
        fileInfoService.delFileBatch(webUserDto.getUserId(), fileIds,false);
        // 返回成功响应，表示文件删除成功
        return getSuccessResponseVO(null);
    }
}
