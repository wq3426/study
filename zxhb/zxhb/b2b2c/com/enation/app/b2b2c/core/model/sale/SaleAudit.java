package com.enation.app.b2b2c.core.model.sale;

import java.io.Serializable;

/**
 * 营销管理申请模板
 * @author LiFenLong
 *
 */
public class SaleAudit implements Serializable{
	private Integer id;
	private Integer store_id;
	private Integer sale_type_id;
	private Integer audit_num;
	private long limit_date;
	private long apply_date;
	private char audit_status;//0:免费发放，1:购买失败，2：购买成功
	private char isFree;//0：否，1：是
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public Integer getSale_type_id() {
		return sale_type_id;
	}
	public void setSale_type_id(Integer sale_type_id) {
		this.sale_type_id = sale_type_id;
	}
	public Integer getAudit_num() {
		return audit_num;
	}
	public void setAudit_num(Integer audit_num) {
		this.audit_num = audit_num;
	}
	public long getLimit_date() {
		return limit_date;
	}
	public void setLimit_date(long limit_date) {
		this.limit_date = limit_date;
	}
	public long getApply_date() {
		return apply_date;
	}
	public void setApply_date(long apply_date) {
		this.apply_date = apply_date;
	}
	public char getAudit_status() {
		return audit_status;
	}
	public void setAudit_status(char audit_status) {
		this.audit_status = audit_status;
	}
	public char getIsFree() {
		return isFree;
	}
	public void setIsFree(char isFree) {
		this.isFree = isFree;
	}

}
