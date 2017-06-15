package com.enation.app.shop.core.tag;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;


@Component
@Scope("prototype")
public class BrandsByCatIdTag extends BaseFreeMarkerTag{

	private IBrandManager brandManager;
	
	/**
	 * 根据tagid获得该分类下的品牌列表
	 */
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		int catid = StringUtil.toInt(params.get("catid").toString(),true);
		List<Brand> brandList  =brandManager.getBrandByCatId(catid);
		return brandList;
	}

	public IBrandManager getBrandManager() {
		return brandManager;
	}

	public void setBrandManager(IBrandManager brandManager) {
		this.brandManager = brandManager;
	}
	
}
