package com.enation.app.base.core.model;

/**
 * @Description 店铺审核（结果）
 *
 * @createTime 2016年9月20日 下午4:02:00
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public class StoreAudit {
	
	
	private int id;					//审核Id
	private int store_id;			//店铺Id
	private String audit_result;	//审核结果（0：驳回 1：通过）
	private String remark;			//备注（驳回理由）
	
	
	
	/*
	 * =====================================================================
	 * GETTER AND SETTER
	 */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStore_id() {
		return store_id;
	}
	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}
	public String getAudit_result() {
		return audit_result;
	}
	public void setAudit_result(String audit_result) {
		this.audit_result = audit_result;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	


}
