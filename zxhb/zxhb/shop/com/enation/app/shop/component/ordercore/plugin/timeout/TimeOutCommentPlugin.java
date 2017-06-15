package com.enation.app.shop.component.ordercore.plugin.timeout;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.plugin.job.IErverDayZeroThirtyExecuteEvent;
import com.enation.app.shop.core.model.Comment;
import com.enation.app.shop.core.model.GoodsComment;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.OrderType;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

import net.sf.json.JSONArray;
/**
 * 发货后，或者服务完成后10*24小时，未确认自动确认
 * @author LiFenLong
 *
 */
@Component
public class TimeOutCommentPlugin extends AutoRegisterPlugin implements IErverDayZeroThirtyExecuteEvent {
	private IDaoSupport daoSupport;
	private IOrderManager orderManager;
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void everyDayZeroThirty() {
		try{
			//获得所有已支付，已服务的订单，服务时间已经超过10天
			long tenDays = 3600 * 24 * 10 ; 
			String sql = "SELECT *FROM es_order o  where (?-service_time)> ? and o.store_id IS NOT NULL AND o.`status` = ? and o.pay_status = ?";
			//得到服务完成状态的订单,超过十天表示用户自动确认.
			List<Map> ordersList = daoSupport.queryForList(sql,DateUtil.getDateline(),tenDays,OrderStatus.ORDER_APPRAISE,OrderStatus.PAY_CONFIRM);
			String sql2 = "SELECT *FROM es_order o  where (?-ship_time)> ? and o.store_id IS NOT NULL AND o.`status` = ? and o.pay_status = ? and o.shipping_method=1 and o.order_type in ("+
					OrderType.GOODS+","+OrderType.ZA_GOODS+")"; 
			//得到邮寄到用户地址，已发货状态的订单
			List<Map> ordersList2 = daoSupport.queryForList(sql,DateUtil.getDateline(),tenDays,OrderStatus.ORDER_SHIP,OrderStatus.PAY_CONFIRM);
			ordersList.addAll(ordersList2);
			if(ordersList!=null && ordersList.size()>0){
				for(int i = 0 ; i < ordersList.size() ; i++){
					daoSupport.execute("update  es_order o set o.status = ? , complete_time = ? WHERE sn = ?", OrderStatus.ORDER_COMPLETE,DateUtil.getDateline(),ordersList.get(i).get("sn"));
				}
			}
			
			
			//中安寄到4S店 类型，10天后自动已备货
			sql = "SELECT *FROM es_order o  where (?-ship_time)> ? and  o.store_id IS NOT NULL AND o.`status` = ? and o.pay_status = 2  and o.order_type = ? and o.shipping_method = 2 ";
			List<Map> ordersList3 = daoSupport.queryForList(sql, DateUtil.getDateline(),tenDays,OrderStatus.ORDER_SHIP,OrderType.ZA_GOODS);
			if(ordersList3!=null && ordersList3.size()>0){
				for(int i = 0 ; i < ordersList3.size() ; i++){
					daoSupport.execute("update  es_order o set o.status = ?  WHERE o.sn = ?", OrderStatus.ORDER_SERVECE,ordersList3.get(i).get("sn"));
				}
			}
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * 评论订单
	 * @param list
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	private void commentOrder(List<Map> orders ){
		//如果符合条件订单，自动评价。
		try{
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String content = "好评";//自动评分默认好评
			for(int i = 0 ; i < orders.size() ; i ++ ){
				Map order = orders.get(i);
				DecimalFormat decimalFormat = new DecimalFormat("0.0");
				String grade = decimalFormat.format(5);
				Comment comment = new Comment();
				comment.setCarplate((String)order.get("carplate"));
				comment.setCreate_time(System.currentTimeMillis());
				comment.setStore_id((Integer)order.get("service_store_id"));
//				comment.setGrade(Double.parseDouble(grade));
				comment.setMember_id((Integer)order.get("member_id"));
				comment.setOrder_sn((String)order.get("sn"));
				comment.setContent(content);
				comment.setIsanonymity(0);
//				comment.setGoods_grade(Double.parseDouble(grade));
				comment.setService_grade(Double.parseDouble(grade));
				this.daoSupport.insert("es_comment",comment);
				//修改订单状态待评价改为已完成
				this.daoSupport.execute("UPDATE es_order SET status = "+ OrderStatus.ORDER_COMPLETE +", complete_time = ? WHERE sn = ?", DateUtil.getDateline() , order.get("sn"));
				//并更新商铺评价分数。
				this.daoSupport.execute("update es_store set comment_grade = ( select round((sum(service_grade)/count(*)),1) from es_comment where store_id = ? ) where  store_id = ?",order.get("store_id"),order.get("store_id")); 
				commentGoods(order);
			}
		
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	public void commentGoods(Map order){
		try {
			int order_id = (int) order.get("order_id");
			String order_sn = (String) order.get("sn");
			String carplate = (String) order.get("carplate");
			int store_id = (int) order.get("store_id");
			// 获取订单里的商品信息，给商品自动评价
			String sql = "select *from es_order_items  where order_id = ?";
			List<Map> goodsList = daoSupport.queryForList(sql, order_id);
			for (int i = 0; i < goodsList.size(); i++) {
				Map goodsMap = goodsList.get(0);
				GoodsComment goodsComment = new GoodsComment();
				goodsComment.setCarplate(carplate);
				goodsComment.setCreate_time(System.currentTimeMillis());
				goodsComment.setStore_id(store_id);
				goodsComment.setGoods_id((int) goodsMap.get("goods_id"));
				goodsComment.setProduct_id((int) goodsMap.get("product_id"));
				goodsComment.setMember_id((int) order.get("member_id"));
				String goods_content = "好评";
				goodsComment.setContent(goods_content);
				goodsComment.setIsanonymity(0);
				goodsComment.setGoods_grade(5.0);
				goodsComment.setOrder_sn(order_sn);
				this.daoSupport.insert("es_goods_comment", goodsComment);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

}
