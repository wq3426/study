package com.wq.spring.high.aware;

import java.io.IOException;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

/**
 * 实现接口BeanNameAware 和 ResourceLoaderAware，获得Bean名称和资源加载的服务，需重写接口方法
 * BeanNameAware 获得容器中Bean的名称
 * ResourceLoaderAware 获得资源加载器，可以获得外部资源文件
 * @author wq3426
 *
 */
@Service
public class AwareService implements BeanNameAware, ResourceLoaderAware {
	private String beanName;
	private ResourceLoader loader;

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		// TODO Auto-generated method stub
		this.loader = resourceLoader;
	}

	@Override
	public void setBeanName(String name) {
		// TODO Auto-generated method stub
		this.beanName = name;
	}
	
	public void outputResult(){
		System.out.println("Bean的名称为："+ beanName);
		Resource resource = loader.getResource("classpath:com/wq/spring/high/aware/test.txt");
		
		try {
			System.out.println("ResourceLoader 加载的文件内容为："+ resource.getInputStream().toString());
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
