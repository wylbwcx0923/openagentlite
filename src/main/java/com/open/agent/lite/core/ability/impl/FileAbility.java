package com.open.agent.lite.core.ability.impl;

import com.open.agent.lite.core.ability.AbstractAbility;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 文件能力
 * 提供文件读写操作
 */
@Component
public class FileAbility extends AbstractAbility {

    public FileAbility() {
        super("FileAbility", "文件读写能力，支持文件的读取和写入操作");
    }

    @Override
    protected String doExecute(Map<String, String> params) {
        String operation = params.get("operation");
        String path = params.get("path");
        String content = params.get("content");

        if (operation == null || path == null) {
            return "参数错误：缺少operation或path参数";
        }

        try {
            Path filePath = Paths.get(path);
            return switch (operation) {
                case "read" -> readFile(filePath);
                case "write" -> {
                    if (content == null) {
                        yield "参数错误：写入操作缺少content参数";
                    }
                    yield writeFile(filePath, content);
                }
                case "exists" -> String.valueOf(Files.exists(filePath));
                case "delete" -> deleteFile(filePath);
                default -> "不支持的操作：" + operation;
            };
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }

    private String readFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            return "文件不存在：" + path;
        }
        if (!Files.isRegularFile(path)) {
            return "路径不是文件：" + path;
        }
        return Files.readString(path);
    }

    private String writeFile(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);
        return "文件写入成功：" + path;
    }

    private String deleteFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            return "文件不存在：" + path;
        }
        Files.delete(path);
        return "文件删除成功：" + path;
    }

    @Override
    public String getParamsDescription() {
        return "operation: 操作类型(read/write/exists/delete), path: 文件路径, content: 写入内容(仅write操作需要)";
    }

    @Override
    protected boolean checkAvailability() {
        return true;
    }
}
