package com.enation.app.sign.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * @Description 用户积分明细表
 *
 * @createTime 2016年9月21日 上午11:05:15
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
public class IntegralDetail {
	private Integer id;//用户积分明细id
	private Integer member_id;//用户id
	private String member_name;//用户名
	private String integral_date;//积分日期
	private char integral_state;//积分状态0：添加积分，1：消费积分
	private Integer integral_ruleId;//积分规则id
	private String remark;
	private String integral_ruleContent;
	private Integer integral_value;
	
	@PrimaryKeyField
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public String getMember_name() {
		return member_name;
	}
	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}
	public char getIntegral_state() {
		return integral_state;
	}
	public void setIntegral_state(char integral_state) {
		this.integral_state = integral_state;
	}
	public Integer getIntegral_ruleId() {
		return integral_ruleId;
	}
	public void setIntegral_ruleId(Integer integral_ruleId) {
		this.integral_ruleId = integral_ruleId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}//备注
	public String getIntegral_date() {
		return integral_date;
	}
	public void setIntegral_date(String integral_date) {
		this.integral_date = integral_date;
	}
	public String getIntegral_ruleContent() {
		return integral_ruleContent;
	}
	public void setIntegral_ruleContent(String integral_ruleContent) {
		this.integral_ruleContent = integral_ruleContent;
	}
	public Integer getIntegral_value() {
		return integral_value;
	}
	public void setIntegral_value(Integer integral_value) {
		this.integral_value = integral_value;
	}
}