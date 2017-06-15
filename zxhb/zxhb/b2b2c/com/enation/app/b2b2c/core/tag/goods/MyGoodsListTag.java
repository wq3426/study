package com.enation.app.b2b2c.core.tag.goods;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 店铺商品标签
 * @author LiFenLong
 *
 */
@Component
public class MyGoodsListTag extends BaseFreeMarkerTag{
	private IStoreGoodsManager storeGoodsManager;
	private IStoreMemberManager storeMemberManager;
	@SuppressWarnings("unchecked")
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		//session中获取会员信息,判断用户是否登陆
		StoreMember member=storeMemberManager.getStoreMember();
		if(member==null){
			HttpServletResponse response= ThreadContextHolder.getHttpResponse();
			try {
				response.sendRedirect("login.html");
			} catch (IOException e) {
				throw new UrlNotFoundException();
			}
		}
		Map result = new HashMap();
		int pageSize=10;
		String disable= request.getParameter("disable")==null?"0":request.getParameter("disable");
		String page = request.getParameter("page") == null? "1" : request.getParameter("page");
		String store_cat=request.getParameter("store_cat")==null?"0":request.getParameter("store_cat");
		String goodsName=request.getParameter("goodsName");
		String market_enable=request.getParameter("market_enable")==null?"99":request.getParameter("market_enable");
		
		if(!store_cat.matches("\\d+") || !market_enable.matches("\\d+") || !disable.matches("\\d+")){
			throw new UrlNotFoundException();
		}
		
		result.put("store_id", member.getStore_id());
		result.put("disable", Integer.parseInt(disable));
		result.put("store_cat", store_cat);
		result.put("goodsName", goodsName);
		result.put("market_enable", Integer.parseInt(market_enable));
		Page storegoods= storeGoodsManager.storeGoodsList(Integer.parseInt(page) , pageSize,result);
		
		//获取总记录数
		Long totalCount = storegoods.getTotalCount();
		
		result.put("page", page);
		result.put("pageSize", pageSize);
		result.put("totalCount", totalCount);
		result.put("storegoods", storegoods);
		return result;
	}
	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}
	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
