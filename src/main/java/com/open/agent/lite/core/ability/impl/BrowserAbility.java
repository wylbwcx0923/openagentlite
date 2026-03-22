package com.open.agent.lite.core.ability.impl;

import com.open.agent.lite.core.ability.AbstractAbility;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.URI;
import java.util.Map;

/**
 * 浏览器能力
 * 提供打开浏览器和访问网页的功能
 */
@Component
public class BrowserAbility extends AbstractAbility {

    public BrowserAbility() {
        super("BrowserAbility", "浏览器操作能力，支持打开浏览器和访问网页");
    }

    @Override
    protected String doExecute(Map<String, String> params) {
        String url = params.get("url");

        if (url == null) {
            return "参数错误：缺少url参数";
        }

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return "浏览器打开成功：" + url;
            } else {
                // 尝试使用系统命令打开浏览器
                String os = System.getProperty("os.name").toLowerCase();
                Runtime runtime = Runtime.getRuntime();
                if (os.contains("win")) {
                    runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
                } else if (os.contains("mac")) {
                    runtime.exec("open " + url);
                } else if (os.contains("linux")) {
                    runtime.exec("xdg-open " + url);
                } else {
                    return "不支持的操作系统：" + os;
                }
                return "浏览器打开成功：" + url;
            }
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }

    @Override
    public String getParamsDescription() {
        return "url: 网页地址";
    }

    @Override
    protected boolean checkAvailability() {
        return true;
    }
}
