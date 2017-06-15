package com.enation.app.shop.core.service.impl;

import org.springframework.stereotype.Component;
import com.enation.app.shop.core.model.CarCheckInfo;
import com.enation.app.shop.core.service.ICarCheckInfoManager;
import com.enation.eop.sdk.database.BaseSupport;

/**
 * 汽车体检信息录入
 * @author yunbs
 *
 */
@Component
public class CarCheckInfoManager extends BaseSupport implements ICarCheckInfoManager {

	@Override
	public void addCarCheckInfo(CarCheckInfo carCheckInfo) {
		this.baseDaoSupport.insert("es_carcheck_info", carCheckInfo);
	}
}
