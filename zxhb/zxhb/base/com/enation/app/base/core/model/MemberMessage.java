package com.enation.app.base.core.model;

/**
 *   app 会员消息 
 * @author chopper
 *
 */
public class MemberMessage {

	/**
	 * id
	 */
	public int 	id;	
	/**
	 * 会员id
	 */
	public int member_id;  
	/**
	 * 消息id
	 */
	public int message_id;
	
	/**
	 * 消息状态
	 */
	public int status;
 

	public MemberMessage(int member_id, int message_id, int status) {
		super(); 
		this.member_id = member_id;
		this.message_id = message_id;
		this.status = status;
	}

	public MemberMessage() {
		super(); 
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMember_id() {
		return member_id;
	}

	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}

	public int getMessage_id() {
		return message_id;
	}

	public void setMessage_id(int message_id) {
		this.message_id = message_id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}
