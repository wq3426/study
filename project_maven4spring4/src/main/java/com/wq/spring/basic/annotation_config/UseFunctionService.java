package com.wq.spring.basic.annotation_config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UseFunctionService {
	@Autowired //1 按照byType的方式将FunctionService注入到类UseFunctionService当中
	FunctionService functionService;
	
	public String sayHello(String word){
		return functionService.sayHello(word);
	}
}
