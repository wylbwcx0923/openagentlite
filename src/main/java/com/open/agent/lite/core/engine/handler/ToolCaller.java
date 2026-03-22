package com.open.agent.lite.core.engine.handler;

import com.alibaba.fastjson.JSONObject;
import com.open.agent.lite.core.engine.ReActContext;
import com.open.agent.lite.core.engine.parser.ResponseParser;
import com.open.agent.lite.mcp.McpToolDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具调用器
 * 负责工具调用，包括解析工具参数和执行工具
 */
public class ToolCaller {

    private static final Logger logger = LoggerFactory.getLogger(ToolCaller.class);
    private final McpToolDispatcher toolDispatcher;
    private final ResponseParser responseParser;

    public ToolCaller(McpToolDispatcher toolDispatcher, ResponseParser responseParser) {
        this.toolDispatcher = toolDispatcher;
        this.responseParser = responseParser;
    }

    /**
     * 调用MCP工具
     * @param ctx 上下文
     */
    public void callTool(ReActContext ctx) {
        try {
            // 获取最近的LLM响应
            String lastResponse = ctx.getLastResponse();
            if (lastResponse == null) {
                logger.warn("没有找到LLM响应");
                return;
            }

            // 解析工具名称
            String toolName = responseParser.getTarget(lastResponse);
            if (toolName == null || toolName.isEmpty()) {
                logger.warn("响应中没有工具名称");
                return;
            }

            // 解析工具参数
            Map<String, String> params = parseToolParams(lastResponse);

            // 调用工具
            String toolResult = toolDispatcher.execute(toolName, params);
            ctx.setToolResult(toolResult);
            logger.info("工具调用完成: {}, 参数: {}, 结果: {}", toolName, params, toolResult);
        } catch (Exception e) {
            logger.error("调用工具时发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 解析工具参数
     * @param response LLM响应
     * @return 参数映射
     */
    private Map<String, String> parseToolParams(String response) {
        Map<String, String> params = new HashMap<>();
        JSONObject paramsJson = responseParser.getParams(response);
        if (paramsJson != null) {
            for (String key : paramsJson.keySet()) {
                params.put(key, paramsJson.getString(key));
            }
        }
        return params;
    }
}