package com.open.agent.lite.skill;

import com.open.agent.lite.skill.loader.SkillLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 技能管理器
 * 负责管理和注册技能
 */
@Component
public class SkillManager {

    private static final Logger logger = LoggerFactory.getLogger(SkillManager.class);
    private final Map<String, Skill> skillsByName = new HashMap<>();
    private final Map<String, Skill> skillsById = new HashMap<>();

    @Autowired
    private SkillLoader skillLoader;

    /**
     * 注册技能
     *
     * @param skill 技能实例
     */
    public void registerSkill(Skill skill) {
        skillsByName.put(skill.getName(), skill);
        skillsById.put(skill.getSkillId(), skill);
        logger.info("已注册技能: {} (ID: {})", skill.getName(), skill.getSkillId());
    }

    /**
     * 通过名称获取技能
     *
     * @param name 技能名称
     * @return 技能实例
     */
    public Skill getSkillByName(String name) {
        return skillsByName.get(name);
    }

    /**
     * 通过ID获取技能
     *
     * @param skillId 技能ID
     * @return 技能实例
     */
    public Skill getSkillById(String skillId) {
        return skillsById.get(skillId);
    }

    /**
     * 获取技能（先尝试通过ID获取，再尝试通过名称获取）
     *
     * @param identifier 技能标识符（ID或名称）
     * @return 技能实例
     */
    public Skill getSkill(String identifier) {
        Skill skill = getSkillById(identifier);
        if (skill == null) {
            skill = getSkillByName(identifier);
        }
        return skill;
    }

    /**
     * 读取技能的原始文件内容
     *
     * @param identifier 技能标识符（ID或名称）
     * @return 技能文件内容
     */
    public String getSkillFileContent(String identifier) {
        // 先尝试通过ID读取
        String content = skillLoader.readSkillFileContent(identifier);
        if (content != null) {
            return content;
        }

        // 再尝试通过名称查找并读取
        Skill skill = getSkillByName(identifier);
        if (skill != null) {
            return skillLoader.readSkillFileContent(skill.getSkillId());
        }

        return null;
    }

    /**
     * 获取所有技能
     *
     * @return 技能映射
     */
    public Map<String, Skill> getAllSkills() {
        return skillsByName;
    }


    /**
     * 获取技能基本信息列表（用于系统提示词）
     *
     * @return 技能基本信息列表
     */
    public String getSkillsBasicInfo() {
        StringBuilder sb = new StringBuilder();
        for (SkillLoader.SkillInfo skillInfo : skillLoader.getAllSkillInfos().values()) {
            sb.append(skillInfo.getName()).append(" (ID: ").append(skillInfo.getSkillId()).append("): ").append(skillInfo.getDescription()).append("\n");
            if (skillInfo.getTriggerCondition() != null && !skillInfo.getTriggerCondition().isEmpty()) {
                sb.append("触发条件: ").append(skillInfo.getTriggerCondition()).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 检查技能是否存在
     *
     * @param identifier 技能标识符（ID或名称）
     * @return 是否存在
     */
    public boolean hasSkill(String identifier) {
        return skillsById.containsKey(identifier) || skillsByName.containsKey(identifier) || skillLoader.getAllSkillInfos().containsKey(identifier);
    }

    /**
     * 检查技能是否存在（通过ID）
     *
     * @param skillId 技能ID
     * @return 是否存在
     */
    public boolean hasSkillById(String skillId) {
        return skillsById.containsKey(skillId) || skillLoader.getAllSkillInfos().containsKey(skillId);
    }

    /**
     * 检查技能是否存在（通过名称）
     *
     * @param name 技能名称
     * @return 是否存在
     */
    public boolean hasSkillByName(String name) {
        return skillsByName.containsKey(name);
    }
}