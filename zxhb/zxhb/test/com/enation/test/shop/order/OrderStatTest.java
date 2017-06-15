package com.enation.test.shop.order;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.enation.app.shop.core.model.statistics.DayAmount;
import com.enation.app.shop.core.model.statistics.MonthAmount;
import com.enation.app.shop.core.service.IStatisticsManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.test.SpringTestSupport;

/**
 * 统计功能测试类
 * 
 * @author lzf<br/>
 *         2010-3-9 下午05:54:51<br/>
 *         version 1.0<br/>
 */
public class OrderStatTest extends SpringTestSupport {

	private ApplicationContext context;
	private IStatisticsManager orderManager;

	@Before
	public void mock() {

		orderManager = getBean("orderManager");
		 
	}

	/**
	 * 取得当前日期所在年的 月-销售额 统计
	 */
	@Test
	public void test_statisticsMonth_Amount() {
//		List<MonthAmount> list = orderManager.statisticsMonth_Amount();
//		for (MonthAmount map : list) {
//			String key = map.getMonth();
//			//System.out.println(key + ":" + map.getAmount());
//
//		}
	}

	/**
	 * 取得指定月份所在年的 月-销售额 统计
	 */
	@Test
	public void test_statisticsMonth_Amount_withInput() {
		List<MonthAmount> list = orderManager.statisticsMonth_Amount("2010-04");
		for (MonthAmount map : list) {
			String key = map.getMonth();
			//System.out.println(key + ":" + map.getAmount());

		}
	}

	/**
	 * 取得当前日期所在月的 日-销售额 统计
	 */
	@Test
	public void test_statisticsDay_Amount() {
		List<DayAmount> list = orderManager.statisticsDay_Amount();
		for (DayAmount map : list) {
			String key = map.getDay();
			//System.out.println(key + ":" + map.getAmount());

		}
	}

	/**
	 * 取得指定月份的 日-销售额 统计
	 */
	@Test
	public void test_statisticsDay_Amount_withInput() {
		List<DayAmount> list = orderManager.statisticsDay_Amount("2010-04");
		for (DayAmount map : list) {
			String key = map.getDay();
			//System.out.println(key + ":" + map.getAmount());

		}
	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

	public IStatisticsManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IStatisticsManager orderManager) {
		this.orderManager = orderManager;
	}

}
