package com.wq.spring.high.task_scheduler;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 计划任务 
 * @author wq3426
 *
 */
public class MainTest {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(TaskSchedulerConfig.class);
	}
}
