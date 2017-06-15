package com.enation.app.b2b2c.core.action.backend.goods;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.shop.ShopApp;
import com.enation.app.shop.core.action.backend.GoodsAction;

/**
 * 自营店商品管理
 * @author [kingapex]
 * @version [1.0]
 * @since [5.1]
 * 2015年10月12日下午4:33:35
 */
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/b2b2c/admin/self/goods/goods_list.html"),
	 @Result(name="cat_tree", type="freemarker", location="/shop/admin/cat/select.html"),
	 @Result(name="input", type="freemarker", location="/shop/admin/goods/goods_input.html"),
	 @Result(name="select_cat", type="freemarker", location="/shop/admin/goods/select_cat.html")
})
@Action("selfStoreGoods")
public class SelfStoreGoodsAction extends GoodsAction {
	
	/**
	 * 店铺商品管理类
	 */
	private IStoreGoodsManager storeGoodsManager;
	
	
	
	/**
	 * 转到商品列表页面
	 * @return
	 */
	public String list(){
		
		return "list";
	}
	
	
	/**
	 * 商品列表页所需的json(商品列表json)
	 * @return 
	 */
	public String listJson(){
		try {
			Map params  = new HashMap();
			params.put("store_id", ShopApp.self_storeid);
			params.put("market_enable",1);
			params.put("disable",0);
			
			this.webpage = this.storeGoodsManager.storeGoodsList(this.getPage(), this.getPageSize(), params);
			
			this.showGridJson(webpage);
			
		} catch (Exception e) {
			 this.logger.error("获取商品列表json出错", e);
			 this.showErrorJson("获取商品列表json出错"+e.getMessage());
			
		}
		return this.JSON_MESSAGE;
	}

	

	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}


	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}
	
	
	
}
