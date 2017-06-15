package com.enation.app.shop.core.service.store.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.SaleType;
import com.enation.app.shop.core.service.store.ISaleTypeShopManager;
import com.enation.eop.sdk.database.BaseSupport;
@Component
public class SaleTypeShopManager extends BaseSupport implements ISaleTypeShopManager{

	@Override
	public List<SaleType> saleTypeList() {
		String sql="select * from es_sale_type";
		return this.baseDaoSupport.queryForList(sql, SaleType.class);
	}

}
