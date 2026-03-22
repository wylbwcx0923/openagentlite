package com.open.agent.lite.core.engine.handler;

import com.alibaba.fastjson.JSONObject;
import com.open.agent.lite.core.ability.Ability;
import com.open.agent.lite.core.ability.AbilityManager;
import com.open.agent.lite.core.engine.ReActContext;
import com.open.agent.lite.core.engine.parser.ResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 能力操作器
 * 负责系统能力操作，包括解析能力参数和执行能力
 */
public class AbilityOperator {

    private static final Logger logger = LoggerFactory.getLogger(AbilityOperator.class);
    private final AbilityManager abilityManager;
    private final ResponseParser responseParser;

    public AbilityOperator(AbilityManager abilityManager, ResponseParser responseParser) {
        this.abilityManager = abilityManager;
        this.responseParser = responseParser;
    }

    /**
     * 调用系统能力，执行操作
     * @param ctx 上下文
     */
    public void operate(ReActContext ctx) {
        try {
            // 获取最近的LLM响应
            String lastResponse = ctx.getLastResponse();
            if (lastResponse == null) {
                logger.warn("没有找到LLM响应");
                return;
            }

            // 解析能力名称
            String abilityName = responseParser.getTarget(lastResponse);
            if (abilityName == null || abilityName.isEmpty()) {
                logger.warn("响应中没有能力名称");
                return;
            }

            // 解析能力参数
            Map<String, String> params = parseAbilityParams(lastResponse);

            // 调用系统能力
            Ability ability = abilityManager.getAbility(abilityName);
            if (ability != null) {
                String result = ability.execute(params);
                ctx.setAnswer(result);
                logger.info("能力执行完成: {}, 参数: {}, 结果: {}", abilityName, params, result);
            } else {
                logger.warn("能力不存在: {}", abilityName);
                ctx.setAnswer("能力不存在: " + abilityName);
            }
        } catch (Exception e) {
            logger.error("操作失败: {}", e.getMessage(), e);
            ctx.setAnswer("操作失败: " + e.getMessage());
        }
    }

    /**
     * 解析能力参数
     * @param response LLM响应
     * @return 参数映射
     */
    private Map<String, String> parseAbilityParams(String response) {
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