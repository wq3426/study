package com.enation.app.sign.service;

import java.util.Map;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.sign.model.IntegralRule;
import com.enation.framework.database.Page;

/**
 * @Description 会员积分接口
 * @createTime 2016年9月20日 上午11:02:15
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
public interface IIntegralRuleManager {

	/**
	 * @description 积分规则数据
	 * @date 2016年9月20日 上午11:16:50
	 * @param integralMap
	 * @param page
	 * @param pageSize
	 * @return
	 * @return Page
	 */
	public Page searchIntegral(Map integralMap, int page, int pageSize);
	/**
	 * @description 增加积分规则
	 * @date 2016年9月20日 下午3:51:23
	 * @param integral
	 * @return void
	 */
	public void addIntegral(IntegralRule integral);
	/**
	 * @description 根据id查询积分规则信息
	 * @date 2016年9月20日 下午5:48:13
	 * @param id
	 * @return
	 * @return Integral
	 */
	public IntegralRule getIntegralById(String id);
	/**
	 * @description 修改积分规则
	 * @date 2016年9月20日 下午6:21:38
	 * @param integral
	 * @return void
	 */
	public void editIntegral(IntegralRule integral);
	/**
	 * @description 新增用户积分明细
	 * @date 2016年9月22日 下午3:46:57
	 * @param nowDay
	 * @param storeMember
	 * @param integralRule
	 * @return void
	 */
	public void addIntegralDetail(String nowDay, StoreMember storeMember, IntegralRule integralRule);
}