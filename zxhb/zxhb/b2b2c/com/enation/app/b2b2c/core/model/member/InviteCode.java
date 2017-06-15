package com.enation.app.b2b2c.core.model.member;

/**
 * 邀请码实体类
 * @author Sylow
 * @version v1.0,2016-02-27
 * @since v5.2
 */
public class InviteCode {
	
	private int id;
	
	/**邀请码*/
	private String code;
	
	/**是否有效 1=有效 0=无效*/
	private int is_enable;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getIs_enable() {
		return is_enable;
	}

	public void setIs_enable(int is_enable) {
		this.is_enable = is_enable;
	}
	
}
