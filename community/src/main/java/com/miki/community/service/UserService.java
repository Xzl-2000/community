package com.miki.community.service;

import com.miki.community.dao.UserMapper;
import com.miki.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findUserById(int Id){
        return userMapper.selectById(Id);
    }
}
