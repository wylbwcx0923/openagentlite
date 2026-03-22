package com.open.agent.lite.core.task;

import com.open.agent.lite.core.engine.ReActEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 任务管理器
 */
@Component
public class TaskManager {

    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);

    private final ReActEngine reActEngine;
    private final Map<String, Task> tasks = new HashMap<>();

    public TaskManager(ReActEngine reActEngine) {
        this.reActEngine = reActEngine;
        logger.info("TaskManager initialized");
    }

    /**
     * 创建任务
     * @param prompt 用户提示词
     * @return 任务ID
     */
    public String createTask(String prompt) {
        String taskId = UUID.randomUUID().toString();
        Task task = new Task(taskId, prompt);
        tasks.put(taskId, task);
        logger.info("创建任务成功，任务ID: {}, 提示词: {}", taskId, prompt);
        return taskId;
    }

    /**
     * 执行任务
     * @param taskId 任务ID
     * @return 执行结果
     */
    public String executeTask(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            logger.warn("执行任务失败，任务不存在: {}", taskId);
            return "任务不存在";
        }

        logger.info("开始执行任务: {}", taskId);
        task.setStatus(Task.TaskStatus.RUNNING);
        try {
            String result = reActEngine.run(task.getPrompt());
            task.setResult(result);
            task.setStatus(Task.TaskStatus.COMPLETED);
            logger.info("任务执行完成: {}", taskId);
            return result;
        } catch (Exception e) {
            String errorMessage = "执行失败: " + e.getMessage();
            task.setResult(errorMessage);
            task.setStatus(Task.TaskStatus.FAILED);
            logger.error("任务执行失败: {}, 错误: {}", taskId, e.getMessage(), e);
            return errorMessage;
        }
    }

    /**
     * 获取任务状态
     * @param taskId 任务ID
     * @return 任务状态
     */
    public Task getTask(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            logger.debug("获取任务失败，任务不存在: {}", taskId);
        } else {
            logger.debug("获取任务成功: {}, 状态: {}", taskId, task.getStatus());
        }
        return task;
    }

    /**
     * 获取所有任务
     * @return 任务映射
     */
    public Map<String, Task> getAllTasks() {
        logger.debug("获取所有任务，共 {} 个", tasks.size());
        return tasks;
    }

    /**
     * 删除任务
     * @param taskId 任务ID
     * @return 是否删除成功
     */
    public boolean deleteTask(String taskId) {
        boolean success = tasks.remove(taskId) != null;
        if (success) {
            logger.info("删除任务成功: {}", taskId);
        } else {
            logger.warn("删除任务失败，任务不存在: {}", taskId);
        }
        return success;
    }
}