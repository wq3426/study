package com.enation.app.shop.core.plugin.order;

import com.enation.app.shop.core.model.Order;

/**
 * Descriptiion:确认服务时间
 * @author xinzai
 * @date 2016年7月12日下午5:18:39
 */
public interface IOrderConfirmServiceEvent {
	
	public void comfirmService(Order order);
	
}
