package com.enation.app.sign.service.impl;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.base.core.model.Adv;
import com.enation.app.sign.model.IntegralRule;
import com.enation.app.sign.model.MemberIntegral;
import com.enation.app.sign.service.IMemberIntegralManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;

/**
 * @Description 用户积分管理
 *
 * @createTime 2016年9月22日 下午3:54:47
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
@SuppressWarnings({ "rawtypes", "unchecked"})
public class MemberIntegralManager extends BaseSupport<MemberIntegral> implements IMemberIntegralManager {

	@Override
	public void addMemberIntegral(StoreMember storeMember,IntegralRule integralRule) {
		// TODO Auto-generated method stub
		String  sql = "select * from es_member_integral where member_id = "+storeMember.getMember_id();
		String sql1 = "";
		MemberIntegral memberIntegral = this.baseDaoSupport.queryForObject(sql, MemberIntegral.class);
		if(memberIntegral != null){
			sql1 = "update es_member_integral set member_integral = "+(integralRule.getIntegral_value()+memberIntegral.getMember_integral())+
					" where id = "+memberIntegral.getId();
		}else{
			sql1 = "insert into es_member_integral(member_id,member_name,member_integral)values("+storeMember.getMember_id()+
					",'"+storeMember.getUsername()+"',"+integralRule.getIntegral_value()+")";
		}	
		this.baseDaoSupport.execute(sql1);
	}
	
	@Override
	public Page searchIntegralDetail(Map integralMap, int page, int pageSize) {
		String sql = createDetailSql(integralMap);
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}
	
	@Override
	public MemberIntegral getMemberIntegral(Integer member_id) {
		String sql = "select emi.* from  es_member_integral emi where emi.member_id = ?";
		return this.daoSupport.queryForObject(sql, MemberIntegral.class, member_id);
	}
	
	private String createDetailSql(Map integralMap){

		Integer stype = (Integer) integralMap.get("stype");
		String keyword = (String) integralMap.get("keyword");
		
		String sql = "select eid.*,eir.integral_ruleContent,eir.integral_value from  es_integral_detail eid,es_integral_rule eir where eid.integral_ruleId = eir.integral_ruleId";
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql+=" and (eid.member_id like '"+keyword+"%'";
				sql+=" or eid.member_name like '"+keyword+"%')";
			}
		}
		return sql;
	}

}
