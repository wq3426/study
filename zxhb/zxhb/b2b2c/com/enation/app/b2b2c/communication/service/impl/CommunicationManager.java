package com.enation.app.b2b2c.communication.service.impl;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.communication.client.TCPClient;
import com.enation.app.b2b2c.communication.service.ICommunicationManager;

@Component
public class CommunicationManager implements ICommunicationManager{

	@Override
	public void send(byte[] message) {
		try{
			TCPClient tcpClient = TCPClient.getInstance();
			tcpClient.sendMsg(message);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
