package com.open.agent.lite.skill;

import com.open.agent.lite.core.ability.AbilityManager;
import com.open.agent.lite.skill.loader.SkillLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于文件定义的技能实现
 * 从SKILL.md文件中加载技能信息
 */
public class FileBasedSkill implements Skill {

    private static final Logger logger = LoggerFactory.getLogger(FileBasedSkill.class);
    private final SkillLoader.SkillInfo skillInfo;
    private AbilityManager abilityManager;

    /**
     * 构造函数
     *
     * @param skillInfo 技能信息
     */
    public FileBasedSkill(SkillLoader.SkillInfo skillInfo) {
        this.skillInfo = skillInfo;
        this.abilityManager = null; // 后续可以通过依赖注入获取
    }

    /**
     * 构造函数
     *
     * @param skillInfo      技能信息
     * @param abilityManager 能力管理器
     */
    public FileBasedSkill(SkillLoader.SkillInfo skillInfo, AbilityManager abilityManager) {
        this.skillInfo = skillInfo;
        this.abilityManager = abilityManager;
    }

    @Override
    public String getSkillId() {
        return skillInfo.getSkillId();
    }

    @Override
    public String getName() {
        return skillInfo.getName();
    }

    @Override
    public String getDescription() {
        return skillInfo.getDescription();
    }

    /**
     * 格式化执行结果
     *
     * @param result 原始执行结果
     * @return 格式化后的结果
     */
    private String formatResult(String result) {
        return "技能: " + skillInfo.getName() + " (ID: " + skillInfo.getSkillId() + ")\n" +
                "执行结果:\n" + result;
    }


    /**
     * 设置能力管理器
     *
     * @param abilityManager 能力管理器
     */
    public void setAbilityManager(AbilityManager abilityManager) {
        this.abilityManager = abilityManager;
    }
}
