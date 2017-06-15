package com.enation.app.b2b2c.core.service.member;


/**
 * 邀请码Manager接口
 * @author Sylow
 *
 */
public interface IInviteCodeManager {
	
	/**
	 * 使用某一邀请码 返回使用结果
	 * @param code 邀请码
	 * @return boolean
	 */
	public boolean useCode(String code);
	
}
