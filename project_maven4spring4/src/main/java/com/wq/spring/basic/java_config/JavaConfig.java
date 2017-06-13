package com.wq.spring.basic.java_config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Java配置：通过 @Configuration 和 @Bean来实现
 * 
 * 使用原则：全局配置使用Java配置，业务Bean使用注解配置
 * @author wq3426
 *
 */

@Configuration //1 使用 @Configuration 定义配置类，这意味着这个类里可能有0个或多个 @Bean 注解，此处没有用包扫描，因为所有的Bean都在此类中定义
public class JavaConfig {
	
	@Bean //2 使用 @Bean 声明当前方法的返回值是一个Bean，Bean的名称是方法名
	public FunctionService functionService(){
		return new FunctionService();
	}
	
	@Bean
	public UseFunctionService useFunctionService(){
		UseFunctionService useFunctionService = new UseFunctionService();
		// 3 注入Bean的时候直接调用方法
		useFunctionService.setFunctionService(functionService());
		return useFunctionService;
	}
	
	@Bean //4 另外一种注入方式：直接将Bean作为参数传入方法（在Spring中，只要容器中存在这个Bean，就可以在另外一个Bean的声明方法中作为参数写入）
	public UseFunctionService useFunctionService(FunctionService functionService){
		UseFunctionService useFunctionService = new UseFunctionService();
		useFunctionService.setFunctionService(functionService);
		return useFunctionService;
	}
}
