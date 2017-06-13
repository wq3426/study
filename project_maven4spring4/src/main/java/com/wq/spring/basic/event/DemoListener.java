package com.wq.spring.basic.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
//1 自定义事件监听器  实现ApplicationListener接口，并制定监听的事件类型
public class DemoListener implements ApplicationListener<DemoEvent> {

	//2 使用onApplicationEvent方法对消息进行接受处理
	@Override
	public void onApplicationEvent(DemoEvent event) {
		// TODO Auto-generated method stub
		String msg = event.getMsg();
		
		System.out.println("我（bean-demoListener）接收到了bean-demoPublisher发布的消息："+msg);
	}

}
