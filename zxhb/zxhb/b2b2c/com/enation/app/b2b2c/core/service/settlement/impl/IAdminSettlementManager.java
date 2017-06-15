package com.enation.app.b2b2c.core.service.settlement.impl;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.settlement.AdminSettlement;
import com.enation.framework.database.Page;

public interface IAdminSettlementManager {
	
	/**添加结算
	 * @param adminSettlement
	 */
	public void add(AdminSettlement adminSettlement);


	/**获取今天reckoning对应某一大类笔数
	 * @param trade_big_type
	 * @return
	 */
	public int countTodayBigTypeNum(Integer trade_big_type);


	/**根据订单号获得admin结算
	 * @param order_sn
	 * @return
	 */
	public AdminSettlement getAdminSettlementByOrder(String order_sn);


	/**获取提款申请列表
	 * @param applyList
	 * @param page
	 * @param pageSize
	 * @param sort
	 * @param order
	 * @return
	 */
	public Page apply_list(Map applyList, int page, int pageSize, String sort, String order);


	/**获取结算列表
	 * @param settlementList
	 * @param page
	 * @param pageSize
	 * @param sort
	 * @param order
	 * @return
	 */
	public Page getSettlementList(Map settlementList, int page, int pageSize, String sort, String order);


	/**获取admin余额
	 * @return
	 */
	public double getAdminBalance();


	/**获取店铺对应类型的金额
	 * @param reckoningType
	 * @param reckoningStatus
	 * @param store_id
	 * @return
	 */
	double getSettlementCountByType(Integer reckoningType, Integer reckoningStatus, Integer store_id);


	/**获取所有订单支付金额
	 * @return
	 */
	public double getOrderPayCount();


	public Map getSettlementCountByDate(long start,long end) ;


	public Map getSettlementAllCount();


	/**admin确认收款
	 * @param sn
	 */
	public void addApply(String sn);


	/**根据大类获取总的交易金额（不含手续费）
	 * @param bigType
	 * @return
	 */
	public double getSettlementCountByBigType(int bigType);


	/**根据大类和时间获取交易金额
	 * @param bigType
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public double getSettlementCountByTypeAndDate(int bigType, long startTime, long endTime);


	
	/**
	 * map中获取筛选参数，获取数量和金额
	 * @param map
	 * @return
	 */
	public Map getSettlementCountByBigType(Map map);


	/**获取下载excel表的数据
	 * @return
	 */
	public List<Map> getExcelData();


	/**获取结算detail弹窗信息
	 * @param sn
	 * @param trade_big_type
	 * @return
	 */
	public Map getSettlementDetail(String sn);


	/**
	 * 获取提现detail弹窗信息
	 * @param sn
	 * @param trade_big_type
	 * @return
	 */
	public Map getDrowMoneyDetail(String sn);


	/**获取提交汇款单信息
	 * @return
	 */
	public Map getApplyDetail(String sn);


	/** @description 获取购买settlement流水号
	 * @date 2016年9月12日 下午3:26:35
	 * @param trade_big_type
	 * @return
	 * @return int
	 */
	public int countTodayAdminTradeBigType(Integer trade_big_type);


	/** @description 发票处理页面,每个store的数据
	 * @date 2016年9月12日 下午8:44:14
	 * @return
	 * @return Page
	 */
	public Page listInvoice(int pageNo, int pageSize, Map result);


}
