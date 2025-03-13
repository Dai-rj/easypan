package com.easypan.utils;

import com.easypan.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 提供执行系统命令功能的工具类，主要用于执行FFmpeg命令
 */
public class ProcessUtils {
    private static final Logger logger = LoggerFactory.getLogger(ProcessUtils.class);

    /**
     * 执行给定的系统命令，并根据参数决定是否记录执行结果日志
     *
     * @param cmd 要执行的系统命令，通常是FFmpeg命令
     * @param outprintLog 布尔值，决定是否输出执行结果日志
     * @throws BusinessException 当命令执行失败时抛出的异常
     */
    public static void executeCommand(String cmd, Boolean outprintLog) throws BusinessException {
        // 检查命令是否为空，如果为空则记录错误日志并返回
        if (StringTools.isEmpty(cmd)) {
            logger.error("--- 指令执行失败，因为要执行的FFmpeg指令为空！ ---");
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            // 执行系统命令
            process = Runtime.getRuntime().exec(cmd);
            // 执行ffmpeg指令
            // 取出输出流和错误流的信息
            // 注意：必须要取出ffmpeg在执行命令过程中产生的输出信息，如果不取的话当输出流信息填满jvm存储输出留信息的缓冲区时，线程就回阻塞住
            PrintStream errorStream = new PrintStream(process.getErrorStream());
            PrintStream inputStream = new PrintStream(process.getInputStream());
            errorStream.start();
            inputStream.start();
            // 等待ffmpeg命令执行完
            process.waitFor();
            // 获取执行结果字符串
            String result = errorStream.stringBuffer.append(inputStream.stringBuffer + "\n").toString();
            // 输出执行的命令信息

            // 根据outprintLog参数决定是否记录执行结果日志
            if (outprintLog) {
                logger.info("执行命令:{}，已执行完毕,执行结果:{}", cmd, result);
            } else {
                logger.info("执行命令:{}，已执行完毕", cmd);
            }
        } catch (Exception e) {
            // 记录执行命令失败的错误信息，并抛出自定义异常
            // logger.error("执行命令失败:{} ", e.getMessage());
            e.printStackTrace();
            throw new BusinessException("视频转换失败");
        } finally {
            // 确保在程序退出前结束已开启的FFmpeg进程
            if (null != process) {
                ProcessKiller ffmpegKiller = new ProcessKiller(process);
                runtime.addShutdownHook(ffmpegKiller);
            }
        }
    }

    /**
     * 在程序退出前结束已有的FFmpeg进程
     */
    private static class ProcessKiller extends Thread {
        private final Process process;

        public ProcessKiller(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            this.process.destroy();
        }
    }


    /**
     * 用于取出ffmpeg线程执行过程中产生的各种输出和错误流的信息
     */
    static class PrintStream extends Thread {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = new StringBuffer();

        public PrintStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try {
                // 检查输入流是否存在，如果不存在则直接返回
                if (null == inputStream) {
                    return;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                // 读取输入流中的每一行并追加到字符串缓冲区
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
            } catch (Exception e) {
                // 记录读取输入流过程中发生的异常
                logger.error("读取输入流出错了！错误信息：" + e.getMessage());
            } finally {
                try {
                    // 关闭输入流和缓冲区读取器
                    if (null != bufferedReader) {
                        bufferedReader.close();
                    }
                    if (null != inputStream) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    // 记录关闭输入流时发生的异常
                    logger.error("调用PrintStream读取输出流后，关闭流时出错！");
                }
            }
        }
    }
}
