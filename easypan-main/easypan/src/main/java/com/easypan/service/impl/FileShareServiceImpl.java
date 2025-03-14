package com.easypan.service.impl;

import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SessionShareDto;
import com.easypan.entity.enums.PageSize;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.entity.enums.ShareValidTypeEnums;
import com.easypan.entity.po.FileShare;
import com.easypan.entity.query.FileShareQuery;
import com.easypan.entity.query.SimplePage;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.exception.BusinessException;
import com.easypan.mappers.FileShareMapper;
import com.easypan.service.FileShareService;
import com.easypan.utils.DateUtil;
import com.easypan.utils.StringTools;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * 分享信息 业务接口实现
 */
@Service("fileShareService")
public class FileShareServiceImpl implements FileShareService {

    @Resource
    private FileShareMapper<FileShare, FileShareQuery> fileShareMapper;

    /**
     * 根据条件查询列表
     *
     * @param param 查询条件
     * @return 符合条件的分享信息列表
     */
    @Override
    public List<FileShare> findListByParam(FileShareQuery param) {
        return this.fileShareMapper.selectList(param);
    }

    /**
     * 根据条件查询计数
     *
     * @param param 查询条件
     * @return 符合条件的分享信息数量
     */
    @Override
    public Integer findCountByParam(FileShareQuery param) {
        return this.fileShareMapper.selectCount(param);
    }

    /**
     * 分页查询方法
     *
     * @param param 查询条件，包括页码和页面大小
     * @return 分页后的分享信息结果集
     */
    @Override
    public PaginationResultVO<FileShare> findListByPage(FileShareQuery param) {
        int count = this.findCountByParam(param);
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        param.setSimplePage(page);
        List<FileShare> list = this.findListByParam(param);
        PaginationResultVO<FileShare> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
        return result;
    }

    /**
     * 新增分享信息
     *
     * @param bean 待插入的分享信息对象
     * @return 插入的行数
     */
    @Override
    public Integer add(FileShare bean) {
        return this.fileShareMapper.insert(bean);
    }

    /**
     * 批量新增分享信息
     *
     * @param listBean 待插入的分享信息对象列表
     * @return 插入的行数
     */
    @Override
    public Integer addBatch(List<FileShare> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileShareMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或修改分享信息
     *
     * @param listBean 待插入或更新的分享信息对象列表
     * @return 影响的行数
     */
    @Override
    public Integer addOrUpdateBatch(List<FileShare> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileShareMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 根据ShareId获取分享信息对象
     *
     * @param shareId 分享ID
     * @return 分享信息对象
     */
    @Override
    public FileShare getFileShareByShareId(String shareId) {
        return this.fileShareMapper.selectByShareId(shareId);
    }

    /**
     * 根据ShareId修改分享信息
     *
     * @param bean    待更新的分享信息对象
     * @param shareId 分享ID
     * @return 影响的行数
     */
    @Override
    public Integer updateFileShareByShareId(FileShare bean, String shareId) {
        return this.fileShareMapper.updateByShareId(bean, shareId);
    }

    /**
     * 根据ShareId删除分享信息
     *
     * @param shareId 分享ID
     * @return 影响的行数
     */
    @Override
    public Integer deleteFileShareByShareId(String shareId) {
        return this.fileShareMapper.deleteByShareId(shareId);
    }

    /**
     * 保存分享信息，包括设置过期时间和生成随机分享ID与提取码
     *
     * @param share 待保存的分享信息对象
     */
    @Override
    public void saveShare(FileShare share) {
        ShareValidTypeEnums typeEnum = ShareValidTypeEnums.getByType(share.getValidType());
        if (null == typeEnum) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (typeEnum != ShareValidTypeEnums.FOREVER) {
            share.setExpireTime(DateUtil.getAfterDate(typeEnum.getDays()));
        }
        Date curDate = new Date();
        share.setShareTime(curDate);
        if (StringTools.isEmpty(share.getCode())) {
            share.setCode(StringTools.getRandomString(Constants.LENGTH_5));
        }
        share.setShareId(StringTools.getRandomString(Constants.LENGTH_20));
        this.fileShareMapper.insert(share);
    }

    /**
     * 批量删除分享信息
     *
     * @param shareIdArray 待删除的分享ID数组
     * @param userId       用户ID，用于确认删除权限
     * @throws BusinessException 如果删除的分享信息数量与预期不符，抛出业务异常
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFileShareBatch(String[] shareIdArray, String userId) {
        Integer count = this.fileShareMapper.deleteFileShareBatch(shareIdArray, userId);
        if (count != shareIdArray.length) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

    /**
     * 校验分享提取码，并更新分享的浏览次数
     *
     * @param shareId 分享ID
     * @param code    提取码
     * @return 包含分享信息的会话DTO对象
     * @throws BusinessException 如果分享信息不存在、已过期或提取码错误，抛出业务异常
     */
    @Override
    public SessionShareDto checkShareCode(String shareId, String code) {
        FileShare share = this.fileShareMapper.selectByShareId(shareId);
        if (null == share || (share.getExpireTime() != null && new Date().after(share.getExpireTime()))) {
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }
        if (!share.getCode().equals(code)) {
            throw new BusinessException("提取码错误");
        }

        //更新浏览次数
        this.fileShareMapper.updateShareShowCount(shareId);
        SessionShareDto shareSessionDto = new SessionShareDto();
        shareSessionDto.setShareId(shareId);
        shareSessionDto.setShareUserId(share.getUserId());
        shareSessionDto.setFileId(share.getFileId());
        shareSessionDto.setExpireTime(share.getExpireTime());
        return shareSessionDto;
    }
}
