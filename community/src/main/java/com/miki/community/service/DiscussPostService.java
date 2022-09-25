package com.miki.community.service;

import com.miki.community.dao.DiscussPostMapper;
import com.miki.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit){
        //这里得到了userid，但是我们要展示的是username，所以应该再有一个根据id查询user的方法
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }
    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
