package com.enation.test.fastdfs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.enation.app.dataanalysis.request.vo.FuelConsumptionRankingReqVO;
import com.enation.eop.processor.CarVinContent;
import com.enation.eop.processor.MobileMessageHttpSend;
import com.enation.framework.util.StringUtil;

import cn.jiguang.commom.utils.StringUtils; 

/**
 * @Description 模拟浏览器请求文件同步至FastDFS分布式文件系统
 *
 * @createTime 2016年8月27日 下午3:07:08
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public class HttpClientRequestTest {
	 
		/**数据分析模块，排名测试
		 * @description 
		 * @date 2016年9月22日 下午4:48:57
		 * @param args
		 */
	    public static void main(String[] args) {  
	           
        	String url = "http://localhost:8080/mall/data/analysis/dataAnalysisAction!analyse.do"; 
            PostMethod postMethod = new PostMethod(url);  
            postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");  
            
            //填入各个表单域的值  
            NameValuePair[] data = {  
            	new NameValuePair("businessCode", "consumption"),  
            	//new NameValuePair("param","{\"brand\":\"马自达\", \"cardriveregion\":\"北京\",\"gender\":\"male\"}")  
            	new NameValuePair("param","{\"brand\":\"\",\"cardriveregion\":\"\",\"gender\":\"male\",\"dataItemType\":\"avg_consumption_month\"}")  
            };  
            
            try {
            	postMethod.setRequestBody(data);  
	            HttpClient httpclient = new HttpClient();
				int statusCode =  httpclient.executeMethod(postMethod); 
				
				if(statusCode == HttpStatus.SC_OK){  
				    String jsonString = postMethod.getResponseBodyAsString();
				    System.out.println(jsonString);
				}
				
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    @Test
	    public void testJson() {
	    	String req = "{\"brand\":\"\", \"cardriveregion\":\"\",\"gender\":\"male\", \"dataItemType\":\"tital_consumption_today\"}";    	
	    	
	    	FuelConsumptionRankingReqVO fuelConsumptionRankingReqVO;
			try {
				fuelConsumptionRankingReqVO = JSON.parseObject(req, FuelConsumptionRankingReqVO.class);
		    	System.out.println(fuelConsumptionRankingReqVO.getBrand());
		    	System.out.println(fuelConsumptionRankingReqVO.getCardriveregion());
		    	System.out.println(fuelConsumptionRankingReqVO.getDataItemType());
		    	System.out.println(fuelConsumptionRankingReqVO.getGender());
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	
	    }
	    
	    @Test
	    public void testDate() {
	    	Date now = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = dateFormat.format(now);
			System.out.println(date);
	    }
	    
	    @Test
	    public void testqueryCarInfoVin() {
	    	String vin = "LVSHCFAE4BF760829";
	    	
	    	//调用第三方数据解析接口
			String reqContent = "key=" + CarVinContent.key + "&vin=" + vin;
			String respContent = MobileMessageHttpSend.postSend(CarVinContent.reqUrl,reqContent);	
			System.out.println(respContent);
	    }
	    
	    @Test
	    public void testGetCarDetailInfo() {
	    	
	    	String url = "http://localhost:8080/mall/api/mobile/carinfo!getCarDetailInfo.do"; 
            PostMethod postMethod = new PostMethod(url);  
            postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");  
            
            //填入各个表单域的值  
            NameValuePair[] data = {  
            	//new NameValuePair("vin", "LVSHCFAE4BF760829")
            	new NameValuePair("vin", "LFMBE22D890173068")
            };  
            
            try {
            	postMethod.setRequestBody(data);  
	            HttpClient httpclient = new HttpClient();
				int statusCode =  httpclient.executeMethod(postMethod); 
				
				if(statusCode == HttpStatus.SC_OK){  
				    String jsonString = postMethod.getResponseBodyAsString();
				    System.out.println(jsonString);
				}
				
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    @Test
	    public void testRankingFlag() {
	    	
	    	String url = "http://localhost:8080/mall/api/shop/member!changeRankingFlag.do"; 
            PostMethod postMethod = new PostMethod(url);  
            postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");  
            
            //填入各个表单域的值  
            NameValuePair[] data = {  
            	new NameValuePair("userName", "admin@1577..com"),
            	new NameValuePair("rankingFlag", "0")
            };  
            
            try {
            	postMethod.setRequestBody(data);  
	            HttpClient httpclient = new HttpClient();
				int statusCode =  httpclient.executeMethod(postMethod); 
				
				if(statusCode == HttpStatus.SC_OK){  
				    String jsonString = postMethod.getResponseBodyAsString();
				    System.out.println(jsonString);
				}
				
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    }
	    
	    /**
	     * @description 测试MD5密码加密
	     * @date 2016年10月10日 下午4:17:54
	     */
	    @Test
	    public void testMD5() {
	    	String pwd ="123123";
	    	String md5 = StringUtil.md5(pwd);
	    	System.out.println(md5);
	    }
	    
	    @Test
	    public void testDeleteBonus() {
	    	String url = "http://localhost:8080/mall/api/b2b2c/promotion!deleteBonus.do"; 
            PostMethod postMethod = new PostMethod(url);  
            postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");  
            
            //填入各个表单域的值  
            NameValuePair[] data = {  
            	new NameValuePair("type_id", "18"),
            	new NameValuePair("bonus_id", "7")
            };  
            
            try {
            	postMethod.setRequestBody(data);  
	            HttpClient httpclient = new HttpClient();
				int statusCode =  httpclient.executeMethod(postMethod); 
				
				if(statusCode == HttpStatus.SC_OK){  
				    String jsonString = postMethod.getResponseBodyAsString();
				    System.out.println(jsonString);
				}
				
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
}
