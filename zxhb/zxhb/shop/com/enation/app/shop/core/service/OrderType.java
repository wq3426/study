package com.enation.app.shop.core.service;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class OrderType {

	/**
	 * ------------------------------------------------------------- 订单商品类型
	 * -------------------------------------------------------------
	 */

	public static final int GOODS = 0;//店铺商品

	public static final int ZA_INSURANCE = 1;//中安保险

	public static final int REPAIR = 2;//保养

	public static final int ZA_GOODS = 3;//中安商品
	
	public static final int STORE_INSURANCE = 4;//店铺保险
	
	public static final int ZA_SERVICE = 5;//中安服务(短信等服务)
	
	public static String getOrderTypeJson() {
		JSONArray arr = new JSONArray();
		JSONObject obj = new JSONObject();

		for (int i = 0; i < getOrderTypeMap().size(); i++) {
			obj.put("orderType", i);
			obj.put("text", getOrderTypeTest(i));
			arr.add(obj);
		}

		return arr.toString();
	}

	public static Map<String, Object> getOrderTypeMap() {
		Map<String, Object> orderTypeMap = new HashMap<String, Object>();
		orderTypeMap.put("STORE_GOODS", GOODS);
		orderTypeMap.put("ZA_GOODS", ZA_GOODS);
		orderTypeMap.put("ZA_INSURANCE", ZA_INSURANCE);
		orderTypeMap.put("STORE_INSURANCE", STORE_INSURANCE);
		orderTypeMap.put("REPAIR", REPAIR);
		orderTypeMap.put("ZA_SERVICE", ZA_SERVICE);
		return orderTypeMap;
	}
	
	public static Map<String,Object> getOrderTypeMapText(){
		Map<String, Object> orderTypeMap = new HashMap<String, Object>();
		orderTypeMap.put(GOODS+"", getOrderTypeTest(GOODS));
		orderTypeMap.put(ZA_INSURANCE+"", getOrderTypeTest(ZA_INSURANCE));
		orderTypeMap.put(REPAIR+"", getOrderTypeTest(REPAIR));
		orderTypeMap.put(ZA_GOODS+"", getOrderTypeTest(ZA_GOODS));
		orderTypeMap.put(STORE_INSURANCE+"", getOrderTypeTest(STORE_INSURANCE));
		orderTypeMap.put(ZA_SERVICE+"", getOrderTypeTest(ZA_SERVICE));
		return orderTypeMap;
	}
	
	public static String getOrderTypeTest(int type) {
		String text = "";

		switch (type) {
		case GOODS:
			text = "经销商-商城";
			break;
		case ZA_INSURANCE:
			text = "中安-保险";
			break;
		case REPAIR:
			text = "经销商-保养";
			break;
		case ZA_GOODS:
			text = "中安-商城";
			break;
		case STORE_INSURANCE:
			text = "经销商-保险";
			break;
		case ZA_SERVICE:
			text = "中安-服务";
			break;
		default:
			text = "错误状态";
			break;

		}
		return text;
	}

}
