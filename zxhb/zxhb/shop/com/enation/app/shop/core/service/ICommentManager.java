package com.enation.app.shop.core.service;

import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Comment;
import com.enation.framework.database.Page;

/**
 * Descriptiion:中安评价管理
 * 
 * @author shixin
 * @date 2016年6月13日上午11:34:34
 */
public interface ICommentManager {

	/**
	 * 保存评价信息，评价信息修改为service_grade
	 */
	public void saveComment(Member member);

	/**
	 * @description 评价新借口，服务和商品评价
	 * @date 2016年8月25日 上午10:20:25
	 * @param member
	 * @return void
	 */
	public void saveOrderComment(Member member, String orderComment);


	/**
	 * @description 评价按类型显示
	 * @date 2016年8月25日 下午5:05:22
	 * @param pageNo
	 * @param pageSize
	 * @param goods_id
	 * @param type
	 * @return Page
	 */
	public Page listGoodsCommentByLevel(int pageNo, int pageSize, int goods_id, String level);

	/**
	 * @description 按类型计算商品评价数量
	 * @date 2016年8月25日 下午5:42:13
	 * @param type,goods_id
	 * @param goods_id
	 * @return number
	 */
	public int count(int level, String goods_id);

	/** @description 服务评价列表
	 * @date 2016年8月30日 下午5:27:14
	 * @param store_id
	 * @return
	 * @return List
	 */
	public Page listServiceComment(Map<String,Object> result);

	/** @description 订单页面获取评价
	 * @date 2016年8月31日 下午4:29:29
	 * @return void
	 */
	public List<Map> getCommentByOrderId(String ordersn);

}
