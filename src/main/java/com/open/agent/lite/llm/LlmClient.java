package com.open.agent.lite.llm;

/**
 * 大模型客户端的抽象接口
 */
public interface LlmClient {

    /**
     * 获取模型类型
     * @return 模型类型
     */
    String modelType();

    /**
     * 跟AI进行聊天
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return AI的回答
     */
    String chat(String systemPrompt, String userPrompt);

}
