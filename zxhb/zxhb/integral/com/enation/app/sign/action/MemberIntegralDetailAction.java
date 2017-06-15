package com.enation.app.sign.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.sign.model.IntegralDetail;
import com.enation.app.sign.model.MemberIntegral;
import com.enation.app.sign.service.IIntegralDetailManager;
import com.enation.app.sign.service.IMemberIntegralManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonMessageUtil;

import net.sf.json.JSONObject;
/**
 * @Description 用户积分明细Action
 * @createTime 2016年9月21日 上午10:00:23
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/integral/admin")
@Action("integralDetail")
@Results({
	@Result(name="list_integralDetail", type="freemarker", location="/integral/admin/detail/integralDetail_list.html")
})
@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })
public class MemberIntegralDetailAction extends WWAction {
	private IMemberIntegralManager memberIntegralManager;
	private IStoreMemberManager storeMemberManager;
	private IIntegralDetailManager integralDetailManager;
	private List<IntegralDetail> IntegralDetailList;
	private Map integralMap;
	private Integer stype;
	private String keyword;
	private MemberIntegral memberIntegral;
	/**
	 * @description 跳转至积分规则列表
	 * @date 2016年9月20日 下午12:07:23
	 * @return
	 * @return String
	 */
	public String integralDetailList() {
		return "list_integralDetail";
	}
	
	/**
	 * @description 获取用户总积分
	 * @date 2016年9月30日 下午5:32:56
	 * @return
	 * @return String
	 */
	public String getMemberAllIntegral(){
		JSONObject data = new JSONObject();
		StoreMember storeMember =storeMemberManager.getStoreMember();
		if(storeMember.getMember_id() != null){
			memberIntegral = this.memberIntegralManager.getMemberIntegral(storeMember.getMember_id());
			data.put("memberIntegral", memberIntegral);
			this.json = JsonMessageUtil.getObjectJson(data);
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description admin获取积分明细列表Json
	 * @date 2016年9月20日 下午12:07:14
	 * @param stype 搜索类型,Integer
	 * @param keyword 搜索关键字,String
	 * @return String
	 */
	public String integerlistJson() {
		integralMap = new HashMap();
		integralMap.put("stype", stype);
		integralMap.put("keyword", keyword); 
		this.webpage = this.memberIntegralManager.searchIntegralDetail(integralMap, this.getPage(), this.getPageSize()); 
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}

	/**
	 * @description 积分明细列表api
	 * @date 2016年9月22日 下午6:13:17
	 * @return
	 * @return String
	 */
	public String getMemberIntegralDetailList(){
		JSONObject data = new JSONObject();
		StoreMember storeMember =storeMemberManager.getStoreMember();
		if(storeMember.getMember_id() != null){
			IntegralDetailList = this.integralDetailManager.getMemberIntegralList(storeMember.getMember_id());
			data.put("IntegralDetailList", IntegralDetailList);
			this.json = JsonMessageUtil.getObjectJson(data);
		}
		return JSON_MESSAGE;
	}
	
	
	public IMemberIntegralManager getMemberIntegralManager() {
		return memberIntegralManager;
	}

	public void setMemberIntegralManager(IMemberIntegralManager memberIntegralManager) {
		this.memberIntegralManager = memberIntegralManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public List<IntegralDetail> getIntegralDetailList() {
		return IntegralDetailList;
	}

	public void setIntegralDetailList(List<IntegralDetail> integralDetailList) {
		IntegralDetailList = integralDetailList;
	}

	public Map getIntegralMap() {
		return integralMap;
	}

	public void setIntegralMap(Map integralMap) {
		this.integralMap = integralMap;
	}

	public Integer getStype() {
		return stype;
	}

	public void setStype(Integer stype) {
		this.stype = stype;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public IIntegralDetailManager getIntegralDetailManager() {
		return integralDetailManager;
	}

	public void setIntegralDetailManager(IIntegralDetailManager integralDetailManager) {
		this.integralDetailManager = integralDetailManager;
	}

	public MemberIntegral getMemberIntegral() {
		return memberIntegral;
	}

	public void setMemberIntegral(MemberIntegral memberIntegral) {
		this.memberIntegral = memberIntegral;
	}
}
