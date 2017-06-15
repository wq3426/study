package com.enation.app.sign.service;

import java.util.List;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.sign.model.MemberSign;

/**
 * @Description 用户签到管理
 *
 * @createTime 2016年9月21日 下午3:11:58
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
public interface IMemberSignManager {
	/**
	 * @description 用户签到列表加载
	 * @date 2016年9月21日 下午3:46:36
	 * @param member_id
	 * @return
	 * @return List<MemberSign>
	 */
	public List<MemberSign> getMemberSignList(Integer member_id);
	
	/**
	 * @param nowDay 
	 * @param beforeDay 
	 * @description 签到 
	 * @date 2016年9月21日 下午4:53:27
	 * @param storeMember
	 * @return void
	 * @throws Exception 
	 */
	public void sign(String nowDay, String beforeDay, StoreMember storeMember) throws Exception;

	
}