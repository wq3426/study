package com.wq.spring.basic.profile;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 测试未通过
 * @author wq3426
 *
 */
public class MainTest {
	
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(ProfileConfig.class);
		//1 设置当前的Profile
		context.getEnvironment().setActiveProfiles("dev");
		//2 注册bean配置类，不然会报错
		context.register(ProfileConfig.class);
		//刷新容器
		context.refresh();
		
		DemoBean demoBean = context.getBean(DemoBean.class);
		
		System.out.println(demoBean.getContent());
		
		context.close();
	}
}
