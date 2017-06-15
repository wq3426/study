package com.enation.app.b2b2c.core.action.api;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.enation.app.b2b2c.core.model.zxhb.OrderDetail;
import com.enation.app.b2b2c.core.service.order.impl.ZxhbOrderManager;
import com.enation.app.shop.component.payment.plugin.wpay.HttpUtil;
import com.enation.app.shop.component.payment.plugin.wpay.PayCommonUtil;
import com.enation.app.shop.component.payment.plugin.wpay.PayConfigUtil;
import com.enation.app.shop.component.payment.plugin.wpay.XMLUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 
 * @author Liuzy in Lasa 
 *
 */
public class WechatCodeServlet extends HttpServlet {

	private static Logger logger = Logger.getLogger(WechatCodeServlet.class);
	
	@Autowired
	private ZxhbOrderManager zxhbOrderManager;
	
	public static int defaultWidthAndHeight=200;
	
	
//	protected void doGet(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		// TODO Auto-generated method stub
//		String nonce_str = PayCommonUtil.getNonce_str();
//		long time_stamp = System.currentTimeMillis() / 1000;
//		String product_id = request.getParameter("order_sn");
//		System.out.println("product_id    "+product_id);
//		String key = PayConfigUtil.API_KEY; // key
//		
//		SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
//		packageParams.put("appid", PayConfigUtil.APP_ID);
//		packageParams.put("mch_id", PayConfigUtil.MCH_ID);
//		packageParams.put("time_stamp", String.valueOf(time_stamp));
//		packageParams.put("nonce_str", nonce_str);
//		packageParams.put("product_id", product_id);
//		String sign = PayCommonUtil.createSign("UTF-8", packageParams, key);//MD5哈希
//	    packageParams.put("sign", sign); 
//	    
//	    //生成参数
//	    String str = ToUrlParams(packageParams);
//	    String payurl = "weixin://wxpay/bizpayurl?" + str;
//	    logger.info("payurl:"+payurl);
//	    
//        
//        //生成二维码
//	    Map<EncodeHintType, Object>  hints=new HashMap<EncodeHintType, Object>();
//        // 指定纠错等级  
//        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);  
//        // 指定编码格式  
//        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");  
//        hints.put(EncodeHintType.MARGIN, 1);
//        try {
//			BitMatrix bitMatrix = new MultiFormatWriter().encode(payurl,BarcodeFormat.QR_CODE, defaultWidthAndHeight, defaultWidthAndHeight, hints);
//			OutputStream out = response.getOutputStream();
//			MatrixToImageWriter.writeToStream(bitMatrix, "png", out);//输出二维码
//            out.flush();
//            out.close();
//            
//        } catch (WriterException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	    
		// 账号信息  
        String key = PayConfigUtil.API_KEY; // key  
  
        String currTime = PayCommonUtil.getCurrTime();  
        String strTime = currTime.substring(8, currTime.length());  
        String strRandom = PayCommonUtil.buildRandom(4) + "";  
        String nonce_str = strTime + strRandom;  
        
        String order_sn = request.getParameter("order_sn");
        
        OrderDetail order_detail = zxhbOrderManager.getOrderDetailBySn(order_sn);
        System.out.println("price   "+order_detail.getTotal_price());
//        String order_price = "1"; // 价格   注意：价格的单位是分  
        String order_price = ""+order_detail.getTotal_price()*100;
        order_price = order_price.substring(0, order_price.indexOf("."));
        System.out.println("order_price   "+order_price);
        String body = "中信订单";   // 商品名称  
        String out_trade_no = order_sn; // 订单号  
          
        // 获取发起电脑 ip  
        String spbill_create_ip = PayConfigUtil.CREATE_IP;  
        // 回调接口   
        String notify_url = PayConfigUtil.NOTIFY_URL;  
        String trade_type = "NATIVE";  
          
        SortedMap<Object,Object> packageParams = new TreeMap<Object,Object>();  
        packageParams.put("appid", PayConfigUtil.APP_ID);  
        packageParams.put("mch_id", PayConfigUtil.MCH_ID);  
        packageParams.put("nonce_str", nonce_str);  
        packageParams.put("body", body);  
        packageParams.put("out_trade_no", out_trade_no);  
        packageParams.put("total_fee", order_price);  
        packageParams.put("spbill_create_ip", spbill_create_ip);  
        packageParams.put("notify_url", notify_url);  
        packageParams.put("trade_type", trade_type);  
  
        String sign = PayCommonUtil.createSign("UTF-8", packageParams,key);  
        packageParams.put("sign", sign);  
          
        String requestXML = PayCommonUtil.getRequestXml(packageParams);  
        String resXml = HttpUtil.postData(PayConfigUtil.UFDODER_URL, requestXML);  
          
        Map map;
		try {
			map = XMLUtil.doXMLParse(resXml);
			String urlCode = (String) map.get("code_url");  
			
			System.out.println("urlCode    "+urlCode);
			
	        //生成二维码
		    Map<EncodeHintType, Object>  hints=new HashMap<EncodeHintType, Object>();
	        // 指定纠错等级  
	        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);  
	        // 指定编码格式  
	        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");  
	        hints.put(EncodeHintType.MARGIN, 1);
	        try {
				BitMatrix bitMatrix = new MultiFormatWriter().encode(urlCode,BarcodeFormat.QR_CODE, defaultWidthAndHeight, defaultWidthAndHeight, hints);
				OutputStream out = response.getOutputStream();
				MatrixToImageWriter.writeToStream(bitMatrix, "png", out);//输出二维码
	            out.flush();
	            out.close();
	            
	        } catch (WriterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        //String return_code = (String) map.get("return_code");  
        //String prepay_id = (String) map.get("prepay_id");  
	}
	
	public String ToUrlParams(SortedMap<Object, Object> packageParams){
		//实际可以不排序
		StringBuffer sb = new StringBuffer();  
        Set es = packageParams.entrySet();  
        Iterator it = es.iterator();  
        while (it.hasNext()) {  
            Map.Entry entry = (Map.Entry) it.next();  
            String k = (String) entry.getKey();  
            String v = (String) entry.getValue();  
            if (null != v && !"".equals(v)) {  
                sb.append(k + "=" + v + "&");  
            }  
        }
        
        sb.deleteCharAt(sb.length()-1);//删掉最后一个&
        return sb.toString();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

}
