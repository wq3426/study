package com.enation.app.b2b2c.core.tag.store;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
import net.sf.json.JSONObject;

/**
 * @Description 店铺保险公司列表
 *
 * @createTime 2016年9月9日 下午8:47:02
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
@Component
public class StoreInsureCompanyListTag extends BaseFreeMarkerTag{
	private IStoreManager storeManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map map = new HashMap();

		JSONObject store_insure_companyObj = storeManager.getStoreInsureCompanyInfo(((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id());
		
		map.put("insure_company_list", store_insure_companyObj.get("insure_company_list"));
		map.put("store_insure_company_list", store_insure_companyObj.get("store_insure_company_list"));
		map.put("insure_company_ids", store_insure_companyObj.get("insure_company_ids"));
		map.put("insure_income_discount", store_insure_companyObj.get("insure_income_discount"));

		return map;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
}
