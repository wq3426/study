package com.enation.eop.processor;

public abstract class MobileContent {
	
		
		public final static String strReg = "101100-WEB-HUAX-175580";   //验证码注册号（由华兴软通提供）
		public final static String strPwd = "ZUAAKFFT";    				//验证码密码（由华兴软通提供）
		public final static String strSourceAdd = "";                   //子通道号，可为空（预留参数一般为空）
	    
		
		public final static String strRegExtend = "101100-WEB-HUAX-124501";   //推广消息注册号（由华兴软通提供）
		public final static String strPwdExtend = "KUNAKYMM";    				//推广消息密码（由华兴软通提供）
		
	    //短信平台服务器url
	    public final static String strRegUrl = "http://www.stongnet.com/sdkhttp/reg.aspx";
	    public final static String strBalanceUrl = "http://www.stongnet.com/sdkhttp/getbalance.aspx";
	    public final static String strSmsUrl = "http://www.stongnet.com/sdkhttp/sendsms.aspx";
	    public final static String strSchSmsUrl = "http://www.stongnet.com/sdkhttp/sendschsms.aspx";
	    public final static String strStatusUrl = "http://www.stongnet.com/sdkhttp/getmtreport.aspx";
	    public final static String strUpPwdUrl = "http://www.stongnet.com/sdkhttp/uptpwd.aspx";
	    
}
