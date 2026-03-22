package com.open.agent.lite.core.ability.impl;

import com.open.agent.lite.core.ability.AbstractAbility;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 计算器能力
 * 提供数学计算功能
 */
@Component
public class CalculatorAbility extends AbstractAbility {

    public CalculatorAbility() {
        super("CalculatorAbility", "数学计算能力，支持基本的数学运算");
    }

    @Override
    protected String doExecute(Map<String, String> params) {
        String expression = params.get("expression");
        if (expression == null || expression.isEmpty()) {
            return "请提供数学表达式";
        }

        try {
            // 简单的数学表达式计算
            double result = evaluateExpression(expression);
            return "计算结果: " + result;
        } catch (Exception e) {
            return "计算失败: " + e.getMessage();
        }
    }

    private double evaluateExpression(String expression) {
        // 简单的表达式计算实现
        // 这里仅支持基本的加减乘除
        expression = expression.replaceAll("\\s+", "");
        
        // 处理括号
        while (expression.contains("(")) {
            int start = expression.lastIndexOf("(");
            int end = expression.indexOf(")", start);
            if (end == -1) {
                throw new IllegalArgumentException("括号不匹配");
            }
            String subExpr = expression.substring(start + 1, end);
            double subResult = evaluateSimpleExpression(subExpr);
            expression = expression.substring(0, start) + subResult + expression.substring(end + 1);
        }
        
        return evaluateSimpleExpression(expression);
    }

    private double evaluateSimpleExpression(String expression) {
        // 处理乘除
        String[] parts = expression.split("[+\\-]");
        
        // 提取操作符
        java.util.List<String> opList = new java.util.ArrayList<>();
        for (char c : expression.toCharArray()) {
            if (c == '+' || c == '-' || c == '*' || c == '/') {
                opList.add(String.valueOf(c));
            }
        }
        String[] ops = opList.toArray(new String[0]);
        
        double result = Double.parseDouble(parts[0]);
        for (int i = 0; i < ops.length && i + 1 < parts.length; i++) {
            double value = Double.parseDouble(parts[i + 1]);
            switch (ops[i]) {
                case "+":
                    result += value;
                    break;
                case "-":
                    result -= value;
                    break;
                case "*":
                    result *= value;
                    break;
                case "/":
                    if (value == 0) {
                        throw new ArithmeticException("除数不能为零");
                    }
                    result /= value;
                    break;
            }
        }
        return result;
    }

    @Override
    public String getParamsDescription() {
        return "expression: 数学表达式，支持加减乘除和括号";
    }

    @Override
    protected boolean checkAvailability() {
        return true;
    }
}
