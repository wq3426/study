package com.wq.spring.basic.aop;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("com.wq.spring.basic.aop")
@EnableAspectJAutoProxy //1 开启Spring对AspectJ代理的支持
public class AopConfig {
	
}
