package com.enation.app.b2b2c.core.model.order;

import com.enation.app.shop.core.model.Order;
import com.enation.framework.database.NotDbField;

/**
 * 店铺订单
 * @author LiFenLong
 *
 */
public class StoreOrder extends Order{
	private Integer store_id;//店铺Id
	private Integer parent_id;//订单父类Id
	private String [] storeids;
	private Double commission;	//订单佣金价格
	private Integer bill_status;	//订单结算状态
	private Integer bill_sn;	//结算订单编号
	private Double  bonus_money;//对应优惠券价格，非数据库字段
	/**
	 * 是否是子订单
	 */
	private boolean is_child_order;
	
	private String store_name; //店铺名称
	
	
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	public Double getCommission() {
		return commission;
	}
	public void setCommission(Double commission) {
		this.commission = commission;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public Integer getParent_id() {
		return parent_id;
	}
	public void setParent_id(Integer parent_id) {
		this.parent_id = parent_id;
	}
	@NotDbField
	public String getOrderType() {
		return "b";
	}
	@NotDbField
	public String[] getStoreids() {
		return storeids;
	}
	public void setStoreids(String[] storeids) {
		this.storeids = storeids;
	}
	public Integer getBill_status() {
		return bill_status;
	}
	public void setBill_status(Integer bill_status) {
		this.bill_status = bill_status;
	}
	public Integer getBill_sn() {
		return bill_sn;
	}
	public void setBill_sn(Integer bill_sn) {
		this.bill_sn = bill_sn;
	}
	
	@NotDbField
	public boolean getIs_child_order() {
		return is_child_order;
	}
	public void setIs_child_order(boolean is_child_order) {
		this.is_child_order = is_child_order;
	}
	public Double getBonus_money() {
		return bonus_money;
	}
	public void setBonus_money(Double bonus_money) {
		this.bonus_money = bonus_money;
	}
	
}
