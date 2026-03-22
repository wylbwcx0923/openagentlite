package com.open.agent.lite.core.task;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 任务模型
 */
@Getter
public class Task {

    private String id;
    private String prompt;
    private String result;
    private TaskStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Task(String id, String prompt) {
        this.id = id;
        this.prompt = prompt;
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public enum TaskStatus {
        PENDING, RUNNING, COMPLETED, FAILED
    }
}