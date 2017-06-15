package com.enation.app.b2b2c.core.tag.saleType;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.saleType.ISaleFreeManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.SaleFree;
import com.enation.app.base.core.model.Store;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 营销模板信息tag
 * @author Yunbs
 * @version v1.0, 2016年8月17日
 * @since v5.2
 */
@Component
@Scope("prototype")
public class SaleFreeForFieldTag extends BaseFreeMarkerTag {
	private IStoreManager storeManager;
	private IStoreMemberManager storeMemberManager;
	private ISaleFreeManager saleFreeManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		StoreMember storeMember =storeMemberManager.getStoreMember() ;
		Integer store_id = storeMember.getStore_id();
		int saleType = Integer.parseInt(params.get("sale_type").toString());
		int isFree = Integer.parseInt(params.get("isFree").toString());
		Store store = storeManager.getStore(store_id);
		List<SaleFree> saleFreeForFieldList = saleFreeManager.getSaleFreeByTypeId(saleType,store.getStore_level(),isFree);
		return saleFreeForFieldList;
	}
	
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}

	public ISaleFreeManager getSaleFreeManager() {
		return saleFreeManager;
	}

	public void setSaleFreeManager(ISaleFreeManager saleFreeManager) {
		this.saleFreeManager = saleFreeManager;
	}
}
