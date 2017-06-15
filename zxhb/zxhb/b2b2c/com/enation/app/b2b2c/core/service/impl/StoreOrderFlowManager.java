package com.enation.app.b2b2c.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.service.IStoreOrderFlowManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.IPointHistoryManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.OrderType;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.util.DateUtil;

@Component
public class StoreOrderFlowManager extends BaseSupport implements IStoreOrderFlowManager {
	private IStoreOrderManager storeOrderManager;
	private OrderPluginBundle orderPluginBundle;
	
	/**
	 * 后台 记录订单操作日志
	 * @param order_id
	 * @param message
	 * @param op_id
	 * @param op_name
	 */
	private void log(Integer order_id,String message){
		AdminUser adminUser = UserConext.getCurrentAdminUser();
		OrderLog orderLog = new OrderLog();
		orderLog.setMessage(message);
		if(adminUser==null){
			orderLog.setOp_id(0);
			orderLog.setOp_name("系统检测");
		}else{
			orderLog.setOp_id(adminUser.getUserid());
			orderLog.setOp_name(adminUser.getUsername());
		}
		orderLog.setOp_time(DateUtil.getDateline());
		orderLog.setOrder_id(order_id);
		this.baseDaoSupport.insert("order_log", orderLog);
	}
	
	private void log(Integer order_id,String message,Integer op_id,String op_name){
		OrderLog orderLog = new OrderLog();
		orderLog.setMessage(message);
		orderLog.setOp_id(op_id);
		orderLog.setOp_name( op_name );
		orderLog.setOp_time(DateUtil.getDateline());
		orderLog.setOrder_id(order_id);
		this.baseDaoSupport.insert("order_log", orderLog);
	}
	
	
	@Override
	public Order payConfirm(int orderId) {
		StoreOrder order = storeOrderManager.get(orderId);
		
		int payStatus = OrderStatus.PAY_CONFIRM;// 已付款
		int orderStatus = OrderStatus.ORDER_PAY_CONFIRM; 
		
//		if(order.getOrder_type() == OrderType.ZA_INSURANCE || order.getOrder_type() == OrderType.STORE_INSURANCE){//如果为保险类型，付款之后为已完成
//			orderStatus = OrderStatus.ORDER_COMPLETE;
//		}
		
		if(logger.isDebugEnabled()){
			logger.debug("更新订单状态["+orderStatus+"],支付状态["+payStatus+ "]");
		}
		
		 AdminUser adminUser = UserConext.getCurrentAdminUser();
			
		 String opuser = "系统";
		 if(adminUser!=null){
			 opuser  = adminUser.getUsername()+"["+adminUser.getRealname()+"]";
		 }
		 
		String  sql = "";
		//父类订单不维护
		if(order.getParent_id()==null){//如果为父类订单，修改子订单的状态
			sql = "update "+this.getTableName("order")+" set status="+orderStatus+",pay_status="+payStatus+"  where  parent_id = ?";
			this.daoSupport.execute(sql, orderId);
			 
		}
		sql = "update payment_logs set status=1,pay_date=?,admin_user=? where order_id=?";// 核销应收
		this.baseDaoSupport.execute(sql, DateUtil.getDateline(), opuser, order.getOrder_id());
		
		sql = "update "+this.getTableName("order")+" set status="+orderStatus+",pay_status="+payStatus+"  where order_id=?";
		this.daoSupport.execute(sql,orderId);
	
		if(adminUser!=null){
			this.log(orderId, "确认付款");
		}else{
			this.log(orderId, "确认付款", null, "系统");
		}
			
			
		order.setStatus( orderStatus);
		order.setPay_status( payStatus );
		
		
		return order;
	}

	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}

	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}

	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}

	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}
	
	

}
