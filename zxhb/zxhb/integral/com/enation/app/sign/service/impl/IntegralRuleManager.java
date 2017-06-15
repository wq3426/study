package com.enation.app.sign.service.impl;

import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.sign.model.IntegralRule;
import com.enation.app.sign.service.IIntegralRuleManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;

/**
 * @Description 会员积分接口管理
 * @createTime 2016年9月20日 上午11:02:58
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
@SuppressWarnings({ "rawtypes", "unchecked"})
public class IntegralRuleManager extends BaseSupport<IntegralRule> implements IIntegralRuleManager {

	@Override
	public Page searchIntegral(Map integralMap, int page, int pageSize) {
		String sql = createTemlSql(integralMap);
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}
	
	private String createTemlSql(Map integralMap){

		Integer stype = (Integer) integralMap.get("stype");
		String keyword = (String) integralMap.get("keyword");
		
		String sql = "select eir.* from  es_integral_rule eir";
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql+=" where (eir.integral_ruleContent like '"+keyword+"%'";
				sql+=" or eir.integral_value like '"+keyword+"%')";
			}
		}
		return sql;
	}

	@Override
	public void addIntegral(IntegralRule integral) {
		this.daoSupport.insert("es_integral_rule", integral);
	}

	@Override
	public IntegralRule getIntegralById(String id) {
		// TODO Auto-generated method stub
		String sql = "select eir.* from  es_integral_rule eir where eir.integral_ruleId = ?";
		return this.daoSupport.queryForObject(sql, IntegralRule.class, id);
	}

	@Override
	public void editIntegral(IntegralRule integral) {
		String sql = "update es_integral_rule set integral_ruleContent = '"+integral.getIntegral_ruleContent()+"',integral_value = "+integral.getIntegral_value()+
					",isSet_integralUp = '"+integral.getIsSet_integralUp()+"',integral_upValue = "+integral.getIntegral_upValue()+",remark = "+integral.getRemark()+
					" where integral_ruleId = "+integral.getIntegral_ruleId();
		this.baseDaoSupport.execute(sql);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addIntegralDetail(String nowDay, StoreMember storeMember,IntegralRule integralRule) {
		// TODO Auto-generated method stub
		String sqlForDetail = "insert into es_integral_detail(member_id,member_name,integral_date,integral_state,integral_ruleId)values("+
				  storeMember.getMember_id()+",'"+storeMember.getUsername()+"','"+nowDay+"',0,"+integralRule.getIntegral_ruleId()+")";
		this.baseDaoSupport.execute(sqlForDetail);
	}

}
