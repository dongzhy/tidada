package com.zhy.tidada.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhy.tidada.model.entity.UserAnswer;
import com.zhy.tidada.model.statistic.AppAnswerCountDTO;
import com.zhy.tidada.model.statistic.AppAnswerResultCountDTO;
import org.apache.ibatis.annotations.Select;

import java.util.List;


/**
 * @author 15922
 * @description 针对表【user_answer(用户答题记录)】的数据库操作Mapper
 * @createDate 2026-03-12 13:00:55
 * @Entity generator.domain.UserAnswer
 */
public interface UserAnswerMapper extends BaseMapper<UserAnswer> {


        @Select("select appId, count(userId) as answerCount from user_answer " +
                "group by appId order by answerCount desc")
        List<AppAnswerCountDTO> doAppAnswerCount();





        @Select("select resultName, count(resultName) as resultCount from user_answer " +
                "where appId = #{appId} group by resultName order by resultCount desc")
        List<AppAnswerResultCountDTO> doAppAnswerResultCount(Long appId);


}




