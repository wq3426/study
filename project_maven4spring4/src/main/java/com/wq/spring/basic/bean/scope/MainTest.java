package com.wq.spring.basic.bean.scope;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 默认为Singleton，相当于@Scope("singleton")
 * @Scope("prototype")//1 每次调用新建一个Bean实例
 * @author wq3426
 *
 */
public class MainTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(ScopeConfig.class);
		
		DemoSingletonService s1 = context.getBean(DemoSingletonService.class);
		DemoSingletonService s2 = context.getBean(DemoSingletonService.class);
		
		System.out.println("s1与s2是否相等"+s1.equals(s2));
		
		DemoPrototypeService p1 = context.getBean(DemoPrototypeService.class);
		DemoPrototypeService p2 = context.getBean(DemoPrototypeService.class);
		
		System.out.println("p1与p2是否相等"+p1.equals(p2));
	}
}
