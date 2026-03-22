package com.open.agent.lite.llm;

import com.open.agent.lite.llm.impl.QwenClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * LLM客户端工厂
 * 负责根据策略选择不同的LLM模型
 */
@Component
public class LlmClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(LlmClientFactory.class);
    private final Map<String, LlmClient> clients = new HashMap<>();
    private final String defaultModel;

    /**
     * 构造函数
     * @param qwenClient 通义千问客户端
     */
    public LlmClientFactory(QwenClient qwenClient) {
        // 注册默认的LLM客户端
        clients.put(qwenClient.modelType(), qwenClient);
        this.defaultModel = qwenClient.modelType();
        logger.info("LlmClientFactory initialized, default model: {}", defaultModel);
    }

    /**
     * 注册LLM客户端
     * @param client LLM客户端
     */
    public void registerClient(LlmClient client) {
        clients.put(client.modelType(), client);
        logger.info("Registered LLM client: {}", client.modelType());
    }

    /**
     * 获取LLM客户端
     * @param modelType 模型类型
     * @return LLM客户端
     */
    public LlmClient getClient(String modelType) {
        LlmClient client = clients.get(modelType);
        if (client == null) {
            logger.warn("LLM client not found for model type: {}, using default: {}", modelType, defaultModel);
            client = clients.get(defaultModel);
        }
        return client;
    }

    /**
     * 获取默认LLM客户端
     * @return 默认LLM客户端
     */
    public LlmClient getDefaultClient() {
        return clients.get(defaultModel);
    }

    /**
     * 获取所有可用的模型类型
     * @return 模型类型列表
     */
    public String[] getAvailableModels() {
        return clients.keySet().toArray(new String[0]);
    }

    /**
     * 检查模型是否可用
     * @param modelType 模型类型
     * @return 是否可用
     */
    public boolean isModelAvailable(String modelType) {
        return clients.containsKey(modelType);
    }
}