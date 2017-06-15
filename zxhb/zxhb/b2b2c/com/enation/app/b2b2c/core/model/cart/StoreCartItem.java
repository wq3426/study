package com.enation.app.b2b2c.core.model.cart;

import com.enation.app.shop.core.model.support.CartItem;
/**
 * b2b2c 购物车商品
 * @author LiFenLong
 *
 */
public class StoreCartItem extends CartItem{
	private Integer store_id;	//店铺Id
	private String store_name;	//店铺名称
	private int goods_transfee_charge; //是否由卖家承担运费（即免运费）
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	public int getGoods_transfee_charge() {
		return goods_transfee_charge;
	}
	public void setGoods_transfee_charge(int goods_transfee_charge) {
		this.goods_transfee_charge = goods_transfee_charge;
	}
}
