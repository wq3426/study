package com.enation.app.dataanalysis.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.ConsumptionStatistics;
import com.enation.app.fastdfsclient.sync.JdbcDaoSupport;
import com.enation.eop.sdk.utils.ValidateUtils;
import com.enation.framework.util.DateUtil;

/**
 * @Description 更新油耗数据
 *
 * @createTime 2016年9月13日 下午2:42:48
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
@Service
public class DataAnalysisDao {

	@SuppressWarnings("rawtypes")
	@Autowired
	private JdbcDaoSupport daoSupport;

	/**
	 * @description 查询当前日期的油耗数据
	 * @date 2016年9月13日 下午3:11:01
	 * @param currentDate
	 * @param carplate
	 */
	@SuppressWarnings("unchecked")
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

	/**
	 * @description 查询当前日期之前n天的油耗数据
	 * @date 2016年9月13日 下午3:52:23
	 * @param lastSevenDay
	 * @param carplate
	 * @return Map<String, Double>
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Double> queryLastDaysConsumption(String lastDays, String currentDate, String carplate, int days) {
		
		long starttime = DateUtil.getDateline(lastDays + " 00:00:00") * 1000;
		long endtime = DateUtil.getDateline(currentDate +" 23:59:59") * 1000;
		
		String sql = 
				" SELECT                                                                         "+
				" 	sum(t.idlingoilconsumption + t.oilconsumption) as tital_consumption,   		 "+
				" 	sum(t.idlingoilconsumption + t.oilconsumption)/"+days+" as avg_consumption,  "+
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
	 * @return
	 */
	@SuppressWarnings("unchecked")
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
	
	/**
	 * @description 更新油耗数据
	 * @date 2016年9月13日 下午4:17:14
	 * @param consumptionStatistics
	 * @return int
	 */
	@Transactional(propagation=Propagation.REQUIRED)
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

	

	/**
	 * @description 插入油耗统计数据
	 * @date 2016年9月13日 下午5:05:16
	 * @param consumptionStatistics
	 * @return int
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	public int insertConsumptionData(ConsumptionStatistics consumptionStatistics) throws Exception {
		daoSupport.insert("es_consumption_statistics", consumptionStatistics);
		return 1;
	}

	/**
	 * @description 查询所有的车牌号
	 * @date 2016年9月13日 下午7:28:00
	 * @return List<Map<String, String>>
	 */
	@SuppressWarnings("unchecked")
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
	
	
	
	
	
	
