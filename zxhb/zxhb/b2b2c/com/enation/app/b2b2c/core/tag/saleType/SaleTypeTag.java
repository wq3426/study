package com.enation.app.b2b2c.core.tag.saleType;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.sale.SaleType;
import com.enation.app.b2b2c.core.service.saleType.ISaleTypeManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 营销类型信息tag
 * @author Yunbs
 * @version v1.0, 2016年8月17日
 * @since v5.2
 */
@Component
@Scope("prototype")
public class SaleTypeTag extends BaseFreeMarkerTag {
	private ISaleTypeManager saleTypeManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		List<SaleType> saleTypeList = saleTypeManager.saleTypeList();
		return saleTypeList;
	}
	public ISaleTypeManager getSaleTypeManager() {
		return saleTypeManager;
	}
	public void setSaleTypeManager(ISaleTypeManager saleTypeManager) {
		this.saleTypeManager = saleTypeManager;
	}
	
}
