package com.enation.app.b2b2c.core.action.api.goods;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IHotGoodsManager;
import com.enation.app.shop.core.model.HotGoods;
import com.enation.framework.action.WWAction;

/**
 * 
 * @author Sylow
 * @version v1.0, 2016年2月28日
 * @since v5.2
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("hot_goods")
public class HotGoodsApiAction extends WWAction {
	
	private IHotGoodsManager hotGoodsManager;
	
	/*位置*/
	private Integer[] sites;
	/*商品编号*/
	private String[] goods_sns;
	/*url*/
	private String[] h_urls;
	//商家id
	private int store_id;
	
	private int id;
	
	private int site;
	
	private String flag;
	/**
	 * 删除热门商品
	 * @return
	 */
	public String delHotGoods(){
		
		try {
			this.hotGoodsManager.delByStoreId(store_id,id,site);
			this.showSuccessJson("保存成功");
		} catch(RuntimeException e) {
			e.printStackTrace();
			this.showErrorJson("" + e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 修改热门商品排序
	 * @return
	 */
	public void updateSite(){
		try {
			this.hotGoodsManager.updateSite(store_id,id,site,flag);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public IHotGoodsManager getHotGoodsManager() {
		return hotGoodsManager;
	}

	public void setHotGoodsManager(IHotGoodsManager hotGoodsManager) {
		this.hotGoodsManager = hotGoodsManager;
	}

	public Integer[] getSites() {
		return sites;
	}

	public void setSites(Integer[] sites) {
		this.sites = sites;
	}

	public String[] getGoods_sns() {
		return goods_sns;
	}

	public void setGoods_sns(String[] goods_sns) {
		this.goods_sns = goods_sns;
	}

	public String[] getH_urls() {
		return h_urls;
	}

	public void setH_urls(String[] h_urls) {
		this.h_urls = h_urls;
	}

	public int getStore_id() {
		return store_id;
	}

	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSite() {
		return site;
	}

	public void setSite(int site) {
		this.site = site;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
}
