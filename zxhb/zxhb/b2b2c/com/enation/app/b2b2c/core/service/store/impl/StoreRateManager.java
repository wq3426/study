package com.enation.app.b2b2c.core.service.store.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.store.StoreRate;
import com.enation.app.b2b2c.core.service.store.IStoreRateManager;
import com.enation.app.shop.core.service.OrderType;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component
public class StoreRateManager extends BaseSupport implements IStoreRateManager {

	@Transactional
	public void save(StoreRate storeRate) {
		daoSupport.insert("es_store_rate", storeRate);
	}

	@Override
	public StoreRate getStoreRate(Integer order_type, Integer level_id) {
		try {
			StoreRate storeRate = (StoreRate) daoSupport.queryForObject(
					"select * from es_store_rate where order_type = ? and  level_id = ?", StoreRate.class, order_type,
					level_id);
			return storeRate;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Page rate_list(Map other, int pageNo, int pageSize , String sort, String order) {
		StringBuffer sql = new StringBuffer();
		String level_id = other.get("level_id") == null ? "" : other.get("level_id").toString();
		String order_type = other.get("order_type") == null ? "" : other.get("order_type").toString();

		sql.append(
				"select esr.*,esl.level_name from es_store_rate esr,es_store_level esl where  esr.level_id = esl.level_id  ");
		if (!StringUtil.isNull(level_id)) {
			sql.append(" and esr.level_id = " + level_id);
		}
		if (!StringUtil.isNull(order_type)) {
			sql.append(" and esr.order_type = " + order_type);
		}
		if(sort.equals("level_name")){
			sort="level_id";
		}
		if(sort.equals("order_type_name")){
			sort="order_type";
		}
		if(!StringUtil.isNull(sort)&&!StringUtil.isNull(order)){
			sql.append(" order  by " + sort + "  "+order);
		}
		Page page = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
		if (page != null) {
			JSONArray array = JSONArray.fromObject(page.getResult());
			if (array != null && array.size()>0) {
				for(int i = 0 ; i < array.size() ; i ++){
					JSONObject object =  array.getJSONObject(i);
					object.put("order_type_name", OrderType.getOrderTypeTest(object.getInt("order_type")));
				}
			}
			page.setResult(array);
		}
		
		JSONObject.fromObject(page);
		return page;

	}

	@Override
	public void delStoreRate(Integer rate_id) {
		try{
			String sql = "delete  from es_store_rate where rate_id = ?";
			this.daoSupport.execute(sql, rate_id);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void editStoreRate(StoreRate storeRate) {
		try{
			daoSupport.execute("update es_store_rate set flow_handling_rate = ? ,flow_service_rate=?,original_handling_rate=?,"
					+ "original_service_rate = ? where rate_id = ?", storeRate.getFlow_handling_rate(),storeRate.getFlow_service_rate(),storeRate.getOriginal_handling_rate(),storeRate.getOriginal_service_rate(),storeRate.getRate_id());
		}catch(Exception e){
			throw e;
		}
		
	}

	@Override
	public StoreRate getStoreRate(Integer rate_id) {
		try{
			return (StoreRate)daoSupport.queryForObject("select *from es_store_rate where rate_id = ?", StoreRate.class, rate_id);
		}catch(Exception e){
			throw e;
		}
	}
	
	

}
