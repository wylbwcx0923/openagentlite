package com.open.agent.lite.skill.loader;

import com.open.agent.lite.util.ReadResourceFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 技能加载器
 * 负责加载和管理SKILL.md文件
 */
@Component
public class SkillLoader {

    private static final Logger logger = LoggerFactory.getLogger(SkillLoader.class);
    private static final String SKILL_CONFIG_FILE = "skills/skill.config";
    private static final String SKILLS_DIR = "skills";
    private final Map<String, SkillInfo> skillInfos = new HashMap<>();

    /**
     * 加载所有技能的基本信息
     */
    public void loadSkills() {
        try {
            String skillConfigStr = ReadResourceFileUtil.readResourceFile(SKILL_CONFIG_FILE);
            List<String> skillConfigLines = Arrays.asList(skillConfigStr.split("\n"));
            skillConfigLines.forEach(skillId -> {
                String skillPath = SKILLS_DIR + "/" + skillId + "/SKILL.md";
                String skillContent = ReadResourceFileUtil.readResourceFile(skillPath);
                processSkillContent(skillId, skillContent);
            });

            logger.info("已加载 {} 个技能基本信息", skillInfos.size());
        } catch (Exception e) {
            logger.error("加载技能失败: {}", e.getMessage(), e);
        }
    }


    /**
     * 处理技能文件内容
     */
    private void processSkillContent(String skillId, String content) {
        SkillInfo skillInfo = new SkillInfo();
        skillInfo.setSkillId(skillId);

        // 简单提取名称和描述（从文件内容的前几行）
        String[] lines = content.split("\\n");
        for (String line : lines) {
            if (line.startsWith("# ")) {
                skillInfo.setName(line.substring(2).trim());
            } else if (line.startsWith("描述：")) {
                skillInfo.setDescription(line.substring(3).trim());
            } else if (line.startsWith("## 触发条件")) {
                // 提取触发条件
                int startIndex = content.indexOf("## 触发条件") + "## 触发条件".length();
                int endIndex = content.indexOf("##", startIndex);
                if (endIndex > startIndex) {
                    skillInfo.setTriggerCondition(content.substring(startIndex, endIndex).trim());
                }
                break;
            }
        }

        if (skillInfo.getName() != null) {
            skillInfos.put(skillId, skillInfo);
            logger.info("已加载技能基本信息: {}", skillId);
        }
    }

    /**
     * 读取技能的原始文件内容
     *
     * @param skillId 技能ID
     * @return 技能文件内容
     */
    public String readSkillFileContent(String skillId) {
        try {
            String skillPath = SKILLS_DIR + "/" + skillId + "/SKILL.md";
            return ReadResourceFileUtil.readResourceFile(skillPath);
        } catch (Exception e) {
            logger.error("读取技能文件失败: {}", skillId, e);
        }
        return null;
    }

    /**
     * 获取技能信息
     *
     * @param skillId 技能ID
     * @return 技能信息
     */
    public SkillInfo getSkillInfo(String skillId) {
        return skillInfos.get(skillId);
    }

    /**
     * 获取所有技能信息
     *
     * @return 技能信息映射
     */
    public Map<String, SkillInfo> getAllSkillInfos() {
        return skillInfos;
    }

    /**
     * 技能信息类
     */
    public static class SkillInfo {
        private String skillId;
        private String name;
        private String description;
        private String triggerCondition;

        // Getters and setters
        public String getSkillId() {
            return skillId;
        }

        public void setSkillId(String skillId) {
            this.skillId = skillId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTriggerCondition() {
            return triggerCondition;
        }

        public void setTriggerCondition(String triggerCondition) {
            this.triggerCondition = triggerCondition;
        }
    }
}