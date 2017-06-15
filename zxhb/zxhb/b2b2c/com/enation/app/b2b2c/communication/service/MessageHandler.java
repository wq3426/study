package com.enation.app.b2b2c.communication.service;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import com.enation.app.b2b2c.communication.server.impl.TCPProtocolImpl;
import com.enation.app.shop.core.service.ICarHodometerManager;
import com.enation.app.shop.core.service.ICarInfoManager;

public class MessageHandler {

	private volatile static MessageHandler messageHandler;
	
	public static Map <String, Map <String, Object>> hotometerMap = new HashMap <String,Map <String, Object>>(); 
	
	private ICarInfoManager carInfoManager;
	
	private ICarHodometerManager carHotometerManager;
	
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
	 * @param bytes2
	 * @return void
	 * @throws Exception 
	 */
	public void handler(byte[] bytes) throws Exception {
		 MessageParse mesParse = new MessageParse();
		 mesParse.handlerMessage(bytes);
		/*if(bytes2!=null && !bytes2.isEmpty()){
			 Map map = bytes2.get("0x2E");//读取
			 if(map!=null && !map.isEmpty()){
				  String isActivated = (String) map.get("D21");//上电时发送至服务器,D21为OBD是否激活
				  if("not activated".equals(isActivated)){//如果没激活调用数据库查询
					  System.out.println("查询carinfo表,判断有没有绑定激活");
				  }
			 }
		}*/
	}
	
	public void sendToObdActivities(TCPProtocolImpl tcpProtocolImpl, SocketChannel obdSocketChannel,String obdDeviceId, byte[] bytes) throws IOException {
		  byte[] toObdMessageBytes = new byte[(bytes.length - 16)];//
		  for(int i = 16; i < bytes.length ; i ++ ){
			  toObdMessageBytes [i-16] =  bytes[i];
		  }
		  tcpProtocolImpl.handleWrite(obdSocketChannel, toObdMessageBytes); 
		}
	public ICarInfoManager getCarInfoManager() {
		return carInfoManager;
	}

	public void setCarInfoManager(ICarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}

	public ICarHodometerManager getCarHotometerManager() {
		return carHotometerManager;
	}

	public void setCarHotometerManager(ICarHodometerManager carHotometerManager) {
		this.carHotometerManager = carHotometerManager;
	}
}
