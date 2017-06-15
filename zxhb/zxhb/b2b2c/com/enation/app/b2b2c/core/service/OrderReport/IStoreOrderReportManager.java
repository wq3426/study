package com.enation.app.b2b2c.core.service.OrderReport;

import org.springframework.stereotype.Component;

/**
 * 店铺单据管理
 * @author fenlongli
 *
 */
@Component
public interface IStoreOrderReportManager {
	/**
	 * 审核申请
	 */
	public void saveAuth(Integer status,Integer id,String seller_remark);
}
