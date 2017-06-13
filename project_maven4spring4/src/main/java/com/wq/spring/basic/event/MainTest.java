package com.wq.spring.basic.event;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * bean与bean之间的消息通信：当
 *   一个Bean处理完一个任务之后，希望另外一个Bean知道并能做出相应的处理，这时就需要让另外一个Bean监听当前Bean所发送的事件
 * @author wq3426
 *
 */
public class MainTest {
	
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = 
				new AnnotationConfigApplicationContext(EventConfig.class);
		DemoPublisher demoPublisher = context.getBean(DemoPublisher.class);
		demoPublisher.publish("hello application event");
		
		context.close();
	}
}
