package com.enation.app.sign.service;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.sign.model.IntegralRule;
import com.enation.app.sign.model.MemberIntegral;
import com.enation.framework.database.Page;

/**
 * @Description 用户积分管理
 *
 * @createTime 2016年9月22日 下午3:54:32
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
public interface IMemberIntegralManager {
	
	/**新增或者修改用户积分
	 * @description 
	 * @date 2016年9月22日 下午3:58:04
	 * @param storeMember
	 * @param integralRule
	 * @return void
	 */
	public void addMemberIntegral(StoreMember storeMember, IntegralRule integralRule);

	/**
	 * @description 查询所有用户积分明细
	 * @date 2016年9月21日 下午12:00:05
	 * @param integralMap
	 * @param page
	 * @param pageSize
	 * @return
	 * @return Page
	 */
	public Page searchIntegralDetail(Map integralMap, int page, int pageSize);
	/**
	 * @description 获取用户总积分
	 * @date 2016年9月30日 下午5:33:14
	 * @param member_id
	 * @return
	 * @return MemberIntegral
	 */
	public MemberIntegral getMemberIntegral(Integer member_id);
	
}