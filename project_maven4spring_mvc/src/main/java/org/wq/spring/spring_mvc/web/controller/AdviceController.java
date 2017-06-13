package org.wq.spring.spring_mvc.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wq.spring.spring_mvc.domain.DemoObj;

@Controller
public class AdviceController {

	/**
	 * 1.通过@ModelAttribute("msg") 获取ExceptionHandlerAdvice中方法addAttributes()设置的Model的属性“msg”的值
	 * @param msg
	 * @param obj
	 * @return
	 */
	@RequestMapping("/advice")
	public String getSomething(@ModelAttribute("msg") String msg, DemoObj obj){// 1
		throw new IllegalArgumentException("非常抱歉，参数有误/" + "来自@ModelAttribute:" + msg);
	}
}
