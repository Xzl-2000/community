package com.miki.community;

import com.miki.community.dao.DiscussPostMapper;
import com.miki.community.dao.LoginTicketMapper;
import com.miki.community.dao.UserMapper;
import com.miki.community.entity.DiscussPost;
import com.miki.community.entity.LoginTicket;
import com.miki.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes=CommunityApplication.class)
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testSelectUser(){
        User user = userMapper.selectById(1);
        System.out.println(user);

        user = userMapper.selectByName("SYSTEM");
        System.out.println(user);

        user = userMapper.selectByEmail("system123@qq.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test123@qq.com");
        user.setHeaderUrl("http://www.nowcodeer.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdateUser(){

        int rows = userMapper.updateStatus(2, 1);
        System.out.println(rows);

        rows = userMapper.updateHeader(2,"http://com.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassword(2,"hello");
        System.out.println(rows);
    }

    @Test
    public void testSelectPosts(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0,0,10);
        for(DiscussPost post:list){
            System.out.println(post);
        }
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

    @Test
    public void testInsertLoginTicket() {
        //向loginTicket随便插数据进行测试
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus("abc", 1);
        LoginTicket loginTicket1 = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket1);
    }

    /*@Test
    public void testUpdatePreviousLoginTicket() {
        loginTicketMapper.updatePreviousLoginTicket("5bcd23c12bfa4c739b202e3661afdbb9", 180);
    }*/


}
