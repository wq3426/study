package com.enation.app.b2b2c.core.service;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.model.PaymentDetail;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.plugin.payment.IPaySuccessProcessor;
import com.enation.app.shop.core.service.ICarInfoManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.impl.CarInfoManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 店铺订单支付成功处理器
 * @author LiFenLong
 *
 */
@Component
public class B2b2cOrderPaySuccessProcessor implements IPaySuccessProcessor {
	private IStoreOrderFlowManager storeOrderFlowManager;
	private IOrderManager orderManager; 
	private IOrderReportManager orderReportManager;
	private IDaoSupport daoSupport;
	private CarInfoManager carInfoManager;
	private IStoreOrderManager storeOrderManager;
	private IBonusManager bonusManager;
	private OrderPluginBundle orderPluginBundle;
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.payment.IPaySuccessProcessor#paySuccess(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void paySuccess(String ordersn, String tradeno, String ordertype) {
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		StoreOrder order  = storeOrderManager.get(ordersn);
		double payMoney= StringUtil.toDouble(request.getParameter("total_fee"));
		order.setPaymoney(payMoney);
		double orderGain = order.getGain(); //订单总优惠价格
		if(order.getPay_status().intValue()== OrderStatus.PAY_CONFIRM ){ //如果是已经支付的，不要再支付
			return ;
		}
		
		/*//获取优惠券优惠价格
		Double bonus_price = 0d;
		if(order.getParent_id()!=null){
			double type_money = bonusManager.getBonusPrice(order.getOrder_id(),order.getSn());
			bonus_price = CurrencyUtil.add(bonus_price, type_money);
		}else{
			List<StoreOrder> storeOrders = storeOrderManager.getChildOrder(order.getOrder_id());
			for(int i = 0 ; i < storeOrders.size() ; i++){
				StoreOrder storeOrder = storeOrders.get(i);
				double type_money = bonusManager.getBonusPrice(order.getOrder_id(),order.getSn());
				bonus_price = CurrencyUtil.add(bonus_price, type_money);
			}
		}*/
		
		
		/*if(CurrencyUtil.add(CurrencyUtil.add(payMoney, orderGain),bonus_price) != order.getNeed_pay_money()){
			throw new RuntimeException("支付金额和订单金额不一致");
		}*/
		
		try {
			if (order.getParent_id() == null) {// 如果为主订单，走主订单流程
				this.mainOrderFlow(order,tradeno);
 
			} else {// 走子订单流程
				this.childOrderFlow(order,tradeno);
			}
			
			//统一更新订单状态
			this.storeOrderFlowManager.payConfirm(order.getOrder_id());
			
			//更改保险/保养相关信息
			orderManager.updateInsureAndRepairInfo(order,carInfoManager);
			
			//更新收益使用记录
			orderManager.updateIncomeUseHistory(order);
			
			//更新用户车牌号对应奖励额度
			this.daoSupport.execute("update es_carinfo set totalgain = totalgain-? where carplate = ?", orderGain,order.getCarplate());
			
		
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		
		
	}
	

	/**
	 * 主订单确认付款流程
	 * @param order
	 */
	private void mainOrderFlow(StoreOrder order,String tradeno) {
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		Double orderGain = order.getGain(); //订单总优惠价格
		Integer isUseGain = order.getIsUseGain();//是否使用优惠
		List<StoreOrder> storeOrders = storeOrderManager.getChildOrder(order.getOrder_id());
		
		if (isUseGain == 1 && orderGain > 0) {
			for (int orderIndex = 0; orderIndex < storeOrders.size(); orderIndex++) {
				StoreOrder childOrder = storeOrders.get(orderIndex);
				// 更新每个orderItem的使用用余额
				double childGain = childOrder.getGain();
				JSONArray itemList = JSONArray.fromObject(childOrder.getItemList());
				if (itemList != null && itemList.size() > 0) {
					for (int i = 0; i < itemList.size(); i++) {
						JSONObject item = JSONObject.fromObject(itemList.get(i));
						Double rewards_limit = ((Double) item.get("rewards_limit") *  item.getInt("num"));
						if (rewards_limit <= childGain) {// 如果小于则扣去
							this.daoSupport.execute(
									"update es_order_items set used_rewards = ? where item_id = ?",
									rewards_limit, (Integer) item.get("item_id"));
							childGain = CurrencyUtil.sub(childGain, rewards_limit);
						} else {
							this.daoSupport.execute(
									"update es_order_items set used_rewards = ? where item_id = ?", childGain,
									(Integer) item.get("item_id"));
							childGain = CurrencyUtil.sub(childGain, childGain);
							break;
						}
					}
				}
			}
		}
		
		// 循环生成子订单明细,并修改优惠券为已支付
		for (int i = 0; i < storeOrders.size(); i++) {
			StoreOrder childOrder = storeOrders.get(i);
			int paymentid = orderReportManager.getPaymentLogId(storeOrders.get(i).getOrder_id());
			PaymentDetail paymentdetail = new PaymentDetail();
			paymentdetail.setAdmin_user("系统");
			paymentdetail.setPay_date(new Date().getTime());
			paymentdetail.setPay_money(CurrencyUtil.sub(childOrder.getNeed_pay_money(), childOrder.getGain()));// 如果有优惠券要减去优惠券
			paymentdetail.setPayment_id(paymentid);
			orderReportManager.addPayMentDetail(paymentdetail);
			double type_money = bonusManager.getBonusPrice(childOrder.getOrder_id(),childOrder.getSn());
			double payMoney = CurrencyUtil.sub(childOrder.getNeed_pay_money(), childOrder.getGain());
			payMoney = CurrencyUtil.sub(payMoney,type_money);
			this.daoSupport.execute("update es_order set paymoney=?,isUseGain=? where order_id=?",
					payMoney,isUseGain,
					childOrder.getOrder_id());
			this.daoSupport.execute("update es_payment_logs set paymoney=? where payment_id=?", 
					payMoney,
					paymentid);
			
			//修改优惠券为已使用
			this.daoSupport.execute("update es_member_bonus set used_time = ?,used = ? where order_id = ? and order_sn = ?",
					System.currentTimeMillis(),
					1,
					childOrder.getOrder_id(),
					childOrder.getSn());
			childOrder.setPaymoney(payMoney);
			orderPluginBundle.onConfirmPay(childOrder,tradeno);
		}
		
	}
	
	
	/**
	 * 子订单确认付款流程
	 * @param 第三方交易流水号
	 * @param order
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	private void childOrderFlow(StoreOrder order,String tradeno) {
		try{
			HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
			Double payMoney= CurrencyUtil.round(StringUtil.toDouble(request.getParameter("total_fee")), 2); //在线支付的金额
			Double orderGain = CurrencyUtil.round(order.getGain(),2); //订单总优惠价格
			Integer isUseGain = order.getIsUseGain();//是否使用优惠
			
			if (isUseGain == 1 && orderGain > 0) {//计算used_reward
				JSONArray itemList = JSONArray.fromObject(order.getItemList());
				if (itemList != null && itemList.size() > 0) {
					for (int i = 0; i < itemList.size(); i++) {
						JSONObject item = JSONObject.fromObject(itemList.get(i));
						Double rewards_limit = CurrencyUtil.mul((Double)item.get("rewards_limit"), item.getInt("num"));
						if (rewards_limit <= orderGain) {// 如果小于则扣去
							this.daoSupport.execute(
									"update es_order_items set used_rewards = ? where item_id = ?",
									rewards_limit,
									(Integer) item.get("item_id"));
							orderGain = CurrencyUtil.sub(orderGain, rewards_limit) ;
						} else {
							this.daoSupport.execute(
									"update es_order_items set used_rewards = ? where item_id = ?",
									orderGain,
									(Integer) item.get("item_id"));
							orderGain = CurrencyUtil.sub(orderGain, orderGain);;
							break;
						}
					}
				}
			}
			
			//生成支付详情
			int paymentid = orderReportManager.getPaymentLogId(order.getOrder_id());
			PaymentDetail paymentdetail=new PaymentDetail();
			paymentdetail.setAdmin_user("系统");
			paymentdetail.setPay_date(new Date().getTime());
			paymentdetail.setPay_money(payMoney);
			paymentdetail.setPayment_id(paymentid);
			orderReportManager.addPayMentDetail(paymentdetail);
			
			
			//修改子订单log
			this.daoSupport.execute("update es_payment_logs set paymoney=? where payment_id=?",payMoney,paymentid);
			
			//更新子订单付款金额
			this.daoSupport.execute("update es_order set paymoney=?  where order_id=?",payMoney,order.getOrder_id());
			
			//修改优惠券为已使用
			this.daoSupport.execute("update es_member_bonus set used_time = ?,used = ? where order_id = ? and order_sn = ?", System.currentTimeMillis(),1,order.getOrder_id(),order.getSn());
			
			//支付成功后生成结算单和更新admin金额
			orderPluginBundle.onConfirmPay(order,tradeno);
		}catch(Exception e){
			throw e;
		}
	}
	
	
	
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public IOrderReportManager getOrderReportManager() {
		return orderReportManager;
	}
	public void setOrderReportManager(IOrderReportManager orderReportManager) {
		this.orderReportManager = orderReportManager;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}
	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}
	public ICarInfoManager getCarInfoManager() {
		return carInfoManager;
	}
	public void setCarInfoManager(CarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}


	public IStoreOrderFlowManager getStoreOrderFlowManager() {
		return storeOrderFlowManager;
	}


	public void setStoreOrderFlowManager(IStoreOrderFlowManager storeOrderFlowManager) {
		this.storeOrderFlowManager = storeOrderFlowManager;
	}


	public IBonusManager getBonusManager() {
		return bonusManager;
	}


	public void setBonusManager(IBonusManager bonusManager) {
		this.bonusManager = bonusManager;
	}


	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}


	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}
	
	
}
