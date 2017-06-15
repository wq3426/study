package com.enation.app.b2b2c.core.service;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.framework.database.Page;

public interface IStoreBonusManager {
	/**
	 * 查询店铺优惠券类表
	 * @return
	 */
	List getBonusList(Integer store_id);
	
	
	/**
	 * 获取当前登录会员的优惠劵
	 * @param memberid	当前登录会员的id
	 * @param store_id	店铺id
	 * @param min_goods_amount 最小订单金额
	 * @return
	 */
	public List<Map> getMemberBonusList(Integer memberid,Integer store_id,Double min_goods_amount);
	
	/**
	 * 根据id查询
	 * @param bonusid
	 * @return
	 */
	public StoreBonus get(Integer bonusid);
	
	/**
	 * 根据条件搜索优惠券    whj 2015-06-01
	 * @param store_id2 
	 * @param pageSize 
	 */
	
	public Page getConditionBonusList(Integer pageNo,int pageSize, Integer store_id, Map map);
	
	/**
	 * 查询会员的所有优惠劵
	 * @param memberid
	 * @return
	 */
	public Page getBonusListBymemberid(int pageNo,int pageSize,Integer memberid);
	
	/**
	 * 设置已使用
	 */
	public void setBonusUsed(Integer bonus_id,Integer member_id);

	/**
	 * @description 根据店铺id,营销类型 修改店铺优惠券管理使用数量
	 * @date 2016年8月18日 下午2:40:51
	 * @param bonusType
	 * @param bonusNumType
	 * @return void
	 * @throws Exception 
	 */
	public void updateBonusStoreCost(StoreBonus storeBonus, Integer bonusNumType) throws Exception;
	
	
}
