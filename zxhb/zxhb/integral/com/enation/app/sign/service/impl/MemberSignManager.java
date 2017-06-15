package com.enation.app.sign.service.impl;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.sign.IntegralRuleSetting;
import com.enation.app.sign.model.IntegralRule;
import com.enation.app.sign.model.MemberSign;
import com.enation.app.sign.service.IIntegralRuleManager;
import com.enation.app.sign.service.IMemberIntegralManager;
import com.enation.app.sign.service.IMemberSignManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.util.DateUtil;

/**
 * @Description 用户签到管理
 *
 * @createTime 2016年9月21日 下午3:11:38
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
@SuppressWarnings({ "rawtypes", "unchecked"})
public class MemberSignManager extends BaseSupport<MemberSign> implements IMemberSignManager {
	
	private IIntegralRuleManager integralRuleManager;
	private IMemberIntegralManager memberIntegralManager;
	
	@Override
	public List<MemberSign> getMemberSignList(Integer member_id) {
		// TODO Auto-generated method stub
		Long[] currentMonth = DateUtil.getCurrentMonth();
		String sql = "select *  from es_member_sign ems where  ems.member_id = ? and ems.sign_time >= '"+
					 DateUtil.toString(currentMonth[0], "yyyy-MM-dd")+"' and ems.sign_time <= '" + DateUtil.toString(currentMonth[1], "yyyy-MM-dd")+"'";
		return this.daoSupport.queryForList(sql, MemberSign.class, member_id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void sign(String nowDay,String beforeDay,StoreMember storeMember) throws Exception {
			String nowSql = "select *  from es_member_sign ems where  ems.member_id = ? and ems.sign_time = '"+nowDay+"'";
			String sql1 = "";
			MemberSign memberSign = this.baseDaoSupport.queryForObject(nowSql, MemberSign.class, storeMember.getMember_id());
			if(memberSign == null){
				//查询当前用户前一天是否签到
				String sql = "select *  from es_member_sign ems where  ems.member_id = ? and ems.sign_time = '"+beforeDay+"'";
				MemberSign signMember = this.baseDaoSupport.queryForObject(sql, MemberSign.class, storeMember.getMember_id());
				if(signMember != null){
					sql1 = "insert into es_member_sign(member_id,member_name,sign_status,sign_time,sign_count)values("+
							storeMember.getMember_id()+",'"+storeMember.getUsername()+"',0"+",'"+nowDay+"',"+(signMember.getSign_count()+1)+")";
				}else{
					sql1 = "insert into es_member_sign(member_id,member_name,sign_status,sign_time,sign_count)values("+
							storeMember.getMember_id()+",'"+storeMember.getUsername()+"',0"+",'"+nowDay+"',1)";
					
				}
				//积分规则
				this.baseDaoSupport.execute(sql1);//新增用户签到
				IntegralRule integralRule = integralRuleManager.getIntegralById(IntegralRuleSetting.EVERY_DAY_SIGN.toString());
				if(integralRule != null){
					//新增用户积分明细
					integralRuleManager.addIntegralDetail(nowDay, storeMember, integralRule);
					//修改或者新增用户积分
					memberIntegralManager.addMemberIntegral(storeMember,integralRule);
				}
			}else{
				throw new Exception("签到失败");
			}
	}

	public IIntegralRuleManager getIntegralRuleManager() {
		return integralRuleManager;
	}

	public void setIntegralRuleManager(IIntegralRuleManager integralRuleManager) {
		this.integralRuleManager = integralRuleManager;
	}

	public IMemberIntegralManager getMemberIntegralManager() {
		return memberIntegralManager;
	}

	public void setMemberIntegralManager(IMemberIntegralManager memberIntegralManager) {
		this.memberIntegralManager = memberIntegralManager;
	}
	
}
