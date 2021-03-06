package com.wq.spring_boot_starter_hello.auto_config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wq.spring_boot_starter_hello.domain.HelloServiceProperties;
import com.wq.spring_boot_starter_hello.service.HelloService;

@Configuration
@EnableConfigurationProperties(HelloServiceProperties.class)
@ConditionalOnClass(HelloService.class)
@ConditionalOnProperty(prefix="hello", value="enabled", matchIfMissing=true)
public class HelloServiceAutoConfiguration {
	
	@Autowired
	private HelloServiceProperties helloServiceProperties;
	
	@Bean
	@ConditionalOnMissingBean(HelloService.class)
	public HelloService helloService(){
		HelloService helloService = new HelloService();
		helloService.setMsg(this.helloServiceProperties.getMsg());
		
		return helloService;
	}
}
