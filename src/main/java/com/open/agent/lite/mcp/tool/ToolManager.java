package com.open.agent.lite.mcp.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具管理器
 * 负责管理和执行工具
 */
@Component
public class ToolManager {

    private static final Logger logger = LoggerFactory.getLogger(ToolManager.class);
    private final Map<String, Tool> tools = new HashMap<>();

    /**
     * 注册工具
     * @param tool 工具实例
     */
    public void registerTool(Tool tool) {
        tools.put(tool.getName(), tool);
        logger.info("Registered tool: {}", tool.getName());
    }

    /**
     * 获取工具
     * @param name 工具名称
     * @return 工具实例
     */
    public Tool getTool(String name) {
        return tools.get(name);
    }

    /**
     * 执行工具
     * @param name 工具名称
     * @param params 工具参数
     * @return 执行结果
     */
    public String executeTool(String name, Map<String, String> params) {
        Tool tool = getTool(name);
        if (tool == null) {
            logger.warn("Tool not found: {}", name);
            return "工具不存在: " + name;
        }
        try {
            logger.info("Executing tool: {}, params: {}", name, params);
            String result = tool.execute(params);
            logger.debug("Tool execution result: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Tool execution failed: {}", e.getMessage(), e);
            return "工具执行失败: " + e.getMessage();
        }
    }

    /**
     * 获取所有工具
     * @return 工具映射
     */
    public Map<String, Tool> getAllTools() {
        return tools;
    }

    /**
     * 获取工具描述
     * @return 工具描述列表
     */
    public String getToolsDescription() {
        StringBuilder sb = new StringBuilder();
        for (Tool tool : tools.values()) {
            sb.append(tool.getName()).append(": " ).append(tool.getDescription()).append("\n");
            sb.append("参数: " ).append(tool.getParamsDescription()).append("\n\n");
        }
        return sb.toString();
    }
}