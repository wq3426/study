package com.enation.app.shop.core.tag.order;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;


/**
 * 订单详细标签 
 * @author kingapex
 *2013-7-28上午10:25:29
 */
@Component
@Scope("prototype")
public class OrderDetailTag extends BaseFreeMarkerTag {
	private IOrderManager orderManager ;
	
	/**
	 * 
	 * 订单详细标签
	 * 必须传递orderid或ordersn参数
	 * @param orderid,订单id，int型
	 * @param ordersn,订单sn,String 型
	 * @return 订单详细 ，Order型
	 * {@link Order}
	 */
	@Override
	public Object exec(Map args) throws TemplateModelException {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String ordersn =  request.getParameter("ordersn");
		String orderid  = request.getParameter("orderid");
		
		Order order  =null;
		if(!StringUtil.isEmpty(orderid) && orderid.matches("\\d+")){
			order=orderManager.get(Integer.valueOf(orderid));
			
		}else if(!StringUtil.isEmpty(ordersn) ){
			order=	orderManager.get(ordersn);
			
		}else{
			throw new UrlNotFoundException();
			
		}
		
		return order;
	}
	
	
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}


}
