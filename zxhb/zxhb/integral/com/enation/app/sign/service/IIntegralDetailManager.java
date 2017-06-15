package com.enation.app.sign.service;

import java.util.List;

import com.enation.app.sign.model.IntegralDetail;

/**
 * @Description 用户积分明细管理
 *
 * @createTime 2016年9月23日 上午9:53:31
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
public interface IIntegralDetailManager {

	/**
	 * @description 查询用户积分明细
	 * @date 2016年9月22日 下午6:25:51
	 * @param member_id
	 * @return
	 * @return List<MemberIntegral>
	 */
	public List<IntegralDetail> getMemberIntegralList(Integer member_id);
	
}