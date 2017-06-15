package com.enation.app.shop.core.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.ConsumptionStatistics;
import com.enation.app.shop.core.service.IHodometerDao;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.ValidateUtils;
import com.enation.framework.util.DateUtil;

/**
 * @Description 上传行程--统计油耗数据DAO
 *
 * @createTime 2016年10月8日 下午4:41:22
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
@SuppressWarnings("rawtypes")
public class HodometerDao extends BaseSupport implements IHodometerDao{

	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Double> queryCurrentDateConsumption(String currentDate, String carplate) {
		
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

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Double> queryLastDaysConsumption(String lastDays, String currentDate, String carplate, int days) {
		
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

	@SuppressWarnings("unchecked")
	@Override
	public boolean checkCarIsExist(String carplate) {
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
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public int updateConsumptionData(ConsumptionStatistics consumptionStatistics) throws Exception{
		
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
	
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public int insertConsumptionData(ConsumptionStatistics consumptionStatistics) throws Exception {
		daoSupport.insert("es_consumption_statistics", consumptionStatistics);
		return 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, String>> queryAllCarplate() {
		String sql = 
				" SELECT                   "+
				" 	t.carplate             "+
				" FROM                     "+
				" 	es_carinfo t           "+
				" WHERE 1 = 1              ";
		
		List<Map<String, String>> carplateList = daoSupport.queryForList(sql);
		return carplateList;
	}
	
	
	
	

	

}
	
	
	
	
	
	
