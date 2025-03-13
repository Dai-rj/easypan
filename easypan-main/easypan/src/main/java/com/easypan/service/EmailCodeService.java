package com.easypan.service;

import com.easypan.entity.po.EmailCode;
import com.easypan.entity.query.EmailCodeQuery;
import com.easypan.entity.vo.PaginationResultVO;

import java.util.List;

/**
 * 邮箱验证码 业务接口
 */
public interface EmailCodeService {

    /**
     * 根据条件查询列表
     * @param param 查询条件
     * @return 符合条件的邮箱验证码列表
     */
    List<EmailCode> findListByParam(EmailCodeQuery param);

    /**
     * 根据条件查询数量
     * @param param 查询条件
     * @return 符合条件的邮箱验证码数量
     */
    Integer findCountByParam(EmailCodeQuery param);

    /**
     * 分页查询
     * @param param 分页查询条件
     * @return 分页查询结果
     */
    PaginationResultVO<EmailCode> findListByPage(EmailCodeQuery param);

    /**
     * 新增邮箱验证码
     * @param bean 待新增的邮箱验证码对象
     * @return 新增的邮箱验证码数量
     */
    Integer add(EmailCode bean);

    /**
     * 批量新增邮箱验证码
     * @param listBean 待新增的邮箱验证码列表
     * @return 新增的邮箱验证码数量
     */
    Integer addBatch(List<EmailCode> listBean);

    /**
     * 批量新增或修改邮箱验证码
     * @param listBean 待新增或修改的邮箱验证码列表
     * @return 新增或修改的邮箱验证码数量
     */
    Integer addOrUpdateBatch(List<EmailCode> listBean);

    /**
     * 多条件更新邮箱验证码
     * @param bean 待更新的邮箱验证码对象
     * @param param 查询条件
     * @return 更新的邮箱验证码数量
     */
    Integer updateByParam(EmailCode bean, EmailCodeQuery param);

    /**
     * 多条件删除邮箱验证码
     * @param param 查询条件
     * @return 删除的邮箱验证码数量
     */
    Integer deleteByParam(EmailCodeQuery param);

    /**
     * 根据邮箱和验证码查询邮箱验证码对象
     * @param email 邮箱
     * @param code 验证码
     * @return 邮箱验证码对象
     */
    EmailCode getEmailCodeByEmailAndCode(String email, String code);

    /**
     * 根据邮箱和验证码修改邮箱验证码
     * @param bean 待修改的邮箱验证码对象
     * @param email 邮箱
     * @param code 验证码
     * @return 修改的邮箱验证码数量
     */
    Integer updateEmailCodeByEmailAndCode(EmailCode bean, String email, String code);

    /**
     * 根据邮箱和验证码删除邮箱验证码
     * @param email 邮箱
     * @param code 验证码
     * @return 删除的邮箱验证码数量
     */
    Integer deleteEmailCodeByEmailAndCode(String email, String code);

    /**
     * 发送邮箱验证码
     * @param email 邮箱
     * @param type 验证码类型
     */
    void sendEmailCode(String email, Integer type);

    /**
     * 校验验证码
     * @param email 邮箱
     * @param code 验证码
     */
    void checkCode(String email, String code);
}
