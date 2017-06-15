package com.enation.app.b2b2c.communication.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.enation.app.shop.core.service.impl.CarHodometerManager;
import com.enation.app.shop.core.service.impl.CarInfoManager;
import com.enation.eop.sdk.utils.GetBean;
import com.enation.framework.test.SpringTestSupport;
import com.enation.framework.util.FileUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MessageParse extends SpringTestSupport{

	private byte SSEP = 0x1c;
	public static Map<String, Map<String, Object>> hotometerMap = new HashMap<String, Map<String,Object>>();
	public static List list;
	private MessageHandler messageHandler = MessageHandler.getInstance();
	private CarHodometerManager carHodometerManager;
	private static CarInfoManager carInfoManager;
	private static ApplicationContext context;
	private static String[] args;
	private static String filePath = "/home/obdMessage/obdlog_"+new SimpleDateFormat("yyyyMMdd").format(new Date())+".txt";

	/**
	 * @description SERVER到OBD的ACK响应包
	 * @date 2016年9月28日 下午3:43:50
	 * @param fid
	 * @param state//报文接收成功state:0x30,接收失败state:0x31
	 * @return
	 * @return void
	 */
	public Byte[] makeServerToObdAck(Byte fid, Byte state) {
		Byte[] fidAck = { (byte) (fid + 0x40), state, SSEP };
		return fidAck;
	}

	/**
	 * @description OBD到server的ACK响应包
	 * @date 2016年9月28日 下午5:02:23
	 * @param fid
	 * @param state
	 * @return
	 * @return Byte[]
	 */
	public Byte[] makeObdToServerAck(Byte msgId, Byte state) {
		Byte[] msgIdAck = { (byte) (msgId + 0x40), state, SSEP };
		return msgIdAck;
	}

	public Map parsePacket(byte[] str) {
		Map<String, String> map = new HashMap<String, String>();
		String deviceId = this.getDeviceId(str);
		map.put("deviceId", deviceId);
		Byte fid = this.getFId(str);
		map.put("fid", fid.toString());
		//String dateTime = this.getTime(str);
		//map.put("time", dateTime);
		Byte msgId = this.getMsgId(str);
		map.put("msgId", msgId.toString());
		return map;

	}

	/**
	 * @description 获取读取的byte，截取deviceId
	 * @date 2016年9月27日 下午6:28:36
	 * @param str
	 * @return
	 * @return String
	 */
	public String getDeviceId(byte[] str) {
		StringBuilder sb = new StringBuilder();
		if (str.length >= 25) {
			for (int i = 0; i < 16; i++) {
				sb.append((char) str[i]);
			}
		} else {
			sb.append("数据为空");
		}
		return sb.toString();
	}

	/**
	 * @description 获取读取的byte，截取FID
	 * @date 2016年9月28日 下午3:50:32
	 * @param str
	 * @return
	 * @return byte
	 */
	public byte getFId(byte[] str) {
		byte fId = 0;
		if (str.length >= 25) {
			fId = str[16];
		}
		return fId;
	}

	/**
	 * @description 解析时间
	 * @date 2016年9月27日 下午7:28:23
	 * @param str
	 * @return
	 * @return BigInteger
	 */
	public static long getTime(byte[] str) {
		String aa = "";
		long dateTime = 0l;
		long d1 = getDateline("2000-01-01 00:00:00");
		if (str.length >= 25) {
			for (int i = 17; i < 21; i++) {
				int ss = (int) str[i];
				if (Integer.toHexString(ss & 0xff).length() == 1) {
					aa += '0';
				}
				aa += Integer.toHexString(ss & 0xff);
			}
			long ss = Long.parseLong(aa, 16);
			dateTime = (ss+d1)*1000;
		}
		return dateTime;
	}

	public byte getMsgId(byte[] str) {
		byte msgId = 0;
		if (str.length >= 25) {
			msgId = str[25];
		}
		return msgId;
	}

	public boolean checkSum(byte[] str){
		int sum = 0;
		String checkNum = "";
		if(str.length == (str[1]+5)){
			for(int i = 2;i<str.length-3;i++){
				sum += (int) str[i] & 0xFF;
			}
			for(int i = str.length-3;i<str.length-1;i++){
				int ss = (int) str[i];
				checkNum += Integer.toHexString(ss & 0xff);
			}
			int checkSum = Integer.parseInt(checkNum, 16);
			if(sum == checkSum){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * @description 解析message信息
	 * @date 2016年9月28日 下午3:36:42
	 * @param str
	 * @return
	 * @return Map
	 * @throws Exception 
	 */
	public void handlerMessage(byte[] str) throws Exception {
		Byte[] bs = null;
		if (str.length >= 26) {
			int pos = 24;
			int  msgLen = (int) str[pos+1];
			String deviceId = this.getDeviceId(str);
			long cmdTime = this.getTime(str);
			//long cmdTime = System.currentTimeMillis();
			this.initHotoMeter(deviceId, false,cmdTime);
			while ((pos <= str.length - (msgLen + 5)) && msgLen > 0) {
				bs = new Byte[msgLen+5];
				for(int i = 0;i< msgLen + 5;i++){
					bs[i]=str[pos+i];
				}
				if(bs[bs.length-1] == 0x1c){
					String msgId = Integer.toHexString(bs[0] & 0xff);
					if (msgId.equals("2e")) {
						
						this.parseMsgIdBy2e(deviceId, bs,cmdTime);
						
					}else if (msgId.equals("38")) {
						
						this.parseMsgIdBy38(str,deviceId, bs,cmdTime);
						
					}/*else if (msgId.equals("25")) {
						
						this.parseMsgIdBy25(str, deviceId, bs,cmdTime);
						
					}*/else if (msgId.equals("2f")) {
						
						this.parseMsgIdBy2f(str,deviceId, bs,cmdTime);
						
					}else if(msgId.equals("35")){
						
						this.parseMsgIdBy35(str,deviceId, bs,cmdTime);
						
					}else if(msgId.equals("2c")){
						
						this.parseMsgIdBy2c(str,deviceId, bs,cmdTime);
						
					}else if(msgId.equals("31")){
						
						this.parseMsgIdBy31(str,deviceId, bs,cmdTime);
						
					}
				}
				pos += msgLen + 5;
				if(pos < str.length){
					msgLen = (int) str[pos+1];
				}
			}
		}
	}

	private void parseMsgIdBy31(byte[] str, String deviceId, Byte[] bs, long cmdTime) {
		int totalNumber = (int)bs[2];
		String errorCodeString = "";
		if((bs.length-1) >= bs[1]){
			for(int i = 3 ; i <totalNumber*4+3;i+=4){
				String errorCode = "";
				for(int j = i ;j < i + 4 ; j++){
					int ss = bs[i];
					errorCode += (char)ss;
				}
				errorCodeString = errorCode + "|" + errorCodeString;
			}
		}
		if(carInfoManager==null){
			carInfoManager = GetBean.getBeanByClass(CarInfoManager.class);
		}
		if(errorCodeString != ""){
			errorCodeString = errorCodeString.substring(0, errorCodeString.length()-1);
			carInfoManager.updateCarErrorCodeInfo(deviceId, errorCodeString);
		}
	}

	private void parseMsgIdBy2c(byte[] str, String deviceId, Byte[] bs,long cmdTime) {
		// TODO Auto-generated method stub
		Map<String, Object> hotoMeter = hotometerMap.get(deviceId);
		JSONObject gpsJson = new JSONObject();
		if(bs.length >= 32){
			int gpsStatus = (int)bs[29];
			if((char)gpsStatus != 'V'){
				// 经度
				int longtitude1 = bs[2];
				int longtitude2 = 0;
				for (int i = 3; i < 6; i++) {
					longtitude2 = ((int) bs[i] & 0xFF) + (longtitude2 * 256);
				}
				// 纬度
				int laitude1 = bs[7];
				int laitude2 = 0;
				for (int i = 8; i < 11; i++) {
					laitude2 = ((int) bs[i] & 0xFF) + (laitude2 * 256);
				}
				System.out.println("[ls 2c] longtitude1 = " + longtitude1 + ",longtitude2 = " + longtitude2);
				System.out.println("[ls 2c] laitude1 = " + laitude1 + ",laitude2 = " + laitude2);
				FileUtil.appendWrite(filePath, "[ls 2c] longtitude1 = " + longtitude1 + ",longtitude2 = " + longtitude2);
				FileUtil.appendWrite(filePath, "[ls 2c] laitude1 = " + laitude1 + ",laitude2 = " + laitude2);
				if((laitude1 + laitude2 + longtitude1 + longtitude2) != 0){
					String longitude = longtitude1 + (double)longtitude2/1000000 + "" ;
					String latitude = laitude1 +  (double)laitude2/1000000 + "";
					System.out.println("[ls 2c] longitude/latitude: " + longitude + "/" + latitude);
					FileUtil.appendWrite(filePath, "[ls 2c] longitude/latitude: " + longitude + "/" + latitude);
					
					if(Double.valueOf(longitude) !=0  && Double.valueOf(latitude) != 0){
						double[] ss = GpsTransFormation.wgs2bd(Double.valueOf(latitude),Double.valueOf(longitude));
						System.out.println("[ls 2c] bd ongitude/latitude: " + ss[0] + "/" + ss[1]);
						FileUtil.appendWrite(filePath, "[ls 2c] bd ongitude/latitude: " + ss[0] + "/" + ss[1]);
						
						gpsJson.put("latitude",new DecimalFormat("#.00000000").format(ss[0]));
						gpsJson.put("longitude",new DecimalFormat("#.00000000").format(ss[1]));
						gpsJson.put("gps_time", toString(new Date(cmdTime), "yyyy-MM-dd HH:mm:ss"));
						gpsJson.put("parseMsgId","parseMsgIdBy2c");
					}
				/*	//更新车辆位置信息
					if(carInfoManager==null){
						carInfoManager = GetBean.getBeanByClass(CarInfoManager.class);
					}
					carInfoManager.updateCarPositionInfo(deviceId, gpsJson.toString());*/
				}
			}
		}
		this.addGpsInfo(deviceId, hotoMeter, gpsJson, cmdTime);
	}

	private void parseMsgIdBy35(byte[] str, String deviceId, Byte[] bs,long cmdTime) {
		Map<String, Object> hotoMeter = hotometerMap.get(deviceId);
		JSONObject gpsJson = new JSONObject();
		if(bs.length >= 32){
			int gpsStatus = (int)bs[29];
			if((char)gpsStatus != 'V'){
				// 经度
				int longtitude1 = bs[2];
				int longtitude2 = 0;
				for (int i = 3; i < 6; i++) {
					longtitude2 = ((int) bs[i] & 0xFF) + (longtitude2 * 256);
				}
				// 纬度
				int laitude1 = bs[7];
				int laitude2 = 0;
				for (int i = 8; i < 11; i++) {
					laitude2 = ((int) bs[i] & 0xFF) + (laitude2 * 256);
				}
				System.out.println("[ls 35] longtitude1 = " + longtitude1 + ",longtitude2 = " + longtitude2);
				System.out.println("[ls 35] laitude1 = " + laitude1 + ",laitude2 = " + laitude2);
				FileUtil.write(filePath, "[ls 35] longtitude1 = " + longtitude1 + ",longtitude2 = " + longtitude2);
				FileUtil.write(filePath, "[ls 35] laitude1 = " + laitude1 + ",laitude2 = " + laitude2);
				if((laitude1 + laitude2 + longtitude1 + longtitude2) != 0){
					String longitude = longtitude1 + (double)longtitude2/1000000 + "" ;
					String latitude = laitude1 +  (double)laitude2/1000000 + "";
					System.out.println("[ls 35] longitude/latitude: " + longitude + "/" + latitude);
					FileUtil.write(filePath, "[ls 35] longitude/latitude: " + longitude + "/" + latitude);
					if(Double.valueOf(longitude) !=0  && Double.valueOf(latitude) != 0){
						double[] ss = GpsTransFormation.wgs2bd(Double.valueOf(latitude),Double.valueOf(longitude));
						System.out.println("[ls 35] bd ongitude/latitude: " + ss[0] + "/" + ss[1]);
						FileUtil.write(filePath, "[ls 35] bd ongitude/latitude: " + ss[0] + "/" + ss[1]);
						gpsJson.put("latitude",new DecimalFormat("#.00000000").format(ss[0]));
						gpsJson.put("longitude",new DecimalFormat("#.00000000").format(ss[1]));
						gpsJson.put("gps_time", toString(new Date(cmdTime), "yyyy-MM-dd HH:mm:ss"));
						gpsJson.put("parseMsgId","parseMsgIdBy35");
					}
					/*	//更新车辆位置信息
					if(carInfoManager==null){
						carInfoManager = GetBean.getBeanByClass(CarInfoManager.class);
					}
					carInfoManager.updateCarPositionInfo(deviceId, gpsJson.toString());*/
				}
			}
		}
		this.addGpsInfo(deviceId, hotoMeter,gpsJson, cmdTime);
	}

	private void parseMsgIdBy2f(byte[] str,String deviceId, Byte[] bs,long cmdTime) {
		Map<String, Object> hotoMeter = hotometerMap.get(deviceId);
		JSONObject gpsJson = new JSONObject();
		int vehicleSpeed = Integer.parseInt(hotoMeter.get("vehicleSpeed").toString());
		// Vehicle speed
		String speed = bs[39].toString();
		if(!speed.equals("0")){
			hotoMeter.put("vehicleSpeed", speed);
		}
		if((Integer.parseInt(speed)-vehicleSpeed) >= 25 || (Integer.parseInt(speed)-vehicleSpeed) <= -25){
			if (Integer.toHexString(bs[4] & 0xff).equals("1")) {
				hotoMeter.put("slambrakenum", (int)hotoMeter.get("slambrakenum")+ 1);
			} else if (Integer.toHexString(bs[4] & 0xff).equals("2") || Integer.toHexString(bs[4] & 0xff).equals("5")) {
				hotoMeter.put("fastaccelarationnum", (int)hotoMeter.get("fastaccelarationnum")+1);
			} else if (Integer.toHexString(bs[4] & 0xff).equals("3")) {
				hotoMeter.put("speed3", (int)hotoMeter.get("speed3") + 1);
			} else if (Integer.toHexString(bs[4] & 0xff).equals("4")) {
				hotoMeter.put("speed4", (int)hotoMeter.get("speed4") + 1);
			} else if (Integer.toHexString(bs[4] & 0xff).equals("5")) {
				hotoMeter.put("speed5", (int)hotoMeter.get("speed5") + 1);
			}
		}
			// Car battery value
			if (Integer.toHexString(bs[5] & 0xff).equals("80")) {
				hotoMeter.put("batteryValue","12.8V");
			}else{
				hotoMeter.put("batteryValue","0.1V");
			}
			
			// battery
			if (Integer.toHexString(bs[6] & 0xff).equals("0")) {
				hotoMeter.put("battery", "Car battery OK");
			}else{
				hotoMeter.put("battery", "Car battery LOW");
			}
			// 经度
			String D519 = "";
			for (int i = 7; i < 10; i++) {
				int ss = (int) bs[i];
				if(ss != 0){
					D519 += (char)ss;
				}
			}
			String D520 = "";
			for (int i = 10; i < 22; i++) {
				int ss = (int) bs[i];
				if(ss != 0){
					D520 += (char)ss;
				}
			}
			//纬度
			String D2135 = "";
			for (int i = 23; i < 25; i++) {
				int ss = (int) bs[i];
				if(ss != 0){
					D2135 += (char)ss;
				}
			}
			String D2136="";
			for (int i = 25; i < 38; i++) {
				int ss = (int) bs[i];
				if(ss != 0){
					D2136 += (char)ss;
				}
			}
			if(D2135 != "" && D2136 != "" && D519 != "" && D520 != ""){
				double longitude = Double.valueOf(D519).doubleValue();
				double longitude1 =Double.valueOf(new DecimalFormat("#.000000000").format(Double.valueOf(D520).doubleValue()/60)).doubleValue();
				longitude += longitude1;
				
				double latitude = Double.valueOf(D2135).doubleValue();
				double latitude1 =Double.valueOf(new DecimalFormat("#.000000000").format(Double.valueOf(D2136).doubleValue()/60)).doubleValue();
				latitude += latitude1;
				
				System.out.println("[ls 2f] longitude/latitude: " + longitude + "/" + latitude);
				FileUtil.appendWrite(filePath, "[ls 2f] longitude/latitude: " + longitude + "/" + latitude);
				if(longitude != 0 && latitude != 0){
					double[] ss = GpsTransFormation.wgs2bd(latitude,longitude);
					System.out.println("[ls 2f] bd ongitude/latitude: " + ss[0] + "/" + ss[1]);
					FileUtil.appendWrite(filePath, "[ls 2f] bd ongitude/latitude: " + ss[0] + "/" + ss[1]);
					
					gpsJson.put("latitude",new DecimalFormat("#.000000000").format(ss[0]));
					gpsJson.put("longitude",new DecimalFormat("#.000000000").format(ss[1]));
					gpsJson.put("gps_time", toString(new Date(cmdTime), "yyyy-MM-dd HH:mm:ss"));
					gpsJson.put("parseMsgId","parseMsgIdBy2f");
				}
				/*//更新车辆位置信息
				if(carInfoManager==null){
					carInfoManager = GetBean.getBeanByClass(CarInfoManager.class);
				}
				carInfoManager.updateCarPositionInfo(deviceId, gpsJson.toString());*/
			}
			// Engine speed, rpm
			String rpm = "";
			for (int i = 40; i < 42; i++) {
				int ss = (int) bs[i];
				if (Integer.toHexString(ss & 0xff).length() == 1) {
					rpm += '0';
				}
				rpm += Integer.toHexString(ss & 0xff);
			}
			int rpmSpeed = Integer.parseInt(rpm, 16);
			int rpmSpeed2 = (int) hotoMeter.get("maxrevolution");
			if(rpmSpeed != 0 && rpmSpeed >= rpmSpeed2){
				hotoMeter.put("maxrevolution", rpmSpeed);
			}
			// Abnormal speed high value
			int highSpeed = bs[42];
			int highSpeed2 = (int) hotoMeter.get("maxspeed");
			if(highSpeed != 0 && highSpeed >= highSpeed2){
				hotoMeter.put("maxspeed", highSpeed);
			}
			// Abnormal speed low value
			int lowSpeed = bs[43];
			int lowSpeed2 = (int) hotoMeter.get("lowSpeed");
			if(lowSpeed <= lowSpeed2){
				hotoMeter.put("lowSpeed", lowSpeed);
			}
			this.addGpsInfo(deviceId, hotoMeter, gpsJson, cmdTime);
	}

	private void parseMsgIdBy25(byte[] str, String deviceId, Byte[] bs,long cmdTime) {
		Map<String, Object> mapaa = new HashMap<String,Object>();
		List<Object> list = new ArrayList<Object>();
		JSONObject json = new JSONObject();
		if(bs.length >= 28){
			int gpsStatus = (int)bs[12];
			if((char)gpsStatus != 'V'){
				// 经度
				String D03 = "";
				int longitude1 = bs[2];
				for (int i = 3; i < 6; i++) {
					int ss = (int) bs[i];
					D03 += Integer.toHexString(ss & 0xff);
				}
				// 纬度
				String D58 = "";
				int laitude1 = bs[7];
				for (int i = 8; i < 11; i++) {
					int ss = (int) bs[i];
					D58 += Integer.toHexString(ss & 0xff);
				}
				if(D03 != "" && D58 != "" ){
					int longitude2 = Integer.parseInt(D03, 16);
					String longitude = longitude1+"." + longitude2;
					
					int laitude2 = Integer.parseInt(D58, 16);
					String latitude = laitude1 + "." + laitude2;
					if(Double.valueOf(longitude) !=0  && Double.valueOf(latitude) != 0){
						double[] ss = GpsTransFormation.wgs2bd(Double.valueOf(latitude),Double.valueOf(longitude));
						json.put("latitude",new DecimalFormat("#.00000000").format(ss[0]));
						json.put("longitude",new DecimalFormat("#.00000000").format(ss[1]));
						json.put("gps_time", toString(new Date(cmdTime), "yyyy-MM-dd HH:mm:ss"));
					}
					/*//更新车辆位置信息
					if(carInfoManager==null){
						carInfoManager = GetBean.getBeanByClass(CarInfoManager.class);
					}
					carInfoManager.updateCarPositionInfo(deviceId, json.toString());*/
				}
			}
		}
		Map<String, Object> hotoMeter = hotometerMap.get(deviceId);
		if(hotoMeter.get("gpsinfo")!= null){
			if(!json.isEmpty()){
				if((cmdTime - (long)hotoMeter.get("dateTime"))  >= 30000){
					list.add(hotoMeter.get("gpsinfo").toString().substring(1,hotoMeter.get("gpsinfo").toString().length()-1)+","+json);
					json.put("dateTime", System.currentTimeMillis());
				}
			}else{
				list.add(hotoMeter.get("gpsinfo").toString().substring(1,hotoMeter.get("gpsinfo").toString().length()-1));
			}
		}else{
			if(!json.isEmpty()){
				if((cmdTime - (long)hotoMeter.get("dateTime"))  >= 30000){
					list.add(json);
					json.put("dateTime", System.currentTimeMillis());
				}
			}
		}
		mapaa.putAll(hotoMeter);
		if(!list.isEmpty()){
			mapaa.put("gpsinfo",list);
		}
		hotometerMap.put(deviceId, mapaa);
	}

	private void parseMsgIdBy38(byte[] str,String deviceId, Byte[] bs,long cmdTime) throws Exception {
		String onOff = Integer.toHexString(bs[2] & 0xff);
		Map<String, Object> hotoMeter = hotometerMap.get(deviceId);
		long  totalmile = 0;
		if (onOff.equals("0")) {
			String D25 = "";
			for (int i = 4; i < 8; i++) {
				int ss = (int) bs[i];
				if(ss != 0){
					if (Integer.toHexString(ss & 0xff).length() == 1) {
						D25 += '0';
					}
					D25 += Integer.toHexString(ss & 0xff);
					
				}
			}
			if(D25 != ""){
				totalmile = Long.parseLong(D25, 16);
				hotoMeter.put("mile", totalmile*(Math.pow(10,-4))); //里程km
			}else{
				hotoMeter.put("mile", 0);
			}
			String D69 = "";
			for (int i = 8; i < 12; i++) {
				int ss = (int) bs[i];
				if(ss != 0){
					if (Integer.toHexString(ss & 0xff).length() == 1) {
						D69 += '0';
					}
					D69 += Integer.toHexString(ss & 0xff);
				}
			}
			if(D69 != ""){
				long  totalconsumed = Long.parseLong(D69, 16);
				hotoMeter.put("oilconsumption", totalconsumed*(Math.pow(10,-6))); //油耗L
			}else{
				hotoMeter.put("oilconsumption", 0);
			}
			Object gpsinfo = hotoMeter.get("gpsinfo");
			if(gpsinfo != null ){
				JSONArray array = JSONArray.fromObject(gpsinfo.toString());
				if(array.size() >= 3){
					hotometerMap.put(deviceId,hotoMeter);
					// 添加行程上传到数据库方法
					if (!hotometerMap.get(deviceId).isEmpty() && (totalmile*(Math.pow(10,-4)))>0.2) {
						if(carHodometerManager==null){
							carHodometerManager = GetBean.getBeanByClass(CarHodometerManager.class);
						}
						carHodometerManager.updateCarHodometer(hotometerMap.get(deviceId).toString());
					}
				}
			}
			//开始一段新的行程
			initHotoMeter(deviceId, true,cmdTime);
		} 
	}

	private void parseMsgIdBy2e(String deviceId, Byte[] bs,long cmdTime) {
		Map<String, Object> msg = new HashMap<String, Object>();
		if (Integer.toHexString(bs[23] & 0xff).equals("31")) {
			msg.put("D21", "activated");
		} else {
			msg.put("D21", "not activated");
		}
		if (Integer.toHexString(bs[24] & 0xff).equals("1")) {
			msg.put("D22", "test OK");
		} else if (Integer.toHexString(bs[24] & 0xff) == "2") {
			msg.put("D22", "test fail");
		} else {
			msg.put("D22", "not tested");
		}
		// 添加处理方法

	}
	public void initHotoMeter(String deviceId, boolean bForce,long cmdTime){
		Map<String, Object> hotoMeter = hotometerMap.get(deviceId);
		if(hotoMeter == null || bForce){
			Map<String, Object> mapaa = new HashMap<String,Object>();
			mapaa.put("slambrakenum",0 );
			mapaa.put("fastaccelarationnum", 0);
			mapaa.put("maxrevolution", 0);
			mapaa.put("maxspeed", 0);
			mapaa.put("lowSpeed", 0);
			mapaa.put("totalmile",0);
			mapaa.put("mile",0);
			mapaa.put("deviceId", deviceId);
			mapaa.put("gpsinfo",null);
			mapaa.put("dateTime", 0l);
			mapaa.put("vehicleSpeed",0);
			hotometerMap.put(deviceId,mapaa);
		}
	}
	public static void addGpsInfo(String deviceId, Map<String, Object> hotoMeter, JSONObject gpsJson, long cmdTime){
		List<Object> list = new ArrayList<Object>();
		long interval = Math.abs(cmdTime - (long)hotoMeter.get("dateTime"));
		if(hotoMeter.get("gpsinfo") != null){
			if(!gpsJson.isEmpty()){
				if(interval >= 15000){
					list.add(hotoMeter.get("gpsinfo").toString().substring(1,hotoMeter.get("gpsinfo").toString().length()-1)+","+gpsJson);
					hotoMeter.put("dateTime", cmdTime);
				}
				//更新车辆位置信息
				if(carInfoManager==null){
					carInfoManager = GetBean.getBeanByClass(CarInfoManager.class);
				}
				carInfoManager.updateCarPositionInfo(deviceId, gpsJson.toString());
				
			}else{
				list.add(hotoMeter.get("gpsinfo").toString().substring(1,hotoMeter.get("gpsinfo").toString().length()-1));
			}
		}else{
			if(!gpsJson.isEmpty()){
				if(interval  >= 15000){
					list.add(gpsJson);
					hotoMeter.put("dateTime", cmdTime);
				}
				//更新车辆位置信息
				if(carInfoManager==null){
					carInfoManager = GetBean.getBeanByClass(CarInfoManager.class);
				}
				carInfoManager.updateCarPositionInfo(deviceId, gpsJson.toString());
			}
		}
		if(!list.isEmpty()){
			hotoMeter.put("gpsinfo",list);
			System.out.println("[ls 2f] : " + JSONArray.fromObject(list).toString() );
		}
		hotometerMap.put(deviceId, hotoMeter);
	}
	public static void main(String args[]) throws Exception{
		MessageParse pack = new MessageParse();
		byte[][] message = {
				   {0x4a,0x54,0x41,0x30,0x30,0x30,0x30,0x31,0x31,0x32,0x32,0x32,0x30,0x30,0x30,0x32,0xf,0x1f,0xffffffab,0xb,0xffffffe3,0x2,0x16,0x55,
					   0x38,0x1b,0x0,0xffffff84,0x0,0x0,0x71,0x49,0x0,0x5,0x23,0x43,0x31,0x4a,0x38,0x47,0x43,0x34,0x38,0x4b,0x33,0x38,0x59,0x31,0x31,0x33,0x39,0x33,0x35,0x5,0xffffff97,0x1c
					   }
		  				};
				for(int i=0;i<message.length;i++){
					byte[] messageStr = message[i];
					pack.handlerMessage(messageStr);
				}	
	}
	public static boolean useList(byte[] arr, byte targetValue) {
	    return Arrays.asList(arr).contains(targetValue);
	}
	public static long getDateline(String date) {
		return (long) (toDate(date, "yyyy-MM-dd hh:mm:ss").getTime() / 1000);
	}

	/**
	 * 将一个字符串转换成日期格式
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date toDate(String date, String pattern) {
		if (("" + date).equals("")) {
			return null;
		}
		if (pattern == null) {
			pattern = "yyyy-MM-dd";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date newDate = new Date();
		try {
			newDate = sdf.parse(date);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return newDate;
	}

	/**
	 * 把日期转换成字符串型
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String toString(Date date, String pattern) {
		if (date == null) {
			return "";
		}
		if (pattern == null) {
			pattern = "yyyy-MM-dd";
		}
		String dateString = "";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			dateString = sdf.format(date);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dateString;
	}

	
	/**
	 * 把日期转换成字符串型
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String toStringTwo(String dateStr, String pattern) {
		if (pattern == null) {
			pattern = "yyyyMMddhhmmss";
		}
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		String dateString = "";
		try {
			dateString = df.parse(dateStr).toLocaleString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateString;
	}

}
