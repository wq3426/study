package org.wq.spring.spring_mvc.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.wq.spring.spring_mvc.web.interceptor.DemoInterceptor;
import org.wq.spring.spring_mvc.web.message_converter.MyMessageConverter;

/**
 * 1.@EnableWebMvc 开启Spring MVC支持
 * 2.继承类WebMvcConfigurerAdapter 或 实现接口WebMvcConfigurer
 *   该继承是为了重写其方法来使用Spring MVC的定制配置，如不用，可以不继承
 */
@Configuration
@EnableWebMvc // 1
@EnableScheduling // 11 开启计划任务支持
@ComponentScan("org.wq.spring.spring_mvc")
public class MyMvcConfig extends WebMvcConfigurerAdapter{// 2

	/**
	 * 配置Spring MVC 的视图解析
	 * @return
	 */
	@Bean
	public InternalResourceViewResolver viewResolver(){
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/classes/views/");
		viewResolver.setSuffix(".jsp");
		viewResolver.setViewClass(JstlView.class);
		
		return viewResolver;
	}

	/**
	 * 3.静态资源映射 addResourceLocations指的是文件放置的目录，addResourceHandler指的是对外暴露的访问路径
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/assets/");// 3
	}
	
	/**
	 * 4.配置拦截器类的Bean
	 * @return
	 */
	@Bean// 4
	public DemoInterceptor demoInterceptor(){
		return new DemoInterceptor();
	}

	/**
	 * 5.重写WebMvcConfigurerAdapter的addInterceptors()方法，注册自定义拦截器DemoInterceptor
	 */
	@Override // 5 
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(demoInterceptor());
	}

	/**
	 * 6.快捷的ViewController 
	 * 如果一个控制器类（如HelloController）只是简单的页面跳转，可通过在配置类中重写WebMvcConfigurerAdapter的addViewControllers()方法
	 * 添加ViewController来代替代
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/index2").setViewName("index2");// 6
		registry.addViewController("/toUpload").setViewName("upload");// 8 添加跳转到文件上传页面的视图控制器
		registry.addViewController("/converter").setViewName("converter");// 9 添加跳转到自定义converter页面的视图控制器
		registry.addViewController("/sse").setViewName("sse");// 10 添加跳转到自定义SSE Demo页面的视图控制器
		registry.addViewController("/async").setViewName("async");// 12 添加跳转到servlet3.0+ async支持页面的视图控制器
	}

	/**
	 * 7.不忽略请求参数中带点的参数：在Spring MVC 中路径参数如果带"."的话，"."后面的值将被忽略
	 * 
	 * 如果请求参数为"xx.yy"，后台将只能得到"xx"
	 * 如：http://localhost:8080/mvc/anno/pathvar/xx.yy 将得到str:xx
	 * 
	 * 如果想要不忽略"."后面的参数，可通过重写WebMvcConfigurerAdapter的configurePathMatch()方法
	 * 如：http://localhost:8080/mvc/anno/pathvar/xx.yy 此时将得到str：xx.yy
	 */
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUseSuffixPatternMatch(false);// 7
	}
	
	/**
	 * 8.配置MultipartResolver 用来上传文件
	 * @return
	 */
	@Bean
	public MultipartResolver multipartResolver(){// 8
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(1000000);//设置上传文件的最大size，以字节为单位
		
		return multipartResolver;
	}

	/**
	 * 配置自定义的HttpMessageConverter的Bean
	 * @return
	 */
	@Bean
	public MyMessageConverter myMessageConverter(){
		return new MyMessageConverter();
	}
	
	/**
	 * 9.将自定义的HttpMessageConverter的Bean注册到Spring MVC 里，此处通过方法2实现
	 * 该注册有两个方法：
	 * 方法1：重写configureMessageConverters()方法，会覆盖掉Spring MVC 默认注册的多个HttpMessageConverter
	 * 方法2：重写extendMessageConverters()方法，仅添加一个自定义的HttpMessageConverter，不会覆盖默认注册的HttpMessageConverter
	 */
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {// 9
		converters.add(myMessageConverter());
	}
	
	
}
