package com.enation.app.b2b2c.component.plugin.store;


import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Store;
import com.enation.framework.plugin.AutoRegisterPlugin;
/**
 * 设置店铺金额初始值
 * @author LiFenLong
 *
 */
@Component
public class StoreAddBalancePlugin extends AutoRegisterPlugin implements IAfterStorePassEvent{
	private IStoreManager storeManager;
	@Override
	public void AfterStorePass(Store store) {
//		storeManager.addStoreBalance(store);
	}
	public IStoreManager getStoreManager() {
		return storeManager;
	}
	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
}
