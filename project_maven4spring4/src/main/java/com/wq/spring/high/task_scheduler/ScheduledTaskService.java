package com.wq.spring.high.task_scheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	@Scheduled(fixedRate = 5000)//声明计划任务，每隔5秒执行一次
	public void reportCurrentTime(){
		System.out.println("每隔五秒执行一次 " + dateFormat.format(new Date()));
	}
	
	@Scheduled(cron = "0 43 16 ? * *")//cron可按指定时间执行，该语法为linux下的定时任务语法
	public void fixTimeExecution(){
		System.out.println("在指定时间 " + dateFormat.format(new Date()) + "执行");
	}
}
