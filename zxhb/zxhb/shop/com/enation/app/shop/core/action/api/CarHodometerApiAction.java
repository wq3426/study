/**
 * 版权：Copyright (C) 2016  中安信博（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：设备api  
 * 修改人：
 * 修改时间：2016-03-14
 * 修改内容: 
 */
package com.enation.app.shop.core.action.api;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.ICarHodometerManager;
import com.enation.app.shop.core.service.impl.CarInfoManager;
import com.enation.app.shop.core.utils.RegularExpressionUtils;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 车辆行程信息api
 * 实体类：CarHodoMeterInfo
 * @author wangqiang 2016年4月8日 下午3:04:05
 *
 */

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("carhodometer")
@SuppressWarnings("serial")
public class CarHodometerApiAction extends WWAction {
	private ICarHodometerManager carHodometerManager;
	private CarInfoManager carInfoManager;
	
	/**
	 * 获取车辆行程信息
	 * @return
	 */
	public String getCarHodometer(){
		String carPlate = getRequest().getParameter("carPlate");
		String startTime = getRequest().getParameter("startTime");
		String endTime = getRequest().getParameter("endTime");
		String pageNum = getRequest().getParameter("pageNum");
		String pageSize = getRequest().getParameter("pageSize");
		int startIndex = getStartIndex(pageNum, pageSize);
		try {
			List carHodometerInfo = carHodometerManager.getCarHodometerInfo(carPlate, startTime, endTime, startIndex, pageSize);
			JSONArray array = JSONArray.fromObject(carHodometerInfo);
			if(carHodometerInfo.size() > 0){
				for(int i=0; i<array.size(); i++){
					JSONObject obj = array.getJSONObject(i);
					String gps_imgurl = UploadUtil.replacePath(obj.getString("gps_imgurl"));
					obj.put("gps_imgurl", gps_imgurl);
				}
				this.json = "{\"result\":1,\"data\":"+array.toString()+"}";
			} else {
				this.json = "{\"result\":1,\"data\":\"\"}";
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("getCarHodometerInfo has errors", e);
			this.showErrorJson(e.getMessage());
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 获取今日收益、总里程、总收益
	 * @return
	 */
	public String getTodayGains(){
		try {
			JSONObject returnObj = new JSONObject();
			String carplate = getRequest().getParameter("carplate");
			Long[] timeArray = getStartAndEndTimeOfToday();
			List list = carInfoManager.getCarInfoByCarplate(carplate);
			if(list.size() > 0){
				JSONObject obj = JSONObject.fromObject(list.get(0));
				JSONObject todayInfo = carHodometerManager.getTodayGainsAndOtherInfo(carplate, timeArray[0], timeArray[1]);
				String todayGains = "";
				String total_drivingtime = "";
				String drivingscore = "";
				if(todayInfo != null){
					todayGains = StringUtil.isNull(todayInfo.getString("today_gain")) ? "0" : todayInfo.getString("today_gain");
					total_drivingtime = StringUtil.isNull(todayInfo.getString("total_drivingtime")) ? "0" : todayInfo.getString("total_drivingtime");
					drivingscore = StringUtil.isNull(todayInfo.getString("drivingscore")) ? "0" : todayInfo.getString("drivingscore");
				}
				String totalmile = StringUtil.isNull(obj.getString("totalmile")) ? "0" : obj.getString("totalmile");
				String totalgain = StringUtil.isNull(obj.getString("totalgain")) ? "0" : obj.getString("totalgain");
				DecimalFormat df = new DecimalFormat("0.00");
				todayGains = df.format(Double.valueOf(todayGains));
				totalmile = df.format(Double.valueOf(totalmile));
				totalgain = df.format(Double.valueOf(totalgain));
				total_drivingtime = df.format(Double.valueOf(total_drivingtime));
				drivingscore = df.format(Double.valueOf(drivingscore));
				
				JSONObject resultObj = new JSONObject();
				resultObj.put("todayGains", todayGains);
				resultObj.put("totalmile", totalmile);
				resultObj.put("totalgain", totalgain);
				resultObj.put("total_drivingtime", total_drivingtime);
				resultObj.put("drivingscore", drivingscore);
				
				returnObj.put("result", 1);
				returnObj.put("data", resultObj.toString());
				returnObj.put("message", "获取信息成功");
			}else{
				returnObj.put("result", 0);
				returnObj.put("message", "根据车牌号获取车辆信息失败");
			}
			this.json = returnObj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("getTodayGains has errors", e);
			this.showErrorJson(e.getMessage());
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 获取当天的起始和结束时间的毫秒数
	 */
	public static Long[] getStartAndEndTimeOfToday() {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(new Date());
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	     
	    Date start = calendar.getTime();
	    
	    calendar.add(Calendar.DAY_OF_MONTH, 1);
	    calendar.add(Calendar.SECOND, -1);

	    Date end = calendar.getTime();
	 
	    return new Long[]{start.getTime(), end.getTime()};
	}
	
	/**
	 * 添加/修改车辆行程
	 * @return
	 */
	public String updateCarHodometer(){
		String carHodometer = new String();
		try {
			HttpServletRequest request = getRequest();
			try{
				carHodometer = getBody(request);
				this.logger.error("updateCarHodometer:" + carHodometer);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			String msg = "";
			if(StringUtil.isNull(carHodometer)){
				msg = "入参为空，请检查";
			}else if(carHodometer.indexOf("{") == -1 || carHodometer.indexOf("}") == -1){
				msg = "行程不是json字符串，请检查";
			}else{
				org.json.JSONObject obj = new org.json.JSONObject(carHodometer.replace("=", ":"));
				String carplate = obj.getString("carplate") == null ? "" : obj.getString("carplate");
				String gpsinfo = obj.getString("gpsinfo") == null ? "" : obj.getString("gpsinfo");
				String mile = obj.getString("mile") == null ? "" : obj.getString("mile");
				if(StringUtil.isNull(carplate)){
					msg = "车牌号为空，请检查";
				}else if(carInfoManager.getCarInfoByCarplate(carplate).size() == 0){
					msg = "该车牌号"+carplate+"不存在，请检查";
				}else if(StringUtil.isNull(gpsinfo)){
					msg = "gpsinfo信息json字符串为空，请检查";
				}else if(gpsinfo.indexOf("{") == -1 || gpsinfo.indexOf("}") == -1){
					msg = "gpsinfo不是json字符串，请检查";
				}else if(StringUtil.isNull(mile)){
					msg = "本次行驶里程mile为空，请检查";
				}else if(!RegularExpressionUtils.matches(mile, RegularExpressionUtils.doublePattern)){
					msg = "本次行驶里程mile不是正整数或小数，请检查";
				}
			}
			
			if(!"".equals(msg)){
				this.json = "{\"result\":0,\"message\":"+"msg"+"}";
				return JSON_MESSAGE;
			}
			org.json.JSONObject obj = carHodometerManager.updateCarHodometer(carHodometer);
			if(obj != null){
				this.json = obj.toString();
			} else {
				this.json = "{\"result\":0,\"message\":"+"添加失败"+"}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("updateCarHodometer has errors", e);
			this.showErrorJson(e.getMessage());
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 上传车辆行程
	 * @return
	 */
	public String uploadNormalAndRetryCarHodometer(){
		String carHodometer = new String();
		try {
			HttpServletRequest request = getRequest();
			try{
				carHodometer = getBody(request);
				this.logger.error("updateCarHodometer:" + carHodometer);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
			String msg = "";
			if(StringUtil.isNull(carHodometer)){
				msg = "入参为空，请检查";
			}else if(carHodometer.indexOf("{") == -1 || carHodometer.indexOf("}") == -1){
				msg = "行程不是json字符串，请检查";
			}else{
				org.json.JSONObject travelObj = new org.json.JSONObject(carHodometer);
				JSONArray hodometerArray = JSONArray.fromObject(travelObj.getString("travel_list"));
				for(int i=0; i<hodometerArray.size(); i++){
					net.sf.json.JSONObject obj = net.sf.json.JSONObject.fromObject(hodometerArray.get(i));
					String carplate = obj.getString("carplate") == null ? "" : obj.getString("carplate");
					String gpsinfo = obj.getString("gpsinfo") == null ? "" : obj.getString("gpsinfo");
					String mile = obj.getString("mile") == null ? "" : obj.getString("mile");
					if(StringUtil.isNull(carplate)){
						msg = "车牌号为空，请检查";
						break;
					}else if(carInfoManager.getCarInfoByCarplate(carplate).size() == 0){
						msg = "该车牌号"+carplate+"不存在，请检查";
						break;
					}else if(StringUtil.isNull(gpsinfo)){
						msg = "gpsinfo信息json字符串为空，请检查";
						break;
					}else if(gpsinfo.indexOf("{") == -1 || gpsinfo.indexOf("}") == -1){
						msg = "gpsinfo不是json字符串，请检查";
						break;
					}else if(StringUtil.isNull(mile)){
						msg = "本次行驶里程mile为空，请检查";
						break;
					}else if(!RegularExpressionUtils.matches(mile, RegularExpressionUtils.doublePattern)){
						msg = "本次行驶里程mile不是正整数或小数，请检查";
						break;
					}
				}
			}
			
			if(!"".equals(msg)){
				this.json = "{\"result\":0,\"message\":"+"msg"+"}";
				return JSON_MESSAGE;
			}
			
			org.json.JSONObject obj = carHodometerManager.uploadNormalAndRetryCarHodometer(carHodometer);
			this.json = "{\"result\":1,\"data\":"+obj.toString()+"}";
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("updateCarHodometer has errors", e);
			this.json = "{\"result\":0,\"message\":"+"添加失败"+"}";
			this.showErrorJson(json);
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 根据车牌号和时间段获取车辆行驶情况报告
	 * 入参：
	 * 	carplate 车牌号
	 *  starttime 起始时间
	 *  endtime 结束时间
	 * @return
	 */
	public String getDrivingReportByCarplate(){
		String carplate = getRequest().getParameter("carplate");
		String starttime = getRequest().getParameter("querydate");
		try {
			JSONArray array = carHodometerManager.getDrivingReportByCarplate(carplate, starttime);
			if(array.size() > 0){
				this.json = "{\"result\":1,\"data\":"+ array.toString() +"}";
			} else {
				this.json = "{\"result\":0,\"message\":\"获取昨日出行报告失败\"}";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			this.logger.error("getDrivingReportByCarplate has errors", e);
			this.showErrorJson(e.getMessage());
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取车辆近期收益  近一周的收益统计
	 * @date 2016年9月21日 上午9:40:28
	 * @return
	 */
	public String getCarRecentGain(){
		try {
			String carplate = getRequest().getParameter("carplate");
			
			//输入校验
			String msg = "";
			if(StringUtil.isNull(carplate)){
				msg = "车牌号为空，请检查";
			}else if(carInfoManager.getCarInfoByCarplate(carplate).size() == 0){
				msg = "该车牌号"+carplate+"不存在，请检查";
			}
			
			JSONObject obj = new JSONObject();
			if(!StringUtil.isNull(msg)){
				obj.put("result", 0);
				obj.put("message", msg);
			}else{
				obj = carHodometerManager.getCarRecentGain(carplate);
			}
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，获取数据失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取车辆行驶数据分析  1 奖励、2 油耗、3 里程、4 驾驶时长，按周、月、年统计
	 * @date 2016年9月21日 上午9:35:48
	 * @return
	 */
	public String getCarTravelDataAnalyse(){
		try {
			String carplate = getRequest().getParameter("carplate");
			String date_type = getRequest().getParameter("date_type");
			String matchStr = "week,month,year";
			
			//输入校验
			String msg = "";
			if(StringUtil.isNull(carplate)){
				msg = "车牌号为空，请检查";
			}else if(carInfoManager.getCarInfoByCarplate(carplate).size() == 0){
				msg = "该车牌号"+carplate+"不存在，请检查";
			}else if(StringUtil.isNull(date_type)){
				msg = "查询日期类型date_type为空，请检查";
			}else if(matchStr.indexOf(date_type) < 0){
				msg = "查询日期类型date_type只能是week(周)、month(月)、year(年)";
			}
			
			JSONObject obj = new JSONObject();
			if(!StringUtil.isNull(msg)){
				obj.put("result", 0);
				obj.put("message", msg);
			}else{
				obj = carHodometerManager.getCarTravelDataAnalyse(carplate, date_type);
			}
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，获取数据失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 测试行程图片生成 
	 * @date 2016年10月24日 下午3:39:46
	 * @return
	 */
	public String getHodometerImage(){
		try {
			carHodometerManager.getHodometerImage();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}

	/**
	 * 给请求返回JSON对象作为响应
	 * @return
	public void returnJson(){
		JSONObject obj = new JSONObject();
		obj.put("username", "wq");
		obj.put("password", "123456");
		this.render(obj.toString(), "text/x-json;charset=UTF-8");
	}
	*/
	public ICarHodometerManager getCarHodometerManager() {
		return carHodometerManager;
	}

	public void setCarHodometerManager(ICarHodometerManager carHodometerManager) {
		this.carHodometerManager = carHodometerManager;
	}

	public CarInfoManager getCarInfoManager() {
		return carInfoManager;
	}

	public void setCarInfoManager(CarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}
}
