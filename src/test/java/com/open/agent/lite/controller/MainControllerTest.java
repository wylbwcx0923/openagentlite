package com.open.agent.lite.controller;

import com.open.agent.lite.core.task.Task;
import com.open.agent.lite.core.task.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MainControllerTest {

    @Mock
    private TaskManager taskManager;
    
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        MainController mainController = new MainController(taskManager);
        mockMvc = MockMvcBuilders.standaloneSetup(mainController).build();
    }

    @Test
    void testHome() throws Exception {
        mockMvc.perform(get("/api"))
                .andExpect(status().isOk())
                .andExpect(content().string("OpenClaw API"));
    }

    @Test
    void testCreateTask() throws Exception {
        String taskId = "test-task-id";
        when(taskManager.createTask("test prompt")).thenReturn(taskId);
        
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"prompt\": \"test prompt\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(taskId));
    }

    @Test
    void testCreateTaskWithoutPrompt() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("请提供prompt参数"));
    }

    @Test
    void testExecuteTask() throws Exception {
        String taskId = "test-task-id";
        String result = "test result";
        when(taskManager.executeTask(taskId)).thenReturn(result);
        
        mockMvc.perform(post("/api/tasks/{taskId}/execute", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(result));
    }

    @Test
    void testGetTask() throws Exception {
        String taskId = "test-task-id";
        Task task = new Task(taskId, "test prompt");
        task.setResult("test result");
        task.setStatus(Task.TaskStatus.COMPLETED);
        
        when(taskManager.getTask(taskId)).thenReturn(task);
        
        mockMvc.perform(get("/api/tasks/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.prompt").value("test prompt"))
                .andExpect(jsonPath("$.result").value("test result"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void testGetTaskNotFound() throws Exception {
        String taskId = "non-existent-task";
        when(taskManager.getTask(taskId)).thenReturn(null);
        
        mockMvc.perform(get("/api/tasks/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("任务不存在"));
    }

    @Test
    void testGetTasks() throws Exception {
        when(taskManager.getAllTasks()).thenReturn(Map.of());
        
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks").exists());
    }

    @Test
    void testDeleteTask() throws Exception {
        String taskId = "test-task-id";
        when(taskManager.deleteTask(taskId)).thenReturn(true);
        
        mockMvc.perform(delete("/api/tasks/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
