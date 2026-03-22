package com.open.agent.lite.core.ability.impl;

import com.open.agent.lite.core.ability.AbstractAbility;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

/**
 * 代码执行能力
 * 提供执行代码的功能
 */
@Component
public class CodeExecuteAbility extends AbstractAbility {

    public CodeExecuteAbility() {
        super("CodeExecuteAbility", "代码执行能力，支持执行Java代码");
    }

    @Override
    protected String doExecute(Map<String, String> params) {
        String code = params.get("code");
        String language = params.get("language");

        if (code == null) {
            return "参数错误：缺少code参数";
        }

        if (language == null) {
            language = "java";
        }

        try {
            if (language.equalsIgnoreCase("java")) {
                return executeJavaCode(code);
            }
            return "不支持的语言：" + language;
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }

    private String executeJavaCode(String code) throws Exception {
        // 创建一个临时类
        String className = "TempClass" + System.currentTimeMillis();
        String fullCode = "public class " + className + " {\n" +
                "    public static void main(String[] args) {\n" +
                code + "\n" +
                "    }\n" +
                "}";

        // 保存到临时文件
        java.io.File tempFile = java.io.File.createTempFile(className, ".java");
        java.io.FileWriter writer = new java.io.FileWriter(tempFile);
        writer.write(fullCode);
        writer.close();

        // 编译代码
        Process compileProcess = Runtime.getRuntime().exec("javac " + tempFile.getAbsolutePath());
        int compileExitCode = compileProcess.waitFor();
        if (compileExitCode != 0) {
            java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(compileProcess.getErrorStream()));
            StringBuilder error = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                error.append(line).append("\n");
            }
            reader.close();
            tempFile.delete();
            return "编译失败\n" + error;
        }

        // 执行代码
        String classPath = tempFile.getParent();
        Process executeProcess = Runtime.getRuntime().exec("java -cp " + classPath + " " + className);

        // 捕获输出
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        
        java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(executeProcess.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            printStream.println(line);
        }
        reader.close();

        // 捕获错误
        reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(executeProcess.getErrorStream()));
        while ((line = reader.readLine()) != null) {
            printStream.println(line);
        }
        reader.close();

        int executeExitCode = executeProcess.waitFor();
        
        // 清理临时文件
        tempFile.delete();
        new java.io.File(tempFile.getParent(), className + ".class").delete();

        if (executeExitCode == 0) {
            return "代码执行成功\n输出：\n" + outputStream.toString();
        } else {
            return "代码执行失败\n输出：\n" + outputStream.toString();
        }
    }

    @Override
    public String getParamsDescription() {
        return "code: 代码内容, language: 语言类型(默认java)";
    }

    @Override
    protected boolean checkAvailability() {
        try {
            // 检查是否有Java编译器
            Process process = Runtime.getRuntime().exec("javac -version");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
