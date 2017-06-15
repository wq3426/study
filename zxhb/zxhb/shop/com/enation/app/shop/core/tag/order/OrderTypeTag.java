package com.enation.app.shop.core.tag.order;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.OrderType;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 订单类型值标签
 * @author kingapex
 *2013-9-25下午9:51:40
 */
@Component
@Scope("prototype")
public class OrderTypeTag extends BaseFreeMarkerTag {

	/**
	 * @param 无
	 * @return 订单类型值，参见：{@link OrderStatus#getOrderStatusMap()} 
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		return  OrderType.getOrderTypeMap();
		
	}

}
