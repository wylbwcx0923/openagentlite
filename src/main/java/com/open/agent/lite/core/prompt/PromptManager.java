package com.open.agent.lite.core.prompt;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * 提示词管理器
 * 负责加载和管理提示词模板
 */
@Component
public class PromptManager {

    private static final Logger logger = LoggerFactory.getLogger(PromptManager.class);
    private final Configuration configuration;

    /**
     * 构造函数
     */
    public PromptManager() {
        configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setClassForTemplateLoading(getClass(), "/prompts");
        configuration.setDefaultEncoding("UTF-8");
        logger.info("PromptManager initialized");
    }

    /**
     * 加载提示词模板
     * @param templateName 模板名称
     * @param data 模板数据
     * @return 渲染后的提示词
     */
    public String loadPrompt(String templateName, Map<String, Object> data) {
        try {
            Template template = configuration.getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(data, writer);
            String prompt = writer.toString();
            logger.debug("Loaded prompt from template: {}", templateName);
            return prompt;
        } catch (IOException | TemplateException e) {
            logger.error("Failed to load prompt template: {}", templateName, e);
            return "";
        }
    }

    /**
     * 加载系统提示词
     * @param tools 工具描述
     * @param skills 技能描述
     * @return 系统提示词
     */
    public String loadSystemPrompt(String tools, String skills) {
        Map<String, Object> data = Map.of(
                "tools", tools,
                "skills", skills
        );
        return loadPrompt("system-prompt.txt", data);
    }

    /**
     * 加载思考提示词
     * @param context 上下文信息
     * @return 思考提示词
     */
    public String loadThinkPrompt(String context) {
        Map<String, Object> data = Map.of(
                "context", context
        );
        return loadPrompt("think-prompt.txt", data);
    }

    /**
     * 加载回答提示词
     * @param context 上下文信息
     * @return 回答提示词
     */
    public String loadAnswerPrompt(String context) {
        Map<String, Object> data = Map.of(
                "context", context
        );
        return loadPrompt("answer-prompt.txt", data);
    }
}