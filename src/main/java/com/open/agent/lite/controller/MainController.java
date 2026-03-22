package com.open.agent.lite.controller;

import com.open.agent.lite.core.task.Task;
import com.open.agent.lite.core.task.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private final TaskManager taskManager;

    public MainController(TaskManager taskManager) {
        this.taskManager = taskManager;
        logger.info("MainController initialized");
    }

    @GetMapping
    public String home(){
        logger.debug("访问首页");
        return "OpenClaw API";
    }

    /**
     * 提交任务
     * @param request 包含prompt的请求体
     * @return 任务ID
     */
    @PostMapping("/tasks")
    public Map<String, String> createTask(@RequestBody Map<String, String> request) {
        logger.info("接收创建任务请求: {}", request);
        String prompt = request.get("prompt");
        if (prompt == null || prompt.isEmpty()) {
            logger.warn("创建任务失败，缺少prompt参数");
            return Map.of("error", "请提供prompt参数");
        }
        try {
            String taskId = taskManager.createTask(prompt);
            logger.info("创建任务成功，任务ID: {}", taskId);
            return Map.of("taskId", taskId);
        } catch (Exception e) {
            logger.error("创建任务失败: {}", e.getMessage(), e);
            return Map.of("error", "创建任务失败: " + e.getMessage());
        }
    }

    /**
     * 执行任务
     * @param taskId 任务ID
     * @return 执行结果
     */
    @PostMapping("/tasks/{taskId}/execute")
    public Map<String, String> executeTask(@PathVariable String taskId) {
        logger.info("接收执行任务请求，任务ID: {}", taskId);
        try {
            String result = taskManager.executeTask(taskId);
            logger.info("执行任务完成，任务ID: {}", taskId);
            return Map.of("result", result);
        } catch (Exception e) {
            logger.error("执行任务失败: {}", e.getMessage(), e);
            return Map.of("error", "执行任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务状态
     * @param taskId 任务ID
     * @return 任务信息
     */
    @GetMapping("/tasks/{taskId}")
    public Map<String, Object> getTask(@PathVariable String taskId) {
        logger.info("接收获取任务请求，任务ID: {}", taskId);
        try {
            Task task = taskManager.getTask(taskId);
            if (task == null) {
                logger.warn("获取任务失败，任务不存在: {}", taskId);
                return Map.of("error", "任务不存在");
            }
            logger.info("获取任务成功，任务ID: {}, 状态: {}", taskId, task.getStatus());
            return Map.of(
                    "id", task.getId(),
                    "prompt", task.getPrompt(),
                    "result", task.getResult(),
                    "status", task.getStatus(),
                    "createdAt", task.getCreatedAt(),
                    "updatedAt", task.getUpdatedAt()
            );
        } catch (Exception e) {
            logger.error("获取任务失败: {}", e.getMessage(), e);
            return Map.of("error", "获取任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务历史
     * @return 所有任务
     */
    @GetMapping("/tasks")
    public Map<String, Object> getTasks() {
        logger.info("接收获取任务列表请求");
        try {
            Map<String, Task> tasks = taskManager.getAllTasks();
            logger.info("获取任务列表成功，共 {} 个任务", tasks.size());
            return Map.of("tasks", tasks);
        } catch (Exception e) {
            logger.error("获取任务列表失败: {}", e.getMessage(), e);
            return Map.of("error", "获取任务列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除任务
     * @param taskId 任务ID
     * @return 删除结果
     */
    @DeleteMapping("/tasks/{taskId}")
    public Map<String, Boolean> deleteTask(@PathVariable String taskId) {
        logger.info("接收删除任务请求，任务ID: {}", taskId);
        try {
            boolean success = taskManager.deleteTask(taskId);
            logger.info("删除任务结果，任务ID: {}, 成功: {}", taskId, success);
            return Map.of("success", success);
        } catch (Exception e) {
            logger.error("删除任务失败: {}", e.getMessage(), e);
            return Map.of("error", false);
        }
    }

}
