package com.open.agent.lite.util;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ReadResourceFileUtil {

    public static String readResourceFile(String fileName) {
        try (InputStream is = ReadResourceFileUtil.class.getClassLoader().getResourceAsStream(fileName)) {
            return IoUtil.read(is).toString();
        } catch (IOException e) {
            throw new RuntimeException("读取配置文件失败", e);
        }
    }

}
