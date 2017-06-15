package com.enation.app.b2b2c.core.model.reckoning;

import java.io.Serializable;

public class ReckoningDetail  implements Serializable{
	/**
	 */
	private static final long serialVersionUID = -7850216953067469825L;

	private Integer id ;
	
	private String order_sn; //订单号
	
	private Long service_time; //服务时间
	
	private Double order_price; //订单价格
	
	private Double use_gain; //使用奖励
	
	private Double use_coupon; //使用优惠券
	
	private Double use_repair_coin ;//
	
	private Double paymoney; //实际支付
	
	private Integer order_type; //订单类型
	
	private Double handling_charge; //手续费
	
	private Double service_charge; //服务费
	
	private Double settlement_money; //结算金额
	
	private Integer store_id; //店铺id
	
	private Long settlement_time; //结算时间
	
	private String level_name ; //店铺等级名称
	

	
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getOrder_sn() {
		return order_sn;
	}


	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}


	public Long getService_time() {
		return service_time;
	}


	public void setService_time(Long service_time) {
		this.service_time = service_time;
	}


	public Double getUse_gain() {
		return use_gain;
	}


	public void setUse_gain(Double use_gain) {
		this.use_gain = use_gain;
	}


	public Double getUse_coupon() {
		return use_coupon;
	}


	public void setUse_coupon(Double use_coupon) {
		this.use_coupon = use_coupon;
	}


	public Double getPaymoney() {
		return paymoney;
	}


	public void setPaymoney(Double paymoney) {
		this.paymoney = paymoney;
	}


	public Integer getOrder_type() {
		return order_type;
	}


	public void setOrder_type(Integer order_type) {
		this.order_type = order_type;
	}


	public Double getHandling_charge() {
		return handling_charge;
	}


	public void setHandling_charge(Double handling_charge) {
		this.handling_charge = handling_charge;
	}


	public Double getService_charge() {
		return service_charge;
	}


	public void setService_charge(Double service_charge) {
		this.service_charge = service_charge;
	}


	public Double getSettlement_money() {
		return settlement_money;
	}


	public void setSettlement_money(Double settlement_money) {
		this.settlement_money = settlement_money;
	}


	public Integer getStore_id() {
		return store_id;
	}


	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}


	public Long getSettlement_time() {
		return settlement_time;
	}


	public void setSettlement_time(Long settlement_time) {
		this.settlement_time = settlement_time;
	}


	public Double getOrder_price() {
		return order_price;
	}


	public void setOrder_price(Double order_price) {
		this.order_price = order_price;
	}


	public String getLevel_name() {
		return level_name;
	}


	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}


	public Double getUse_repair_coin() {
		return use_repair_coin;
	}


	public void setUse_repair_coin(Double use_repair_coin) {
		this.use_repair_coin = use_repair_coin;
	}
	
	
	
}
