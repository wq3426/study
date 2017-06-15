package com.enation.test.eop.utils;

import org.junit.Test;

import com.enation.eop.sdk.utils.EopUtil;
import com.enation.framework.util.FileUtil;

public class EopUtilTest {

	private static final String PATH = "http://static.eop.com/";

	@Test
	public void testWrapHtml() {

		String content = FileUtil
				.readFile("com/enation/eop/utils/index.html");
		content = EopUtil.wrapcss(content, PATH);
		content = EopUtil.wrapimage(content, PATH);
	    content = EopUtil.wrapjavascript(content, PATH);
		//System.out.println(content);
		String result = FileUtil
		.readFile("com/enation/eop/utils/result.html");
	//	Assert.assertEquals(result,content);
	}
}
