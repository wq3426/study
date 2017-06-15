package com.enation.test.page;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.enation.eop.resource.IThemeManager;
import com.enation.eop.resource.model.Theme;
import com.enation.framework.test.SpringTestSupport;

public class ThemeTest extends SpringTestSupport {
	
	
	
	@Before
	public void mock(){
	}
	
	@Test
	public void themeGetTest(){
		
		IThemeManager themeManager  = this.getBean("themeManager");
		Theme theme = themeManager.getTheme( 1);
		assertEquals(theme.getPath(),"default");
	}
	
	
}
