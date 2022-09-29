package com.miki.community.controller;

import com.google.code.kaptcha.Producer;
import com.miki.community.entity.User;
import com.miki.community.service.UserService;
import com.miki.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    //声明日志
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;//把服务注入

    @Autowired
    private Producer kaptchaProducer;//把验证码的核心类注入

    @Value("${server.servlet.context-path}")
    private String contextPath;//项目地址

    //访问注册页面的请求
    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    //访问登入页面的请求
    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    //注册账号
    @RequestMapping(path ="/register",method = RequestMethod.POST)
    public String getRegisterPage(Model model, User user){//SpringMVC会根据同名原则把user信息传给user
        Map<String, Object> map = userService.register(user);
        if(map==null||map.isEmpty()){//注册成功
            model.addAttribute("msg","注册成功，我们已经为您发送一封激活邮件，请尽快完成激活！");
            model.addAttribute("target","/index");//设置8s后最终跳转目标
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    //激活，URL格式 http:localhost:8080/community/activation/101/code
    @RequestMapping(path = "/actvation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功！您现在就可以使用账号登录了！");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "此账号已被激活过，请勿重复激活！");
            model.addAttribute("target", "/index");
        }
        else {
            model.addAttribute("msg", "激活失败！请提供正确激活码！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    //生成验证码
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session) {//用response手动输出图片，把验证码存到session
        //生成验证码
        String text = kaptchaProducer.createText();
        //生成图片
        BufferedImage image = kaptchaProducer.createImage(text);
        //将验证码存入session
        session.setAttribute("kaptcha", text);
        //将图片传给浏览器
        response.setContentType("image/png");/*声明给浏览器返回什么类型的数据*/
        try {
            OutputStream outputStream = response.getOutputStream();/*获取输出字符流*/
            ImageIO.write(image, "png", outputStream);/*向浏览器输出图片工具*/
        } catch (IOException e) {
            //throw new RuntimeException("读取验证码图片失败，服务器发生异常", e);
            logger.error("响应验证码失败：" + e.getMessage());
        }
    }

    //登录，/login地址一样，方法不一样可以
    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username, String password, String code, boolean rememberme/*是否勾选记住我*/,
                        Model model, HttpSession session, HttpServletResponse response/*存放cookie*/
                       /* @CookieValue(value = "kaptchaOwner", required = false) String kaptchaOwner*/) {
        //首先判断验证码是否正确，空的或者不相等都不对
        String kaptcha = (String) session.getAttribute("kaptcha");
        /*String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtils.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }*/
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码错误！");
            return "/site/login";
        }

        //检查账号，密码

        //根据是否 记住我 传入生效时间
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECOND : DEFAULT_EXPIRED_SECOND;
        //接收调用service后返回的map
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        //判断map里是否包含ticket
        if (map.containsKey("ticket")) {
            //如果ticket存在，则创建一个cookie携带ticket给浏览器,map.get("ticket")得到一个对象，而参数要求为String类型，故toString
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            //错误时返回登录页面，并显示错误信息
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    //退出登录
    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket" ) String ticket) {
        if (ticket != null) {
            userService.logout(ticket);
        }
        return "redirect:/index";
    }

    //忘记密码
    @RequestMapping(path = "/forget",method = RequestMethod.GET)
    public String getForgetPage(){
        return "/site/forget";
    }

    //忘记密码后获取验证码
    @RequestMapping(path = "/getCode",method = RequestMethod.GET)
    public String getCode(String email,Model model,HttpSession session){
        //email接收邮箱，session存放验证码code
        Map<String, Object> map = userService.getCode(email);
        if(map.containsKey("emailMsg")/*containsKey用于判断map中是否包含指定的键名*/){
            model.addAttribute("emailMsg",map.get("emailMsg"));
        }else{//邮箱正常已发送验证码
              //告诉用户验证码已发送，将code存到session中
            model.addAttribute("msg","验证码已发送到您的邮箱！");
            session.setAttribute("code",map.get("code"));
            //后序判断用户输入验证码的时候验证码是否过期
            session.setAttribute("expirationTime",map.get("expirationTime"));
        }
        return "/site/forget";
    }

    //重置密码
    @RequestMapping(path = "/forget",method = RequestMethod.POST)
    public String forget(Model model,String email,String verifycode,HttpSession session,String password){
        //判断是否输入验证码
        if(StringUtils.isBlank(verifycode)){
            model.addAttribute("codeMsg","请输入验证码！");
            return "/site/forget";
        }
        //判断验证码是否正确
        if(!verifycode.equals(session.getAttribute("code"))){
            model.addAttribute("codeMsg","输入的验证码不正确！");
            return "/site/forget";
        }
        //判断验证码是否在有效时间内
        //通过格式化日期LocalDateTime进行比较
        if(LocalDateTime.now().isAfter((LocalDateTime)session.getAttribute("expirationTime"))){
            model.addAttribute("codeMsg","该验证码已失效，请重新获取验证码！");
            return "/site/forget";
        }
        Map<String,Object> map = userService.resetPassword(email,password);
        //修改成功
        if(map==null||map.isEmpty()){
            model.addAttribute("msg","修改密码成功，请使用新的密码进行登入！");
            //跳转到operate-result界面,传目的地址/login
            model.addAttribute("target","/login");
            return "/site/operate-result";
        }else{
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/forget";
        }
    }

}
