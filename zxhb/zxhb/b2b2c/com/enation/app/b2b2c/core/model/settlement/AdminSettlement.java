package com.enation.app.b2b2c.core.model.settlement;

import java.io.Serializable;

import org.omg.CORBA.PRIVATE_MEMBER;

public class AdminSettlement implements Serializable{
	
	private Integer id ;
	
	private String sn ; //流水号
	
	private Integer member_id ; //交易id
	
	private String object_name; //交易对象名称
	
	private Double  trade_money; //提款金额 （支出）
	
	private Integer trade_big_type ; //交易大类
	
	private Integer  trade_small_type ; //交易小类
	
	private Integer admin_pay_type ; //支付给admin的类型
	
	private Double bank_handling_charge ;// 支付收款手续费
	
	private Double real_settlement_money ; // 实际结算给admin的金额 （收入） order paymoney * 千分之5
	
	private Double balance_record ; //余额记录

	private Long trade_time ; //交易时间（提现为处理时间）
	
	private String order_sn ; //订单号
	
	private String pay_sn; //第三方交易流水号
	
	public Integer getId() {
		return id;
	}

	public String getSn() {
		return sn;
	}

	public Integer getMember_id() {
		return member_id;
	}

	public Integer getTrade_big_type() {
		return trade_big_type;
	}

	public Integer getTrade_small_type() {
		return trade_small_type;
	}

	public Integer getAdmin_pay_type() {
		return admin_pay_type;
	}

	public Double getBank_handling_charge() {
		return bank_handling_charge;
	}

	public Double getReal_settlement_money() {
		return real_settlement_money;
	}


	public Double getBalance_record() {
		return balance_record;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}

	public void setTrade_big_type(Integer trade_big_type) {
		this.trade_big_type = trade_big_type;
	}

	public void setTrade_small_type(Integer trade_small_type) {
		this.trade_small_type = trade_small_type;
	}

	public void setAdmin_pay_type(Integer admin_pay_type) {
		this.admin_pay_type = admin_pay_type;
	}

	public void setBank_handling_charge(Double bank_handling_charge) {
		this.bank_handling_charge = bank_handling_charge;
	}

	public void setReal_settlement_money(Double real_settlement_money) {
		this.real_settlement_money = real_settlement_money;
	}


	public void setBalance_record(Double balance_record) {
		this.balance_record = balance_record;
	}

	public Long getTrade_time() {
		return trade_time;
	}

	public void setTrade_time(Long trade_time) {
		this.trade_time = trade_time;
	}

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public String getObject_name() {
		return object_name;
	}

	public void setObject_name(String object_name) {
		this.object_name = object_name;
	}

	public Double getTrade_money() {
		return trade_money;
	}

	public void setTrade_money(Double trade_money) {
		this.trade_money = trade_money;
	}

	public String getPay_sn() {
		return pay_sn;
	}

	public void setPay_sn(String pay_sn) {
		this.pay_sn = pay_sn;
	}
	
	
}
