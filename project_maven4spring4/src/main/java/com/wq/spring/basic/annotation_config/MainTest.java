package com.wq.spring.basic.annotation_config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Service/@Component/@Controller/@Repository注解将类FunctionService注入到Spring容器中，由Spring容器来管理
 * 
 * @Configuration //1 声明当前类是一个配置类
 * @ComponentScan("com.wq.spring.basic.annotation_config")//2 自动扫描包名下的注解类并实例化注入到Spring容器中
 * @author wq3426
 *
 */
public class MainTest {
	
	public static void main(String[] args) {
		//1 使用AnnotationConfigApplicationContext作为Spring容器，接受输入一个配置类作为参数
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(DiConfig.class);
		//2 获得注解声明配置的UseFunctionService的Bean实例
		UseFunctionService useFunctionService = context.getBean(UseFunctionService.class);
		
		System.out.println(useFunctionService.sayHello("wq"));
		
		context.close();
	}
}
