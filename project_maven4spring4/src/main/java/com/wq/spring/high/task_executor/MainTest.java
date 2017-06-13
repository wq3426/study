package com.wq.spring.high.task_executor;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 多线程
 * 
 * Spring通过任务执行器TaskExecutor来实现多线程和并发编程，使用ThreadPoolTaskExecutor可实现一个基于线程池的TaskExecutor
 * 实际开发中任务一般是非阻碍的，即异步的，所以我们要在配置类中通过@EnableAsync开启对异步任务的支持，并通过@Async注解来声明其是一个异步任务
 * @author wq3426
 *
 */
public class MainTest {
	
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(TaskExecutorConfig.class);
		AsyncTaskService asyncTaskService = context.getBean(AsyncTaskService.class);
		
		for(int i=0; i<10; i++){
			asyncTaskService.executeAsyncTask(i);
			asyncTaskService.executeAsyncTaskPlus(i);
		}
		
		context.close();
	}
}
