package com.open.agent.lite.core.engine;

import com.open.agent.lite.core.engine.handler.*;
import com.open.agent.lite.core.engine.parser.ResponseParser;
import com.open.agent.lite.core.prompt.PromptManager;
import com.open.agent.lite.llm.LlmClientFactory;
import com.open.agent.lite.mcp.tool.ToolManager;
import com.open.agent.lite.skill.SkillManager;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ReAct核心调度引擎
 * 控制 AI 思考 → 选技能 → 调用工具 → 回答 的整个流程
 */
@Component
public class ReActEngine {

    private static final Logger logger = LoggerFactory.getLogger(ReActEngine.class);

    // 注入LLM客户端工厂
    @Resource
    private LlmClientFactory llmClientFactory;

    // 注入技能管理器
    @Resource
    private SkillManager skillManager;

    // 注入工具管理器
    @Resource
    private ToolManager toolManager;

    // 注入提示词管理器
    @Resource
    private PromptManager promptManager;

    // 注入处理器
    @Resource
    private ResponseParser responseParser;

    @Resource
    private MemoryHandler memoryHandler;

    @Resource
    private SkillSelector skillSelector;

    @Resource
    private ToolCaller toolCaller;

    @Resource
    private AnswerGenerator answerGenerator;

    @Resource
    private AbilityOperator abilityOperator;

    @Value("${engine.react.max-steps:10}")
    private int maxSteps;

    @Value("${engine.react.timeout:300}")
    private int timeout;

    /**
     * 入口：用户发一句话，引擎跑完整流程
     *
     * @param userInput 用户输入
     * @return 最终回答
     */
    public String run(String userInput) {
        logger.info("开始执行任务，用户提示: {}", userInput);

        // 1. 创建上下文
        ReActContext ctx = new ReActContext();
        ctx.setUserInput(userInput);

        // 2. 检索相关记忆
        String memory = memoryHandler.getRelevantMemory(userInput);
        if (!memory.isEmpty()) {
            ctx.addToHistory("Memory: " + memory);
            logger.debug("已检索到相关记忆");
        }

        long startTime = System.currentTimeMillis();

        // 3. 开始 ReAct 循环（可以多次轮回思考、调工具）
        ReActStep step;
        do {
            // 检查超时
            if (System.currentTimeMillis() - startTime > timeout * 1000L) {
                logger.warn("任务执行超时，已达到时间限制");
                return "任务执行超时，已达到时间限制";
            }

            // 检查最大步骤
            if (ctx.getStepCount() >= maxSteps) {
                logger.warn("任务执行超时，已达到最大步数限制");
                return "任务执行超时，已达到最大步数限制";
            }

            ctx.incrementStepCount();
            logger.debug("执行步骤: {}/{}", ctx.getStepCount(), maxSteps);

            // 4. LLM 思考，判断下一步做什么
            step = thinkNextStep(ctx);
            logger.debug("下一步操作: {}", step);

            // 5. 根据步骤执行
            switch (step) {
                case SELECT_SKILL -> skillSelector.selectSkill(ctx);
                case CALL_TOOL -> toolCaller.callTool(ctx);
                case ANSWER -> answerGenerator.generateAnswer(ctx);
                case OPERATE -> abilityOperator.operate(ctx);
                case END -> ctx.setFinished(true);
            }
        } while (!ctx.isFinished());

        // 6. 存储对话到记忆
        memoryHandler.storeConversation(ctx);

        // 7. 返回最终回答
        logger.info("任务执行完成，最终答案: {}", ctx.getAnswer());
        return ctx.getAnswer();
    }

    /**
     * 让 LLM 判断下一步该干嘛
     *
     * @param ctx 上下文
     * @return 下一步操作
     */
    private ReActStep thinkNextStep(ReActContext ctx) {
        try {
            // 构建系统提示词
            String toolsDescription = toolManager.getToolsDescription();
            String skillsDescription = skillManager.getSkillsBasicInfo();
            String systemPrompt = promptManager.loadSystemPrompt(toolsDescription, skillsDescription);

            // 构建思考提示词
            String context = ctx.getContextSummary();
            String thinkPrompt = promptManager.loadThinkPrompt(context);

            // 调用LLM
            String response = llmClientFactory.getDefaultClient().chat(systemPrompt, thinkPrompt);
            ctx.addToHistory("Assistant: " + response);

            // 解析响应，确定下一步操作
            return responseParser.parseNextStep(response);
        } catch (Exception e) {
            logger.error("思考过程中发生错误: {}", e.getMessage(), e);
            return ReActStep.ANSWER;
        }
    }
}
