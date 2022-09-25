package com.miki.community.dao;

import com.miki.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {//把要用的方法声明好就行，编写配置文件，为每个方法提供对应的sql，mybatis会自动生成实现类

    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id,int status);

    int updateHeader(int id,String headerUrl);

    int updatePassword(int id,String password);
}
