package com.enation.app.sign.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.sign.model.IntegralRule;
import com.enation.app.sign.service.IIntegralRuleManager;
import com.enation.framework.action.WWAction;
/**
 * @Description 积分规则Action
 * @createTime 2016年9月20日 上午10:00:23
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/integral/admin")
@Action("integral")
@Results({
	@Result(name="list_integralRule", type="freemarker", location="/integral/admin/rule/integralRule_list.html"),
	@Result(name="add_integral",type="freemarker", location="/integral/admin/rule/integralRule_add.html"),
	@Result(name="edit_integral",type="freemarker", location="/integral/admin/rule/integralRule_edit.html")
})
@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })
public class IntegralRuleAction extends WWAction {

	private IIntegralRuleManager integralRuleManager;
	private Map integralMap;
	private Integer stype;
	private String keyword;
	private IntegralRule integral;
	private String startIntegralDate;
	private String endIntegralDate;
	private String id;
	
	/**
	 * @description 跳转至积分规则列表
	 * @date 2016年9月20日 下午12:07:23
	 * @return
	 * @return String
	 */
	public String integralRuleList() {
		return "list_integralRule";
	}
	
	/**
	 * @description 获取积分规则列表Json
	 * @date 2016年9月20日 下午12:07:14
	 * @param stype 搜索类型,Integer
	 * @param keyword 搜索关键字,String
	 * @return String
	 */
	public String integrallistJson() {
		
		integralMap = new HashMap();
		integralMap.put("stype", stype);
		integralMap.put("keyword", keyword); 
		this.webpage = this.integralRuleManager.searchIntegral(integralMap, this.getPage(), this.getPageSize()); 
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	} 
	
	/**
	 * @description 新增积分规则页面跳转
	 * @date 2016年9月20日 下午12:07:06
	 * @return
	 * @return String
	 */
	public String add(){
		return "add_integral";
	}
	
	/**
	 * @description 增加积分规则
	 * @date 2016年9月20日 下午3:50:39
	 * @return
	 * @return String
	 */
	public String saveAddIntegral() {
		this.integralRuleManager.addIntegral(integral);
		this.showSuccessJson("积分规则添加成功");
		return JSON_MESSAGE;
	}

	/**
	 * @description 修改积分规则页面跳转
	 * @date 2016年9月20日 下午12:07:06
	 * @return
	 * @return String
	 */
	public String edit(){
		integral=integralRuleManager.getIntegralById(id);
		return "edit_integral";
	}
	
	/**
	 * @description 修改积分规则
	 * @date 2016年9月20日 下午6:21:10
	 * @return
	 * @return String
	 */
	public String editIntegral() {
		this.integralRuleManager.editIntegral(integral);
		this.showSuccessJson("积分规则修改成功");
		return JSON_MESSAGE;
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

	public IntegralRule getIntegral() {
		return integral;
	}

	public void setIntegral(IntegralRule integral) {
		this.integral = integral;
	}

	public String getStartIntegralDate() {
		return startIntegralDate;
	}

	public void setStartIntegralDate(String startIntegralDate) {
		this.startIntegralDate = startIntegralDate;
	}

	public String getEndIntegralDate() {
		return endIntegralDate;
	}

	public void setEndIntegralDate(String endIntegralDate) {
		this.endIntegralDate = endIntegralDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public IIntegralRuleManager getIntegralRuleManager() {
		return integralRuleManager;
	}

	public void setIntegralRuleManager(IIntegralRuleManager integralRuleManager) {
		this.integralRuleManager = integralRuleManager;
	}
}
