package com.enation.app.sign.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * @Description 积分规则
 *
 * @createTime 2016年9月20日 上午11:05:15
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
public class IntegralRule {
	private Integer integral_ruleId;	//积分规则id
	private String integral_ruleContent;//积分规则内容
	private Integer integral_value;	//积分值
	private char isSet_integralUp;	//是否设置每天的积分上限0：否,1：是
	private Integer integral_upValue;//每天积分上限
	private String remark;//备注
	
	private long start_integral_date;//开始积分时间
	private long end_integral_date;//结束积分时间
	@PrimaryKeyField
	public Integer getIntegral_ruleId() {
		return integral_ruleId;
	}
	public void setIntegral_ruleId(Integer integral_ruleId) {
		this.integral_ruleId = integral_ruleId;
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
	public char getIsSet_integralUp() {
		return isSet_integralUp;
	}
	public void setIsSet_integralUp(char isSet_integralUp) {
		this.isSet_integralUp = isSet_integralUp;
	}
	public Integer getIntegral_upValue() {
		return integral_upValue;
	}
	public void setIntegral_upValue(Integer integral_upValue) {
		this.integral_upValue = integral_upValue;
	}
	public long getStart_integral_date() {
		return start_integral_date;
	}
	public void setStart_integral_date(long start_integral_date) {
		this.start_integral_date = start_integral_date;
	}
	public long getEnd_integral_date() {
		return end_integral_date;
	}
	public void setEnd_integral_date(long end_integral_date) {
		this.end_integral_date = end_integral_date;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}