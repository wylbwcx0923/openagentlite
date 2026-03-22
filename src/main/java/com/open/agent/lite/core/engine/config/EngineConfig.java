package com.open.agent.lite.core.engine.config;

import com.open.agent.lite.core.ability.AbilityManager;
import com.open.agent.lite.core.engine.handler.*;
import com.open.agent.lite.core.engine.parser.ResponseParser;
import com.open.agent.lite.core.memory.MemoryManager;
import com.open.agent.lite.core.prompt.PromptManager;
import com.open.agent.lite.llm.LlmClientFactory;
import com.open.agent.lite.mcp.McpToolDispatcher;
import com.open.agent.lite.skill.SkillManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 引擎配置类
 * 负责注册各个处理器为Spring Bean
 */
@Configuration
public class EngineConfig {

    @Bean
    public ResponseParser responseParser() {
        return new ResponseParser();
    }

    @Bean
    public MemoryHandler memoryHandler(MemoryManager memoryManager) {
        return new MemoryHandler(memoryManager);
    }

    @Bean
    public SkillSelector skillSelector(SkillManager skillManager, ResponseParser responseParser) {
        return new SkillSelector(skillManager, responseParser);
    }

    @Bean
    public ToolCaller toolCaller(McpToolDispatcher toolDispatcher, ResponseParser responseParser) {
        return new ToolCaller(toolDispatcher, responseParser);
    }

    @Bean
    public AnswerGenerator answerGenerator(LlmClientFactory llmClientFactory, PromptManager promptManager, ResponseParser responseParser) {
        return new AnswerGenerator(llmClientFactory, promptManager, responseParser);
    }

    @Bean
    public AbilityOperator abilityOperator(AbilityManager abilityManager, ResponseParser responseParser) {
        return new AbilityOperator(abilityManager, responseParser);
    }
}