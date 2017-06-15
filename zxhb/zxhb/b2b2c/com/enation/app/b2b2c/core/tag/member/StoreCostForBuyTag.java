package com.enation.app.b2b2c.core.tag.member;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.StoreCost;
import com.enation.app.base.core.service.IStoreCostManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商家广告信息免费发布次数和已购买次数tag
 * @author Yunbs
 * @version v1.0, 2016年8月17日
 * @since v5.2
 */
@Component
@Scope("prototype")
public class StoreCostForBuyTag extends BaseFreeMarkerTag {
	private IStoreCostManager storeCostManager;
	private IStoreMemberManager storeMemberManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map result=new HashMap();
		StoreMember storeMember =storeMemberManager.getStoreMember() ;
		Integer store_id = storeMember.getStore_id();
		int saleType = Integer.parseInt(params.get("saleType").toString());
		StoreCost storeCost = storeCostManager.getStoreCostForBuy(store_id,saleType);
		result.put("storeCost", storeCost);
		return result;
	}
	
	public IStoreCostManager getStoreCostManager() {
		return storeCostManager;
	}
	public void setStoreCostManager(IStoreCostManager storeCostManager) {
		this.storeCostManager = storeCostManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

}
