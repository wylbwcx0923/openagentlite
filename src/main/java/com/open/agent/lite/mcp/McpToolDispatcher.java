package com.open.agent.lite.mcp;

import com.open.agent.lite.mcp.tool.ToolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * MCP工具调度器
 * 负责执行工具调用
 */
@Component
public class McpToolDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(McpToolDispatcher.class);
    private final ToolManager toolManager;

    /**
     * 构造函数
     * @param toolManager 工具管理器
     */
    public McpToolDispatcher(ToolManager toolManager) {
        this.toolManager = toolManager;
        logger.info("McpToolDispatcher initialized");
    }

    /**
     * 执行工具
     * @param toolName 工具名称
     * @param params 工具参数
     * @return 执行结果
     */
    public String execute(String toolName, Map<String, String> params) {
        logger.info("Executing tool: {}, params: {}", toolName, params);
        try {
            String result = toolManager.executeTool(toolName, params);
            logger.debug("Tool execution result: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Tool execution failed: {}", e.getMessage(), e);
            return "工具执行失败: " + e.getMessage();
        }
    }

    /**
     * 执行工具（简化版）
     * @param toolCall 工具调用字符串
     * @return 执行结果
     */
    public String execute(String toolCall) {
        logger.info("Executing tool call: {}", toolCall);
        try {
            // 解析工具调用字符串
            String[] parts = toolCall.split(" ", 2);
            if (parts.length < 1) {
                return "工具调用格式错误";
            }
            String toolName = parts[0];
            Map<String, String> params = new java.util.HashMap<>();
            
            if (parts.length > 1) {
                String[] paramParts = parts[1].split(" ");
                for (String param : paramParts) {
                    String[] keyValue = param.split("=", 2);
                    if (keyValue.length == 2) {
                        params.put(keyValue[0], keyValue[1]);
                    }
                }
            }
            
            return execute(toolName, params);
        } catch (Exception e) {
            logger.error("Tool call parsing failed: {}", e.getMessage(), e);
            return "工具调用解析失败: " + e.getMessage();
        }
    }
}