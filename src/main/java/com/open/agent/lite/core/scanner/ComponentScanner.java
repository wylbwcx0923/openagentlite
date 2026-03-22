package com.open.agent.lite.core.scanner;

import com.open.agent.lite.core.ability.Ability;
import com.open.agent.lite.core.ability.AbilityManager;
import com.open.agent.lite.mcp.tool.Tool;
import com.open.agent.lite.mcp.tool.ToolManager;
import com.open.agent.lite.skill.Skill;
import com.open.agent.lite.skill.SkillManager;
import com.open.agent.lite.skill.loader.SkillLoader;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 组件扫描器
 * 系统启动时自动扫描所有Skill、Tool和Ability组件
 */
@Component
public class ComponentScanner {

    private static final Logger logger = LoggerFactory.getLogger(ComponentScanner.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SkillManager skillManager;

    @Autowired
    private ToolManager toolManager;

    @Autowired
    private AbilityManager abilityManager;

    @Autowired
    private SkillLoader skillLoader;

    /**
     * 初始化方法，系统启动时执行
     */
    @PostConstruct
    public void init() {
        logger.info("开始组件扫描...");
        scanAbilities();
        scanTools();
        scanSkills();
        logger.info("组件扫描完成");
    }

    /**
     * 扫描所有能力组件
     */
    private void scanAbilities() {
        try {
            Map<String, Ability> abilities = applicationContext.getBeansOfType(Ability.class);
            for (Ability ability : abilities.values()) {
                abilityManager.registerAbility(ability);
                logger.info("自动注册能力: {}", ability.getName());
            }
            logger.info("已扫描 {} 个能力", abilities.size());
        } catch (Exception e) {
            logger.error("扫描能力失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 扫描所有工具组件
     */
    private void scanTools() {
        try {
            Map<String, Tool> tools = applicationContext.getBeansOfType(Tool.class);
            for (Tool tool : tools.values()) {
                toolManager.registerTool(tool);
                logger.info("自动注册工具: {}", tool.getName());
            }
            logger.info("已扫描 {} 个工具", tools.size());
        } catch (Exception e) {
            logger.error("扫描工具失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 扫描所有技能组件
     */
    private void scanSkills() {
        try {
            // 加载所有技能信息
            skillLoader.loadSkills();
            
            // 从技能信息创建技能实例并注册
            for (SkillLoader.SkillInfo skillInfo : skillLoader.getAllSkillInfos().values()) {
                Skill skill = Skill.createFromSkillInfo(skillInfo, abilityManager);
                skillManager.registerSkill(skill);
                logger.info("自动注册技能: {} (带依赖)", skill.getSkillId());
            }
            
            // 扫描Java类实现的技能
            Map<String, Skill> skills = applicationContext.getBeansOfType(Skill.class);
            for (Skill skill : skills.values()) {
                if (!(skill instanceof com.open.agent.lite.skill.FileBasedSkill)) {
                    skillManager.registerSkill(skill);
                    logger.info("自动注册技能: {}", skill.getName());
                }
            }
            logger.info("已扫描 {} 个技能", skills.size() + skillLoader.getAllSkillInfos().size());
        } catch (Exception e) {
            logger.error("扫描技能失败: {}", e.getMessage(), e);
        }
    }
}
