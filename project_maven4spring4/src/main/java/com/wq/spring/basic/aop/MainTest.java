package com.wq.spring.basic.aop;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * pom.xml中增加aop依赖
 * @author wq3426
 *
 */
public class MainTest {
	
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(AopConfig.class);
		DemoAnnotationService demoAnnotationService = 
				context.getBean(DemoAnnotationService.class);
		DemoMethodService demoMethodService = 
				context.getBean(DemoMethodService.class);
		
		demoAnnotationService.add();
		
		demoMethodService.add();
		
		context.close();
	}
}
