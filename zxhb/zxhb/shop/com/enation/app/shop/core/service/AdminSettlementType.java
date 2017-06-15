package com.enation.app.shop.core.service;

import java.util.HashMap;
import java.util.Map;

import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.OrderType;
import com.oreilly.servlet.multipart.MacBinaryDecoderOutputStream;

import edu.emory.mathcs.backport.java.util.TreeMap;

public abstract class AdminSettlementType {//交易大类
	
	

	/**
	 * -------------------------------------------------------------
	 * 							交易大类
	 * -------------------------------------------------------------
	 */
	public static final int BIG_BUY_SERVICE = 1;
	public static final int BIG_DRAW_MONEY = 2;
	public static final int BIG_STORE_BUY_SERVICE = 3; 
	public static final int BIG_REFUND = 4;

	/**
	 * -------------------------------------------------------------
	 * 						交易小类 用户 购买服务
	 * -------------------------------------------------------------
	 */
	public static final int APP_STORE_GOODS = OrderType.GOODS;
	public static final int APP_ZA_INSURANCE = OrderType.ZA_INSURANCE; 
	public static final int APP_REPAIR = OrderType.REPAIR; 
	public static final int APP_ZA_GOODS = OrderType.ZA_GOODS; 
	public static final int APP_STORE_INSURANCE = OrderType.STORE_INSURANCE; 
	/**
	 * -------------------------------------------------------------
	 * 						交易小类：提现
	 * -------------------------------------------------------------
	 */	
	public static final int STORE_DRAW_MONEY = 1; //经销商提现
	
	/**
	 * -------------------------------------------------------------
	 * 						交易小类：经销商 购买服务
	 * -------------------------------------------------------------
	 */
	
	public static final int STORE_PHONE_MESSAGE = 1;//购买短信
	public static final int STORE_ADVERTISERMENT= 2 ; //广告发布
	public static final int STORE_ADV_ERTISERMENT = 3;//广告发布栏位数
	public static final int STORE_APPERTISERMENT = 4;//信息推送
	public static final int STORE_APP_ERTISERMENT = 5;//信息推送栏位数
	public static final int STORE_BONUS_MESSAGE = 6 ; //优惠券数量
	/**
	 * -------------------------------------------------------------
	 * 						交易小类 退款
	 * -------------------------------------------------------------
	 */	
	public static final int MEMBER_REFUND = 1; //用户退款

	
	
	/**
	 * 获取admin交易类型状态map
	 * key为类型变量名字串
	 * value类型值
	 * @return
	 */
	public static Map<String,Object> getAdminSettlementTypeMap(){
		Map<String,Object> map = new HashMap<String, Object>(17);
		//交易大类
		map.put("BIG_BUY_SERVICE",AdminSettlementType.BIG_BUY_SERVICE) ;
		map.put("BIG_DRAW_MONEY",AdminSettlementType.BIG_DRAW_MONEY) ;
		map.put("BIG_STORE_BUY_SERVICE",AdminSettlementType.BIG_STORE_BUY_SERVICE) ;
		map.put("BIG_REFUND",AdminSettlementType.BIG_REFUND) ;
	
		
		//app用户 购买服务
		map.put("APP_STORE_GOODS", AdminSettlementType.APP_STORE_GOODS);
		map.put("APP_INSURANCE",AdminSettlementType.APP_ZA_INSURANCE);
		map.put("APP_REPAIR", AdminSettlementType.APP_REPAIR);
		map.put("APP_ZA_GOODS", AdminSettlementType.APP_ZA_GOODS);
		
		
		//提现
		map.put("STORE_DRAW_MONEY", AdminSettlementType.STORE_DRAW_MONEY);
		
		
		//经销商 购买服务
		map.put("STORE_PHONE_MESSAGE", AdminSettlementType.STORE_PHONE_MESSAGE);
		map.put("STORE_ADVERTISERMENT", AdminSettlementType.STORE_ADVERTISERMENT);
		map.put("STORE_ADV_ERTISERMENT", AdminSettlementType.STORE_ADV_ERTISERMENT);
		map.put("STORE_APPERTISERMENT", AdminSettlementType.STORE_APPERTISERMENT);
		map.put("STORE_APP_ERTISERMENT", AdminSettlementType.STORE_APP_ERTISERMENT);
		map.put("STORE_BONUS_MESSAGE", AdminSettlementType.STORE_BONUS_MESSAGE);
		
		
		//退款
		map.put("MEMBER_REFUND", AdminSettlementType.MEMBER_REFUND);
		
		return map;
	}
	
	public static Map getAdminSettlementBigTypeMap(){ 
		Map map = new  TreeMap();
		map.put(AdminSettlementType.BIG_BUY_SERVICE,getAdminSettlementTypeText(AdminSettlementType.BIG_BUY_SERVICE));
		map.put(AdminSettlementType.BIG_DRAW_MONEY,getAdminSettlementTypeText(AdminSettlementType.BIG_DRAW_MONEY)) ;
		map.put(AdminSettlementType.BIG_STORE_BUY_SERVICE,getAdminSettlementTypeText(AdminSettlementType.BIG_STORE_BUY_SERVICE)) ;
		map.put(AdminSettlementType.BIG_REFUND,getAdminSettlementTypeText(AdminSettlementType.BIG_REFUND)) ;
		return map;
	}
	/**
	 * 获取admin结算大类类型
	 * @param status
	 * @return
	 */
	public static String getAdminSettlementTypeText(int type){
		String text = "";
		
		switch (type) {
		case BIG_BUY_SERVICE:
			text = "购买服务";
			break;
		case BIG_DRAW_MONEY:
			text = "提现";
			break;
		case BIG_STORE_BUY_SERVICE:
			text = "经销商购买服务";
			break;
		case BIG_REFUND:
			text = "用户退款";
			break;
			
		default:
			text = "错误状态";
			break;
		}
		return text;
                  
	}
	
	
	/**
	 * 获取app用户购买类型
	 * @param status
	 * @return
	 */
	public static String getAppBuyTypeText(int type){
		String text = "";
		
		switch (type) {
		
		case APP_STORE_GOODS:
			text = "经销商-商城";
			break;
		case APP_ZA_INSURANCE:
			text = "中安-保险";
			break;
		case APP_REPAIR:
			text = "经销商-保养";
			break;
		case APP_ZA_GOODS:
			text = "中安-商城";
			break;
		case APP_STORE_INSURANCE:
			text = "经销商-保险";
			break;
		default:
			text = "错误状态";
			break;

		}
		return text;
	}	

	

	
	/**
	 * 获取提现状态text
	 * @param status
	 * @return
	 */
	public static String getDrawMoneyTypeText(int type){
		String text = "";
		
		switch (type) {
		case STORE_DRAW_MONEY:
			text = "经销商提现";
			break;
		default:
			text = "错误状态";
			break;
		}
                     
		return text;
	}
		

	/**
	 * 获取经销商购买服务状态text
	 * @param status
	 * @return
	 */
	public static String getStoreBuyServiceTypeText(int type){
		String text = "";
		
		switch (type) {

		case STORE_PHONE_MESSAGE:
			text = "短信服务";
			break;

		case STORE_ADVERTISERMENT:
			text = "广告发布";
			break;
			
		case STORE_ADV_ERTISERMENT:
			text = "广告发布栏位数";
			break;
			
		case STORE_APPERTISERMENT:
			text = "信息推送";
			break;
			
		case STORE_APP_ERTISERMENT:
			text = "信息推送栏位数";
			break;
		
		case STORE_BONUS_MESSAGE:
			text = "优惠券数量";
			break;
	
		default:
			text = "错误状态";
			break;
		}
                     
		return text;
	}
	
	/**
	 * 获取经销商购买服务状态text
	 * @param status
	 * @return
	 */
	public static String getMemberRefundTypeText(int type){
		String text = "";
		
		switch (type) {
		
		case MEMBER_REFUND:
			text = "用户退款";
			break;
	
		default:
			text = "错误状态";
			break;
		}
                     
		return text;
	}


}
