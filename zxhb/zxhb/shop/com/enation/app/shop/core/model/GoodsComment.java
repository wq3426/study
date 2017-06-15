package com.enation.app.shop.core.model;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

public class GoodsComment implements Serializable {
	private Integer id;

	private Integer goods_id;  

	private Integer product_id;

	private Integer store_id;

	private Integer member_id;

	private String carplate;

	private Integer isanonymity; //是否匿名
	
	private Double goods_grade;//商品评分
	
	private String content;
	
	private Long create_time; //评价时间
	
	private String order_sn ; //关联订单

	public Integer getId() {
		return id;
	}
	
	@PrimaryKeyField
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}

	public Integer getProduct_id() {
		return product_id;
	}

	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}
	
	
	
	
}
