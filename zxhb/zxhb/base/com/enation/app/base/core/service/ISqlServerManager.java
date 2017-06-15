package com.enation.app.base.core.service;

import java.util.Map;

/**
 * sql server 安装接口
 * @author xulipeng
 *
 */
public interface ISqlServerManager {
	
	/**
	 * 安装默认数据
	 */
	public void installData(String table,Map data);

}
