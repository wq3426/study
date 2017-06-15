package com.enation.app.b2b2c.core.service.store;

import java.util.Map;

import com.enation.app.b2b2c.core.model.store.StoreRate;
import com.enation.framework.database.Page;

public interface IStoreRateManager {

	public void  save(StoreRate storeRate);


	public StoreRate getStoreRate(Integer order_type, Integer level_id);


	public Page rate_list(Map other , int pageNo ,int pageSize, String sort, String order);


	public void delStoreRate(Integer rate_id);


	public void editStoreRate(StoreRate storeRate);


	public StoreRate getStoreRate(Integer rate_id);


}
