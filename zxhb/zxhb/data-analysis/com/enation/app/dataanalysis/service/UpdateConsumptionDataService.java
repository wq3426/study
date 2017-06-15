package com.enation.app.dataanalysis.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.base.core.model.ConsumptionStatistics;
import com.enation.app.dataanalysis.dao.DataAnalysisDao;
import com.enation.app.dataanalysis.exception.DataAnalysisException;
import com.enation.eop.sdk.utils.DateUtils;

/**
 * @Description 更新油耗数据
 *
 * @createTime 2016年9月13日 下午2:42:48
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
@Service
public class UpdateConsumptionDataService  {

	@Autowired
	private DataAnalysisDao dataAnalysisDao;
	
	
	/**
	 * @description 执行油耗数据统计
	 * @date 2016年9月13日 下午7:14:40
	 * @return
	 */
	public int handleAllCarConsumptionData() {
		
		//获取当前时间
		String nowDate = DateUtils.getNowDate();
		
		//查询所有的车辆
		List<Map<String, String>> carplateList = dataAnalysisDao.queryAllCarplate();
		
		for (Map<String, String> carplateMap : carplateList) {
			String carplate = carplateMap.get("carplate");
			try {
				updateConsumptionData(nowDate, carplate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 1;
	}
	/**
	 * @description 更新油耗统计数据
	 * @date 2016年9月13日 下午6:22:56
	 * @param currentDate
	 * @param carplate
	 * @return int
	 * @throws Exception
	 */
	public int updateConsumptionData(String currentDate, String carplate) throws Exception {
		
		//获取该车某天的总油耗，计算该天的平均油耗
		Map<String, Double> currentDateConsumption = dataAnalysisDao.queryCurrentDateConsumption(currentDate, carplate);
		
		//获取该车该日期之前7天的总油耗，并计算周平均油耗
		String lastSevenDays = DateUtils.calculateDateAddDays(currentDate,-7);//计算前7天的日期
		Map<String, Double> lastSevenDaysConsumption = dataAnalysisDao.queryLastDaysConsumption(lastSevenDays, currentDate, carplate , 7);

		//获取该车该日期之前30天的总油耗，并计算月平均油耗
		String lastThirtyDays = DateUtils.calculateDateAddDays(currentDate,-30);//计算前30天的日期
		Map<String, Double> lastThirtyDaysConsumption = dataAnalysisDao.queryLastDaysConsumption(lastThirtyDays, currentDate, carplate, 30);
		
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
		boolean isExist = dataAnalysisDao.checkCarIsExist(carplate);
		if(isExist) {
			//更新油耗数据统计表
			result = dataAnalysisDao.updateConsumptionData(consumptionStatistics);
		} else {
			//插入数据到油耗数据统计表
			result = dataAnalysisDao.insertConsumptionData(consumptionStatistics);
		}
		
		if(result != 1) {
			throw new DataAnalysisException("油耗数据统计表操作数据异常");
		}
		return result;
	}

	

}
