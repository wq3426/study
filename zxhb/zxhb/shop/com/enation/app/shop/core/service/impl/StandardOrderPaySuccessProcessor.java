package com.enation.app.shop.core.service.impl;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PaymentDetail;
import com.enation.app.shop.core.plugin.payment.IPaySuccessProcessor;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 标准订单支付成功处理器
 * @author kingapex
 *2013-9-24上午11:17:19
 */
public class StandardOrderPaySuccessProcessor implements IPaySuccessProcessor {
	private IOrderFlowManager orderFlowManager;
	private IOrderManager orderManager; 
	private CarInfoManager carInfoManager;
	private IAdminUserManager adminUserManager;
	private IOrderReportManager orderReportManager;
	private IDaoSupport daoSupport;
	
	@Override
	public void paySuccess(String ordersn, String tradeno, String ordertype) {
	/*	HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		Order order  = orderManager.get(ordersn);
		
		if( order.getPay_status().intValue()== OrderStatus.PAY_CONFIRM ){ //如果是已经支付的，不要再支付
			return ;
		}
		
		
		this.orderFlowManager.payConfirm(order.getOrder_id());

		try{
			//添加支付详细对象 @author LiFenLong
			//AdminUser adminUser = UserConext.getCurrentAdminUser();
			
			
			Double needPayMoney= StringUtil.toDouble(request.getParameter("total_fee")); //在线支付的金额
			Double orderGain = order.getGain(); //订单总优惠价格
			Integer isUseGain = order.getIsUseGain();//是否使用优惠
			
			int paymentid = orderReportManager.getPaymentLogId(order.getOrder_id());
			PaymentDetail paymentdetail=new PaymentDetail();
			paymentdetail.setAdmin_user("系统");
			paymentdetail.setPay_date(new Date().getTime());
			paymentdetail.setPay_money(needPayMoney);
			paymentdetail.setPayment_id(paymentid);
			orderReportManager.addPayMentDetail(paymentdetail);
			
			
			//修改订单状态为已付款付款
			this.daoSupport.execute("update es_payment_logs set paymoney=paymoney+? where payment_id=?",needPayMoney,paymentid);
			
			//更新订单的已付金额和订单使用奖励
			this.daoSupport.execute("update es_order set paymoney=paymoney+? ,need_pay_money=need_pay_money+? , gain = ? where order_id=?",needPayMoney,needPayMoney,orderGain,order.getOrder_id());
			
			
			//更新用户车牌号对应奖励额度
			this.daoSupport.execute("update es_carinfo set totalgain = totalgain-? where carplate = ?", orderGain,order.getCarplate());
			
			//更新每个orderItem的使用用余额
			if(isUseGain == 1 && orderGain > 0){
				JSONArray itemList = JSONArray.fromObject(order.getItemList());
				if(itemList!=null && itemList.size() > 0){
					for(int i = 0 ; i < itemList.size() ; i++){
						JSONObject item = JSONObject.fromObject(itemList.get(i));
						Double rewards_limit = ((Double)item.get("rewards_limit") * (Integer)item.get("num"));
						if(rewards_limit <= orderGain){//如果小于则扣去
							this.daoSupport.execute("update es_order_items set used_rewards = ? where item_id = ?",rewards_limit,(Integer)item.get("item_id"));
							orderGain -= rewards_limit;
						}else{
							this.daoSupport.execute("update es_order_items set used_rewards = ? where item_id = ?",orderGain,(Integer)item.get("item_id"));
							orderGain -= orderGain;
							break;
						}
					}
				}
			}
			//保险订单支付完成，更新下次购买保险时间 年份加1
			orderManager.updateInsurenextTime(order,carInfoManager);
			
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}*/
		
		
	}
	
	
	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}
	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}


	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}


	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
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


	public CarInfoManager getCarInfoManager() {
		return carInfoManager;
	}

	public void setCarInfoManager(CarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}

	
	
}
