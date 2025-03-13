package com.easypan.controller;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.exception.BusinessException;


import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class  AGlobalExceptionHandlerController extends ABaseController {

    private static final Logger logger = LoggerFactory.getLogger(AGlobalExceptionHandlerController.class);

    /**
     * 全局异常处理器，用于处理控制器层未捕获的异常
     *
     * @param e 异常对象，包含异常信息
     * @param request HttpServletRequest对象，用于获取请求信息
     * @return 返回一个对象，包含错误信息的响应
     */
    @ExceptionHandler(value = Exception.class)
    Object handleException(Exception e, HttpServletRequest request) {
        // 记录错误日志，包括请求地址和错误信息
        logger.error("请求错误，请求地址{},错误信息:", request.getRequestURL(), e);

        // 创建一个响应对象用于封装错误信息
        ResponseVO ajaxResponse = new ResponseVO();

        // 判断异常类型并设置相应的错误码和信息
        // 404错误，表示请求的资源未找到
        if (e instanceof NoHandlerFoundException) {
            ajaxResponse.setCode(ResponseCodeEnum.CODE_404.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_404.getMsg());
            ajaxResponse.setStatus(STATIC_ERROR);
        } else if (e instanceof BusinessException biz) {
            // 业务错误，根据业务异常设置错误码和信息
            ajaxResponse.setCode(biz.getCode() == null ? ResponseCodeEnum.CODE_600.getCode() : biz.getCode());
            ajaxResponse.setInfo(biz.getMessage());
            ajaxResponse.setStatus(STATIC_ERROR);
        } else if (e instanceof BindException|| e instanceof MethodArgumentTypeMismatchException) {
            // 参数类型错误，设置错误码和信息
            ajaxResponse.setCode(ResponseCodeEnum.CODE_600.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_600.getMsg());
            ajaxResponse.setStatus(STATIC_ERROR);
        } else if (e instanceof DuplicateKeyException) {
            // 主键冲突错误，设置错误码和信息
            ajaxResponse.setCode(ResponseCodeEnum.CODE_601.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_601.getMsg());
            ajaxResponse.setStatus(STATIC_ERROR);
        } else {
            // 其他未知错误，设置通用的错误码和信息
            ajaxResponse.setCode(ResponseCodeEnum.CODE_500.getCode());
            ajaxResponse.setInfo(ResponseCodeEnum.CODE_500.getMsg());
            ajaxResponse.setStatus(STATIC_ERROR);
        }

        // 返回封装好的响应对象
        return ajaxResponse;
    }
}
