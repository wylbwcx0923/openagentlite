package com.open.agent.lite.core.memory;

import java.util.List;

/**
 * 记忆接口
 * 定义记忆的基本方法
 */
public interface Memory {

    /**
     * 存储记忆
     * @param content 记忆内容
     */
    void store(String content);

    /**
     * 检索记忆
     * @param query 查询内容
     * @return 相关记忆
     */
    List<String> retrieve(String query);

    /**
     * 获取所有记忆
     * @return 所有记忆
     */
    List<String> getAll();

    /**
     * 清空记忆
     */
    void clear();

    /**
     * 获取记忆大小
     * @return 记忆大小
     */
    int size();
}