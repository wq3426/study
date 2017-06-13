package com.wq.spring.high.integration_test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 需要在pom.xml中增加Spring test 支持juint的配置 
 * @author wq3426
 */

//在JUnit环境下提供Spring TestContext Framework 的功能
@RunWith(SpringJUnit4ClassRunner.class)
//加载配置ApplicationContext, class属性用来加载配置类
@ContextConfiguration(classes = {TestConfig.class})
//声明活动的profile
@ActiveProfiles("prod")
public class DemoBeanIntegrationTests {
	@Autowired
	private TestBean testBean;
	
	@Test
	public void prodBeanShouldInject(){
		String expected = "from production profile";
		String actual = testBean.getContent();
		Assert.assertEquals(expected, actual);
	}
}
