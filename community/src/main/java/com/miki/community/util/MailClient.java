package com.miki.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Autowired
    private JavaMailSender mailSender;//把组件调入进来

    @Value("${spring.mail.username}")
    private String from;//发件人

    //封装一个公有的方法给外界调用
    public void sendMail(String to,String subject,String content){//利用JavaMailSender组件实现发邮件的功能
    //to 发送对象，subject 邮件主题，content 邮件内容
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);//不要漏
            helper.setSubject(subject);
            helper.setText(content,true);//后面true表示html文本而不是默认的普通文字
            mailSender.send(helper.getMimeMessage());//发送

        } catch (MessagingException e) {
            logger.error("发送邮件失败："+e.getMessage());//出现异常记录日志
        }

    }

}
