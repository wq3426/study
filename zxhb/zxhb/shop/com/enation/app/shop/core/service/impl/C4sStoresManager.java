package com.enation.app.shop.core.service.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.C4sModel;
import com.enation.app.shop.core.service.I4sStoresManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 4s店信息管理
 * @author wangqiang 2016年4月1日 下午7:55:42
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class C4sStoresManager extends BaseSupport implements I4sStoresManager {

	@Override
	public Page get4sStoresInfoPage(int pageNo, int pageSize, String carmodel_id, String store_cityid, String repair4sstoreid) {
		try {
			StringBuilder sql = new StringBuilder();
			String sign_store_cityid = "";
			sql.append("SELECT store_id, store_name, REPLACE(store_logo,?,?) store_logo, customer_phone phone, attr, comment_grade, discountcontract, discountnoncontract FROM es_store WHERE store_id != 1 AND store_id in ") 
			   .append("(SELECT DISTINCT(t1.store_id) FROM es_store t1, es_store_repairitem t2, es_store_audit t3 WHERE t1.store_id=t2.store_id AND t1.store_id=t3.store_id AND t1.auditstatus='2' AND t3.audit_result='1' AND t2.carmodel_id=?)");
			if(!StringUtil.isNull(repair4sstoreid)){
				sql.append(" AND store_id != '" + repair4sstoreid +"'");
				String csql = "select store_cityid from es_store where store_id="+repair4sstoreid;
				sign_store_cityid = daoSupport.queryForString(csql);
			}
			if(!StringUtil.isNull(store_cityid)){
				sql.append(" AND store_cityid = "+store_cityid);
			}else{
				sql.append(" AND store_cityid = "+sign_store_cityid);
			}
			sql.append(" ORDER BY comment_grade DESC");

			Page page = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_domain(), carmodel_id);
			this.logger.debug("4sStoresList:" + JSONArray.fromObject(page).toString());
			
			return page;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public C4sModel get4sStoresById(int repair4sstoreid) {
		String sql = "select * from es_store where store_id = ?";
		C4sModel c4sModel = (C4sModel)this.daoSupport.queryForObject(sql, C4sModel.class, repair4sstoreid);
		return c4sModel;
	}

	
	public JSONObject getRepairInfoByCar(int pageNo,String carplate,String store_cityid,String repair4sstoreid) {
		JSONObject jsonObject = new JSONObject();
		try {
			String sql = "SELECT c1.repair4sstoreid, c1.repairlastmile, c1.carmodelid, c1.totalgain," 
					   + " c2.repairinterval, c2.brand"
					   + " FROM es_carinfo c1, es_carmodels c2 WHERE c1.carmodelid=c2.id and c1.carplate=?";
			List list = daoSupport.queryForList(sql, carplate);
			JSONObject obj = JSONObject.fromObject(list.get(0));
			String carmodelid = obj.getString("carmodelid");//车型ID
			long repairlastmile = obj.getInt("repairlastmile");//上次保养里程
			long repairinterval = obj.getInt("repairinterval");//保养间隔里程
			double repairtotalgain = obj.getDouble("totalgain");//可用保养收益
			int signStoreId = obj.getInt("repair4sstoreid");//签约的4s店id
			String brand = obj.getString("brand");//获取车辆品牌
			//计算下次保养里程
			long nextmile = ((repairlastmile % repairinterval) > (repairinterval / 2)) ? ((repairlastmile/repairinterval) + 2)*repairinterval : ((repairlastmile/repairinterval) + 1)*repairinterval;
			
			sql = "select repaircontent, price from es_repairintervalitem where carmodelid=? and repairinterval=?";
			list = daoSupport.queryForList(sql, carmodelid, nextmile);
			obj = JSONObject.fromObject(list.get(0));
			long mkprice = obj.getLong("price");//获取下次保养价格
			String repaircontent = obj.getString("repaircontent").replace(";", ",");
			
			sql = "SELECT itemname FROM es_repairitem WHERE id IN ("+ repaircontent +")";
			list = daoSupport.queryForList(sql);
			JSONArray tempArray = JSONArray.fromObject(list);
			repaircontent = "";
			for(int i=0; i<tempArray.size(); i++){
				repaircontent += tempArray.getJSONObject(i).getString("itemname")+ "、";
			}
			repaircontent = repaircontent.substring(0, repaircontent.lastIndexOf("、"));//保养内容字符串
			
			DecimalFormat df = new DecimalFormat("0.00");
			sql = "select brand_id from es_brand where name=?";
			int brand_id = daoSupport.queryForInt(sql, brand);
			
			StringBuilder sqlBuider = new StringBuilder();
			sqlBuider.append("SELECT *FROM es_store WHERE store_id != 1 AND brand_id= " + brand_id + " ") ;//根据品牌获取4s店列表
			String sign_store_cityid = "";
			if(!StringUtil.isNull(repair4sstoreid)){
				sqlBuider.append("  AND store_id != '" + repair4sstoreid +"'");
				String csql = "select store_cityid from es_store where store_id="+repair4sstoreid;
				sign_store_cityid = daoSupport.queryForString(csql);
			}
			if(!StringUtil.isNull(store_cityid)){
				sqlBuider.append("  AND store_cityid = "+store_cityid);
			}else{
				sqlBuider.append("  AND store_cityid = "+sign_store_cityid);
			}
			
			sqlBuider.append(" ORDER BY comment_grade DESC");
			Page page = this.daoSupport.queryForPage(sqlBuider.toString(),pageNo, 10);
			JSONArray storeobjArray = new JSONArray();
			if(!StringUtil.isNull(repair4sstoreid) && pageNo==1){//第一页时获取当前店铺信息放在list第一位
				JSONArray belongStoreArray = this.get4sStoreById(Integer.parseInt(repair4sstoreid));
				belongStoreArray.addAll((List)page.getResult());
				page.setResult(belongStoreArray.toString());
			}
			list = JSONArray.toList(JSONArray.fromObject(page.getResult()));
			//name telephone address image mkprice price securitygain repairestimatedmaxgain nextmile  repaircontent
			for(int i=0; i<list.size(); i++){
				JSONObject storeobj = JSONObject.fromObject(list.get(i));
				String store_logo = UploadUtil.replacePath(storeobj.getString("store_logo"));
				storeobj.put("store_logo", store_logo);
				double discountcontract = storeobj.getDouble("discountcontract");//签约折扣
				double discountnoncontract = storeobj.getDouble("discountnoncontract");//非签约折扣
				int store_id = storeobj.getInt("store_id");
				if(store_id == signStoreId){
					storeobj.put("sign", "yes");
					double repairestimatedmaxgain = mkprice * (1 - discountcontract);//预计保养最大收益，即保养收益使用最高额度
					repairestimatedmaxgain = Double.valueOf(df.format(repairestimatedmaxgain));
					storeobj.put("repairestimatedmaxgain", repairestimatedmaxgain);
					storeobj.put("securitygain", repairtotalgain);
				}else{
					storeobj.put("sign", "no");
					double repairestimatedmaxgain = mkprice * (1 - discountnoncontract);//预计保养最大收益，即保养收益使用最高额度
					repairestimatedmaxgain = Double.valueOf(df.format(repairestimatedmaxgain));
					storeobj.put("repairestimatedmaxgain", repairestimatedmaxgain);
					storeobj.put("securitygain", repairtotalgain);
				}
				
				sql = "select t.*, "+ mkprice +" * ratio price from es_repair_timeregion t order by time_region_id";
				JSONArray time_regionArray = JSONArray.fromObject(daoSupport.queryForList(sql));
				sql = "SELECT MAX(price) max_price, MIN(price) min_price FROM  (" + sql + ") a";
				JSONObject priceObj = JSONObject.fromObject(daoSupport.queryForList(sql).get(0));
				double min_price = priceObj.getDouble("min_price");
				double max_price = priceObj.getDouble("max_price");
				
				storeobj.put("mkprice", mkprice);//原价
				storeobj.put("nextmile", nextmile);//保养服务里程
				storeobj.put("repaircontent", repaircontent);//保养内容
				storeobj.put("time_regions", time_regionArray);//保养时间段
				storeobj.put("min_price", min_price);
				storeobj.put("max_price", max_price);
				storeobjArray.add(storeobj);
			}
			page.setCurrentPageNo(pageNo);
			page.setResult(JSONArray.toList(storeobjArray));
			jsonObject = jsonObject.fromObject(page);
		} catch (Exception e) {
			// TODO: handle exceptiond
			e.printStackTrace();
		}
		return jsonObject;
	}

	@Override
	public JSONObject getCarRepairStoreList(int pageNo, String carplate, String store_cityid, String repair4sstoreid) {
		JSONObject jsonObject = new JSONObject();
		try {
			String sql = "SELECT c1.repair4sstoreid, c1.repairlastmile, c1.carmodelid, c1.totalgain," 
					   + " c2.repairinterval, c2.brand, c3.brand_id"
					   + " FROM es_carinfo c1, es_carmodels c2, es_brand c3 WHERE c1.carmodelid=c2.id and c2.brand=c3.name and c1.carplate=?";
			List list = daoSupport.queryForList(sql, carplate);
			JSONObject obj = JSONObject.fromObject(list.get(0));
			String carmodelid = obj.getString("carmodelid");//车型ID
			long repairlastmile = obj.getInt("repairlastmile");//上次保养里程
			long repairinterval = obj.getInt("repairinterval");//保养间隔里程
			double repairtotalgain = obj.getDouble("totalgain");//可用保养收益
			int signStoreId = obj.getInt("repair4sstoreid");//签约的4s店id
			int brand_id = obj.getInt("brand_id");//获取车辆品牌id
			
			//计算下次保养里程
			long nextmile = ((repairlastmile % repairinterval) > (repairinterval / 2)) ? ((repairlastmile/repairinterval) + 2)*repairinterval : ((repairlastmile/repairinterval) + 1)*repairinterval;
			
			DecimalFormat df = new DecimalFormat("0.00");
			
			//获取支持该车型保养的通过审核的店铺id
			String storeId = "SELECT DISTINCT(t1.store_id) FROM es_store t1, es_store_repairitem t2, es_store_audit t3 WHERE t1.store_id=t2.store_id AND t1.store_id=t3.store_id AND t1.auditstatus='2' AND t3.audit_result='1' AND t2.carmodel_id="+carmodelid;
			
			StringBuilder sqlBuider = new StringBuilder();
			sqlBuider.append("SELECT store_id, store_name, REPLACE(store_logo,?,?) store_logo, customer_phone phone, attr, comment_grade, discountcontract, discountnoncontract FROM es_store WHERE store_id != 1 AND brand_id= " + brand_id + " ") ;//根据品牌获取4s店列表
			String sign_store_cityid = "";
			if(!StringUtil.isNull(repair4sstoreid)){
				sqlBuider.append(" AND store_id != '" + repair4sstoreid +"'");
				String csql = "select store_cityid from es_store where store_id="+repair4sstoreid;
				sign_store_cityid = daoSupport.queryForString(csql);
			}
			if(!StringUtil.isNull(store_cityid)){
				sqlBuider.append(" AND store_cityid = "+store_cityid);
			}else{
				sqlBuider.append(" AND store_cityid = "+sign_store_cityid);
			}
			sqlBuider.append(" and store_id in ("+ storeId +")");
			
			sqlBuider.append(" ORDER BY comment_grade DESC");

			Page page = this.daoSupport.queryForPage(sqlBuider.toString(),pageNo, 10, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_domain());
			JSONArray storeobjArray = new JSONArray();
			if(!StringUtil.isNull(repair4sstoreid) && pageNo==1){//第一页时获取当前店铺信息放在list第一位
				JSONArray belongStoreArray = this.get4sStoreById(Integer.parseInt(repair4sstoreid));
				belongStoreArray.addAll((List)page.getResult());
				page.setResult(belongStoreArray.toString());
			}
			list = JSONArray.toList(JSONArray.fromObject(page.getResult()));

			//name telephone address image mkprice price securitygain repairestimatedmaxgain nextmile  repaircontent
			for(int i=0; i<list.size(); i++){
				JSONObject storeobj = JSONObject.fromObject(list.get(i));
				String store_logo = UploadUtil.replacePath(storeobj.getString("store_logo"));
				storeobj.put("store_logo", store_logo);
				double discountcontract = storeobj.getDouble("discountcontract");//签约折扣
				double discountnoncontract = storeobj.getDouble("discountnoncontract");//非签约折扣
				int store_id = storeobj.getInt("store_id");
				
				sql = "SELECT SUM(item_price+repair_price) price FROM es_store_repairitem WHERE carmodel_id=? AND store_id=? AND repair_item_id IN "
					+ "(SELECT repair_item_id FROM (SELECT carmodel_id, repair_item_id, repair_interval, MOD(?, repair_interval) m FROM es_carmodel_repair_items) t WHERE t.carmodel_id=? AND t.m = 0)";
				List repairList = daoSupport.queryForList(sql, carmodelid, store_id, nextmile, carmodelid);
				obj = JSONObject.fromObject(repairList.get(0));
				double mkprice = obj.getDouble("price");//获取下次保养价格

				if(store_id == signStoreId){
					storeobj.put("sign", "yes");
					double repairestimatedmaxgain = mkprice * (1 - discountcontract);//预计保养最大收益，即保养收益使用最高额度
					repairestimatedmaxgain = Double.valueOf(df.format(repairestimatedmaxgain));
					storeobj.put("repairestimatedmaxgain", repairestimatedmaxgain);
					mkprice = mkprice * discountcontract;
				}else{
					storeobj.put("sign", "no");
					double repairestimatedmaxgain = mkprice * (1 - discountnoncontract);//预计保养最大收益，即保养收益使用最高额度
					repairestimatedmaxgain = Double.valueOf(df.format(repairestimatedmaxgain));
					storeobj.put("repairestimatedmaxgain", repairestimatedmaxgain);
					mkprice = mkprice * discountnoncontract;
				}
				
				sql = "select t.*, "+ mkprice +" * ratio price from es_repair_timeregion t order by time_region_id";
				List time_region_list = daoSupport.queryForList(sql);
				double min_price = -1.0;
				double max_price = -1.0;
				if(time_region_list.size() > 0){
					sql = "SELECT MAX(price) max_price, MIN(price) min_price FROM  (" + sql + ") a";
					JSONObject priceObj = JSONObject.fromObject(daoSupport.queryForList(sql).get(0));
					min_price = priceObj.getDouble("min_price");
					max_price = priceObj.getDouble("max_price");
				}
				
				storeobj.put("mkprice", mkprice);//原价
				storeobj.put("min_price", min_price);
				storeobj.put("max_price", max_price);
				storeobjArray.add(storeobj);
			}
			page.setCurrentPageNo(pageNo);
			page.setResult(JSONArray.toList(storeobjArray));
			jsonObject = jsonObject.fromObject(page);
		} catch (Exception e) {
			// TODO: handle exceptiond
			e.printStackTrace();
		}
		return jsonObject;
	}

	@Override
	public JSONArray get4sStoreById(int repair4sstoreid) {
		JSONArray jsonArray  = new JSONArray();
		try{
			String sql = "SELECT store_id, store_name, store_logo, customer_phone phone, attr, comment_grade, discountcontract, discountnoncontract FROM es_store where store_id = ?";
			List  list = daoSupport.queryForList(sql, repair4sstoreid);
			jsonArray = jsonArray.fromObject(list);
		}catch(Exception e){
			e.printStackTrace();
		}
		return jsonArray;
	}

	@Override
	public Page listComment(int pageNo,int pageSize,int store_id) {
		Page page = null;
		try{
			StringBuffer sql =new StringBuffer("select c.*,m.username,m.face from es_member m ,es_comment c where m.member_id = c.member_id and c.store_id = ?");
			page = this.daoSupport.queryForPage(sql.toString(),pageNo, pageSize,store_id);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return  page;
	}

	@Override
	public Map get4sGrades(int store_id) {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("select cast(sum(service_grade)/count(*)  as  decimal(20,1)) count_service_grade,cast(sum(goods_grade)/count(*)  as  decimal(20,1)) count_goods_grade  from es_comment where store_id = ?");
 			Map map = daoSupport.queryForMap(sql.toString(),store_id);
 			return map;
		}catch(Exception e){
			return null;
		}
	
	}

	@Override
	public JSONObject getStoreRepairDetail(String store_id, String carplate, String order_time) {
		JSONObject obj = new JSONObject();
		try {
			JSONObject returnObj = new JSONObject();
			
			String sql = "SELECT t1.repairlastmile, t1.carmodelid, t1.repair4sstoreid, t2.repairinterval FROM es_carinfo t1, es_carmodels t2 WHERE t1.carmodelid=t2.id AND t1.carplate=?";
			List list = daoSupport.queryForList(sql, carplate);
			JSONObject carmodelObj = JSONObject.fromObject(list.get(0));
			int carmodel_id = carmodelObj.getInt("carmodelid");//车型ID
			int repair4sstoreid = carmodelObj.getInt("repair4sstoreid");//签约4s店id
			long repairlastmile = carmodelObj.getInt("repairlastmile");//上次保养里程
			long repairinterval = carmodelObj.getInt("repairinterval");//保养间隔里程
			//计算下次保养里程
			long nextmile = ((repairlastmile % repairinterval) > (repairinterval / 2)) ? ((repairlastmile/repairinterval) + 2)*repairinterval : ((repairlastmile/repairinterval) + 1)*repairinterval;
		
			/**
			 * 获取店铺保养预约时间范围
			 */
			sql = "SELECT repair_time_range, discountcontract, discountnoncontract FROM es_store WHERE store_id=?";
			list = daoSupport.queryForList(sql, store_id);
			JSONObject storeObj = JSONObject.fromObject(list.get(0));
			int repair_time_range = storeObj.getInt("repair_time_range");
			double discountcontract = storeObj.getDouble("discountcontract");
			double discountnoncontract = storeObj.getDouble("discountnoncontract");
			double discount = 0.0;
			if(repair4sstoreid == Integer.valueOf(store_id)){//签约折扣
				discount = discountcontract;
			}else{//非签约折扣
				discount = discountnoncontract;
			}
	
			/**
			 * 获取店铺车型保养项目（含必须保养和可选保养项目）
			 */
			//获取必选保养项目价格list
			String itemSql1 = "SELECT t1.repair_item_id, t2.itemname, t2.is_necessary, t1.item_price*"+ discount +" item_price, t1.repair_price*"+ discount +" repair_price, 1 is_required FROM es_store_repairitem t1, es_repair_items t2 WHERE t1.repair_item_id=t2.id AND t1.store_id=? AND t1.carmodel_id=? AND t1.repair_item_id IN ";
			JSONArray requiredItemArray = new JSONArray();
			String requiredRepairItemId = "SELECT repair_item_id FROM (SELECT carmodel_id, repair_item_id, repair_interval, IFNULL(MOD(?, repair_interval),1) m FROM es_carmodel_repair_items) t WHERE t.carmodel_id=? AND t.m = 0";
			String requiredItemSql = itemSql1 + "("+ requiredRepairItemId +") order by t1.repair_item_id";
			List requiredItemList = daoSupport.queryForList(requiredItemSql, store_id, carmodel_id, nextmile, carmodel_id);
			String repair_item_ids = "";
			if(requiredItemList.size() > 0){
				requiredItemArray = JSONArray.fromObject(requiredItemList);
				for(int i=0; i<requiredItemArray.size(); i++){
					JSONObject tempObj = requiredItemArray.getJSONObject(i);
					repair_item_ids += tempObj.getInt("repair_item_id") + ",";
				}
			}
			repair_item_ids = repair_item_ids.length() > 0 ? repair_item_ids.substring(0, repair_item_ids.length()-1) : repair_item_ids;
		
			//获取可选保养项目价格list
			String itemSql2 = "SELECT t1.repair_item_id, t2.itemname, t2.is_necessary, t1.item_price*"+ discount +" item_price, t1.repair_price*"+ discount +" repair_price, 0 is_required FROM es_store_repairitem t1, es_repair_items t2 WHERE t1.repair_item_id=t2.id AND t1.store_id=? AND t1.carmodel_id=? AND t1.repair_item_id IN ";
			JSONArray choosableItemArray = new JSONArray();
			String choosableRepairItemId = "SELECT repair_item_id FROM (SELECT carmodel_id, repair_item_id, repair_interval, IFNULL(MOD(?, repair_interval),1) m FROM es_carmodel_repair_items) t WHERE t.carmodel_id=? AND t.m <> 0";
			String choosableItemSql = itemSql2 + "("+ choosableRepairItemId +") order by t1.repair_item_id";
			List choosableItemList = daoSupport.queryForList(choosableItemSql, store_id, carmodel_id, nextmile, carmodel_id);
			if(choosableItemList.size() > 0){
				choosableItemArray = JSONArray.fromObject(choosableItemList);
			}
			
			/**
			 * 获取店铺保养时间段
			 */
			JSONArray timeRegionArray = new JSONArray();
			
			//获取店铺保养项目价格合计
			sql = "SELECT SUM(item_price+repair_price) * "+ discount +" price, SUM(item_price) * "+ discount +" items_price, SUM(repair_price) * "+ discount +" repair_total_price "
				+ "FROM es_store_repairitem t1, es_repair_items t2 WHERE t1.repair_item_id=t2.id AND t1.store_id=? AND t1.carmodel_id=? AND t1.repair_item_id IN "+ "("+ requiredRepairItemId +")";
			List priceList = daoSupport.queryForList(sql, store_id, carmodel_id, nextmile, carmodel_id);
			
			double price = 0.0;
			double items_price = 0.0;
			double repair_total_price = 0.0;
			if(priceList.size() > 0){
				JSONObject priceObj = JSONObject.fromObject(priceList.get(0));
				price = priceObj.getDouble("price");
				items_price = priceObj.getDouble("items_price");
				repair_total_price = priceObj.getDouble("repair_total_price");
			}
			
			//获取店铺保养时间段和工位
			SimpleDateFormat regionFormat = new SimpleDateFormat("HH:mm");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			long book_time = sdf.parse(order_time).getTime();
			String time_range = regionFormat.format(book_time);//获取预约起始时间HH:mm
			
			if(sdf.parse(sdf.format(System.currentTimeMillis())).getTime() == book_time){//如果是当天
				Calendar ca = Calendar.getInstance();
				ca.setTime(new Date());
				ca.add(Calendar.HOUR, 2);
				time_range = regionFormat.format(ca.getTimeInMillis());
			}
			
			sql = "select t1.*, (TIME_TO_SEC(t1.starttime) > TIME_TO_SEC(?)) bookable, "+ items_price +"+"+ repair_total_price +" * t1.ratio price, (t1.station - IFNULL(t2.station, 0)) usable_station "
			    + "FROM es_repair_timeregion t1 "
			    + "LEFT JOIN (SELECT time_region_id, order_date, SUM(station) station FROM es_store_repair_timeregion WHERE store_id=? AND order_date=? AND order_status=1 GROUP BY time_region_id, order_date) t2 "
			    + "ON t1.time_region_id=t2.time_region_id "
			    + "WHERE t1.store_id=? order by t1.starttime";

			List timeRegionList = daoSupport.queryForList(sql, time_range, store_id, book_time, store_id);
			if(timeRegionList.size() > 0){
				timeRegionArray = JSONArray.fromObject(timeRegionList);
			}
			
			returnObj.put("repair_time_range", repair_time_range);
			returnObj.put("repair_mile", nextmile);
			returnObj.put("required_item_list", requiredItemArray);
			returnObj.put("choosable_item_list", choosableItemArray);
			returnObj.put("repair_item_ids", repair_item_ids);
			returnObj.put("price", price);
			returnObj.put("servicetime_total_price", repair_total_price);
			returnObj.put("time_region_list", timeRegionArray);
			
			obj.put("data", returnObj);
			obj.put("result", 1);
			obj.put("message", "返回成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return obj;
	}

}
