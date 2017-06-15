package com.enation.app.b2b2c.core.action.backend.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.enation.app.b2b2c.core.model.zxhb.OrderDetail;
import com.enation.app.b2b2c.core.service.order.IZxhbOrderManager;
import com.enation.eop.sdk.utils.KdniaoTrackQueryAPI;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONObject;

@Component
/**
 * 店铺订单管理
 * @author LiFenLong
 *
 */
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/b2b2c/admin/order/zxhb_order_list.html"),
	 @Result(name="editShoppingPage",type="freemarker", location="/b2b2c/admin/order/edit_shopping.html"),
	 @Result(name="specifics",type="freemarker", location="/b2b2c/admin/order/zxhb_order_specifics.html")
})
@Action("zxhbOrder")
@SuppressWarnings("unchecked")
public class ZxhbOrderAction extends WWAction{
	
	private IZxhbOrderManager zxhbOrderManager;
	
	private Map orderMap;
	
	private List shippingList;
	
	private String order_sn;
	
	private OrderDetail orderDetail;
	
	private String keyword;
	
	private String referee;
	
	private String spec_value;
	
	private String ta_spec_value;
	
	private String user_telephone;
	
	private String username;
	
	private String start_time;
	
	private String end_time;
	
	public String list(){
	
		return "list";
	}
	
	public String listJson(){
		orderMap = new HashMap();
		orderMap.put("keyword", keyword);
		orderMap.put("order_sn", order_sn);
		orderMap.put("referee", referee);
		orderMap.put("spec_value", spec_value);
		orderMap.put("ta_spec_value", ta_spec_value);
		orderMap.put("user_telephone", user_telephone);
		orderMap.put("username", username);
		orderMap.put("start_time", start_time);
		orderMap.put("end_time", end_time);
		this.webpage = this.zxhbOrderManager.listOrder(orderMap, this.getPage(),this.getPageSize(), this.getSort(),this.getOrder());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	
	public String editShoppingPage(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		order_sn = request.getParameter("order_sn");
		orderDetail = zxhbOrderManager.getOrderDetailBySn(order_sn);
		shippingList = zxhbOrderManager.listShipping();
		return "editShoppingPage";
	}
	
	public String saveShopping(){
		try{
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String shipping_id = request.getParameter("shipping_id");
			String shipping_no = request.getParameter("shipping_no");
			orderDetail = zxhbOrderManager.getOrderDetailBySn(order_sn);
			orderDetail.setShipping_id(shipping_id);
			orderDetail.setShipping_no(shipping_no);
			if(orderDetail.getStatus() == 1){
				orderDetail.setStatus(2);
			}
			zxhbOrderManager.editOrderDetail(orderDetail);
			showSuccessJson("保存成功");
		}catch(Exception e){
			showErrorJson("保存失败");
		}
		return JSON_MESSAGE;
	}
	
	/** @description 订单详细信息
	 * @date 2016年12月19日 下午4:07:21
	 * @return
	 * @return String
	 */
	public String specifics(){
		try{
			orderMap = zxhbOrderManager.getSpecificsBySn(order_sn);
			orderMap.put("spec_image", UploadUtil.replacePath((String)orderMap.get("spec_image")));
			orderMap.put("create_time_date",DateUtil.toString((long)orderMap.get("create_time"), "yyyy-MM-dd hh:mm:ss"));
			String shipping_type = (String)orderMap.get("shipping_type");
			String shipping_no = (String)orderMap.get("shipping_no");
			if(!StringUtil.isNull(shipping_type)&&!StringUtil.isNull(shipping_no) ){
				KdniaoTrackQueryAPI kdniaoTrackQueryAPI = new KdniaoTrackQueryAPI();
				String result = kdniaoTrackQueryAPI.getOrderTracesByJson(shipping_type, shipping_no);
				JSONObject shippingResult = JSONObject.fromObject(result);
				boolean success = (boolean)shippingResult.get("Success");
				if(success){//成功
					orderMap.put("shippingList",shippingResult.getJSONArray("Traces"));
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return "specifics";
	}


	public IZxhbOrderManager getZxhbOrderManager() {
		return zxhbOrderManager;
	}


	public void setZxhbOrderManager(IZxhbOrderManager zxhbOrderManager) {
		this.zxhbOrderManager = zxhbOrderManager;
	}

	public Map getOrderMap() {
		return orderMap;
	}

	public void setOrderMap(Map orderMap) {
		this.orderMap = orderMap;
	}

	public String getOrder_sn() {
		return order_sn;
	}

	public void setOrder_sn(String order_sn) {
		this.order_sn = order_sn;
	}

	public List getShippingList() {
		return shippingList;
	}

	public void setShippingList(List shippingList) {
		this.shippingList = shippingList;
	}

	public OrderDetail getOrderDetail() {
		return orderDetail;
	}

	public void setOrderDetail(OrderDetail orderDetail) {
		this.orderDetail = orderDetail;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getReferee() {
		return referee;
	}

	public void setReferee(String referee) {
		this.referee = referee;
	}

	public String getSpec_value() {
		return spec_value;
	}

	public void setSpec_value(String spec_value) {
		this.spec_value = spec_value;
	}

	public String getTa_spec_value() {
		return ta_spec_value;
	}

	public void setTa_spec_value(String ta_spec_value) {
		this.ta_spec_value = ta_spec_value;
	}

	public String getUser_telephone() {
		return user_telephone;
	}

	public void setUser_telephone(String user_telephone) {
		this.user_telephone = user_telephone;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}


		
	
}
