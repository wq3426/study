package com.wq.spring.basic.annotation_config;

import org.springframework.stereotype.Service;

@Service //1 @Service/@Component/@Controller/@Repository注解将类FunctionService注入到Spring容器中，由Spring容器来管理
public class FunctionService {
	
	public String sayHello(String word){
		return "Hello " + word + " !";
	}

}
