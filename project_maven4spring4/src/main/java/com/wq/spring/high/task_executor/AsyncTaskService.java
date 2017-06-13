package com.wq.spring.high.task_executor;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncTaskService {

	/**
	 * 通过该注解表明该方法是一个异步方法，如果注解在类级别，则该类所有方法都是异步方法
	 * 这里的方法自动被注入使用ThreadPoolTaskExecutor为实现类的TaskExecutor类
	 */
	@Async 
	public void executeAsyncTask(Integer i){
		System.out.println("执行异步任务："+i);
	}
	
	@Async
	public void executeAsyncTaskPlus(Integer i){
		System.out.println("执行异步任务+1："+(i+1));
	}
}
