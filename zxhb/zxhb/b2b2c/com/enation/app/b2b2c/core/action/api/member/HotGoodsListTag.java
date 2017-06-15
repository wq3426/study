package com.enation.app.b2b2c.core.action.api.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.goods.IHotGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.shop.core.model.HotGoods;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 热门商品list
 * @author Sylow
 * @version v1.0, 2016年2月28日
 * @since v5.2
 */
@Component
public class HotGoodsListTag extends BaseFreeMarkerTag {
	
	private IHotGoodsManager hotGoodsManager;
	private IStoreMemberManager storeMemberManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		List<HotGoods> list = null;
		int storeid = Integer.parseInt(params.get("store_id").toString());
		if(storeid==0){
			StoreMember storeMember = storeMemberManager.getStoreMember();;
			storeid = storeMember.getStore_id();
		}
		list = this.hotGoodsManager.list(storeid);
		return  (list == null) ? new ArrayList() : list;
	}

	public IHotGoodsManager getHotGoodsManager() {
		return hotGoodsManager;
	}

	public void setHotGoodsManager(IHotGoodsManager hotGoodsManager) {
		this.hotGoodsManager = hotGoodsManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	
}
