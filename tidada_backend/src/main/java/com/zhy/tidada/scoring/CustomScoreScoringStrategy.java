package com.zhy.tidada.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhy.tidada.common.ErrorCode;
import com.zhy.tidada.exception.BusinessException;
import com.zhy.tidada.model.dto.question.QuestionContentDTO;
import com.zhy.tidada.model.entity.App;
import com.zhy.tidada.model.entity.Question;
import com.zhy.tidada.model.entity.ScoringResult;
import com.zhy.tidada.model.entity.UserAnswer;
import com.zhy.tidada.model.vo.QuestionVO;
import com.zhy.tidada.service.QuestionService;
import com.zhy.tidada.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 自定义打分
 */
@ScoringStrategyConfig(appType = 0,scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {
    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        // 1. 根据 id 查询到题目和题目结果信息（按分数降序排序）
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class)
                        .eq(ScoringResult::getAppId, appId)
                        .orderByDesc(ScoringResult::getResultScoreRange)
        );
        if (CollectionUtils.isEmpty(scoringResultList)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"当前应用未配置评分");
        }

        // 2. 统计用户的总得分
        int totalScore = 0;
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        if (CollectionUtils.isEmpty(questionContent)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目无配置选项");
        }

        // 遍历题目列表
        for (QuestionContentDTO questionContentDTO : questionContent) {
            List<QuestionContentDTO.Option> options = questionContentDTO.getOptions();
            if (CollectionUtils.isEmpty(options)) {
                continue;
            }
            // 遍历答案列表
            for (String answer : choices) {
                // 遍历题目中的选项
                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                    // 如果答案和选项的key匹配
                    if (option.getKey().equals(answer)) {
                        int score = Optional.of(option.getScore()).orElse(0);
                        totalScore += score;
                    }
                }
            }
        }

        // 3. 遍历得分结果，找到第一个用户分数大于得分范围的结果，作为最终结果
        ScoringResult maxScoringResult = null;
        for (ScoringResult scoringResult : scoringResultList) {
            if (totalScore >= scoringResult.getResultScoreRange()) {
                maxScoringResult = scoringResult;
                break;
            }
        }
        if (maxScoringResult == null) {
            maxScoringResult  = scoringResultList.get(scoringResultList.size() - 1);
        }

        // 4. 构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        userAnswer.setResultScore(totalScore);
        return userAnswer;
    }
}
