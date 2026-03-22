package com.open.agent.lite.core.ability.impl;

import com.open.agent.lite.core.ability.AbstractAbility;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 网络能力
 * 提供网络请求和搜索功能
 */
@Component
public class NetworkAbility extends AbstractAbility {

    private final OkHttpClient client = new OkHttpClient();

    public NetworkAbility() {
        super("NetworkAbility", "网络请求能力，支持网络请求和搜索功能");
    }

    @Override
    protected String doExecute(Map<String, String> params) {
        String operation = params.get("operation");
        String url = params.get("url");
        String query = params.get("query");

        if (operation == null) {
            return "参数错误：缺少operation参数";
        }

        try {
            return switch (operation) {
                case "search" -> {
                    if (query == null || query.isEmpty()) {
                        yield "搜索操作缺少query参数";
                    }
                    yield search(query);
                }
                case "request" -> {
                    if (url == null || url.isEmpty()) {
                        yield "请求操作缺少url参数";
                    }
                    yield sendRequest(url);
                }
                default -> "不支持的操作：" + operation;
            };
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }

    private String search(String query) throws IOException {
        // 这里使用一个简单的搜索API示例
        // 实际使用时需要替换为真实的搜索API
        String url = "https://api.example.com/search?q=" + query;
        return sendRequest(url);
    }

    private String sendRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                return "请求失败: " + response.message();
            }
        }
    }

    @Override
    public String getParamsDescription() {
        return "operation: 操作类型(search/request), url: 请求地址(仅request操作需要), query: 搜索查询(仅search操作需要)";
    }

    @Override
    protected boolean checkAvailability() {
        return true;
    }
}
