package com.wq.spring_boot.thymeleaf;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wq.spring_boot.thymeleaf.domain.Person;

/**
 * 1.如果Spring Boot提供的Spring MVC不符合要求，可以通过如下方式来添加自己的MVC配置
 * 方式一：一个配置类加上@EnableWebMvc注解来实现完全自己控制的MVC配置
 * 方式二：如果要保留Spring Boot提供的便利，又需要增加自己的额外配置，无需使用@EnableWebMvc注解，可以定义一个继承WebMvcConfigurerAdapter 
 *      或 实现接口WebMvcConfigurer的配置类然后重写它们的方法，这种方法重写不会覆盖Spring Boot的WebMvcAutoConfiguration的方法，
 *      可以让自己的配置和Spring Boot的自动配置同时有效。【推荐】
 * @author wq3426
 *
 */
@Controller
@SpringBootApplication
//@EnableWebMvc // 1 
public class MyApplication {
	
	@RequestMapping("/")
	public String index(Model model){
		Person single = new Person("aa", 11);
		
		List<Person> people = new ArrayList<Person>();
		Person p1 = new Person("xx",11);
		Person p2 = new Person("yy",22);
		Person p3 = new Person("zz",33);
		people.add(p1);
		people.add(p2);
		people.add(p3);
		
		model.addAttribute("singlePerson", single);
		model.addAttribute("people", people);
		
		return "index";
	}
	
	@Component
	public static class CustomServletContainer implements EmbeddedServletContainerCustomizer {

		@Override
		public void customize(ConfigurableEmbeddedServletContainer container) {
			container.setPort(8888);
			container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404.html"));
			container.setSessionTimeout(10, TimeUnit.MINUTES);
		}
		
	}
	
	public static void main(String[] args) {
		SpringApplication.run(MyApplication.class, args);
	}
}
