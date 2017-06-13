package com.wq.spring_boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wq.spring_boot.domain.AuthorSettings;
import com.wq.spring_boot_starter_hello.service.HelloService;

/**
 * 1.@SpringBootApplication Spring Boot项目的核心注解，用于开启自动配置
 * 该注解包含了 @SpringBootConfiguration @EnableAutoConfiguration @ComponentScan 等注解
 * Spring Boot会自动扫描@SpringBootApplication所在类的同级包以及下级包里的Bean，建议该入口类放置在顶层
 * 
 * @EnableAutoConfiguration 让Spring Boot根据类路径中的jar包依赖为当前项目进行自动配置
 * 
 * 如果要关闭特定的自动配置，可以使用参数exclude，例如：
 * @SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
 * 
 * @author wq3426
 *
 */
@RestController
@SpringBootApplication// 1 开启自动配置
//@ImportResource({"classpath:some-context.xml","classpath:another-context.xml"})// 5 如有需要，可加载xml配置文件
public class MySpringBootApplication {// 2 入口类
	@Value("${book.author}")
	private String bookAuthor;
	@Value("${book.name}")// 6 【常规属性配置】可以通过@Value注解获取在application.properties文件中定义的属性的值
	private String bookName;
	@Autowired
	private AuthorSettings authorSettings;// 7 【类型安全的配置】
	@Autowired
	private HelloService helloService;// 8 自定义的自动配置包spring-boot-starter-hello，自动注入HelloService的Bean

	@RequestMapping("/")
	public String index(){
		return "Hello Spring Boot";
	}
	
	@RequestMapping("/hello")
	public String hello(){
		return helloService.sayHello();
	}
	
	@RequestMapping("/bookinfo")
	public String bookInfo(){
		return "book name is: "+ bookName + " and book author is: " + bookAuthor;
	}
	
	@RequestMapping("/authorinfo")
	public String authorInfo(){
		return authorSettings.getName() +"---"+ authorSettings.getAge();
	}
	
	public static void main(String[] args) {// 3 main方法： 项目启动的入口，运行后，可在浏览器输入http://localhost:8080/,查看访问结果
		SpringApplication.run(MySpringBootApplication.class, args);// 4 通过入口类启动Spring Boot项目
	}
}
