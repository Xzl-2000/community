package com.miki.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean//声明一个bean，这个bean将被spring容器装配管理
    //Kaptcah的核心类是一个接口——》Producer
    public Producer kaptchaProducer(){//方法名就是bean的名字
        Properties properties = new Properties();//为k-y结构，相当于一个map，为config传入数据
        //配置properties属性
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");//字号
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");//字颜色：黑
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");//字符范围
        properties.setProperty("kaptcha.textproducer.char.length", "4");//字符长度
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");//使用的干扰——》没有干扰

        DefaultKaptcha kaptcha = new DefaultKaptcha();
        //实例化它的实现类，需要向其传入一些配置参数，封装到config对象里
        Config config = new Config(properties);
        //config要求我们传入一个properties对象(里面都是k-y类型，相当于一个map)
        kaptcha.setConfig(config);
        //将config set给kaptcha主键

        return kaptcha;
    }

}
