package com.miki.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication//这个注解标识的类表示这是个配置文件
public class CommunityApplication {
//自动创建了spring容器，自动扫描配置类所在的包以及子包下的类
	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}
