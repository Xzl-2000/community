package com.miki.community.dao;

import com.miki.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    //因为做的是一个分页查询的功能，所以返回的是多个数据——>一个集合List，集合里装的是帖子的对象
    /**
     * 分页查询讨论帖信息
     *
     * @param userId 当传入的 userId = 0 时查找所有用户的帖子
     *               当传入的 userId != 0 时，查找该指定用户的帖子
     * @param offset 每页的起始索引
     * @param limit  每页显示多少条数据
     * @param orderMode  排行模式(若传入 1, 则按照热度来排序)
     * @return
     */
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    /**
     * 查询讨论贴的个数
     * @param userId 当传入的 userId = 0 时计算所有用户的帖子总数
     *               当传入的 userId ！= 0 时计算该指定用户的帖子总数
     * @return
     */
    //Param注解用于给参数取别名
    //如果只有一个参数，且在mapper.xml文件中被使用在<if>标签中(动态sql中)使用时，则一定要用@Param注解取别名
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * 插入/添加帖子
     * @param discussPost
     * @return
     *//*
    int insertDiscussPost(DiscussPost discussPost);

    *//**
     * 根据 id 查询帖子
     * @param id
     * @return
     *//*
    DiscussPost selectDiscussPostById(int id);

    *//**
     * 修改评论数量
     * @param id
     * @param commentCount
     * @return
     *//*
    int updateCommentCount(int id, int commentCount);

    *//**
     * 修改帖子类型：0-普通; 1-置顶;
     * @param id
     * @param type
     * @return
     *//*
    int updateType(int id, int type);

    *//**
     * 修改帖子状态：0-正常; 1-精华; 2-拉黑;
     * @param id
     * @param status
     * @return
     *//*
    int updateStatus(int id, int status);

    *//**
     * 修改帖子分数
     * @param id
     * @param score
     * @return
     *//*
    int updateScore(int id, double score);*/
}
