package com.enation.app.b2b2c.core.service.member;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.zxhb.OrderAddress;
import com.enation.app.b2b2c.core.model.zxhb.OrderDetail;
import com.enation.app.b2b2c.core.model.zxhb.OrderUser;

import net.sf.json.JSONObject;

/**
 * 多用户版会员管理类
 * @author LiFenLong
 *
 */
public interface IStoreMemberManager {
	public static final String CURRENT_STORE_MEMBER_KEY="curr_store_member";
	/**
	 * 修改会员信息
	 * @param member
	 */
	public void edit(StoreMember member);
	
	/**
	 * 获取店铺会员
	 * @param member_id
	 * @return StoreMember
	 */
	public StoreMember getMember(Integer member_id);
	/**
	 * 获取店铺会员
	 * @param member_name
	 * @return
	 */
	public StoreMember getMember(String member_name);
	
	/**
	 * 获取当前登录的会员
	 * @author LiFenLong
	 * @return StoreMember
	 */
	public StoreMember getStoreMember();

	public StoreMember getStoreMemberByStoreId(Integer store_id);

	/**
	 * @description 更新登录密码
	 * @date 2016年9月29日 下午8:08:03
	 * @param user_password
	 */
	public void updateUserLoginPassword(String user_password, int store_id);

	/**
	 * @description 验证通过，注册用户，同时生成预约订单
	 * @date 2016年12月7日 下午9:29:06
	 * @param user
	 * @param order_detail
	 * @return
	 */
	public JSONObject createUserAndOrder(OrderUser user, OrderDetail order_detail) throws RuntimeException, Exception;

	/**
	 * @param mobile 
	 * @description 查询预约列表
	 * @date 2016年12月13日 下午4:49:14
	 * @return
	 */
	public JSONObject getCardOrderList(String mobile);

	/**
	 * @description 修改订单收货地址
	 * @date 2016年12月20日 上午10:45:13
	 * @param order_id
	 * @param address
	 * @return
	 */
	public JSONObject editZxOrderAddress(String order_id, String address) throws Exception;

	/**
	 * @description 支付成功，更新订单状态为已支付
	 * @date 2017年1月5日 下午3:59:12
	 * @param status
	 * @param trade_no
	 * @param pay_type
	 * @param out_trade_no
	 * @throws Exception
	 */
	public void updateOrderStatus(Integer status, String trade_no, String pay_type, String out_trade_no) throws Exception;

	/**
	 * @description 根据订单号获取该支付订单信息
	 * @date 2016年12月21日 上午9:50:23
	 * @param out_trade_no
	 * @return
	 */
	public JSONObject getCardOrderInfo(String out_trade_no);

	/**
	 * @description 新年愿望保存
	 * @date 2016年12月31日 下午4:46:09
	 * @param wisher
	 * @param wisher_telephone
	 * @param wish_items
	 * @return
	 */
	public void saveWishes(String wisher, String wisher_telephone, String wish_items) throws Exception;
	
	
}
