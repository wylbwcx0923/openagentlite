package com.open.agent.lite.core.engine;

/**
 * ReAct步骤枚举
 * 定义ReAct流程的各个步骤
 */
public enum ReActStep {
    /**
     * 选择技能
     */
    SELECT_SKILL,
    
    /**
     * 调用工具
     */
    CALL_TOOL,
    
    /**
     * 生成回答
     */
    ANSWER,

    /**
     * 操作
     */
    OPERATE,
    /**
     * 结束流程
     */
    END
}