package com.wq.spring_boot.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 1.【类型安全的配置】
 * 使用@Value注入每个配置在实际项目中会显得格外麻烦，所以Spring Boot还提供了基于类型安全的配置方式
 * 通过@ConfigurationProperties将properties属性和一个Bean及其属性关联，从而实现类型安全的配置
 * - locations 指定.properties文件的位置
 * - prefix 指定properties配置的前缀
 * @author wq3426
 *
 */
@Component
@ConfigurationProperties(locations="classpath:config/app.properties", prefix = "author")// 1
public class AuthorSettings {
	private String name;
	private Long age;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getAge() {
		return age;
	}
	public void setAge(Long age) {
		this.age = age;
	}
}
