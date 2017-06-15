package com.enation.test.eop.test.cache;

import org.junit.Test;

import com.enation.eop.resource.model.EopSite;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;

public class EhcacheTest {
	
	@Test
	public void test(){
		ICache<EopSite> cache = CacheFactory.getCache("siteCache");
		
		long beginTime = System.currentTimeMillis();
		for(int i=0;i<10000;i++){
			EopSite site = EopSite.getInstance();
		 
			String domain = "www.site"+i+".com";
			cache.put(domain, site);
		}
		long endTime = System.currentTimeMillis();
		//System.out.println("压入完成");
		System.out.printf("执行时间是:%d ms %n", endTime - beginTime);
		
		 beginTime = System.currentTimeMillis();
		EopSite mysite = cache.get("www.site789.com");
		//System.out.println("找到site"+ mysite.getSitename());
		 endTime = System.currentTimeMillis();
		System.out.printf("执行时间是:%d ms %n", endTime - beginTime);
	}
}
