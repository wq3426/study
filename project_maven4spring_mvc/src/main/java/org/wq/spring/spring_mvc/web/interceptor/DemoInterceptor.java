package org.wq.spring.spring_mvc.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 1.自定义拦截器实现：继承类HandlerInterceptorAdapter 或 实现接口 HandlerInterceptor
 * 2.preHandle() 在请求发生前执行
 * 3.postHandle() 在请求完成后执行
 */
public class DemoInterceptor extends HandlerInterceptorAdapter {// 1

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
			throws Exception {// 2
		long startTime = System.currentTimeMillis();
		request.setAttribute("startTime", startTime);
		
		System.out.println("startTime " + startTime);
		
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) 
			throws Exception {// 3
		long startTime = (Long) request.getAttribute("startTime");
		request.removeAttribute("startTime");
		long endTime = System.currentTimeMillis();
		System.out.println("本次请求处理时间为：" + new Long(endTime - startTime) + "ms");
		request.setAttribute("handlingTime", endTime - startTime);
		
		System.out.println("handlingTime " + (endTime - startTime));
	}

}
