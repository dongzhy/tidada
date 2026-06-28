package com.zhy.tidada.model.statistic;

import lombok.Data;

/**
 * app用户提交答案数统计
 */
@Data
public class AppAnswerCountDTO {

    private long appId;
    /**
     * 用户提交答案数
     */
    private long answerCount;
}
