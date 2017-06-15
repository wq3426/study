package com.enation.app.shop.core.service.statistics.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.statistics.IFlowStatisticsManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.util.DateUtil;

@Component
public class FlowStatisticsManager extends BaseSupport implements IFlowStatisticsManager {

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IFlowStatisticsManager#addFlowLog(int)
	 */
	@Override
	public void addFlowLog(int goodsId) {
		
		// 获取当前时间戳
		long timeStamp = DateUtil.getDateline();
		String sql = "INSERT INTO es_flow_log(goods_id, visit_time) VALUES(?, ?)";
		this.daoSupport.execute(sql, goodsId, timeStamp);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IFlowStatisticsManager#getFlowStatistics(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getFlowStatistics(String statisticsType, String startDate,
			String endDate) {
		String dateWhere = ""; // 时间条件
		String dateType = "%d";	//时间类型 按年查询就显示 12个月 按月查询就显示天数

		// 如果包含开始时间条件
		if (startDate != null && !"".equals(startDate)) {

			dateWhere += "AND visit_time >= " + startDate;
		}
		
		// 如果包含结束时间条件
		if (endDate != null && !"".equals(endDate)) {

			dateWhere += " AND visit_time <= " + endDate;
		}
		
		// 如果是按照年份查询
		if ("1".equals(statisticsType)) {
			dateType = "%m";
		}
	
		String sql = "SELECT count(flow_id) AS num, CONVERT( FROM_UNIXTIME(visit_time, '"
				+ dateType
				+ "'), SIGNED )AS day_num "
				+ "FROM es_flow_log "
				+ "WHERE 1=1 "
				+ dateWhere
				+ " GROUP BY day_num ORDER BY day_num";
		
		List<Map<String, Object>> data = this.daoSupport.queryForList(sql);
		
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IFlowStatisticsManager#getGoodsFlowStatistics(int, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getGoodsFlowStatistics(int topNum, String startDate,
			String endDate) {
		String dateWhere = ""; // 时间条件

		// 如果包含开始时间条件
		if (startDate != null && !"".equals(startDate)) {

			dateWhere += "AND f.visit_time >= " + startDate;
		}
		// 如果包含结束时间条件
		if (endDate != null && !"".equals(endDate)) {

			dateWhere += " AND f.visit_time <= " + endDate;
		}

		String sql = "SELECT count(f.flow_id)AS num, g.NAME FROM es_flow_log f "
				+ "LEFT JOIN es_goods g ON f.goods_id = g.goods_id "
				+ "WHERE 1 = 1 "
				+ dateWhere
				+ " GROUP BY f.goods_id ORDER BY num DESC LIMIT 0," + topNum;
		
		List<Map<String, Object>> data = this.daoSupport.queryForList(sql);
		
		return data;
	}
	
	

}
