package com.open.agent.lite.core.ability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 能力抽象基类
 * 提供通用的能力实现
 */
public abstract class AbstractAbility implements Ability {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final String name;
    private final String description;

    /**
     * 构造函数
     * @param name 能力名称
     * @param description 能力描述
     */
    protected AbstractAbility(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String execute(Map<String, String> params) {
        try {
            logger.info("Executing ability: {}, params: {}", name, params);
            String result = doExecute(params);
            logger.debug("Ability execution result: {}", result);
            return result;
        } catch (Exception e) {
            logger.error("Ability execution failed: {}", e.getMessage(), e);
            return "能力执行失败: " + e.getMessage();
        }
    }

    /**
     * 实际执行能力的方法
     * @param params 能力参数
     * @return 执行结果
     */
    protected abstract String doExecute(Map<String, String> params);

    @Override
    public boolean isAvailable() {
        try {
            return checkAvailability();
        } catch (Exception e) {
            logger.warn("Failed to check availability for ability {}: {}", name, e.getMessage());
            return false;
        }
    }

    /**
     * 检查能力是否可用
     * @return 是否可用
     */
    protected abstract boolean checkAvailability();
}
