package com.enation.app.shop.core.service;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.enation.framework.database.Page;

/**
 * 车辆行程信息接口
 * @author wangqiang 2016年4月8日 下午3:04:35
 *
 */
public interface ICarHodometerManager {

	/**
	 * 新增/修改车辆行程信息
	 * @param carHodoMeter 
	 * @return JSONObject
	 * @throws Exception 
	 */
	JSONObject updateCarHodometer(String carHodometer) throws Exception;
	
	/**
	 * 获取车辆行程信息
	 * @param carPlate
	 * @param startTime
	 * @param endTime
	 * @param startIndex
	 * @param pageSize
	 * @return
	 */
	public List getCarHodometerInfo(String carPlate, String startTime, String endTime, int startIndex, String pageSize);

	/**
	 * 获取今日收益和其他信息
	 * @param carplate 
	 * @param long1
	 * @param long2
	 * @return
	 */
	net.sf.json.JSONObject getTodayGainsAndOtherInfo(String carplate, Long starttime, Long endtime);

	/**
	 * 昨日出行报告
	 * @param carplate
	 * @param starttime
	 * @return
	 */
	net.sf.json.JSONArray getDrivingReportByCarplate(String carplate, String starttime);

	/**
	 * @description 上传正常和app缓存的失败行程
	 * @date 2016年8月31日 下午3:43:16
	 * @param carHodometer
	 * @return
	 * @throws Exception 
	 */
	org.json.JSONObject uploadNormalAndRetryCarHodometer(String carHodometer) throws Exception;

	/** @description  根据Map参数获取车辆行程信息
	 * @date 2016年8月22日 下午1:55:40
	 * @param paramMap
	 * @return Page
	 */
	Page getCarHodometerList(Map paramMap);

	/**
	 * @description 获取车辆近期收益  近一周的收益统计
	 * @date 2016年9月21日 上午9:56:38
	 * @param carplate
	 * @return
	 */
	net.sf.json.JSONObject getCarRecentGain(String carplate);

	/**
	 * @description 获取车辆行驶数据分析  1 奖励、2 油耗、3 里程、4 驾驶时长，按周、月、年统计
	 * @date 2016年9月21日 上午11:59:53
	 * @param carplate  车牌号
	 * @param date_type 查询日期类型  week  按周    month  按月  year  按年
	 * @return
	 */
	net.sf.json.JSONObject getCarTravelDataAnalyse(String carplate, String date_type);

	void getHodometerImage();
}