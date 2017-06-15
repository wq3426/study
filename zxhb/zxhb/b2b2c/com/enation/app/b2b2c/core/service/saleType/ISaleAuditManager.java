package com.enation.app.b2b2c.core.service.saleType;

import java.util.Map;

import com.enation.app.base.core.model.SaleFree;
import com.enation.framework.database.Page;

public interface ISaleAuditManager {

	/**
	 * @description 店铺免费/购买申请营销类型
	 * @date 2016年9月2日 上午10:13:59
	 * @param store_id
	 * @param typeId 
	 * @param saleFree
	 * @param isFree 
	 * @return void
	 */
	public void addSaleAudit(Integer store_id, String typeId, SaleFree saleFree, int auditStatus);
	/**
	 * @description 根据storeId获取该店铺的免费申请信息和购买信息
	 * @date 2016年9月2日 下午3:54:33
	 * @param parseInt
	 * @param pageSize
	 * @param map
	 * @param store_id
	 * @return
	 * @return Page
	 */
	public Page getAllAudit(int pageNo, int pageSize, Map map, Integer store_id);

}
