package com.enation.app.b2b2c.core.service.store.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.store.StoreLevel;
import com.enation.app.b2b2c.core.service.store.IStoreLevelManager;
import com.enation.eop.sdk.database.BaseSupport;
@Component
public class StoreLevelManager extends BaseSupport implements IStoreLevelManager{

	@Override
	public List storeLevelList() {
		String sql="select * from es_store_level";
		return this.daoSupport.queryForList(sql);
	}

	@Override
	public void addStoreLevel(StoreLevel storeLevel) {
		this.daoSupport.insert("es_store_level", storeLevel);
		
	}

	@Override
	public void editStoreLevel(StoreLevel storeLevel) {
		this.daoSupport.update("es_store_level", storeLevel, "level_id="+storeLevel.getLevel_id());
	}

	@Override
	public void delStoreLevel(Integer levelId) throws Exception {
		String sql1= "select * from es_store where store_level=?";
		List storeList = this.baseDaoSupport.queryForList(sql1, levelId);
		if(storeList.isEmpty()){
			String sql="DELETE from es_store_level WHERE level_id=?";
			this.daoSupport.execute(sql, levelId);
		}else{
			throw new Exception();
		}
	}

	@Override
	public StoreLevel getStoreLevel(Integer levelId) {
		String sql="select * from es_store_level where level_id=?";
		return (StoreLevel) this.baseDaoSupport.queryForObject(sql,StoreLevel.class, levelId);
	}

	@Override
	public StoreLevel getStoreLevelByStoreId(Integer service_store_id) {
		String sql = "select l.* from es_store s,es_store_level  l where  s.store_level = l.level_id and  s.store_id = ?";
		return (StoreLevel)daoSupport.queryForObject(sql, StoreLevel.class, service_store_id);
	}
	
	

}
