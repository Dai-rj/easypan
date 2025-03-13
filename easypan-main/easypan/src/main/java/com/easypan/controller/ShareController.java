package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.po.FileShare;
import com.easypan.entity.query.FileShareQuery;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.service.FileShareService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("shareController")
@RequestMapping("/share")
public class ShareController extends ABaseController {
    @Resource
    private FileShareService fileShareService;


    /**
     * 加载用户文件分享列表
     *
     * 此方法用于获取当前用户根据特定条件分享的文件列表它首先设置查询的排序方式，
     * 然后从会话中获取用户信息，并根据这些信息和查询条件来检索文件列表
     *
     * @param session 当前用户的会话，用于获取用户信息
     * @param query 文件分享查询对象，包含查询条件
     * @return 返回包含查询结果的响应对象
     */
    @RequestMapping("/loadShareList")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadShareList(HttpSession session, FileShareQuery query) {
        // 设置查询结果按分享时间降序排序
        query.setOrderBy("share_time desc");

        // 从会话中获取当前用户信息
        SessionWebUserDto userDto = getUserInfoFromSession(session);

        // 设置查询条件为当前用户的ID
        query.setUserId(userDto.getUserId());

        // 设置查询条件为需要查询文件名
        query.setQueryFileName(true);

        // 调用服务方法，分页查询文件分享列表
        PaginationResultVO resultVO = this.fileShareService.findListByPage(query);

        // 返回包含查询结果的成功响应
        return getSuccessResponseVO(resultVO);
    }

    /**
     * 处理文件分享请求
     *
     * 该方法用于让用户能够分享他们的文件它接收必要的参数来创建一个文件分享对象，
     * 并将其保存到数据库中
     *
     * @param session 用户会话，用于获取用户信息
     * @param fileId 文件ID，用于标识要分享的文件
     * @param validType 有效期类型，定义文件分享链接的有效期限
     * @param code 分享码，可选参数，用于增强分享的安全性
     * @return 返回一个包含分享信息的响应对象
     */
    @RequestMapping("/shareFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO shareFile(HttpSession session,
                                @VerifyParam(required = true) String fileId,
                                @VerifyParam(required = true) Integer validType,
                                String code) {
        // 从会话中获取用户信息
        SessionWebUserDto userDto = getUserInfoFromSession(session);

        // 创建一个文件分享对象
        FileShare share = new FileShare();
        // 设置文件ID
        share.setFileId(fileId);
        // 设置有效期类型
        share.setValidType(validType);
        // 设置分享码，可能为null
        share.setCode(code);
        // 设置用户ID，关联到分享该文件的用户
        share.setUserId(userDto.getUserId());

        // 保存文件分享信息到数据库
        fileShareService.saveShare(share);

        // 返回成功响应，包含分享信息
        return getSuccessResponseVO(share);
    }

    /**
     * 取消分享功能接口
     * 该方法用于接收取消分享的请求，根据提供的分享ID列表批量取消文件分享
     *
     * @param session HttpSession对象，用于获取当前用户的会话信息
     * @param shareIds 字符串类型的分享ID列表，多个ID之间以逗号分隔，是强制要求的参数
     * @return ResponseVO对象，包含取消分享操作的结果信息
     *
     * 方法通过解析shareIds参数来获取需要取消分享的文件ID列表，并从会话中获取当前用户信息，
     * 然后调用fileShareService的deleteFileShareBatch方法来批量取消这些文件的分享
     */
    @RequestMapping("/cancelShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO cancelShare(HttpSession session, @VerifyParam(required = true) String shareIds) {
        // 从会话中获取当前用户信息
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        // 调用服务方法，根据分享ID列表和用户ID批量取消文件分享
        fileShareService.deleteFileShareBatch(shareIds.split(","), userDto.getUserId());
        // 返回成功响应，表示取消分享操作完成
        return getSuccessResponseVO(null);
    }
}
