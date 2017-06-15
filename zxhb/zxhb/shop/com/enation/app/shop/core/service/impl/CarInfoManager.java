package com.enation.app.shop.core.service.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.CarInfoVin;
import com.enation.app.base.core.model.ConsumptionStatistics;
import com.enation.app.base.jPush.util.JPushUtil;
import com.enation.app.shop.core.model.CarInfo;
import com.enation.app.shop.core.model.CarModel;
import com.enation.app.shop.core.model.TrafficRestriction;
import com.enation.app.shop.core.service.ICarInfoManager;
import com.enation.app.shop.core.service.InsureType;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.DateUtils;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.eop.sdk.utils.ValidateUtils;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;

/**
 * 车辆信息管理
 * @author linsen 2016年4月4日
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CarInfoManager extends BaseSupport implements ICarInfoManager {
	
	public static Map<String, String> carGpsMap = new HashMap<String, String>();//车辆位置信息map
	public static Map<String, Long> carPushTimeMap = new HashMap<String, Long>();//车辆位置推送时间map
	public static Map<String, String> carErrorCodeMap = new HashMap<String, String>();//车辆错误信息map
	@Override
	public String getEstimatedInsureFeeAndMaxGain(int carModelID, String carUseCharacter, long carFirstBuyTime, String InsureSet){
		double insureEstimatedFee = 0.0;
		double insureEstimatedMaxGain = 0.0;
		try {
			String[] usedtype = {"家庭自用车","企业非营业客车","机关非营业客车","营业客车"};
			String sql = "select * from es_carmodels where id=?";
			List list =  daoSupport.queryForList(sql, carModelID);
			net.sf.json.JSONObject carmodel = net.sf.json.JSONObject.fromObject(list.get(0));
			sql = "select insurance, insurediscount, zadiscount  from es_insurances where id in(" + InsureSet + ") and insurance <> '交强险'";
			List resultList = this.baseDaoSupport.queryForList(sql);
			net.sf.json.JSONObject jsonObj = JSONArray.fromObject(resultList).getJSONObject(0);
			String insurelimit = jsonObj.getString("insurance");//保险套餐内容
			double insurediscount = jsonObj.getDouble("insurediscount");//保险公司给用户的折扣价
			double zadiscount = jsonObj.getDouble("zadiscount");//保险公司给我们的折扣价
			long oneyearTime = 1000 * 60 * 60 * 24 * 365l;//一年时间的毫秒数
			double carage = (double)((System.currentTimeMillis() - carFirstBuyTime) / oneyearTime); //根据车辆购买时间计算车龄
			
			//添加具体计算流程
			double compulsory_fee = 0.0;//交强险
			double damage_fee = 0.0;//车损险
			double thirdparty_fee = 0.0;//第三责任险
			double theft_fee = 0.0;//盗抢险
			double exempt_fee = 0.0;//不计免赔险
			int seats = carmodel.getInt("seats");
			long price = carmodel.getLong("price");
			
			if(usedtype[0].equals(carUseCharacter)){//家庭自用车
				if(seats < 6){//6座以下
					compulsory_fee = 950.0;
					theft_fee = 120 + price * 0.53/100;
					if(insurelimit.contains("30万")){
						thirdparty_fee = 1226.0;
					}else if(insurelimit.contains("50万")){
						thirdparty_fee = 1472.0;
					}
					if(carage < 1.0){
						damage_fee = 539.0 + price* 1.28/100;
					}else if(carage < 2.0){
						damage_fee = 513.0 + price* 1.22/100;
					}else if(carage < 6.0){
						damage_fee = 508.0 + price* 1.21/100;
					}else{
						damage_fee = 523.0 + price* 1.24/100;
					}
				}else{
					compulsory_fee = 1100.0;
					theft_fee = 140 + price * 0.44/100;
					if(insurelimit.contains("30万")){
						thirdparty_fee = 1081.0;
					}else if(insurelimit.contains("50万")){
						thirdparty_fee = 1287.0;
					}
					if(carage < 1.0){
						damage_fee = 646.0 + price* 1.28/100;
					}else if(carage < 2.0){
						damage_fee = 616.0 + price* 1.22/100;
					}else if(carage < 6.0){
						damage_fee = 609.0 + price* 1.21/100;
					}else{
						damage_fee = 628.0 + price* 1.24/100;
					}
				}
			}else if(usedtype[1].equals(carUseCharacter)){//企业非营业客车
				if(seats < 6){//6座以下
					compulsory_fee = 1000.0;
					theft_fee = 120 + price * 0.47/100;
					if(insurelimit.contains("30万")){
						thirdparty_fee = 1287.0;
					}else if(insurelimit.contains("50万")){
						thirdparty_fee = 1531.0;
					}
					if(carage < 1.0){
						damage_fee = 335.0 + price* 1.11/100;
					}else if(carage < 2.0){
						damage_fee = 319.0 + price* 1.06/100;
					}else if(carage < 6.0){
						damage_fee = 316.0 + price* 1.05/100;
					}else{
						damage_fee = 325.0 + price* 1.08/100;
					}
				}else{
					compulsory_fee = 1130.0;
					theft_fee = 130 + price * 0.44/100;
					if(insurelimit.contains("30万")){
						thirdparty_fee = 1216.0;
					}else if(insurelimit.contains("50万")){
						thirdparty_fee = 1453.0;
					}
					if(carage < 1.0){
						damage_fee = 402.0 + price* 1.05/100;
					}else if(carage < 2.0){
						damage_fee = 383.0 + price* 1.00/100;
					}else if(carage < 6.0){
						damage_fee = 379.0 + price* 0.99/100;
					}else{
						damage_fee = 390.0 + price* 1.02/100;
					}
				}
			}else if(usedtype[2].equals(carUseCharacter)){//机关非营业客车
				if(seats < 6){//6座以下
					compulsory_fee = 950.0;
					theft_fee = 110 + price * 0.40/100;
					if(insurelimit.contains("30万")){
						thirdparty_fee = 1093.0;
					}else if(insurelimit.contains("50万")){
						thirdparty_fee = 1301.0;
					}
					if(carage < 1.0){
						damage_fee = 259.0 + price* 0.86/100;
					}else if(carage < 2.0){
						damage_fee = 247.0 + price* 0.82/100;
					}else if(carage < 6.0){
						damage_fee = 245.0 + price* 0.81/100;
					}else{
						damage_fee = 252.0 + price* 0.84/100;
					}
				}else{
					compulsory_fee = 1070.0;
					theft_fee = 120 + price * 0.37/100;
					if(insurelimit.contains("30万")){
						thirdparty_fee = 1047.0;
					}else if(insurelimit.contains("50万")){
						thirdparty_fee = 1245.0;
					}
					if(carage < 1.0){
						damage_fee = 311.0 + price* 0.82/100;
					}else if(carage < 2.0){
						damage_fee = 296.0 + price* 0.78/100;
					}else if(carage < 6.0){
						damage_fee = 293.0 + price* 0.77/100;
					}else{
						damage_fee = 302.0 + price* 0.79/100;
					}
				}
			}else if(usedtype[3].equals(carUseCharacter)){//营业客车
				if(seats < 6){//6座以下
					compulsory_fee = 1800.0;
					theft_fee = 110 + price * 0.40/100;
					//暂无
					if(insurelimit.contains("30万")){
						thirdparty_fee = 1093.0;
					}else if(insurelimit.contains("50万")){
						thirdparty_fee = 1301.0;
					}
					if(carage < 1.0){
						damage_fee = 259.0 + price* 0.86/100;
					}else if(carage < 2.0){
						damage_fee = 247.0 + price* 0.82/100;
					}else if(carage < 6.0){
						damage_fee = 245.0 + price* 0.81/100;
					}else{
						damage_fee = 252.0 + price* 0.84/100;
					}
				}else{
					compulsory_fee = 2360.0;
					//暂无
					theft_fee = 120 + price * 0.37/100;
					if(insurelimit.contains("30万")){
						thirdparty_fee = 1047.0;
					}else if(insurelimit.contains("50万")){
						thirdparty_fee = 1245.0;
					}
					if(carage < 1.0){
						damage_fee = 311.0 + price* 0.82/100;
					}else if(carage < 2.0){
						damage_fee = 296.0 + price* 0.78/100;
					}else if(carage < 6.0){
						damage_fee = 293.0 + price* 0.77/100;
					}else{
						damage_fee = 302.0 + price* 0.79/100;
					}
				}
			}
			
			exempt_fee = (thirdparty_fee + damage_fee) * 0.15;
			
			DecimalFormat df = new DecimalFormat("0.00");
			insureEstimatedFee = compulsory_fee + thirdparty_fee + damage_fee + exempt_fee + theft_fee;
			//收益百分比设为固定值：15%
			insureEstimatedMaxGain = insureEstimatedFee * (insurediscount * 0.1 + (insurediscount - zadiscount));
			insureEstimatedFee = Double.valueOf(df.format(insureEstimatedFee));
			insureEstimatedMaxGain = Double.valueOf(df.format(insureEstimatedMaxGain));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		JSONObject obj = new JSONObject();
		obj.put("insureEstimatedMaxGain", insureEstimatedMaxGain);
		obj.put("insureEstimatedFee", insureEstimatedFee);
		
		return obj.toString();
	}
	
	@Override
	public List queryBondedObds(List<String> obds, String carowner) {		
		String sql = "SELECT obdmacaddr FROM es_carinfo WHERE carowner<>? AND UPPER(obdmacaddr) in(";
		
		if(obds.size() > 0) {
			for(int i = 0; i < obds.size(); i++) {
				sql += "'" + obds.get(i) + "',";
			}
		}
		sql = sql.substring(0, sql.lastIndexOf(",")) + ")";
		List list = this.daoSupport.queryForList(sql, carowner);
		this.logger.debug("queryBondedObds:" + JSONArray.fromObject(list).toString());
		return list;
	}
	
	@Override
	public JSONArray getCarInfoList(String owner) {
		try {
			String sql = "SELECT t.*, IFNULL(t3.store_name, '') store_name, IFNULL(t3.after_phone, 114) after_phone FROM "
					   + "(SELECT t1.*, REPLACE(t2.brandimage, '"+ EopSetting.FILE_STORE_PREFIX +"', '"+ SystemSetting.getStatic_server_domain() +"') brandimage, t2.brand, t2.series, t2.sales_name, t2.seats, t2.price, t2.discharge FROM es_carinfo t1, es_carmodels t2 WHERE t1.`carmodelid`=t2.`id` AND t1.carowner=?) t"
					   + " LEFT JOIN es_store t3 ON t.repair4sstoreid=t3.store_id";
			
			List returnList = this.daoSupport.queryForList(sql, owner);

			//根据保险套餐id、4s店id获取保险套餐内容、4s店名称添加到返回的JSONObject中
			if(returnList.size() > 0){
				JSONArray returnJsonArray = JSONArray.fromObject(returnList);
				for(int i=0; i<returnJsonArray.size(); i++){
					net.sf.json.JSONObject carObj = returnJsonArray.getJSONObject(i);
					carObj.put("car_use_type", CarInfo.carUseTypeMap.get(carObj.getInt("car_use_type")));
					carObj.put("car_kind", CarInfo.carKindMap.get(carObj.getInt("car_kind")));
					carObj.put("carplate", carObj.getString("carplate").toUpperCase());
				}
				this.logger.debug("carInfoList:" + returnJsonArray.toString());
				return returnJsonArray;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public JSONArray getCarInfoMobile(String carplate) {
		try {
			String sql = "SELECT c1.*, c2.*, c3.store_name, c3.after_phone FROM es_carinfo c1, es_carmodels c2, es_store c3 WHERE c1.`carmodelid`=c2.`id` AND c1.`repair4sstoreid`=c3.`store_id` AND c1.carplate=? ORDER BY c1.id";
			List carInfoList = daoSupport.queryForList(sql, carplate);
			if(carInfoList.size() > 0){
				net.sf.json.JSONArray returnObj = getInsureAndRepair4sInfo(JSONArray.fromObject(carInfoList));
				
				return returnObj;
			}
			return null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List getCarInfoByCarplate(String carPlate) {
		try {
			String sql = "SELECT t1.*, t2.repairintervaltime FROM es_carinfo t1, es_carmodels t2 WHERE t1.carmodelid=t2.id AND t1.carplate=?";
			List carInfoList = daoSupport.queryForList(sql,carPlate);
			
			return carInfoList;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public net.sf.json.JSONArray updateCarInfo(String carInfoJson){
		try {
			JSONObject obj = new JSONObject(carInfoJson);
			String carPlate = obj.getString("carplate").toUpperCase();
			String carOwner = obj.getString("carowner");
			if(obj.has("carfirstbuytime")){
				long insurenextbuytime = getInsureNextTime(obj.getLong("carfirstbuytime"));
				obj.put("insurenextbuytime", insurenextbuytime);
			}
			if(obj.has("carvin")){//添加车辆时，设置创建时间
				obj.put("create_time", new Date().getTime());
			}
			if(obj.has("carmodelid") && obj.has("carusecharacter") && obj.has("carfirstbuytime") && obj.has("insuresetid")){
				String insureJson = getEstimatedInsureFeeAndMaxGain(obj.getInt("carmodelid"), obj.getString("carusecharacter"), obj.getLong("carfirstbuytime"), obj.getString("insuresetid"));
				net.sf.json.JSONObject insureObj = net.sf.json.JSONObject.fromObject(insureJson);
				obj.put("insureestimatedfee", insureObj.get("insureEstimatedFee"));
				obj.put("insureestimatedmaxgain", insureObj.get("insureEstimatedMaxGain"));
			}
			if(obj.has("repair4sstoreid")){//设置签约时间
				obj.put("contracttime", System.currentTimeMillis());
			}
			if(obj.has("repair4sstoreid") && obj.has("obdmacaddr")){//设置初装4s店
				addOriginalInfo(obj.getInt("repair4sstoreid"), obj.getString("obdmacaddr"), carPlate);
				obj.put("original_storeid", obj.getInt("repair4sstoreid"));
			}
			if(obj.has("repairlastmile")){
				obj.put("repairlastmile_updatetime", new Date().getTime());//设置上次保养里程更新时间，用于在上传行程时进行校验
			}
			if(obj.has("carmodelid") && obj.has("repairlastmile") && obj.has("repairlasttime")){
				net.sf.json.JSONObject repairInfoObj = getRepairInfo(obj);
				obj.put("repairestimatedfee", repairInfoObj.getDouble("repairestimatedfee"));
				obj.put("repairnexttime", repairInfoObj.getLong("repairnexttime"));
			}
			
			String sql;
			String productSql;
			//add check for carOwner, carOwner必须是合法的用户
			if (carPlate != null && carOwner != null){
				sql = "SELECT * FROM es_carinfo c WHERE carplate=? ORDER BY c.id";
				List list = this.daoSupport.queryForList(sql, carPlate);
				if (list.size() > 0) {
					//update
					sql = "update es_carinfo set ";
				} else {
					//add a new car
					sql = "insert into es_carinfo set ";
				}
				
				Iterator<String> keys = obj.keys();
				while(keys.hasNext()) {
					String key = keys.next();
					if(!"carplate".equals(key) && !"carowner".equals(key)){
						sql += key + "='" + obj.get(key).toString() + "'";
						if (keys.hasNext())
							sql += ",";
					}
				}
				if(sql.lastIndexOf(",") == sql.length()-1){ 
					sql = sql.substring(0, sql.length()-1);
				}
				if(list.size() == 0){
					sql += ", carplate='" + carPlate +"', carowner='"+ carOwner +"'";
				}
				if(list.size() > 0){
					sql += " where carplate='" + carPlate +"'";
				}
				this.logger.debug("updateCarInfo: " + sql);
				this.baseDaoSupport.execute(sql);
				
				//将选定的保险套餐信息作为product添加到数据库表es_product
				if(obj.has("insuresetid")){
					int goods_id = getGoodsidOfInsureOrRepair(1);
					productSql = "select product_id from es_product where name=? and goods_id=?";
					List productidlist = daoSupport.queryForList(productSql, carPlate, goods_id);
					if(productidlist.size() == 0){
						productSql = "insert into es_product set goods_id=?, name=?";
						baseDaoSupport.execute(productSql, goods_id, carPlate);
					}
				}
				//将选定的保养套餐信息作为product添加到数据库表es_product
				if(obj.has("repair4sstoreid")){
					int goods_id = getGoodsidOfInsureOrRepair(2);
					productSql = "select product_id from es_product where name=? and goods_id=?";
					List productidlist = daoSupport.queryForList(productSql, carPlate, goods_id);
					if(productidlist.size() == 0){
						productSql = "insert into es_product set goods_id=?, name=?";
						baseDaoSupport.execute(productSql, goods_id, carPlate);
					}
				}
			}
			
			sql = "SELECT c1.*, c2.*, c3.store_name, c3.after_phone FROM es_carinfo c1, es_carmodels c2, es_store c3 WHERE c1.`carmodelid`=c2.`id` AND c1.`repair4sstoreid`=c3.`store_id` AND c1.carowner=? ORDER BY c1.id";
			List returnList = this.daoSupport.queryForList(sql, carOwner);
			
			//根据保险套餐id、4s店id获取保险套餐内容、4s店名称添加到返回的JSONObject中
			if(returnList.size() > 0){
				net.sf.json.JSONArray returnObj = getInsureAndRepair4sInfo(JSONArray.fromObject(returnList));
				this.logger.debug("updateCarInfo:" + returnObj.toString());
				return returnObj;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return null;
	}
	
	private void addOriginalInfo(Integer store_id, String obdmacaddr, String carPlate) {
		String sql = "insert into es_store_originalobd set original_storeid=?, obdmacaddr=?, create_time=?, carplate=?";
		daoSupport.execute(sql, store_id, obdmacaddr, new Date().getTime(), carPlate);
	}

	public Integer getGoodsidOfInsureOrRepair(int type){
		int goods_id = 0;
		if(type == 1){
			goods_id = daoSupport.queryForInt("select goods_id from es_goods where name='汽车保险'");
		}
		if(type == 2){
			goods_id = daoSupport.queryForInt("select goods_id from es_goods where name='汽车保养'");
		}
		return goods_id;
	}
	
	@Override
	public boolean updateCarRepairInfo(String carplate, String repairlastmile, String repairlasttime, String repairlastprice, String repair_source) {
		try {
			String sql = "update es_carinfo set";
			ArrayList params = new ArrayList();
			if(!StringUtil.isNull(repairlastmile)){
				sql += " repairlastmile=?, repairlastmile_updatetime=?,";
				params.add(repairlastmile);
				params.add(new Date().getTime());
			}
			if(!StringUtil.isNull(repairlasttime)){
				sql += " repairlasttime=?,";
				params.add(DateUtil.toDate(repairlasttime, null).getTime());
			}
			if(!StringUtil.isNull(repairlastprice)){
				sql += " repairlastprice=?,";
				params.add(repairlastprice.replaceAll(",", ""));
			}
			if(!StringUtil.isNull(repair_source)){
				sql += " repair_source=?,";
				params.add(repair_source);
			}
			if(sql.lastIndexOf(",") == sql.length()-1){ sql = sql.substring(0, sql.length()-1);}
			sql += " where carplate=?";
			params.add(carplate);
			daoSupport.execute(sql, params.toArray());
			
			//更新下次保养时间，计算下次保养预估费用
			JSONObject obj = new JSONObject(net.sf.json.JSONObject.fromObject(getCarInfoByCarplate(carplate).get(0)).toString());
			net.sf.json.JSONObject json = getRepairInfo(obj);
			sql = "update es_carinfo set repairestimatedfee=?, repairnexttime=? where carplate=?";
			daoSupport.execute(sql, json.get("repairestimatedfee"), json.get("repairnexttime"), carplate);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}
	
	private net.sf.json.JSONObject getCarRepairInfo(String carPlate, JSONObject obj) {
		long repairlastmile = obj.getLong("repairlastmile");//上次保养里程
		long repairlasttime = obj.getLong("repairlasttime");//上次保养时间
		String querySql = "SELECT t2.repairinterval, t2.repairintervaltime, t2.id, t1.repair4sstoreid FROM es_carinfo t1, es_carmodels t2 WHERE t1.carmodelid=t2.id AND t1.carplate=?";
		List list = daoSupport.queryForList(querySql, carPlate);
		net.sf.json.JSONObject carmodelObj = net.sf.json.JSONObject.fromObject(list.get(0));
		long repairinterval = carmodelObj.getLong("repairinterval");//保养间隔里程
		long repairintervaltime = carmodelObj.getLong("repairintervaltime");//保养间隔时间
		int carmodel_id = carmodelObj.getInt("id");//车型id
		int repair4sstoreid = carmodelObj.getInt("repair4sstoreid");//车辆签约店铺id
		long repairnexttime = repairlasttime + repairintervaltime;
		repairnexttime = repairnexttime < new Date().getTime() ? new Date().getTime() : repairnexttime;
		
		//计算下次保养里程
		long repairnextmile = ((repairlastmile % repairinterval) > (repairinterval / 2)) ? ((repairlastmile/repairinterval) + 2)*repairinterval : ((repairlastmile/repairinterval) + 1)*repairinterval;
		
		net.sf.json.JSONObject returnObj = new net.sf.json.JSONObject();
		if(repair4sstoreid == 0){//还未签约店铺
			returnObj.put("repairestimatedfee", 0.0);
			returnObj.put("repairnexttime", repairnexttime);
		}else{//已经签约，获取店铺保养项目价格
			String requiredRepairItemId = "SELECT repair_item_id FROM (SELECT carmodel_id, repair_item_id, repair_interval, IFNULL(MOD(?, repair_interval),1) m FROM es_carmodel_repair_items) t WHERE t.carmodel_id=? AND t.m = 0";
			querySql = "SELECT SUM(item_price+repair_price) price FROM es_store_repairitem t1, es_repair_items t2 "
					 + "WHERE t1.repair_item_id=t2.id AND t1.store_id=? AND t1.carmodel_id=? AND t1.repair_item_id IN "+ "("+ requiredRepairItemId +")";
			List priceList = daoSupport.queryForList(querySql, repair4sstoreid, carmodel_id, repairnextmile, carmodel_id);
			String priceStr = net.sf.json.JSONObject.fromObject(priceList.get(0)).getString("price");
			double price = StringUtil.isEmpty(priceStr) ? 0.0 : Double.valueOf(priceStr);
			
			querySql = "SELECT discountcontract FROM es_store WHERE store_id="+repair4sstoreid;
			String discountcontract = daoSupport.queryForString(querySql);
			
			if(!StringUtil.isEmpty(discountcontract)){
				price = price * Double.valueOf(discountcontract);
			}
			
			DecimalFormat df = new DecimalFormat("0.00");
			
			returnObj.put("repairestimatedfee", df.format(price));
			returnObj.put("repairnexttime", repairnexttime);
		}
		
		return returnObj;
	}

	private net.sf.json.JSONObject getRepairInfo(JSONObject obj) {
		int carmodelid = obj.getInt("carmodelid");
		long repairlastmile = obj.getLong("repairlastmile");//上次保养里程
		long repairlasttime = obj.getLong("repairlasttime");//上次保养时间
		String querySql = "SELECT repairinterval, repairintervaltime FROM es_carmodels WHERE id=?";
		List list = daoSupport.queryForList(querySql, carmodelid);
		net.sf.json.JSONObject carmodelObj = net.sf.json.JSONObject.fromObject(list.get(0));
		long repairinterval = carmodelObj.getLong("repairinterval");//保养间隔里程
		long repairintervaltime = carmodelObj.getLong("repairintervaltime");//保养间隔时间
		long repairnexttime = repairlasttime + repairintervaltime;
		repairnexttime = repairnexttime < new Date().getTime() ? new Date().getTime() : repairnexttime;
		
		//计算下次保养里程
		long nextmile = ((repairlastmile % repairinterval) > (repairinterval / 2)) ? ((repairlastmile/repairinterval) + 2)*repairinterval : ((repairlastmile/repairinterval) + 1)*repairinterval;
		querySql = "select repaircontent, price from es_repairintervalitem where carmodelid=? and repairinterval=?";
		list = daoSupport.queryForList(querySql, carmodelid, nextmile);
		carmodelObj = net.sf.json.JSONObject.fromObject(list.get(0));
		long mkprice = carmodelObj.getLong("price");//获取下次保养价格
		
		net.sf.json.JSONObject returnObj = new net.sf.json.JSONObject();
		returnObj.put("repairestimatedfee", mkprice);
		returnObj.put("repairnexttime", repairnexttime);
		return returnObj;
	}

	@Override
	public net.sf.json.JSONObject updateInsureAndRepair4sInfo(String carPlate, String insuranceId, String repair4sStoreId, String insureestimatedfee, String insureestimatedmaxgain) {
		try {
			String sql = "update es_carinfo set ";
			if(insuranceId != null && !"".equals(insuranceId)){
				sql += "insuresetid='"+insuranceId+"'";
				net.sf.json.JSONObject carinfoObj = net.sf.json.JSONObject.fromObject(getCarInfoByCarplate(carPlate).get(0));
				net.sf.json.JSONObject insureObj = net.sf.json.JSONObject.fromObject(getEstimatedInsureFeeAndMaxGain(carinfoObj.getInt("carmodelid"), carinfoObj.getString("carusecharacter"), carinfoObj.getLong("carfirstbuytime"), insuranceId));
				sql += ", insureestimatedfee="+ insureObj.getDouble("insureEstimatedFee") +", insureestimatedmaxgain="+ insureObj.getDouble("insureEstimatedMaxGain");
			}
			if(repair4sStoreId != null && !"".equals(repair4sStoreId)){
				sql += "repair4sstoreid="+repair4sStoreId;
				
				//修改会员当前车辆的签约店铺id,记录解约操作到解约记录表
				String productSql = "select repair4sstoreid, contracttime from es_carinfo where carplate=?";
				List list = daoSupport.queryForList(productSql, carPlate);
				net.sf.json.JSONObject tmpObj = net.sf.json.JSONObject.fromObject(list.get(0));
				String last_belong_store_id = tmpObj.getString("repair4sstoreid");
				String contracttime = "null".equals(tmpObj.getString("contracttime")) ? "0" : tmpObj.getString("contracttime");
				long new_contracttime = System.currentTimeMillis();
			
				if(!repair4sStoreId.equals(last_belong_store_id)){
					//记录解约操作
					productSql = "insert into es_discontract_record set contract_store_id=?, discontract_store_id=?, carplate=?, contract_time=?, discontract_time=?";
					daoSupport.execute(productSql, repair4sStoreId, last_belong_store_id, carPlate, contracttime, new_contracttime);
					
					//修改签约4s店
					productSql = "update es_carinfo set repair4sstoreid=?, last_belong_store_id=?, contracttime=? where carplate=?";
					daoSupport.execute(productSql, repair4sStoreId, last_belong_store_id, new_contracttime, carPlate);
				}
			}
			sql += " where carplate='"+carPlate+"'";
			this.daoSupport.execute(sql);
			sql = "SELECT c1.*, c2.*, c3.store_name, c3.after_phone FROM es_carinfo c1, es_carmodels c2, es_store c3 WHERE c1.`carmodelid`=c2.`id` AND c1.`repair4sstoreid`=c3.`store_id` AND c1.carplate=? ORDER BY c1.id";
			List returnList = this.daoSupport.queryForList(sql, carPlate);
			net.sf.json.JSONObject returnObj = getInsureAndRepair4sInfo(JSONArray.fromObject(returnList)).getJSONObject(0);
			
			this.logger.debug("carInfo:" + returnObj.toString());
			return returnObj;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 公共方法：用于获取车辆的保险内容、保养的4s店名称
	 * @param jsonArray
	 * @return
	 */
	public net.sf.json.JSONArray getInsureAndRepair4sInfo(JSONArray jsonArray){
		for(int i=0; i<jsonArray.size(); i++){
			net.sf.json.JSONObject jsonObj = jsonArray.getJSONObject(i);
			String carmodelimage = UploadUtil.replacePath(jsonObj.getString("carmodelimage"));
			String brandimage = UploadUtil.replacePath(jsonObj.getString("brandimage"));
			String modelimage = UploadUtil.replacePath(jsonObj.getString("modelimage"));
			String insuresetid = jsonObj.getString("insuresetid");
			List insureList = null;
			String sql = "select company, insurance from es_insurances where id";
			if(insuresetid.indexOf(",") > 0){
				sql += " in ("+ insuresetid +")";
				insureList = this.daoSupport.queryForList(sql);
			}else{
				sql += "=?";
				insureList = this.daoSupport.queryForList(sql, insuresetid); 
			}
			net.sf.json.JSONArray insureArray = JSONArray.fromObject(insureList);
			String insureContent = "";
			String insureCompany = "";
			for(int j=0; j<insureArray.size(); j++){
				insureContent += insureArray.getJSONObject(j).getString("insurance") + "/";
				insureCompany = insureArray.getJSONObject(j).getString("company");
			}
			insureContent = insureContent.substring(0, insureContent.lastIndexOf("/"));
			jsonObj.put("insurance", insureContent);
			jsonObj.put("insurecompany", insureCompany);
			jsonObj.put("carmodelimage", carmodelimage);
			jsonObj.put("brandimage", brandimage);
			jsonObj.put("modelimage", modelimage);
		}
		return jsonArray;
	}
	
	/**
	 * 根据首次购车时间计算下次保险购买时间
	 */
	public long getInsureNextTime(long carfirstbuydate) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String carfirstbuytime = sdf.format(carfirstbuydate);
			Calendar buytime = Calendar.getInstance();
			buytime.setTime(sdf.parse(carfirstbuytime));
			Calendar currtime = Calendar.getInstance();
			currtime.setTime(new Date());
			String insurenextbuytime = "";//下次保险购买时间
			
			if(sdf.format(buytime.getTime()).equals(sdf.format(currtime.getTime()))){
				insurenextbuytime = sdf.format(currtime.getTime());
			}else{
				if(buytime.getTimeInMillis() < currtime.getTimeInMillis()){
					if(buytime.get(Calendar.MONTH) < currtime.get(Calendar.MONTH)){
						insurenextbuytime = (currtime.get(Calendar.YEAR)+1) +"-"+ (buytime.get(Calendar.MONTH)+1) +"-"+ buytime.get(Calendar.DATE);
					}else if(buytime.get(Calendar.MONTH) == currtime.get(Calendar.MONTH)){
						if(buytime.get(Calendar.DATE) < currtime.get(Calendar.DATE)){
							insurenextbuytime = currtime.get(Calendar.YEAR) +"-"+ (buytime.get(Calendar.MONTH)+1) +"-"+ buytime.get(Calendar.DATE);
						}else{
							insurenextbuytime = (currtime.get(Calendar.YEAR)+1) +"-"+ (buytime.get(Calendar.MONTH)+1) +"-"+ buytime.get(Calendar.DATE);
						}
					}else{
						insurenextbuytime = (currtime.get(Calendar.YEAR)) +"-"+ (buytime.get(Calendar.MONTH)+1) +"-"+ buytime.get(Calendar.DATE);
					}
				}
			}
			return sdf.parse(insurenextbuytime).getTime();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public boolean unboundObdByCarplate(String car_id) {
		try {
			String sql = "update es_carinfo set obdmacaddr='', original_storeid=0 where id=?";
			daoSupport.execute(sql, car_id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public net.sf.json.JSONObject getCarRepairManual(String carplate) {
		net.sf.json.JSONObject obj = new net.sf.json.JSONObject();
		
		try {
			net.sf.json.JSONObject dataObj = new net.sf.json.JSONObject();
			net.sf.json.JSONObject carmodelObj = new net.sf.json.JSONObject();//车辆信息对象
			JSONArray repairMenualArray = new JSONArray();//保养手册array
			/**
			 * 获取车辆信息
			 */
			String sql = "SELECT t3.logo brandimage, t1.carplate, t1.carmodelid, t1.totalmile, t1.repairlastmile, FROM_UNIXTIME(t1.car_register_time/1000,'%Y-%m-%d') car_register_time, t2.brand, t2.series, t2.nk, t2.sales_name, t2.repairinterval "
					   + "FROM es_carinfo t1, es_carmodels t2, es_brand t3 WHERE t1.carmodelid=t2.id AND t2.brand_id=t3.brand_id AND t1.carplate=?";
			List list = daoSupport.queryForList(sql, carplate);
			if(list.size() > 0){
				carmodelObj = net.sf.json.JSONObject.fromObject(list.get(0));
				
				/**
				 * 获取车型保养信息
				 */
				int carmodel_id = carmodelObj.getInt("carmodelid");
				long repairinterval = carmodelObj.getLong("repairinterval");
				long repairlastmile = carmodelObj.getLong("repairlastmile");
				carmodelObj.put("brandimage", UploadUtil.replacePath(carmodelObj.getString("brandimage")));
				//获取下次保养里程
				long nextmile = ((repairlastmile % repairinterval) > (repairinterval / 2)) ? ((repairlastmile/repairinterval) + 2)*repairinterval : ((repairlastmile/repairinterval) + 1)*repairinterval;
				List<Long> repairIntervalList = getRepairIntervalList(repairinterval, CarModel.max_mile);//获取保养公里间隔list
				
				String itemSql = "SELECT itemname, is_required FROM "
						       + "(SELECT t1.carmodel_id, t1.repair_item_id, t2.itemname, IFNULL(MOD(?, repair_interval),1) is_required FROM es_carmodel_repair_items t1, es_repair_items t2 WHERE t1.repair_item_id=t2.id) t "
						       + "WHERE t.carmodel_id=? ORDER BY t.repair_item_id";

				for(int i=0; i<repairIntervalList.size(); i++){
					net.sf.json.JSONObject repairObj = new net.sf.json.JSONObject();
					long tempInterval = repairIntervalList.get(i);
					repairObj.put("repair_mile", tempInterval);
					
					if(tempInterval == 0){//0公里做特殊处理
						repairObj.put("is_repaired", 0);
						repairObj.put("required_content", "");
						repairObj.put("choosable_content", "");
					}else{
						//获取保养里程高亮状态
						if(nextmile > tempInterval){//已经保养
							repairObj.put("is_repaired", 0);
						}else if(nextmile == tempInterval){//下一次将要保养
							repairObj.put("is_repaired", 1);
						}else{
							repairObj.put("is_repaired", 2);//以后要保养
						}
						//获取保养里程的保养内容
						List itemList = daoSupport.queryForList(itemSql, tempInterval, carmodel_id);
						if(itemList.size() > 0){
							JSONArray itemArray = JSONArray.fromObject(itemList);
							List<String> requiredList = new ArrayList<>();
							List<String> choosableList = new ArrayList<>();
							for(int j=0; j<itemArray.size(); j++){
								net.sf.json.JSONObject itemObj = itemArray.getJSONObject(j);
								String itemname = itemObj.getString("itemname");
								int is_required = itemObj.getInt("is_required");
								if(is_required == 0){
									requiredList.add(itemname);
								}else{
									choosableList.add(itemname);
								}
							}
							repairObj.put("required_content", StringUtil.arrayToString(requiredList.toArray(), "、"));
							repairObj.put("choosable_content", StringUtil.arrayToString(choosableList.toArray(), "、"));
						}
					}
					
					repairMenualArray.add(repairObj);
				}
			}
			
			dataObj.put("car_info", carmodelObj);
			dataObj.put("repairMenualArray", repairMenualArray);
			
			obj.put("data", dataObj);
			obj.put("result", 1);
			obj.put("message", "成功获取数据");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return obj;
	}
	
	public List<Long> getRepairIntervalList(long interval, long total_interval){
		int count = (int) (total_interval / interval);
		List<Long> list = new ArrayList<>();
		long currInterval = 0;
		for(int i=0; i<=count; i++){
			list.add(currInterval);
			currInterval += interval;
		}
		
		return list;
	}

	@Override
	public net.sf.json.JSONObject updateContractRepair4sStoreInfo(String carplate, String repair4sstoreid, String repairlastmile, String repairlasttime) {
		try {
			//更新车辆签约信息
			repairlastmile = ("".equals(repairlastmile) || repairlastmile == null) ? "0" : repairlastmile;
			repairlasttime = ("".equals(repairlasttime) || repairlasttime == null) ? "0" : repairlasttime;
			
			String sql = "select repair4sstoreid, repairlastmile from es_carinfo where carplate=?";
			List carinfoList = daoSupport.queryForList(sql, carplate);
			int contract_store_id = 0;
			if(carinfoList.size() > 0){
				net.sf.json.JSONObject carObj = net.sf.json.JSONObject.fromObject(carinfoList.get(0));
				contract_store_id = carObj.getInt("repair4sstoreid");
				if("0".equals(repairlastmile)){//只是修改签约店铺，则查询车辆的上次保养里程并赋值
					repairlastmile = carObj.getString("repairlastmile");
				}
			}
			
			JSONObject paramObj = new JSONObject();
			paramObj.put("repairlastmile", repairlastmile);
			paramObj.put("repairlasttime", repairlasttime);
			
			net.sf.json.JSONObject repairInfoObj = getCarRepairInfo(carplate, paramObj);
			sql = "update es_carinfo set repair4sstoreid=?, repairlastmile=?, repairestimatedfee=?, contracttime=? ";
			if("0".equals(repairlasttime) && contract_store_id == 0){//首次签约，更新下次保养时间
				sql += ", repairnexttime=? ";
			}
			if(!"0".equals(repairlasttime)){
				sql += ", repairlasttime=?, repairnexttime=? ";
			}
			sql += "where carplate=?";
			
			//如果不是第一次签约，修改会员当前车辆的签约店铺id,记录解约操作到解约记录表
			if(contract_store_id != 0){
				String productSql = "select contracttime, repair4sstoreid from es_carinfo where carplate=?";
				List list = daoSupport.queryForList(productSql, carplate);
				net.sf.json.JSONObject tmpObj = net.sf.json.JSONObject.fromObject(list.get(0));
				String last_belong_store_id = tmpObj.getString("repair4sstoreid");
				long contracttime = tmpObj.getLong("contracttime");
				long new_contracttime = System.currentTimeMillis();
			
				if(!repair4sstoreid.equals(last_belong_store_id)){
					//记录解约操作
					productSql = "insert into es_discontract_record set contract_store_id=?, discontract_store_id=?, carplate=?, contract_time=?, discontract_time=?";
					daoSupport.execute(productSql, repair4sstoreid, last_belong_store_id, carplate, contracttime, new_contracttime);
					
					//修改签约4s店
					productSql = "update es_carinfo set repair4sstoreid=?, last_belong_store_id=?, contracttime=? where carplate=?";
					daoSupport.execute(productSql, repair4sstoreid, last_belong_store_id, new_contracttime, carplate);
				}
			}
			
			if(!"0".equals(repairlasttime)){
				daoSupport.execute(sql, repair4sstoreid, repairlastmile, repairInfoObj.getDouble("repairestimatedfee"), System.currentTimeMillis(), repairlasttime, repairInfoObj.getLong("repairnexttime"), carplate);
			}else if("0".equals(repairlasttime) && contract_store_id == 0){
				daoSupport.execute(sql, repair4sstoreid, repairlastmile, repairInfoObj.getDouble("repairestimatedfee"), System.currentTimeMillis(), repairInfoObj.getLong("repairnexttime"), carplate);
			}else{
				daoSupport.execute(sql, repair4sstoreid, repairlastmile, repairInfoObj.getDouble("repairestimatedfee"), System.currentTimeMillis(), carplate);
			}

			//返回车辆列表信息
			sql = "select carowner from es_carinfo where carplate='"+ carplate +"'";
			String carowner = daoSupport.queryForString(sql);
			
			sql = "SELECT t.*, IFNULL(t3.store_name, '') store_name, IFNULL(t3.after_phone, 114) after_phone FROM "
					+ "(SELECT t1.*, REPLACE(t2.brandimage, '"+ EopSetting.FILE_STORE_PREFIX +"', '"+ SystemSetting.getStatic_server_domain() +"') brandimage, t2.brand, t2.series, t2.sales_name, t2.seats, t2.price, t2.discharge FROM es_carinfo t1, es_carmodels t2 WHERE t1.`carmodelid`=t2.`id` AND t1.carowner=?) t "
					+ "LEFT JOIN es_store t3 ON t.repair4sstoreid=t3.store_id";
				
			List returnList = this.daoSupport.queryForList(sql, carowner);

			//根据保险套餐id、4s店id获取保险套餐内容、4s店名称添加到返回的JSONObject中
			if(returnList.size() > 0){
				net.sf.json.JSONArray resultArray = JSONArray.fromObject(returnList);
				this.logger.debug("updateCarInfo:" + resultArray.toString());
				
				net.sf.json.JSONObject returnObj = new net.sf.json.JSONObject();
				
				returnObj.put("result", 1);
				returnObj.put("data", resultArray.toString());
				returnObj.put("message", "修改签约信息成功");
				
				return returnObj;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public net.sf.json.JSONObject verifyObdMacAddr(String obdmacaddr) {
		net.sf.json.JSONObject obj = new net.sf.json.JSONObject();
		boolean isBound = false;
		try {
			obdmacaddr = obdmacaddr.substring(0,2) + obdmacaddr.substring(6);
			String sql = "SELECT COUNT(*) FROM es_carinfo WHERE obdmacaddr=?";
			int count = daoSupport.queryForInt(sql, obdmacaddr.toLowerCase());
			if(count > 0){
				isBound = true;
			}
			
			if(isBound){
				obj.put("result", 0);
				obj.put("message", "该OBD已绑定其他车辆，不能重复绑定");
			}else{
				obj.put("result", 1);
				obj.put("message", "该OBD还未绑定车辆，可以绑定");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONArray inputCarInfo(String carInfo) {
		try {
			JSONObject obj = new JSONObject(carInfo);
			String carPlate = obj.getString("carplate").toLowerCase();
			String carOwner = obj.getString("carowner");
			
			if(obj.has("car_register_time")){
				long insurenextbuytime = getInsureNextTime(obj.getLong("car_register_time"));
				obj.put("insurenextbuytime", insurenextbuytime);
			}
			if(obj.has("carmodelid") && obj.has("car_use_type") && obj.has("car_register_time") && obj.has("car_kind")){
				net.sf.json.JSONObject insureObj = getInsureEstimatedPriceAndMaxUsableGain(obj.getInt("carmodelid"), obj.getInt("car_use_type"), obj.getLong("car_register_time"), obj.getInt("car_kind"));
				obj.put("insureestimatedfee", insureObj.get("insure_estimated_fee"));
				obj.put("insureestimatedmaxgain", insureObj.get("insure_estimated_max_gain"));
				obj.put("car_age", insureObj.get("car_age"));
				obj.put("carmodelimage", insureObj.get("car_model_image"));
			}
//			if(obj.has("repair4sstoreid")){//设置初装4s店
//				net.sf.json.JSONObject originalObj = saveOriginalInfo(obj.getInt("repair4sstoreid"), carPlate);
//				if(!StringUtil.isEmpty(originalObj.getString("original_storeid"))){
//					obj.put("original_storeid", originalObj.getInt("original_storeid"));
//				}
//			}
			if(obj.has("repair4sstoreid")){//设置签约时间
				obj.put("contracttime", System.currentTimeMillis());
			}
			if(obj.has("repair4sstoreid")){//记录签约历史记录
				String repair4sstoreid = obj.getString("repair4sstoreid");
				
				String sql = "select repair4sstoreid, repairlastmile from es_carinfo where carplate=?";
				List carinfoList = daoSupport.queryForList(sql, carPlate);
				int contract_store_id = 0;
				if(carinfoList.size() > 0){
					net.sf.json.JSONObject carObj = net.sf.json.JSONObject.fromObject(carinfoList.get(0));
					contract_store_id = carObj.getInt("repair4sstoreid");
				}
				
				//如果不是第一次签约，修改会员当前车辆的签约店铺id,记录解约操作到解约记录表
				if(contract_store_id != 0){
					String productSql = "select contracttime, repair4sstoreid from es_carinfo where carplate=?";
					List list = daoSupport.queryForList(productSql, carPlate);
					net.sf.json.JSONObject tmpObj = net.sf.json.JSONObject.fromObject(list.get(0));
					String last_belong_store_id = tmpObj.getString("repair4sstoreid");
					long contracttime = tmpObj.getLong("contracttime");
					long new_contracttime = System.currentTimeMillis();
				
					if(!repair4sstoreid.equals(last_belong_store_id)){
						//记录解约操作
						productSql = "insert into es_discontract_record set contract_store_id=?, discontract_store_id=?, carplate=?, contract_time=?, discontract_time=?";
						daoSupport.execute(productSql, repair4sstoreid, last_belong_store_id, carPlate, contracttime, new_contracttime);
						
						//修改签约4s店
						productSql = "update es_carinfo set repair4sstoreid=?, last_belong_store_id=?, contracttime=? where carplate=?";
						daoSupport.execute(productSql, repair4sstoreid, last_belong_store_id, new_contracttime, carPlate);
					}
				}
			}
			if(obj.has("repairlastmile") || obj.has("repairlasttime")){
				obj.put("repairlastmile_updatetime", new Date().getTime());//设置上次保养里程更新时间，用于在上传行程时进行校验
				String repairlastmile = obj.getString("repairlastmile");
				String repairlasttime = obj.getString("repairlasttime");
				
				if(StringUtil.isNull(repairlastmile)){
					obj.put("repairlastmile", "0");
				}
				if(StringUtil.isNull(repairlasttime)){
					obj.put("repairlasttime", "0");
				}
				
				net.sf.json.JSONObject repairInfoObj = getCarRepairInfo(carPlate, obj);
				obj.put("repairestimatedfee", repairInfoObj.getDouble("repairestimatedfee"));
				obj.put("repairnexttime", repairInfoObj.getLong("repairnexttime"));
			}
			
			//添加车辆保养信息是否设置状态 1 是 0 否
			if(obj.has("repairlastmile") || obj.has("repairlasttime")){
				obj.put("has_set_repair_info", 1);
			}
			
			int carOperateFlag = 0;//车辆信息修改标识  1 添加  2 修改
			String sql;
			String productSql;
			//add check for carOwner, carOwner必须是合法的用户
			if (carPlate != null && carOwner != null){
				sql = "SELECT * FROM es_carinfo c WHERE carplate=? ORDER BY c.id";
				List list = this.daoSupport.queryForList(sql, carPlate);
				if (list.size() > 0) {
					//update
					sql = "update es_carinfo set ";
				} else {
					//add a new car
					sql = "insert into es_carinfo set ";
				}
				
				Iterator<String> keys = obj.keys();
				while(keys.hasNext()) {
					String key = keys.next();
					if(!"carplate".equals(key) && !"carowner".equals(key)){
						if("obdmacaddr".equals(key) && !obj.get(key).toString().contains(":") && obj.get(key).toString().toUpperCase().contains("JT")){
							String obdmacaddr = obj.get(key).toString().toLowerCase();
							obdmacaddr = obdmacaddr.substring(0,2) + obdmacaddr.substring(6);

							sql += key + "='" + obdmacaddr.toUpperCase() + "'";
							if (keys.hasNext())
								sql += ",";
							continue;
						}
						
						sql += key + "='" + obj.get(key).toString() + "'";
						if (keys.hasNext())
							sql += ",";
					}
				}
				if(sql.lastIndexOf(",") == sql.length()-1){ 
					sql = sql.substring(0, sql.length()-1);
				}
				if(list.size() == 0){
					sql += ", carplate='" + carPlate +"', carowner='"+ carOwner +"', create_time=" + new Date().getTime();
				}
				if(list.size() > 0){
					sql += " where carplate='" + carPlate +"'";
				}

				this.baseDaoSupport.execute(sql);
				if(sql.indexOf("insert") >= 0){//添加车辆
					carOperateFlag = 1;
				}
				if(sql.indexOf("update") >= 0){//修改车辆信息
					carOperateFlag = 2;
				}
				
				//添加保险、保养的product到数据库表es_product,用于购物下单
				int goods_id = getGoodsidOfInsureOrRepair(1);
				productSql = "select product_id from es_product where name=? and goods_id=?";
				List productidlist = daoSupport.queryForList(productSql, carPlate, goods_id);
				if(productidlist.size() == 0){
					productSql = "insert into es_product set goods_id=?, name=?";
					baseDaoSupport.execute(productSql, goods_id, carPlate);
				}
				goods_id = getGoodsidOfInsureOrRepair(2);
				productSql = "select product_id from es_product where name=? and goods_id=?";
				productidlist = daoSupport.queryForList(productSql, carPlate, goods_id);
				if(productidlist.size() == 0){
					productSql = "insert into es_product set goods_id=?, name=?";
					baseDaoSupport.execute(productSql, goods_id, carPlate);
				}
			}
			
			// 初始化油耗统计数据  
			if(carOperateFlag == 1){//添加车辆信息结束
				//封装实体
				ConsumptionStatistics consumptionStatistics = new ConsumptionStatistics();
				String nowDate = DateUtils.getNowDate();
				consumptionStatistics.setCarplate(carPlate);
				consumptionStatistics.setCurrentdate(nowDate);
				consumptionStatistics.setAvg_consumption_month(0.00);
				consumptionStatistics.setAvg_consumption_today(0.00);
				consumptionStatistics.setAvg_consumption_week(0.00);
				consumptionStatistics.setTital_consumption_month(0.00);
				consumptionStatistics.setTital_consumption_today(0.00);
				consumptionStatistics.setTital_consumption_week(0.00);
				consumptionStatistics.setTital_mileage_month(0.00);
				consumptionStatistics.setTital_mileage_today(0.00);
				consumptionStatistics.setTital_mileage_week(0.00);
				
				daoSupport.insert("es_consumption_statistics", consumptionStatistics);
			}
			
			//查询车辆列表信息返回
			sql = "SELECT t.*, IFNULL(t3.store_name, '') store_name, IFNULL(t3.after_phone, 114) after_phone FROM "
				+ "(SELECT t1.*, REPLACE(t2.brandimage, '"+ EopSetting.FILE_STORE_PREFIX +"', '"+ SystemSetting.getStatic_server_domain() +"') brandimage, t2.brand, t2.series, t2.sales_name, t2.seats, t2.price, t2.discharge FROM es_carinfo t1, es_carmodels t2 WHERE t1.`carmodelid`=t2.`id` AND t1.carowner=?) t "
				+ "LEFT JOIN es_store t3 ON t.repair4sstoreid=t3.store_id";
			
			List returnList = this.daoSupport.queryForList(sql, carOwner);

			//根据保险套餐id、4s店id获取保险套餐内容、4s店名称添加到返回的JSONObject中
			if(returnList.size() > 0){
				net.sf.json.JSONArray returnObj = JSONArray.fromObject(returnList);
				for(int i=0; i<returnObj.size(); i++){
					net.sf.json.JSONObject carObj = returnObj.getJSONObject(i);
					carObj.put("car_use_type", CarInfo.carUseTypeMap.get(carObj.getInt("car_use_type")));
					carObj.put("car_kind", CarInfo.carKindMap.get(carObj.getInt("car_kind")));
					carObj.put("carplate", carObj.getString("carplate").toUpperCase());
				}
				this.logger.debug("updateCarInfo:" + returnObj.toString());
				return returnObj;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return null;
	}
	
	private net.sf.json.JSONObject saveOriginalInfo(int repair4sstoreid, String carPlate) {
		net.sf.json.JSONObject obj = new net.sf.json.JSONObject();
		String sql = "select repair4sstoreid, obdmacaddr from es_carinfo where carplate=?";
		List carList = daoSupport.queryForList(sql, carPlate);
		if(carList.size() > 0){
			net.sf.json.JSONObject carObj = net.sf.json.JSONObject.fromObject(carList.get(0));
			int car_repair4sstoreid = carObj.getInt("repair4sstoreid");
			String obdmacaddr = carObj.getString("obdmacaddr");
			
			if(car_repair4sstoreid == 0 && !StringUtil.isEmpty(obdmacaddr)){
				sql = "insert into es_store_originalobd set original_storeid=?, obdmacaddr=?, create_time=?";
				daoSupport.execute(sql, repair4sstoreid, obdmacaddr, new Date().getTime());
				obj.put("original_storeid", repair4sstoreid);
			}
		}
		
		return obj;
	}

	/**
	 * @description 计算车辆预估保费和最高可用收益
	 * @date 2016年9月14日 下午4:32:14
	 * @param carmodelid         车型id
	 * @param car_use_type       使用性质
	 * @param car_register_time  车辆行驶证注册时间
	 * @param car_kind           车辆类别 
	 * @return
	 */
	private net.sf.json.JSONObject getInsureEstimatedPriceAndMaxUsableGain(int carmodelid, int car_use_type, long car_register_time, int car_kind) {
		net.sf.json.JSONObject obj = new net.sf.json.JSONObject();
		try {
			String sql = "SELECT seats, discharge, price, modelimage FROM es_carmodels WHERE id=?";
			List carmodelList = daoSupport.queryForList(sql, carmodelid);
			
			if(carmodelList.size() > 0){
				DecimalFormat formatter = new DecimalFormat("0.00");
				
				net.sf.json.JSONObject carObj = net.sf.json.JSONObject.fromObject(carmodelList.get(0));
				int seats = carObj.getInt("seats");
				double car_age = Math.floor((double)(new Date().getTime() - car_register_time) / (12 * 30 * 24 * 60 * 60 * 1000l));
				double car_price = carObj.getDouble("price") * 10000;
				String car_discharge = carObj.getString("discharge");
				String car_model_image = carObj.getString("modelimage");
				
				//计算车辆折旧价格
				car_price = InsureType.getDepreciationCarPrice(formatter, car_use_type, car_kind, car_age, car_price);
				
				//计算预估保费和最高可用收益
				double insure_price = 0.0;
				//车辆损失险
				double damage_insure_price = InsureType.getDamageInsurePrice(formatter, car_use_type, seats, car_age, car_price);
				insure_price += damage_insure_price;
				//机动车盗抢险
				double theft_insure_price = InsureType.getTheftInsurePrice(formatter, car_use_type, seats, car_price);
				insure_price += theft_insure_price;
				//第三者责任险
				double thirdparty_insure_price = InsureType.getThirdpartyInsurePrice(formatter, car_use_type, seats, InsureType.thirdpartyInsureCoverageArray.length - 1);
				insure_price += thirdparty_insure_price;
				//玻璃破碎险
				double glass_breakage_insure_price = InsureType.getGlassBreakageInsurePrice(formatter, car_use_type, seats, InsureType.glassTypeArray.length - 1, car_price);
				insure_price += glass_breakage_insure_price;
				//司机责任险
				double driver_insure_price = InsureType.getDriverInsurePrice(formatter, car_use_type, seats, InsureType.driverInsureCoverageArray.length - 1);
				insure_price += driver_insure_price;
				//乘客责任险
				double passenger_insure_price = InsureType.getPassengerInsurePrice(formatter, car_use_type, seats, InsureType.passengerInsureCoverageArray.length - 1);
				insure_price += passenger_insure_price;
				//自燃损失险
				double self_ignite_insure_price = InsureType.getself_ignite_insure_price(formatter, car_age, car_price);
				insure_price += self_ignite_insure_price;
				//车身划痕险
				double scratch_insure_price = InsureType.getScratchInsurePrice(formatter, car_age, InsureType.scratchInsureCoverageArray.length - 1, car_price);
				insure_price += scratch_insure_price;
				//涉水险
				double wade_insure_price = 0.0;
				insure_price += wade_insure_price;
				//不计免赔险
				double exempt_insure_price = Double.valueOf(formatter.format((damage_insure_price + scratch_insure_price + thirdparty_insure_price + driver_insure_price + passenger_insure_price) * 0.15 + theft_insure_price * 0.2));
				insure_price += exempt_insure_price;
				//交强险
				double compulsory_insure_price = InsureType.getCompulsoryInsurePrice(formatter, car_use_type, seats);
				insure_price += compulsory_insure_price;
				//车船税
				double travel_tax = InsureType.getTravelTax(formatter, car_discharge);
				insure_price += travel_tax;
				
				insure_price = Double.valueOf(formatter.format(insure_price));
				double insure_estimated_max_gain = Double.valueOf(formatter.format(insure_price * 0.15));
				
				obj.put("car_age", car_age);
				obj.put("insure_estimated_fee", insure_price);
				obj.put("insure_estimated_max_gain", insure_estimated_max_gain);
				obj.put("car_model_image", car_model_image);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	public long getCarRepairCoin(Integer service_store_id, String carplate) {
		try {
			String sql = "SELECT repair_total_coin FROM es_car_repair_coin WHERE store_id=? AND carplate=?";
			List coinList = daoSupport.queryForList(sql, service_store_id, carplate);
			long repair_total_coin = 0;
			if(coinList.size() > 0){
				repair_total_coin = net.sf.json.JSONObject.fromObject(coinList.get(0)).getLong("repair_total_coin");
			}
			
			return repair_total_coin;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public CarInfoVin queryCarInfoVin(String vin) {
		try {
			String sql = "select * from es_carinfo_vin t where 1=1 and t.vin = '" + vin + "'";
			List<CarInfoVin> carInfoVinList = daoSupport.queryForList(sql, CarInfoVin.class);
			if(ValidateUtils.isNotEmpty(carInfoVinList)) {
				return carInfoVinList.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public int saveCarInfoVin(CarInfoVin carInfoVin) {
		try {
			daoSupport.insert("es_carinfo_vin", carInfoVin);
			CarModel carmodel = getCarmodelInfo(carInfoVin);
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * @description 获取vin解析的车型数据，匹配数据库，如果能匹配到，更新车型表数据，再返回更新后的车型信息
	 * @date 2016年9月24日 上午11:39:36
	 * @param carInfoVin
	 * @return
	 */
	public CarModel getCarmodelInfo(CarInfoVin carInfoVin){
		try {
			String sql = "SELECT id, brand_id, brand, series, nk, sales_name, seats, discharge, gearboxtype FROM es_carmodels WHERE brand=? AND series=? AND sales_name=?";
			List list = daoSupport.queryForList(sql, carInfoVin.getPp(), carInfoVin.getCxi(), carInfoVin.getXsmc());
			if(list.size() > 0){//查询成功，返回车型对象
				net.sf.json.JSONObject carmodelObj = net.sf.json.JSONObject.fromObject(list.get(0));
				int carmodel_id = carmodelObj.getInt("id");
				//1 手动  2 自动
				int gearboxtype = 0;
				if("手动".equals(carInfoVin.getBsqlx())){
					gearboxtype = 1;
				}else{
					gearboxtype = 2;
				}
				//将vin解析到的车型数据更新车型表
				sql = "UPDATE es_carmodels SET model=?, seats=?, discharge=?, gearboxtype=?, price=? WHERE id=?";
				daoSupport.execute(sql, carInfoVin.getCx(), carInfoVin.getZws(), carInfoVin.getPl(), gearboxtype, carInfoVin.getZdjg(), carmodel_id);
				//返回车型对象
				sql = "SELECT * from es_carmodels WHERE id=?";
				CarModel carmodel = (CarModel) daoSupport.queryForObject(sql, CarModel.class, carmodel_id);
				return carmodel;
			}
			//返回车型对象
			sql =                                        
				" SELECT  t1.*                           "+
				" FROM                                   "+
				" 	es_carmodels t1,                     "+
				" 	es_carinfo_vin t2                    "+
				" WHERE                                  "+
				" 	1 = 1                                "+
				" AND t1.id = t2.carmodel_id             "+
				" AND t2.vin = '"+carInfoVin.getVin() +"'      ";
			CarModel carmodel = (CarModel) daoSupport.queryForObject(sql, CarModel.class);
			return carmodel;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public net.sf.json.JSONObject getCarHodometerIncomeHistory(String carplate, int startIndex, String pageSize) {
		net.sf.json.JSONObject obj = new net.sf.json.JSONObject();
		try {
			String sql = "SELECT id, TYPE, query_id, carplate, reward, FROM_UNIXTIME(timeline/1000, '%Y-%m-%d %H:%i') timeline, detail FROM es_car_gain_history WHERE carplate=? ORDER BY timeline LIMIT "+ startIndex +","+ pageSize;
			List incomeList = daoSupport.queryForList(sql, carplate);

			JSONArray resultObj = new JSONArray();
			if(incomeList.size() > 0){
				JSONArray incomeArray = JSONArray.fromObject(incomeList);
				DecimalFormat format = new DecimalFormat("0.00");
				for(int i=0; i<incomeArray.size(); i++){
					net.sf.json.JSONObject incomeObj = incomeArray.getJSONObject(i);
					int type = incomeObj.getInt("type");
					double reward = incomeObj.getDouble("reward");
					String rewards = "";
					if(type == CarInfo.DRIVING_INCOME && reward > 0){
						rewards = "+"+format.format(reward);
						incomeObj.put("reward", rewards);
					}else if(type == CarInfo.ORDER_INCOME_USE){
						rewards = "-"+format.format(reward);
						incomeObj.put("reward", rewards);
					}
					resultObj.add(incomeObj);
				}
			}
			
			obj.put("data", resultObj);
			obj.put("result", 1);
			obj.put("message", "获取收益历史记录成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	public net.sf.json.JSONObject getCarSignStoreList(String carplate) {
		net.sf.json.JSONObject obj = new net.sf.json.JSONObject();
		try {
			String sql = "SELECT t2.store_id, t2.store_name FROM es_carinfo t1, es_store t2 WHERE t1.repair4sstoreid=t2.store_id AND t1.carplate=?";
			List signStoreList = daoSupport.queryForList(sql, carplate);
			if(signStoreList.size() > 0){
				net.sf.json.JSONObject signStoreObj = net.sf.json.JSONObject.fromObject(signStoreList.get(0));
				int repair4sstoreid = signStoreObj.getInt("store_id");
				
				sql = "SELECT t1.store_id, t1.store_name FROM es_store t1, (SELECT DISTINCT(store_id) store_id FROM es_car_repair_coin_history WHERE carplate=? AND store_id != ?) t2 WHERE t1.store_id=t2.store_id";
				List list = daoSupport.queryForList(sql, carplate, repair4sstoreid);
				JSONArray resultArray = new JSONArray();
				resultArray.add(signStoreObj);
				if(list.size() > 0){
					resultArray.addAll(list);
				}
				
				obj.put("data", resultArray);
				obj.put("result", 1);
				obj.put("message", "查询店铺签约历史列表成功");
			}else{
				obj.put("result", 0);
				obj.put("message", "您还没有签约店铺，请先签约");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		
		return obj;
	}

	@Override
	public net.sf.json.JSONObject getCarRepairCoinHistory(String carplate, String store_id, int startIndex, String pageSize) {
		net.sf.json.JSONObject obj = new net.sf.json.JSONObject();
		try {
			net.sf.json.JSONObject resultObj = new net.sf.json.JSONObject();
			
			//查询签约店铺信息
			String sql = "SELECT store_name FROM es_store WHERE store_id=?";
			List storeList = daoSupport.queryForList(sql, store_id);
			if(storeList.size() > 0){
				net.sf.json.JSONObject storeObj = net.sf.json.JSONObject.fromObject(storeList.get(0));
				String store_name = storeObj.getString("store_name");

				//查询车辆在签约店铺的保养币余额
				sql = "SELECT repair_total_coin FROM es_car_repair_coin WHERE store_id=? AND carplate=?";
				List coinList = daoSupport.queryForList(sql, store_id, carplate);
				Long repair_total_coin = 0l;
				if(coinList.size() > 0){
					repair_total_coin = net.sf.json.JSONObject.fromObject(coinList.get(0)).getLong("repair_total_coin");
				}
		
				//查询车辆在签约店铺的保养币使用记录
				sql = "SELECT id, TYPE, query_id , carplate, reward, FROM_UNIXTIME(timeline/1000, '%Y-%m-%d %H:%i') timeline, detail FROM es_car_repair_coin_history WHERE store_id=? AND carplate=? ORDER BY timeline LIMIT "+ startIndex +","+ pageSize;
				List incomeList = daoSupport.queryForList(sql, store_id, carplate);
				JSONArray resultArray = new JSONArray();
				if(incomeList.size() > 0){
					JSONArray incomeArray = JSONArray.fromObject(incomeList);
					for(int i=0; i<incomeArray.size(); i++){
						net.sf.json.JSONObject incomeObj = incomeArray.getJSONObject(i);
						int type = incomeObj.getInt("type");
						double reward = incomeObj.getDouble("reward");
						String rewards = "";
						if(type == CarInfo.INSURE_PAY_REPAIR_COIN_GET && reward > 0){
							rewards = "+"+reward;
							incomeObj.put("reward", rewards);
						}else if(type == CarInfo.ORDER_REPAIR_COIN_USE){
							rewards = "-"+reward;
							incomeObj.put("reward", rewards);
						}
						resultArray.add(incomeObj);
					}
				}
				
				resultObj.put("store_name", store_name);
				resultObj.put("repair_total_coin", repair_total_coin);
				resultObj.put("repair_coin_history_list", resultArray);
				
				obj.put("data", resultObj);
				obj.put("result", 1);
				obj.put("message", "获取收益历史记录成功");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		
		return obj;
	}

	@Override
	public void updateCarInfoVin(int carInfoVinId, int carModelId) {
		String sql = 
				" UPDATE es_carinfo_vin t            				"+
				" SET t.carmodel_id = '"+carModelId+"'              "+
				" WHERE 1=1                     				    "+
				" AND t.id = '"+carInfoVinId+"'          	        "; 
		daoSupport.execute(sql);	
	}

	@Override
	public TrafficRestriction getTrafficRestriction(String city, String limit_date) {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("select *from es_traffic_restriction where city = ? and limit_date = ?");
			return (TrafficRestriction)daoSupport.queryForObject(sql.toString(), TrafficRestriction.class, city,limit_date);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void addTrafficRestriction(TrafficRestriction trafficRestriction) {
		try{
			StringBuffer sql = new StringBuffer();
			daoSupport.insert("es_traffic_restriction", trafficRestriction);
		}catch(Exception e){
			throw e;
		}
	}

	@Override
	public int getDeviceIsBanding(String device_id) {
		try{
			String sql = "select count(*) from es_carinfo where  obdmacadr= ?";
			return daoSupport.queryForInt(sql, device_id);
		}catch(Exception e){
			throw e;
		}
	}

	@Override
	public net.sf.json.JSONObject getCarPositionInfo(String carplate) throws Exception {
		net.sf.json.JSONObject obj = new net.sf.json.JSONObject();
		try {
			String sql = "SELECT t1.obdmacaddr, t2.brand, t2.series, REPLACE(t2.brandimage, ?, ?) brandimage FROM es_carinfo t1, es_carmodels t2 WHERE t1.carmodelid=t2.id AND t1.carplate=?";
			List carinfoList = daoSupport.queryForList(sql, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_domain(), carplate);
			if(carinfoList.size() > 0){
				String msg = "";
				
				net.sf.json.JSONObject carObj = net.sf.json.JSONObject.fromObject(carinfoList.get(0));
				String deviceId = carObj.getString("obdmacaddr");
				String gpsInfo = carGpsMap.get(deviceId.toUpperCase());
				if(StringUtil.isNull(gpsInfo)){//第一次获取或服务器重启，map值为空时，查询数据库获取最后一次的车辆位置
					sql = "SELECT gpsinfo WHERE carplate='"+ carplate +"'";
					gpsInfo = daoSupport.queryForString(sql);
					if(StringUtil.isNull(gpsInfo)){
						msg = "该车辆暂无gps定位信息";
					}
				}
//				String gpsInfo = "{'gps_time':'1476235895000','latitude':'121.127313628','longitude':'121.435932'}";
				if(!StringUtil.isNull(msg)){
					obj.put("result", 0);
					obj.put("message", msg);
				}else{
					net.sf.json.JSONObject dataObj = new net.sf.json.JSONObject();
					dataObj.put("brand", carObj.getString("brand"));
					dataObj.put("series", carObj.getString("series"));
					dataObj.put("brandimage", carObj.getString("brandimage"));
					dataObj.put("gpsinfo", gpsInfo);
					
					obj.put("result", 1);
					obj.put("data", dataObj.toString());
					obj.put("message", "查询成功");
				}
			}else{
				obj.put("result", 0);
				obj.put("message", "该车辆不存在，请检查");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}
	
	/**
	 * @description 更新车辆位置信息
	 * @date 2016年10月14日 下午4:06:19
	 * @param deviceId  车辆obd地址
	 * @param gpsInfo   gps位置信息
	 */
	public void updateCarPositionInfo(String deviceId, String gpsInfo){
		try {
			JSONObject gpsObj = new JSONObject(gpsInfo);
			gpsObj.put("gps_time", DateUtil.toString(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
			carGpsMap.put(deviceId, gpsObj.toString());
			
			//定时推送位置信息给app
			if(!"".equals(deviceId)){
				String sql = "SELECT carplate FROM es_carinfo WHERE obdmacaddr='"+ deviceId.toLowerCase() +"'";
				String carplate = daoSupport.queryForString(sql);
				if(!"".equals(carplate)){//车牌号不为空，开始推送
					sql = "SELECT t2.brand, t2.series, REPLACE(t2.brandimage, ?, ?) brandimage FROM es_carinfo t1, es_carmodels t2 "
						+ "WHERE t1.carmodelid=t2.id "
						+ "AND t1.carplate=?";
					List carList = daoSupport.queryForList(sql, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_domain(), carplate);
					if(carList.size() > 0){
						net.sf.json.JSONObject carObj = net.sf.json.JSONObject.fromObject(carList.get(0));
						net.sf.json.JSONObject dataObj = new net.sf.json.JSONObject();
						dataObj.put("brand", carObj.getString("brand"));
						dataObj.put("series", carObj.getString("series"));
						dataObj.put("brandimage", carObj.getString("brandimage"));
						dataObj.put("gpsinfo", gpsObj.toString());
						
						Long pushTime = carPushTimeMap.get("pushTime");

						if(pushTime == null){//第一次推送或服务器重启
							carPushTimeMap.put("pushTime", System.currentTimeMillis());
							JPushUtil.SendPushToAllTagAndMsgAndExtra(carplate.toUpperCase(), dataObj.toString(), "", "");
						}else{
							Calendar c = Calendar.getInstance();
							c.setTimeInMillis(pushTime);
							if((new Date().getTime() - c.getTime().getTime()) > SystemSetting.car_position_push_interval){
								carPushTimeMap.put("pushTime", System.currentTimeMillis());
								JPushUtil.SendPushToAllTagAndMsgAndExtra(carplate.toUpperCase(), dataObj.toString(), "", "");
							}
						}
						
						//记录车辆最后的位置信息
						sql = "SELECT COUNT(*) FROM es_car_position_history WHERE carplate='"+ carplate +"'";
						int count = daoSupport.queryForInt(sql);
						if(count > 0){
							sql = "UPDATE es_car_position_history SET gpsinfo=? WHERE carplate=?";
						}else {
							sql = "INSERT INTO es_car_position_history SET gpsinfo=?, carplate=?";
						}
						daoSupport.execute(sql, dataObj.toString(), carplate);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void updateCarErrorCodeInfo(String deviceId, String errorCodeString) {
		carErrorCodeMap.put(deviceId,errorCodeString);
	}

	@Override
	public net.sf.json.JSONObject getCarErrorCodeInfo(String carplate) {
		net.sf.json.JSONObject obj = new net.sf.json.JSONObject();
		try {
			String sql = "SELECT t1.obdmacaddr, t2.brand, t2.series, REPLACE(t2.brandimage, ?, ?) brandimage FROM es_carinfo t1, es_carmodels t2 WHERE t1.carmodelid=t2.id AND t1.carplate=?";
			List carinfoList = daoSupport.queryForList(sql, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_domain(), carplate);
			if(carinfoList.size() > 0){
				String msg = "";
				
				net.sf.json.JSONObject carObj = net.sf.json.JSONObject.fromObject(carinfoList.get(0));
				String deviceId = carObj.getString("obdmacaddr");
				String carErrorCode = carErrorCodeMap.get(deviceId.toUpperCase());
				if(StringUtil.isNull(carErrorCode)){
						msg = "该车辆暂无错误信息";
				}else{
					net.sf.json.JSONObject dataObj = new net.sf.json.JSONObject();
					dataObj.put("brand", carObj.getString("brand"));
					dataObj.put("series", carObj.getString("series"));
					dataObj.put("brandimage", carObj.getString("brandimage"));
					dataObj.put("carErrorCode", carErrorCode);
					
					obj.put("result", 1);
					obj.put("data", dataObj.toString());
					obj.put("message", "查询成功");
				}
			}else{
				obj.put("result", 0);
				obj.put("message", "该车辆不存在，请检查");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return obj;
	}
}