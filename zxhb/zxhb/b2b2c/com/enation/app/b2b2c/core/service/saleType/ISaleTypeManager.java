package com.enation.app.b2b2c.core.service.saleType;

import java.util.List;

import com.enation.app.b2b2c.core.model.sale.SaleType;

/**
 * 营销管理类型维护
 * @author LiFenLong
 *
 */
public interface ISaleTypeManager {

	/**
	 * 获取营销管理类型列表
	 * @return List
	 */
	public List<SaleType> saleTypeList();
	/**
	 * 添加营销管理类型
	 */
	public void addSaleType(SaleType saleType);
	/**
	 * 获取一个营销管理类型对象
	 * @param levelId
	 */
	public SaleType getSaleType(Integer id);
	/**
	 * 修改营销管理类型
	 * @param no 
	 * @param integer 
	 */
	
	public void editSaleType(SaleType saleType);
	/**
	 * 删除营销管理类型
	 * @param Id
	 * @throws Exception 
	 */
	public void delSaleType(Integer id);

}
