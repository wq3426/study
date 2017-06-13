package com.wq.spring.basic.java_config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Java配置：通过 @Configuration 和 @Bean来实现
 * 
 * 使用原则：全局配置使用Java配置，业务Bean使用注解配置
 * @author wq3426
 *
 */
public class MainTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(JavaConfig.class);
		UseFunctionService useFunctionService = 
				context.getBean(UseFunctionService.class);
		
		System.out.println(useFunctionService.sayHello("wq3426"));
		
		context.close();
	}
}
