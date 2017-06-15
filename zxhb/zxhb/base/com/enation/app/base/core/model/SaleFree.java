package com.enation.app.base.core.model;

import java.io.Serializable;

/**
 * 营销管理免费模板
 * @author LiFenLong
 *
 */
public class SaleFree implements Serializable{
	private Integer id;
	private Integer type_id;
	private Integer use_num;
	private Integer level_id;
	
	private String type_name;
	private String level_name;
	
	private String limit_date;
	private Double price;
	private String isFree;
	private char isInitia;
	
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
	public Integer getUse_num() {
		return use_num;
	}
	public void setUse_num(Integer use_num) {
		this.use_num = use_num;
	}
	public Integer getLevel_id() {
		return level_id;
	}
	public void setLevel_id(Integer level_id) {
		this.level_id = level_id;
	}
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public String getLevel_name() {
		return level_name;
	}
	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}
	public String getLimit_date() {
		return limit_date;
	}
	public void setLimit_date(String limit_date) {
		this.limit_date = limit_date;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getIsFree() {
		return isFree;
	}
	public void setIsFree(String isFree) {
		this.isFree = isFree;
	}
	public char getIsInitia() {
		return isInitia;
	}
	public void setIsInitia(char isInitia) {
		this.isInitia = isInitia;
	}

}
