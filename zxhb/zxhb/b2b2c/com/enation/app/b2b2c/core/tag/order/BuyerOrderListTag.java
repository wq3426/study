package com.enation.app.b2b2c.core.tag.order;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 获取买家的订单<br>
 * 在会员中心使用
 * @author kingaepx
 *
 */
@Component
public class BuyerOrderListTag extends BaseFreeMarkerTag{
	private IStoreOrderManager storeOrderManager;
	private IStoreMemberManager storeMemberManager;
	@SuppressWarnings("unchecked")
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Member member = storeMemberManager.getStoreMember();
		if(member==null){
			throw new TemplateModelException("未登陆不能使用此标签[MemberOrderListTag]");
		}
		Map result = new HashMap();
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize = 10;
		String status = request.getParameter("status");
		String keyword = request.getParameter("keyword");
		
		Page ordersPage = storeOrderManager.pageBuyerOrders(Integer.valueOf(page), pageSize,status,keyword);
		Long totalCount = ordersPage.getTotalCount();
		
		result.put("totalCount", totalCount);
		result.put("pageSize", pageSize);
		result.put("page", page);
		result.put("ordersList", ordersPage);

		//Author LiFenLong
		Map<String,Object> orderstatusMap=OrderStatus.getOrderStatusMap();
		for (String orderStatus: orderstatusMap.keySet()) {
			result.put(orderStatus, orderstatusMap.get(orderStatus));
		}
		
		if(status!=null){
			result.put("status",Integer.valueOf( status) );
		}
		
		return result;
	}
	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}

	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}
 
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
