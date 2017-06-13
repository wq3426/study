package org.wq.spring.spring_mvc.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller //声明一个控制器
public class HelloController {

	@RequestMapping("/index") //配置URL和方法之间的映射
	public String hello(){
		
		//Maven的页面一般放在src/main/webapp/WEB-INF下，此处"src/main/resources"是按照SpringBoot的放置习惯来放置页面的
		return "index"; //匹配返回的路径，添加Config类中viewResolver设置的前缀和后缀：/WEB-INF/classes/views/index.jsp
	}
}
