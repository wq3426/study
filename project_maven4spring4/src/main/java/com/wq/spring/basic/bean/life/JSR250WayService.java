package com.wq.spring.basic.bean.life;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class JSR250WayService {

	@PostConstruct //1 在构造函数执行完之后执行
	public void init(){
		System.out.println("jsr250-init-method");
	}
	
	public JSR250WayService(){
		super();
		System.out.println("初始化构造函数-JS250WayService");
	}
	
	@PreDestroy //2 在Bean销毁之前执行
	public void destroy(){
		System.out.println("jsr250-destroy-method");
	}
}
