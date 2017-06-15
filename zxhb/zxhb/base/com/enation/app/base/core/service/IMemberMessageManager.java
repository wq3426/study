package com.enation.app.base.core.service;
 

/**
 * 会员消息 管理类
 * @author chopper
 *
 */
public interface IMemberMessageManager {

	
	/**
	 * 为会员消息发送
	 * @param am
	 * @param member_id
	 */
	public void sendmessage(int amid,int member_id);
	
	
}
