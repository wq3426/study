package com.enation.app.b2b2c.core.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.enation.app.b2b2c.communication.client.TCPClient;
import com.enation.app.b2b2c.communication.server.TCPServer;
import com.enation.framework.util.StringUtil;


public class ObdServiceSocketListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try{
			 //加载配置文件
			String path = StringUtil.getRootPath();
			path = path+"/config/jdbc.properties";
			
			InputStream in  = new FileInputStream(new File(path));
			Properties props = new Properties();
			props.load(in);
			String  tcpPort = (String) props.get("tcpPort");
			TCPServer server = new TCPServer();
			Thread thread = new Thread(server);
			thread.start();
			TCPClient tcpClient = TCPClient.getInstance();
			String hostIp = "127.1.1.0" ;
			int hostListenningPort = Integer.parseInt(tcpPort);
			tcpClient.setHostIp(hostIp);
			tcpClient.setHostListenningPort(hostListenningPort);
			tcpClient.initialize(hostIp, hostListenningPort);
		}catch(Exception e){
			System.out.println("error : ObdServiceSocketListener open false");
			e.printStackTrace();
		}
	
	}

}
