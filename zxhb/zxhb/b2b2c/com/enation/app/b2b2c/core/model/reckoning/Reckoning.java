package com.enation.app.b2b2c.core.model.reckoning;

import java.io.Serializable;

public class Reckoning implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4222487646567998550L;

	private Integer id ; 
	
	private String sn;  //交易流水号
	
	private Integer trade_type ; //店铺交易类型
	
	private Integer trade_status ; //店铺交易状态
	
	private Double trade_money; //交易金钱
	
	private Double balance; //每单余额
	
	private Integer store_id; //关联店铺
	
	private String receipt_file; //提现凭证
	
	private Integer reckoning_detail_id; //关联detail
	
	private Long trade_time; //交易时间，为结算和提现申请时间
	
	private String apply_remarks; //申请备注
	
	private String pay_type; //支付类型
	
	private String stage_no; //提现期号
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Integer getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(Integer trade_type) {
		this.trade_type = trade_type;
	}

	public Integer getTrade_status() {
		return trade_status;
	}

	public void setTrade_status(Integer trade_status) {
		this.trade_status = trade_status;
	}

	public Double getTrade_money() {
		return trade_money;
	}

	public void setTrade_money(Double trade_money) {
		this.trade_money = trade_money;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}

	public String getReceipt_file() {
		return receipt_file;
	}

	public void setReceipt_file(String receipt_file) {
		this.receipt_file = receipt_file;
	}

	public Integer getReckoning_detail_id() {
		return reckoning_detail_id;
	}

	public void setReckoning_detail_id(Integer reckoning_detail_id) {
		this.reckoning_detail_id = reckoning_detail_id;
	}

	public Long getTrade_time() {
		return trade_time;
	}

	public void setTrade_time(Long trade_time) {
		this.trade_time = trade_time;
	}

	public String getApply_remarks() {
		return apply_remarks;
	}

	public void setApply_remarks(String apply_remarks) {
		this.apply_remarks = apply_remarks;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getStage_no() {
		return stage_no;
	}

	public void setStage_no(String stage_no) {
		this.stage_no = stage_no;
	}
	
	
}
