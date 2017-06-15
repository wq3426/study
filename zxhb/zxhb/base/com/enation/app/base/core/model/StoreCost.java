package com.enation.app.base.core.model;

import java.io.Serializable;

/**
 *店铺消费统计表
 * @author LiFenLong
 *
 */
public class StoreCost implements Serializable{
	private Integer id;
	private Integer store_id;
	private Integer level_id;
	private Integer type_id;
	private long valid_start_date;
	private long valid_end_date;
	private Integer used_num;
	private Integer surp_num;
	private String isFree;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getType_id() {
		return type_id;
	}
	public void setType_id(Integer type_id) {
		this.type_id = type_id;
	}
	public Integer getLevel_id() {
		return level_id;
	}
	public void setLevel_id(Integer level_id) {
		this.level_id = level_id;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public long getValid_start_date() {
		return valid_start_date;
	}
	public void setValid_start_date(long valid_start_date) {
		this.valid_start_date = valid_start_date;
	}
	public long getValid_end_date() {
		return valid_end_date;
	}
	public void setValid_end_date(long valid_end_date) {
		this.valid_end_date = valid_end_date;
	}
	public Integer getUsed_num() {
		return used_num;
	}
	public void setUsed_num(Integer used_num) {
		this.used_num = used_num;
	}
	public Integer getSurp_num() {
		return surp_num;
	}
	public void setSurp_num(Integer surp_num) {
		this.surp_num = surp_num;
	}
	public String getIsFree() {
		return isFree;
	}
	public void setIsFree(String isFree) {
		this.isFree = isFree;
	}

}
