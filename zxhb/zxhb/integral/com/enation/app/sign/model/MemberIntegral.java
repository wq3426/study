package com.enation.app.sign.model;

import com.enation.framework.database.PrimaryKeyField;

/**
 * @Description 用户积分表
 *
 * @createTime 2016年9月21日 上午11:05:15
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
public class MemberIntegral {
	private Integer id;//用户积分明细id
	private Integer member_id;//用户id
	private String member_name;//用户名
	private long member_integral;//用户积分
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
	public long getMember_integral() {
		return member_integral;
	}
	public void setMember_integral(long member_integral) {
		this.member_integral = member_integral;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}//备注
	
}