package com.open.agent.lite.llm.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.open.agent.lite.llm.LlmClient;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 通义千问客户端
 * 实现LlmClient接口，用于调用通义千问API
 */
@Component
public class QwenClient implements LlmClient {

    private static final Logger logger = LoggerFactory.getLogger(QwenClient.class);
    private static final int MAX_RETRIES = 3;
    private static final int TIMEOUT_SECONDS = 60;
    private static final int BASE_DELAY_MS = 1000;

    // 静态OkHttpClient实例，避免每次调用都创建新实例
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    @Value("${llm.qwen.api-key:}")
    private String apiKey;

    @Value("${llm.qwen.api-url:}")
    private String apiUrl;

    @Value("${llm.qwen.model:}")
    private String model;

    @Value("${llm.qwen.temperature:0.7}")
    private double temperature;

    @Value("${llm.qwen.top-p:0.9}")
    private double topP;

    @Value("${llm.qwen.max-tokens:2048}")
    private int maxTokens;

    @Override
    public String modelType() {
        return "qwen";
    }

    /**
     * 验证配置
     */
    private void validateConfiguration() {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("请配置llm.qwen.api-key");
        }
        if (apiUrl == null || apiUrl.isEmpty()) {
            throw new RuntimeException("请配置llm.qwen.api-url");
        }
        if (model == null || model.isEmpty()) {
            throw new RuntimeException("请配置llm.qwen.model");
        }
    }

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        validateConfiguration();
        logger.info("调用通义千问API进行聊天，模型: {}", model);

        // 构建请求
        Request request = buildRequest(systemPrompt, userPrompt);

        // 执行请求，支持重试
        return executeWithRetry(request);
    }

    /**
     * 构建请求
     */
    private Request buildRequest(String systemPrompt, String userPrompt) {
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", temperature);
        requestBody.put("top_p", topP);
        requestBody.put("max_tokens", maxTokens);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "user", "content", userPrompt));
        requestBody.put("messages", messages);

        String jsonBody = JSON.toJSONString(requestBody);
        logger.debug("请求体: {}", jsonBody);

        // 构建请求
        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json; charset=utf-8")
        );

        return new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
    }

    /**
     * 执行请求并支持重试
     */
    private String executeWithRetry(Request request) {
        int retryCount = 0;
        while (true) {
            try {
                if (retryCount > 0) {
                    logger.info("第 {} 次尝试调用通义千问API", retryCount + 1);
                }
                return executeRequest(request);
            } catch (IOException e) {
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    long delayMs = calculateBackoffDelay(retryCount);
                    logger.warn("调用通义千问API失败，{}ms后重试 ({}/{}) 错误信息: {}", 
                               delayMs, retryCount, MAX_RETRIES, e.getMessage());
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("线程被中断", ie);
                    }
                } else {
                    logger.error("调用通义千问API失败，已达到最大重试次数", e);
                    throw new RuntimeException("调用通义千问API失败", e);
                }
            }
        }
    }

    /**
     * 计算退避延迟时间
     */
    private long calculateBackoffDelay(int retryCount) {
        // 指数退避策略，每次重试延迟翻倍
        return BASE_DELAY_MS * (1L << (retryCount - 1));
    }

    /**
     * 执行请求并解析响应
     */
    private String executeRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorMessage = buildErrorMessage(response);
                logger.error("通义千问API调用失败: {}", errorMessage);
                throw new IOException(errorMessage);
            }
            
            String responseBody = getResponseBody(response);
            logger.info("通义千问API响应: {}", responseBody);
            return parseResponse(responseBody);
        }
    }

    /**
     * 构建错误消息
     */
    private String buildErrorMessage(Response response) throws IOException {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("状态码: ").append(response.code());
        
        if (response.body() != null) {
            String errorBody = response.body().string();
            errorMessage.append(", 错误响应: ").append(errorBody);
        }
        
        return errorMessage.toString();
    }

    /**
     * 获取响应体
     */
    private String getResponseBody(Response response) throws IOException {
        if (response.body() == null) {
            throw new IOException("API响应为空");
        }
        return response.body().string();
    }

    /**
     * 解析API响应
     */
    private String parseResponse(String response) {
        try {
            JSONObject jsonObject = JSON.parseObject(response);
            
            // 检查是否有错误
            if (jsonObject.containsKey("error")) {
                handleApiError(jsonObject);
            }
            
            // 提取AI回答
            return extractAnswer(jsonObject);
        } catch (Exception e) {
            logger.error("解析API响应失败: {}", e.getMessage(), e);
            throw new RuntimeException("解析API响应失败", e);
        }
    }

    /**
     * 处理API错误
     */
    private void handleApiError(JSONObject jsonObject) {
        JSONObject error = jsonObject.getJSONObject("error");
        String errorMessage = error.getString("message");
        String errorType = error.getString("type");
        logger.error("API返回错误: {} - {}", errorType, errorMessage);
        throw new RuntimeException("API返回错误: " + errorMessage);
    }

    /**
     * 提取AI回答
     */
    private String extractAnswer(JSONObject jsonObject) {
        if (!jsonObject.containsKey("choices")) {
            logger.error("响应中缺少choices字段: {}", jsonObject);
            throw new RuntimeException("响应格式错误，缺少choices字段");
        }
        
        JSONArray choices = jsonObject.getJSONArray("choices");
        if (choices.isEmpty()) {
            logger.error("choices数组为空: {}", jsonObject);
            throw new RuntimeException("响应格式错误，choices数组为空");
        }
        
        JSONObject choice = choices.getJSONObject(0);
        if (!choice.containsKey("message")) {
            logger.error("choice中缺少message字段: {}", choice);
            throw new RuntimeException("响应格式错误，缺少message字段");
        }
        
        JSONObject message = choice.getJSONObject("message");
        if (!message.containsKey("content")) {
            logger.error("message中缺少content字段: {}", message);
            throw new RuntimeException("响应格式错误，缺少content字段");
        }
        
        return message.getString("content");
    }
}
