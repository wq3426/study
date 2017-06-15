package com.enation.app.shop.component.ordercore.plugin.timeout;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.job.IEveryDayExecuteEvent;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 不付款的订单72小时内就取消
 * @author LiFenLong
 *
 */
@Component
public class TimeOutOrderPrint extends AutoRegisterPlugin implements IEveryDayExecuteEvent{
	private IDaoSupport daoSupport;
	private IOrderFlowManager orderFlowManager;
	@Override
	public void everyDay() {//订单取消时间修改为1天
		String sql="SELECT order_id from es_order  WHERE disabled=0 AND create_time+?<? AND (status=? or status=?) AND create_time>?";
		List<Map> list= daoSupport.queryForList(sql,(24*3600),DateUtil.getDateline(),OrderStatus.ORDER_NOT_PAY,OrderStatus.ORDER_NOT_CONFIRM,1398873600);
		for (Map map:list) {
			orderFlowManager.cancel(Integer.parseInt(map.get("order_id").toString()), "订单24小时没有进行付款");
		}
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}
	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}
}
