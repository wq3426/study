package com.enation.app.b2b2c.core.service;

import java.util.List;

import com.enation.app.b2b2c.core.model.StoreDlyType;
import com.enation.app.shop.core.model.support.DlyTypeConfig;
import com.enation.app.shop.core.model.support.TypeAreaConfig;
import com.enation.framework.database.Page;

/**
 * 店铺配送方式接口
 * @author xulipeng
 *
 */
public interface IStoreDlyTypeManager {
	
	
	/**
	 * 根据模板id查出所有的配送方式
	 * @param store_id
	 * @return
	 */
	List getDlyTypeList(Integer template_id);
	
	
	/**
	 * 根据店铺id获取配送方式列表<br>
	 * @param storeid 店铺id
	 * @param pageNo 页码
	 * @param pageSize  页大小
	 * @return 配送方式列表
	 */
	public Page pageByStoreId(int storeid,int pageNo,int pageSize);
	
	
	
	/**
	 * 添加配送方式
	 * @param storeDlyType
	 * @param config
	 * @param configArray
	 */
	void add(StoreDlyType storeDlyType,DlyTypeConfig config,TypeAreaConfig[] configArray);
	
	/**
	 * 查出最后一个id
	 * @return
	 */
	Integer getLastId();
	
	/**
	 * 根据id 查询配送方式
	 * @param typeid
	 * @return
	 */
	List getDlyTypeById(String  typeid);
	
	/**
	 * 根据配送方式id 查询所指定地区的列表
	 * @param type_id
	 * @return
	 */
	List getDlyTypeAreaList(Integer type_id);
	
	
	/**
	 * 根据店铺id 查询默认仓库
	 * @param Storeid
	 * @return
	 */
	Integer getDefaultDlyId(Integer storeid);
	
	/**
	 * 根据模板id删除模板下的配送方式
	 * @param tempid
	 */
	void del_dlyType(Integer tempid);
	
}
