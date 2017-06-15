package com.enation.app.fastdfsclient.config;

import com.enation.framework.util.StringUtil;

public class FastDfsClientConfig {
	
	public static String GROUPNAME = "group1";//文件上传的storage组名
	
	public static String CONF_FILENAME = StringUtil.getRootPath().replace("/", "\\") +
			"\\WebContent\\WEB-INF\\config\\fdfs_client.conf";//配置文件的绝对路径
	
	public static String SERVER_IP = "123.57.57.145"; // nginx服务器的IP地址	
	
}
