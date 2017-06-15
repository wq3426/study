package com.enation.app.shop.core.service.impl;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.InsureCompanyModel;
import com.enation.app.shop.core.model.InsureTypeModel;
import com.enation.app.shop.core.service.InsureType;
import com.enation.app.shop.core.model.InsuranceModel;
import com.enation.app.shop.core.service.IInsuranceManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.Page;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 保险信息管理
 * @author wangqiang 2016年4月1日 下午7:48:51
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class InsuranceManager extends BaseSupport implements IInsuranceManager {

	@Override
	public List<InsuranceModel> getInsureInfoList(String company) {
		String sql = "SELECT * FROM es_insurances c where c.company=? ORDER BY id";
		List<InsuranceModel> list = this.daoSupport.queryForList(sql, company);
		this.logger.debug("insurancesList:" + JSONArray.fromObject(list).toString());
		return list;
	}
	
	@Override
	public List getInsureCompanyList() {
		String sql = "SELECT DISTINCT(company), companyimage FROM es_insurances";
		List list = this.daoSupport.queryForList(sql);
		this.logger.debug("getInsureCompanyList:" + JSONArray.fromObject(list).toString());
		return list;
	}

	@Override
	public InsuranceModel getInsureById(int insuranceId) {
			String sql = "select *from es_insurances where id = ?";
			InsuranceModel insuranceModel = (InsuranceModel)this.daoSupport.queryForObject(sql, InsuranceModel.class, insuranceId);
			this.logger.debug("insuranceModel:" + JSONArray.fromObject(insuranceModel).toString());
			return insuranceModel;
	}

	

	@Override
	public List<InsuranceModel> getInsureListByIds(String[] insuranceIds) {
		StringBuffer sqlbuffer = new StringBuffer();
		sqlbuffer.append("select *from es_insurances where id in (");
		for(int i = 0 ; i < insuranceIds.length ; i ++){
			if( i == insuranceIds.length-1){
				sqlbuffer.append("?)");
				break;
			}
			sqlbuffer.append("?,");
		}
		List<InsuranceModel> insuranceModelList = this.baseDaoSupport.queryForList(sqlbuffer.toString(), InsuranceModel.class, insuranceIds.toString());
		this.logger.debug("List<InsuranceModel> :" + JSONArray.fromObject(insuranceModelList).toString());
		return insuranceModelList;
	}

	@Override
	public Page getInsureCompanyListByPage(Map keyMap, int page, int pageSize, String sort, String order) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT id, company_name, telephone, sort, FROM_UNIXTIME(create_time/1000, '%Y-%m-%d %H:%i:%s') create_time, FROM_UNIXTIME(update_time/1000, '%Y-%m-%d %H:%i:%s') update_time FROM es_insurance_company_info WHERE 1=1");
		
		String keyword = (String) keyMap.get("keyword");
		
		if(keyword!=null){			
			sql.append(" and (company_name like '%"+keyword+"%'");
			sql.append(" or telephone like '%"+keyword+"%')");
		}
		
		sql.append(" order by "+sort+" "+order);

		Page webPage = this.baseDaoSupport.queryForPage(sql.toString(), page, pageSize);
		
		return webPage;
	}

	@Override
	public boolean isExistInsureCompany(InsureCompanyModel insureCompany) {
		try {
			String sql = "select count(*) from es_insurance_company_info where company_name=?";
			int count = daoSupport.queryForInt(sql, insureCompany.getCompany_name());
			if(count > 0){
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject addInsureCompany(InsureCompanyModel insureCompany, File data, String dataFileName) throws Exception {
		JSONObject obj = new JSONObject();
		try {
			String imagepath = UploadUtil.uploadFile(data, dataFileName, SystemSetting.insure_company_image_savedir);
			insureCompany.setCreate_time(System.currentTimeMillis());
			insureCompany.setUpdate_time(System.currentTimeMillis());
			insureCompany.setLogo(imagepath);
			
			daoSupport.insert("es_insurance_company_info", insureCompany);
			
			obj.put("result", 1);
			obj.put("message", "添加成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	public InsureCompanyModel getInsureCompany(Integer id) {
		try {
			String sql = "select * from es_insurance_company_info where id=?";
			return (InsureCompanyModel) daoSupport.queryForObject(sql, InsureCompanyModel.class, id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject editInsureCompany(InsureCompanyModel insureCompany, File data, String dataFileName) throws Exception {
		JSONObject obj = new JSONObject();
		String new_modelImagePath = "";
		String old_modelImagePath = "";
		try {
			if(data != null){
				old_modelImagePath = SystemSetting.getFile_path() + insureCompany.getLogo().replace(EopSetting.FILE_STORE_PREFIX+"/files", "");
				new_modelImagePath = UploadUtil.uploadFile(data, dataFileName, SystemSetting.insure_company_image_savedir);
				insureCompany.setLogo(new_modelImagePath);//设置上传的新图片路径
				new_modelImagePath = SystemSetting.getFile_path() + new_modelImagePath.replace(EopSetting.FILE_STORE_PREFIX+"/files", "");
			}
			daoSupport.update("es_insurance_company_info", insureCompany, "id="+insureCompany.getId());
			
			FileUtil.delete(old_modelImagePath);//修改成功，删除旧的图片
			
			obj.put("result", 1);
			obj.put("message", "修改成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			FileUtil.delete(new_modelImagePath);//修改失败，删除新上传的图片
			throw e;
		}
		return obj;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject deleteInsureCompany(Integer[] id) throws Exception {
		JSONObject obj = new JSONObject();
		try {
			String ids = StringUtil.arrayToString(id, ",");
			String sql = "select logo from es_insurance_company_info where id in ("+ ids +")";
			List imageList = daoSupport.queryForList(sql);
			
			sql = "delete from es_insurance_company_info where id in ("+ ids +")";
			daoSupport.execute(sql);
			
			//删除记录成功，删除车型图片
			if(imageList.size() > 0){
				JSONArray fsArray = JSONArray.fromObject(imageList);
				for(int i=0; i<fsArray.size(); i++){
					JSONObject tmp = fsArray.getJSONObject(i);
					String fsPath = tmp.getString("logo");
					String imagePath = SystemSetting.getFile_path() + fsPath.replace(EopSetting.FILE_STORE_PREFIX+"/files", "");
					FileUtil.delete(imagePath);
				}
			}
			
			obj.put("result", 1);
			obj.put("message", "删除记录成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	public Page getInsureTypeListByPage(Map keyMap, int page, int pageSize, String sort, String order) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t1.id, t1.`company_id` ,t2.`company_name`, t1.`insure_name`, t1.`insure_type`, t1.`sort`, ");
		sql.append("FROM_UNIXTIME(t1.create_time/1000, '%Y-%m-%d %H:%i:%s') create_time, FROM_UNIXTIME(t1.update_time/1000, '%Y-%m-%d %H:%i:%s') update_time ");
		sql.append("FROM es_insurance_types_info t1, es_insurance_company_info t2 WHERE t1.`company_id`=t2.`id`");
		String keyword = (String) keyMap.get("keyword");
		
		if(keyword!=null){			
			sql.append(" and (t2.company_name like '%"+keyword+"%' or t1.insure_name like '%"+keyword+"%')");
		}
		
		sql.append(" order by t1.company_id, t1."+sort+" "+order);

		Page webPage = this.baseDaoSupport.queryForPage(sql.toString(), page, pageSize);
		
		return webPage;
	}

	@Override
	public List<InsureCompanyModel> getInsureCompanySelectList() {
		String sql = "SELECT id, company_name FROM es_insurance_company_info ORDER BY sort";
		return daoSupport.queryForList(sql, InsureCompanyModel.class);
	}

	@Override
	public boolean isExistInsureType(InsureTypeModel insureType) {
		try {
			String sql = "select count(*) from es_insurance_types_info where insure_type=? and company_id=?";
			int count = daoSupport.queryForInt(sql, insureType.getInsure_type(), insureType.getCompany_id());
			if(count > 0){
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject addInsureType(InsureTypeModel insureType) {
		JSONObject obj = new JSONObject();
		try {
			insureType.setCreate_time(System.currentTimeMillis());
			insureType.setUpdate_time(System.currentTimeMillis());
			
			daoSupport.insert("es_insurance_types_info", insureType);
			
			obj.put("result", 1);
			obj.put("message", "添加成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	public InsureTypeModel getInsureType(Integer id) {
		try {
			String sql = "select * from es_insurance_types_info where id=?";
			return (InsureTypeModel) daoSupport.queryForObject(sql, InsureTypeModel.class, id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject editInsureType(InsureTypeModel insureType) {
		JSONObject obj = new JSONObject();
		try {
			daoSupport.update("es_insurance_types_info", insureType, "id="+insureType.getId());
			
			obj.put("result", 1);
			obj.put("message", "修改成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject deleteInsureType(Integer[] id) throws Exception {
		JSONObject obj = new JSONObject();
		try {
			String ids = StringUtil.arrayToString(id, ",");
			
			String sql = "delete from es_insurance_types_info where id in ("+ ids +")";
			daoSupport.execute(sql);
			
			obj.put("result", 1);
			obj.put("message", "删除记录成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	public JSONObject getInsureCompanies(String carplate) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "SELECT t1.`store_id`, t1.`insure_company_ids`, t2.`store_name` FROM es_store_insurance t1, es_store t2 WHERE t1.`store_id`=t2.`store_id` "
					   + "AND (t1.`store_id`=(SELECT repair4sstoreid FROM es_carinfo WHERE carplate=?)) ORDER BY t1.store_id DESC";

			List list = daoSupport.queryForList(sql, carplate);
			if(list.size() > 0){
				JSONArray returnArray = new JSONArray();
				
				JSONArray insureStoreArray = JSONArray.fromObject(list);
				for(int i=0; i<insureStoreArray.size(); i++){
					JSONObject returnObj = new JSONObject();
					
					JSONObject insureStoreObj = insureStoreArray.getJSONObject(i);
					String insure_company_ids = insureStoreObj.getString("insure_company_ids");
					int store_id = insureStoreObj.getInt("store_id");
					
					sql = "SELECT "+ store_id +" store_id, id, company_name, REPLACE(logo,?,?) logo FROM es_insurance_company_info WHERE id IN ("+ insure_company_ids +") ORDER BY sort";
					List insureCompanyList = daoSupport.queryForList(sql, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_domain());
					
					returnObj.put("store_id", insureStoreObj.getInt("store_id"));
					returnObj.put("store_name", insureStoreObj.getString("store_name"));
					returnObj.put("insure_company_list", JSONArray.fromObject(insureCompanyList));
					
					returnArray.add(returnObj);
				}
				obj.put("data", returnArray);
				obj.put("result", 1);
				obj.put("message", "获取数据成功");
			}else{
				obj.put("data", "");
				obj.put("result", 0);
				obj.put("message", "没有获取到记录");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	public JSONObject getInsuresInfoOfStoreInsureCompany(DecimalFormat df, String store_id, String insure_company_id, Map<Integer, JSONObject> insureTypePriceMap, String carplate) {
		JSONObject obj = new JSONObject();
		try {
			if(insureTypePriceMap != null){
				//查询车辆购买保险预计可获得的保养币
				String sql = "SELECT insureestimatedfee FROM es_carinfo WHERE carplate='"+ carplate +"'";
				String insureestimatedfee = daoSupport.queryForString(sql);
				
				//查询店铺合作保险公司支持的险种价格返回页面
				JSONArray businessInsureArray = new JSONArray();
				JSONArray forceInsureArray = new JSONArray();

				sql = "SELECT insure_type FROM es_insurance_types_info WHERE company_id=? ORDER BY sort";
				List insureTypeList = daoSupport.queryForList(sql, insure_company_id);
				if(insureTypeList.size() > 0){
					JSONArray insureTypeArray = JSONArray.fromObject(insureTypeList);
					for(int i=0; i<insureTypeArray.size(); i++){
						int insureType = insureTypeArray.getJSONObject(i).getInt("insure_type");
						
						if(insureType == InsureType.compulsory_insure || insureType == InsureType.travel_tax){//添加交强险和车船税等强制险种
							forceInsureArray.add(insureTypePriceMap.get(insureType));
						}else {
							businessInsureArray.add(insureTypePriceMap.get(insureType));//添加商业险种
						}
					}
				}

				//店铺可用收益百分比系数
				double insure_income_discount = 0.15;
				
				long estimated_repair_coin = (long) Math.floor(Double.valueOf(insureestimatedfee) * 0.15);

				JSONObject dataObj = new JSONObject();
				dataObj.put("estimated_repair_coin", estimated_repair_coin);
				dataObj.put("business_insure_list", businessInsureArray);
				dataObj.put("force_insure_list", forceInsureArray);
				dataObj.put("insure_income_discount", insure_income_discount);

				obj.put("data", dataObj);
				obj.put("result", 1);
				obj.put("message", "获取车辆险种信息成功");
			}else{
				obj.put("result", 0);
				obj.put("message", "根据车牌号没有查找到车辆记录");
				obj.put("data", "");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		
		return obj;
	}

	/**
	 * @description 根据车牌获取车型，计算保险费用
	 * @date 2016年9月11日 下午6:04:53
	 * @param carplate                 车牌号
	 * @param df                       金额格式化formatter
	 * @param glass_type               玻璃破碎险-玻璃类型
	 * @param thirdparty_coverage_id   第三者责任险-保额
	 * @param driver_coverage_id       车上人员-司机责任险-保额
	 * @param passenger_coverage_id    车上人员-乘客责任险-保额
	 * @param scratch_coverage_id      车身划痕险-保额
	 * @param exempt_insure_typeids    不计免赔险-选择的险种id集合
	 * @return
	 */
	public Map<Integer, JSONObject> getCarInsureTypePriceMap(String carplate, DecimalFormat df, String glass_type, String thirdparty_coverage_id, String driver_coverage_id, 
                                                             String passenger_coverage_id, String scratch_coverage_id, String exempt_insure_typeids) {
		Map<Integer, JSONObject> insureTypePriceMap = null;
		try {
			String sql = "SELECT t1.car_use_type, t1.car_kind, t1.car_age, t2.seats, t2.discharge, t2.price FROM es_carinfo t1, es_carmodels t2 WHERE t1.`carmodelid`=t2.`id` AND t1.carplate=?";
			List carList = daoSupport.queryForList(sql, carplate);
			if(carList.size() > 0){
				JSONObject carObj = JSONObject.fromObject(carList.get(0));
				int car_use_type = carObj.getInt("car_use_type");
				int car_kind = carObj.getInt("car_kind");
				double car_age = carObj.getDouble("car_age");
				int seats = carObj.getInt("seats");
				double car_price = carObj.getDouble("price") * 10000;
				String car_discharge = carObj.getString("discharge");
				
				/**
				 * 计算折旧后车辆价格
				 */
				car_price = InsureType.getDepreciationCarPrice(df, car_use_type, car_kind, car_age, car_price);
				
				/**
				 * 计算保险公司各险种预估费用
				 */
				Map<Integer, String> insuresMap = InsureType.getInsureTypeMap();
         
				//车辆损失险-保费计算
				JSONObject damage_insure_obj = new JSONObject();
				double damage_insure_price = InsureType.getDamageInsurePrice(df, car_use_type, seats, car_age, car_price);
				damage_insure_obj.put("insure_id", InsureType.damage_insure);
				damage_insure_obj.put("insure_name", insuresMap.get(InsureType.damage_insure));
				damage_insure_obj.put("insure_param_list", new JSONArray());
				damage_insure_obj.put("insure_price", damage_insure_price);
				damage_insure_obj.put("exempt_insure_price", df.format(damage_insure_price * 0.15));
				damage_insure_obj.put("insure_describe", "用于赔付车辆发生的保险事故损失");
				
				//机动车盗抢险-保费计算
				JSONObject theft_insure_obj = new JSONObject();
				double theft_insure_price = InsureType.getTheftInsurePrice(df, car_use_type, seats, car_price);
				theft_insure_obj.put("insure_id", InsureType.theft_insure);
				theft_insure_obj.put("insure_name", insuresMap.get(InsureType.theft_insure));
				theft_insure_obj.put("insure_param_list", new JSONArray());
				theft_insure_obj.put("insure_price", theft_insure_price);
				theft_insure_obj.put("exempt_insure_price", df.format(theft_insure_price * 0.2));
				theft_insure_obj.put("insure_describe", "用于赔付车辆被盗/抢发生的损失");
				
				//第三者责任险-保费计算
				JSONObject thirdparty_insure_obj = new JSONObject();
				double thirdparty_insure_price = 0.0;
				JSONArray thirdparty_param_array = InsureType.getInsureParamArray(InsureType.thirdparty_insure);
				for(int i=0; i<thirdparty_param_array.size(); i++){
					int type_id = thirdparty_param_array.getJSONObject(i).getInt("type_id");
					double type_price = InsureType.getThirdpartyInsurePrice(df, car_use_type, seats, type_id);
					thirdparty_param_array.getJSONObject(i).put("type_price", type_price);
					thirdparty_param_array.getJSONObject(i).put("exempt_insure_price", df.format(type_price * 0.15));
				}
				if(!StringUtil.isEmpty(thirdparty_coverage_id)){
					thirdparty_insure_price = InsureType.getThirdpartyInsurePrice(df, car_use_type, seats, Integer.valueOf(thirdparty_coverage_id));
					thirdparty_insure_obj.put("thirdparty_coverage_id", thirdparty_coverage_id);
				}
				thirdparty_insure_obj.put("insure_id", InsureType.thirdparty_insure);
				thirdparty_insure_obj.put("insure_name", insuresMap.get(InsureType.thirdparty_insure));
				thirdparty_insure_obj.put("insure_param_list", thirdparty_param_array);
				thirdparty_insure_obj.put("insure_price", thirdparty_insure_price);
				thirdparty_insure_obj.put("insure_describe", "用于赔付第三者造成的人身伤亡或财产的直接损失");
				
				//玻璃破碎险-保费计算
				JSONObject glass_breakage_insure_obj = new JSONObject();
				double glass_breakage_insure_price = 0.0;
				JSONArray glass_breakage_param_array = InsureType.getInsureParamArray(InsureType.glass_breakage_insure);
				for(int i=0; i<glass_breakage_param_array.size(); i++){
					int type_id = glass_breakage_param_array.getJSONObject(i).getInt("type_id");
					double type_price = InsureType.getGlassBreakageInsurePrice(df, car_use_type, seats, type_id, car_price);
					glass_breakage_param_array.getJSONObject(i).put("type_price", type_price);
				}
				if(!StringUtil.isEmpty(glass_type)){
					glass_breakage_insure_price = InsureType.getGlassBreakageInsurePrice(df, car_use_type, seats, Integer.valueOf(glass_type), car_price);
					glass_breakage_insure_obj.put("glass_type", glass_type);
				}
				glass_breakage_insure_obj.put("insure_id", InsureType.glass_breakage_insure);
				glass_breakage_insure_obj.put("insure_name", insuresMap.get(InsureType.glass_breakage_insure));
				glass_breakage_insure_obj.put("insure_param_list", glass_breakage_param_array);
				glass_breakage_insure_obj.put("insure_price", glass_breakage_insure_price);
				glass_breakage_insure_obj.put("insure_describe", "用于赔付本车玻璃破碎造成的损失");
				
				//司机责任险-保费计算
				JSONObject driver_insure_obj = new JSONObject();
				double driver_insure_price = 0.0;
				JSONArray driver_param_array = InsureType.getInsureParamArray(InsureType.driver_insure);
				for(int i=0; i<driver_param_array.size(); i++){
					int type_id = driver_param_array.getJSONObject(i).getInt("type_id");
					double type_price = InsureType.getDriverInsurePrice(df, car_use_type, seats, type_id);
					driver_param_array.getJSONObject(i).put("type_price", type_price);
					driver_param_array.getJSONObject(i).put("exempt_insure_price", df.format(type_price * 0.15));
				}
				if(!StringUtil.isEmpty(driver_coverage_id)){
					driver_insure_price = InsureType.getDriverInsurePrice(df, car_use_type, seats, Integer.valueOf(driver_coverage_id));
					driver_insure_obj.put("driver_coverage_id", driver_coverage_id);
				}
				driver_insure_obj.put("insure_id", InsureType.driver_insure);
				driver_insure_obj.put("insure_name", insuresMap.get(InsureType.driver_insure));
				driver_insure_obj.put("insure_param_list", driver_param_array);
				driver_insure_obj.put("insure_price", driver_insure_price);
				driver_insure_obj.put("insure_describe", "意外情况下对司机的人身安全进行赔偿");
				
				//乘客责任险-保费计算
				JSONObject passenger_insure_obj = new JSONObject();
				double passenger_insure_price = 0.0;
				JSONArray passenger_param_array = InsureType.getInsureParamArray(InsureType.passenger_insure);
				for(int i=0; i<passenger_param_array.size(); i++){
					int type_id = passenger_param_array.getJSONObject(i).getInt("type_id");
					double type_price = InsureType.getPassengerInsurePrice(df, car_use_type, seats, type_id);
					passenger_param_array.getJSONObject(i).put("type_price", type_price);
					passenger_param_array.getJSONObject(i).put("exempt_insure_price", df.format(type_price * 0.15));
				}
				if(!StringUtil.isEmpty(passenger_coverage_id)){
					passenger_insure_price = InsureType.getPassengerInsurePrice(df, car_use_type, seats, Integer.valueOf(passenger_coverage_id));
					passenger_insure_obj.put("passenger_coverage_id", passenger_coverage_id);
				}
				passenger_insure_obj.put("insure_id", InsureType.passenger_insure);
				passenger_insure_obj.put("insure_name", insuresMap.get(InsureType.passenger_insure));
				passenger_insure_obj.put("insure_param_list", passenger_param_array);
				passenger_insure_obj.put("insure_price", passenger_insure_price);
				passenger_insure_obj.put("insure_describe", "用于赔付车内本车乘客(非驾驶员)的人身伤亡费用");
				
				//自燃损失险-计算保费
				JSONObject self_ignite_insure_obj = new JSONObject();
				double self_ignite_insure_price = InsureType.getself_ignite_insure_price(df, car_age, car_price);
				self_ignite_insure_obj.put("insure_id", InsureType.self_ignite_insure);
				self_ignite_insure_obj.put("insure_name", insuresMap.get(InsureType.self_ignite_insure));
				self_ignite_insure_obj.put("insure_param_list", new JSONArray());
				self_ignite_insure_obj.put("insure_price", self_ignite_insure_price);
				self_ignite_insure_obj.put("insure_describe", "用于赔付车的自身原因起火造成车辆本身的损失");
				
				//车身划痕险-计算保费
				JSONObject scratch_insure_obj = new JSONObject();
				double scratch_insure_price = 0.0;
				JSONArray scratch_param_array = InsureType.getInsureParamArray(InsureType.scratch_insure);
				for(int i=0; i<scratch_param_array.size(); i++){
					int type_id = scratch_param_array.getJSONObject(i).getInt("type_id");
					double type_price = InsureType.getScratchInsurePrice(df, car_age, type_id, car_price);
					scratch_param_array.getJSONObject(i).put("type_price", type_price);
					scratch_param_array.getJSONObject(i).put("exempt_insure_price", df.format(type_price * 0.15));
				}
				if(!StringUtil.isEmpty(scratch_coverage_id)){
					scratch_insure_price = InsureType.getScratchInsurePrice(df, car_age, Integer.valueOf(scratch_coverage_id), car_price);
					scratch_insure_obj.put("scratch_coverage_id", scratch_coverage_id);
				}
				scratch_insure_obj.put("insure_id", InsureType.scratch_insure);
				scratch_insure_obj.put("insure_name", insuresMap.get(InsureType.scratch_insure));
				scratch_insure_obj.put("insure_param_list", scratch_param_array);
				scratch_insure_obj.put("insure_price", scratch_insure_price);
				scratch_insure_obj.put("insure_describe", "用于赔付他人恶意行为造成的车辆车身人为划痕造成的损失");
				
				//涉水险-计算保费
				JSONObject wade_insure_obj = new JSONObject();
				double wade_insure_price = 0.0;
				wade_insure_obj.put("insure_id", InsureType.wade_insure);
				wade_insure_obj.put("insure_name", insuresMap.get(InsureType.wade_insure));
				wade_insure_obj.put("insure_param_list", new JSONArray());
				wade_insure_obj.put("insure_price", wade_insure_price);
				wade_insure_obj.put("insure_describe", "用于赔付因水淹或涉水行驶造成发动机损坏的费用");
				
				//不计免赔险-计算保费
				JSONObject exempt_insure_obj = new JSONObject();
				double exempt_insure_price = 0.0;
				JSONArray exempt_param_array = InsureType.getInsureParamArray(InsureType.exempt_insure);
				if(!StringUtil.isEmpty(exempt_insure_typeids)){
					String[] insureTypeArray = exempt_insure_typeids.split(",");
					for(int i=0; i<insureTypeArray.length; i++){
						if(insureTypeArray[i].equals(InsureType.thirdparty_insure+"")){//第三者责任险
							exempt_insure_price += thirdparty_insure_price * 0.15;
						}
						if(insureTypeArray[i].equals(InsureType.damage_insure+"")){//车辆损失险
							exempt_insure_price += damage_insure_price * 0.15;
						}
						if(insureTypeArray[i].equals(InsureType.driver_insure+"")){//司机责任险
							exempt_insure_price += driver_insure_price * 0.15;
						}
						if(insureTypeArray[i].equals(InsureType.passenger_insure+"")){//乘客责任险
							exempt_insure_price += passenger_insure_price * 0.15;
						}
						if(insureTypeArray[i].equals(InsureType.scratch_insure+"")){//车身划痕险
							exempt_insure_price += scratch_insure_price * 0.15;
						}
						if(insureTypeArray[i].equals(InsureType.theft_insure+"")){//机动车盗抢险
							exempt_insure_price += theft_insure_price * 0.2;
						}
					}
					exempt_insure_price = Double.valueOf(df.format(exempt_insure_price));
					exempt_insure_obj.put("exempt_insure_typeids", exempt_insure_typeids);
				}
				exempt_insure_obj.put("insure_id", InsureType.exempt_insure);
				exempt_insure_obj.put("insure_name", insuresMap.get(InsureType.exempt_insure));
				exempt_insure_obj.put("insure_param_list", exempt_param_array);
				exempt_insure_obj.put("insure_price", exempt_insure_price);
				exempt_insure_obj.put("insure_describe", "事故发生后自己不再承担损失");
				
				//交强险-计算保费
				JSONObject compulsory_insure_obj = new JSONObject();
				double compulsory_insure_price = InsureType.getCompulsoryInsurePrice(df, car_use_type, seats);
				compulsory_insure_obj.put("insure_id", InsureType.compulsory_insure);
				compulsory_insure_obj.put("insure_name", insuresMap.get(InsureType.compulsory_insure));
				compulsory_insure_obj.put("insure_param_list", new JSONArray());
				compulsory_insure_obj.put("insure_price", compulsory_insure_price);
				compulsory_insure_obj.put("insure_describe", "国家规定的强制保险");

				//车船税
				JSONObject travel_tax_obj = new JSONObject();
				double travel_tax = InsureType.getTravelTax(df, car_discharge);
				travel_tax_obj.put("insure_id", InsureType.travel_tax);
				travel_tax_obj.put("insure_name", insuresMap.get(InsureType.travel_tax));
				travel_tax_obj.put("insure_param_list", new JSONArray());
				travel_tax_obj.put("insure_price", travel_tax);
				travel_tax_obj.put("insure_describe", "以排量为收取标准的国家税收");
				
				insureTypePriceMap = new HashMap<>();
				insureTypePriceMap.put(InsureType.damage_insure, damage_insure_obj);//车辆损失险
				insureTypePriceMap.put(InsureType.theft_insure, theft_insure_obj);//机动车盗抢险
				insureTypePriceMap.put(InsureType.thirdparty_insure, thirdparty_insure_obj);//第三者责任险
				insureTypePriceMap.put(InsureType.glass_breakage_insure, glass_breakage_insure_obj);//玻璃破碎险
				insureTypePriceMap.put(InsureType.driver_insure, driver_insure_obj);//司机责任险
				insureTypePriceMap.put(InsureType.passenger_insure, passenger_insure_obj);//乘客责任险
				insureTypePriceMap.put(InsureType.self_ignite_insure, self_ignite_insure_obj);//自燃损失险
				insureTypePriceMap.put(InsureType.scratch_insure, scratch_insure_obj);//车身划痕险
				insureTypePriceMap.put(InsureType.wade_insure, wade_insure_obj);//涉水险
				insureTypePriceMap.put(InsureType.exempt_insure, exempt_insure_obj);//不计免赔险
				insureTypePriceMap.put(InsureType.compulsory_insure, compulsory_insure_obj);//交强险
				insureTypePriceMap.put(InsureType.travel_tax, travel_tax_obj);//车船税
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return insureTypePriceMap;
	}
	
}
