package com.enation.app.shop.core.action.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;

@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("*payment-callback")
public class PaymentCallbackApiAction extends WWAction {

	public  String execute() {
		try{
			HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
			String url = RequestUtil.getRequestUrl(httpRequest);
			String pluginid = null;
			String ordertype =null;
			String[] params =this.getPluginid(url);
			
			ordertype= params[0];
			pluginid= params[1];
			
			String error = "参数不正确";
			
			if(params==null){
				this.json=error;
				return this.JSON_MESSAGE;
			}
			
			
			if (null == pluginid) {
				this.json=error;
				return this.JSON_MESSAGE;
			}  
			
			if (null == ordertype) {
				this.json=error;
				return this.JSON_MESSAGE;
			}  
			
			 
			IPaymentEvent paymentPlugin = SpringContextHolder.getBean(pluginid);
			this.json= paymentPlugin.onCallBack(ordertype);
			
			this.logger.debug("支付回调结果"+json);
		}catch(Exception e){
			this.logger.error("支付回调发生错误",e);
			this.json = "error";
		}
		return this.JSON_MESSAGE;
		 
	}
	
	private String[] getPluginid(String url) {
		String pluginid = null;
		String ordertype= null;
		String[] params = new String[2];
		String pattern = ".*/(\\w+)_(\\w+)_(payment-callback).do(.*)";
		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(url);
		if (m.find()) {
			ordertype = m.replaceAll("$1");
			pluginid = m.replaceAll("$2");
			params[0]=ordertype;
			params[1]=pluginid;
			return params;
		} else {
			return null;
		}
	}
	
	  public static void main(String[] args) {
		  String url ="/credit_alipay_payment-callback.do";
			String pattern = ".*/(\\w+)_(\\w+)_(payment-callback).do(.*)";
			Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
			Matcher m = p.matcher(url);
			if (m.find()) {
				String 	ordertype = m.replaceAll("$1");
				String 	pluginid = m.replaceAll("$2");
				//System.out.println(ordertype);
				//System.out.println(pluginid);
			} 
	}
	

}
