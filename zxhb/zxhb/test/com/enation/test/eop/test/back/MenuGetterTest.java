package com.enation.test.eop.test.back;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MenuGetterTest {
	
	@Before
	public void setup() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "spring/*.xml" });
	 
	}
	
	
	
	@Test
	public void testGetJson(){
//		String json = MenuJsonGetter.getMenuJson(1, 1);
//		//System.out.println(json);
	}
	
}
