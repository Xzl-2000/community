package com.miki.community.controller;

import com.miki.community.entity.DiscussPost;
import com.miki.community.entity.Page;
import com.miki.community.entity.User;
import com.miki.community.service.DiscussPostService;
import com.miki.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {//封装到page
        //在方法调用前，SpringMVC会自动实例化Model和Page,并将Page注入Model.
        //所以thymeleaf中可以直接访问Page对象中的数据.

        //服务器为page设置一些值i，总行数，路径，其他的值页面会处理
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPosts = new ArrayList<>();//将discuss_post和user二者组装放在一个新的集合里
        if (list != null) {
            for (DiscussPost post : list) {//遍历查出来的list
                Map<String, Object> map = new HashMap<>();//因为遍历的结果都要装到map里，所以先实例化一个map对象
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                discussPosts.add(map);//把map装到新的集合里
            }
        }

        model.addAttribute("discussPosts", discussPosts);//把结果传到model里
        return "/index";
    }
}