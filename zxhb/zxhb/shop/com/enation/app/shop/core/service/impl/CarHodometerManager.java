package com.enation.app.shop.core.service.impl;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.ConsumptionStatistics;
import com.enation.app.shop.core.model.CarInfo;
import com.enation.app.shop.core.model.CarModel;
import com.enation.app.shop.core.service.ICarHodometerManager;
import com.enation.app.shop.core.utils.DistanceUtils;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.DateUtils;
import com.enation.eop.sdk.utils.ValidateUtils;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;

/**
 * 车辆行程信息管理
 * @author wangqiang 2016年4月8日 下午3:05:04
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CarHodometerManager extends BaseSupport implements ICarHodometerManager {

	
	public static void main(String[] args) {
		/**
		double mile = 101.62;
		double totalmile = 3187;
		long carfirstbuytime = 1469548800000l;
		long onemonthTime = 1000 * 60 * 60 * 24 * 30l;//一个月时间的毫秒数
		long timediff = System.currentTimeMillis() - carfirstbuytime;
		long a = timediff / onemonthTime;
		double caragebymonth = (double)(a < 1 ? 1 : a); //根据车辆购买时间计算车龄 /月份
		double milepermonth = (totalmile / caragebymonth) < 1 ? 1 : (totalmile / caragebymonth);//计算上月平均里程
//System.out.println("milepermonth---"+milepermonth);
		milepermonth = milepermonth < 1500 ? 1500 : milepermonth;//设置每月最低平均行驶里程
		int fastaccelarationnum = 9;//急加速次数
		int slambrakenum = 5;//急减速次数
		long drivingtime = 4089000;//驾驶时长
		double drvtime = (double)drivingtime/(1*60*60*1000);
		int overdriving = drvtime > 4.0 ? (int)(drvtime - 3) : 0;//疲劳驾驶次数
		int maxspeed = 126;//最高车速
		int overspeed = maxspeed > 120 ? 1 : 0;
		int maxrevolution = 2808;//最大转速
		int maxrevolution_overtime = maxrevolution > 5000 ? 1 : 0;
		double insuremaxgainpermonth = 1447.3 / 12;//预计月最高收益
		
		//保险
		DecimalFormat df = new DecimalFormat("0.00");
		double insurereward = Double.valueOf(df.format(mile * insuremaxgainpermonth / milepermonth));
		double insurededuction = Double.valueOf(df.format((fastaccelarationnum + slambrakenum + (maxrevolution > 5000 ? 1 : 0) + (maxspeed > 120 ? 1 : 0) + overdriving) * 0.2));
//System.out.println("insurereward----"+insurereward+"---insurededuction---"+insurededuction);	
		
		//保养
		long nextmile = ((2313 % 5000) > (5000 / 2)) ? ((2313/5000) + 2)*5000 : ((2313/5000) + 1)*5000;
//System.out.println("nextmile----"+nextmile);
		double price = 354;
		double discountcontract = 0.75;
		double repairestimatedmaxgain = price * (1 - discountcontract);//预计保养最大收益
		
		double repairreward = Double.valueOf(df.format(repairestimatedmaxgain * mile / 5000));
		double repairdeduction = Double.valueOf(df.format((fastaccelarationnum + slambrakenum + (maxrevolution > 5000 ? 1 : 0) + (maxspeed > 120 ? 1 : 0) + overdriving) * 0.2));
//System.out.println("repairreward----"+repairreward+"---repairdeduction---"+repairdeduction);
		
		double score = 0;
		int radio = 0;
		if(mile <=5){
			score = 20 - mile;
			radio = 5;
		}else if(mile <= 20){
			score = 7.5;
			radio = 20;
		}else if(mile <= 30){
			score = 4;
			radio = 30;
		}else if(mile <= 50){
			score = 4;
			radio = 50;
		}else if(mile <= 100){
			score = 2;
			radio = 100;
		}else if(mile <= 200){
			score = 1.5;
			radio = 150;
		}else if(mile <= 500){
			score = 0.5;
			radio = 500;
		}else{
			score = 0.3;
			radio = 1;
		}
		double drivingscore = 100 - (fastaccelarationnum + slambrakenum)*score - (overdriving + (maxspeed > 120 ? 1 : 0))*5;//驾驶得分
//		drivingscore = drivingscore < 0 ? (int)Math.rint(5) : mile <= 500 ? drivingscore * mile / radio : drivingscore;
		drivingscore = drivingscore < 0 ? (int)Math.rint(5) : drivingscore;
//		int drivingscore = 100 - (fastaccelarationnum + slambrakenum)*2 - (overdriving + (maxspeed > 120 ? 1 : 0))*5;//驾驶得分
		System.out.println("drivingscore>>>>>>>"+drivingscore);
		String caraa="{gpsinfo:[{'dateTime':'1969-12-21 17:49:57','laitude':'121.127313628','longitude':'121.435932'}, {'dateTime':'1969-12-21 17:49:57','laitude':'121.127313628','longitude':'121.435932'}]}";
		CarHodometerManager car = new CarHodometerManager();
		car.generateGpsImg("");
		 **/
		
//		List dateList = getDateStringList("week");
//		System.out.println(dateList.toString());

		//116.324166371,40.071893524  max_lng
		//116.321640129,40.060345593  min_lat
		//116.316947214,40.066501245  min_lng
		//116.324166371,40.071893524  max_lat
		
		//116.324260716,40.072583844
		//116.316331274,40.069744597
		//116.31151548,40.075651596
		//116.314378041,40.076082518
//		double distance1 = DistanceUtils.getDistance(116.324166371, 40.071893524, 116.321640129, 40.060345593);
//		double distance2 = DistanceUtils.getDistance(116.316947214, 40.066501245, 116.324166371, 40.071893524);
//		System.out.println(distance1);
//		System.out.println(distance2);
//		double distance3 = DistanceUtils.getDistance(116.324260716,40.072583844, 116.316331274,40.069744597);
//		double distance4 = DistanceUtils.getDistance(116.31151548,40.075651596, 116.314378041,40.076082518);
//		System.out.println(distance3);
//		System.out.println(distance4);
	}

	private Object startdate;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject updateCarHodometer(String carHodometerJson) throws Exception{
		carHodometerJson=carHodometerJson.replace("=", ":");
		System.out.println("carHodometerJson " +carHodometerJson);
		JSONObject obj = new JSONObject(carHodometerJson);
		String gpsinfo = null;
		if(obj.has("gpsinfo")){
			gpsinfo = obj.get("gpsinfo").toString();
		}
		String deviceId = obj.getString("deviceId");
		String carPlate = null;
		if(!StringUtil.isEmpty(deviceId)){
			String sql1 = "select carplate from es_carinfo where obdmacaddr = '"+ deviceId+"'";
			carPlate = this.daoSupport.queryForString(sql1);
		}
		String sql;
		String insertSql;
		
		//生成行程轨迹缩略图，保存到数据库
		String currmile = obj.getDouble("mile")+"";
		String gps_imgurl = generateGpsImg(gpsinfo, currmile);
		obj.put("gps_imgurl", gps_imgurl);

		double insuretotalgain = 0.0;
		double repairtotalgain = 0.0;
		double totalgain = 0.0;
		long totalmile = obj.getLong("totalmile");
		long startTime = System.currentTimeMillis();
		JSONObject resultObj = new JSONObject();
		
		try {
			if (carPlate != null){
				sql = "SELECT c1.insureestimatedfee, c1.insureestimatedmaxgain, c1.totalmile, c1.car_register_time, c1.repairlastmile, c1.carmodelid,"
					+ " c2.repairinterval"
					+ " FROM es_carinfo c1, es_carmodels c2 WHERE c1.carmodelid=c2.id and c1.carplate=?";
				List resultList = this.daoSupport.queryForList(sql, carPlate);
				net.sf.json.JSONObject jsonObj = JSONArray.fromObject(resultList).getJSONObject(0);
				totalmile = jsonObj.getLong("totalmile");
				double mile = obj.getDouble("mile");//本次行驶里程
				double oilconsumption = obj.getDouble("oilconsumption");//本次行程的耗油量
				
				//add a new hodometer
				/*insertSql = "insert into es_hodometer set ";
				Iterator<String> keys = obj.keys();
				while(keys.hasNext()) {
					String key = keys.next();
					sql += key + "='" + obj.get(key).toString() + "'";
					if (keys.hasNext())
						sql += ",";
				}*/
				/**
				 * 计算保险相关收益 insurereward 保险安全奖励，insurededuction 保险危险扣除
				 */
				double insurereward = 0.0;//保险安全奖励
				double insurededuction = 0.0;//保险危险扣除
				double insuremaxgainpermonth = jsonObj.getDouble("insureestimatedmaxgain") / 12;//预计月最高收益
				long onemonthTime = 1000 * 60 * 60 * 24 * 30l;//一个月时间的毫秒数
				long carfirstbuytime = jsonObj.getLong("car_register_time");//首次购买时间
				long timediff = System.currentTimeMillis() - carfirstbuytime;
				long a = timediff / onemonthTime;
				double caragebymonth = (double)(a < 1 ? 1 : a); //根据车辆购买时间计算车龄 /月份
				double milepermonth = (totalmile / caragebymonth) < 1 ? 1 : (totalmile / caragebymonth);//计算上月平均里程
				milepermonth = milepermonth < 1500 ? 1500 : milepermonth;//设置每月最低平均行驶里程
				int fastaccelarationnum = 0;
				if(obj.has("fastaccelarationnum")){
					fastaccelarationnum = obj.getInt("fastaccelarationnum");//急加速次数
				}
				int slambrakenum = 0;
				if(obj.has("slambrakenum")){
					slambrakenum = obj.getInt("slambrakenum");//急减速次数
				}
				long drivingtime = 4089000;//驾驶时长
				double drvtime = (double)drivingtime/(1*60*60*1000);
				int overdriving = drvtime > 4.0 ? (int)(drvtime - 3) : 0;//疲劳驾驶次数
				int maxspeed = 0;
				if(obj.has("maxspeed")){
					maxspeed = obj.getInt("maxspeed");//最高车速
				}
				int overspeed = maxspeed > 120 ? 1 : 0;
				int maxrevolution = 0;
				if(obj.has("maxrevolution")){
					maxrevolution = obj.getInt("maxrevolution");//最大转速
				}
				int maxrevolution_overtime = maxrevolution > 5000 ? 1 : 0;
				
				DecimalFormat df = new DecimalFormat("0.00");
				insurereward = Double.valueOf(df.format(mile * insuremaxgainpermonth / milepermonth));
				insurededuction = Double.valueOf(df.format((fastaccelarationnum + slambrakenum + (maxrevolution > 5000 ? 1 : 0) + (maxspeed > 120 ? 1 : 0) + overdriving) * 0.2));
				
				/**
				 * 计算保养相关收益 repairreward 保养安全奖励，repairdeduction 保养危险扣除
				 */
				double repairreward = 0.0;//保养安全奖励
				double repairdeduction = 0.0;//保养危险扣除
				long repairlastmile = jsonObj.getInt("repairlastmile");//上次保养里程
				long repairinterval = jsonObj.getInt("repairinterval");//保养间隔里程
				String carmodelid = jsonObj.getString("carmodelid");//车型ID
				
//				String sql2 = "select repairlastmile_updatetime from es_carinfo where carplate=?";
//				long repairlastmile_updatetime = daoSupport.queryForLong(sql2, carPlate);
//				sql2 = "select count(*) from es_hodometer where carplate=? AND starttime >= ?";
//				int count = daoSupport.queryForInt(sql2, carPlate, repairlastmile_updatetime);
//				if(count == 0){//在更改上次保养里程后第一次上传行程时，对获取到的车辆总里程和用户填写的上次保养里程做校验
//					if(repairlastmile > totalmile){
//						repairlastmile = totalmile;
//					}
//				}
				sql = "select obdmacaddr from es_carinfo where carplate='"+ carPlate +"'";
				String obdmacaddr = daoSupport.queryForString(sql);
				if(StringUtil.isEmpty(obdmacaddr) || StringUtil.isNull(obdmacaddr)){
					resultObj.put("result", 0);
					resultObj.put("message", "该车牌号未绑定obd设备，请检查");
				}else{
					//计算下次保养里程，获取下次保养价格
					long nextmile = ((repairlastmile % repairinterval) > (repairinterval / 2)) ? ((repairlastmile/repairinterval) + 2)*repairinterval : ((repairlastmile/repairinterval) + 1)*repairinterval;
					nextmile = nextmile > CarModel.max_mile ? CarModel.max_mile : nextmile;
//					sql2 = "select price from es_repairintervalitem where carmodelid=? and repairinterval=?";
//					long price = this.daoSupport.queryForLong(sql2, carmodelid, nextmile);//获取下次保养价格
					double repairestimatedmaxgain = 0;//预计保养最大收益
					
					repairreward = Double.valueOf(df.format(repairestimatedmaxgain * mile / repairinterval));
					repairdeduction = Double.valueOf(df.format((fastaccelarationnum + slambrakenum + (maxrevolution > 5000 ? 1 : 0) + (maxspeed > 120 ? 1 : 0) + overdriving) * 0.2));
					repairreward = 0;
					repairdeduction = 0;
					
					//将计算后的收益等数据添加到行程表中
					double insuregain = Double.valueOf(df.format(insurereward - insurededuction));//本次行程保险收益
					double repairgain = Double.valueOf(df.format(repairreward - repairdeduction));//本次行程保养收益
					double reward = insurereward + repairreward;//本次行程安全驾驶奖励
					double deduction = insurededuction + repairdeduction;//本次行程危险驾驶扣除
					int drivingscore = 100 - (fastaccelarationnum + slambrakenum)*2 - (overdriving + (maxspeed > 120 ? 1 : 0))*5;//驾驶得分
					drivingscore = drivingscore < 0 ? 0 : drivingscore;
					String carstatus = "Ⅰ级";
					String drivingaction = drivingscore > 90 ? "Ⅰ级" : "Ⅱ级";
					insertSql = "insert into es_hodometer set carplate=?,starttime=?,totalmile=?,mile=?,oilconsumption=?,maxspeed=?,maxrevolution=?,fastaccelarationnum=?,slambrakenum=?,gpsinfo=?,gps_imgurl=?,"
							  + "overdriving=?, overspeed=?, maxrevolution_overtime=?, insuregain=?, repairgain=?, reward=?, deduction=?, drivingscore=?, carstatus=?, drivingaction=?";
					this.logger.debug("updateCarHodometer: " + insertSql);
					this.baseDaoSupport.execute(insertSql,carPlate,startTime,totalmile,mile,oilconsumption,maxspeed,maxrevolution,fastaccelarationnum,slambrakenum,gpsinfo,gps_imgurl,
							overdriving, overspeed, maxrevolution_overtime, insuregain, repairgain, reward, deduction, drivingscore, carstatus, drivingaction);
					
					//更新保险和保养收益到车辆信息表es_carinfo中的保险总收益、保养总收益、总收益、总里程
					sql = "select insuretotalgain, repairtotalgain, totalgain from es_carinfo where carplate=?";
					List gainList = this.baseDaoSupport.queryForList(sql, carPlate);
					net.sf.json.JSONObject jObj = JSONArray.fromObject(gainList).getJSONObject(0);
					insuretotalgain += jObj.get("insuretotalgain") == null ? 0.0 : jObj.getDouble("insuretotalgain") + insuregain;
					repairtotalgain += jObj.get("repairtotalgain") == null ? 0.0 : jObj.getDouble("repairtotalgain") + repairgain;
					double currtotalgain = Double.valueOf(df.format(insuregain + repairgain));//本次行程总收益
					totalgain = jObj.get("totalgain") == null ? currtotalgain : jObj.getDouble("totalgain") + currtotalgain;
					totalmile += mile;
					sql = "update es_carinfo set insuretotalgain=?, repairtotalgain=?, totalgain=?, totalmile=?, repairlastmile=? where carplate=?";
					this.daoSupport.execute(sql, df.format(insuretotalgain), df.format(repairtotalgain), totalgain, totalmile, repairlastmile, carPlate);
					
					//记录当前获取到收益历史记录表
					int hodometerId = daoSupport.getLastId("es_hodometer");
					String detail = "行驶里程"+ mile +"公里，其中奖励"+ reward +"元，危险扣除"+ deduction +"元，总累计"+ currtotalgain +"元";
					sql = "INSERT INTO es_car_gain_history SET TYPE=?, query_id=?, carplate=?, reward=?, timeline=?, detail=?";
					daoSupport.execute(sql, CarInfo.DRIVING_INCOME, hodometerId, carPlate, currtotalgain, new Date().getTime(), detail);

					//统计油耗数据
					String nowDate = DateUtils.getNowDate();
					int result = updateConsumptionData(nowDate,carPlate);
					if(result != 1) {
						logger.info("油耗数据统计出现错误");
						throw new RuntimeException("油耗数据统计失败");
					}
					
					JSONObject returnObj = new JSONObject();
					returnObj.put("totalmile", totalmile);
					returnObj.put("insuretotalgain", insuretotalgain);
					returnObj.put("repairtotalgain", repairtotalgain);
					returnObj.put("totalgain", totalgain);
					
					resultObj.put("result", 1);
					resultObj.put("data", returnObj);
				}
			}
			return resultObj;
		}  catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
	}
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject uploadNormalAndRetryCarHodometer(String carHodometer) throws Exception {
		try {
			double insuretotalgain = 0.0;
			double repairtotalgain = 0.0;
			double totalgain = 0.0;
			long totalmile = 0;
			
			DecimalFormat df = new DecimalFormat("0.00");
			JSONObject travelObj = new JSONObject(carHodometer);
			JSONArray hodometerArray = JSONArray.fromObject(travelObj.getString("travel_list"));
			for(int i=0; i<hodometerArray.size(); i++){
				net.sf.json.JSONObject obj = net.sf.json.JSONObject.fromObject(hodometerArray.get(i));

				String carPlate = obj.getString("carplate");
				String gpsinfo = obj.getString("gpsinfo");
//				totalmile = obj.getLong("totalmile");
				
				String currtmile = obj.getString("mile");
				String gps_imgurl = generateGpsImg(gpsinfo, currtmile);//生成行程轨迹缩略图，保存到数据库
				obj.put("gps_imgurl", gps_imgurl);
				
				String sql;
				
				if (carPlate != null){
					sql = "SELECT c1.insureestimatedfee, c1.insureestimatedmaxgain, c1.totalmile, c1.car_register_time, c1.repairlastmile, c1.carmodelid,"
						+ " c2.repairinterval"
						+ " FROM es_carinfo c1, es_carmodels c2 WHERE c1.carmodelid=c2.id and c1.carplate=?";
					List resultList = this.daoSupport.queryForList(sql, carPlate);
					net.sf.json.JSONObject jsonObj = JSONArray.fromObject(resultList).getJSONObject(0);
					double mile = obj.getDouble("mile");//本次行驶里程
					totalmile = jsonObj.getLong("totalmile");
					
					//add a new hodometer
					sql = "insert into es_hodometer set ";
					sql += "carplate='" + obj.getString("carplate") + "',";
					if(!StringUtil.isNull(obj.getString("drivingtime"))){
						sql += "drivingtime=" + obj.getString("drivingtime") + ",";
					}
					if(!StringUtil.isNull(obj.getString("fastaccelarationnum"))){
						sql += "fastaccelarationnum=" + obj.getString("fastaccelarationnum") + ",";
					}
					if(!StringUtil.isNull(obj.getString("gpsinfo"))){
						sql += "gpsinfo='" + obj.getString("gpsinfo") + "',";
					}
					if(!StringUtil.isNull(obj.getString("idlingoilconsumption"))){
						sql += "idlingoilconsumption=" + obj.getString("idlingoilconsumption") + ",";
					}
					if(!StringUtil.isNull(obj.getString("idlingtime"))){
						sql += "idlingtime=" + obj.getString("idlingtime") + ",";
					}
					if(!StringUtil.isNull(obj.getString("maxrevolution"))){
						sql += "maxrevolution=" + obj.getString("maxrevolution") + ",";
					}
					if(!StringUtil.isNull(obj.getString("maxspeed"))){
						sql += "maxspeed=" + obj.getString("maxspeed") + ",";
					}
					if(!StringUtil.isNull(obj.getString("mile"))){
						sql += "mile=" + obj.getString("mile") + ",";
					}
					if(!StringUtil.isNull(obj.getString("oilconsumption"))){
						sql += "oilconsumption=" + obj.getString("oilconsumption") + ",";
					}
					if(!StringUtil.isNull(obj.getString("slambrakenum"))){
						sql += "slambrakenum=" + obj.getString("slambrakenum") + ",";
					}
					if(!StringUtil.isNull(obj.getString("starttime"))){
						sql += "starttime=" + obj.getString("starttime") + ",";
					}
					if(!StringUtil.isNull(obj.getString("gps_imgurl"))){
						sql += "gps_imgurl='" + obj.getString("gps_imgurl") + "'";
					}
					
					/**
					 * 计算保险相关收益 insurereward 保险安全奖励，insurededuction 保险危险扣除
					 */
					double insurereward = 0.0;//保险安全奖励
					double insurededuction = 0.0;//保险危险扣除
					double insuremaxgainpermonth = jsonObj.getDouble("insureestimatedmaxgain") / 12;//预计月最高收益
					long onemonthTime = 1000 * 60 * 60 * 24 * 30l;//一个月时间的毫秒数
					long carfirstbuytime = jsonObj.getLong("car_register_time");//首次购买时间
					long timediff = System.currentTimeMillis() - carfirstbuytime;
					long a = timediff / onemonthTime;
					double caragebymonth = (double)(a < 1 ? 1 : a); //根据车辆购买时间计算车龄 /月份
					double milepermonth = (totalmile / caragebymonth) < 1 ? 1 : (totalmile / caragebymonth);//计算上月平均里程
					milepermonth = milepermonth < 1500 ? 1500 : milepermonth;//设置每月最低平均行驶里程
					int fastaccelarationnum = obj.getInt("fastaccelarationnum");//急加速次数
					int slambrakenum = obj.getInt("slambrakenum");//急减速次数
					long drivingtime = obj.getLong("drivingtime");//驾驶时长
					double drvtime = (double)drivingtime/(1*60*60*1000);
					int overdriving = drvtime > 4.0 ? (int)(drvtime - 3) : 0;//疲劳驾驶次数
					int maxspeed = obj.getInt("maxspeed");//最高车速
					int overspeed = maxspeed > 120 ? 1 : 0;
					int maxrevolution = obj.getInt("maxrevolution");//最大转速
					int maxrevolution_overtime = maxrevolution > 5000 ? 1 : 0;
					
					insurereward = Double.valueOf(df.format(mile * insuremaxgainpermonth / milepermonth));
					insurededuction = Double.valueOf(df.format((fastaccelarationnum + slambrakenum + (maxrevolution > 5000 ? 1 : 0) + (maxspeed > 120 ? 1 : 0) + overdriving) * 0.2));
					
					/**
					 * 计算保养相关收益 repairreward 保养安全奖励，repairdeduction 保养危险扣除
					 */
					double repairreward = 0.0;//保养安全奖励
					double repairdeduction = 0.0;//保养危险扣除
					long repairlastmile = jsonObj.getInt("repairlastmile");//上次保养里程
					long repairinterval = jsonObj.getInt("repairinterval");//保养间隔里程
					String carmodelid = jsonObj.getString("carmodelid");//车型ID
					
					String sql2 = "select repairlastmile_updatetime from es_carinfo where carplate=?";
					long repairlistmile_updatetime = daoSupport.queryForLong(sql2, carPlate);
					sql2 = "select count(*) from es_hodometer where carplate=? AND starttime >= ?";
					int count = daoSupport.queryForInt(sql2, carPlate, repairlistmile_updatetime);
					if(count == 0){//在更改上次保养里程后第一次上传行程时，对获取到的车辆总里程和用户填写的上次保养里程做校验
						if(repairlastmile > totalmile){
							repairlastmile = totalmile;
						}
					}
					//计算下次保养里程，获取下次保养价格
					long nextmile = ((repairlastmile % repairinterval) > (repairinterval / 2)) ? ((repairlastmile/repairinterval) + 2)*repairinterval : ((repairlastmile/repairinterval) + 1)*repairinterval;
//					sql2 = "select price from es_repairintervalitem where carmodelid=? and repairinterval=?";
//					long price = this.daoSupport.queryForLong(sql2, carmodelid, nextmile);//获取下次保养价格
					double repairestimatedmaxgain = 0;//预计保养最大收益
					
					repairreward = Double.valueOf(df.format(repairestimatedmaxgain * mile / repairinterval));
					repairdeduction = Double.valueOf(df.format((fastaccelarationnum + slambrakenum + (maxrevolution > 5000 ? 1 : 0) + (maxspeed > 120 ? 1 : 0) + overdriving) * 0.2));
					repairreward = 0;
					repairdeduction = 0;
					
					//将计算后的收益等数据添加到行程表中
					double insuregain = Double.valueOf(df.format(insurereward - insurededuction));//本次行程保险收益
					double repairgain = Double.valueOf(df.format(repairreward - repairdeduction));//本次行程保养收益
					double reward = insurereward + repairreward;//本次行程安全驾驶奖励
					double deduction = insurededuction + repairdeduction;//本次行程危险驾驶扣除
					int drivingscore = 100 - (fastaccelarationnum + slambrakenum)*2 - (overdriving + (maxspeed > 120 ? 1 : 0))*5;//驾驶得分
					drivingscore = drivingscore < 0 ? 0 : drivingscore;
					String carstatus = "Ⅰ级";
					String drivingaction = drivingscore > 90 ? "Ⅰ级" : "Ⅱ级";
					sql += ", overdriving=?, overspeed=?, maxrevolution_overtime=?, insuregain=?, repairgain=?, reward=?, deduction=?, drivingscore=?, carstatus=?, drivingaction=?";
					this.logger.debug("updateCarHodometer: " + sql);

					this.baseDaoSupport.execute(sql, overdriving, overspeed, maxrevolution_overtime, insuregain, repairgain, reward, deduction, drivingscore, carstatus, drivingaction);
					
					//更新保险和保养收益到车辆信息表es_carinfo中的保险总收益、保养总收益、总收益、总里程
					sql = "select insuretotalgain, repairtotalgain, totalgain from es_carinfo where carplate=?";
					List gainList = this.baseDaoSupport.queryForList(sql, carPlate);
					net.sf.json.JSONObject jObj = JSONArray.fromObject(gainList).getJSONObject(0);
					insuretotalgain += jObj.get("insuretotalgain") == null ? 0.0 : jObj.getDouble("insuretotalgain") + insuregain;
					repairtotalgain += jObj.get("repairtotalgain") == null ? 0.0 : jObj.getDouble("repairtotalgain") + repairgain;
					double currtotalgain = Double.valueOf(df.format(insuregain + repairgain));//本次行程总收益
					totalgain = jObj.get("totalgain") == null ? currtotalgain : jObj.getDouble("totalgain") + currtotalgain;
					totalmile += mile;
					sql = "update es_carinfo set insuretotalgain=?, repairtotalgain=?, totalgain=?, totalmile=?, repairlastmile=? where carplate=?";
					this.daoSupport.execute(sql, df.format(insuretotalgain), df.format(repairtotalgain), totalgain, totalmile, repairlastmile, carPlate);

					//统计油耗数据
//					String nowDate = DateUtils.getNowDate();
//					int result = updateConsumptionData(nowDate,carPlate);
//					if(result != 1) {
//						logger.info("油耗数据统计出现错误");
//						throw new RuntimeException("油耗数据统计失败");
//					}
					
					JSONObject returnObj = new JSONObject();
					returnObj.put("totalmile", totalmile);
					returnObj.put("insuretotalgain", insuretotalgain);
					returnObj.put("repairtotalgain", repairtotalgain);
					returnObj.put("totalgain", totalgain);
					
					travelObj.put("result", 1);
					travelObj.put("data", returnObj);
				}
			}
			return travelObj;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
	}
	
	@Override
	public void getHodometerImage() {
		try {
			//测试url：localhost:8080/mall/api/mobile/carhodometer!getHodometerImage.do
			String sql = "select gpsinfo from es_hodometer where id=243";
			String gpsinfo = daoSupport.queryForString(sql);
			generateGpsImg(gpsinfo, "0.9");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}


	private String generateGpsImg(String gpsinfo, String currmile) {
		try {
//			StringBuilder json = new StringBuilder();
//			json.append("[{\"latitude\":39.921573,\"longitude\":116.517159,\"gps_time\":\"2016-07-29 16:37\"},");
//			json.append("{\"latitude\":39.921546,\"longitude\":116.517097,\"gps_time\":\"2016-07-29 16:39\"},");
//			json.append("{\"latitude\":39.921915,\"longitude\":116.517056,\"gps_time\":\"2016-07-29 16:40\"},");
//			json.append("{\"latitude\":39.922631,\"longitude\":116.521926,\"gps_time\":\"2016-07-29 16:41\"},");
//			json.append("{\"latitude\":39.922755,\"longitude\":116.523566,\"gps_time\":\"2016-07-29 16:42\"},");
//			json.append("{\"latitude\":39.922797,\"longitude\":116.524037,\"gps_time\":\"2016-07-29 16:43\"},");
//			json.append("{\"latitude\":39.922794,\"longitude\":116.524138,\"gps_time\":\"2016-07-29 16:44\"},");
//			json.append("{\"latitude\":39.922919,\"longitude\":116.522969,\"gps_time\":\"2016-07-29 16:45\"},");
//			json.append("{\"latitude\":39.922314,\"longitude\":116.514881,\"gps_time\":\"2016-07-29 16:46\"},");
//			json.append("{\"latitude\":39.921932,\"longitude\":116.505322,\"gps_time\":\"2016-07-29 16:47\"},");
//			json.append("{\"latitude\":39.921847,\"longitude\":116.503833,\"gps_time\":\"2016-07-29 16:48\"},");
//			json.append("{\"latitude\":39.921644,\"longitude\":116.49774,\"gps_time\":\"2016-07-29 16:49\"},");
//			json.append("{\"latitude\":39.92155,\"longitude\":116.497574,\"gps_time\":\"2016-07-29 16:50\"},");
//			json.append("{\"latitude\":39.922492,\"longitude\":116.496614,\"gps_time\":\"2016-07-29 16:51\"},");
//			json.append("{\"latitude\":39.926572,\"longitude\":116.496791,\"gps_time\":\"2016-07-29 16:52\"},");
//			json.append("{\"latitude\":39.926556,\"longitude\":116.496593,\"gps_time\":\"2016-07-29 16:53\"},");
//			json.append("{\"latitude\":39.926587,\"longitude\":116.496728,\"gps_time\":\"2016-07-29 16:54\"},");
//			json.append("{\"latitude\":39.929444,\"longitude\":116.49662,\"gps_time\":\"2016-07-29 16:55\"},");
//			json.append("{\"latitude\":39.93441,\"longitude\":116.496542,\"gps_time\":\"2016-07-29 16:56\"},");
//			json.append("{\"latitude\":39.942808,\"longitude\":116.49653,\"gps_time\":\"2016-07-29 16:57\"},");
//			json.append("{\"latitude\":39.949087,\"longitude\":116.496457,\"gps_time\":\"2016-07-29 16:58\"},");
//			json.append("{\"latitude\":39.955642,\"longitude\":116.496497,\"gps_time\":\"2016-07-29 16:59\"},");
//			json.append("{\"latitude\":39.963406,\"longitude\":116.494566,\"gps_time\":\"2016-07-29 17:00\"},");
//			json.append("{\"latitude\":39.966888,\"longitude\":116.491424,\"gps_time\":\"2016-07-29 17:01\"},");
//			json.append("{\"latitude\":39.969247,\"longitude\":116.48818,\"gps_time\":\"2016-07-29 17:02\"},");
//			json.append("{\"latitude\":39.972136,\"longitude\":116.483802,\"gps_time\":\"2016-07-29 17:03\"},");
//			json.append("{\"latitude\":39.975558,\"longitude\":116.478529,\"gps_time\":\"2016-07-29 17:04\"},");
//			json.append("{\"latitude\":39.978833,\"longitude\":116.473462,\"gps_time\":\"2016-07-29 17:05\"},");
//			json.append("{\"latitude\":39.980365,\"longitude\":116.471125,\"gps_time\":\"2016-07-29 17:06\"},");
//			json.append("{\"latitude\":39.983413,\"longitude\":116.466562,\"gps_time\":\"2016-07-29 17:07\"},");
//			json.append("{\"latitude\":39.988548,\"longitude\":116.458865,\"gps_time\":\"2016-07-29 17:08\"},");
//			json.append("{\"latitude\":39.992371,\"longitude\":116.452053,\"gps_time\":\"2016-07-29 17:09\"},");
//			json.append("{\"latitude\":39.994949,\"longitude\":116.445008,\"gps_time\":\"2016-07-29 17:10\"},");
//			json.append("{\"latitude\":39.995278,\"longitude\":116.44165,\"gps_time\":\"2016-07-29 17:11\"},");
//			json.append("{\"latitude\":39.995218,\"longitude\":116.432815,\"gps_time\":\"2016-07-29 17:12\"},");
//			json.append("{\"latitude\":39.995174,\"longitude\":116.428653,\"gps_time\":\"2016-07-29 17:13\"},");
//			json.append("{\"latitude\":39.995086,\"longitude\":116.424472,\"gps_time\":\"2016-07-29 17:14\"},");
//			json.append("{\"latitude\":39.99508,\"longitude\":116.422634,\"gps_time\":\"2016-07-29 17:15\"},");
//			json.append("{\"latitude\":39.994977,\"longitude\":116.418106,\"gps_time\":\"2016-07-29 17:16\"},");
//			json.append("{\"latitude\":39.994996,\"longitude\":116.413487,\"gps_time\":\"2016-07-29 17:17\"},");
//			json.append("{\"latitude\":39.995,\"longitude\":116.40937,\"gps_time\":\"2016-07-29 17:18\"},");
//			json.append("{\"latitude\":39.994907,\"longitude\":116.407026,\"gps_time\":\"2016-07-29 17:19\"},");
//			json.append("{\"latitude\":39.99481,\"longitude\":116.403936,\"gps_time\":\"2016-07-29 17:20\"},");
//			json.append("{\"latitude\":39.994896,\"longitude\":116.401863,\"gps_time\":\"2016-07-29 17:21\"},");
//			json.append("{\"latitude\":39.994205,\"longitude\":116.400051,\"gps_time\":\"2016-07-29 17:22\"},");
//			json.append("{\"latitude\":39.994638,\"longitude\":116.397382,\"gps_time\":\"2016-07-29 17:23\"},");
//			json.append("{\"latitude\":39.994489,\"longitude\":116.392077,\"gps_time\":\"2016-07-29 17:24\"},");
//			json.append("{\"latitude\":39.994296,\"longitude\":116.388852,\"gps_time\":\"2016-07-29 17:25\"},");
//			json.append("{\"latitude\":39.995498,\"longitude\":116.384876,\"gps_time\":\"2016-07-29 17:26\"},");
//			json.append("{\"latitude\":39.998236,\"longitude\":116.383864,\"gps_time\":\"2016-07-29 17:27\"},");
//			json.append("{\"latitude\":40.001696,\"longitude\":116.38148,\"gps_time\":\"2016-07-29 17:28\"},");
//			json.append("{\"latitude\":40.004253,\"longitude\":116.379333,\"gps_time\":\"2016-07-29 17:29\"},");
//			json.append("{\"latitude\":40.009297,\"longitude\":116.375435,\"gps_time\":\"2016-07-29 17:30\"},");
//			json.append("{\"latitude\":40.016388,\"longitude\":116.370119,\"gps_time\":\"2016-07-29 17:31\"},");
//			json.append("{\"latitude\":40.019878,\"longitude\":116.367437,\"gps_time\":\"2016-07-29 17:32\"},");
//			json.append("{\"latitude\":40.022487,\"longitude\":116.365356,\"gps_time\":\"2016-07-29 17:33\"},");
//			json.append("{\"latitude\":40.024904,\"longitude\":116.363498,\"gps_time\":\"2016-07-29 17:34\"},");
//			json.append("{\"latitude\":40.027065,\"longitude\":116.361798,\"gps_time\":\"2016-07-29 17:35\"},");
//			json.append("{\"latitude\":40.028305,\"longitude\":116.360843,\"gps_time\":\"2016-07-29 17:36\"},");
//			json.append("{\"latitude\":40.029664,\"longitude\":116.359741,\"gps_time\":\"2016-07-29 17:37\"},");
//			json.append("{\"latitude\":40.030837,\"longitude\":116.358793,\"gps_time\":\"2016-07-29 17:38\"},");
//			json.append("{\"latitude\":40.032973,\"longitude\":116.357125,\"gps_time\":\"2016-07-29 17:39\"},");
//			json.append("{\"latitude\":40.038054,\"longitude\":116.353189,\"gps_time\":\"2016-07-29 17:40\"},");
//			json.append("{\"latitude\":40.045371,\"longitude\":116.347536,\"gps_time\":\"2016-07-29 17:41\"},");
//			json.append("{\"latitude\":40.053837,\"longitude\":116.341116,\"gps_time\":\"2016-07-29 17:42\"},");
//			json.append("{\"latitude\":40.063616,\"longitude\":116.333594,\"gps_time\":\"2016-07-29 17:43\"},");
//			json.append("{\"latitude\":40.072997,\"longitude\":116.326399,\"gps_time\":\"2016-07-29 17:44\"},");
//			json.append("{\"latitude\":40.077362,\"longitude\":116.323288,\"gps_time\":\"2016-07-29 17:45\"},");
//			json.append("{\"latitude\":40.077557,\"longitude\":116.323247,\"gps_time\":\"2016-07-29 17:46\"},");
//			json.append("{\"latitude\":40.078833,\"longitude\":116.322558,\"gps_time\":\"2016-07-29 17:47\"},");
//			json.append("{\"latitude\":40.080782,\"longitude\":116.325517,\"gps_time\":\"2016-07-29 17:48\"},");
//			json.append("{\"latitude\":40.081242,\"longitude\":116.327304,\"gps_time\":\"2016-07-29 17:49\"},");
//			json.append("{\"latitude\":40.081774,\"longitude\":116.329301,\"gps_time\":\"2016-07-29 17:50\"},");
//			json.append("{\"latitude\":40.081776,\"longitude\":116.3293,\"gps_time\":\"2016-07-29 17:51\"},");
//			json.append("{\"latitude\":40.080776,\"longitude\":116.325072,\"gps_time\":\"2016-07-29 17:52\"},");
//			json.append("{\"latitude\":40.07957,\"longitude\":116.320905,\"gps_time\":\"2016-07-29 17:53\"},");
//			json.append("{\"latitude\":40.081409,\"longitude\":116.318954,\"gps_time\":\"2016-07-29 17:54\"},");
//			json.append("{\"latitude\":40.078115,\"longitude\":116.32189,\"gps_time\":\"2016-07-29 17:55\"},");
//			json.append("{\"latitude\":40.073503,\"longitude\":116.325465,\"gps_time\":\"2016-07-29 17:56\"},");
//			json.append("{\"latitude\":40.074109,\"longitude\":116.323336,\"gps_time\":\"2016-07-29 17:57\"},");
//			json.append("{\"latitude\":40.073509,\"longitude\":116.320109,\"gps_time\":\"2016-07-29 17:58\"},");
//			json.append("{\"latitude\":40.0735,\"longitude\":116.320315,\"gps_time\":\"2016-07-29 17:59\"}]");
//			JSONArray array = JSONArray.fromObject(json.toString());
			JSONArray oldarray = JSONArray.fromObject(gpsinfo);
			
			//gps点经纬度过滤
			oldarray = gpsinfoFilter(oldarray, Double.valueOf(currmile));
			
			//gps点个数过滤
			int max_size = 316;//最大gps轨迹点数量
			
			int length = oldarray.size();
			JSONArray array = new JSONArray();
			array.add(oldarray.getJSONObject(0));//添加起点
			int step = 0;
			if(length > max_size){
				step = length % max_size == 0 ? length / max_size - 1 : length / max_size;
			}
			for(int i=1; i<oldarray.size()-1; i++){
				i += step;
				if(i+step < oldarray.size()-1){
					array.add(oldarray.getJSONObject(i));
				}
			}
			array.add(oldarray.get(oldarray.size() - 1));//添加终点
		
			//确定图片缩放级别，生成图片
    		net.sf.json.JSONObject orignObject = array.getJSONObject(0);
    		double orign_lng = orignObject.getDouble("longitude");
    		double orign_lat = orignObject.getDouble("latitude");
    		double max_distance = 0.0;//行驶轨迹的最大直线距离
			String ak = "lAcvDh3Ipl1RBSbViH2HxQcog2TYeYG0";
			String mcode = "B5:1C:08:EF:BB:E1:1D:30:1E:E5:0C:2D:DF:EF:2E:B3:68:9C:01:5C;com.obdpay.obdpay";
			String paths = "";
			double max_lng = array.getJSONObject(0).getDouble("longitude");
			double min_lng = array.getJSONObject(0).getDouble("longitude");
			double max_lat = array.getJSONObject(0).getDouble("latitude");
			double min_lat = array.getJSONObject(0).getDouble("latitude");
			for(int i=1; i<array.size(); i++){
				net.sf.json.JSONObject obj = array.getJSONObject(i);
				double lng = obj.getDouble("longitude");
    			double lat = obj.getDouble("latitude");
    			double distance = DistanceUtils.getDistance(orign_lng, orign_lat, lng, lat);
    			if(max_lng < lng){
    				max_lng = lng;
    			}
    			if(min_lng > lng){
    				min_lng = lng;
    			}
    			if(max_lat < lat){
    				max_lat = lat;
    			}
    			if(min_lat > lat){
    				min_lat = lat;
    			}
    			if(max_distance < distance){
    				max_distance = distance;
    			}
				if(i == array.size() - 1){
					paths += obj.getString("longitude") +","+ obj.getString("latitude");
				}else{
					paths += obj.getString("longitude") +","+ obj.getString("latitude")+";";
				}
			}

			double avg_lng = (max_lng + min_lng) / 2;
			double avg_lat = (max_lat + min_lat) / 2;
			String center = avg_lng +","+ avg_lat;
			long mile = (long) Math.ceil(max_distance / 1000);//根据行驶最大距离确定图片缩放级别
    		int zoom = 3;
    		if(mile <= 1){
    			zoom = 18;
    		}else if(mile <=2){
    			zoom = 17;
    		}else if(mile <= 5){
    			zoom = 16;
    		}else if(mile <= 10){
    			zoom = 15;
    		}else if(mile <= 20){
    			zoom = 14;
    		}else if(mile <= 25){
    			zoom = 13;
    		}else if(mile <= 50){
    			zoom = 12;
    		}else if(mile <= 100){
    			zoom = 11;
    		}else if(mile <= 200){
    			zoom = 10;
    		}else if(mile <= 500){
    			zoom = 9;
    		}else if(mile <= 1000){
    			zoom = 8;
    		}else{
    			zoom = 7;
    		}
    		zoom = zoom - 2;
			String requestURL = "http://api.map.baidu.com/staticimage/v2?ak="+ak+"&mcode="+mcode+"&width=800&height=400&center="+center+"&zoom="+zoom+"&paths="+paths+"&pathStyles=0x000fff,5,1";
			HttpClient client = new HttpClient();  
	        GetMethod method = new GetMethod(requestURL);
	        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8"); 
	        // 设置请求包头文件  
	        method.setRequestHeader("Content-Type", "text/xml;charset=utf-8");  
	        client.executeMethod(method);  
	        InputStream in = method.getResponseBodyAsStream();
	        String fileName = DateUtil.toString(new Date(), "yyyyMMddHHmmss") + StringUtil.getRandStr(4) + ".jpg";
	        String file_path = SystemSetting.getFile_path();
			String filePath = file_path + "/car_hodometer_image/" + fileName;
			String path = EopSetting.FILE_STORE_PREFIX+ "/files/car_hodometer_image/" + fileName;
	        FileUtil.createFile(in, filePath);
			return path;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @description gps点经纬度过滤,过滤掉偏差过大的点
	 * @date 2016年10月29日 下午8:17:18
	 * @param gpsinfoArray
	 * @param currmile
	 * @return
	 */
	public JSONArray gpsinfoFilter(JSONArray gpsinfoArray, Double currmile){
		JSONArray resultArray = new JSONArray();
		try {
//			System.out.println(gpsinfoArray.size());
			//获取最大、最小的经度、纬度
//			double max_lng = gpsinfoArray.getJSONObject(0).getDouble("longitude");
//			double min_lng = gpsinfoArray.getJSONObject(0).getDouble("longitude");
//			double max_lat = gpsinfoArray.getJSONObject(0).getDouble("latitude");
//			double min_lat = gpsinfoArray.getJSONObject(0).getDouble("latitude");
			double sum_lng = 0.0;
			double sum_lat = 0.0;
			for(int i=1; i<gpsinfoArray.size(); i++){
				net.sf.json.JSONObject obj = gpsinfoArray.getJSONObject(i);
				double lng = obj.getDouble("longitude");
    			double lat = obj.getDouble("latitude");
//    			if(max_lng < lng){
//    				max_lng = lng;
//    			}
//    			if(min_lng > lng){
//    				min_lng = lng;
//    			}
//    			if(max_lat < lat){
//    				max_lat = lat;
//    			}
//    			if(min_lat > lat){
//    				min_lat = lat;
//    			}
    			
    			sum_lng += lng;
    			sum_lat += lat;
			}

			//计算最大经度、纬度偏差度数
//			double max_longitude_deviation = currmile / 100;
//			double max_latitude_deviation = currmile / (100 * Math.abs(Math.cos(min_lat)));
			
			//计算本次行程gps点的经度、纬度平均数
			double avg_lng = sum_lng / gpsinfoArray.size();
			double avg_lat = sum_lat / gpsinfoArray.size();
			
//			System.out.println("avg_lng  "+avg_lng);
//			System.out.println("avg_lat  "+avg_lat);
			
			//计算误差平均数
			double avg_lng_deviation = 0.0;
			double avg_lat_deviation = 0.0;
			for(int i=0; i<gpsinfoArray.size(); i++){
				net.sf.json.JSONObject obj = gpsinfoArray.getJSONObject(i);
				double lng = obj.getDouble("longitude");
				double lat = obj.getDouble("latitude");
				
				avg_lng_deviation += Math.abs(lng-avg_lng);
				avg_lat_deviation += Math.abs(lat-avg_lat);
			}
			avg_lng_deviation = avg_lng_deviation / (gpsinfoArray.size()/2);
			avg_lat_deviation = avg_lat_deviation / (gpsinfoArray.size()/2);
			
//			System.out.println("avg_lng_deviation  "+avg_lng_deviation);
//			System.out.println("avg_lat_deviation  "+avg_lat_deviation);
			
			//过滤误差过大的点
			for(int i=0; i<gpsinfoArray.size(); i++){
				net.sf.json.JSONObject obj = gpsinfoArray.getJSONObject(i);
				double lng_deviation = Math.abs(obj.getDouble("longitude") - avg_lng);
				double lat_deviation = Math.abs(obj.getDouble("latitude") - avg_lat);
				
//				System.out.println(avg_lng+"  "+obj.getDouble("longitude") +"  "+ lng_deviation);
//				System.out.println(avg_lat+"  "+obj.getDouble("latitude") +"  "+ lat_deviation);
				
				if(lng_deviation <= avg_lng_deviation && lat_deviation <= avg_lat_deviation){
					resultArray.add(obj);
				}
			}
//			System.out.println(resultArray.size());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return resultArray;
	}
	
	@Override
	public List getCarHodometerInfo(String carPlate, String startTime, String endTime, int startIndex, String pageSize) {
		String sql = "SELECT * FROM es_hodometer WHERE 1=1";
		if(carPlate != null && !"".equals(carPlate)){
			sql += " and carplate ='"+ carPlate +"'";
		}
		if(startTime != null && !"".equals(startTime)){
			sql += " and starttime >=" + startTime;
		}
		if(endTime != null && !"".equals(endTime)){
			sql += " and starttime <=" + endTime;
		}
		sql += " ORDER BY starttime DESC";
		sql += " LIMIT "+ startIndex +","+ pageSize;
		List returnList = this.daoSupport.queryForList(sql);
		return returnList;
	}

	@Override
	public net.sf.json.JSONObject getTodayGainsAndOtherInfo(String carplate, Long starttime, Long endtime) {
		try {
			String sql = "select SUM(insuregain+repairgain) today_gain, SUM(drivingtime) total_drivingtime, AVG(drivingscore) drivingscore  from es_hodometer WHERE carplate='"+ carplate +"' AND starttime BETWEEN "+ starttime +" AND "+ endtime;
			List list = daoSupport.queryForList(sql);
			if(list.size() > 0){
				return net.sf.json.JSONObject.fromObject(list.get(0));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public net.sf.json.JSONArray getDrivingReportByCarplate(String carplate, String starttime) {
		try {
			JSONArray array = new JSONArray();
			//驾驶情况
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT IFNULL(SUM(t.slambrakenum), 0) slambrakenum, IFNULL(SUM(t.fastaccelarationnum), 0) fastaccelarationnum, ");
			sql.append("IFNULL(MAX(t.drivingtime), 0) max_drivingtime, IFNULL(SUM(t.drivingtime), 0) sum_drivingtime, ");
			sql.append("IFNULL(SUM(t.overdriving), 0) overdriving, IFNULL(SUM(t.overspeed), 0) overspeed, IFNULL(SUM(t.maxrevolution_overtime), 0) maxrevolution_overtime, ");
			sql.append("IFNULL(MAX(t.mile), 0) max_mile, IFNULL(SUM(t.mile), 0) sum_mile, IFNULL(SUM(t.oilconsumption + t.idlingoilconsumption), 0) total_oilconsumption, ");
			sql.append("IFNULL(SUM(t.reward), 0) sum_reward, IFNULL(SUM(t.deduction), 0) sum_deduction, IFNULL(SUM(t.reward - t.deduction), 0) sum_gain ");
			sql.append("FROM es_hodometer t WHERE carplate=? ");
			sql.append("AND FROM_UNIXTIME(t.starttime/1000,'%Y-%m-%d') = FROM_UNIXTIME(?/1000,'%Y-%m-%d')");
			net.sf.json.JSONObject obj = new net.sf.json.JSONObject();
			List list = daoSupport.queryForList(sql.toString(), carplate, starttime);
			if(list.size() > 0){
				obj = net.sf.json.JSONObject.fromObject(list.get(0));
				if(obj != null){
					int slambrakenum = obj.getInt("slambrakenum");//急减速次数
					obj.put("slambrakenum_deduct", slambrakenum * 0.4);
					int fastaccelarationnum = obj.getInt("fastaccelarationnum");//急加速次数
					obj.put("fastaccelarationnum_deduct", fastaccelarationnum * 0.4);
					int overdriving = obj.getInt("overdriving");//疲劳驾驶次数
					obj.put("overdriving_deduct", overdriving * 0.4);
					int overspeed = obj.getInt("overspeed");//超速次数
					obj.put("overspeed_deduct", overspeed * 0.4);
					int maxrevolution_overtime = obj.getInt("maxrevolution_overtime");//最大转速超速次数 > 5000
					obj.put("maxrevolution_overtime_deduct", maxrevolution_overtime * 0.4);
					long max_drivingtime = obj.getLong("max_drivingtime");//最长行驶时间
					long sum_drivingtime = obj.getLong("sum_drivingtime");//合计行驶时间
					double max_mile = obj.getDouble("max_mile");//最长行驶里程
					double sum_mile = obj.getDouble("sum_mile");//合计行驶里程
					double total_oilconsumption = obj.getDouble("total_oilconsumption");//油耗
					
					//奖励统计
					double sum_reward = obj.getDouble("sum_reward");//昨日出行奖励
					double sum_deduction = obj.getDouble("sum_deduction");//昨日危险驾驶扣除
					double sum_gain = obj.getDouble("sum_gain");//昨日安全驾驶收益
				}
			}
			
			//保障服务
			String insureMsg = "正常";
			String repairMsg = "正常";
			StringBuffer safeSql = new StringBuffer();
			safeSql.append("SELECT t.`insurenextbuytime`, t.`repairlasttime`, t.`repairnexttime`, t.`repairlastmile`, t.`totalmile`, m.`repairinterval` ");
			safeSql.append("FROM es_carinfo t, es_carmodels m WHERE t.`carmodelid`=m.`id` AND t.carplate=?");
			list = daoSupport.queryForList(safeSql.toString(), carplate);
			net.sf.json.JSONObject safeObj = new net.sf.json.JSONObject();
			if(list.size() > 0){
				safeObj = net.sf.json.JSONObject.fromObject(daoSupport.queryForList(safeSql.toString(), carplate).get(0));
				//保险
				long insurenextbuytime = safeObj.getLong("insurenextbuytime");
				if(Long.valueOf(starttime) > insurenextbuytime){//保险已过期
					long insureovertime = Long.valueOf(starttime) - insurenextbuytime;
					long over_days = (insureovertime / (1*24*60*60*1000) == 0) ? 1 : insureovertime / (1*24*60*60*1000);
//					insureMsg = "您的车辆保险已过期"+ over_days +"天，请尽快续保";
					insureMsg = "爱车保险已到期，快去续保吧";
				}
				safeObj.put("insureMsg", insureMsg);
				//保养
				long repairlastmile = safeObj.getLong("repairlastmile");
				long repairinterval = safeObj.getLong("repairinterval");
				long repairnextmile = 0;
				if(repairinterval > 0){
					repairnextmile = ((repairlastmile % repairinterval) > (repairinterval / 2)) ? ((repairlastmile/repairinterval) + 2)*repairinterval : ((repairlastmile/repairinterval) + 1)*repairinterval;
				}
				long repairnexttime = safeObj.getLong("repairnexttime");
				if(Long.valueOf(starttime) > repairnexttime){//保养已过期
					long repairovertime = Long.valueOf(starttime) - repairnexttime;
					long over_days = (repairovertime / (1*24*60*60*1000) == 0) ? 1 : repairovertime / (1*24*60*60*1000);
//					repairMsg = "您的爱车已超出保养期限"+ over_days +"天，请尽快为您的爱车做保养！";
					repairMsg = "爱车保养已到期，快去保养吧";
				}
				safeObj.put("repairnextmile", repairnextmile);
				safeObj.put("repairMsg", repairMsg);
			}
			
			array.add(obj);
			array.add(safeObj);
			return array;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Page getCarHodometerList(Map paramMap) {
		String sort = String.valueOf(paramMap.get("sort"));
		String order = String.valueOf(paramMap.get("order"));
		String keyword = String.valueOf(paramMap.get("keyword"));
		int pageNo = (int)paramMap.get("pageNo");
		int pageSize = (int)paramMap.get("pageSize");
		StringBuffer sql = new StringBuffer();
		sql.append("select *,(starttime+drivingtime) endtime from  es_hodometer  eh where 1=1 ");
		if(!StringUtil.isNull(keyword)){
			sql.append(" AND eh.carplate like '%").append(keyword).append("%'");
		}
		sql.append("ORDER BY eh.starttime DESC ");
		return daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
	}

	@Override
	public net.sf.json.JSONObject getCarRecentGain(String carplate) {
		net.sf.json.JSONObject obj = new net.sf.json.JSONObject();
		try {
			net.sf.json.JSONObject resultObj = new net.sf.json.JSONObject();
			
			//获取近一周的日期字符串
			List<String> dateList = getDateStringList("week");
			
			//获取日期为key的map集合，用于按顺序返回数据
			Map<String, String> gainMap = getDateMap(dateList);
			
			//查询近一周的每日收益
			String sql = "SELECT startdate, IFNULL(SUM(insuregain+repairgain),0) gain FROM (SELECT FROM_UNIXTIME(starttime/1000, '%Y-%m-%d') startdate, t.* FROM es_hodometer t WHERE t.carplate=?) t1 "
					   + "WHERE t1.startdate IN ("+ StringUtil.arrayToString(dateList.toArray(), ",") +") GROUP BY t1.startdate ORDER BY t1.startdate";

			List gainList = daoSupport.queryForList(sql, carplate);
			
			if(gainList.size() > 0){
				for(int i=0; i<gainList.size(); i++){
					net.sf.json.JSONObject gainObj = net.sf.json.JSONObject.fromObject(gainList.get(i));
					String satrtdate = gainObj.getString("startdate");
					String gain = gainObj.getString("gain");

					if(gainMap.containsKey(satrtdate)){
						gainMap.put(satrtdate, gain);
					}
				}
			}
			
			//获取返回的数据list
			List<String> value_list = new ArrayList<String>(gainMap.values());
			
			//查询近一周的收益的最大值、最小值
			double max_gain = Double.valueOf(value_list.get(0).toString());
			double min_gain = Double.valueOf(value_list.get(0).toString());
			
			DecimalFormat df = new DecimalFormat("0.00");

			for(int i=1; i<value_list.size(); i++){
				double tempValue = Double.valueOf(value_list.get(i).toString());
				if(tempValue > max_gain){
					max_gain = tempValue;
				}
				if(tempValue < min_gain){
					min_gain = tempValue;
				}
			}
			resultObj.put("max_value", df.format(max_gain));
			resultObj.put("min_value", df.format(min_gain));
			
			resultObj.put("value_list", value_list);
			resultObj.put("type", 1);
			
			obj.put("result", 1);
			obj.put("message", "获取数据成功");
			obj.put("data", resultObj);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}
	
	@Override
	public net.sf.json.JSONObject getCarTravelDataAnalyse(String carplate, String date_type) {
		net.sf.json.JSONObject obj = new net.sf.json.JSONObject();
		try {
			/**
			 * 统计近一周 或 一个月的1 奖励、2 油耗、3 里程、4 驾驶时长
			 */
			if("week".equals(date_type) || "month".equals(date_type)){
				List dateList = getDateStringList(date_type);
				
				JSONArray resultArray = new JSONArray();
				
				net.sf.json.JSONObject rewardObj = new net.sf.json.JSONObject();//收益obj
				net.sf.json.JSONObject oilObj = new net.sf.json.JSONObject();//油耗obj
				net.sf.json.JSONObject mileObj = new net.sf.json.JSONObject();//里程obj
				net.sf.json.JSONObject drivingtimeObj = new net.sf.json.JSONObject();//驾驶时长obj
				
				String sql = "SELECT startdate, IFNULL(SUM(insuregain+repairgain),0) gain, IFNULL(SUM(idlingoilconsumption+oilconsumption),0) oil_consumption, IFNULL(SUM(mile),0) mile, IFNULL(SUM(drivingtime),0) drivingtime "
						   + "FROM (SELECT FROM_UNIXTIME(starttime/1000, '%Y-%m-%d') startdate, t.* FROM es_hodometer t WHERE t.carplate=?) t1 "
						   + "WHERE t1.startdate IN ("+ StringUtil.arrayToString(dateList.toArray(), ",") +") GROUP BY t1.startdate ORDER BY t1.startdate";

				List analyseDataList = daoSupport.queryForList(sql, carplate);

				
				//获取日期为key的map集合，用于按顺序返回数据
				Map<String, String> gainMap = getDateMap(dateList);
				Map<String, String> oilMap = getDateMap(dateList);
				Map<String, String> mileMap = getDateMap(dateList);
				Map<String, String> drivingTimeMap = getDateMap(dateList);
				double sumGain = 0.0;//累计收益
				double sumOil = 0.0;//累计油耗
				double sumMile = 0.0;//累计里程
				long sumDrivingTime = 0;//累计驾驶时长
				
				if(analyseDataList.size() > 0){
					for(int i=0; i<analyseDataList.size(); i++){
						net.sf.json.JSONObject dataObj = net.sf.json.JSONObject.fromObject(analyseDataList.get(i));
						String satrtdate = dataObj.getString("startdate");
						String gain = dataObj.getString("gain");
						String oil_consumption = dataObj.getString("oil_consumption");
						String mile = dataObj.getString("mile");
						long drivingtime = dataObj.getLong("drivingtime");

						if(gainMap.containsKey(satrtdate)){
							gainMap.put(satrtdate, gain);
							oilMap.put(satrtdate, oil_consumption);
							mileMap.put(satrtdate, mile);
							drivingTimeMap.put(satrtdate, drivingtime+"");
							
							sumGain += Double.valueOf(gain);
							sumOil += Double.valueOf(oil_consumption);
							sumMile += Double.valueOf(mile);
							sumDrivingTime += drivingtime;
						}
					}
				}
				
				//获取返回的数据list
				List carRecentGainList = new ArrayList<>(gainMap.values());//收益list
				List carRecentOilList = new ArrayList<>(oilMap.values());//油耗list
				List carRecentMileList = new ArrayList<>(mileMap.values());//行驶里程list
				List carRecentDrivingTimeList = new ArrayList<>(drivingTimeMap.values());//驾驶时长list

				//查询近一周或一个月的1 奖励、2 油耗、3 里程、4 驾驶时长的最大值、最小值
				double max_gain = Double.valueOf(carRecentGainList.get(0).toString());
				double min_gain = Double.valueOf(carRecentGainList.get(0).toString());
				double max_oil = Double.valueOf(carRecentOilList.get(0).toString());
				double min_oil = Double.valueOf(carRecentOilList.get(0).toString());
				double max_mile = Double.valueOf(carRecentMileList.get(0).toString());
				double min_mile = Double.valueOf(carRecentMileList.get(0).toString());
				long max_drivingTime = Long.valueOf(carRecentDrivingTimeList.get(0).toString());
				long min_drivingTime = Long.valueOf(carRecentDrivingTimeList.get(0).toString());

				DecimalFormat df = new DecimalFormat("0.00");

				for(int i=1; i<carRecentGainList.size(); i++){
					double tempValue = Double.valueOf(carRecentGainList.get(i).toString());
					if(tempValue > max_gain){
						max_gain = tempValue;
					}
					if(tempValue < min_gain){
						min_gain = tempValue;
					}
				}
				rewardObj.put("max_value", df.format(max_gain));
				rewardObj.put("min_value", df.format(min_gain));
				
				for(int i=1; i<carRecentOilList.size(); i++){
					double tempValue = Double.valueOf(carRecentOilList.get(i).toString());
					if(tempValue > max_oil){
						max_oil = tempValue;
					}
					if(tempValue < min_oil){
						min_oil = tempValue;
					}
				}
				oilObj.put("max_value", df.format(max_oil));
				oilObj.put("min_value", df.format(min_oil));
				
				for(int i=1; i<carRecentMileList.size(); i++){
					double tempValue = Double.valueOf(carRecentMileList.get(i).toString());
					if(tempValue > max_mile){
						max_mile = tempValue;
					}
					if(tempValue < min_mile){
						min_mile = tempValue;
					}
				}
				mileObj.put("max_value", df.format(max_mile));
				mileObj.put("min_value", df.format(min_mile));
				
				for(int i=1; i<carRecentDrivingTimeList.size(); i++){
					long tempValue = Long.valueOf(carRecentDrivingTimeList.get(i).toString());
					if(tempValue > max_drivingTime){
						max_drivingTime = tempValue;
					}
					if(tempValue < min_drivingTime){
						min_drivingTime = tempValue;
					}
				}
				drivingtimeObj.put("max_value", max_drivingTime);
				drivingtimeObj.put("min_value", min_drivingTime);
				
				rewardObj.put("sum_value", df.format(sumGain));
				rewardObj.put("value_list", carRecentGainList);
				rewardObj.put("type", 1);
				oilObj.put("sum_value", df.format(sumOil));
				oilObj.put("value_list", carRecentOilList);
				oilObj.put("type", 2);
				mileObj.put("sum_value", df.format(sumMile));
				mileObj.put("value_list", carRecentMileList);
				mileObj.put("type", 3);
				drivingtimeObj.put("sum_value", sumDrivingTime);
				drivingtimeObj.put("value_list", carRecentDrivingTimeList);
				drivingtimeObj.put("type", 4);
				
				resultArray.add(rewardObj);
				resultArray.add(oilObj);
				resultArray.add(mileObj);
				resultArray.add(drivingtimeObj);
				
				obj.put("data", resultArray);
			}
			
			/**
			 * 统计近一年的1 奖励、2 油耗、3 里程、4 驾驶时长
			 */
			if("year".equals(date_type)){
				List dateList = getDateStringList(date_type);

				JSONArray resultArray = new JSONArray();
				
				net.sf.json.JSONObject rewardObj = new net.sf.json.JSONObject();//收益obj
				net.sf.json.JSONObject oilObj = new net.sf.json.JSONObject();//油耗obj
				net.sf.json.JSONObject mileObj = new net.sf.json.JSONObject();//里程obj
				net.sf.json.JSONObject drivingtimeObj = new net.sf.json.JSONObject();//驾驶时长obj
				
				List carRecentGainList = new ArrayList<>();//收益list
				List carRecentOilList = new ArrayList<>();//油耗list
				List carRecentMileList = new ArrayList<>();//行驶里程list
				List carRecentDrivingTimeList = new ArrayList<>();//驾驶时长list
				
				double sumGain = 0.0;//累计收益
				double sumOil = 0.0;//累计油耗
				double sumMile = 0.0;//累计里程
				long sumDrivingTime = 0;//累计驾驶时长

				String sql;
				List analyseDataList;
				for(int i=0; i<dateList.size(); i++){
					String[] timeArray = ((String)dateList.get(i)).split(",");
					
					sql = "SELECT IFNULL(SUM(insuregain+repairgain),0) gain, IFNULL(SUM(idlingoilconsumption+oilconsumption),0) oil_consumption, IFNULL(SUM(mile),0) mile, IFNULL(SUM(drivingtime),0) drivingtime "
						+ "FROM (SELECT FROM_UNIXTIME(starttime/1000, '%Y-%m-%d') startdate, t.* FROM es_hodometer t WHERE t.carplate=?) t1 "
						+ "WHERE t1.startdate BETWEEN "+ timeArray[0] +" AND "+ timeArray[1] +" ORDER BY t1.startdate";

					analyseDataList = daoSupport.queryForList(sql, carplate);

					if(analyseDataList.size() > 0){
						net.sf.json.JSONObject dataObj = net.sf.json.JSONObject.fromObject(analyseDataList.get(0));
						String gain = dataObj.getString("gain");
						String oil_consumption = dataObj.getString("oil_consumption");
						String mile = dataObj.getString("mile");
						Long drivingtime = dataObj.getLong("drivingtime");

						carRecentGainList.add(gain);
						carRecentOilList.add(oil_consumption);
						carRecentMileList.add(mile);
						carRecentDrivingTimeList.add(drivingtime+"");
						
						sumGain += Double.valueOf(gain);
						sumOil += Double.valueOf(oil_consumption);
						sumMile += Double.valueOf(mile);
						sumDrivingTime += drivingtime;
					}else{
						carRecentGainList.add("0");
						carRecentOilList.add("0");
						carRecentMileList.add("0");
						carRecentDrivingTimeList.add("0");
					}
				}
				
				//查询近一年内的1 奖励、2 油耗、3 里程、4 驾驶时长的最大值、最小值
				double max_gain = Double.valueOf(carRecentGainList.get(0).toString());
				double min_gain = Double.valueOf(carRecentGainList.get(0).toString());
				double max_oil = Double.valueOf(carRecentOilList.get(0).toString());
				double min_oil = Double.valueOf(carRecentOilList.get(0).toString());
				double max_mile = Double.valueOf(carRecentMileList.get(0).toString());
				double min_mile = Double.valueOf(carRecentMileList.get(0).toString());
				long max_drivingTime = Long.valueOf(carRecentDrivingTimeList.get(0).toString());
				long min_drivingTime = Long.valueOf(carRecentDrivingTimeList.get(0).toString());

				DecimalFormat df = new DecimalFormat("0.00");

				for(int i=1; i<carRecentGainList.size(); i++){
					double tempValue = Double.valueOf(carRecentGainList.get(i).toString());
					if(tempValue > max_gain){
						max_gain = tempValue;
					}
					if(tempValue < min_gain){
						min_gain = tempValue;
					}
				}
				rewardObj.put("max_value", df.format(max_gain));
				rewardObj.put("min_value", df.format(min_gain));
				
				for(int i=1; i<carRecentOilList.size(); i++){
					double tempValue = Double.valueOf(carRecentOilList.get(i).toString());
					if(tempValue > max_oil){
						max_oil = tempValue;
					}
					if(tempValue < min_oil){
						min_oil = tempValue;
					}
				}
				oilObj.put("max_value", df.format(max_oil));
				oilObj.put("min_value", df.format(min_oil));
				
				for(int i=1; i<carRecentMileList.size(); i++){
					double tempValue = Double.valueOf(carRecentMileList.get(i).toString());
					if(tempValue > max_mile){
						max_mile = tempValue;
					}
					if(tempValue < min_mile){
						min_mile = tempValue;
					}
				}
				mileObj.put("max_value", df.format(max_mile));
				mileObj.put("min_value", df.format(min_mile));
				
				for(int i=1; i<carRecentDrivingTimeList.size(); i++){
					long tempValue = Long.valueOf(carRecentDrivingTimeList.get(i).toString());
					if(tempValue > max_drivingTime){
						max_drivingTime = tempValue;
					}
					if(tempValue < min_drivingTime){
						min_drivingTime = tempValue;
					}
				}
				drivingtimeObj.put("max_value", max_drivingTime);
				drivingtimeObj.put("min_value", min_drivingTime);
				
				rewardObj.put("sum_value", df.format(sumGain));
				rewardObj.put("value_list", carRecentGainList);
				rewardObj.put("type", 1);
				oilObj.put("sum_value", df.format(sumOil));
				oilObj.put("value_list", carRecentOilList);
				oilObj.put("type", 2);
				mileObj.put("sum_value", df.format(sumMile));
				mileObj.put("value_list", carRecentMileList);
				mileObj.put("type", 3);
				drivingtimeObj.put("sum_value", sumDrivingTime);
				drivingtimeObj.put("value_list", carRecentDrivingTimeList);
				drivingtimeObj.put("type", 4);
				
				resultArray.add(rewardObj);
				resultArray.add(oilObj);
				resultArray.add(mileObj);
				resultArray.add(drivingtimeObj);
				
				obj.put("data", resultArray);
			}
			
			obj.put("result", 1);
			obj.put("message", "获取数据成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}


	public List getDateStringList(String type){
		List dateList = new ArrayList<>();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		
		if("week".equals(type)){
			int count = 7;
			
			for(int i=0; i<count; i++){
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -i);
				dateList.add("'"+format.format(c.getTime())+"'");
			}
		}
		
		if("month".equals(type)){
			int count = 31;
			
			for(int i=0; i<count; i++){
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, -i);
				dateList.add("'"+format.format(c.getTime())+"'");
			}
		}
		
		if("year".equals(type)){
			int count = 12;
			
			for(int i=0; i<count; i++){
				Calendar c = Calendar.getInstance();
				int a = i;
				c.add(Calendar.MONTH, -a);
				String endtime = "'"+format.format(c.getTime())+"'";
				a++;
				c = Calendar.getInstance();
				c.add(Calendar.MONTH, -a);
				String starttime = "'"+format.format(c.getTime())+"'";
				dateList.add(starttime +","+ endtime);
			}
		}
		
		Collections.reverse(dateList);
		
		return dateList;
	}
	
	public Map<String, String> getDateMap(List<String> dateList) {
		Map<String, String> map = new TreeMap<>();
		if(dateList.size() > 0){
			for(int i=0; i<dateList.size(); i++){
				map.put(dateList.get(i).replaceAll("'", ""), "0");
			}
		}
		return map;
	}
	
	/**
	 * @description 更新油耗统计数据
	 * @date 2016年9月13日 下午6:22:56
	 * @param currentDate
	 * @param carplate
	 * @return int
	 * @throws Exception
	 */
	protected int updateConsumptionData(String currentDate, String carplate) throws Exception {
		
		//获取该车某天的总油耗，计算该天的平均油耗
		Map<String, Double> currentDateConsumption = queryCurrentDateConsumption(currentDate, carplate);
		
		//获取该车该日期之前7天的总油耗，并计算周平均油耗
		String lastSevenDays = DateUtils.calculateDateAddDays(currentDate,-7);//计算前7天的日期
		Map<String, Double> lastSevenDaysConsumption = queryLastDaysConsumption(lastSevenDays, currentDate, carplate, 7);

		//获取该车该日期之前30天的总油耗，并计算月平均油耗
		String lastThirtyDays = DateUtils.calculateDateAddDays(currentDate,-30);//计算前30天的日期
		Map<String, Double> lastThirtyDaysConsumption = queryLastDaysConsumption(lastThirtyDays, currentDate, carplate, 30);
		
		//获取数据,如果为null时设置默认值为0.00
		double tital_consumption_today = currentDateConsumption.get("tital_consumption_today") == null ? 0.00 :currentDateConsumption.get("tital_consumption_today");
		double avg_consumption_today = currentDateConsumption.get("avg_consumption_today") == null ? 0.00 : currentDateConsumption.get("tital_consumption_today");
		double tital_mileage_today = currentDateConsumption.get("tital_mileage_today") == null ? 0.00 : currentDateConsumption.get("tital_mileage_today");
		double tital_consumption_week = lastSevenDaysConsumption.get("tital_consumption") == null ? 0.00 : lastSevenDaysConsumption.get("tital_consumption");
		double avg_consumption_week = lastSevenDaysConsumption.get("avg_consumption") == null ? 0.00 : lastSevenDaysConsumption.get("avg_consumption");
		double tital_mileage_week = lastSevenDaysConsumption.get("tital_mileage") == null ? 0.00 : lastSevenDaysConsumption.get("tital_mileage");
		double tital_consumption_month = lastThirtyDaysConsumption.get("tital_consumption") == null ? 0.00 : lastThirtyDaysConsumption.get("tital_consumption");
		double avg_consumption_month = lastThirtyDaysConsumption.get("avg_consumption") == null ? 0.00 : lastThirtyDaysConsumption.get("avg_consumption");
		double tital_mileage_month = lastThirtyDaysConsumption.get("tital_mileage")== null ? 0.00 : lastThirtyDaysConsumption.get("tital_mileage");
		
		//封装实体
		ConsumptionStatistics consumptionStatistics = new ConsumptionStatistics();
		consumptionStatistics.setAvg_consumption_month(avg_consumption_month);
		consumptionStatistics.setAvg_consumption_today(avg_consumption_today);
		consumptionStatistics.setAvg_consumption_week(avg_consumption_week);
		consumptionStatistics.setCarplate(carplate);
		consumptionStatistics.setCurrentdate(currentDate);
		consumptionStatistics.setTital_consumption_month(tital_consumption_month);
		consumptionStatistics.setTital_consumption_today(tital_consumption_today);
		consumptionStatistics.setTital_consumption_week(tital_consumption_week);
		consumptionStatistics.setTital_mileage_month(tital_mileage_month);
		consumptionStatistics.setTital_mileage_today(tital_mileage_today);
		consumptionStatistics.setTital_mileage_week(tital_mileage_week);
	
		//判断这辆车是否有油耗统计数据
		int result = 0;
		boolean isExist = checkCarIsExist(carplate);
		if(isExist) {
			//更新油耗数据统计表
			result = updateConsumptionData(consumptionStatistics);
		} else {
			//插入数据到油耗数据统计表
			result = insertConsumptionData(consumptionStatistics);
		}
		
		if(result != 1) {
			throw new RuntimeException("油耗数据统计表操作数据异常");
		}
		return result;
	}

	/**
	 * @description 查询当前日期的油耗数据
	 * @date 2016年9月13日 下午3:11:01
	 * @param currentDate
	 * @param carplate
	 */
	private Map<String,Double> queryCurrentDateConsumption(String currentDate, String carplate) {
		
		long starttime = DateUtil.getDateline(currentDate +" 00:00:00") * 1000;
		long endtime = DateUtil.getDateline(currentDate + " 23:59:59") * 1000;
		
		String sql = 
			" SELECT                                                                         "+
			" 	sum(t.idlingoilconsumption + t.oilconsumption) as tital_consumption_today,   "+
			" 	avg(t.idlingoilconsumption + t.oilconsumption) as avg_consumption_today,     "+
			" 	sum(t.mile) as tital_mileage_today                                           "+
			" FROM                                                                           "+
			" 	es_hodometer t                                                               "+
			" WHERE 1=1                                                                      "+
			" AND t.starttime = "+ starttime +"                                              "+
			" AND t.starttime <= "+ endtime +"                                               "+
			" AND t.carplate = '"+ carplate +"'                                              ";
			
		Map<String,Double> currentDateConsumption = daoSupport.queryForMap(sql);
		return currentDateConsumption;
	}
	
	/**
	 * @description 查询当前日期之前n天的油耗数据
	 * @date 2016年9月13日 下午3:52:23
	 * @param lastSevenDay
	 * @param carplate
	 * @return Map<String, Double>
	 */
	private Map<String, Double> queryLastDaysConsumption(String lastDays, String currentDate, String carplate, int days) {
		
		long starttime = DateUtil.getDateline(lastDays + " 00:00:00") * 1000;
		long endtime = DateUtil.getDateline(currentDate +" 23:59:59") * 1000;
		
		String sql = 
				" SELECT                                                                         "+
				" 	sum(t.idlingoilconsumption + t.oilconsumption) as tital_consumption,   		 "+
				" 	sum(t.idlingoilconsumption + t.oilconsumption) /"+days+" as avg_consumption, "+
				" 	sum(t.mile) as tital_mileage                                          		 "+
				" FROM                                                                           "+
				" 	es_hodometer t                                                               "+
				" WHERE 1=1                                                                      "+
				" AND t.starttime >= "+ starttime +"         									 "+
				" AND t.starttime <= "+ endtime +"                        						 "+
				" AND t.carplate = '"+ carplate +"'                                              ";
				
		Map<String,Double> lastDaysConsumption = daoSupport.queryForMap(sql);
		return lastDaysConsumption;
	}
	
	/**
	 * @description 校验该车是否已存在数据
	 * @date 2016年9月13日 下午4:19:59
	 * @param carplate
	 * @return boolean
	 */
	private boolean checkCarIsExist(String carplate) {
		String sql = 
			" SELECT                            "+
			" 	t.id                            "+
			" FROM                              "+
			" 	es_consumption_statistics t     "+
			" WHERE 1 = 1                       "+
			" AND t.carplate = '"+carplate+"'   ";
		
		List<Map<String, Integer>> carExist = daoSupport.queryForList(sql);	
		return ValidateUtils.isEmpty(carExist) ? false : true;
	}
	
	/**
	 * @description 更新油耗数据
	 * @date 2016年9月13日 下午4:17:14
	 * @param consumptionStatistics
	 * @return int
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	private int updateConsumptionData(ConsumptionStatistics consumptionStatistics) throws Exception{
		
		//获取参数
		String carplate = consumptionStatistics.getCarplate();
		String current_date = consumptionStatistics.getCurrentdate();
		double avg_consumption_month = consumptionStatistics.getAvg_consumption_month();
		double avg_consumption_today = consumptionStatistics.getAvg_consumption_today();
		double avg_consumption_week = consumptionStatistics.getAvg_consumption_week();
		double tital_consumption_month = consumptionStatistics.getTital_consumption_month();
		double tital_consumption_today = consumptionStatistics.getTital_consumption_today();
		double tital_consumption_week = consumptionStatistics.getTital_consumption_week();
		double tital_mileage_month = consumptionStatistics.getTital_mileage_month();
		double tital_mileage_today = consumptionStatistics.getTital_mileage_today();
		double tital_mileage_week = consumptionStatistics.getTital_mileage_week();
	
		String sql = 
				" UPDATE es_consumption_statistics t       							"+
				" SET                                      							"+
				" 	t.tital_mileage_today = "+ tital_mileage_today +",             	"+
				" 	t.tital_mileage_week = "+ tital_mileage_week +",                "+
				" 	t.tital_consumption_today = "+ tital_consumption_today +",      "+
				" 	t.tital_mileage_month = "+ tital_mileage_month +",              "+
				" 	t.currentdate = '"+ current_date +"',                   		"+
				" 	t.avg_consumption_month = "+ avg_consumption_month +",          "+
				" 	t.tital_consumption_week = "+ tital_consumption_week +",        "+
				" 	t.tital_consumption_month = "+ tital_consumption_month +",      "+
				" 	t.avg_consumption_today = "+ avg_consumption_today +",          "+
				" 	t.avg_consumption_week = "+ avg_consumption_week +"             "+
				" WHERE    1=1                                						"+
				" AND t.carplate = '"+ carplate +"'                					";
		
		daoSupport.execute(sql);
		return 1;
	}
	
	/**
	 * @description 插入油耗统计数据
	 * @date 2016年9月13日 下午5:05:16
	 * @param consumptionStatistics
	 * @return int
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	private int insertConsumptionData(ConsumptionStatistics consumptionStatistics) throws Exception {
		daoSupport.insert("es_consumption_statistics", consumptionStatistics);
		return 1;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * ---------------------------------------------------------------------------------------
	 * GETTER AND SETTER
	 */
	
	
	
	
} 