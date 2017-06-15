package com.enation.app.shop.component.payment.plugin.wpay;

public class PayConfigUtil {

	//以下相关参数需要根据自己实际情况进行配置
	public static String APP_ID = "wx3b4dc672e9771ef9";// appid

	public static String APP_SECRET = "ff41b4383c6622092ef32ea725c8260c";// appsecret 
	public static String MCH_ID = "1429076602";// 你的商业号
	public static String API_KEY = "3BA104F9C1FB6B60E829BA7FD7360F03";// API key 
	
	public static String CREATE_IP = "123.57.57.145";// key 
	public static String UFDODER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";//统一下单接口 
	public static String NOTIFY_URL = "http://www.trans-it.cn:6066/zxhb/api/b2b2c/storeApi!payReNotifyByWechat.do";//回调地址
}
