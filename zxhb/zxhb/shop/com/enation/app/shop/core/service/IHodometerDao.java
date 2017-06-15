package com.enation.app.shop.core.service;

import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.ConsumptionStatistics;

/**
 * @Description 上传行程--统计油耗数据DAO接口
 *
 * @createTime 2016年10月8日 下午4:41:22
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public interface IHodometerDao {

	/**
	 * @description 查询当前日期的油耗数据
	 * @date 2016年9月13日 下午3:11:01
	 * @param currentDate
	 * @param carplate
	 */
	public Map<String,Double> queryCurrentDateConsumption(String currentDate, String carplate); 

	/**
	 * @description 查询当前日期之前n天的油耗数据
	 * @date 2016年9月13日 下午3:52:23
	 * @param lastSevenDay
	 * @param carplate
	 * @return Map<String, Double>
	 */
	public Map<String, Double> queryLastDaysConsumption(String lastDays, String currentDate, String carplate, int days); 
	
	/**
	 * @description 校验该车是否已存在数据
	 * @date 2016年9月13日 下午4:19:59
	 * @param carplate
	 * @return
	 */
	public boolean checkCarIsExist(String carplate);
	
	/**
	 * @description 更新油耗数据
	 * @date 2016年9月13日 下午4:17:14
	 * @param consumptionStatistics
	 * @return int
	 */
	public int updateConsumptionData(ConsumptionStatistics consumptionStatistics) throws Exception;

	

	/**
	 * @description 插入油耗统计数据
	 * @date 2016年9月13日 下午5:05:16
	 * @param consumptionStatistics
	 * @return int
	 */
	public int insertConsumptionData(ConsumptionStatistics consumptionStatistics) throws Exception; 

	/**
	 * @description 查询所有的车牌号
	 * @date 2016年9月13日 下午7:28:00
	 * @return List<Map<String, String>>
	 */
	public List<Map<String, String>> queryAllCarplate(); 
	
	
	
	

	

}
	
	
	
	
	
	
