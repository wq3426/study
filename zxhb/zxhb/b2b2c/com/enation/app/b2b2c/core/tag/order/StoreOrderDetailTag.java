package com.enation.app.b2b2c.core.tag.order;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.shop.core.model.Order;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;

/**
 * 查看店铺订单详细标签
 * @author LiFenLong
 *
 */
@Component
public class StoreOrderDetailTag extends BaseFreeMarkerTag{
	private IStoreOrderManager storeOrderManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String ordersn =  request.getParameter("ordersn");
		String orderid  = request.getParameter("orderid");
		
		Order order  =null;
		if(!StringUtil.isEmpty(orderid)){
			order = storeOrderManager.get(Integer.valueOf(orderid));
			
		}else if(!StringUtil.isEmpty(ordersn)){
			order =	storeOrderManager.get(ordersn);
			
		}else{
			throw new UrlNotFoundException();
			
		}
		
		return order;
	}
	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}
	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}
}
