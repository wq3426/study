package com.enation.app.b2b2c.core.action.api.payment;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;
/**
 * 店铺支付API
 * @author LiFenLong
 *
 */
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("storePaymentApi")
public class StorePaymentApiAction extends WWAction{
	private IPaymentManager paymentManager;
	private IOrderManager orderManager;
	private IStoreOrderManager storeOrderManager;
	/**
	 * 跳转到第三方支付页面
	 * @param orderid 订单Id,Integer
	 * @param paymentid 支付方式Id,Integer
	 * @param payCfg 支付方式,PayCfg
	 * @return html和脚本
	 */
	public String execute(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		//订单id参数
		Integer orderId=  StringUtil.toInt( request.getParameter("orderid") ,null);
		if(orderId == null ){
			this.json=("必须传递orderid参数");
			return this.JSON_MESSAGE;
		}
		
		//支付方式id参数
		Integer paymentId=  StringUtil.toInt( request.getParameter("paymentid") ,null);
		StoreOrder order = this.storeOrderManager.get(orderId);
		
		if(order==null){
			this.json=("该订单不存在");
			return this.JSON_MESSAGE;
		}
		
		//如果没有传递支付方式id，则使用订单中的支付方式
		if(paymentId==null){
			paymentId = order.getPayment_id(); 
		}
		
		PayCfg payCfg = this.paymentManager.get(paymentId);

		IPaymentEvent paymentPlugin = SpringContextHolder.getBean(payCfg.getType());
		String payhtml = paymentPlugin.onPay(payCfg, order);

		// 用户更换了支付方式，更新订单的数据
		if (order.getPayment_id().intValue() != paymentId.intValue()) {
			this.orderManager.updatePayMethod(orderId, paymentId, payCfg.getType(), payCfg.getName());
		}
		this.json=(payhtml);
		return this.JSON_MESSAGE;
	}
	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}
	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}
	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}
}
