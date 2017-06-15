package com.enation.app.b2b2c.core.service.goods;

import java.util.Map;

import com.enation.framework.database.Page;

/**
 * b2b2c商品库存管理
 * @author [kingapex]
 * @version [1.0]
 * @since [5.1]
 * 2015年10月23日下午4:12:05
 */
public interface IB2b2cGoodsStoreManager {
	
	
	
	/**
	 * 读取自营店商品库存
	 * @param filter  过滤参数，key的信息如下:<br>
	 * <li>stype:搜索类型(Integer型,0为基本搜素)</li>
	 * <li>keyword:关键字(String型)</li>
	 * <li>name:商品名称(String型)</li>
	 * <li>sn:商品sn(String)</li>
	 * <li>depotid:仓库id(Integer型)</li>
	 * @param page 页码
	 * @param pageSize 页大小
	 * @param sortField 排序字段
	 * @param sortType 排序方式
	 * @return 商品库存的分页列表
	 */
	public Page listSelfStore(Map filter, int page, int pageSize, String sortField, String sortType) ;
	
	
}
