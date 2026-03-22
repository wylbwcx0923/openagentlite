package com.open.agent.lite.core.memory.impl;

import com.open.agent.lite.core.memory.Memory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 短期记忆
 * 使用内存存储，适合存储最近的对话历史
 */
@Component
public class ShortTermMemory implements Memory {

    private static final Logger logger = LoggerFactory.getLogger(ShortTermMemory.class);
    private final List<String> memories = new ArrayList<>();

    @Override
    public void store(String content) {
        synchronized (memories) {
            // 如果超过最大大小，移除最早的记忆
            // 最大记忆数量
            int maxSize = 100;
            if (memories.size() >= maxSize) {
                memories.remove(0);
            }
            memories.add(content);
            logger.debug("已存储短期记忆: {}", content);
        }
    }

    @Override
    public List<String> retrieve(String query) {
        synchronized (memories) {
            // 简单的关键词匹配
            List<String> results = memories.stream()
                    .filter(memory -> memory.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            logger.debug("已检索到 {} 条短期记忆，查询内容: {}", results.size(), query);
            return results;
        }
    }

    @Override
    public List<String> getAll() {
        synchronized (memories) {
            return new ArrayList<>(memories);
        }
    }

    @Override
    public void clear() {
        synchronized (memories) {
            memories.clear();
            logger.info("已清空短期记忆");
        }
    }

    @Override
    public int size() {
        synchronized (memories) {
            return memories.size();
        }
    }

    /**
     * 获取最近的记忆
     * @param count 记忆数量
     * @return 最近的记忆
     */
    public List<String> getRecent(int count) {
        synchronized (memories) {
            int start = Math.max(0, memories.size() - count);
            return memories.subList(start, memories.size());
        }
    }

    /**
     * 压缩记忆，保留最重要的信息
     * @return 压缩后的记忆
     */
    public String compress() {
        synchronized (memories) {
            // 简单的压缩策略：保留最近的10条记忆
            List<String> recent = getRecent(10);
            StringBuilder compressed = new StringBuilder();
            for (String memory : recent) {
                compressed.append(memory).append("\n");
            }
            return compressed.toString();
        }
    }
}