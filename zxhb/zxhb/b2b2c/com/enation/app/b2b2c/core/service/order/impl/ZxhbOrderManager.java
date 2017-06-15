package com.enation.app.b2b2c.core.service.order.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.RepairOrderDetail;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.model.zxhb.OrderDetail;
import com.enation.app.b2b2c.core.model.zxhb.Shipping;
import com.enation.app.b2b2c.core.pluin.order.StoreCartPluginBundle;
import com.enation.app.b2b2c.core.service.IStoreOrderFlowManager;
import com.enation.app.b2b2c.core.service.IStorePromotionManager;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.StoreCartKeyEnum;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.b2b2c.core.service.order.IZxhbOrderManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.base.core.model.Store;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.model.CarInfo;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PaymentDetail;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.InsureType;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.OrderType;
import com.enation.app.shop.core.service.impl.CarInfoManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 多店铺订单管理类<br>
 * 负责多店铺订单的创建、查询
 * 
 * @author kingapex
 * @version 2.0: 对价格逻辑进行改造 2015年8月21日下午1:49:27
 */
@Component
public class ZxhbOrderManager extends BaseSupport implements IZxhbOrderManager {

	@Override
	public void addShipping(Shipping shipping) {
		try {
			daoSupport.insert("shipping", shipping);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Page listOrder(Map orderMap, int page, int pageSize, String sort, String order) {
		String sql = createTempSql(orderMap, sort, order);
		Page webPage = daoSupport.queryForPage(sql, page, pageSize);
		return webPage;
	}

	/**
	 * 生成查询sql
	 * 
	 * @param map
	 * @param sortField
	 * @param sortType
	 * @return
	 */
	private String createTempSql(Map map, String sortField, String sortType) {
		String keyword = (String) map.get("keyword");
		String order_sn = (String) map.get("order_sn");
		String referee = (String) map.get("referee");
		String ta_spec_value = (String) map.get("ta_spec_value");
		String spec_value = (String) map.get("spec_value");
		String username = (String) map.get("username");
		String user_telephone = (String) map.get("user_telephone");
		String start_time = (String) map.get("start_time");
		String end_time = (String) map.get("end_time");
		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT " + " od.*, od.receive_user username, " + " ou.user_telephone, " + " sv.spec_value, "
				+ " esv.spec_value ta_spec_value, " + " s.shipping_name " + " FROM " + " 	order_detail od "
				+ " INNER JOIN order_user ou ON od.user_id = ou.user_id "
				+ " INNER JOIN es_spec_values sv ON od.gg_spec_value_id = sv.spec_value_id "
				+ " INNER JOIN es_spec_values esv ON od.ta_spec_value_id = esv.spec_value_id "
				+ " LEFT JOIN shipping s on od.shipping_id = s.id WHERE 1=1");
		if (!StringUtil.isNull(username)) {
			sql.append(" and od.username like '%" + username + "%'");
		}
		if (!StringUtil.isNull(user_telephone)) {
			sql.append(" and ou.user_telephone like '%" + user_telephone + "%'");
		}
		if (!StringUtil.isNull(order_sn)) {
			sql.append(" and od.order_sn like '%" + order_sn + "%' ");
		}
		if (!StringUtil.isNull(referee)) {
			sql.append(" and od.referee like '%" + referee + "%' ");
		}
		if (!StringUtil.isNull(keyword)) {
			sql.append(" and  (od.order_sn like '%" + keyword + "%' or od.receive_user like '%" + keyword + "%')");
		}
		if (!StringUtil.isNull(spec_value)) {
			sql.append(" and sv.spec_value like '%" + spec_value + "%'");
		}
		if (!StringUtil.isNull(spec_value)) {
			sql.append(" and esv.spec_value like '%" + ta_spec_value + "%'");
		}
		if (!StringUtil.isNull(start_time)) {
			long start = DateUtil.getDatelineTwo(start_time);
			sql.append(" and od.create_time >= " + start);
		}
		if (!StringUtil.isNull(end_time)) {
			long end = DateUtil.getDateline(end_time + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
			end *= 1000;
			sql.append(" and od.create_time <= " + end);
		}

		sql.append(" ORDER BY od." + sortField + " " + sortType);
		return sql.toString();
	}

	@Override
	public JSONObject getBaiduShippingApi(String shipping_type, String shipping_no) {
		URL url = null;
		HttpURLConnection connection = null;
		String strUrl = "http://apis.baidu.com/netpopo/express/express1";
		String apiKey = "8537c630771395fac39ffb1252c591d5";
		String param = "type=" + shipping_type + "&number=" + shipping_no;
		try {
			url = new URL(strUrl + "?" + param);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setUseCaches(false);
			connection.setRequestProperty("apikey", apiKey);
			connection.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}

			reader.close();
			JSONObject object = JSONObject.fromObject(buffer.toString());
			return object;
		} catch (Exception e) {
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	
	
	@Override
	public Map getSpecificsBySn(String order_sn) {
		String sql = " SELECT " +
				 " od.*, od.receive_user username, " 	 +
				 " ou.user_telephone, "  +
				 " sv.spec_value, "  +
				 " esv.spec_value ta_spec_value, "  +
				 " s.shipping_name, "  +
				 " s.shipping_type, " +
				 " g.intro, "  +
				 " g.name, "  +
				 " esv.spec_image "  +
				 " FROM "  +
				 " order_detail od "  +
				 " INNER JOIN order_user ou ON od.user_id = ou.user_id "  +
				 " INNER JOIN es_spec_values sv ON od.gg_spec_value_id = sv.spec_value_id "  +
				 " INNER JOIN es_spec_values esv ON od.ta_spec_value_id = esv.spec_value_id "  +
				 " INNER JOIN es_goods g on od.goods_id = g.goods_id "  +
				 " LEFT JOIN shipping s ON od.shipping_id = s.id "  +
				 " WHERE "  +
				 " 	1 = 1 "  +
				 " and order_sn = ? "  +
				 " ORDER BY " +
				 " od.order_id DESC";
//		System.out.println(sql);
		return daoSupport.queryForMap(sql, order_sn);
	}

	@Override
	public List listShipping() {
		String sql = "select *from  shipping";
		return daoSupport.queryForList(sql);
	}

	@Override
	public OrderDetail getOrderDetailBySn(String order_sn) {
		String sql = "select *from order_detail where order_sn = ?";
		return (OrderDetail) daoSupport.queryForObject(sql, OrderDetail.class, order_sn);
	}

	@Override
	public void editOrderDetail(OrderDetail orderDetail) {
		try {
			daoSupport.update("order_detail", orderDetail, "order_sn = " + orderDetail.getOrder_sn());
		} catch (Exception e) {
			throw e;
		}
	}

}
