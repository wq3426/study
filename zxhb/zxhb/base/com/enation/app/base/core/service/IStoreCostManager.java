package com.enation.app.base.core.service;

import com.enation.app.base.core.model.SaleFree;
import com.enation.app.base.core.model.StoreCost;


/**
 * 店铺管理类
 * @author LiFenLong
 *2014-9-11
 */
public interface IStoreCostManager {
	/**
	 * 根据店铺id,营销类型 获取该店铺免费的专栏管理发布次数
	 * @param saleType 
	 * @param store_id 
	 * @return
	 */
	public StoreCost getStoreCost(Integer store_id, Integer saleType);
	
	/**
	 * @description 根据店铺id,营销类型 获取该店铺已购买的专栏管理发布次数
	 * @date 2016年9月17日 下午4:45:54
	 * @param store_id
	 * @param saleType
	 * @return
	 * @return StoreCost
	 */
	public StoreCost getStoreCostForBuy(Integer store_id, int saleType);

	/**
	 * @description 根据店铺id,营销类型 修改店铺专栏管理/信息服务发布次数
	 * @date 2016年8月17日 下午6:22:43
	 * @param store_id
	 * @param advPublishNumType
	 * @return void
	 * @throws Exception 
	 */
	public void updateStoreCost(Integer store_id, Integer publishNumType) throws Exception;
	/**
	 * @description 根据店铺id,营销类型 修改店铺短信服务使用数量
	 * @date 2016年8月18日 下午3:51:06
	 * @param store_id
	 * @param noteNumType
	 * @param length 
	 * @return void
	 */
	public void updateNoteStoreCost(int store_id, Integer noteNumType, int count);
	/**
	 * @description 添加购买营销类型
	 * @date 2016年9月1日 下午6:59:38
	 * @param saleFree
	 * @param typeId 
	 * @param store_id
	 * @return void
	 */
	public void addStoreCostForBuySale(SaleFree saleFree, String typeId, Integer store_id);
}
