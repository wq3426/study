package com.enation.app.b2b2c.core.tag.goods;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
import net.sf.json.JSONObject;

/**
 * @Description 纪念卡信息获取
 *
 * @createTime 2016年12月6日 下午9:06:09
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */

@Component
public class ZxCardGoodsListTag extends BaseFreeMarkerTag {
	
	private IStoreGoodsManager storeGoodsManager;
	private IStoreMemberManager storeMemberManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		JSONObject goodsObj = storeGoodsManager.getZxCardGoodsInfo();
		
		return goodsObj;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}
	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}
	
}
