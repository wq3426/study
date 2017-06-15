package com.enation.app.shop.core.model;

import java.io.Serializable;

public class Comment implements Serializable{
	

	private Integer id;
	
	private Integer store_id;
	
	private Integer member_id;
	
	private String carplate;
	
	private String order_sn ;
	
	private Long create_time;
	
	private Double grade;//评分弃用
	 
	private Double goods_grade; //商品评分弃用
	
	private Double service_grade;
	
	private Integer isanonymity;
	
	private String content;
	
	
	public Integer getIsanonymity() {
		return isanonymity;
	}

	public void setIsanonymity(Integer isanonymity) {
		this.isanonymity = isanonymity;
	}

	public Double getGoods_grade() {
		return goods_grade;
	}

	public void setGoods_grade(Double goods_grade) {
		this.goods_grade = goods_grade;
	}

	public Double getService_grade() {
		return service_grade;
	}

	public void setService_grade(Double service_grade) {
		this.service_grade = service_grade;
	}

	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	

	public Integer getId() {
		return id;
	}

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}

	public Integer getMember_id() {
		return member_id;
	}

	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}

	public String getCarplate() {
		return carplate;
	}

	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public Double getGrade() {
		return grade;
	}

	public void setGrade(Double grade) {
		this.grade = grade;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
	
	
}
