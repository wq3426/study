package com.enation.app.b2b2c.core.action.api.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.goods.IHotGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.HotGoods;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 热门商品list
 * @author Sylow
 * @version v1.0, 2016年2月28日
 * @since v5.2
 */
@Component
public class HotAndGoodsListTag extends BaseFreeMarkerTag {
	
	private IGoodsManager goodsManager;
	private IStoreMemberManager storeMemberManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		List<Goods> list = null;
		int storeid = Integer.parseInt(params.get("store_id").toString());
		if(storeid==0){
			StoreMember storeMember = storeMemberManager.getStoreMember();;
			storeid = storeMember.getStore_id();
		}
		list = this.goodsManager.listGoods(storeid);
		return  (list == null) ? new ArrayList() : list;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	
}
