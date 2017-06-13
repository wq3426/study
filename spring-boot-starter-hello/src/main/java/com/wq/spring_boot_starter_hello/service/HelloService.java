package com.wq.spring_boot_starter_hello.service;

/**
 * 根据此类是否存在来创建这个类的Bean，这个类可以是第三方类库的类
 * @author wq3426
 *
 */
public class HelloService {
	private String msg;
	
	public String sayHello(){
		return "Hello " + msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
