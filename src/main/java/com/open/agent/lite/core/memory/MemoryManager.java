package com.open.agent.lite.core.memory;

import com.open.agent.lite.core.memory.impl.LongTermMemory;
import com.open.agent.lite.core.memory.impl.ShortTermMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 记忆管理器
 * 负责管理短期记忆和长期记忆
 */
@Component
public class MemoryManager {

    private static final Logger logger = LoggerFactory.getLogger(MemoryManager.class);
    private final ShortTermMemory shortTermMemory;
    private final LongTermMemory longTermMemory;

    /**
     * 构造函数
     * @param shortTermMemory 短期记忆
     * @param longTermMemory 长期记忆
     */
    public MemoryManager(ShortTermMemory shortTermMemory, LongTermMemory longTermMemory) {
        this.shortTermMemory = shortTermMemory;
        this.longTermMemory = longTermMemory;
        logger.info("记忆管理器初始化完成");
    }

    /**
     * 存储记忆
     * @param content 记忆内容
     * @param isLongTerm 是否长期记忆
     */
    public void store(String content, boolean isLongTerm) {
        if (isLongTerm) {
            longTermMemory.store(content);
        } else {
            shortTermMemory.store(content);
        }
    }

    /**
     * 存储短期记忆
     * @param content 记忆内容
     */
    public void storeShortTerm(String content) {
        shortTermMemory.store(content);
    }

    /**
     * 存储长期记忆
     * @param content 记忆内容
     */
    public void storeLongTerm(String content) {
        longTermMemory.store(content);
    }

    /**
     * 检索记忆
     * @param query 查询内容
     * @return 相关记忆
     */
    public List<String> retrieve(String query) {
        List<String> results = shortTermMemory.retrieve(query);
        results.addAll(longTermMemory.retrieve(query));
        return results;
    }

    /**
     * 获取短期记忆摘要
     * @return 短期记忆摘要
     */
    public String getShortTermSummary() {
        return shortTermMemory.compress();
    }

    /**
     * 获取长期记忆摘要
     * @return 长期记忆摘要
     */
    public String getLongTermSummary() {
        return longTermMemory.summarize();
    }

    /**
     * 获取所有记忆
     * @return 所有记忆
     */
    public List<String> getAllMemories() {
        List<String> all = shortTermMemory.getAll();
        all.addAll(longTermMemory.getAll());
        return all;
    }

    /**
     * 清空记忆
     * @param clearLongTerm 是否清空长期记忆
     */
    public void clear(boolean clearLongTerm) {
        shortTermMemory.clear();
        if (clearLongTerm) {
            longTermMemory.clear();
        }
    }

    /**
     * 获取记忆大小
     * @return 记忆大小
     */
    public int getSize() {
        return shortTermMemory.size() + longTermMemory.size();
    }

    /**
     * 压缩记忆，优化存储
     */
    public void compress() {
        // 压缩短期记忆
        shortTermMemory.compress();
        logger.info("记忆压缩完成");
    }
}