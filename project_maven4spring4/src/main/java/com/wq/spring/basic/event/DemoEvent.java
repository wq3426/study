package com.wq.spring.basic.event;

import org.springframework.context.ApplicationEvent;

/**
 * 自定义事件
 * @author wq3426
 */
public class DemoEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;
	private String msg;
	
	public DemoEvent(Object source, String msg) {
		super(source);
		// TODO Auto-generated constructor stub
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
