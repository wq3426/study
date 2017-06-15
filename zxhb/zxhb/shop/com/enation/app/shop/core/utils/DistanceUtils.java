package com.enation.app.shop.core.utils;

import java.io.InputStream;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DistanceUtils {
	//地球平均半径  
    private static final double EARTH_RADIUS = 6378137;  
    //把经纬度转为度（°）  
    private static double rad(double d){  
       return d * Math.PI / 180.0;  
    }  
      
    /**  
     * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米  
     * @param lng1  
     * @param lat1  
     * @param lng2  
     * @param lat2  
     * @return  
     */  
    public static double getDistance(double lng1, double lat1, double lng2, double lat2){  
       double radLat1 = rad(lat1);  
       double radLat2 = rad(lat2);  
       double a = radLat1 - radLat2;  
       double b = rad(lng1) - rad(lng2);  
       double s = 2 * Math.asin(  
            Math.sqrt(  
                Math.pow(Math.sin(a/2),2)   
                + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)  
            )  
        );  
       s = s * EARTH_RADIUS;  
       s = Math.round(s * 10000) / 10000;  
       return s;  
    }
    
    public static void main(String[] args) {
    	try {
    		StringBuilder json = new StringBuilder();
    		json.append("[{\"latitude\":39.921573,\"longitude\":116.517159,\"gps_time\":\"2016-07-29 16:37\"},");
    		json.append("{\"latitude\":39.921546,\"longitude\":116.517097,\"gps_time\":\"2016-07-29 16:39\"},");
    		json.append("{\"latitude\":39.921915,\"longitude\":116.517056,\"gps_time\":\"2016-07-29 16:40\"},");
    		json.append("{\"latitude\":39.922631,\"longitude\":116.521926,\"gps_time\":\"2016-07-29 16:41\"},");
    		json.append("{\"latitude\":39.922755,\"longitude\":116.523566,\"gps_time\":\"2016-07-29 16:42\"},");
    		json.append("{\"latitude\":39.922797,\"longitude\":116.524037,\"gps_time\":\"2016-07-29 16:43\"},");
    		json.append("{\"latitude\":39.922794,\"longitude\":116.524138,\"gps_time\":\"2016-07-29 16:44\"},");
    		json.append("{\"latitude\":39.922919,\"longitude\":116.522969,\"gps_time\":\"2016-07-29 16:45\"},");
    		json.append("{\"latitude\":39.922314,\"longitude\":116.514881,\"gps_time\":\"2016-07-29 16:46\"},");
    		json.append("{\"latitude\":39.921932,\"longitude\":116.505322,\"gps_time\":\"2016-07-29 16:47\"},");
    		json.append("{\"latitude\":39.921847,\"longitude\":116.503833,\"gps_time\":\"2016-07-29 16:48\"},");
    		json.append("{\"latitude\":39.921644,\"longitude\":116.49774,\"gps_time\":\"2016-07-29 16:49\"},");
    		json.append("{\"latitude\":39.92155,\"longitude\":116.497574,\"gps_time\":\"2016-07-29 16:50\"},");
    		json.append("{\"latitude\":39.922492,\"longitude\":116.496614,\"gps_time\":\"2016-07-29 16:51\"},");
    		json.append("{\"latitude\":39.926572,\"longitude\":116.496791,\"gps_time\":\"2016-07-29 16:52\"},");
    		json.append("{\"latitude\":39.926556,\"longitude\":116.496593,\"gps_time\":\"2016-07-29 16:53\"},");
    		json.append("{\"latitude\":39.926587,\"longitude\":116.496728,\"gps_time\":\"2016-07-29 16:54\"},");
    		json.append("{\"latitude\":39.929444,\"longitude\":116.49662,\"gps_time\":\"2016-07-29 16:55\"},");
    		json.append("{\"latitude\":39.93441,\"longitude\":116.496542,\"gps_time\":\"2016-07-29 16:56\"},");
    		json.append("{\"latitude\":39.942808,\"longitude\":116.49653,\"gps_time\":\"2016-07-29 16:57\"},");
    		json.append("{\"latitude\":39.949087,\"longitude\":116.496457,\"gps_time\":\"2016-07-29 16:58\"},");
    		json.append("{\"latitude\":39.955642,\"longitude\":116.496497,\"gps_time\":\"2016-07-29 16:59\"},");
    		json.append("{\"latitude\":39.963406,\"longitude\":116.494566,\"gps_time\":\"2016-07-29 17:00\"},");
    		json.append("{\"latitude\":39.966888,\"longitude\":116.491424,\"gps_time\":\"2016-07-29 17:01\"},");
    		json.append("{\"latitude\":39.969247,\"longitude\":116.48818,\"gps_time\":\"2016-07-29 17:02\"},");
    		json.append("{\"latitude\":39.972136,\"longitude\":116.483802,\"gps_time\":\"2016-07-29 17:03\"},");
    		json.append("{\"latitude\":39.975558,\"longitude\":116.478529,\"gps_time\":\"2016-07-29 17:04\"},");
    		json.append("{\"latitude\":39.978833,\"longitude\":116.473462,\"gps_time\":\"2016-07-29 17:05\"},");
    		json.append("{\"latitude\":39.980365,\"longitude\":116.471125,\"gps_time\":\"2016-07-29 17:06\"},");
    		json.append("{\"latitude\":39.983413,\"longitude\":116.466562,\"gps_time\":\"2016-07-29 17:07\"},");
    		json.append("{\"latitude\":39.988548,\"longitude\":116.458865,\"gps_time\":\"2016-07-29 17:08\"},");
    		json.append("{\"latitude\":39.992371,\"longitude\":116.452053,\"gps_time\":\"2016-07-29 17:09\"},");
    		json.append("{\"latitude\":39.994949,\"longitude\":116.445008,\"gps_time\":\"2016-07-29 17:10\"},");
    		json.append("{\"latitude\":39.995278,\"longitude\":116.44165,\"gps_time\":\"2016-07-29 17:11\"},");
    		json.append("{\"latitude\":39.995218,\"longitude\":116.432815,\"gps_time\":\"2016-07-29 17:12\"},");
    		json.append("{\"latitude\":39.995174,\"longitude\":116.428653,\"gps_time\":\"2016-07-29 17:13\"},");
    		json.append("{\"latitude\":39.995086,\"longitude\":116.424472,\"gps_time\":\"2016-07-29 17:14\"},");
    		json.append("{\"latitude\":39.99508,\"longitude\":116.422634,\"gps_time\":\"2016-07-29 17:15\"},");
    		json.append("{\"latitude\":39.994977,\"longitude\":116.418106,\"gps_time\":\"2016-07-29 17:16\"},");
    		json.append("{\"latitude\":39.994996,\"longitude\":116.413487,\"gps_time\":\"2016-07-29 17:17\"},");
    		json.append("{\"latitude\":39.995,\"longitude\":116.40937,\"gps_time\":\"2016-07-29 17:18\"},");
    		json.append("{\"latitude\":39.994907,\"longitude\":116.407026,\"gps_time\":\"2016-07-29 17:19\"},");
    		json.append("{\"latitude\":39.99481,\"longitude\":116.403936,\"gps_time\":\"2016-07-29 17:20\"},");
    		json.append("{\"latitude\":39.994896,\"longitude\":116.401863,\"gps_time\":\"2016-07-29 17:21\"},");
    		json.append("{\"latitude\":39.994205,\"longitude\":116.400051,\"gps_time\":\"2016-07-29 17:22\"},");
    		json.append("{\"latitude\":39.994638,\"longitude\":116.397382,\"gps_time\":\"2016-07-29 17:23\"},");
    		json.append("{\"latitude\":39.994489,\"longitude\":116.392077,\"gps_time\":\"2016-07-29 17:24\"},");
    		json.append("{\"latitude\":39.994296,\"longitude\":116.388852,\"gps_time\":\"2016-07-29 17:25\"},");
    		json.append("{\"latitude\":39.995498,\"longitude\":116.384876,\"gps_time\":\"2016-07-29 17:26\"},");
    		json.append("{\"latitude\":39.998236,\"longitude\":116.383864,\"gps_time\":\"2016-07-29 17:27\"},");
    		json.append("{\"latitude\":40.001696,\"longitude\":116.38148,\"gps_time\":\"2016-07-29 17:28\"},");
    		json.append("{\"latitude\":40.004253,\"longitude\":116.379333,\"gps_time\":\"2016-07-29 17:29\"},");
    		json.append("{\"latitude\":40.009297,\"longitude\":116.375435,\"gps_time\":\"2016-07-29 17:30\"},");
    		json.append("{\"latitude\":40.016388,\"longitude\":116.370119,\"gps_time\":\"2016-07-29 17:31\"},");
    		json.append("{\"latitude\":40.019878,\"longitude\":116.367437,\"gps_time\":\"2016-07-29 17:32\"},");
    		json.append("{\"latitude\":40.022487,\"longitude\":116.365356,\"gps_time\":\"2016-07-29 17:33\"},");
    		json.append("{\"latitude\":40.024904,\"longitude\":116.363498,\"gps_time\":\"2016-07-29 17:34\"},");
    		json.append("{\"latitude\":40.027065,\"longitude\":116.361798,\"gps_time\":\"2016-07-29 17:35\"},");
    		json.append("{\"latitude\":40.028305,\"longitude\":116.360843,\"gps_time\":\"2016-07-29 17:36\"},");
    		json.append("{\"latitude\":40.029664,\"longitude\":116.359741,\"gps_time\":\"2016-07-29 17:37\"},");
    		json.append("{\"latitude\":40.030837,\"longitude\":116.358793,\"gps_time\":\"2016-07-29 17:38\"},");
    		json.append("{\"latitude\":40.032973,\"longitude\":116.357125,\"gps_time\":\"2016-07-29 17:39\"},");
    		json.append("{\"latitude\":40.038054,\"longitude\":116.353189,\"gps_time\":\"2016-07-29 17:40\"},");
    		json.append("{\"latitude\":40.045371,\"longitude\":116.347536,\"gps_time\":\"2016-07-29 17:41\"},");
    		json.append("{\"latitude\":40.053837,\"longitude\":116.341116,\"gps_time\":\"2016-07-29 17:42\"},");
    		json.append("{\"latitude\":40.063616,\"longitude\":116.333594,\"gps_time\":\"2016-07-29 17:43\"},");
    		json.append("{\"latitude\":40.072997,\"longitude\":116.326399,\"gps_time\":\"2016-07-29 17:44\"},");
    		json.append("{\"latitude\":40.077362,\"longitude\":116.323288,\"gps_time\":\"2016-07-29 17:45\"},");
    		json.append("{\"latitude\":40.077557,\"longitude\":116.323247,\"gps_time\":\"2016-07-29 17:46\"},");
    		json.append("{\"latitude\":40.078833,\"longitude\":116.322558,\"gps_time\":\"2016-07-29 17:47\"},");
    		json.append("{\"latitude\":40.080782,\"longitude\":116.325517,\"gps_time\":\"2016-07-29 17:48\"},");
    		json.append("{\"latitude\":40.081242,\"longitude\":116.327304,\"gps_time\":\"2016-07-29 17:49\"},");
    		json.append("{\"latitude\":40.081774,\"longitude\":116.329301,\"gps_time\":\"2016-07-29 17:50\"},");
    		json.append("{\"latitude\":40.081776,\"longitude\":116.3293,\"gps_time\":\"2016-07-29 17:51\"},");
    		json.append("{\"latitude\":40.080776,\"longitude\":116.325072,\"gps_time\":\"2016-07-29 17:52\"},");
    		json.append("{\"latitude\":40.07957,\"longitude\":116.320905,\"gps_time\":\"2016-07-29 17:53\"},");
    		json.append("{\"latitude\":40.081409,\"longitude\":116.318954,\"gps_time\":\"2016-07-29 17:54\"},");
    		json.append("{\"latitude\":40.078115,\"longitude\":116.32189,\"gps_time\":\"2016-07-29 17:55\"},");
    		json.append("{\"latitude\":40.073503,\"longitude\":116.325465,\"gps_time\":\"2016-07-29 17:56\"},");
    		json.append("{\"latitude\":40.074109,\"longitude\":116.323336,\"gps_time\":\"2016-07-29 17:57\"},");
    		json.append("{\"latitude\":40.073509,\"longitude\":116.320109,\"gps_time\":\"2016-07-29 17:58\"},");
    		json.append("{\"latitude\":40.0735,\"longitude\":116.320315,\"gps_time\":\"2016-07-29 17:59\"}]");
    		JSONArray array = JSONArray.fromObject(json.toString());
    		int centerIndex = array.size() / 2;
    		JSONObject orignObject = array.getJSONObject(0);
    		double orign_lng = orignObject.getDouble("longitude");
    		double orign_lat = orignObject.getDouble("latitude");
    		double max_distance = 0.0;
    		String paths = "";
    		for(int i=1; i<array.size(); i++){
    			JSONObject obj = array.getJSONObject(i);
    			double lng = obj.getDouble("longitude");
    			double lat = obj.getDouble("latitude");
    			double distance = getDistance(orign_lng, orign_lat, lng, lat);
    			if(max_distance < distance){
    				max_distance = distance;
    			}
    			if(i == array.size() - 1){
    				paths += obj.getString("longitude") +","+ obj.getString("latitude");
    			}else{
    				paths += obj.getString("longitude") +","+ obj.getString("latitude")+";";
    			}
    		}
            String ak = "lAcvDh3Ipl1RBSbViH2HxQcog2TYeYG0";
    		String mcode = "B5:1C:08:EF:BB:E1:1D:30:1E:E5:0C:2D:DF:EF:2E:B3:68:9C:01:5C;com.obdpay.obdpay";
    		String center = array.getJSONObject(centerIndex).getString("longitude")+","+array.getJSONObject(centerIndex).getString("latitude");
    		long mile = Math.round(max_distance / 1000);
    		int zoom = 3;
    		if(mile <= 1){
    			zoom = 18;
    		}else if(mile <=2){
    			zoom = 17;
    		}else if(mile <= 5){
    			zoom = 16;
    		}else if(mile <= 10){
    			zoom = 15;
    		}else if(mile <= 20){
    			zoom = 14;
    		}else if(mile <= 25){
    			zoom = 13;
    		}else if(mile <= 50){
    			zoom = 12;
    		}else if(mile <= 100){
    			zoom = 11;
    		}else if(mile <= 200){
    			zoom = 10;
    		}else if(mile <= 500){
    			zoom = 9;
    		}else if(mile <= 1000){
    			zoom = 8;
    		}else{
    			zoom = 7;
    		}
    		zoom = zoom - 1;
    		String requestURL = "http://api.map.baidu.com/staticimage/v2?ak="+ak+"&mcode="+mcode+"&width=800&height=400&center="+center+"&zoom="+zoom+"&paths="+paths+"&pathStyles=0x000fff,5,1";
    		HttpClient client = new HttpClient();  
            GetMethod method = new GetMethod(requestURL);
            method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8"); 
            // 设置请求包头文件  
            method.setRequestHeader("Content-Type", "text/xml;charset=utf-8");  
            client.executeMethod(method);  
            InputStream in = method.getResponseBodyAsStream();
            String fileName = DateUtil.toString(new Date(), "yyyyMMddHHmmss") + StringUtil.getRandStr(4) + ".jpg";
            String static_server_path = SystemSetting.getStatic_server_path();
    		String filePath = SystemSetting.getFile_path() + "/files/car_hodometer_image/" + fileName;
    		String path = EopSetting.FILE_STORE_PREFIX+ "/attachment/car_hodometer_image/" + fileName;
            FileUtil.createFile(in, filePath);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
