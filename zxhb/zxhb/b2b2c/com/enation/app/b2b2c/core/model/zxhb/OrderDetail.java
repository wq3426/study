package com.enation.app.b2b2c.core.model.zxhb;

/**
 * @Description 预约订单详情实体类
 *
 * @createTime 2016年12月7日 下午9:11:17
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
public class OrderDetail {
	private Integer order_id;//订单id
	private String order_sn;//订单序列号
	private Integer user_id;//用户id
	private Integer status;//订单状态                       9 已预约   0 待支付  1 已支付，待发货  2 配送中 3 已送达   8 已取消
	private String receive_user;//收货人
	private String shipping_no;//物流单号
	private String shipping_id;//物流id
	private Integer goods_id;//商品id
	private Integer gg_spec_value_id;//规格id
	private Integer ta_spec_value_id;//图案id
	private Integer order_count;//商品数量
	private Double unit_price;//单价
	private Double total_price;//总价
	private Integer pay_type;//支付类型   1 支付宝  2 微信  3 银联
	private String trade_no;//交易号
	private String address;//邮寄地址
	private Long create_time;//订单创建时间
	private String referee_code ; //邀请码
	private String referee ;//邀请码姓名
	
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
	public String getOrder_sn() {
		return order_sn;
	}
	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getShipping_no() {
		return shipping_no;
	}
	public void setShipping_no(String shipping_no) {
		this.shipping_no = shipping_no;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public Integer getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}
	public Integer getGg_spec_value_id() {
		return gg_spec_value_id;
	}
	public void setGg_spec_value_id(Integer gg_spec_value_id) {
		this.gg_spec_value_id = gg_spec_value_id;
	}
	public Integer getTa_spec_value_id() {
		return ta_spec_value_id;
	}
	public void setTa_spec_value_id(Integer ta_spec_value_id) {
		this.ta_spec_value_id = ta_spec_value_id;
	}
	public Integer getOrder_count() {
		return order_count;
	}
	public void setOrder_count(Integer order_count) {
		this.order_count = order_count;
	}
	public String getReferee() {
		return referee;
	}
	public void setReferee(String referee) {
		this.referee = referee;
	}
	public String getReferee_code() {
		return referee_code;
	}
	public void setReferee_code(String referee_code) {
		this.referee_code = referee_code;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getShipping_id() {
		return shipping_id;
	}
	public void setShipping_id(String shipping_id) {
		this.shipping_id = shipping_id;
	}
	public Double getUnit_price() {
		return unit_price;
	}
	public void setUnit_price(Double unit_price) {
		this.unit_price = unit_price;
	}
	public Double getTotal_price() {
		return total_price;
	}
	public void setTotal_price(Double total_price) {
		this.total_price = total_price;
	}
	public String getTrade_no() {
		return trade_no;
	}
	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}
	public Integer getPay_type() {
		return pay_type;
	}
	public void setPay_type(Integer pay_type) {
		this.pay_type = pay_type;
	}
	public String getReceive_user() {
		return receive_user;
	}
	public void setReceive_user(String receive_user) {
		this.receive_user = receive_user;
	}
}
