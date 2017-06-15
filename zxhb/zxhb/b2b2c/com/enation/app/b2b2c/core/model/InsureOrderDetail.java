package com.enation.app.b2b2c.core.model;

/**
 * 保险订单详情model类
 * @author wangqiang 2016年7月13日 上午10:07:34
 *
 */
public class InsureOrderDetail {
	private Integer id;
	private Integer order_id;//订单主键id
	private String carplate;//车牌号
	private Integer insure_id;//保险公司id
	private String insure_company;//保险公司名称
	private String policy_no;//保单号
	private Long insure_starttime;//保险开始时间
	private Long insure_endtime;//保险结束时间
	private Double compulsory_fee;//交强险
	private Double damage_fee;//车损险
	private Double theft_fee;//盗抢险
	private Double exempt_fee;//不计免赔险
	private Double thirdparty_fee;//第三者责任险
	private Double insure_price;//订单支付金额
	private Long create_time;//订单生成时间
	private Long update_time;//更新时间
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
	public String getCarplate() {
		return carplate;
	}
	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}
	public Integer getInsure_id() {
		return insure_id;
	}
	public void setInsure_id(Integer insure_id) {
		this.insure_id = insure_id;
	}
	public String getInsure_company() {
		return insure_company;
	}
	public void setInsure_company(String insure_company) {
		this.insure_company = insure_company;
	}
	public String getPolicy_no() {
		return policy_no;
	}
	public void setPolicy_no(String policy_no) {
		this.policy_no = policy_no;
	}
	public Long getInsure_starttime() {
		return insure_starttime;
	}
	public void setInsure_starttime(Long insure_starttime) {
		this.insure_starttime = insure_starttime;
	}
	public Long getInsure_endtime() {
		return insure_endtime;
	}
	public void setInsure_endtime(Long insure_endtime) {
		this.insure_endtime = insure_endtime;
	}
	public Double getCompulsory_fee() {
		return compulsory_fee;
	}
	public void setCompulsory_fee(Double compulsory_fee) {
		this.compulsory_fee = compulsory_fee;
	}
	public Double getDamage_fee() {
		return damage_fee;
	}
	public void setDamage_fee(Double damage_fee) {
		this.damage_fee = damage_fee;
	}
	public Double getTheft_fee() {
		return theft_fee;
	}
	public void setTheft_fee(Double theft_fee) {
		this.theft_fee = theft_fee;
	}
	public Double getExempt_fee() {
		return exempt_fee;
	}
	public void setExempt_fee(Double exempt_fee) {
		this.exempt_fee = exempt_fee;
	}
	public Double getThirdparty_fee() {
		return thirdparty_fee;
	}
	public void setThirdparty_fee(Double thirdparty_fee) {
		this.thirdparty_fee = thirdparty_fee;
	}
	public Double getInsure_price() {
		return insure_price;
	}
	public void setInsure_price(Double insure_price) {
		this.insure_price = insure_price;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public Long getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Long update_time) {
		this.update_time = update_time;
	}
}
