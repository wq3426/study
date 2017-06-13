package org.wq.spring.spring_mvc.web.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.wq.spring.spring_mvc.config.MyMvcConfig;
import org.wq.spring.spring_mvc.web.service.DemoService;

/**
 * 1.声明加载的ApplicationContext是一个WebApplicationContext,它的属性指定的是Web资源的位置，默认为"src/main/webapp",本例修改为"src/main/resources"
 * @author wq3426
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MyMvcConfig.class})
@WebAppConfiguration("src/main/resources")// 1 
public class ControllerIntegrationTests {
	private MockMvc mockMvc;// 2 模拟MVC对象，通过MockMvcBuilders.webAppContextSetup(this.wac).build()初始化
	
	@Autowired
	private DemoService demoService;
	
	@Autowired
	WebApplicationContext wac;
	
	@Autowired
	MockHttpSession session;// 3 可注入模拟的http session，此处仅演示，未使用
	
	@Autowired
	MockHttpServletRequest request;
	
	@Before// 4 测试开始前，初始化mockMvc
	public void setup(){
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}
	
	@Test
	public void testNormalController() throws Exception{
		mockMvc.perform(get("/normal"))// 5 模拟向/normal发送get请求
			   .andExpect(status().isOk())// 6 预期返回状态为200
			   .andExpect(view().name("page"))// 7 预期view名称为page
			   .andExpect(forwardedUrl("/WEB-INF/classes/views/page.jsp"))// 8 预期页面跳转路径为 /WEB-INF/classes/views/page.jsp
			   .andExpect(model().attribute("msg", demoService.saySomething()));// 9 预期model里的值是demoService.saySomething()返回值"hello"
	}
	
	@Test
	public void testRestController() throws Exception{
		mockMvc.perform(get("/testRest"))
		       .andExpect(status().isOk())
		       .andExpect(content().contentType("text/plain;charset=UTF-8"))// 10 预期返回值的媒体类型为"text/plain;charset=UTF-8"
		       .andExpect(content().string(demoService.saySomething()));// 11 预期返回值的内容为demoService.saySomething()返回值"hello"
	}
	
}
