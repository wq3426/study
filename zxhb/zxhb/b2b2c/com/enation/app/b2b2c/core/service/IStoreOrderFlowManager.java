package com.enation.app.b2b2c.core.service;

import com.enation.app.shop.core.model.Order;

public interface IStoreOrderFlowManager {
	/**
	 * 确认付款
	 * @param orderId 订单标识
	 * @param paymentId 订单付款ID 2 为货到付款
	 */
	public Order payConfirm(int orderId);
}
