package com.open.agent.lite.core.engine.handler;

import com.open.agent.lite.core.engine.ReActContext;
import com.open.agent.lite.core.engine.parser.ResponseParser;
import com.open.agent.lite.core.prompt.PromptManager;
import com.open.agent.lite.llm.LlmClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 回答生成器
 * 负责生成最终回答，包括从LLM响应中获取答案和使用传统方式生成答案
 */
public class AnswerGenerator {

    private static final Logger logger = LoggerFactory.getLogger(AnswerGenerator.class);
    private final LlmClientFactory llmClientFactory;
    private final PromptManager promptManager;
    private final ResponseParser responseParser;

    public AnswerGenerator(LlmClientFactory llmClientFactory, PromptManager promptManager, ResponseParser responseParser) {
        this.llmClientFactory = llmClientFactory;
        this.promptManager = promptManager;
        this.responseParser = responseParser;
    }

    /**
     * 生成最终回答
     * @param ctx 上下文
     */
    public void generateAnswer(ReActContext ctx) {
        try {
            // 获取最近的LLM响应
            String lastResponse = ctx.getLastResponse();
            if (lastResponse != null) {
                // 从响应中获取答案
                String answer = responseParser.getAnswer(lastResponse);
                if (answer != null && !answer.isEmpty()) {
                    ctx.setAnswer(answer);
                    ctx.setFinished(true);
                    logger.info("从LLM响应中获取最终回答");
                    return;
                }
            }

            // 如果响应中没有答案，使用传统方式生成
            // 构建回答提示词
            String context = ctx.getContextSummary();
            String answerPrompt = promptManager.loadAnswerPrompt(context);

            // 调用LLM生成回答
            String answer = llmClientFactory.getDefaultClient().chat("", answerPrompt);
            ctx.setAnswer(answer);
            ctx.setFinished(true);
            logger.info("生成最终回答");
        } catch (Exception e) {
            logger.error("生成回答时发生错误: {}", e.getMessage(), e);
            ctx.setAnswer("生成回答失败: " + e.getMessage());
            ctx.setFinished(true);
        }
    }
}