package com.wq.spring.basic.aop;

import org.springframework.stereotype.Service;

@Service
public class DemoMethodService {
	
	public void add(){
		System.out.println("method add()");
	}
}
