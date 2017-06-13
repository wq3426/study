package com.wq.spring.high.aware;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 当在项目中需要用到Spring容器本身的功能资源时，需要用到Srping Aware
 * @author wq3426
 *
 */
public class MainTest {
	
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(AwareConfig.class);
		AwareService awareService = context.getBean(AwareService.class);
		awareService.outputResult();
		
		context.close();
	}
}
