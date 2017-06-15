package com.enation.app.sign.service.impl;

import java.util.List;

import com.enation.app.sign.model.IntegralDetail;
import com.enation.app.sign.service.IIntegralDetailManager;
import com.enation.eop.sdk.database.BaseSupport;

/**
 * @Description 会员积分明细管理
 *
 * @createTime 2016年9月23日 上午9:59:09
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
@SuppressWarnings({ "rawtypes", "unchecked"})
public class IntegralDetailManager extends BaseSupport<IntegralDetail> implements IIntegralDetailManager{

	@Override
	public List<IntegralDetail> getMemberIntegralList(Integer member_id) {
		String sql = "select eid.*,eir.integral_ruleContent,eir.integral_value from  es_integral_detail eid,es_integral_rule eir "
					+"where eid.integral_ruleId = eir.integral_ruleId and eid.member_id = ? order  by integral_date desc";
		List<IntegralDetail> integralDetails = this.baseDaoSupport.queryForList(sql, IntegralDetail.class,member_id);
		return integralDetails;
	}
	

}
