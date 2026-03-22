package com.open.agent.lite.core.engine.handler;

import com.open.agent.lite.core.engine.ReActContext;
import com.open.agent.lite.core.memory.MemoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 记忆处理器
 * 负责处理记忆相关操作，包括获取相关记忆和存储对话
 */
public class MemoryHandler {

    private static final Logger logger = LoggerFactory.getLogger(MemoryHandler.class);
    private final MemoryManager memoryManager;

    public MemoryHandler(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
    }

    /**
     * 获取相关记忆
     * @param query 查询内容
     * @return 相关记忆
     */
    public String getRelevantMemory(String query) {
        try {
            // 检索相关记忆
            List<String> memories = memoryManager.retrieve(query);
            if (!memories.isEmpty()) {
                // 合并记忆
                StringBuilder memory = new StringBuilder();
                for (String m : memories) {
                    memory.append(m).append("\n");
                }
                return memory.toString();
            }
        } catch (Exception e) {
            logger.error("获取记忆失败: {}", e.getMessage(), e);
        }
        return "";
    }

    /**
     * 存储对话到记忆
     * @param ctx 上下文
     */
    public void storeConversation(ReActContext ctx) {
        try {
            // 存储对话历史到短期记忆
            String conversation = ctx.getContextSummary();
            memoryManager.storeShortTerm(conversation);
            logger.debug("已将对话存储到短期记忆");

            // 如果对话包含重要信息，可以存储到长期记忆
            // 这里简化处理，只存储最终回答
            if (ctx.getAnswer() != null && !ctx.getAnswer().isEmpty()) {
                memoryManager.storeLongTerm("Q: " + ctx.getUserInput() + " A: " + ctx.getAnswer());
                logger.debug("已将关键信息存储到长期记忆");
            }
        } catch (Exception e) {
            logger.error("存储对话失败: {}", e.getMessage(), e);
        }
    }
}