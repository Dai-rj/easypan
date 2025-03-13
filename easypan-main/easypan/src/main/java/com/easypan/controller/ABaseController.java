package com.easypan.controller;
import com.easypan.entity.constants.Constants;
import com.easypan.entity.dto.SessionWebUserDto;
import com.easypan.entity.enums.ResponseCodeEnum;
import com.easypan.entity.vo.PaginationResultVO;
import com.easypan.entity.vo.ResponseVO;
import com.easypan.exception.BusinessException;
import com.easypan.utils.CopyTools;
import com.easypan.utils.StringTools;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;


public class ABaseController {

    public static final Logger logger = LoggerFactory.getLogger(ABaseController.class);

    protected static final String STATIC_SUCCESS = "success";

    protected static final String STATIC_ERROR = "error";

    /**
     * 创建一个成功的响应对象
     * 该方法用于封装一个成功响应的ResponseVO对象，包含状态、响应码、消息和具体数据
     *
     * @param t 泛型参数，表示具体的业务数据，可以是任意类型
     * @return 返回一个封装了成功响应信息的ResponseVO对象
     */
    protected <T> ResponseVO<T> getSuccessResponseVO(T t) {
        // 创建一个空的ResponseVO对象
        ResponseVO<T> responseVO = new ResponseVO<>();

        // 设置响应状态为成功
        responseVO.setStatus(STATIC_SUCCESS);

        // 设置响应码为200，表示请求成功
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());

        // 设置响应消息，这里使用枚举中预定义的成功消息
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());

        // 设置具体的业务数据
        responseVO.setData(t);

        // 返回封装好的ResponseVO对象
        return responseVO;
    }

    /**
     * 创建一个业务错误的响应对象
     * 当业务异常发生时，此方法用于构建一个包含错误信息的响应对象
     * 它处理异常信息，设置响应代码和错误信息，并可选地包含异常数据
     *
     * @param e 业务异常对象，包含错误代码和错误信息
     * @param t 可选的附加数据，通常用于传递异常相关的业务对象
     * @return 返回一个包含错误信息和可选数据的响应对象
     */
    protected <T> ResponseVO getBusinessErrorResponseVO(BusinessException e, T t) {
        // 初始化一个新的响应对象
        ResponseVO vo = new ResponseVO();
        // 设置响应状态为错误
        vo.setStatus(STATIC_ERROR);
        // 检查异常代码是否为空，如果为空则使用默认错误代码
        if (e.getCode() == null) {
            vo.setCode(ResponseCodeEnum.CODE_600.getCode());
        } else {
            // 否则使用异常对象中的错误代码
            vo.setCode(e.getCode());
        }
        // 设置错误信息为异常消息
        vo.setInfo(e.getMessage());
        // 可选地设置异常数据
        vo.setData(t);
        // 返回构建好的响应对象
        return vo;
    }

    /**
     * 创建一个表示服务器错误的ResponseVO对象
     * 此方法用于封装任何类型的对象到一个表示500服务器错误的响应对象中
     * 它设置响应的状态为错误，错误代码为500，并提供标准的错误信息
     *
     * @param t 任何类型的对象，表示需要封装到响应中的数据
     * @return 返回一个包含错误信息和数据的ResponseVO对象
     */
    protected <T> ResponseVO getServerErrorResponseVO(T t) {
        // 创建一个新的ResponseVO对象
        ResponseVO vo = new ResponseVO();
        // 设置响应状态为错误
        vo.setStatus(STATIC_ERROR);
        // 设置响应代码为500，表示服务器内部错误
        vo.setCode(ResponseCodeEnum.CODE_500.getCode());
        // 设置响应的信息为500对应的错误消息
        vo.setInfo(ResponseCodeEnum.CODE_500.getMsg());
        // 将传入的对象设置为响应的数据部分
        vo.setData(t);
        // 返回构建好的ResponseVO对象
        return vo;
    }

    /**
     * 将分页结果转换为指定类型的分页结果VO
     * 此方法用于在保持分页信息不变的情况下，将分页结果中的列表项转换为另一种类型
     *
     * @param <S> 源分页结果中列表项的类型
     * @param <T> 目标分页结果中列表项的类型
     * @param result 源分页结果VO，包含分页信息和列表数据
     * @param classz 目标分页结果中列表项的类型
     * @return 转换后的分页结果VO，包含转换类型后的列表数据和不变的分页信息
     */
    protected <S, T> PaginationResultVO<T> convert2PaginationVO(PaginationResultVO<S> result, Class<T> classz) {
        // 创建一个新的分页结果VO，用于承载转换后的数据
        PaginationResultVO<T> resultVO = new PaginationResultVO<>();
        // 将源分页结果中的列表项转换为指定类型，并设置到目标分页结果VO中
        resultVO.setList(CopyTools.copyList(result.getList(), classz));
        // 将源分页结果中的页码信息复制到目标分页结果VO中
        resultVO.setPageNo(result.getPageNo());
        resultVO.setPageSize(result.getPageSize());
        resultVO.setPageTotal(result.getPageTotal());
        resultVO.setTotalCount(result.getTotalCount());
        // 返回转换后的分页结果VO
        return resultVO;
    }

    /**
     * 读取文件内容并将其写入HTTP响应中
     *
     * @param response HTTP响应对象，用于输出文件内容到客户端
     * @param filePath 文件路径，指定需要读取的文件位置
     */
    protected void readFile(HttpServletResponse response, String filePath) {
        // 检查文件路径是否有效，无效则直接返回
        if (!StringTools.pathIsOk(filePath)) {
            return;
        }

        OutputStream out = null;
        FileInputStream in = null;

        try {
            // 创建文件对象
            File file = new File(filePath);
            // 如果文件不存在，则直接返回
            if (!file.exists()) {
                return;
            }

            // 创建文件输入流
            in = new FileInputStream(file);
            byte[] byteData = new byte[1024];

            // 获取响应的输出流
            out = response.getOutputStream();
            int len = 0;

            // 循环读取文件内容并写入响应输出流
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }

            // 刷新输出流，确保所有数据都被写入
            out.flush();
        } catch (Exception e) {
            // 记录文件读取异常
            logger.error("读取文件异常", e);
        } finally {
            // 关闭输出流
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // 记录关闭输出流时的IO异常
                    logger.error("IO异常", e);
                }
            }

            // 关闭文件输入流
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // 记录关闭文件输入流时的IO异常
                    logger.error("IO异常", e);
                }
            }
        }
    }

    /**
     * 从HTTP会话中获取用户信息
     *
     * 该方法旨在从给定的HTTP会话中提取用户信息，具体是以SessionWebUserDto对象的形式存在
     * 它通过使用Constants.SESSION_KEY作为键来从会话属性中获取用户信息
     *
     * @param session HttpSession对象，代表用户的会话
     * @return SessionWebUserDto对象，包含从会话中获取的用户信息如果没有找到，则返回null
     */
    protected SessionWebUserDto getUserInfoFromSession(HttpSession session) {
        return (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
    }
}
