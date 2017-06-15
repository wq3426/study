package com.enation.app.b2b2c.core.tag.order;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.component.bonus.tag.MemberUsedBonusListTag;
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
@Scope("prototype")
public class OrderBonusDetailTag extends BaseFreeMarkerTag{
	private IBonusManager bonusManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String ordersn =  request.getParameter("ordersn");
		String orderid  = request.getParameter("orderid");
		MemberBonus bonus  =null;
		if(!StringUtil.isNull(orderid)){
			bonus = bonusManager.getOrderBonus(null, Integer.parseInt(orderid));
		}else if(!StringUtil.isNull(ordersn)){
			bonus = bonusManager.getOrderBonus(ordersn,null);
		}
		return bonus;
	}
	public IBonusManager getBonusManager() {
		return bonusManager;
	}
	public void setBonusManager(IBonusManager bonusManager) {
		this.bonusManager = bonusManager;
	}
	
}


