package com.open.agent.lite.core.memory.impl;

import com.open.agent.lite.core.memory.Memory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 长期记忆
 * 使用文件存储，适合存储长期的知识和经验
 */
@Component
public class LongTermMemory implements Memory {

    private static final Logger logger = LoggerFactory.getLogger(LongTermMemory.class);
    private final List<String> memories = new ArrayList<>();
    private final String storageFile = "long-term-memory.txt";

    /**
     * 构造函数
     */
    public LongTermMemory() {
        loadFromFile();
        logger.info("长期记忆初始化完成，包含 {} 条记忆", memories.size());
    }

    @Override
    public void store(String content) {
        synchronized (memories) {
            memories.add(content);
            saveToFile();
            logger.debug("已存储长期记忆: {}", content);
        }
    }

    @Override
    public List<String> retrieve(String query) {
        synchronized (memories) {
            // 简单的关键词匹配
            List<String> results = memories.stream()
                    .filter(memory -> memory.toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            logger.debug("已检索到 {} 条长期记忆，查询内容: {}", results.size(), query);
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
            saveToFile();
            logger.info("已清空长期记忆");
        }
    }

    @Override
    public int size() {
        synchronized (memories) {
            return memories.size();
        }
    }

    /**
     * 从文件加载记忆
     */
    private void loadFromFile() {
        File file = new File(storageFile);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    memories.add(line);
                }
            } catch (IOException e) {
                logger.error("从文件加载长期记忆失败: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 保存记忆到文件
     */
    private void saveToFile() {
        File file = new File(storageFile);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String memory : memories) {
                writer.write(memory);
                writer.newLine();
            }
        } catch (IOException e) {
            logger.error("保存长期记忆到文件失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 概括记忆，提取关键信息
     * @return 概括后的记忆
     */
    public String summarize() {
        synchronized (memories) {
            // 简单的概括策略：返回记忆数量和部分内容
            StringBuilder summary = new StringBuilder();
            summary.append("长期记忆包含 ").append(memories.size()).append(" 条记录。\n");
            if (!memories.isEmpty()) {
                summary.append("最近的记录：\n");
                int count = Math.min(5, memories.size());
                List<String> recent = memories.subList(Math.max(0, memories.size() - count), memories.size());
                for (String memory : recent) {
                    summary.append("- ").append(memory).append("\n");
                }
            }
            return summary.toString();
        }
    }
}