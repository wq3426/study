package com.enation.app.b2b2c.communication.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.communication.service.ICommunicationManager;
import com.enation.framework.action.WWAction;



/**
 * 结算API
 * @author fenlongli
 * @date 2015-6-7 下午4:05:25
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("communication")
public class CommunicationApiAction extends WWAction{
	
	private ICommunicationManager communicationManager;
	public String activationObd(){
		HttpServletRequest request = this.getRequest();
		String carplate = request.getParameter("carplate");
		String ObdDeviceId = "JTA0000111840002";
		//自己组织盈架后台命令发送 0~15为赢家dev_id,16~31 为obdDiv,32需要执行的命令messageId,34为执行命令
		String deviceId = "0000000000000000";
		byte[] activationMessage = new byte[32];
		int position = 0 ;
		for(byte b : deviceId.getBytes()){
			activationMessage[position++] = b;
		}
		for(byte b : ObdDeviceId.getBytes()){
			activationMessage[position++] = b;
		}
		activationMessage[position++] = 0x23; //
		activationMessage[position++] = 0x00; //
		
		communicationManager.send(activationMessage);
		return JSON_MESSAGE;
	}
	
	public ICommunicationManager getCommunicationManager() {
		return communicationManager;
	}
	public void setCommunicationManager(ICommunicationManager communicationManager) {
		this.communicationManager = communicationManager;
	}
	
	
	

	
	
}
