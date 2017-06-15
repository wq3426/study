package com.enation.app.b2b2c.core.tag.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * @Description 获取店铺保养时间列表
 *
 * @createTime 2016年9月1日 下午6:55:12
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
@Component
public class StoreCarRepairByTimeListTag extends BaseFreeMarkerTag{
	private IStoreManager storeManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map map = new HashMap();
		
		List timeRegionList = storeManager.getStoreCarRepairTimeList(((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id());
		int repair_time_range = storeManager.getStore(((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id()).getRepair_time_range();
		
		map.put("timeRegionList", timeRegionList);
		map.put("repair_time_range", repair_time_range);
		
		return map;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
}
