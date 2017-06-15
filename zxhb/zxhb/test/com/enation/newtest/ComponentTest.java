package com.enation.newtest;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class ComponentTest {
	@Test
	public void test() throws IOException{
		
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] res= resolver.getResources("classpath*:*.*/component.xml");
		
		if(res==null) return ;
		
		for(int i=0;i<res.length;i++){
			//System.out.println(res[i]);
		}
		
//		String path = "spring/*.xml";
//		if (path.startsWith("/")) {
//			path = path.substring(1);
//		}
//		Enumeration resourceUrls =new DefaultResourceLoader().getClassLoader().getResources(path);
//		Set result = new LinkedHashSet(16);
//		while (resourceUrls.hasMoreElements()) {
//			URL url = (URL) resourceUrls.nextElement();
//			//System.out.println(url);
//		}
		 
	}
}
