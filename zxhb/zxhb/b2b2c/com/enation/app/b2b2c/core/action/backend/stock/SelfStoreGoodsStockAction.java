package com.enation.app.b2b2c.core.action.backend.stock;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.b2b2c.core.service.goods.IB2b2cGoodsStoreManager;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsSpecManager;
import com.enation.app.shop.core.action.backend.GoodsStoreAction;
import com.enation.framework.database.Page;
 
/**
 * 自营店库存管理
 * @author [kingapex]
 * @version [1.0]
 * @since [5.1]
 * 2015年10月23日下午1:40:55
 */
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="goodsstore_list",type="freemarker", location="/b2b2c/admin/self/goods/self_store_list.html"),
	 @Result(name="dialog_html",type="freemarker", location="/b2b2c/admin/self/goods/goodsstore/dialog.html")
})
@Action("selfStoreGoodsStock")
public class SelfStoreGoodsStockAction extends GoodsStoreAction{
	
	private IStoreGoodsSpecManager storeGoodsSpecManager;
	private IB2b2cGoodsStoreManager b2b2cGoodsStoreManager;
	

	/**
	 * 跳转至商品库存管理页面
	 * @return 商品库存管理页面
	 */
	public String listGoodsStore(){
		return "goodsstore_list";
	}
	
	
	
	/**
	 * 获取商品库存管理列表Json
	 * @param stype 搜索类型,Integer
	 * @param keyword 搜索关键字,String
	 * @param name 商品名称,String
	 * @param sn 商品编号,String
	 * @param depot_id 库房Id,Integer
	 * @return 商品库存管理列表Json
	 */
	@SuppressWarnings("unchecked")
	public String listGoodsStoreJson(){
		
		Map storeMap = new HashMap();
		storeMap.put("stype", stype);
		storeMap.put("keyword", keyword);
		storeMap.put("name", name);
		storeMap.put("sn", sn);
		depot_id = depot_id==null?0:depot_id;
		storeMap.put("depotid", depot_id);
		
		Page page=this.b2b2cGoodsStoreManager.listSelfStore(storeMap, this.getPage(), this.getPageSize(),this.getSort(),this.getOrder());
		
		this.showGridJson(page);
		
		return JSON_MESSAGE;
	}
	
	 
	 
 
 


	public IB2b2cGoodsStoreManager getB2b2cGoodsStoreManager() {
		return b2b2cGoodsStoreManager;
	}



	public void setB2b2cGoodsStoreManager(IB2b2cGoodsStoreManager b2b2cGoodsStoreManager) {
		this.b2b2cGoodsStoreManager = b2b2cGoodsStoreManager;
	}

}
