package com.wq.spring.basic.bean.life;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * pom.xml中增加依赖 JSR250支持 Bean的初始化和销毁
 * @author wq3426
 *
 */
public class MainTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(PrePostConfig.class);
		BeanWayService beanWayService = context.getBean(BeanWayService.class);
		JSR250WayService jsr250WayService = context.getBean(JSR250WayService.class);
		
		context.close();
	}
}
