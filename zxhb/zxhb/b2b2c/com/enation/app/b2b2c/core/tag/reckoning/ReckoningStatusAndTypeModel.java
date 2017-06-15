package com.enation.app.b2b2c.core.tag.reckoning;

import java.io.IOException;
import java.util.Map;

import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeStatus;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeType;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.OrderType;
import com.enation.framework.util.StringUtil;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class ReckoningStatusAndTypeModel implements TemplateDirectiveModel{

	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		int status=  StringUtil.toInt( params.get("status").toString() ,true);
		String type= params.get("type").toString();
		if("trade_status".equals(type)){
			String text = ReckoningTradeStatus.getName(status);
			env.getOut().write(text);
		}
		if("trade_type".equals(type)){
			String text = ReckoningTradeType.getName(status);
			env.getOut().write(text);
		}
		if("order_type".equals(type)){
			String text = OrderType.getOrderTypeTest(status);
			env.getOut().write(text);
		}
	
	}

}
