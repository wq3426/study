package com.enation.eop.sdk;

import com.enation.eop.resource.model.EopSite;

/**
 * @Description 应用接口
 *
 * @createTime 2016年9月7日 下午1:15:00
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public interface IApp {
	
	/**
	 * @description 在系统初始化时会调用此方法
	 * @date 2016年9月7日 下午1:15:16
	 */
	public void install();
	
	/**
	 * @description 
	 * @date 2016年9月7日 下午1:15:27
	 * @return String
	 */
	public String dumpXml();
	
	/**
	 * @description session失效 事件
	 * @date 2016年9月7日 下午1:15:44
	 * @param sessionid
	 * @param site
	 */
	public void sessionDestroyed(String sessionid,EopSite site );
	
	/**
	 * @description 应用的名称
	 * @date 2016年9月7日 下午1:15:59
	 * @return String
	 */
	public String getName();
	
	/**
	 * @description 应用的id
	 * @date 2016年9月7日 下午1:16:30
	 * @return String
	 */
	public String getId();
		
	/**
	 * @description 应用的命名空间
	 * @date 2016年9月7日 下午1:16:47
	 * @return String
	 */
	public String getNameSpace();
	
}
