package com.miki.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes=CommunityApplication.class)//该注解启用CommunityApplication类为配置类
class CommunityApplicationTests implements ApplicationContextAware {//哪个类想得到spring容器，就实现这个接口

	private ApplicationContext applicationContext;//定义一个变量将spring容器暂存下来,程序启动时其他地方就能使用spring容器

//重写这个set方法,这个ApplicationContext就是spring容器
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Test
	public void testApplication(){
		System.out.println(applicationContext);
	}
}