package com.enation.app.b2b2c.core.tag.goods;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
import net.sf.json.JSONObject;

/**
 * @Description 订单详情获取
 *
 * @createTime 2016年12月6日 下午9:06:09
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */

@Component
public class ZxOrderInfoTag extends BaseFreeMarkerTag {
	
	private IStoreGoodsManager storeGoodsManager;
	private IStoreMemberManager storeMemberManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String mobile = (String) params.get("mobile");
		String order_id = (String) params.get("order_id");
		
		JSONObject orderObj = storeGoodsManager.getZxOrderInfo(mobile, order_id);
		
		return orderObj;
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
