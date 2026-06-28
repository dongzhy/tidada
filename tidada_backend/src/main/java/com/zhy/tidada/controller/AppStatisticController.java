package com.zhy.tidada.controller;

import com.zhy.tidada.common.BaseResponse;
import com.zhy.tidada.common.ErrorCode;
import com.zhy.tidada.common.ResultUtils;
import com.zhy.tidada.exception.ThrowUtils;
import com.zhy.tidada.mapper.UserAnswerMapper;
import com.zhy.tidada.model.statistic.AppAnswerCountDTO;
import com.zhy.tidada.model.statistic.AppAnswerResultCountDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/app/statistic")
@Slf4j
public class AppStatisticController {

    @Resource
    private UserAnswerMapper userAnswerMapper;

    /**
     * 热门应用及回答数统计
     * @return
     */

    @GetMapping("/answer_count")
    public BaseResponse<List<AppAnswerCountDTO>> getAppAnswerCount() {
        return ResultUtils.success(userAnswerMapper.doAppAnswerCount());
    }
    @GetMapping("/answer_result_count")
    public BaseResponse<List<AppAnswerResultCountDTO>> getAppAnswerResultCount(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(userAnswerMapper.doAppAnswerResultCount(appId));
    }

}
