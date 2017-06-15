package com.enation.app.b2b2c.core.service.member.impl;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.InviteCode;
import com.enation.app.b2b2c.core.service.InviteCodeState;
import com.enation.app.b2b2c.core.service.member.IInviteCodeManager;
import com.enation.eop.sdk.database.BaseSupport;

/**
 * 邀请码Manager实现类
 * @author Sylow
 * @version 1.0,2016-02-27
 * @since v5.2
 */
@Component
public class InviteCodeManager extends BaseSupport<InviteCode> implements IInviteCodeManager {

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.member.IInviteCodeManager#useCode(java.lang.String)
	 */
	@Override
	public boolean useCode(String code) {
		String sql = "SELECT * FROM es_invite_code WHERE code = ?";
		InviteCode inviteCode = this.daoSupport.queryForObject(sql, InviteCode.class, code);
		
		// 如果存在该 邀请码
		if (inviteCode != null) {
			
			// 如果邀请码有效
			if (inviteCode.getIs_enable() == InviteCodeState.ENABLE) {
				return true;
			}
			
		} else {
			throw new RuntimeException("该邀请码不存在");
		}
		return false;
	}

}
