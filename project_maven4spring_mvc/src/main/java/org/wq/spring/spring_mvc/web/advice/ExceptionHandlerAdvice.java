package org.wq.spring.spring_mvc.web.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * 通过@ControllerAdvice 可以将对于控制器的全局配置放在同一个位置，注解了@Controller的类的方法可使用 
 * @ExceptionHandler @InitBinder @ModelAttribute 注解到方法上，这对所有注解了@RequestMapping的控制器内的方法有效
 * @author wq3426
 *
 */

@ControllerAdvice // 1 声明一个控制器建言，该注解组合了@Component,会自动注册为Bean
public class ExceptionHandlerAdvice {

	@ExceptionHandler(value=Exception.class)// 2  定义全局异常处理,value属性可过滤拦截的条件
	public ModelAndView exception(Exception exception, WebRequest request){
		ModelAndView modelAndView = new ModelAndView("error");//error页面
		modelAndView.addObject("errorMessage", exception.getMessage());
		
		return modelAndView;
	}
	
	@ModelAttribute // 3 该注解可将键值对添加到全局，所有添加@RequestMapping注解的方法可获得此键值对
	public void addAttributes(Model model){
		model.addAttribute("msg", "额外信息");
	}
	
	@InitBinder // 4 该注解可定制WebDataBinder，WebDataBinder用来自动绑定前台请求参数到Model中
	public void initBinder(WebDataBinder webDataBinder){
//		webDataBinder.setDisallowedFields("id");// 5 此处指定忽略前台request请求参数中的id，使用debug调试可看到id属性值为null
	}
}
