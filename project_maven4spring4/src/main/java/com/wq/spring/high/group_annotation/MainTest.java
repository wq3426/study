package com.wq.spring.high.group_annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 使用自定义的注解@WqConfiguration
 * @author wq3426
 *
 */
public class MainTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(DemoConfig.class);
		DemoService demoService = context.getBean(DemoService.class);
		demoService.outputResult();
		
		context.close();
	}
}
