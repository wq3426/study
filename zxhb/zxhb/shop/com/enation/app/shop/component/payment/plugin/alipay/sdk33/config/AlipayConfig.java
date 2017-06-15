package com.enation.app.shop.component.payment.plugin.alipay.sdk33.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
	
 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {
	
	//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String partner = "2088021811562448";
	// 商户的私钥
	public static String key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAK6i6+3azUtlbnk+r7B2wzx3+I5TPW8XfI6MDZK2Bjq+6xOvdV6DAUy+RXjXX4MfaSWbHPwOZ+owYkiG0rLY97AI4vyznzBW1shTatbRQUrLluAUR2O/Onh1MWudZxvaL73jMHj3le+IUc1IRvXJlpg1n8eeSniPxYZAwfPFSV1FAgMBAAECgYAr3Gd9GdMt+I/Ci+f8Xhey/D7FIErxt+hktPBDxMvTvVe1XgAQeL6+05sHOrCWcjEqbMbe149p1jUZGiOZH701YtCi+olxe6zrx/91PWea/0PsViV8c1TrXaH09DCqhU7YQuhKIwf8sFSZjv1GDwkGCWW3UG5g6T+XHPScOXO8gQJBAOJkIm/KFN5OFC/1UEdzBdkk6kncofdGZSbNnoPoBz3yyB5OlPpZjWJR2kdaLFDu9og53V08432uJvZIUcq9O+kCQQDFefjixqyHzPVCc8OtwDQLCRPuWMy3secYrkpMPFkDHVUMul6+LsNxMYCaay8uozKnjdf+JkJlwgM1o3uP6uj9AkEA4JaEDzCDaVI99qG/ZVI3cAaOIn4tc3izaj47zhHF9W+/lUlRw9ZJS0t2ZqiPJC8cBWKaNx/rcsfswRBXi/AU2QJAQGhCy7wew4AOsijNtprMnpjMXl2qD3O/uuYFp427Pm/PsIIMqpzX89BAcJzq25wwxkNWIjdaSpQXtePnRg5JBQJBAKnkWokWUwc0wDr7ax82eERCInBEKFM2Xf3++32Ot2xKO4d4bVM4Y7s9Cd1ZNTKnOsHAwkvHFsie8+JmjXJC/Uk=";
			
	public static  String seller_email="";
	
	// 支付宝的公钥，无需修改该值
	public static String ali_public_key  = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "D:\\";

	// 字符编码格式 目前支持 gbk 或 utf-8
	public static String input_charset = "utf-8";
	
	// 签名方式 不需修改
	public static String sign_type = "RSA";

}
