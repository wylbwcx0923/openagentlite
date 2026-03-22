package com.open.agent.lite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 * 负责返回前端页面
 */
@Controller
public class HomeController {

    /**
     * 返回首页
     * @return 首页视图
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * 返回首页（备用路径）
     * @return 首页视图
     */
    @GetMapping("/index")
    public String index() {
        return "index";
    }
}
