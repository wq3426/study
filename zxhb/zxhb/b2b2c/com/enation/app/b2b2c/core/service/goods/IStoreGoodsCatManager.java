package com.enation.app.b2b2c.core.service.goods;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.store.StoreCat;
import com.enation.framework.database.Page;

/**
 * 店铺分类管理类
 * @author LiFenLong
 *
 */
public interface IStoreGoodsCatManager {
	/**
	 * 店铺分类列表
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List storeCatList(Integer storeId);
	/**
	 * 店铺所有分类列表
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public List storeAllCatList(Integer storeid);
	/**
	 * 添加店铺分类
	 * @param StoreCat
	 */
	public void addStoreCat(StoreCat storeCat);
	/**
	 * 修改店铺分类
	 * @param storeCat
	 */
	public void editStoreCat(StoreCat storeCat);
	/**
	 * 删除店铺分类
	 * @param storeCatId
	 */
	public void deleteStoreCat(Integer storeCatId,Integer storeid);
	
	/**
	 * 查询店铺顶级分类列表 不包括本身
	 * @return
	 */
	List getStoreCatList(Integer catid,Integer storeId);
	
	/**
	 * 根据store_cat_pid和sort判断该分类下是否已经有此排序
	 * 返回值是0，表示无排序，
	 * @param map
	 * @return
	 */
	public Integer getStoreCatNum(Integer store_id,Integer store_cat_pid,Integer sort);
	
	
	StoreCat getStoreCat(Map map);
	
	/**
	 * 根据店铺分类ID获取分类父ID
	 * @author DMRain 2016-1-19
	 * @param catid 店铺分类ID
	 * @return pid 店铺分类父ID
	 * 返回父ID为0，代表此分类不是子分类
	 */
	public Integer is_children(Integer catid);
	
	/**
	 * 根据store_cat_name和store_id判断是否存在该商品分类
	 * @param map
	 * @return
	 */
	public int getStoreCatNameIsExist(String store_cat_name, Integer store_id);
	
}
