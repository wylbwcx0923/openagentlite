package com.open.agent.lite.core.ability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 能力管理器
 * 负责管理和执行核心能力
 */
@Component
public class AbilityManager {

    private static final Logger logger = LoggerFactory.getLogger(AbilityManager.class);
    private final Map<String, Ability> abilities = new HashMap<>();

    /**
     * 注册能力
     * @param ability 能力实例
     */
    public void registerAbility(Ability ability) {
        if (ability.isAvailable()) {
            abilities.put(ability.getName(), ability);
            logger.info("Registered ability: {}", ability.getName());
        } else {
            logger.warn("Ability {} is not available, skipping registration", ability.getName());
        }
    }

    /**
     * 获取能力
     * @param name 能力名称
     * @return 能力实例
     */
    public Ability getAbility(String name) {
        return abilities.get(name);
    }

    /**
     * 执行能力
     * @param name 能力名称
     * @param params 能力参数
     * @return 执行结果
     */
    public String executeAbility(String name, Map<String, String> params) {
        Ability ability = getAbility(name);
        if (ability == null) {
            logger.warn("Ability not found: {}", name);
            return "能力不存在: " + name;
        }
        try {
            return ability.execute(params);
        } catch (Exception e) {
            logger.error("Ability execution failed: {}", e.getMessage(), e);
            return "能力执行失败: " + e.getMessage();
        }
    }

    /**
     * 获取所有能力
     * @return 能力映射
     */
    public Map<String, Ability> getAllAbilities() {
        return abilities;
    }

    /**
     * 获取能力描述
     * @return 能力描述列表
     */
    public String getAbilitiesDescription() {
        StringBuilder sb = new StringBuilder();
        for (Ability ability : abilities.values()) {
            sb.append(ability.getName()).append(": ").append(ability.getDescription()).append("\n");
            sb.append("参数: " ).append(ability.getParamsDescription()).append("\n\n");
        }
        return sb.toString();
    }

    /**
     * 检查能力是否存在
     * @param name 能力名称
     * @return 是否存在
     */
    public boolean hasAbility(String name) {
        return abilities.containsKey(name);
    }
}
