package com.enation.app.b2b2c.core.action.backend.order;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.enation.app.shop.core.action.backend.OrderAction;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.OrderGridUrlKeyEnum;


/**
 * 自营店订单管理action
 * @author [kingapex]
 * @version [1.0]
 * @since [5.1]
 * 2015年11月6日下午3:15:41
 */

@ParentPackage("shop_default")
@Namespace("/b2b2c/admin")
@Action("selfStoreOrder")
@Results({
	@Result(name="list", type="freemarker", location="/shop/admin/order/order_list.html"),
	@Result(name="trash_list", type="freemarker", location="/shop/admin/order/trash_list.html"),
	@Result(name="detail", type="freemarker", location="/shop/admin/order/order_detail.html"),
	@Result(name="not_ship", type="freemarker", location="/shop/admin/order/not_ship.html"),
	@Result(name="not_pay", type="freemarker", location="/shop/admin/order/not_pay.html"),
	@Result(name="not_rog", type="freemarker", location="/shop/admin/order/not_rog.html"),
	@Result(name="list_express", type="freemarker", location="/shop/admin/order/listForExpressNo.html")
})
@Lazy(false)
@SuppressWarnings({"rawtypes","serial","static-access","unchecked"})
public class SelfStoreOrderAction extends OrderAction {
	
	@Autowired
	private IOrderManager selfStoreOrderManager;
	
	public IOrderManager getSelfStoreOrderManager() {
		return selfStoreOrderManager;
	}
	
 
	public void setSelfStoreOrderManager(IOrderManager selfStoreOrderManager) {
		this.selfStoreOrderManager = selfStoreOrderManager;
	}
	
	
	@Override
	public void setOrderManager(IOrderManager orderManager) {
		super.setOrderManager(this.getSelfStoreOrderManager());
	}


	
	/**
	 * 覆写父类的此方法，以改变grid视图中的数据源url
	 */
	@Override
	public Map<String, String> getGridUrl() {
		
		Map<String,String> urlMap  = new HashMap<String,String>();
		urlMap.put(OrderGridUrlKeyEnum.not_pay.toString(), "selfStoreOrder!listJson.do?order_state=wait_pay");
		urlMap.put(OrderGridUrlKeyEnum.not_ship.toString(), "selfStoreOrder!listJson.do?order_state=wait_ship");
		urlMap.put(OrderGridUrlKeyEnum.detail.toString(), "/shop/admin/order!detail.do?self_store=yes&");

		return urlMap;
	}

	
	
	
}
