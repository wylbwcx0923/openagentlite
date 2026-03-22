package com.open.agent.lite.core.ability.impl;

import com.open.agent.lite.core.ability.AbstractAbility;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.util.Map;

/**
 * 应用启动能力
 * 提供启动应用程序的功能
 */
@Component
public class AppAbility extends AbstractAbility {

    public AppAbility() {
        super("AppAbility", "应用启动能力，支持启动应用程序");
    }

    @Override
    protected String doExecute(Map<String, String> params) {
        String path = params.get("path");

        if (path == null) {
            return "参数错误：缺少path参数";
        }

        try {
            File appFile = new File(path);
            if (!appFile.exists()) {
                return "应用程序不存在：" + path;
            }

            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(appFile);
            } else {
                // 尝试使用系统命令启动应用
                String os = System.getProperty("os.name").toLowerCase();
                Runtime runtime = Runtime.getRuntime();
                if (os.contains("win")) {
                    runtime.exec("cmd.exe /c start " + path);
                } else if (os.contains("mac")) {
                    runtime.exec("open " + path);
                } else if (os.contains("linux")) {
                    runtime.exec("xdg-open " + path);
                } else {
                    return "不支持的操作系统：" + os;
                }
            }
            return "应用启动成功：" + path;
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }

    @Override
    public String getParamsDescription() {
        return "path: 应用程序路径";
    }

    @Override
    protected boolean checkAvailability() {
        return true;
    }
}
