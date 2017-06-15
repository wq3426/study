package com.enation.app.b2b2c.core.service.store;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.store.StoreLevel;

/**
 * 店铺等级管理类
 * @author LiFenLong
 *
 */
public interface IStoreLevelManager {

	/**
	 * 获取店铺等级列表
	 * @return List
	 */
	public List storeLevelList();
	/**
	 * 添加店铺等级
	 * @param levelName
	 * @param note_num 
	 */
	public void addStoreLevel(StoreLevel storeLevel);
	/**
	 * 修改店铺等级
	 * @param no 
	 * @param integer 
	 */
	public void editStoreLevel(StoreLevel storeLevel);
	/**
	 * 删除店铺等级
	 * @param levelId
	 * @throws Exception 
	 */
	public void delStoreLevel(Integer levelId) throws Exception;
	/**
	 * 获取一个店铺等级对象
	 * @param levelId
	 */
	public StoreLevel getStoreLevel(Integer levelId);
	
	
	/**获取店铺等级
	 * @param service_store_id
	 * @return
	 */
	public StoreLevel getStoreLevelByStoreId(Integer service_store_id);
}
