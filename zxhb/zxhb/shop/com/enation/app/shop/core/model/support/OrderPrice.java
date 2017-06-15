package com.enation.app.shop.core.model.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.enation.framework.util.CurrencyUtil;

/**
 * 订单价格信息实体
 * @author kingapex
 * 2010-5-30上午11:58:33
 * 
 */
public class OrderPrice implements Serializable {
	
	//商品价格，经过优惠过的
	private Double goodsPrice;
		
	//订单总价，商品价格+运费
	private Double orderPrice;

	//配送费用
	private Double shippingPrice; 
	
	//需要支付的金额(应付款)
	private Double needPayMoney; 
	
	//优惠的价格总计
	private Double discountPrice; 
	
	
 
	//商品重量
	private Double weight ; 
	
	//可获得积分
	private Integer point; 
	
	//获取订单商品总数
	private Integer goods_num;
	
	private Map<String,Object> discountItem; //使用优惠的项目
	
	public OrderPrice(){
		discountItem = new HashMap<String, Object>();
	
	}
 
	public Double getGoodsPrice() {
		if(goodsPrice!=null){
			goodsPrice=CurrencyUtil.round(goodsPrice, 2);
		}else{
			return 0.00d;
		}
		return goodsPrice;
	}
	public void setGoodsPrice(Double goodsPrice) {
		this.goodsPrice = goodsPrice;
	}
	public Double getOrderPrice() {
		if(orderPrice!=null){
			orderPrice=CurrencyUtil.round(orderPrice, 2);
		}else{
			return 0.00d;
		}
		return orderPrice;
	}
	public void setOrderPrice(Double orderPrice) {
		this.orderPrice = orderPrice;
	}
	public Double getDiscountPrice() {
		discountPrice= discountPrice==null?0:discountPrice;
		if(discountPrice!=null){
			discountPrice=CurrencyUtil.round(discountPrice, 2);
		}else{
			return 0.00d;
		}
		return discountPrice;
	}
	public void setDiscountPrice(Double discountPrice) {
		this.discountPrice = discountPrice;
	}
	public Integer getPoint() {
		return point;
	}
	public void setPoint(Integer point) {
		this.point = point;
	}
	 
	public Double getWeight() {
		if(weight!=null){
			weight=CurrencyUtil.round(weight, 2);
		}else{
			return 0.00d;
		}
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public Double getShippingPrice() {
		if(shippingPrice!=null){
			shippingPrice=CurrencyUtil.round(shippingPrice, 2);
		}else{
			return 0.00d;
		}
		return shippingPrice;
	}
	public void setShippingPrice(Double shippingPrice) {
		this.shippingPrice = shippingPrice;
	}
	public Map<String, Object> getDiscountItem() {
		return discountItem;
	}
	public void setDiscountItem(Map<String, Object> discountItem) {
		this.discountItem = discountItem;
	}
	
	public Double getNeedPayMoney() {
		if(needPayMoney!=null){
			needPayMoney=CurrencyUtil.round(needPayMoney, 2);
		}else{
			return 0.00d;
		}
		return needPayMoney;
	}
	public void setNeedPayMoney(Double needPayMoney) {
		this.needPayMoney = needPayMoney;
	}

	public Integer getGoods_num() {
		return goods_num;
	}

	public void setGoods_num(Integer goods_num) {
		this.goods_num = goods_num;
	}
 
	
	
}
