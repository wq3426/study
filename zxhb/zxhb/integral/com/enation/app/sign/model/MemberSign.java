package com.enation.app.sign.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * @Description 用户签到表
 *
 * @createTime 2016年9月21日 上午11:05:15
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
public class MemberSign {
	private Integer id;//用户积分明细id
	private Integer member_id;//用户id
	private String member_name;//用户名
	private char sign_status;//当日是否签到
	private String sign_time;//签到时间
	private Integer sign_count;//持续签到天数
	private String remark;
	
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
	public char getSign_status() {
		return sign_status;
	}
	public void setSign_status(char sign_status) {
		this.sign_status = sign_status;
	}
	public String getSign_time() {
		return sign_time;
	}
	public void setSign_time(String sign_time) {
		this.sign_time = sign_time;
	}
	public Integer getSign_count() {
		return sign_count;
	}
	public void setSign_count(Integer sign_count) {
		this.sign_count = sign_count;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}//备注
	
}