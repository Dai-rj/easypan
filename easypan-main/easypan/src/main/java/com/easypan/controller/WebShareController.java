package com.easypan.controller;

import com.easypan.annotation.GlobalInterceptor;
import com.easypan.annotation.VerifyParam;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SessionShareDto;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.enums.FileDelFlagEnums;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.entity.po.FileInfo;
import com.easypan.entity.po.FileShare;
import com.easypan.entity.po.UserInfo;
import com.easypan.entity.query.FileInfoQuery;
import com.easypan.entity.vo.FileInfoVO;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.entity.vo.ShareInfoVO;
import com.easypan.exception.BusinessException;
import com.easypan.service.FileInfoService;
import com.easypan.service.FileShareService;
import com.easypan.service.UserInfoService;
import com.easypan.utils.CopyTools;
import com.easypan.utils.StringTools;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

@RestController("webShareController")
@RequestMapping("/showShare")
public class WebShareController extends CommonFileController {

    @Resource
    private FileShareService fileShareService;

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private UserInfoService userInfoService;


    /**
     * 获取分享登录信息
     *
     * 此方法用于获取用户分享文件的登录信息它首先从会话中获取分享信息，
     * 如果分享信息存在，则进一步获取分享的详细信息，并判断当前用户是否是分享者
     *
     * @param session 当前用户的会话，用于获取会话中的用户和分享信息
     * @param shareId 分享的唯一标识符，用于获取特定分享的信息
     * @return 返回一个包含分享信息的响应对象如果分享信息不存在，则返回空响应对象
     */
    @RequestMapping("/getShareLoginInfo")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO getShareLoginInfo(HttpSession session, @VerifyParam(required = true) String shareId) {
        // 从会话中获取分享信息
        SessionShareDto shareSessionDto = getSessionShareFromSession(session, shareId);
        if (shareSessionDto == null) {
            // 如果分享信息不存在，返回空响应对象
            return getSuccessResponseVO(null);
        }
        // 获取分享的详细信息
        ShareInfoVO shareInfoVO = getShareInfoCommon(shareId);
        // 判断是否是当前用户分享的文件
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        if (userDto != null && userDto.getUserId().equals(shareSessionDto.getShareUserId())) {
            // 如果是当前用户分享的文件，设置标志为true
            shareInfoVO.setCurrentUser(true);
        } else {
            // 如果不是当前用户分享的文件，设置标志为false
            shareInfoVO.setCurrentUser(false);
        }
        // 返回包含分享信息的响应对象
        return getSuccessResponseVO(shareInfoVO);
    }

    /**
     * 获取分享信息
     *
     * 此方法用于处理获取分享信息的请求它接受一个分享ID作为参数，并返回相应的分享信息
     * 使用了@RequestMapping注解来映射HTTP请求到此方法，以及@GlobalInterceptor注解来进行全局拦截器配置
     *
     * @param shareId 分享的唯一标识符，用于查询特定的分享信息
     * @return 返回一个ResponseVO对象，其中包含分享信息
     */
    @RequestMapping("/getShareInfo")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO getShareInfo(@VerifyParam(required = true) String shareId) {
        return getSuccessResponseVO(getShareInfoCommon(shareId));
    }

    /**
     * 获取文件分享的通用信息
     * 此方法主要用于获取并验证文件分享的信息，包括检查分享的有效性、文件信息和用户信息
     *
     * @param shareId 分享的唯一标识符，用于获取分享信息
     * @return 返回一个ShareInfoVO对象，其中包含了分享的详细信息
     * @throws BusinessException 如果分享不存在或已过期，或者文件已被删除，则抛出此异常
     */
    private ShareInfoVO getShareInfoCommon(String shareId) {
        // 根据分享ID获取文件分享对象
        FileShare share = fileShareService.getFileShareByShareId(shareId);
        // 检查分享对象是否存在以及是否已过期
        if (null == share || (share.getExpireTime() != null && new Date().after(share.getExpireTime()))) {
            // 如果分享不存在或已过期，则抛出异常
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        // 将文件分享对象转换为分享信息视图对象
        ShareInfoVO shareInfoVO = CopyTools.copy(share, ShareInfoVO.class);
        // 根据文件ID和用户ID获取文件信息
        FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(share.getFileId(), share.getUserId());
        // 检查文件信息是否存在以及文件是否已被删除
        if (fileInfo == null || !FileDelFlagEnums.USING.getFlag().equals(fileInfo.getDelFlag())) {
            // 如果文件信息不存在或文件已被删除，则抛出异常
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        // 设置分享信息中的文件名
        shareInfoVO.setFileName(fileInfo.getFileName());
        // 根据用户ID获取用户信息
        UserInfo userInfo = userInfoService.getUserInfoByUserId(share.getUserId());
        // 设置分享信息中的昵称、头像和用户ID
        shareInfoVO.setNickName(userInfo.getNickName());
        shareInfoVO.setAvatar(userInfo.getQqAvatar());
        shareInfoVO.setUserId(userInfo.getUserId());
        // 返回分享信息视图对象
        return shareInfoVO;
    }

    /**
     * 校验分享码
     *
     * 此方法用于验证用户提供的分享码是否有效分享操作涉及到文件或资源的共享，
     * 因此需要确保分享码的合法性和安全性该方法不要求用户登录即可访问，但会对请求参数进行验证
     *
     * @param session HttpSession对象，用于存储分享信息
     * @param shareId 要验证的分享ID，是验证分享码的必要参数
     * @param code 用户提供的分享码，用于验证分享的合法性和有效性
     * @return 返回一个ResponseVO对象，包含处理结果信息
     */
    @RequestMapping("/checkShareCode")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO checkShareCode(HttpSession session,
                                     @VerifyParam(required = true) String shareId,
                                     @VerifyParam(required = true) String code) {
        // 调用fileShareService的checkShareCode方法验证分享码，并获取分享信息
        SessionShareDto shareSessionDto = fileShareService.checkShareCode(shareId, code);
        // 将验证后的分享信息存储在session中，以便后续访问共享资源时使用
        session.setAttribute(Constants.SESSION_SHARE_KEY + shareId, shareSessionDto);
        // 返回一个成功的响应对象，表示分享码验证成功
        return getSuccessResponseVO(null);
    }

    /**
     * 获取文件列表
     *
     * 该方法用于加载文件列表，可以根据分享ID和文件父ID来获取相应的文件信息列表
     *
     * @param session HttpSession对象，用于获取当前会话信息
     * @param shareId 分享ID，用于确定特定的文件分享
     * @return 返回一个ResponseVO对象，其中包含文件列表信息
     */
    @RequestMapping("/loadFileList")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO loadFileList(HttpSession session,
                                   @VerifyParam(required = true) String shareId, String filePid) {
        // 检查分享信息，确保分享有效且存在
        SessionShareDto shareSessionDto = checkShare(session, shareId);

        // 创建文件信息查询对象
        FileInfoQuery query = new FileInfoQuery();

        // 如果文件父ID不为空且不为"0"，则进行进一步检查并设置查询条件
        if (!StringTools.isEmpty(filePid) && !Constants.ZERO_STR.equals(filePid)) {
            // 检查文件父ID是否有效
            fileInfoService.checkRootFilePid(shareSessionDto.getFileId(), shareSessionDto.getShareUserId(), filePid);
            // 设置查询的文件父ID
            query.setFilePid(filePid);
        } else {
            // 如果文件父ID为空或为"0"，则查询根文件信息
            query.setFileId(shareSessionDto.getFileId());
        }

        // 设置查询的用户ID、排序方式、删除标志
        query.setUserId(shareSessionDto.getShareUserId());
        query.setOrderBy("last_update_time desc");
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());

        // 调用服务方法，分页查询文件信息列表
        PaginationResultVO resultVO = fileInfoService.findListByPage(query);

        // 返回成功响应，包含转换后的文件信息列表
        return getSuccessResponseVO(convert2PaginationVO(resultVO, FileInfoVO.class));
    }


    /**
     * 校验分享是否失效
     *
     * @param session 用户会话，用于获取会话中的分享信息
     * @param shareId 分享ID，用于标识特定的分享
     * @return 返回SessionShareDto对象，包含分享的详细信息
     * @throws BusinessException 如果分享不存在或已过期，则抛出业务异常
     */
    private SessionShareDto checkShare(HttpSession session, String shareId) {
        // 从会话中获取分享信息
        SessionShareDto shareSessionDto = getSessionShareFromSession(session, shareId);
        // 如果分享信息为空，则抛出异常，表示分享不存在
        if (shareSessionDto == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_903);
        }
        // 检查分享是否已过期，如果当前时间在分享的过期时间之后，则抛出异常
        if (shareSessionDto.getExpireTime() != null && new Date().after(shareSessionDto.getExpireTime())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }
        // 分享有效，返回分享信息
        return shareSessionDto;
    }


    /**
     * 获取目录信息
     *
     * 此方法用于获取指定共享目录的详细信息它需要会话信息、共享ID和路径作为输入参数
     * 通过验证参数的有效性，并从会话中提取共享信息，最终调用上级类的方法来获取目录信息
     *
     * @param session 当前的HTTP会话，用于管理用户会话信息
     * @param shareId 共享的唯一标识符，用于确定特定的共享资源
     * @param path 目录路径，指定需要获取信息的目录位置
     * @return 返回一个包含目录信息的ResponseVO对象
     */
    @RequestMapping("/getFolderInfo")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO getFolderInfo(HttpSession session,
                                    @VerifyParam(required = true) String shareId,
                                    @VerifyParam(required = true) String path) {
        // 验证共享的有效性，并从会话中获取共享信息
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        // 调用上级类的方法，获取目录信息，并返回结果
        return super.getFolderInfo(path, shareSessionDto.getShareUserId());
    }

    /**
     * 根据分享ID和文件ID获取文件
     * 该方法主要用于处理用户通过分享链接获取文件的请求
     *
     * @param response 用于向客户端返回文件数据的对象
     * @param session 用于存储用户会话信息，此处用于验证分享的有效性
     * @param shareId 分享的唯一标识符，用于确定是哪个分享的文件
     * @param fileId 文件的唯一标识符，用于指定需要获取的文件
     */
    @RequestMapping("/getFile/{shareId}/{fileId}")
    public void getFile(HttpServletResponse response, HttpSession session,
                            @PathVariable("shareId") @VerifyParam(required = true) String shareId,
                            @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        // 验证分享的有效性并获取分享相关的用户信息
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        // 调用父类方法获取文件并返回给客户端
        super.getFile(response, fileId, shareSessionDto.getShareUserId());
    }

    /**
     * 根据分享ID和文件ID获取视频信息
     *
     * @param response HTTP响应对象，用于向客户端返回数据
     * @param session HTTP会话对象，用于存储客户端的会话信息
     * @param shareId 分享ID，用于标识特定的分享
     * @param fileId 文件ID，用于标识特定的文件
     */
    @RequestMapping("/ts/getVideoInfo/{shareId}/{fileId}")
    public void getVideoInfo(HttpServletResponse response,
                             HttpSession session,
                             @PathVariable("shareId") @VerifyParam(required = true) String shareId,
                             @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        // 检查分享的有效性并获取分享用户的会话信息
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        // 根据文件ID和分享用户的ID获取文件信息，并通过HTTP响应返回给客户端
        super.getFile(response, fileId, shareSessionDto.getShareUserId());
    }

    /**
     * 创建文件下载链接
     * 该方法用于生成特定文件的下载URL，不需要用户登录验证，但需要正确的shareId和fileId参数
     *
     * @param session HttpSession对象，用于检查分享会话状态
     * @param shareId 分享标识符，用于确定特定的分享活动
     * @param fileId 文件标识符，用于指定需要生成下载链接的文件
     * @return 返回一个ResponseVO对象，其中包含生成的下载URL信息
     */
    @RequestMapping("/createDownloadUrl/{shareId}/{fileId}")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO createDownloadUrl(HttpSession session,
                                        @PathVariable("shareId") @VerifyParam(required = true) String shareId,
                                        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        // 检查分享有效性并获取分享会话信息
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        // 调用父类方法生成文件下载链接，并返回生成结果
        return super.createDownloadUrl(fileId, shareSessionDto.getShareUserId());
    }

    /**
     * 下载
     *
     * 本方法用于处理文件下载请求通过指定的代码获取文件并将其传输到客户端
     * 它被设计为一个控制器方法处理"/download/{code}"路径的请求
     *
     * @param request 代表HTTP请求对象，包含请求相关的所有信息
     * @param response 代表HTTP响应对象，用于向客户端发送响应数据
     * @param code 文件的唯一代码，用于标识和检索要下载的文件
     * @throws Exception 如果在执行下载过程中遇到任何错误，抛出异常
     */
    @RequestMapping("/download/{code}")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @PathVariable("code") @VerifyParam(required = true) String code) throws Exception {
        // 调用父类的download方法处理实际的文件下载逻辑
        super.download(request, response, code);
    }

    /**
     * 保存分享
     *
     * 此方法用于用户保存其他用户分享的文件到自己的网盘中
     * 它首先验证分享的合法性和用户身份，然后调用服务层方法完成文件的保存
     *
     * @param session 当前用户的会话，用于获取用户信息和分享信息
     * @param shareId 分享的唯一标识符，用于验证分享和获取分享详情
     * @param shareFileIds 被分享文件的ID列表，表示用户想要保存的文件
     * @param myFolderId 用户的文件夹ID，表示用户想要将文件保存到哪个文件夹中
     * @return 返回操作结果，成功则返回成功响应，否则抛出异常
     */
    @RequestMapping("/saveShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO saveShare(HttpSession session,
                                @VerifyParam(required = true) String shareId,
                                @VerifyParam(required = true) String shareFileIds,
                                @VerifyParam(required = true) String myFolderId) {
        // 检查分享信息是否有效并获取分享会话详情
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        // 从会话中获取当前用户信息
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        // 验证分享者和接收者是否为同一用户，如果是，则抛出异常，因为用户不能保存自己的分享到自己的网盘
        if (shareSessionDto.getShareUserId().equals(webUserDto.getUserId())) {
            throw new BusinessException("自己分享的文件无法保存到自己的网盘");
        }
        // 调用服务层方法，保存分享的文件到用户指定的文件夹中
        fileInfoService.saveShare(shareSessionDto.getFileId(), shareFileIds, myFolderId, shareSessionDto.getShareUserId(), webUserDto.getUserId());
        // 返回成功响应
        return getSuccessResponseVO(null);
    }
}
