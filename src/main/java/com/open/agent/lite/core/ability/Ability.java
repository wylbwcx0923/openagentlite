package com.open.agent.lite.core.ability;

import java.util.Map;

/**
 * 基础能力接口
 * 定义能力的基本方法
 */
public interface Ability {

    /**
     * 获取能力名称
     * @return 能力名称
     */
    String getName();

    /**
     * 获取能力描述
     * @return 能力描述
     */
    String getDescription();

    /**
     * 执行能力
     * @param params 能力参数
     * @return 执行结果
     */
    String execute(Map<String, String> params);

    /**
     * 获取能力参数描述
     * @return 参数描述
     */
    String getParamsDescription();

    /**
     * 检查能力是否可用
     * @return 是否可用
     */
    boolean isAvailable();
}
