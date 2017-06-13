package org.wq.spring.spring_mvc.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.wq.spring.spring_mvc.domain.DemoObj;

@Controller
public class MyConverterController {
	
	@RequestMapping(value="/convert", produces="application/x-wq")// 1 指定返回的媒体类型为自定义的媒体类型application/x-wq
	public @ResponseBody DemoObj convert(@RequestBody DemoObj demoObj){// 2 @RequestBody将请求的参数封装成DemoObj对象
		
		return demoObj;
	}
}
