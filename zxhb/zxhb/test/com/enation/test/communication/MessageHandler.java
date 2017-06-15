package com.enation.test.communication;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import com.enation.app.shop.core.service.ICarHodometerManager;
import com.enation.app.shop.core.service.ICarInfoManager;
import com.enation.app.shop.core.service.impl.CarHodometerManager;
import com.enation.test.communication.server.impl.TCPProtocolImpl;

public class MessageHandler {

	private volatile static MessageHandler messageHandler;
	
	public static Map <String, Map <String, Object>> hotometerMap = new HashMap <String,Map <String, Object>>(); 
	
	
	private MessageHandler() {
	}

	public static MessageHandler getInstance() {
		if (messageHandler == null) {
			synchronized (MessageHandler.class) {
				if (messageHandler == null) {
					messageHandler = new MessageHandler();
				}
			}
		}
		return messageHandler;
	}
	
	
	public boolean activateRegister(TCPProtocolImpl impl,Map<String,String> map){
		return true;
	}
	public boolean activateRegister(Map<String,String> map){
		return true;
	}
	/** @description 统一处理解析的报文信息
	 * @date 2016年9月28日 下午6:19:05
	 * @param resultMap
	 * @return void
	 * @throws Exception 
	 */
	public void handler(Byte[] bytes) throws Exception {
		 MessageParse mesParse = new MessageParse();
		 mesParse.handlerMessage(bytes);
		/*if(resultMap!=null && !resultMap.isEmpty()){
			 Map map = resultMap.get("0x2E");//读取
			 if(map!=null && !map.isEmpty()){
				  String isActivated = (String) map.get("D21");//上电时发送至服务器,D21为OBD是否激活
				  if("not activated".equals(isActivated)){//如果没激活调用数据库查询
					  System.out.println("查询carinfo表,判断有没有绑定激活");
				  }
			 }
		}*/
	}
	
	public void sendToObdActivities(TCPProtocolImpl tcpProtocolImpl, SocketChannel obdSocketChannel,String obdDeviceId) throws IOException {
	 	byte[] toObdMessageBytes = new byte[30];
		int position = 0;
		for(byte by : obdDeviceId.getBytes()){ 
			toObdMessageBytes[position++] = by;
		}
		//time   
		byte[] activities = {0x00,0x00,0x00,0x00,0x02,0x16,0x55,0x23,0x02,0x00,0x00,0x00,0x00,(byte)0x1c}; //时间和版本
		for(byte by : activities){
			toObdMessageBytes[position++] = by;
		}
		tcpProtocolImpl.handleWrite(obdSocketChannel, toObdMessageBytes); 
		}
}
