package com.enation.app.shop.component.ordercore.plugin.base;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.Depot;
import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.plugin.order.IOrderTabShowEvent;
import com.enation.app.shop.core.plugin.order.IShowOrderDetailHtmlEvent;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 订单详细页基本信息显示插件
 * @author kingapex
 *2012-2-16下午7:20:00
 */
@Component
public class OrderDetailBasePlugin extends AutoRegisterPlugin implements
		IOrderTabShowEvent,IShowOrderDetailHtmlEvent {
	
	private IOrderManager orderManager;
	private IMemberManager memberManager;
	private IOrderReportManager orderReportManager;
	private IDepotManager depotManager;
	private IPaymentManager paymentManager;
	private IDlyTypeManager dlyTypeManager;
	private ILogiManager logiManager;
	@Override
	public boolean canBeExecute(Order order) {
		 
		return true;
	}

	@Override
	public String getTabName(Order order) {
	 
		return "基本信息";
	}

	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}
	/**
	 * @param order 订单
	 * member 会员
	 * deliveryList 货运信息
	 * depotList 仓库列表
	 * payCfgList 支付方式列表
	 * OrderStatus 订单状态
	 */
	@Override
	public String onShowOrderDetailHtml(Order order) {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		FreeMarkerPaser freeMarkerPaser =FreeMarkerPaser.getInstance();
		
		List itemList = this.orderManager.listGoodsItems(order.getOrder_id()); // 订单商品列表
		freeMarkerPaser.setClz(this.getClass());
		if (order.getMember_id() != null){
			Member	member = this.memberManager.get(order.getMember_id());
			freeMarkerPaser.putData("member",member);
		}
		
		List<Delivery> deliveryList  = orderReportManager.getDeliveryList(order.getOrder_id());
		List<Depot>depotList=this.depotManager.list();
		List<DlyType> dlyTypeList  = dlyTypeManager.list();
		List<PayCfg> payCfgList= paymentManager.list();
		List logiList=logiManager.list();
		freeMarkerPaser.putData("deliveryList",deliveryList);		
		freeMarkerPaser.putData("itemList",itemList);		
		freeMarkerPaser.putData(OrderStatus.getOrderStatusMap());
		freeMarkerPaser.putData("depotList", depotList);
		freeMarkerPaser.putData("payCfgList", payCfgList);
		freeMarkerPaser.putData("dlyTypeList", dlyTypeList);
		//物流公司列表
		freeMarkerPaser.putData("logiList",logiList);
		freeMarkerPaser.putData("OrderStatus", OrderStatus.getOrderStatusMap());
		freeMarkerPaser.putData("eop_product",EopSetting.PRODUCT);
		freeMarkerPaser.putData("self_store",request.getParameter("self_store"));
		
		Integer depotid  = order.getDepotid();
		
		Depot depot = this.depotManager.get(depotid);
		freeMarkerPaser.putData("depot" ,depot);
		
		freeMarkerPaser.setPageName("base");
		return freeMarkerPaser.proessPageContent();
	}	


	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IOrderReportManager getOrderReportManager() {
		return orderReportManager;
	}

	public void setOrderReportManager(IOrderReportManager orderReportManager) {
		this.orderReportManager = orderReportManager;
	}

	public IDepotManager getDepotManager() {
		return depotManager;
	}

	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}

	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}

	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}

	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}

	public ILogiManager getLogiManager() {
		return logiManager;
	}

	public void setLogiManager(ILogiManager logiManager) {
		this.logiManager = logiManager;
	}
}
