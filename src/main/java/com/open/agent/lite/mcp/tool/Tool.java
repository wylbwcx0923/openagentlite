package com.open.agent.lite.mcp.tool;

import java.util.Map;

/**
 * 工具接口
 */
public interface Tool {

    /**
     * 获取工具名称
     * @return 工具名称
     */
    String getName();

    /**
     * 获取工具描述
     * @return 工具描述
     */
    String getDescription();

    /**
     * 执行工具
     * @param params 工具参数
     * @return 执行结果
     */
    String execute(Map<String, String> params);

    /**
     * 获取工具参数描述
     * @return 参数描述
     */
    String getParamsDescription();
}