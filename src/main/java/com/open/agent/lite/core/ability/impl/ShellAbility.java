package com.open.agent.lite.core.ability.impl;

import com.open.agent.lite.core.ability.AbstractAbility;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Shell能力
 * 提供执行Shell脚本和命令的功能
 */
@Component
public class ShellAbility extends AbstractAbility {

    public ShellAbility() {
        super("ShellAbility", "Shell执行能力，支持执行Shell脚本和命令");
    }

    @Override
    protected String doExecute(Map<String, String> params) {
        String command = params.get("command");

        if (command == null) {
            return "参数错误：缺少command参数";
        }

        try {
            String os = System.getProperty("os.name").toLowerCase();
            Process process;
            if (os.contains("win")) {
                process = Runtime.getRuntime().exec("cmd.exe /c " + command);
            } else {
                process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            }

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            StringBuilder error = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    error.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return "命令执行成功\n输出：\n" + output.toString();
            } else {
                return "命令执行失败，退出码：" + exitCode + "\n错误：\n" + error.toString();
            }
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }

    @Override
    public String getParamsDescription() {
        return "command: Shell命令";
    }

    @Override
    protected boolean checkAvailability() {
        return true;
    }
}
