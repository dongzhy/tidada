package com.zhy.tidada.model.dto.userAnswer;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建用户答案请求
 *
 * @author <a href="https://github.com/dongzhy">程序员zhy</a>
 */
@Data
public class UserAnswerAddRequest implements Serializable {
    /**
     * id 【用于保证幂等性，用户答案id】
     */
    private Long id;

    /**
     * 应用 id
     */
    private Long appId;


    /**
     * 用户答案（JSON 数组）
     */
    private List<String> choices;




    private static final long serialVersionUID = 1L;
}