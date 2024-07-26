package com.easypan.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//TestController 类用于处理与测试相关的HTTP请求。

@RestController
public class TestController {
//    处理/test路径的GET请求@return 字符串 "test"，作为HTTP响应的主体返回给客户端。
    @RequestMapping("/test")
    public String test(){
        return "test";
    }

}
