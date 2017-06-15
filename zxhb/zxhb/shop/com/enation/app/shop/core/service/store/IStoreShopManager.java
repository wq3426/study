package com.enation.app.shop.core.service.store;

import com.enation.app.base.core.model.Store;


/**
 * 店铺管理类
 * @author LiFenLong
 *2014-9-11
 */
public interface IStoreShopManager {
	/**
	 * 根据等级发放免费使用短信/栏位/优惠券/信息服务
	 * @param storeId
	 */
	public void addStoreUseData(Store store);
	
}
