package com.enation.app.b2b2c.core.service;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.base.core.model.Member;
import com.enation.framework.database.Page;

/**
 * 店铺促销管理接口
 * @author xulipeng
 * 2015年1月12日23:07:19
 */
public interface IStorePromotionManager {

	/**
	 * 添加满减优惠
	 * @return
	 */
	public void add_FullSubtract(StoreBonus bonus);
	
	/**
	 * 会员领取优惠卷
	 * @param member	会员
	 * @param type_id	优惠卷id
	 * @param bonusid 
	 */
	public void receive_bonus(Integer member_id,Integer store_id, Integer type_id);
	
	/**
	 * 获取优惠劵
	 * @param type_id
	 * @return
	 */
	public StoreBonus getBonus(Integer type_id);
	
	/**
	 * 获取会员领取的优惠劵的数量
	 * @param type_id
	 * @return
	 */
	public int getmemberBonus(Integer type_id,Integer memberid);
	
	/**
	 * 修改优惠劵
	 * @param bonus
	 */
	public void edit_FullSubtract(StoreBonus bonus);
	
	/**
	 * 删除优惠劵
	 * @param bonus_id 
	 * @param bonus
	 */
	public void deleteBonus(Integer type_id, Integer bonus_id);
	
	/**
	 * 获取会员所有未使用优惠卷
	 * @param memberId
	 * @return
	 */
	public List<StoreBonus> getBonusByMemberId(int memberId);
	
	/**
	 * 发布优惠券信息
	 * @param bonusType
	 * @throws Exception 
	 */
	public void changeStatus(StoreBonus storeBonus) throws Exception;
	
	/**
	 * 根据用户绑定的4s店Id获取优惠券信息
	 * @param storeId
	 * @param member 
	 * @return
	 */
	public List<StoreBonus> getBonusByStoreId(int storeId, Member member);
	/**
	 *app端用户领取的优惠券信息 ，1：已经领取 2：没有使用的优惠券
	 *store_id用户绑定的4s店id
	 * @param pageSize 
	 * @param pageSize2 
	 */
	public Page getBonusBymemberId(Integer member_id, int page, int pageSize);
	/**
	 * 获取用户信息
	 * @param member_id
	 * @return
	 */
	public StoreMember getStoreMember(Integer member_id);
	/**
	 *app端用户支付时加载优惠券信息 ，
	 *1：已经领取  2：绑定的4s店铺
	 *3：最低消费金额小于等于支付金额的优惠券4：没有使用的优惠券
	 *store_id用户绑定的4s店id
	 *payMoney用户支付的金额
	 * @param nowTime 
	 */
	public List<Map> getBonusInPay(Integer member_id, Integer store_id, double payMoney, long nowTime);

	
	
	/**根据订单获取绑定的优惠券
	 * @param member_id
	 * @param store_id
	 * @param order_id
	 * @return
	 */
	public Map getStoreBonusByOrder(int member_id, int store_id, int order_id);

	public Page getBonusByStoreIdAndPage(int storeId, Member member, Integer bonusPage, int pageSize);




}
