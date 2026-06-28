package com.zhy.tidada.service;

import com.zhy.tidada.model.entity.PostThumb;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhy.tidada.model.entity.User;

/**
 * 帖子点赞服务
 *
 * @author <a href="https://github.com/dongzhy">程序员zhy</a>
 */
public interface PostThumbService extends IService<PostThumb> {

    /**
     * 点赞
     *
     * @param postId
     * @param loginUser
     * @return
     */
    int doPostThumb(long postId, User loginUser);

    /**
     * 帖子点赞（内部服务）
     *
     * @param userId
     * @param postId
     * @return
     */
    int doPostThumbInner(long userId, long postId);
}
