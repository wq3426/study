package com.enation.app.b2b2c.core.service.goods;

import java.util.List;

import com.enation.app.shop.core.model.HotGoods;


/**
 * 热门商品manager接口
 * @author Sylow
 * @version v1.0, 2016年2月28日
 * @since v5.2
 */
public interface IHotGoodsManager {
	
	/**
	 * 新增热门商品
	 * @param hotGoods 
	 */
	public void add(HotGoods hotGoods);
	
	/**
	 * 根据商家id删除热门商品
	 * @param storeId
	 * @param id 
	 * @param site 
	 */
	public void delByStoreId(int storeId, int id, int site);
	
	/**
	 * 根据商家id获取热门商品
	 * @param storeId 商家id
	 * @return
	 */
	public List<HotGoods> list(int storeId);
	/**
	 * 修改商品的排序
	 * @param store_id
	 * @param id
	 * @param site
	 * @param flag
	 */
	public void updateSite(int store_id, int id, int site, String flag);
	

}
