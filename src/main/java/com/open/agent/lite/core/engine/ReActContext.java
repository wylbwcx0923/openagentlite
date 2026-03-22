package com.open.agent.lite.core.engine;

import com.open.agent.lite.skill.Skill;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ReAct上下文
 * 管理ReAct流程的上下文信息
 */
@Getter
public class ReActContext {

    /**
     * 用户输入
     * -- GETTER --
     *  获取用户输入
     *
     * @return 用户输入

     */
    private String userInput;
    
    /**
     * 对话历史
     * -- GETTER --
     *  获取对话历史
     *
     * @return 对话历史

     */
    private final List<String> conversationHistory = new ArrayList<>();
    
    /**
     * 当前技能
     * -- GETTER --
     *  获取当前技能
     *
     *
     * -- SETTER --
     *  设置当前技能
     *
     @return 当前技能
      * @param currentSkill 当前技能

     */
    @Setter
    private Skill currentSkill;
    
    /**
     * 技能参数
     * -- GETTER --
     *  获取技能参数
     *
     * @return 技能参数

     */
    private Map<String, String> skillParams;
    
    /**
     * 工具结果
     * -- GETTER --
     *  获取工具结果
     *
     * @return 工具结果

     */
    private String toolResult;
    
    /**
     * 最终回答
     * -- GETTER --
     *  获取最终回答
     *
     * @return 最终回答

     */
    private String answer;
    
    /**
     * 是否完成
     * -- SETTER --
     *  设置是否完成
     *
     *
     * -- GETTER --
     *  检查是否完成
     *
     @param finished 是否完成
      * @return 是否完成

     */
    @Setter
    private boolean finished = false;
    
    /**
     * 步骤计数器
     * -- GETTER --
     *  获取步骤计数器
     *
     * @return 步骤计数器

     */
    private int stepCount = 0;

    /**
     * 设置用户输入
     * @param userInput 用户输入
     */
    public void setUserInput(String userInput) {
        this.userInput = userInput;
        addToHistory("User: " + userInput);
    }

    /**
     * 添加到对话历史
     * @param entry 历史条目
     */
    public void addToHistory(String entry) {
        conversationHistory.add(entry);
    }

    /**
     * 设置工具结果
     * @param toolResult 工具结果
     */
    public void setToolResult(String toolResult) {
        this.toolResult = toolResult;
        addToHistory("Tool: " + toolResult);
    }

    /**
     * 设置最终回答
     * @param answer 最终回答
     */
    public void setAnswer(String answer) {
        this.answer = answer;
        addToHistory("Assistant: " + answer);
    }

    /**
     * 增加步骤计数器
     */
    public void incrementStepCount() {
        stepCount++;
    }

    /**
     * 获取上下文摘要
     * @return 上下文摘要
     */
    public String getContextSummary() {
        StringBuilder summary = new StringBuilder();
        for (String entry : conversationHistory) {
            summary.append(entry).append("\n");
        }
        return summary.toString();
    }

    /**
     * 获取最近的LLM响应
     * @return 最近的LLM响应
     */
    public String getLastResponse() {
        if (conversationHistory.isEmpty()) {
            return null;
        }
        // 查找最近的Assistant响应
        for (int i = conversationHistory.size() - 1; i >= 0; i--) {
            String entry = conversationHistory.get(i);
            if (entry.startsWith("Assistant: ")) {
                // 移除"Assistant: "前缀，返回原始响应
                return entry.substring(11);
            }
        }
        return null;
    }

    /**
     * 设置技能参数
     * @param skillParams 技能参数
     */
    public void setSkillParams(Map<String, String> skillParams) {
        this.skillParams = skillParams;
    }
}