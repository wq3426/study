package com.enation.app.shop.core.service.store.impl;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.SaleFree;
import com.enation.app.base.core.model.Store;
import com.enation.app.base.core.model.StoreCost;
import com.enation.app.shop.core.model.SaleType;
import com.enation.app.shop.core.service.store.ISaleTypeShopManager;
import com.enation.app.shop.core.service.store.IStoreShopManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.util.DateUtil;
@Component
public class StoreShopManager  extends BaseSupport implements IStoreShopManager{
	private ISaleTypeShopManager saleTypeShopManager;
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addStoreUseData(Store store) {
		List<SaleType> saleTypeList = saleTypeShopManager.saleTypeList();
		if(!saleTypeList.isEmpty()){
			for(int i=0;i<saleTypeList.size();i++){
				String sql = "SELECT * FROM es_sale_free WHERE type_id="+saleTypeList.get(i).getType_id()+
							 " AND level_id="+ store.getStore_level()+" AND isFree=0 AND isInitia=0";
				SaleFree saleFree = (SaleFree) this.daoSupport.queryForObject(sql, SaleFree.class);
				if(saleFree != null){
					StoreCost storeCost = new StoreCost();
					storeCost.setStore_id(store.getStore_id());
					storeCost.setType_id(saleFree.getType_id());
					if(!saleFree.getLimit_date().equals("0")){
						 Integer limitDate = Integer.parseInt(saleFree.getLimit_date());
						 Long[] currentMonth = DateUtil.getCurrentLastMonth(limitDate+1);
						 storeCost.setValid_start_date(currentMonth[0]*1000);
						 storeCost.setValid_end_date(currentMonth[1]*1000);
					}
					storeCost.setUsed_num(0);
					storeCost.setSurp_num(saleFree.getUse_num());
					storeCost.setIsFree("0");
					storeCost.setLevel_id(store.getStore_level());
					this.daoSupport.insert("es_store_cost", storeCost);
				}
			}
		}
	}
	public ISaleTypeShopManager getSaleTypeShopManager() {
		return saleTypeShopManager;
	}
	public void setSaleTypeShopManager(ISaleTypeShopManager saleTypeShopManager) {
		this.saleTypeShopManager = saleTypeShopManager;
	}
	
}
