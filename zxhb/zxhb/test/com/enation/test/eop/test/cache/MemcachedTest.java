package com.enation.test.eop.test.cache;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.enation.eop.resource.model.EopSite;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class MemcachedTest {

	@org.junit.Test
	public void Test() {
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil
				.getAddresses("127.0.0.1:11211"));
		MemcachedClient memcachedClient = null;
		try {
			
			memcachedClient = builder.build();
			long beginTime = System.currentTimeMillis();
			for(int i=0;i<10000;i++){
				EopSite site = EopSite.getInstance();
				String domain = "www.site"+i+".com";
				memcachedClient.set(domain,0, site);
			}
			long endTime = System.currentTimeMillis();
			//System.out.println("压入完成");
			System.out.printf("执行时间是:%d ms %n", endTime - beginTime);
			
			
			 beginTime = System.currentTimeMillis();
			 EopSite mysite = memcachedClient.get("www.site789.com");
				//System.out.println("找到site"+ mysite.getSitename());
				 endTime = System.currentTimeMillis();
				System.out.printf("执行时间是:%d ms %n", endTime - beginTime);
			
				//memcachedClient.replace(arg0, o, arg2, arg3)
			
			
		} catch (MemcachedException e) {
			System.err.println("MemcachedClient operation fail");
			e.printStackTrace();
		} catch (TimeoutException e) {
			System.err.println("MemcachedClient operation timeout");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// ignore
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
		
			memcachedClient.shutdown();
		} catch (IOException e) {
			System.err.println("Shutdown MemcachedClient fail");
			e.printStackTrace();
		}
	}
}
