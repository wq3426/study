package com.wq.spring.high.conditional;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 通过@Conditional(WindowsCondition.class)  实例化符合条件的接口实现类
 * @author wq3426
 *
 */
public class MainTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(ConditionConfig.class);
		ListService listService = context.getBean(ListService.class);
		
		System.out.println(context.getEnvironment().getProperty("os.name") 
				           +"系统下的列表命令为："
				           +listService.showListCmd());
	}
}
