package com.open.agent.lite.core.engine;

import com.open.agent.lite.llm.LlmClient;
import com.open.agent.lite.mcp.tool.ToolManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ReActEngineTest {

    @Mock
    private LlmClient llmClient;
    
    @Mock
    private ToolManager toolManager;
    
    private ReActEngine reActEngine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reActEngine = new ReActEngine();
        // 使用反射设置依赖
        try {
            java.lang.reflect.Field llmClientField = ReActEngine.class.getDeclaredField("llmClient");
            llmClientField.setAccessible(true);
            llmClientField.set(reActEngine, llmClient);
            
            java.lang.reflect.Field toolManagerField = ReActEngine.class.getDeclaredField("toolManager");
            toolManagerField.setAccessible(true);
            toolManagerField.set(reActEngine, toolManager);
            
            java.lang.reflect.Field maxStepsField = ReActEngine.class.getDeclaredField("maxSteps");
            maxStepsField.setAccessible(true);
            maxStepsField.set(reActEngine, 3);
            
            java.lang.reflect.Field timeoutField = ReActEngine.class.getDeclaredField("timeout");
            timeoutField.setAccessible(true);
            timeoutField.set(reActEngine, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRunWithFinalAnswer() {
        String userPrompt = "What is the capital of France?";
        String llmResponse = "思考：用户问法国的首都是什么，我知道法国的首都是巴黎。\nFinal Answer: Paris";
        
        when(llmClient.chat("", "")).thenReturn(llmResponse);
        
        String result = reActEngine.run(userPrompt);
        assertEquals("Paris", result);
    }

    @Test
    void testRunWithToolCall() {
        String userPrompt = "What is 2 + 2?";
        String llmResponse1 = "思考：用户问2加2等于多少，我需要使用计算器工具来计算。\nAction: calculator, expression=2+2";
        String toolResponse = "计算结果: 4";
        String llmResponse2 = "思考：工具返回了结果4，这是正确的答案。\nFinal Answer: 4";
        
        when(llmClient.chat("", "")).thenReturn(llmResponse1, llmResponse2);
        when(toolManager.executeTool("calculator", java.util.Map.of("expression", "2+2"))).thenReturn(toolResponse);
        
        String result = reActEngine.run(userPrompt);
        assertEquals("4", result);
    }

    @Test
    void testRunWithMaxSteps() {
        String userPrompt = "What is the meaning of life?";
        String llmResponse = "思考：这是一个深刻的问题，需要更多思考。";
        
        when(llmClient.chat("", "")).thenReturn(llmResponse);
        
        String result = reActEngine.run(userPrompt);
        assertEquals("任务执行超时，已达到最大步数限制", result);
    }
}
