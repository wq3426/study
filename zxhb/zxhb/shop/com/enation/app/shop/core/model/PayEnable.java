package com.enation.app.shop.core.model;


/**
 * 可支付接口
 * @author kingapex
 *2013-9-23下午3:01:53
 */
public interface PayEnable {
	
	/**
	 * 需要支付的金额
	 * @return
	 */
	public Double getNeedPayMoney();
	
	
	/**
	 * 订单编号
	 * @return
	 */
	public String getSn();
	
	
	/**
	 * 订单类型
	 * @return
	 */
	public String getOrderType();
	
	
}
