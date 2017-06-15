package com.enation.app.shop.component.payment.plugin.alipay.wap;

import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.payment.plugin.alipay.JavashopAlipayUtil;
import com.enation.app.shop.component.payment.plugin.alipay.sdk33.config.AlipayConfig;
import com.enation.app.shop.component.payment.plugin.alipay.sdk33.util.AlipaySubmit;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StringUtil;


@Component
public class AlipayWapPlugin extends AbstractPaymentPlugin  implements IPaymentEvent{
	
	
	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
		
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			Order storeOrder = (Order)order; 
			Map<String,String> params = paymentManager.getConfigParams(this.getId());//获取数据库config字段
			//卖家支付宝帐户
			String seller_email =params.get("seller_email");
			String partner =params.get("partner");
			String key =  params.get("key");
			//key从数据库中查出来
//			String key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK6i6+3azUtlbnk+r7B2wzx3+I5TPW8XfI6MDZK2Bjq+6xOvdV6DAUy+RXjXX4MfaSWbHPwOZ+owYkiG0rLY97AI4vyznzBW1shTatbRQUrLluAUR2O/Onh1MWudZxvaL73jMHj3le+IUc1IRvXJlpg1n8eeSniPxYZAwfPFSV1FAgMBAAECgYAr3Gd9GdMt+I/Ci+f8Xhey/D7FIErxt+hktPBDxMvTvVe1XgAQeL6+05sHOrCWcjEqbMbe149p1jUZGiOZH701YtCi+olxe6zrx/91PWea/0PsViV8c1TrXaH09DCqhU7YQuhKIwf8sFSZjv1GDwkGCWW3UG5g6T+XHPScOXO8gQJBAOJkIm/KFN5OFC/1UEdzBdkk6kncofdGZSbNnoPoBz3yyB5OlPpZjWJR2kdaLFDu9og53V08432uJvZIUcq9O+kCQQDFefjixqyHzPVCc8OtwDQLCRPuWMy3secYrkpMPFkDHVUMul6+LsNxMYCaay8uozKnjdf+JkJlwgM1o3uP6uj9AkEA4JaEDzCDaVI99qG/ZVI3cAaOIn4tc3izaj47zhHF9W+/lUlRw9ZJS0t2ZqiPJC8cBWKaNx/rcsfswRBXi/AU2QJAQGhCy7wew4AOsijNtprMnpjMXl2qD3O/uuYFp427Pm/PsIIMqpzX89BAcJzq25wwxkNWIjdaSpQXtePnRg5JBQJBAKnkWokWUwc0wDr7ax82eERCInBEKFM2Xf3++32Ot2xKO4d4bVM4Y7s9Cd1ZNTKnOsHAwkvHFsie8+JmjXJC/Uk=";
			AlipayConfig.key=key;
			AlipayConfig.partner=partner;
			AlipayConfig.seller_email=seller_email;
			AlipayConfig.sign_type="RSA";//移动端使用RSA加密
			String out_trade_no = order.getSn(); // 商户网站订单
			String content_encoding = params.get("content_encoding"); //编码格式
			
			String payment_type = "1";
			//必填，不能修改
			//服务器异步通知页面路径
			String notify_url =this.getCallBackUrl(payCfg, order);
			//需http://格式的完整路径，不能加?id=123这类自定义参数

			//页面跳转同步通知页面路径
//			String return_url = this.getReturnWapUrl(payCfg, order);
			//需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/
			
//			String show_url= this.getWapShowUrl(order);
			
			//商户订单号
			out_trade_no = new String( out_trade_no.getBytes("ISO-8859-1"),"UTF-8")  ;
			//商户网站订单系统中唯一订单号，必填

//			String sitename = EopSite.getInstance().getSitename();
			//订单名称
			String subject = "中安信博科技（北京）有限公司";
			
			if(!StringUtil.isEmpty(content_encoding)){
				 subject = new String(subject.getBytes("ISO-8859-1"),content_encoding);
			}
			//必填

			String body =  ("订单："+out_trade_no);
			if(!StringUtil.isEmpty(content_encoding)){
				body=new String( body.getBytes("ISO-8859-1"),content_encoding);
			}
			
			//计算支付价格
			Double gain= storeOrder.getGain();//用户可用安全奖励
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP); 
			//使用优惠券
			Double bonus_price = StringUtil.toDouble(request.getAttribute("bonus_price").toString(),0d);
			Double need_pay_money = CurrencyUtil.sub(storeOrder.getNeed_pay_money(), bonus_price);
			//使用奖励
			need_pay_money = CurrencyUtil.sub(need_pay_money, gain);
			//使用保养币(保养订单)
			double repair_coin = storeOrder.getRepair_coin();
			need_pay_money = CurrencyUtil.sub(need_pay_money, repair_coin);
			
			String total_fee = decimalFormat.format(need_pay_money);
//			String price = new String(String.valueOf(0.10d-storeOrder.getGain()).getBytes("ISO-8859-1"),"UTF-8");
			
			
			
			//把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
	        sParaTemp.put("partner", AlipayConfig.partner);//合作身份者ID
	        sParaTemp.put("_input_charset", AlipayConfig.input_charset);//字符编码格式
			sParaTemp.put("payment_type", payment_type);//支付类型。默认值为：1（商品 购买）。 
			sParaTemp.put("notify_url", notify_url); //设置支付宝回调链接
			sParaTemp.put("out_trade_no", out_trade_no);//商户网站 唯一订单 号 
			sParaTemp.put("subject", subject);//商品名称 
			sParaTemp.put("total_fee", total_fee);//总金额
			sParaTemp.put("seller_id", AlipayConfig.partner);//卖家支付宝账号（邮箱或手机 号码格式）或其对应的支付宝 唯一用户号（以 2088 开头的 纯 16 位数字）
			sParaTemp.put("seller_email", AlipayConfig.seller_email);
			sParaTemp.put("body", body);//商品详情 
			sParaTemp.put("service", "mobile.securitypay.pay"); //接口名称（注意）这里使用的是移动端接口
//			sParaTemp.put("return_url", return_url);安卓支付不需要
//			sParaTemp.put("show_url", show_url);   安卓支付不需要
//			sParaTemp.put("it_b_pay", "");
//			sParaTemp.put("extern_token", "");
			
//			String sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"get","确认");
			String sHtmlText = AlipaySubmit.buildParamsJson(sParaTemp);
			return sHtmlText;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String onCallBack(String ordertype) {
		try {
			
			Map<String,String> paramscfg = paymentManager.getConfigParams(this.getId());
			//卖家支付宝帐户
			String partner =paramscfg.get("partner");
			String key =  paramscfg.get("key");
			AlipayConfig.key=key;
			AlipayConfig.partner=partner;
			AlipayConfig.ali_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
			String param_encoding = paramscfg.get("param_encoding");
			HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
			/*Enumeration<String> parameterNames = request.getParameterNames();
			while(parameterNames.hasMoreElements()){
				String name = parameterNames.nextElement();
				System.out.println("name:" + name + "-- value:"+request.getParameter(name));
			}*/
			//商户订单号
			String out_trade_no = request.getParameter("out_trade_no");
			//支付宝交易号
			String trade_no = request.getParameter("trade_no");
			//交易状态
			String trade_status = request.getParameter("trade_status");
			
			if(JavashopAlipayUtil.verify(param_encoding)){//验证成功
				//////////////////////////////////////////////////////////////////////////////////////////
				//请在这里加上商户的业务逻辑程序代码
				this.paySuccess(out_trade_no,trade_no, ordertype);
				//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				
				if(trade_status.equals("TRADE_FINISHED")||trade_status.equals("TRADE_SUCCESS")){
					this.logger.info("异步校验订单["+out_trade_no+"]成功");
					return ("success");	//请不要修改或删除
					
				}else {
					this.logger.info("异步校验订单["+out_trade_no+"]成功");
					return ("success");	//请不要修改或删除
				}
			}else{//验证失败
				this.logger.info("异步校验订单["+out_trade_no+"]失败");
				return ("fail");
			}
		 
		} catch (Exception e) {
			e.printStackTrace();
			return ("fail");
		}
	}

	@Override
	public String onReturn(String ordertype) {
	//	return "SP20140516024914";
			try {
				Map<String,String> cfgparams = paymentManager.getConfigParams(this.getId());
				String key =cfgparams.get("key");
				String partner =cfgparams.get("partner");
				AlipayConfig.key=key;
				AlipayConfig.partner=partner;
				String param_encoding = cfgparams.get("param_encoding");  

				//获取支付宝GET过来反馈信息
				HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();

				//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
				//商户订单号
				String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

				//支付宝交易号
				String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

				
				//计算得出通知验证结果
				boolean verify_result = JavashopAlipayUtil.verify(param_encoding);
				if(verify_result){
					this.paySuccess(out_trade_no,trade_no,ordertype);
					this.logger.info("同步校验订单["+out_trade_no+"]成功");
					return out_trade_no;	
				}else{
					this.logger.info("同步校验订单失败");
					throw new RuntimeException("验证失败");  
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				this.logger.info("异步校验订单失败");
				throw new RuntimeException("验证失败",e);  
			}
	}

	@Override
	public String getId() {
		return "alipayWapPlugin";
	}

	@Override
	public String getName() {
		return "支付宝Wap支付接口";
	}
	
	
	/**解析支付宝传回的参数列表
	 * @return
	 */
	private Map<String, String> parseXML(String notify_data) {
		Map<String,String> params =new HashMap<String,String>();
		try{
			Document doc = DocumentHelper.parseText(notify_data);
			Element root = doc.getRootElement();
			Iterator it = root.elementIterator();
			Element element;
			while (it.hasNext()) {
				element = (Element) it.next();
				params.put(element.getName(),element.getText());
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return params;
	}

}
