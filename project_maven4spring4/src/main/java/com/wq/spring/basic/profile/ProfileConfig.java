package com.wq.spring.basic.profile;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class ProfileConfig {

	@Bean
	@Profile("dev")//1 Profile 为dev时实例化的Bean
	public DemoBean devDemoBean(){
		return new DemoBean("from development profile");
	}
	
	@Bean
	@Profile("prod")//2 Profile 为prod时实例化的Bean
	public DemoBean prodDemoBean(){
		return new DemoBean("from production profile");
	}
}
