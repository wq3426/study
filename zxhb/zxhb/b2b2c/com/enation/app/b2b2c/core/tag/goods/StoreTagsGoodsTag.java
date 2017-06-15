package com.enation.app.b2b2c.core.tag.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsTagManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.shop.core.service.IGoodsTagManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 获取店铺标签
 * @author LiFenLong
 * 
 * xulipeng 修改
 * 2014年12月4日14:46:22
 *
 */
@Component

public class StoreTagsGoodsTag extends BaseFreeMarkerTag{
	private IStoreGoodsTagManager storeGoodsTagManager;
	private IStoreMemberManager storeMemberManager;
	@SuppressWarnings("unchecked")
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		String mark = (String) params.get("mark");
		
		//xulipeng 增加店铺id，查找数量  条件
		Integer storeid = (Integer) params.get("storeid");
		if(storeid==null || storeid==0){
			StoreMember storeMember = storeMemberManager.getStoreMember();
			storeid = storeMember.getStore_id();
		}
		Integer num = (Integer) params.get("num");
		if(num==null || num==0){
			num=this.getPageSize();
		}
		Map map = new HashMap();
		map.put("mark", mark);
		map.put("storeid", storeid);
		
		Map result = new HashMap();
		String page = request.getParameter("page");
		int pageSize=10;
		page = (page == null || page.equals("")) ? "1" : page;
		Page webpage=new Page();
		//查询标签商品列表
		webpage = storeGoodsTagManager.getGoodsList(map, this.getPage(), num);
		//获取总记录数
		Long totalCount = webpage.getTotalCount();
		result.put("page", page);
		result.put("pageSize", pageSize);
		result.put("totalCount", totalCount);
		result.put("goodsTag", webpage);
		result.put("list", webpage.getResult());
		return result;
	}
	public IStoreGoodsTagManager getStoreGoodsTagManager() {
		return storeGoodsTagManager;
	}
	public void setStoreGoodsTagManager(IStoreGoodsTagManager storeGoodsTagManager) {
		this.storeGoodsTagManager = storeGoodsTagManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
