package com.enation.app.shop.core.model;

import com.enation.framework.database.DynamicField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 购物车实体
 * @author kingapex
 *2010-3-23下午03:34:04
 */
public class Cart extends DynamicField {
	
	private Integer cart_id;
	private Integer goods_id;
	private Integer product_id;
	private Integer num;
	private Double weight;
	private String session_id;
	private Integer itemtype;
	private String name;
	private Double price;
	private Double rewards_limit;
	private String addon; //附件字串
	private Integer store_id;
	private Integer member_id;
	private String carplate;
	
	public boolean isImmediately = true; //判断是否为立即下单，不为数据库字段
	private Integer insure_repair_specid;//保险/保养订单特殊属性
    
	@PrimaryKeyField
	public Integer getCart_id() {
		return cart_id;
	}
	public void setCart_id(Integer cartId) {
		cart_id = cartId;
	}
	public Integer getProduct_id() {
		return product_id;
	}
	public void setProduct_id(Integer productId) {
		product_id = productId;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String sessionId) {
		session_id = sessionId;
	}
	public Integer getItemtype() {
		return itemtype;
	}
	public void setItemtype(Integer itemtype) {
		this.itemtype = itemtype;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getAddon() {
		return addon;
	}
	public void setAddon(String addon) {
		this.addon = addon;
	}
	public Integer getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public Double getRewards_limit() {
		return rewards_limit;
	}
	public void setRewards_limit(Double rewards_limit) {
		this.rewards_limit = rewards_limit;
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
	public Integer getInsure_repair_specid() {
		return insure_repair_specid;
	}
	public void setInsure_repair_specid(Integer insure_repair_specid) {
		this.insure_repair_specid = insure_repair_specid;
	}
	
	@Override
	public String toString() {
		return "Cart [cart_id=" + cart_id + ", goods_id=" + goods_id + ", product_id=" + product_id + ", num=" + num
				+ ", weight=" + weight + ", session_id=" + session_id + ", itemtype=" + itemtype + ", name=" + name
				+ ", price=" + price + ", rewards_limit=" + rewards_limit + ", addon=" + addon + ", store_id="
				+ store_id + ", member_id=" + member_id + ", carplate=" + carplate + ", isImmediately=" + isImmediately
				+ ", insure_repair_specid=" + insure_repair_specid + "]";
	}
}
