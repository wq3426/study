package com.enation.app.b2b2c.core.service.reckoning;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.reckoning.Reckoning;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningDetail;
import com.enation.framework.database.Page;


public interface IReckoningManager {

	/**添加结算详情
	 * @param reckoning_detail
	 * @return
	 */
	public Integer addDetail(ReckoningDetail reckoning_detail);
	
	/**添加结算
	 * @param reckoning
	 */
	public void add(Reckoning reckoning);

	/**结算订单
	 * @param double1 
	 * @param time
	 */
	public void orderSettlement(Reckoning reckoning, Double balance);
	
	/**
	 * @description 定时结算普通商品 
	 * @date 2016年10月13日 上午10:16:50
	 * @param reckoning
	 * @param balance
	 * @return void
	 */
	public void timeOutOrderSettlement(Reckoning reckoning, Double balance);
	
	/**获取要结算的订单列表
	 * @param time
	 * @return
	 */
	public List<Reckoning>  getReckoningBySettlementTime(long time);

	
	/**
	 * @param orderSn
	 * @return
	 */
	public Reckoning getReckoningByOrder(String orderSn);

	/**
	 * @param 
	 * @param store_id
	 * @return
	 */
	public double getSettlementCount(Integer reckoningType,Integer reckoningStatus, Integer store_id);
	
	/**
	 * @param parseInt
	 * @param pageSize
	 * @param store_id
	 * @param result
	 * @return
	 */
	public Page getReckoningList(int pageNo, int pageSize, Integer store_id, Map result);

	/**根据流水号查询
	 * @param sn
	 * @return
	 */
	public Reckoning getReckoningBySn(String sn);
	
	
	public List<Map> getReckonings(int store_id ,String trade_type,String trade_status,String startTime,String endTime,String sn) ;

	/** @description 更新本期已结算
	 * @date 2016年9月6日 上午10:11:03
	 * @param sn
	 * @return void
	 */
	public void saveStageno(String sn, Integer store_id);

	/** @description 得到本期结算单
	 * @date 2016年9月6日 下午2:23:07
	 * @param parseInt
	 * @param pageSize
	 * @param store_id
	 * @param result
	 * @return
	 * @return Page
	 */
	public Page getThisPeriodReckoning(int parseInt, int pageSize, Integer store_id, Map result);

	/** @description 得到往期结算单
	 * @date 2016年9月7日 上午9:46:34
	 * @param parseInt
	 * @param pageSize
	 * @param store_id
	 * @param result
	 * @return
	 * @return Page
	 */
	public Page getSettledList(int parseInt, int pageSize, Integer store_id, Map result);

	/** @description 4S店收入
	 * @date 2016年9月12日 下午3:55:19
	 * @param result
	 * @return
	 * @return Map
	 */
	public Map getReckoningByIncome(Map result);

	/** @description 4S店支出
	 * @date 2016年9月12日 下午3:55:37
	 * @param result
	 * @return
	 * @return Map
	 */
	public Map getReckoningByPay(Map result);

	/** @description  admin根据id获取4S店结算详情
	 * @date 2016年9月17日 下午1:19:21
	 * @param id
	 * @return
	 * @return Map
	 */
	public Map getTransactionDetailById(String id);

	/** @description 得到对应期号列表
	 * @date 2016年9月18日 下午3:07:31
	 * @param store_id
	 * @param result
	 * @return
	 * @return List<Map>
	 */
	List getSettledListByNo(Integer store_id, Map result);


	/** @description 得到相关店铺的本期列表
	 * @date 2016年9月18日 下午3:57:12
	 * @param store_id
	 * @param result
	 * @return
	 * @return List<Map>
	 */
	public List<Map> getThisPeriodList(int store_id, Map result);

	/** @description 统计体现次数
	 * @date 2016年9月18日 下午9:00:50
	 * @param store_id
	 * @return
	 * @return int
	 */
	public int countDrawMoneyByStoreId(Integer store_id);

	/** @description 
	 * @date 2016年9月20日 上午10:08:37
	 * @param store_id
	 * @return
	 * @return List<Map>
	 */
	public List<Map> getSettledStageNoList(Integer store_id);


}
