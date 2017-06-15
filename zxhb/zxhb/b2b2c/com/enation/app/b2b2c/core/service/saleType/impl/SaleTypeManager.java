package com.enation.app.b2b2c.core.service.saleType.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.sale.SaleType;
import com.enation.app.b2b2c.core.service.saleType.ISaleTypeManager;
import com.enation.eop.sdk.database.BaseSupport;
@Component
public class SaleTypeManager extends BaseSupport implements ISaleTypeManager{

	@Override
	public List<SaleType> saleTypeList() {
		String sql="select * from es_sale_type";
		return this.baseDaoSupport.queryForList(sql, SaleType.class);
	}

	@Override
	public void addSaleType(SaleType saleType) {
		this.daoSupport.insert("es_sale_type", saleType);
	}

	@Override
	public SaleType getSaleType(Integer id) {
		String sql="select * from es_sale_type where type_id=?";
		return (SaleType) this.baseDaoSupport.queryForObject(sql,SaleType.class, id);
	}

	@Override
	public void editSaleType(SaleType saleType) {
		this.daoSupport.update("es_sale_type", saleType, "type_id="+saleType.getType_id());
	}

	@Override
	public void delSaleType(Integer id) {
		String sql="DELETE from es_sale_type WHERE type_id=?";
		this.daoSupport.execute(sql, id);
	}

}
