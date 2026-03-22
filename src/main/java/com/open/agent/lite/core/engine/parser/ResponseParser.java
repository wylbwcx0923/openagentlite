package com.open.agent.lite.core.engine.parser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.open.agent.lite.core.engine.ReActStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LLM响应解析器
 * 负责解析LLM的响应，确定下一步操作
 */
public class ResponseParser {

    private static final Logger logger = LoggerFactory.getLogger(ResponseParser.class);

    /**
     * 解析LLM响应，确定下一步操作
     * @param response LLM响应
     * @return 下一步操作
     */
    public ReActStep parseNextStep(String response) {
        try {
            // 解析JSON响应
            JSONObject jsonObject = parseResponseJson(response);
            String action = jsonObject.getString("action");

            // 根据action字段确定下一步操作
            if ("select_skill".equals(action)) {
                return ReActStep.SELECT_SKILL;
            } else if ("call_tool".equals(action)) {
                return ReActStep.CALL_TOOL;
            } else if ("end".equals(action)) {
                return ReActStep.END;
            } else if ("answer".equals(action)) {
                return ReActStep.ANSWER;
            } else if ("operate".equals(action)) {
                return ReActStep.OPERATE;
            }
        } catch (Exception e) {
            logger.error("解析LLM响应失败: {}", e.getMessage(), e);
        }

        // 默认返回回答
        return ReActStep.ANSWER;
    }

    /**
     * 解析LLM响应中的JSON对象
     * @param response LLM响应
     * @return JSON对象
     */
    public JSONObject parseResponseJson(String response) {
        try {
            // 只取json部分，其他的去除
            String jsonStr = response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1);
            return JSON.parseObject(jsonStr);
        } catch (Exception e) {
            logger.error("解析JSON失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从响应中获取目标（技能或工具名称）
     * @param response LLM响应
     * @return 目标名称
     */
    public String getTarget(String response) {
        JSONObject jsonObject = parseResponseJson(response);
        if (jsonObject != null) {
            return jsonObject.getString("target");
        }
        return null;
    }

    /**
     * 从响应中获取参数
     * @param response LLM响应
     * @return 参数映射
     */
    public JSONObject getParams(String response) {
        JSONObject jsonObject = parseResponseJson(response);
        if (jsonObject != null) {
            return jsonObject.getJSONObject("params");
        }
        return null;
    }

    /**
     * 从响应中获取答案
     * @param response LLM响应
     * @return 答案
     */
    public String getAnswer(String response) {
        JSONObject jsonObject = parseResponseJson(response);
        if (jsonObject != null) {
            return jsonObject.getString("answer");
        }
        return null;
    }
}