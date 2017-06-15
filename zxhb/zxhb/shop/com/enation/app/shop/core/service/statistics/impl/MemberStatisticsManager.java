/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：会员统计实现类
 *  修改人：Sylow
 *  修改时间：2015-09-23
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.service.statistics.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.statistics.IMemberStatisticsManager;
import com.enation.eop.sdk.database.BaseSupport;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * 会员统计实现类
 * 
 * @author Sylow
 * @version v1.0,2015-09-23
 * @since v4.0
 *
 */
@Component
public class MemberStatisticsManager extends BaseSupport implements IMemberStatisticsManager {
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.service.statistics.IMemberStatisticsManager
	 * #getOrderNumTop(int)
	 */
	@Override
	public List<Map<String, Object>> getOrderNumTop(int topNum, String startDate, String endDate) {

		// 如果排名没有值
		if (topNum == 0) {
			topNum = 15;
		}
		
		String dateWhere = "";		// 时间条件
		
		// 如果包含开始时间条件
		if (startDate != null && !"".equals(startDate)) {
			
			dateWhere += "AND o.create_time >= " + startDate;
		}
		// 如果包含结束时间条件
		if (endDate != null && !"".equals(endDate)) {
			
			dateWhere += " AND o.create_time <= " + endDate;
		}

		// 拼接Sql 条件是 网上支付且已经付款的 或者 货到付款且订单完成的 有效订单
		String sql = "SELECT m.member_id,m.`name`,m.nickname,count(o.order_id) AS num "
				+ "FROM es_order o "
				+ "LEFT JOIN es_member m ON o.member_id = m.member_id WHERE o.disabled = 0 AND ((o.payment_id = 3  AND o.status = "
				+ OrderStatus.ORDER_COMPLETE
				+ ")	OR ( o.pay_status = "
				+ OrderStatus.ORDER_PAY_CONFIRM
				+ "))"
				+ " " + dateWhere
				+ " GROUP BY member_id "
				+ "ORDER BY num DESC LIMIT 0," + topNum;

		List<Map<String, Object>> list = this.daoSupport.queryForList(sql);

		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.enation.app.shop.core.service.statistics.IMemberStatisticsManager
	 * #getGoodsNumTop(int)
	 */
	@Override
	public List<Map<String, Object>> getGoodsNumTop(int topNum, String startDate, String endDate) {
		
		// 如果排名没有值
		if (topNum == 0) {
			topNum = 15;
		}
		
		String dateWhere = "";		// 时间条件
		
		// 如果包含开始时间条件
		if (startDate != null && !"".equals(startDate)) {
			
			dateWhere += "AND o.create_time >= " + startDate;
		}
		// 如果包含结束时间条件
		if (endDate != null && !"".equals(endDate)) {
			
			dateWhere += " AND o.create_time <= " + endDate;
		}

		// 拼接Sql 条件是 网上支付且已经付款的 或者 货到付款且订单完成的 订单
		String sql = "SELECT m.member_id, m.name, m.nickname, sum(i.num) AS num FROM es_order o"
				+ " LEFT JOIN es_member m ON o.member_id = m.member_id "
				+ "LEFT JOIN es_order_items i ON i.order_id = o.order_id  "
				+ "WHERE o.disabled = 0 AND( (o.payment_id = 3 AND o.status = "
				+ OrderStatus.ORDER_COMPLETE
				+ ") OR (o.pay_status = "
				+ OrderStatus.ORDER_PAY_CONFIRM
				+ "))"
				+ " " + dateWhere
				+ " GROUP BY m.member_id ORDER BY num DESC LIMIT 0," + topNum;

		List<Map<String, Object>> list = this.daoSupport.queryForList(sql);

		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IMemberStatisticsManager#getOrderPriceTop(int)
	 */
	@Override
	public List<Map<String, Object>> getOrderPriceTop(int topNum, String startDate, String endDate) {
		
		// 如果排名没有值
		if (topNum == 0) {
			topNum = 15;
		}
		
		String dateWhere = "";		// 时间条件
		
		// 如果包含开始时间条件
		if (startDate != null && !"".equals(startDate)) {
			
			dateWhere += "AND o.create_time >= " + startDate;
		}
		// 如果包含结束时间条件
		if (endDate != null && !"".equals(endDate)) {
			
			dateWhere += " AND o.create_time <= " + endDate;
		}

		// 拼接Sql 条件是 网上支付且已经付款的 或者 货到付款且订单完成的 有效订单
		String sql = "SELECT m.member_id,m.`name`,m.nickname,sum(o.need_pay_money) AS price "
				+ "FROM es_order o "
				+ "LEFT JOIN es_member m ON o.member_id = m.member_id WHERE o.disabled = 0 AND ((o.payment_id = 3  AND o.status = "
				+ OrderStatus.ORDER_COMPLETE
				+ ")	OR ( o.pay_status = "
				+ OrderStatus.ORDER_PAY_CONFIRM
				+ "))"
				+ " " + dateWhere
				+ " GROUP BY member_id "
				+ "ORDER BY price DESC LIMIT 0," + topNum;

		List<Map<String, Object>> list = this.daoSupport.queryForList(sql);

		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IMemberStatisticsManager#getOrderPriceDis(java.util.List, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getOrderPriceDis(List<Integer> sections,
			String startDate, String endDate) {
		
		// 如果没有区间数据
		if (sections == null || sections.size() == 0) {
			
			throw new RuntimeException("区间不能为空");
		}
		
		
		String interval = "";		//INTERVAL 分组
		String dateWhere = "";		// 时间条件
		String intervalDesc = "";	//INTERVAL 描述
		List<Integer> list = new ArrayList<Integer>();	//筛选后的区间值
		
		// 如果包含开始时间条件
		if (startDate != null && !"".equals(startDate)) {
			
			dateWhere += "AND o.create_time >= " + startDate;
		}
		// 如果包含结束时间条件
		if (endDate != null && !"".equals(endDate)) {
			
			dateWhere += " AND o.create_time <= " + endDate;
		}
		
		// 遍历去除null值
		for(Integer temp : sections ) {
			if(temp != null && !"".equals(temp)) {
				list.add(temp);
			}
		}
		
		Collections.sort(list);			//先按照价格从小到大排序
		
		//遍历区间数组 拼凑interval分组和描述
		for (int i = 0; i < list.size(); i++ ) {
			int price = list.get(i);
			int nextPrice = 0;				//下一个区间的价格
			
			interval += price + ",";
			
			//如果是最后一个区间
			if (i == list.size() - 1) {
								
				intervalDesc += "'" + price + "+',";
			} else{
				
				nextPrice = list.get(i + 1);
				intervalDesc += "'" + price + "~" + nextPrice + "',";
			}
			
		}
		
		//减去最后一个逗号
		interval = interval.substring(0, interval.length() - 1);
		intervalDesc = intervalDesc.substring(0, intervalDesc.length() - 1);
		
		String sql = "SELECT count(o.order_id) AS num, elt(INTERVAL(o.need_pay_money, "
				+ interval
				+ "), "
				+ intervalDesc
				+ ")AS elt_data,o.need_pay_money FROM es_order o "
				+ "WHERE 1=1 "
				+ dateWhere
				+ " GROUP BY elt_data ORDER BY o.need_pay_money";

		List<Map<String, Object>> data = this.daoSupport.queryForList(sql);

		return data;
	}

	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IMemberStatisticsManager#getBuyFre(int, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> getBuyFre(String startDate, String endDate) {


		String dateWhere = ""; // 时间条件

		// 如果包含开始时间条件
		if (startDate != null && !"".equals(startDate)) {

			dateWhere += "AND o.create_time >= " + startDate;
		}
		// 如果包含结束时间条件
		if (endDate != null && !"".equals(endDate)) {

			dateWhere += " AND o.create_time <= " + endDate;
		}
		
		//   查询购买总人数 sql
		String totalSql = "SELECT count(member_id) AS member_num FROM ("
				+ "SELECT count(o.member_id)AS num, o.member_id FROM es_order AS o "
				+ "WHERE 1=1 "
				+ dateWhere
				+ " GROUP BY o.member_id ORDER BY num) AS pc";
		int totla = this.daoSupport.queryForInt(totalSql);
		
		//正常数据
		String sql = "SELECT " + totla + " AS total_num, num AS buy_num, count(member_id) member_num FROM "
				+ "(SELECT count(o.member_id)AS num, o.member_id FROM es_order o "
				+ "WHERE 1=1 "
				+ dateWhere
				+ " GROUP BY o.member_id ORDER BY num)AS pc GROUP BY num";
		
		List<Map<String, Object>> data = this.daoSupport.queryForList(sql);
		
		return data;
	}

	@Override
	public List<Map<String, Object>> getBuyTimeDis(String startDate,
			String endDate) {
		String dateWhere = ""; // 时间条件

		// 如果包含开始时间条件
		if (startDate != null && !"".equals(startDate)) {

			dateWhere += "AND o.create_time >= " + startDate;
		}
		// 如果包含结束时间条件
		if (endDate != null && !"".equals(endDate)) {

			dateWhere += " AND o.create_time <= " + endDate;
		}

		String sql = "SELECT count(o.order_id) AS num, CONVERT(FROM_UNIXTIME(o.create_time, '%k'),SIGNED) AS hour_num "
				+ "FROM es_order o "
				+ "WHERE 1=1 "
				+ dateWhere
				+ " GROUP BY hour_num ORDER BY hour_num";
		
		List<Map<String, Object>> data = this.daoSupport.queryForList(sql);
		
		return data;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IMemberStatisticsManager#getAddMemberNum(int)
	 */
	@Override
	public List<Map<String, Object>> getAddMemberNum(String startDate, String endDate) {
		
		String dateWhere = "";		// 时间条件
		
		// 如果包含开始时间条件
		if (startDate != null && !"".equals(startDate)) {
			
			dateWhere += "AND o.create_time >= " + startDate;
		}
		// 如果包含结束时间条件
		if (endDate != null && !"".equals(endDate)) {
			
			dateWhere += " AND o.create_time <= " + endDate;
		}

		// 拼接Sql 条件是 网上支付且已经付款的 或者 货到付款且订单完成的 有效订单
		
		String sql = "SELECT count(em.member_id) as membernum , em.e_regtime as membertime FROM "
				+ "( SELECT m.member_id,m.regtime, FROM_UNIXTIME(m.regtime, '%d') AS e_regtime FROM es_member m where m.regtime >= "+startDate +" and  m.regtime <= "+endDate +") "
				+ "as em GROUP BY em.e_regtime";

		List<Map<String, Object>> list = this.daoSupport.queryForList(sql);

		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IMemberStatisticsManager#getAddMemberNum(int)
	 */
	@Override
	public List<Map<String, Object>> getLastAddMemberNum(String lastStartDate, String lastEndDate) {
		
		String dateWhere = "";		// 时间条件
		
		// 如果包含开始时间条件
		if (lastStartDate != null && !"".equals(lastStartDate)) {
			
			dateWhere += "AND o.create_time >= " + lastStartDate;
		}
		// 如果包含结束时间条件
		if (lastEndDate != null && !"".equals(lastEndDate)) {
			
			dateWhere += " AND o.create_time <= " + lastEndDate;
		}

		// 拼接Sql 条件是 网上支付且已经付款的 或者 货到付款且订单完成的 有效订单
		
		String sql = "SELECT count(em.member_id) as membernum , em.e_regtime as membertime FROM "
				+ "( SELECT m.member_id,m.regtime, FROM_UNIXTIME(m.regtime, '%d') AS e_regtime FROM es_member m where m.regtime >= "+lastStartDate +" and  m.regtime <= "+lastEndDate +") "
				+ "as em GROUP BY em.e_regtime";

		List<Map<String, Object>> list = this.daoSupport.queryForList(sql);

		return list;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IMemberStatisticsManager#getAddMemberNum(int)
	 */
	@Override
	public List<Map<String, Object>> getAddYearMemberNum(String startDate, String endDate) {
		
		String dateWhere = "";		// 时间条件
		
		// 如果包含开始时间条件
		if (startDate != null && !"".equals(startDate)) {
			
			dateWhere += "AND o.create_time >= " + startDate;
		}
		// 如果包含结束时间条件
		if (endDate != null && !"".equals(endDate)) {
			
			dateWhere += " AND o.create_time <= " + endDate;
		}

		// 拼接Sql 条件是 网上支付且已经付款的 或者 货到付款且订单完成的 有效订单
		
		String sql = "SELECT count(em.member_id) as membernum , em.e_regtime as membertime FROM "
				+ "( SELECT m.member_id,m.regtime, FROM_UNIXTIME(m.regtime, '%m') AS e_regtime FROM es_member m where m.regtime >= "+startDate +" and  m.regtime <= "+endDate +") "
				+ "as em GROUP BY em.e_regtime";

		List<Map<String, Object>> list = this.daoSupport.queryForList(sql);

		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.statistics.IMemberStatisticsManager#getAddMemberNum(int)
	 */
	@Override
	public List<Map<String, Object>> getLastAddYearMemberNum(String lastStartDate, String lastEndDate) {
		
		String dateWhere = "";		// 时间条件
		
		// 如果包含开始时间条件
		if (lastStartDate != null && !"".equals(lastStartDate)) {
			
			dateWhere += "AND o.create_time >= " + lastStartDate;
		}
		// 如果包含结束时间条件
		if (lastEndDate != null && !"".equals(lastEndDate)) {
			
			dateWhere += " AND o.create_time <= " + lastEndDate;
		}

		// 拼接Sql 条件是 网上支付且已经付款的 或者 货到付款且订单完成的 有效订单
		
		String sql = "SELECT count(em.member_id) as membernum , em.e_regtime as membertime FROM "
				+ "( SELECT m.member_id,m.regtime, FROM_UNIXTIME(m.regtime, '%m') AS e_regtime FROM es_member m where m.regtime >= "+lastStartDate +" and  m.regtime <= "+lastEndDate +") "
				+ "as em GROUP BY em.e_regtime";

		List<Map<String, Object>> list = this.daoSupport.queryForList(sql);

		return list;
	}
	
	
	
}
