package com.miki.community.service;

import com.miki.community.dao.LoginTicketMapper;
import com.miki.community.dao.UserMapper;
import com.miki.community.entity.LoginTicket;
import com.miki.community.entity.User;
import com.miki.community.util.CommunityConstant;
import com.miki.community.util.CommunityUtil;
import com.miki.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {//让激活的类实现这个常量接口
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")//域名
    private String domain;

    @Value("${server.servlet.context-path}")//项目名
    private String contextPath;

    @Autowired
    private LoginTicketMapper loginTicketMapper;//登入凭证

    public User findUserById(int Id){
        return userMapper.selectById(Id);
    }

    //注册用户

    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();
        //map用来记录返回参数出现的问题，若为空则没有问题

        //进行判断，空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        //验证账号
        User user1 = userMapper.selectByName(user.getUsername());
        if (user1 != null) {
            map.put("usernameMsg", "账号已存在");
            return map;
        }

        //验证邮箱
        User user2 = userMapper.selectByEmail(user.getEmail());
        if (user2 != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));//截取只要5位
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());//设置激活码
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));//给一个随机头像，用随机数替代占位符%d
        user.setCreateTime(new Date());
        userMapper.insertUser(user);//添加到库里，MyBatis获取id回填

        //激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());//向context内添加数据，k-y结构
        //URL格式 http:localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        //用模板引擎生成html格式内容
        String content = templateEngine.process("/mail/activation", context);
        //发送邮件
        mailClient.sendMail(user.getEmail(), "账号激活", content);

        return map;
    }

    //激活方法
    public int activation(int userId,String code){
        User user = userMapper.selectById(userId);//根据id找到用户对象，根据status属性判断是否激活
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);//如果激活码一致，则更改账号状态为1（即激活）
            //clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    //用户登录
    public Map<String, Object> login(String username, String password, long expiredSeconds) {
        //用户名，密码，凭证生效时间
        Map<String, Object> map = new HashMap<>();

        //空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }

        //验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在");
            return map;
        }

        //验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活");
            return map;
        }

        //验证密码，这里的密码是明文密码，需要加入salt后加密再进行比较（md5对同一串字符加密结果是一样的）
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码错误");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();//创建实体
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());//生成随机字符串
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));//设置有效时间，当前时间+往后推秒数（*1000是换算ms为s）
        loginTicketMapper.insertLoginTicket(loginTicket);
//      String redisKey = RedisKeyUtils.getTicketKey(loginTicket.getTicket());
//      redisTemplate.opsForValue().set(redisKey, loginTicket);

        //把ticket放入map后保存
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    //退出登录
    public void logout(String ticket) {
        loginTicketMapper.updateStatus(ticket, 1);
//        String redisKey = RedisKeyUtils.getTicketKey(ticket);
//        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
//        loginTicket.setStatus(1);
//        redisTemplate.opsForValue().set(redisKey, loginTicket);
    }

    //忘记密码

    //给邮箱发送验证码
    public Map<String, Object> getCode(String email) {
        Map<String, Object> map = new HashMap<>();
        //空值判断
        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }
        //邮箱是否绑定账号
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            map.put("emailMsg", "该邮箱没有绑定账号");
            return map;
        }
        //验证账号状态是否激活
        if (user.getStatus()==0) {
            map.put("emailMsg", "此邮箱绑定的账号还未激活，请先进行激活！");
            return map;
        }
        //邮箱输入正常情况下，发送验证码到输入的邮箱

        Context context = new Context();
        //向context内添加数据，k-y结构，需要传入邮箱email和验证码code
        context.setVariable("email", email);
        //生成验证码
        String code = CommunityUtil.generateUUID().substring(0, 6);
        context.setVariable("code",code);

        //用模板引擎生成html格式内容
        String content = templateEngine.process("/mail/forget", context);
        //发送邮件
        mailClient.sendMail(email, "Miki社区验证码", content);

        map.put("code",code);//往map中存放一个code，用于与用户输入的验证码进行比较
        //添加一个验证码的过期时间
        map.put("expirationTime", LocalDateTime.now().plusMinutes(2L));

        return map;
    }

    //重置密码
    public Map<String,Object> resetPassword(String email, String password) {
        Map<String, Object> map = new HashMap<>();
        //空值处理
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","请输入新的密码！");
            return map;
        }
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            return map;
        }
        //重置密码
        userMapper.updatePassword(user.getId(), CommunityUtil.md5(password + user.getSalt()));
        //clearCache(user.getId());
        return map;
    }

}
