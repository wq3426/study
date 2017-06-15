package com.enation.app.shop.core.service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @Description 保险险种类型抽象类，用于定义险种类型值
 *
 * @createTime 2016年9月8日 下午8:02:47
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
public abstract class InsureType {
	public static final int damage_insure = 1;         //车辆损失险
	public static final int theft_insure = 2;          //机动车盗抢险
	public static final int thirdparty_insure = 3;     //第三者责任险
	public static final int glass_breakage_insure = 4; //玻璃破碎险
	public static final int driver_insure = 5;         //司机责任险
	public static final int passenger_insure = 6;      //乘客责任险
	public static final int self_ignite_insure = 7;    //自燃损失险
	public static final int scratch_insure = 8;        //车身划痕险
	public static final int wade_insure = 9;           //涉水险
	public static final int exempt_insure = 10;        //不计免赔险
	
	public static final int compulsory_insure = 11;    //交强险
	public static final int travel_tax = 12;           //车船税
	
	public static final String[] glassTypeArray = {"国产","进口"};  //玻璃破碎险-玻璃类型
	public static final String[] thirdpartyInsureCoverageArray = {"5万","10万","15万","20万","30万","50万","100万"};//第三者责任险-保额
	public static final String[] driverInsureCoverageArray = {"1万","2万","5万","10万"};//车上人员-司机责任险-保额
	public static final String[] passengerInsureCoverageArray = {"1万","2万","5万","10万"};//车上人员-乘客责任险-保额
	public static final String[] scratchInsureCoverageArray = {"2000","5000","1万","2万"};//车身划痕险-保额
	
	public static Map<Integer, String> exempt_insureMap;//不计免赔险适用范围map集合
	
	static {
		exempt_insureMap = new HashMap<>();
		
		exempt_insureMap.put(damage_insure, "车损");
		exempt_insureMap.put(theft_insure, "盗抢");
		exempt_insureMap.put(thirdparty_insure, "第三者");
		exempt_insureMap.put(driver_insure, "司机");
		exempt_insureMap.put(passenger_insure, "乘客");
		exempt_insureMap.put(scratch_insure, "划痕");
	}
	
	/**
	 * @description 获取险种类型、类型名称map
	 * @date 2016年9月9日 下午8:10:42
	 * @return
	 */
	public static Map<Integer, String> getInsureTypeMap(){
		Map<Integer, String> map = new HashMap<Integer, String>();
		
		map.put(damage_insure, "车辆损失险");
		map.put(theft_insure, "机动车盗抢险");
		map.put(thirdparty_insure, "第三者责任险");
		map.put(glass_breakage_insure, "玻璃破碎险");
		map.put(driver_insure, "司机责任险");
		map.put(passenger_insure, "乘客责任险");
		map.put(self_ignite_insure, "自燃损失险");
		map.put(scratch_insure, "车身划痕险");
		map.put(wade_insure, "涉水险");
		map.put(exempt_insure, "不计免赔险");
		map.put(compulsory_insure, "交强险");
		map.put(travel_tax, "车船税");

		return map;
	}
	
	/**
	 * @description 根据险种类型id获取计算保费所需参数JSONArray
	 * @date 2016年9月12日 下午9:19:22
	 * @param type  险种类型id
	 * @return
	 */
	public static JSONArray getInsureParamArray(int insure_type){
		JSONArray returnArray = new JSONArray();
		
		String[] paramArray = {};
		if(insure_type == InsureType.glass_breakage_insure){//获取玻璃破碎险玻璃类型json
			paramArray = glassTypeArray;
		}
		if(insure_type == InsureType.thirdparty_insure){//获取第三者责任险保额的json
			paramArray = thirdpartyInsureCoverageArray;
		}
		if(insure_type == InsureType.driver_insure){//获取车上人员-司机责任险保额json
			paramArray = driverInsureCoverageArray;
		}
		if(insure_type == InsureType.passenger_insure){//获取车上人员-乘客责任险保额json
			paramArray = passengerInsureCoverageArray;
		}
		if(insure_type == InsureType.scratch_insure){//获取划痕险保额json
			paramArray = scratchInsureCoverageArray;
		}
		
		for(int i=0; i<paramArray.length; i++){
			JSONObject obj = new JSONObject();
			obj.put("type_id", i);
			obj.put("type_value", paramArray[i]);
			
			returnArray.add(obj);
		}
		
		if(insure_type == InsureType.exempt_insure){//获取不计免赔险险种类型json
			returnArray = new JSONArray();
			
			for(Map.Entry<Integer, String> m : exempt_insureMap.entrySet()){
				JSONObject obj = new JSONObject();
				obj.put("type_id", m.getKey());
				obj.put("type_value", m.getValue());
				
				returnArray.add(obj);
			}
		}
		
		return returnArray;
	}
	
	/**
	 * @description 将险种参数id的JSONArray转换成险种参数值的JSONArray
	 * @date 2016年9月12日 下午9:08:43
	 * @param jsonarray
	 * @return
	 */
	public static Map<Integer, Object> getInsureParamValueMap(JSONArray jsonarray){
		Map<Integer, Object> map = new HashMap<>();

		for(int i=0; i<jsonarray.size(); i++){
			JSONObject obj = jsonarray.getJSONObject(i);
			int param_type = obj.getInt("param_type");
			String param_value = obj.getString("param_value");
			
			String param_name = "";
			if(param_type == InsureType.glass_breakage_insure){//获取玻璃破碎险玻璃类型名
				param_name = glassTypeArray[Integer.valueOf(param_value)];
			}
			if(param_type == InsureType.thirdparty_insure){//获取第三者责任险保额
				param_name = thirdpartyInsureCoverageArray[Integer.valueOf(param_value)];
			}
			if(param_type == InsureType.driver_insure){//获取车上人员-司机责任险保额
				param_name = driverInsureCoverageArray[Integer.valueOf(param_value)];
			}
			if(param_type == InsureType.passenger_insure){//获取车上人员-乘客责任险保额
				param_name = passengerInsureCoverageArray[Integer.valueOf(param_value)];
			}
			if(param_type == InsureType.scratch_insure){//获取划痕险保额
				param_name = scratchInsureCoverageArray[Integer.valueOf(param_value)];
			}
			if(param_type == InsureType.exempt_insure){
				String[] exemptArray = param_value.split(",");
				for(int j=0; j<exemptArray.length; j++){
					param_name += exempt_insureMap.get(Integer.valueOf(exemptArray[j])) + ",";
				}
				param_name = param_name.length() > 0 ? param_name.substring(0, param_name.length() - 1) : param_name;
			}
			
			map.put(param_type, param_name);
		}
		
		return map;
	}
	
	/**
	 * @description 计算车辆折旧后的价格
	 * @date 2016年9月10日 下午1:57:37
	 * @param df  金额格式化formatter
	 * @param car_use_type  车辆使用性质  1 "家庭自用车"  2 "企业非营业客车"  3 "机关非营业客车"  4 "营业客车-出租"  5 "营业客车-其他"
	 * @param car_kind      车辆类别   1 "9座以下客车"  2 "10座以上客车"  3 "微型载货汽车"  4 "载货汽车(带拖挂)" 5 "低速货车/三轮汽车"  6 "其他车辆"
	 * @param car_age       车龄
	 * @param car_price     车型指导价-即保额
	 * @return
	 */
	public static double getDepreciationCarPrice(DecimalFormat formatter, int car_use_type, int car_kind, double car_age, double car_price) {
		int car_age_month = (int) Math.floor(car_age * 12);//车辆折旧月数
		
		if(car_age_month > 1){//车龄不足一个月的，不计算折旧价格
			if(car_kind == 1){//9座以下客车
				if(car_use_type == 1){//家庭自用车
					car_price = car_price - (car_price * car_age_month * 6 / 1000);
				}else if(car_use_type == 2 || car_use_type == 3){//企业非营业客车   或   机关非营业客车
					car_price = car_price - (car_price * car_age_month * 6 / 1000);
				}else if(car_use_type == 4){//营业客车-出租
					car_price = car_price - (car_price * car_age_month * 11 / 1000);
				}else if(car_use_type == 5){//营业客车-其他
					car_price = car_price - (car_price * car_age_month * 9 / 1000);
				}
			}else if(car_kind == 2){//10座以上客车
				if(car_use_type == 1){//家庭自用车
					car_price = car_price - (car_price * car_age_month * 9 / 1000);
				}else if(car_use_type == 2 || car_use_type == 3){//企业非营业客车   或   机关非营业客车
					car_price = car_price - (car_price * car_age_month * 9 / 1000);
				}else if(car_use_type == 4){//营业客车-出租
					car_price = car_price - (car_price * car_age_month * 11 / 1000);
				}else if(car_use_type == 5){//营业客车-其他
					car_price = car_price - (car_price * car_age_month * 9 / 1000);
				}
			}else if(car_kind == 3){//微型载货汽车
				if(car_use_type == 2 || car_use_type == 3){//企业非营业客车   或   机关非营业客车
					car_price = car_price - (car_price * car_age_month * 9 / 1000);
				}else if(car_use_type == 4){//营业客车-出租
					car_price = car_price - (car_price * car_age_month * 11 / 1000);
				}else if(car_use_type == 5){//营业客车-其他
					car_price = car_price - (car_price * car_age_month * 11 / 1000);
				}
			}else if(car_kind == 4){//载货汽车(带拖挂)
				if(car_use_type == 2 || car_use_type == 3){//企业非营业客车   或   机关非营业客车
					car_price = car_price - (car_price * car_age_month * 9 / 1000);
				}else if(car_use_type == 4){//营业客车-出租
					car_price = car_price - (car_price * car_age_month * 11 / 1000);
				}else if(car_use_type == 5){//营业客车-其他
					car_price = car_price - (car_price * car_age_month * 11 / 1000);
				}
			}else if(car_kind == 5){//低速货车/三轮汽车
				if(car_use_type == 2 || car_use_type == 3){//企业非营业客车   或   机关非营业客车
					car_price = car_price - (car_price * car_age_month * 11 / 1000);
				}else if(car_use_type == 4){//营业客车-出租
					car_price = car_price - (car_price * car_age_month * 14 / 1000);
				}else if(car_use_type == 5){//营业客车-其他
					car_price = car_price - (car_price * car_age_month * 14 / 1000);
				}
			}else if(car_kind == 6){//其他车辆
				if(car_use_type == 2 || car_use_type == 3){//企业非营业客车   或   机关非营业客车
					car_price = car_price - (car_price * car_age_month * 9 / 1000);
				}else if(car_use_type == 4){//营业客车-出租
					car_price = car_price - (car_price * car_age_month * 11 / 1000);
				}else if(car_use_type == 5){//营业客车-其他
					car_price = car_price - (car_price * car_age_month * 9 / 1000);
				}
			}
		}
		
		return Double.valueOf(formatter.format(car_price));
	}
	
	/**
	 * @description 计算保费-车辆损失险
	 * @date 2016年9月10日 下午2:41:45
	 * @param df  金额格式化formatter
	 * @param car_use_type  1 家庭自用车   2  企业非营业客车  3  机关非营业客车  4  营业客车-出租  5  营业客车-其他
	 * @param seats    6座以下   6-10座  
	 * @param car_age   1年以下   1-2年  2-6年    6年以上
	 * @param car_price  车型指导价-即保额
	 * @return
	 */
	public static Double getDamageInsurePrice(DecimalFormat formatter, int car_use_type, int seats, double car_age, double car_price) {
		double damage_insure_price = 0.0;
		if(car_use_type == 1){//家庭自用车
			if(car_age < 1){//1年以下
				if(seats < 6){//6座以下
					damage_insure_price = 539.0 + car_price * 1.28 / 100;
				}else if(seats < 10){//6-10座 
					damage_insure_price = 646.0 + car_price * 1.28 / 100;
				}
			}else if(car_age < 2){//1-2年
				if(seats < 6){//6座以下
					damage_insure_price = 513.0 + car_price * 1.22 / 100;
				}else if(seats < 10){//6-10座 
					damage_insure_price = 616.0 + car_price * 1.22 / 100;
				}
			}else if(car_age < 6){//2-6年
				if(seats < 6){//6座以下
					damage_insure_price = 508.0 + car_price * 1.21 / 100;
				}else if(seats < 10){//6-10座 
					damage_insure_price = 609.0 + car_price * 1.21 / 100;
				}
			}else {//6年以上
				if(seats < 6){//6座以下
					damage_insure_price = 523.0 + car_price * 1.24 / 100;
				}else if(seats < 10){//6-10座 
					damage_insure_price = 628.0 + car_price * 1.24 / 100;
				}
			}
		}else if(car_use_type == 2){//企业非营业客车
			if(car_age < 1){//1年以下
				if(seats < 6){//6座以下
					damage_insure_price = 335.0 + car_price * 1.11 / 100;
				}else if(seats < 10){//6-10座 
					damage_insure_price = 402.0 + car_price * 1.05 / 100;
				}
			}else if(car_age < 2){//1-2年
				if(seats < 6){//6座以下
					damage_insure_price = 319.0 + car_price * 1.06 / 100;
				}else if(seats < 10){//6-10座 
					damage_insure_price = 383.0 + car_price * 1.00 / 100;
				}
			}else if(car_age < 6){//2-6年
				if(seats < 6){//6座以下
					damage_insure_price = 316.0 + car_price * 1.05 / 100;
				}else if(seats < 10){//6-10座 
					damage_insure_price = 379.0 + car_price * 0.99 / 100;
				}
			}else {//6年以上
				if(seats < 6){//6座以下
					damage_insure_price = 325.0 + car_price * 1.08 / 100;
				}else if(seats < 10){//6-10座 
					damage_insure_price = 390.0 + car_price * 1.02 / 100;
				}
			}
		}else if(car_use_type == 3){//机关非营业客车
			if(car_age < 1){//1年以下
				if(seats < 6){//6座以下
					damage_insure_price = 259.0 + car_price * 0.86 / 100;
				}else if(seats < 10){//6-10座 
					damage_insure_price = 311.0 + car_price * 0.82 / 100;
				}
			}else if(car_age < 2){//1-2年
				if(seats < 6){//6座以下
					damage_insure_price = 247.0 + car_price * 0.82 / 100;
				}else if(seats < 10){//6-10座 
					damage_insure_price = 296.0 + car_price * 0.78 / 100;
				}
			}else if(car_age < 6){//2-6年
				if(seats < 6){//6座以下
					damage_insure_price = 245.0 + car_price * 0.81 / 100;
				}else if(seats < 10){//6-10座 
					damage_insure_price = 293.0 + car_price * 0.77 / 100;
				}
			}else {//6年以上
				if(seats < 6){//6座以下
					damage_insure_price = 252.0 + car_price * 0.84 / 100;
				}else if(seats < 10){//6-10座 
					damage_insure_price = 302.0 + car_price * 0.79 / 100;
				}
			}
		}
		
		return Double.valueOf(formatter.format(damage_insure_price));
	}
	
	/**
	 * @description 计算保费-机动车盗抢险
	 * @date 2016年9月10日 下午3:11:12
	 * @param df  金额格式化formatter
	 * @param car_use_type  1 家庭自用车   2  企业非营业客车  3  机关非营业客车  4  营业客车-出租  5  营业客车-其他
	 * @param seats         6座以下   6-10座
	 * @param car_price  车型指导价-即保额
	 * @return
	 */
	public static double getTheftInsurePrice(DecimalFormat formatter, int car_use_type, int seats, double car_price) {
		double theft_insure_price = 0.0;
		if(car_use_type == 1){//家庭自用车
			if(seats < 6){//6座以下
				theft_insure_price = 120.0 + car_price * 0.53 / 100;
			}else if(seats < 10){//6-10座 
				theft_insure_price = 140.0 + car_price * 0.44 / 100;
			}
		}else if(car_use_type == 2){//企业非营业客车
			if(seats < 6){//6座以下
				theft_insure_price = 120.0 + car_price * 0.47 / 100;
			}else if(seats < 10){//6-10座 
				theft_insure_price = 130.0 + car_price * 0.44 / 100;
			}
		}else if(car_use_type == 3){//机关非营业客车
			if(seats < 6){//6座以下
				theft_insure_price = 110.0 + car_price * 0.40 / 100;
			}else if(seats < 10){//6-10座 
				theft_insure_price = 120.0 + car_price * 0.37 / 100;
			}
		}
		
		return Double.valueOf(formatter.format(theft_insure_price));
	}
	
	/**
	 * @description 计算保费-第三者责任险
	 * @date 2016年9月10日 下午3:30:29
	 * @param df  金额格式化formatter
	 * @param car_use_type             1 家庭自用车   2  企业非营业客车  3  机关非营业客车  4  营业客车-出租  5  营业客车-其他
	 * @param seats                    6座以下   6-10座
	 * @param thirdparty_coverage_id   保额在第三者责任险-保额字符串数组 {"5万","10万","20万","30万","50万","100万","200万"} 中的索引
	 * @return
	 */
	public static double getThirdpartyInsurePrice(DecimalFormat formatter, int car_use_type, int seats, int thirdparty_coverage_id) {
		double thirdparty_insure_price = 0.0;
		String[] thirdpartyInsureCoverageArray = InsureType.thirdpartyInsureCoverageArray;
		if(car_use_type == 1){//家庭自用车
			if(seats < 6){//6座以下
				if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("5万")){
					thirdparty_insure_price = 710.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("10万")){
					thirdparty_insure_price = 1026.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("15万")){
					thirdparty_insure_price = 1169.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("20万")){
					thirdparty_insure_price = 1270.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("30万")){
					thirdparty_insure_price = 1434.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("50万")){
					thirdparty_insure_price = 1721.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("100万")){
					thirdparty_insure_price = 2242.0;
				}
			}else if(seats < 10){//6-10座 
				if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("5万")){
					thirdparty_insure_price = 659.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("10万")){
					thirdparty_insure_price = 928.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("15万")){
					thirdparty_insure_price = 1048.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("20万")){
					thirdparty_insure_price = 1131.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("30万")){
					thirdparty_insure_price = 1266.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("50万")){
					thirdparty_insure_price = 1507.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("100万")){
					thirdparty_insure_price = 1963.0;
				}
			}
		}else if(car_use_type == 2){//企业非营业客车
			if(seats < 6){//6座以下
				if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("5万")){
					thirdparty_insure_price = 758.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("10万")){
					thirdparty_insure_price = 1067.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("15万")){
					thirdparty_insure_price = 1206.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("20万")){
					thirdparty_insure_price = 1301.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("30万")){
					thirdparty_insure_price = 1456.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("50万")){
					thirdparty_insure_price = 1734.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("100万")){
					thirdparty_insure_price = 2258.0;
				}
			}else if(seats < 10){//6-10座 
				if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("5万")){
					thirdparty_insure_price = 730.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("10万")){
					thirdparty_insure_price = 1039.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("15万")){
					thirdparty_insure_price = 1179.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("20万")){
					thirdparty_insure_price = 1275.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("30万")){
					thirdparty_insure_price = 1433.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("50万")){
					thirdparty_insure_price = 1711.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("100万")){
					thirdparty_insure_price = 2228.0;
				}
			}
		}else if(car_use_type == 3){//机关非营业客车
			if(seats < 6){//6座以下
				if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("5万")){
					thirdparty_insure_price = 758.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("10万")){
					thirdparty_insure_price = 1067.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("15万")){
					thirdparty_insure_price = 1206.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("20万")){
					thirdparty_insure_price = 1301.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("30万")){
					thirdparty_insure_price = 1456.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("50万")){
					thirdparty_insure_price = 1734.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("100万")){
					thirdparty_insure_price = 2258.0;
				}
			}else if(seats < 10){//6-10座 
				if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("5万")){
					thirdparty_insure_price = 730.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("10万")){
					thirdparty_insure_price = 1039.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("15万")){
					thirdparty_insure_price = 1179.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("20万")){
					thirdparty_insure_price = 1275.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("30万")){
					thirdparty_insure_price = 1433.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("50万")){
					thirdparty_insure_price = 1711.0;
				}else if(thirdpartyInsureCoverageArray[thirdparty_coverage_id].equals("100万")){
					thirdparty_insure_price = 2228.0;
				}
			}
		}
		
		return Double.valueOf(formatter.format(thirdparty_insure_price));
	}
	
	/**
	 * @description 计算保费-玻璃破碎险
	 * @date 2016年9月10日 下午4:04:49
	 * @param df  金额格式化formatter
	 * @param car_use_type            1 家庭自用车   2  企业非营业客车  3  机关非营业客车  4  营业客车-出租  5  营业客车-其他
	 * @param seats                   6座以下   6-10座    10座以上
	 * @param glass_type              玻璃类型在玻璃破碎险-玻璃类型字符串数组  {"国产","进口"} 中的索引 
	 * @param car_price               车型指导价-即保额
	 * @return
	 */
	public static double getGlassBreakageInsurePrice(DecimalFormat formatter, int car_use_type, int seats, int glass_type, double car_price) {
		double glass_breakage_insure_price = 0.0;
		String[] glassTypeArray = InsureType.glassTypeArray;
		if(car_use_type == 1){//家庭自用车
			if(seats < 6){//6座以下
				if(glassTypeArray[glass_type].equals("国产")){
					glass_breakage_insure_price = car_price * 0.19 / 100;
				}else if(glassTypeArray[glass_type].equals("进口")){
					glass_breakage_insure_price = car_price * 0.31 / 100;
				}
			}else if(seats < 10){//6-10座 
				if(glassTypeArray[glass_type].equals("国产")){
					glass_breakage_insure_price = car_price * 0.20 / 100;
				}else if(glassTypeArray[glass_type].equals("进口")){
					glass_breakage_insure_price = car_price * 0.32 / 100;
				}
			}else {//10座以上
				if(glassTypeArray[glass_type].equals("进口")){
					glass_breakage_insure_price = car_price * 0.38 / 100;
				}
			}
		}else if(car_use_type == 2){//企业非营业客车
			if(seats < 6){//6座以下
				if(glassTypeArray[glass_type].equals("国产")){
					glass_breakage_insure_price = car_price * 0.14 / 100;
				}else if(glassTypeArray[glass_type].equals("进口")){
					glass_breakage_insure_price = car_price * 0.26 / 100;
				}
			}else if(seats < 10){//6-10座 
				if(glassTypeArray[glass_type].equals("国产")){
					glass_breakage_insure_price = car_price * 0.15 / 100;
				}else if(glassTypeArray[glass_type].equals("进口")){
					glass_breakage_insure_price = car_price * 0.26 / 100;
				}
			}
		}else if(car_use_type == 3){//机关非营业客车
			if(seats < 6){//6座以下
				if(glassTypeArray[glass_type].equals("国产")){
					glass_breakage_insure_price = car_price * 0.14 / 100;
				}else if(glassTypeArray[glass_type].equals("进口")){
					glass_breakage_insure_price = car_price * 0.26 / 100;
				}
			}else if(seats < 10){//6-10座 
				if(glassTypeArray[glass_type].equals("国产")){
					glass_breakage_insure_price = car_price * 0.14 / 100;
				}else if(glassTypeArray[glass_type].equals("进口")){
					glass_breakage_insure_price = car_price * 0.26 / 100;
				}
			}
		}
		
		return Double.valueOf(formatter.format(glass_breakage_insure_price));
	}
	
	/**
	 * @description 计算保费-司机责任险
	 * @date 2016年9月10日 下午4:25:42
	 * @param formatter 金额格式化formatter
	 * @param car_use_type            1 家庭自用车   2  企业非营业客车  3  机关非营业客车  4  营业客车-出租  5  营业客车-其他
	 * @param seats                   6座以下   6-10座    10座以上
	 * @param driver_coverage_id      保额在司机责任险-保额字符串数组 {"1万","2万","5万","10万"} 中的索引
	 * @return
	 */
	public static double getDriverInsurePrice(DecimalFormat formatter, int car_use_type, int seats, int driver_coverage_id) {
		double driver_insure_price = 0.0;
		String[] driverInsureCoverageArray = InsureType.driverInsureCoverageArray;
		if(car_use_type == 1){//家庭自用车
			if(seats < 6){//6座以下
				if(driverInsureCoverageArray[driver_coverage_id].equals("1万")){
					driver_insure_price = 10000.0 * 0.41 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("2万")){
					driver_insure_price = 20000.0 * 0.41 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("5万")){
					driver_insure_price = 50000.0 * 0.41 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("10万")){
					driver_insure_price = 100000.0 * 0.41 / 100;
				}
			}else if(seats < 10){//6-10座 
				if(driverInsureCoverageArray[driver_coverage_id].equals("1万")){
					driver_insure_price = 10000.0 * 0.39 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("2万")){
					driver_insure_price = 20000.0 * 0.39 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("5万")){
					driver_insure_price = 50000.0 * 0.39 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("10万")){
					driver_insure_price = 100000.0 * 0.39 / 100;
				}
			}
		}else if(car_use_type == 2){//企业非营业客车
			if(seats < 6){//6座以下
				if(driverInsureCoverageArray[driver_coverage_id].equals("1万")){
					driver_insure_price = 10000.0 * 0.41 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("2万")){
					driver_insure_price = 20000.0 * 0.41 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("5万")){
					driver_insure_price = 50000.0 * 0.41 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("10万")){
					driver_insure_price = 100000.0 * 0.41 / 100;
				}
			}else if(seats < 10){//6-10座 
				if(driverInsureCoverageArray[driver_coverage_id].equals("1万")){
					driver_insure_price = 10000.0 * 0.38 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("2万")){
					driver_insure_price = 20000.0 * 0.38 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("5万")){
					driver_insure_price = 50000.0 * 0.38 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("10万")){
					driver_insure_price = 100000.0 * 0.38 / 100;
				}
			}
		}else if(car_use_type == 3){//机关非营业客车
			if(seats < 6){//6座以下
				if(driverInsureCoverageArray[driver_coverage_id].equals("1万")){
					driver_insure_price = 10000.0 * 0.39 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("2万")){
					driver_insure_price = 20000.0 * 0.39 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("5万")){
					driver_insure_price = 50000.0 * 0.39 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("10万")){
					driver_insure_price = 100000.0 * 0.39 / 100;
				}
			}else if(seats < 10){//6-10座 
				if(driverInsureCoverageArray[driver_coverage_id].equals("1万")){
					driver_insure_price = 10000.0 * 0.36 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("2万")){
					driver_insure_price = 20000.0 * 0.36 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("5万")){
					driver_insure_price = 50000.0 * 0.36 / 100;
				}else if(driverInsureCoverageArray[driver_coverage_id].equals("10万")){
					driver_insure_price = 100000.0 * 0.36 / 100;
				}
			}
		}
		
		return Double.valueOf(formatter.format(driver_insure_price));
	}
	
	/**
	 * @description 计算保费-乘客责任险
	 * @date 2016年9月10日 下午4:47:36
	 * @param formatter   金额格式化formatter
	 * @param car_use_type            1 家庭自用车   2  企业非营业客车  3  机关非营业客车  4  营业客车-出租  5  营业客车-其他
	 * @param seats                   6座以下   6-10座    10座以上
	 * @param passenger_coverage_id   保额在乘客责任险-保额字符串数组 {"1万","2万","5万","10万"} 中的索引
	 * @return
	 */
	public static double getPassengerInsurePrice(DecimalFormat formatter, int car_use_type, int seats, int passenger_coverage_id) {
		double passenger_insure_price = 0.0;
		int insure_seats = seats - 1;//投保乘客座位数
		String[] passengerInsureCoverageArray = InsureType.passengerInsureCoverageArray;
		if(car_use_type == 1){//家庭自用车
			if(seats < 6){//6座以下
				if(passengerInsureCoverageArray[passenger_coverage_id].equals("1万")){
					passenger_insure_price = 10000.0 * insure_seats * 0.26 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("2万")){
					passenger_insure_price = 20000.0 * insure_seats * 0.26 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("5万")){
					passenger_insure_price = 50000.0 * insure_seats * 0.26 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("10万")){
					passenger_insure_price = 100000.0 * insure_seats * 0.26 / 100;
				}
			}else if(seats < 10){//6-10座 
				if(passengerInsureCoverageArray[passenger_coverage_id].equals("1万")){
					passenger_insure_price = 10000.0 * insure_seats * 0.25 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("2万")){
					passenger_insure_price = 20000.0 * insure_seats * 0.25 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("5万")){
					passenger_insure_price = 50000.0 * insure_seats * 0.25 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("10万")){
					passenger_insure_price = 100000.0 * insure_seats * 0.25 / 100;
				}
			}
		}else if(car_use_type == 2){//企业非营业客车
			if(seats < 6){//6座以下
				if(passengerInsureCoverageArray[passenger_coverage_id].equals("1万")){
					passenger_insure_price = 10000.0 * insure_seats * 0.25 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("2万")){
					passenger_insure_price = 20000.0 * insure_seats * 0.25 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("5万")){
					passenger_insure_price = 50000.0 * insure_seats * 0.25 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("10万")){
					passenger_insure_price = 100000.0 * insure_seats * 0.25 / 100;
				}
			}else if(seats < 10){//6-10座 
				if(passengerInsureCoverageArray[passenger_coverage_id].equals("1万")){
					passenger_insure_price = 10000.0 * insure_seats * 0.23 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("2万")){
					passenger_insure_price = 20000.0 * insure_seats * 0.23 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("5万")){
					passenger_insure_price = 50000.0 * insure_seats * 0.23 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("10万")){
					passenger_insure_price = 100000.0 * insure_seats * 0.23 / 100;
				}
			}
		}else if(car_use_type == 3){//机关非营业客车
			if(seats < 6){//6座以下
				if(passengerInsureCoverageArray[passenger_coverage_id].equals("1万")){
					passenger_insure_price = 10000.0 * insure_seats * 0.24 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("2万")){
					passenger_insure_price = 20000.0 * insure_seats * 0.24 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("5万")){
					passenger_insure_price = 50000.0 * insure_seats * 0.24 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("10万")){
					passenger_insure_price = 100000.0 * insure_seats * 0.24 / 100;
				}
			}else if(seats < 10){//6-10座 
				if(passengerInsureCoverageArray[passenger_coverage_id].equals("1万")){
					passenger_insure_price = 10000.0 * insure_seats * 0.22 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("2万")){
					passenger_insure_price = 20000.0 * insure_seats * 0.22 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("5万")){
					passenger_insure_price = 50000.0 * insure_seats * 0.22 / 100;
				}else if(passengerInsureCoverageArray[passenger_coverage_id].equals("10万")){
					passenger_insure_price = 100000.0 * insure_seats * 0.22 / 100;
				}
			}
		}
		
		return Double.valueOf(formatter.format(passenger_insure_price));
	}
	
	/**
	 * @description 计算保费-自燃损失险
	 * @date 2016年9月10日 下午5:08:49
	 * @param formatter   金额格式化formatter
	 * @param car_age     车龄   1年以下  1-2年  2-6年  6年以上
	 * @param car_price   车型指导价-即保额
	 * @return
	 */
	public static double getself_ignite_insure_price(DecimalFormat formatter, double car_age, double car_price) {
		double self_ignite_insure_price = 0.0;
		if(car_age < 1){//1年以下
			self_ignite_insure_price = car_price * 0.15 / 100;
		}else if(car_age < 2){//1-2年
			self_ignite_insure_price = car_price * 0.18 / 100;
		}else if(car_age < 6){//2-6年
			self_ignite_insure_price = car_price * 0.20 / 100;
		}else {//6年以上
			self_ignite_insure_price = car_price * 0.23 / 100;
		}
		
		return Double.valueOf(formatter.format(self_ignite_insure_price));
	}
	
	/**
	 * @description 计算保费-车身划痕险
	 * @date 2016年9月10日 下午5:16:24
	 * @param formatter            金额格式化formatter
	 * @param car_age              车龄   2年以下  2年及以上
	 * @param scratch_coverage_id  保额在车身划痕险-保额字符串数组 {"2000","5000","1万","2万"} 中的索引
	 * @param car_price            车型指导价-即保额
	 * @return
	 */
	public static double getScratchInsurePrice(DecimalFormat formatter, double car_age, int scratch_coverage_id, double car_price) {
		double scratch_insure_price = 0.0;
		String[] scratchInsureCoverageArray = InsureType.scratchInsureCoverageArray;
		if(car_age < 2){//2年以下
			if(car_price < 30){//30万以下
				if(scratchInsureCoverageArray[scratch_coverage_id].equals("2000")){
					scratch_insure_price = 400.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("5000")){
					scratch_insure_price = 570.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("1万")){
					scratch_insure_price = 760.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("2万")){
					scratch_insure_price = 1140.0;
				}
			}else if(car_price < 50){//30-50万
				if(scratchInsureCoverageArray[scratch_coverage_id].equals("2000")){
					scratch_insure_price = 585.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("5000")){
					scratch_insure_price = 900.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("1万")){
					scratch_insure_price = 1170.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("2万")){
					scratch_insure_price = 1780.0;
				}
			}else {//50万以上
				if(scratchInsureCoverageArray[scratch_coverage_id].equals("2000")){
					scratch_insure_price = 850.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("5000")){
					scratch_insure_price = 1100.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("1万")){
					scratch_insure_price = 1500.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("2万")){
					scratch_insure_price = 2250.0;
				}
			}
		}else{//2年及以上
			if(car_price < 30){//30万以下
				if(scratchInsureCoverageArray[scratch_coverage_id].equals("2000")){
					scratch_insure_price = 610.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("5000")){
					scratch_insure_price = 850.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("1万")){
					scratch_insure_price = 1300.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("2万")){
					scratch_insure_price = 1900.0;
				}
			}else if(car_price < 50){//30-50万
				if(scratchInsureCoverageArray[scratch_coverage_id].equals("2000")){
					scratch_insure_price = 900.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("5000")){
					scratch_insure_price = 1350.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("1万")){
					scratch_insure_price = 1800.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("2万")){
					scratch_insure_price = 2600.0;
				}
			}else {//50万以上
				if(scratchInsureCoverageArray[scratch_coverage_id].equals("2000")){
					scratch_insure_price = 1100.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("5000")){
					scratch_insure_price = 1500.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("1万")){
					scratch_insure_price = 2000.0;
				}else if(scratchInsureCoverageArray[scratch_coverage_id].equals("2万")){
					scratch_insure_price = 3000.0;
				}
			}
		}
		
		return Double.valueOf(formatter.format(scratch_insure_price));
	}
	
	/**
	 * @description 计算保费-交强险
	 * @date 2016年9月10日 下午6:37:35
	 * @param formatter            金额格式化formatter
	 * @param car_use_type         1 家庭自用车   2  企业非营业客车  3  机关非营业客车  4  营业客车-出租  5  营业客车-城市公交  6 营业客车-公路客运  7 非营业货车  8 营业货车
	 * @param seats                6座以下、6-10座、10-20座、20-36座、36座以上
	 * @return
	 */
	public static double getCompulsoryInsurePrice(DecimalFormat formatter, int car_use_type, int seats) {
		double compulsory_insure_price = 0.0;
		if(car_use_type == 1){//家庭自用车
			if(seats < 6){
				compulsory_insure_price = 950.0;
			}else{
				compulsory_insure_price = 1100.0;
			}
		}else if(car_use_type == 2){//企业非营业客车
			if(seats < 6){
				compulsory_insure_price = 1000.0;
			}else if(seats < 10){
				compulsory_insure_price = 1130.0;
			}else if(seats < 20){
				compulsory_insure_price = 1220.0;
			}else {
				compulsory_insure_price = 1270.0;
			}
		}else if(car_use_type == 3){//机关非营业客车
			if(seats < 6){
				compulsory_insure_price = 950.0;
			}else if(seats < 10){
				compulsory_insure_price = 1070.0;
			}else if(seats < 20){
				compulsory_insure_price = 1140.0;
			}else {
				compulsory_insure_price = 1320.0;
			}
		}else if(car_use_type == 4){//营业客车-出租
			if(seats < 6){
				compulsory_insure_price = 1800.0;
			}else if(seats < 10){
				compulsory_insure_price = 2360.0;
			}else if(seats < 20){
				compulsory_insure_price = 2400.0;
			}else if(seats < 36){
				compulsory_insure_price = 2560.0;
			}else {
				compulsory_insure_price = 3530.0;
			}
		}else if(car_use_type == 5){//营业客车-城市公交
			if(seats < 10){
				compulsory_insure_price = 2250.0;
			}else if(seats < 20){
				compulsory_insure_price = 2520.0;
			}else if(seats < 36){
				compulsory_insure_price = 3020.0;
			}else {
				compulsory_insure_price = 3140.0;
			}
		}else if(car_use_type == 6){//营业客车-公路客运
			if(seats < 10){
				compulsory_insure_price = 2350.0;
			}else if(seats < 20){
				compulsory_insure_price = 2620.0;
			}else if(seats < 36){
				compulsory_insure_price = 3420.0;
			}else {
				compulsory_insure_price = 4690.0;
			}
		}
		
		return Double.valueOf(formatter.format(compulsory_insure_price));
	}
	
	/**
	 * @description 车船税计算
	 * @date 2016年9月10日 下午6:04:00
	 * @param formatter 金额格式化formatter
	 * @param car_discharge  排量 1.0L(含)以下    1.0-1.6L（含）  1.6-2.0L（含） 2.0-2.5L（含） 2.5-3.0L（含） 3.0-4.0L（含）4.0L以上
	 * @return
	 */
	public static double getTravelTax(DecimalFormat formatter, String car_discharge) {
		double travel_tax = 0.0;
		if(!StringUtil.isEmpty(car_discharge)){
			double discharge = Double.valueOf(car_discharge.trim().replaceAll("L", "").replaceAll("T", ""));
			if(discharge <= 1.0){//1.0L(含)以下
				travel_tax = 250.0;
			}else if(discharge <= 1.6){//1.0-1.6L（含）
				travel_tax = 350.0;
			}else if(discharge <= 2.0){//1.6-2.0L（含）
				travel_tax = 400.0;
			}else if(discharge <= 2.5){//2.0-2.5L（含）
				travel_tax = 750.0;
			}else if(discharge <= 3.0){//2.5-3.0L（含）
				travel_tax = 1600.0;
			}else if(discharge <= 4.0){//3.0-4.0L（含）
				travel_tax = 2900.0;
			}else {//4.0L以上
				travel_tax = 4400.0;
			}
		}
		
		return Double.valueOf(formatter.format(travel_tax));
	}
}
