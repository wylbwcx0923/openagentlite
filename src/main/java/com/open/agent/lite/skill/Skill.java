package com.open.agent.lite.skill;

import com.open.agent.lite.skill.loader.SkillLoader;

/**
 * 技能接口
 * 定义技能的基本方法
 */
public interface Skill {

    /**
     * 获取技能ID
     * @return 技能ID
     */
    String getSkillId();

    /**
     * 获取技能名称
     * @return 技能名称
     */
    String getName();

    /**
     * 获取技能描述
     * @return 技能描述
     */
    String getDescription();
    
    /**
     * 从技能信息创建技能实例
     * @param skillInfo 技能信息
     * @param abilityManager 能力管理器
     * @return 技能实例
     */
    static Skill createFromSkillInfo(SkillLoader.SkillInfo skillInfo, com.open.agent.lite.core.ability.AbilityManager abilityManager) {
        return new FileBasedSkill(skillInfo, abilityManager);
    }
}
