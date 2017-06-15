package com.enation.eop.sdk.utils;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * @Description 从spring容器中获取对象
 *
 * @createTime 2016年8月23日 下午3:23:41
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public class GetBean {

	/**
	 * @description 从Web容器中获得Bean对象
	 * @date 2016年8月23日 下午3:24:02
	 * @param beanName
	 * @param cls
	 * @return
	 */
	public static <T>T getBean(String beanName, Class<T> cls){
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		T bean = (T) context.getBean(beanName, cls);
		return bean;
	}

	/**
	 * @description 从Web容器中获得Bean对象
	 * @date 2016年8月23日 下午3:25:28
	 * @param cls
	 * @return
	 */
	public static <T>T getBeanByClass(Class<T> cls){
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		T bean = (T) context.getBean(cls);
		return bean;
	}
}
