package com.enation.app.shop.core.service.impl;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Comment;
import com.enation.app.shop.core.model.GoodsComment;
import com.enation.app.shop.core.service.ICommentManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CommentManager extends BaseSupport implements ICommentManager {

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveComment(Member member) {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String carplate = request.getParameter("carplate");
			String goods_grade = request.getParameter("goods_grade");
			String service_grade = request.getParameter("service_grade");
			String order_sn = request.getParameter("order_sn");
			String content = request.getParameter("content");
			int store_id = StringUtil.toInt(request.getParameter("store_id"));
			if (store_id == -1 || StringUtil.isNull(carplate) || StringUtil.isNull(order_sn)) {
				throw new RuntimeException("服务器未响应，请重试");
			}
			int isanonymity = StringUtil.toInt(request.getParameter("isanonymity"), 0);

			DecimalFormat decimalFormat = new DecimalFormat("0.0");
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
			if (!StringUtil.isNull(goods_grade)) {
				goods_grade = decimalFormat.format(goods_grade);
			} else {
				goods_grade = "0";
			}

			if (!StringUtil.isNull(service_grade)) {
				service_grade = decimalFormat.format(Double.parseDouble(service_grade));
			} else {
				service_grade = "0";
			}

			// String grade =
			// decimalFormat.format((StringUtil.toDouble(goods_grade, 0d) +
			// StringUtil.toDouble(service_grade,0d)) / 2);
			Comment comment = new Comment();
			comment.setCarplate(carplate);
			comment.setCreate_time(System.currentTimeMillis());
			comment.setStore_id(store_id);
			comment.setGrade(0d);
			comment.setMember_id(member.getMember_id());
			comment.setOrder_sn(order_sn);
			comment.setContent(content);
			comment.setIsanonymity(isanonymity);
			comment.setGoods_grade(Double.parseDouble(goods_grade));
			comment.setService_grade(Double.parseDouble(service_grade));
			this.daoSupport.insert("es_comment", comment);
			// 修改订单状态待评价改为已完成
			this.daoSupport.execute(
					"UPDATE es_order SET status = " + OrderStatus.ORDER_COMPLETE + ", complete_time = ?  WHERE sn = ?",
					DateUtil.getDateline(), order_sn);
			// 并更新商铺评价分数。
			this.daoSupport.execute(
					"update es_store set comment_grade = ( select round((sum(service_grade)/count(*)),1) from es_comment where store_id = ? ) where  store_id = ?",
					store_id, store_id);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveOrderComment(Member member, String orderComment) {
		try {
			if (StringUtil.isNull(orderComment)) {
				throw new RuntimeException("服务器未响应，请重试");
			}
			JSONObject orderCommentJson = JSONObject.fromObject(orderComment);
			String carplate = orderCommentJson.getString("carplate");
			int store_id = orderCommentJson.getInt("store_id");
			int isanonymity = orderCommentJson.getInt("isanonymity");
			String order_sn = orderCommentJson.getString("order_sn");
			DecimalFormat decimalFormat = new DecimalFormat("0.0");
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
			// 服务评价
			JSONObject service_comment = orderCommentJson.getJSONObject("service_comment");
			String service_content = service_comment.getString("content");
			double service_grade = service_comment.getDouble("service_grade");
			service_content = StringUtil.filterEmoji(service_content);
			if(StringUtil.isNull(service_content)){
				service_content = "好评";
			}
			Comment comment = new Comment();
			comment.setCarplate(carplate);
			comment.setCreate_time(System.currentTimeMillis());
			comment.setStore_id(store_id);
			comment.setGrade(0d);
			comment.setMember_id(member.getMember_id());
			comment.setOrder_sn(order_sn);
			comment.setContent(service_content);
			comment.setIsanonymity(isanonymity);
			comment.setGoods_grade(0d);
			comment.setService_grade(service_grade);
			this.daoSupport.insert("es_comment", comment);
			// 修改订单状态未评价为已评价
			this.daoSupport.execute(
					"UPDATE es_order SET  is_comment = 1 where sn = ?",
					 order_sn);
			// 并更新商铺评价分数。
			this.daoSupport.execute(
					"update es_store set comment_grade = ( select round((sum(service_grade)/count(*)),1) from es_comment where store_id = ? ) where  store_id = ?",
					store_id, store_id);

			// 商品评价
			JSONArray goods_comments = orderCommentJson.getJSONArray("goods_comments");
			for (int i = 0; i < goods_comments.size(); i++) {
				JSONObject goods_comment = goods_comments.getJSONObject(i);
				double goods_grade = goods_comment.getDouble("goods_grade");
				String goods_content = goods_comment.getString("goods_content");
				goods_content = StringUtil.filterEmoji(goods_content);
				if(StringUtil.isNull(goods_content)){
					goods_content = "好评";
				}
				int goods_id = goods_comment.getInt("goods_id");
				int product_id = goods_comment.getInt("product_id");
				GoodsComment goodsComment = new GoodsComment();
				goodsComment.setCarplate(carplate);
				goodsComment.setCreate_time(System.currentTimeMillis());
				goodsComment.setStore_id(store_id);
				goodsComment.setGoods_id(goods_id);
				goodsComment.setProduct_id(product_id);
				goodsComment.setMember_id(member.getMember_id());
				goodsComment.setContent(goods_content);
				goodsComment.setIsanonymity(isanonymity);
				goodsComment.setCreate_time(System.currentTimeMillis());
				goodsComment.setGoods_grade(goods_grade);
				goodsComment.setOrder_sn(order_sn);
				this.daoSupport.insert("es_goods_comment", goodsComment);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}


	@Override
	public Page listGoodsCommentByLevel(int pageNo, int pageSize, int goods_id, String level) {
		Page page = null;
		try {
			StringBuffer sql = new StringBuffer(
					"select egc.*,m.face,m.username from es_goods_comment egc ,es_member m where egc.goods_id = ? AND m.member_id = egc.member_id  ");
			if (Integer.parseInt(level) == 1 ) {
			} else if (Integer.parseInt(level) == 2) {
				sql.append(" AND goods_grade > 3");
			} else if (Integer.parseInt(level) == 3) {
				sql.append(" AND goods_grade = 3");
			} else if (Integer.parseInt(level) == 4) {
				sql.append(" AND goods_grade < 3");
			}
			sql.append(" order by egc.create_time desc");
			page = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, goods_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}

	@Override
	public int count(int level, String goods_id) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select count(*) num from es_goods_comment where 1=1 ");
			if (level == 1) {// 所有

			} else if (level == 2) {// 好评
				sql.append(" AND goods_grade > 3");
			} else if (level == 3) {
				sql.append(" AND goods_grade = 3");
			} else if (level == 4) {
				sql.append(" AND goods_grade < 3");
			}
			sql.append(" AND goods_id = ? order by create_time desc");
			return daoSupport.queryForInt(sql.toString(), goods_id);
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public Page listServiceComment(Map<String,Object> result) {
		String storeId = (String)result.get("storeId");
		String userInfo = (String)result.get("userInfo");
		String startTime = (String)result.get("startTime");
		String endTime = (String)result.get("endTime");
		String order_sn = (String)result.get("order_sn");
		String pageNo = (String)result.get("page");
		String pageSize = (String)result.get("pageSize");
		StringBuffer sql = new StringBuffer();
		sql.append("select ec.*,em.username,em.fullname,em.face,(ec.create_time div 1000) time");
		sql.append("  FROM es_comment ec,es_member em WHERE  ec.member_id = em.member_id  AND ec.store_id = ? ");
		if(!StringUtil.isNull(userInfo)){
			sql.append(" AND (em.fullname like '%").append(userInfo).append("%'");
			sql.append(" or em.username like '%").append(userInfo).append("%')");
		}
		if(!StringUtil.isNull(order_sn)){
			sql.append(" AND ec.order_sn like '%").append(order_sn).append("%'");
		}
		if(!StringUtil.isNull(startTime)){
			startTime+=" 00:00:00";
			long time = DateUtil.toDate(startTime, "yyyy-MM-dd hh:mm:ss").getTime();
			sql.append(" AND ec.create_time >= '").append(time).append("'");
		}
		if(!StringUtil.isNull(endTime)){
			endTime +=" 23:59:59";
			long  time =  DateUtil.toDate(endTime, "yyyy-MM-dd hh:mm:ss").getTime();
			sql.append(" AND ec.create_time <= '").append(time).append("'");
		}
		sql.append(" ORDER by ec.create_time  desc");
		return daoSupport.queryForPage(sql.toString(), Integer.parseInt(pageNo), Integer.parseInt(pageSize),storeId );
	}

	@Override
	public List<Map> getCommentByOrderId(String ordersn) {
		try{
			String sql = "select (e.create_time div 1000) time,e.* from es_comment  e where e.order_sn  = ? ";
			return  daoSupport.queryForList(sql, ordersn);
		}catch(Exception e){
			return Collections.emptyList();
		}
	
	}

}
