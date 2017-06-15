package com.enation.app.shop.core.service.store;

import java.util.List;

import com.enation.app.shop.core.model.SaleType;


/**
 * 营销管理类型维护
 * @author LiFenLong
 *
 */
public interface ISaleTypeShopManager {

	/**
	 * 获取营销管理类型列表
	 * @return List
	 */
	public List<SaleType> saleTypeList();
}
