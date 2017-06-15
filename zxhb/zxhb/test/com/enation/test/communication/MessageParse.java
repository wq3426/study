package com.enation.test.communication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.service.ICarHodometerManager;
import com.enation.app.shop.core.service.impl.CarHodometerManager;

public class MessageParse {

	private byte SSEP = 0x1c;
	public static Map<String, Map<String, Object>> hotometerMap = new HashMap<String, Map<String, Object>>();
	public static List list;
	private MessageHandler messageHandler = MessageHandler.getInstance();

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

	public Map parsePacket(Byte[] str) {
		Map<String, String> map = new HashMap<String, String>();
		String deviceId = this.getDeviceId(str);
		map.put("deviceId", deviceId);
		Byte fid = this.getFId(str);
		map.put("fid", fid.toString());
		String dateTime = this.getTime(str);
		map.put("time", dateTime);
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
	public String getDeviceId(Byte[] str) {
		StringBuilder sb = new StringBuilder();
		if (str.length >= 25) {
			for (int i = 0; i < 16; i++) {
				sb.append((char) str[i].intValue());
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
	public byte getFId(Byte[] str) {
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
	public String getTime(Byte[] str) {
		String aa = "";
		String dateTime = "";
		long d1 = getDateline("2000-01-01 00:00:00");
		if (str.length >= 25) {
			for (int i = 17; i < 21; i++) {
				int ss = (int) str[i];
				aa += Integer.toHexString(ss & 0xff);
			}
			long ss = Long.parseLong(aa, 16);
			dateTime = toString(new Date(ss - d1), "yyyy-MM-dd HH:mm:ss");
		}
		return dateTime;
	}

	public byte getMsgId(Byte[] str) {
		byte msgId = 0;
		if (str.length >= 25) {
			msgId = str[25];
		}
		return msgId;
	}

	/**
	 * @description 解析message信息
	 * @date 2016年9月28日 下午3:36:42
	 * @param str
	 * @return
	 * @return Map
	 * @throws Exception 
	 */
	public void handlerMessage(Byte[] str) throws Exception {
		Byte[] bs = null;
		if (str.length >= 26) {
			int aa = (int) str[25];
			int pos = 24;
			int bsb = 0;
			String deviceId = this.getDeviceId(str);
			while (pos <= str.length - aa - 5) {
				if (pos == str.length - aa - 5) {
					bs = new Byte[str.length - pos];
					for (int i = pos - bsb; i < str.length - pos + 24; i++) {
						for (int j = i - pos + bsb; j < i - pos + bsb + 1; j++) {
							bs[j] = str[i];
						}
					}
				} else {
					bs = new Byte[str.length - pos - aa - 5];
					for (int i = aa + pos + 5; i < str.length; i++) {
						for (int j = i - pos - aa - 5; j < i - aa - pos - 4; j++) {
							bs[j] = str[i];
						}
					}
				}
				String msgId = Integer.toHexString(bs[0] & 0xff);
				if (msgId.equals("2e")) {

					this.parseMsgIdBy2e(deviceId, bs);

				} else if (msgId.equals("38")) {

					this.parseMsgIdBy38(str,deviceId, bs);

				} else if (msgId.equals("25")) {

					this.parseMsgIdBy25(str, deviceId, bs);

				} else if (msgId.equals("2f")) {

					this.parseMsgIdBy2f(deviceId, bs);

				}
				pos += bs.length;
				bsb = bs.length;
			}
		}
	}

	private void parseMsgIdBy2f(String deviceId, Byte[] bs) {
		Map<String, Object> hotoMeter = hotometerMap.get(deviceId);
		Map<String, Object> mapaa = new HashMap<String, Object>();
		if (hotoMeter == null) {
			// Speed
			if (Integer.toHexString(bs[4] & 0xff).equals("1")) {
				mapaa.put("slambrakenum", 1);
			} else if (Integer.toHexString(bs[4] & 0xff).equals("2") || Integer.toHexString(bs[4] & 0xff).equals("5")) {
				mapaa.put("fastaccelarationnum", 1);
			} else if (Integer.toHexString(bs[4] & 0xff).equals("3")) {
				mapaa.put("speed3", 1);
			} else if (Integer.toHexString(bs[4] & 0xff).equals("4")) {
				mapaa.put("speed4", 1);
			} else {
				mapaa.put("speed5", 1);
			}
			// Car battery value
			if (Integer.toHexString(bs[5] & 0xff).equals("80")) {
				mapaa.put("batteryValue","12.8V");
			}else{
				mapaa.put("batteryValue","0.1V");
			}
			// battery
			if (Integer.toHexString(bs[6] & 0xff).equals("0")) {
				mapaa.put("battery", "Car battery OK");
			}else{
				mapaa.put("battery", "Car battery LOW");
			}
			// Vehicle speed
			String speed = bs[39].toString();
			mapaa.put("vehicleSpeed", speed);
			// Engine speed, rpm
			String rpm = "";
			for (int i = 40; i < 42; i++) {
				int ss = (int) bs[i];
				rpm += Integer.toHexString(ss & 0xff);
			}
			int rpmSpeed = Integer.parseInt(rpm, 16);
			mapaa.put("maxrevolution", rpmSpeed + "");
			// Abnormal speed high value
			String highSpeed = bs[42].toString();
			mapaa.put("maxspeed", highSpeed);
			// Abnormal speed low value
			String lowSpeed = bs[43].toString();
			mapaa.put("lowSpeed", lowSpeed);
		}else{
			if (Integer.toHexString(bs[4] & 0xff).equals("1")) {
				if (hotoMeter.containsKey("slambrakenum")) {
					mapaa.put("slambrakenum", hotoMeter.get("slambrakenum") + "" + 1);
				}
			} else if (Integer.toHexString(bs[4] & 0xff).equals("2") || Integer.toHexString(bs[4] & 0xff).equals("5")) {
				if (hotoMeter.containsKey("fastaccelarationnum")) {
					mapaa.put("fastaccelarationnum", hotoMeter.get("fastaccelarationnum") + "" + 1);
				}
			} else if (Integer.toHexString(bs[4] & 0xff).equals("3")) {
				if (hotoMeter.containsKey("speed3")) {
					mapaa.put("speed3", hotoMeter.get("speed3") + "" + 1);
				}
			} else if (Integer.toHexString(bs[4] & 0xff).equals("4")) {
				if (hotoMeter.containsKey("speed4")) {
					mapaa.put("speed4", hotoMeter.get("speed4") + "" + 1);
				}
			} else if (Integer.toHexString(bs[4] & 0xff).equals("5")) {
				if (hotoMeter.containsKey("speed5")) {
					mapaa.put("speed5", hotoMeter.get("speed5") + "" + 1);
				}
			}
			// Car battery value
			if(hotoMeter.containsKey("batteryValue")){
				mapaa.put("batteryValue",Integer.toHexString(bs[5] & 0xff));
			}
			// battery
			if(hotoMeter.containsKey("battery")){
				mapaa.put("battery",Integer.toHexString(bs[6] & 0xff));
			}
			
			// Vehicle speed
			if(hotoMeter.containsKey("vehicleSpeed")){
				mapaa.put("vehicleSpeed",bs[39].toString());
			}
			
			// Engine speed, rpm
			String rpm = "";
			for (int i = 40; i < 42; i++) {
				int ss = (int) bs[i];
				rpm += Integer.toHexString(ss & 0xff);
			}
			int rpmSpeed = Integer.parseInt(rpm, 16);
			if(hotoMeter.containsKey("maxrevolution")){
				mapaa.put("maxrevolution",rpmSpeed+"");
			}
			// Abnormal speed high value
			if(hotoMeter.containsKey("maxspeed")){
				mapaa.put("maxspeed",bs[42].toString());
			}
			// Abnormal speed low value
			if(hotoMeter.containsKey("lowSpeed")){
				mapaa.put("lowSpeed",bs[43].toString());
			}
			mapaa.put("gpsinfo", hotoMeter.get("gpsinfo"));
		}
		hotometerMap.put(deviceId, mapaa);
		System.out.println(hotometerMap);
	}

	private void parseMsgIdBy25(Byte[] str, String deviceId, Byte[] bs) {
		Map<String, Object> msg = new HashMap<String, Object>();
		Map<String, Object> mapaa = new HashMap<String, Object>();
		// 经度
		String D03 = "";
		int longitude1 = bs[2];
		for (int i = 3; i < 6; i++) {
			int ss = (int) bs[i];
			D03 += Integer.toHexString(ss & 0xff);
		}
		int longitude2 = Integer.parseInt(D03, 16);
		String longitude = longitude1 + "." + longitude2;
		msg.put("longitude", longitude);
		/*
		 * if(Integer.toHexString(bs[4] & 0xff).equals("e")){ msg.put(
		 * "E/W indicator", "east"); }else if(Integer.toHexString(bs[4] &
		 * 0xff).equals("w")){ msg.put("E/W indicator", "west"); }else{ msg.put(
		 * "E/W indicator", "N/A"); }
		 */
		// 纬度
		String D58 = "";
		int laitude1 = bs[7];
		for (int i = 7; i < 11; i++) {
			int ss = (int) bs[i];
			D58 += Integer.toHexString(ss & 0xff);
		}
		int laitude2 = Integer.parseInt(D58, 16);
		String laitude = laitude1 + "." + laitude2;
		msg.put("laitude", laitude);
		/*
		 * if(Integer.toHexString(bs[9] & 0xff).equals("n")){ msg.put(
		 * "N/S indicator", "north"); }else if(Integer.toHexString(bs[9] &
		 * 0xff).equals("s")){ msg.put("N/S indicator", "south"); }else{
		 * msg.put("N/S indicator", "N/A"); }
		 */
		// utc time
		String time = "";
		String dateTime = "";
		long d1 = getDateline("2000-01-01 00:00:00");
		if (str.length >= 25) {
			for (int i = 17; i < 21; i++) {
				int ss = (int) str[i];
				time += Integer.toHexString(ss & 0xff);
			}
			long ss = Long.parseLong(time, 16);
			dateTime = toString(new Date(ss - d1), "yyyy-MM-dd HH:mm:ss");
		}
		msg.put("dateTime", dateTime);
		Map<String, Object> hotoMeter = hotometerMap.get(deviceId);
		if(hotoMeter == null){
			mapaa.put("gpsinfo", msg);
		}else{
			mapaa.putAll(hotoMeter);
			mapaa.put("gpsinfo", msg);
		}
		hotometerMap.put(deviceId, mapaa);
		System.out.println(hotometerMap);
	}

	private void parseMsgIdBy38(Byte[] str,String deviceId, Byte[] bs) throws Exception {
		
		String onOff = Integer.toHexString(bs[2] & 0xff);
		String batteryValue = Integer.toHexString(bs[3] & 0xff);
		ICarHodometerManager carHotometerManager = new CarHodometerManager();
		Map<String, Object> mapaa = new HashMap<String, Object>();
		if (onOff.equals("0")) {
			mapaa.put("Engine ON/OFF ", "off");
			// 添加行程上传到数据库方法
			if (!hotometerMap.get(deviceId).isEmpty()) {
				carHotometerManager.updateCarHodometer(hotometerMap.get(deviceId).toString().replace("=", ":"));
			}
		} else if (onOff.equals("1")) {
			mapaa.put("Engine ON/OFF", "on");
			mapaa.put("batteryValue", batteryValue);
			hotometerMap.put(deviceId,mapaa);
		} else if (onOff.equals("2")) {
			mapaa.put("Engine ON/OFF ", "off to on event");
		} else {
			mapaa.put("Engine ON/OFF ", "sleeping");
		}
	}

	private void parseMsgIdBy2e(String deviceId, Byte[] bs) {
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

	
	public static void main(String args[]) throws Exception{
		Byte[] message = {0x4a,0x54,0x41,0x30,0x30,0x30,0x30,0x31,0x31,0x31,0x38,0x34,0x30,0x30,0x30,0x32,0x12,0x01,(byte)0xe2,(byte)0xfe,0x63,0x02,0x16,0x55,0x25,0x1d,
	  			0x79, 0x06,(byte) 0xA6,(byte) 0xDC,0x0e,0x79,0x06,(byte) 0xA6,(byte) 0xDC,0x34,0x52,0x37,0x32,0x35,0x32,0x33,0x36,0x37,0x31,0x30,0x34,0x33,0x31,0x30,0x00,0x34,0x34,
	  			0x34,0x34,0x04,(byte)0xec,0x1c,0x2f,0x2a,0x01,0x00,0x00,(byte) 0x96,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	  			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,(byte) 0xef,(byte) 0xc7,(byte) 0xff,0x00,0x04c,(byte)0xec,0x1c,0x2f,0x2a,0x01,0x00,0x00,(byte) 0x96,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
	  			0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,(byte) 0xef,(byte) 0xc7,(byte) 0xff,0x00,0x04c,(byte)0xec,0x1c,0x25,0x1d,
	  			0x79, 0x06,(byte) 0xA6,(byte) 0xDC,0x0e,0x79,0x06,(byte) 0xA6,(byte) 0xDC,0x34,0x52,0x37,0x32,0x35,0x32,0x33,0x36,0x37,0x31,0x30,0x34,0x33,0x31,0x30,0x00,0x34,0x34,
	  			0x34,0x34,0x04,(byte)0xec,0x1c};

		MessageParse pack = new MessageParse();
		pack.handlerMessage(message);
	}
	
	public static long getDateline(String date) {
		return (long) (toDate(date, "yyyy-MM-dd").getTime() / 1000);
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
}
