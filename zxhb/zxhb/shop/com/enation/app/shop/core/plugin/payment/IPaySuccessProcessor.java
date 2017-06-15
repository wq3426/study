package com.enation.app.shop.core.plugin.payment;

/**
 * 支付成功处理器
 * @author kingapex
 *2013-9-24上午10:56:24
 */
public interface IPaySuccessProcessor {
	
	
	
	/**
	 * 对于支付成功的处理方法
	 * @param ordersn 订单编号
	 * @param tradeno 第三方交易流水号
	 * @param ordertype 订单类型 已知的：standard 标准订单，credit:信用账户充值
	 */
	public void paySuccess(String ordersn,String tradeno ,String ordertype);
	
	
	
}
