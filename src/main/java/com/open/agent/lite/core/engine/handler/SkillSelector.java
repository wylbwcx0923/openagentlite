package com.open.agent.lite.core.engine.handler;

import com.open.agent.lite.core.engine.ReActContext;
import com.open.agent.lite.core.engine.parser.ResponseParser;
import com.open.agent.lite.skill.SkillManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 技能选择器
 * 负责技能选择，包括读取技能文件内容和设置当前技能
 */
public class SkillSelector {

    private static final Logger logger = LoggerFactory.getLogger(SkillSelector.class);
    private final SkillManager skillManager;
    private final ResponseParser responseParser;

    public SkillSelector(SkillManager skillManager, ResponseParser responseParser) {
        this.skillManager = skillManager;
        this.responseParser = responseParser;
    }

    /**
     * 选择技能
     * @param ctx 上下文
     */
    public void selectSkill(ReActContext ctx) {
        try {
            // 获取最近的LLM响应
            String lastResponse = ctx.getLastResponse();
            if (lastResponse == null) {
                logger.warn("没有找到LLM响应");
                return;
            }

            // 解析JSON响应
            String skillName = responseParser.getTarget(lastResponse);
            if (skillName == null || skillName.isEmpty()) {
                logger.warn("响应中没有技能名称");
                return;
            }

            // 读取技能的原始文件内容
            String skillContent = skillManager.getSkillFileContent(skillName);
            if (skillContent != null) {
                // 将技能的原始文件内容添加到上下文
                ctx.addToHistory("Skill Info: " + skillContent);
                logger.info("加载技能原始文件内容: {}", skillName);
            }

            // 选择技能
            if (skillManager.hasSkill(skillName)) {
                ctx.setCurrentSkill(skillManager.getSkill(skillName));
                logger.info("选择技能: {}", skillName);
            } else {
                logger.warn("技能不存在: {}", skillName);
            }
        } catch (Exception e) {
            logger.error("选择技能时发生错误: {}", e.getMessage(), e);
        }
    }
}