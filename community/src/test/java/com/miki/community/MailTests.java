package com.miki.community;

import com.miki.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes=CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;//将组件注入

    @Autowired
    private TemplateEngine templateEngine;//Spring管理的模板引擎

    @Test
    public void testTextMail(){
        mailClient.sendMail("xiongzilong321@sina.com","Test","Welcome!");
    }

    @Test
    public void testHttpMail(){
        Context context = new Context();//给模板传参
        context.setVariable("username","Sunday");
        //调用模板引擎生成动态网页，content就是内容
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);
        //发送邮件
        mailClient.sendMail("xiongzilong321@sina.com","HTML",content);
    }
}
