package com.open.agent.lite.core.parser;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LLM响应解析器
 */
public class LLMResponseParser {

    private static final Pattern ACTION_PATTERN = Pattern.compile("Action: *([^,]+)(?:, *([^=]+)=([^,]+))*");
    private static final Pattern FINAL_ANSWER_PATTERN = Pattern.compile("Final Answer: *(.+)");
    private static final Pattern THOUGHT_PATTERN = Pattern.compile("思考： *(.+)");

    /**
     * 解析LLM响应
     * @param response LLM响应
     * @return 解析结果
     */
    public ParsedResponse parse(String response) {
        ParsedResponse parsedResponse = new ParsedResponse();

        // 解析思考
        Matcher thoughtMatcher = THOUGHT_PATTERN.matcher(response);
        if (thoughtMatcher.find()) {
            parsedResponse.setThought(thoughtMatcher.group(1).trim());
        }

        // 解析最终答案
        Matcher finalAnswerMatcher = FINAL_ANSWER_PATTERN.matcher(response);
        if (finalAnswerMatcher.find()) {
            parsedResponse.setFinalAnswer(finalAnswerMatcher.group(1).trim());
            return parsedResponse;
        }

        // 解析行动
        Matcher actionMatcher = ACTION_PATTERN.matcher(response);
        if (actionMatcher.find()) {
            String toolName = actionMatcher.group(1).trim();
            Map<String, String> params = new HashMap<>();

            // 解析参数
            for (int i = 2; i <= actionMatcher.groupCount(); i += 2) {
                if (actionMatcher.group(i) != null && actionMatcher.group(i + 1) != null) {
                    params.put(actionMatcher.group(i).trim(), actionMatcher.group(i + 1).trim());
                }
            }

            parsedResponse.setToolName(toolName);
            parsedResponse.setParams(params);
        }

        return parsedResponse;
    }

    /**
     * 解析结果类
     */
    @Setter
    @Getter
    public static class ParsedResponse {
        private String thought;
        private String toolName;
        private Map<String, String> params;
        private String finalAnswer;

        public boolean hasFinalAnswer() {
            return finalAnswer != null && !finalAnswer.isEmpty();
        }

        public boolean hasAction() {
            return toolName != null && !toolName.isEmpty();
        }
    }
}