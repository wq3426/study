package org.wq.spring.spring_mvc.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Web 配置
 * WebApplicationInitializer Spring提供的配置Servlet3.0+配置的接口，用来替代web.xml文件
 * 实现此接口将会自动被SpringServletContainerInitializer获取到（用来启动Servlet3.0容器）
 * @author wq3426
 */
public class WebInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		// TODO Auto-generated method stub
		
		//新建WebApplicationContext，注册配置类，并将其和当前servletContext关联
		AnnotationConfigWebApplicationContext ctx = 
				new AnnotationConfigWebApplicationContext();
		ctx.register(MyMvcConfig.class);
		ctx.setServletContext(servletContext);
		
		//注册Spring MVC的DispatcherServlet
		Dynamic servlet = servletContext.addServlet("dispatcher", new DispatcherServlet(ctx));
		servlet.addMapping("/");
		servlet.setLoadOnStartup(1);
		servlet.setAsyncSupported(true);//开启servlet3.0+的异步方法支持
	}

}
