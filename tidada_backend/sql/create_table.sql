# 数据库初始化
-- 创建库
create database if not exists tidada;

-- 切换库
use tidada;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;


-- 应用表
create table if not exists app
(
    id              bigint auto_increment comment 'id' primary key,
    appName         varchar(128)                       not null comment '应用名',
    appDesc         varchar(2048)                      null comment '应用描述',
    appIcon         varchar(1024)                      null comment '应用图标',
    appType         tinyint  default 0                 not null comment '应用类型（0-得分类，1-测评类）',
    scoringStrategy tinyint  default 0                 not null comment '评分策略（0-自定义，1-AI）',
    reviewStatus    int      default 0                 not null comment '审核状态：0-待审核, 1-通过, 2-拒绝',
    reviewMessage   varchar(512)                       null comment '审核信息',
    reviewerId      bigint                             null comment '审核人 id',
    reviewTime      datetime                           null comment '审核时间',
    userId          bigint                             not null comment '创建用户 id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_appName (appName)
) comment '应用' collate = utf8mb4_unicode_ci;


-- 题目表
create table if not exists question
(
    id              bigint auto_increment comment 'id' primary key,
    questionContent text                               null comment '题目内容（json格式）',
    appId           bigint                             not null comment '应用 id',
    userId          bigint                             not null comment '创建用户 id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_appId (appId)
) comment '题目' collate = utf8mb4_unicode_ci;


-- 评分结果表
create table if not exists scoring_result
(
    id               bigint auto_increment comment 'id' primary key,
    resultName       varchar(128)                       not null comment '结果名称，如物流师',
    resultDesc       text                               null comment '结果描述',
    resultPicture    varchar(1024)                      null comment '结果图片',
    resultProp       varchar(128)                       null comment '结果属性集合 JSON，如 [I,S,T,J]',
    resultScoreRange int                                null comment '结果得分范围，如 80，表示 80及以上的分数命中此结果',
    appId            bigint                             not null comment '应用 id',
    userId           bigint                             not null comment '创建用户 id',
    createTime       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete         tinyint  default 0                 not null comment '是否删除',
    index idx_appId (appId)
) comment '评分结果' collate = utf8mb4_unicode_ci;

-- 得分 评分结果初始化
INSERT INTO scoring_result (id, resultName, resultDesc, resultPicture, resultProp, resultScoreRange, createTime, updateTime, isDelete, appId, userId) VALUES (17, '知识大师', '你真棒棒哦，知识掌握地非常出色！', null, null, 9, '2024-04-25 15:05:44', '2024-05-09 12:28:21', 0, 2, 1);
INSERT INTO scoring_result (id, resultName, resultDesc, resultPicture, resultProp, resultScoreRange, createTime, updateTime, isDelete, appId, userId) VALUES (18, '地理小能手！', '你对于地理知识了解得相当不错，但还有一些小地方需要加强哦！', null, null, 7, '2024-04-25 15:05:44', '2024-05-09 12:28:21', 0, 2, 1);
INSERT INTO scoring_result (id, resultName, resultDesc, resultPicture, resultProp, resultScoreRange, createTime, updateTime, isDelete, appId, userId) VALUES (19, '继续加油！', '还需努力哦', null, null, 0, '2024-04-25 15:05:44', '2024-05-09 12:28:21', 0, 2, 1);

-- 用户答题记录表
create table if not exists user_answer
(
    id              bigint auto_increment primary key,
    appId           bigint                             not null comment '应用 id',
    appType         tinyint  default 0                 not null comment '应用类型（0-得分类，1-角色测评类）',
    scoringStrategy tinyint  default 0                 not null comment '评分策略（0-自定义，1-AI）',
    choices         text                               null comment '用户答案（JSON 数组）',
    resultId        bigint                             null comment '评分结果 id',
    resultName      varchar(128)                       null comment '结果名称，如物流师',
    resultDesc      text                               null comment '结果描述',
    resultPicture   varchar(1024)                      null comment '结果图标',
    resultScore     int                                null comment '得分',
    userId          bigint                             not null comment '用户 id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_appId (appId),
    index idx_userId (userId)
) comment '用户答题记录' collate = utf8mb4_unicode_ci;


-- 用户答题记录表[分表0]
create table if not exists user_answer_0
(
    id              bigint auto_increment primary key,
    appId           bigint                             not null comment '应用 id',
    appType         tinyint  default 0                 not null comment '应用类型（0-得分类，1-角色测评类）',
    scoringStrategy tinyint  default 0                 not null comment '评分策略（0-自定义，1-AI）',
    choices         text                               null comment '用户答案（JSON 数组）',
    resultId        bigint                             null comment '评分结果 id',
    resultName      varchar(128)                       null comment '结果名称，如物流师',
    resultDesc      text                               null comment '结果描述',
    resultPicture   varchar(1024)                      null comment '结果图标',
    resultScore     int                                null comment '得分',
    userId          bigint                             not null comment '用户 id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_appId (appId),
    index idx_userId (userId)
) comment '用户答题记录 分表0' collate = utf8mb4_unicode_ci;


-- 用户答题记录表[分表0]
create table if not exists user_answer_1
(
    id              bigint auto_increment primary key,
    appId           bigint                             not null comment '应用 id',
    appType         tinyint  default 0                 not null comment '应用类型（0-得分类，1-角色测评类）',
    scoringStrategy tinyint  default 0                 not null comment '评分策略（0-自定义，1-AI）',
    choices         text                               null comment '用户答案（JSON 数组）',
    resultId        bigint                             null comment '评分结果 id',
    resultName      varchar(128)                       null comment '结果名称，如物流师',
    resultDesc      text                               null comment '结果描述',
    resultPicture   varchar(1024)                      null comment '结果图标',
    resultScore     int                                null comment '得分',
    userId          bigint                             not null comment '用户 id',
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除',
    index idx_appId (appId),
    index idx_userId (userId)
) comment '用户答题记录 分表1' collate = utf8mb4_unicode_ci;


-- 1. 得分类应用 appType=0 scoringStrategy=0（你报错的类型）
INSERT INTO app (id, appName, appDesc, appType, scoringStrategy, createTime, updateTime, isDelete)
VALUES
    (10001, '数学得分测评', '选择题答题算总分，按分数评级', 0, 0, NOW(), NOW(), 0),
-- 2. 测评类应用 appType=1 scoringStrategy=0（自定义人格测评）
    (10002, '性格MBTI测评', '人格属性统计匹配结果', 1, 0, NOW(), NOW(), 0),
-- 3. AI测评 appType=1 scoringStrategy=1
    (10003, 'AI心理测评', 'AI自动生成评价', 1, 1, NOW(), NOW(), 0);

ALTER TABLE user_answer MODIFY COLUMN userId BIGINT DEFAULT 0 COMMENT '答题用户id';
-- 3. 得分类题目 10001（question表自带userId创建人字段，补充创建人ID）
INSERT INTO question (id, appId, questionContent, userId, createTime, updateTime, isDelete) VALUES
    (20001, 10001, '[
    {
        "title": "1+1等于几",
        "options": [
            {"key": "A", "value": "1", "score": 0},
            {"key": "B", "value": "2", "score": 10},
            {"key": "C", "value": "3", "score": 0}
        ]
    },
    {
        "title": "2*3等于几",
        "options": [
            {"key": "A", "value": "5", "score": 0},
            {"key": "B", "value": "6", "score": 10},
            {"key": "C", "value": "9", "score": 0}
        ]
    }
]', 2032130737862610946, NOW(), NOW(), 0);

-- 4. MBTI测评题目 10002
INSERT INTO question (id, appId, questionContent, userId, createTime, updateTime, isDelete) VALUES
    (20002, 10002, '[
    {
        "title": "聚会更喜欢主动聊天",
        "options": [
            {"key": "A", "value": "是", "result": "E"},
            {"key": "B", "value": "否", "result": "I"}
        ]
    },
    {
        "title": "做事喜欢提前规划",
        "options": [
            {"key": "A", "value": "是", "result": "J"},
            {"key": "B", "value": "随性", "result": "P"}
        ]
    }
]', 2032130737862610946, NOW(), NOW(), 0);

-- 5. 得分类评分结果
INSERT INTO scoring_result (id, appId, resultName, resultDesc, resultPicture, resultScoreRange, userId, createTime, updateTime, isDelete)
VALUES
    (30001, 10001, '不及格', '得分0~9，基础薄弱，需要多加练习', '', 0, 2032130737862610946, NOW(), NOW(), 0),
    (30002, 10001, '良好', '得分10~19，基础掌握不错，还有提升空间', '', 10, 2032130737862610946, NOW(), NOW(), 0),
    (30003, 10001, '优秀', '得分20，全部答对，知识掌握牢固', '', 20, 2032130737862610946, NOW(), NOW(), 0);

-- 6. MBTI评分结果
INSERT INTO scoring_result (id, appId, resultName, resultDesc, resultPicture, resultProp, userId, createTime, updateTime, isDelete)
VALUES
    (30004, 10002, '外向活泼型(EJ)', '性格外向，做事有条理，擅长社交组织', '', '["E","J"]', 2032130737862610946, NOW(), NOW(), 0),
    (30005, 10002, '内向随性型(IP)', '性格内敛，喜欢自由，不喜束缚', '', '["I","P"]', 2032130737862610946, NOW(), NOW(), 0);

INSERT INTO user_answer (id, appId, appType, scoringStrategy, choices, resultId, resultName, resultDesc, resultPicture, resultScore, userId, createTime, updateTime, isDelete)
VALUES
    (50001, 10001, 0, 0, '["B","B"]', 30003, '优秀', '得分20，全部答对，知识掌握牢固', '', 20, 2032130737862610946, NOW(), NOW(), 0),
    (50002, 10002, 1, 0, '["A","A"]', 30004, '外向活泼型(EJ)', '性格外向，做事有条理，擅长社交组织', '', 0, 2032130737862610946, NOW(), NOW(), 0);