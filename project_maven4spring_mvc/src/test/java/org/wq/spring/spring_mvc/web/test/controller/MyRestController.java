package org.wq.spring.spring_mvc.web.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wq.spring.spring_mvc.web.service.DemoService;

@RestController
public class MyRestController {
	@Autowired
	DemoService demoService;
	
	@RequestMapping(value="/testRest", produces="text/plain;charset=UTF-8")
	public String testRest(){
		return demoService.saySomething();
	}
}
