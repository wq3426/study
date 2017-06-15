package com.enation.app.shop.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.AdminSettlementType;
import com.enation.app.shop.core.service.OrderType;
import com.enation.framework.util.StringUtil;
import com.hp.hpl.sparta.xpath.ThisNodeTest;

/** @Description (获取流水号工具类)
 *
 * @createTime 2016年9月1日 上午11:59:27
 *
 * @author <a href="mailto:huashixin@trans-it.cn">huashixin</a>
 */

public class SerailNumberUtils {
	
	/**
	 * @param bigType 交易大类
	 * @param smallType 交易小类
	 * @param count   当天交易顺位
	 * @return
	 */
	public static  String getSerailNumber(int bigType,int smallType,int count){
		StringBuffer serailNumber = new StringBuffer();
		//前8位为交易日期
		String time = new SimpleDateFormat("yyyyMMdd").format(new Date());
		serailNumber.append(time);
		
		//9-10位为交易大类
		if(bigType < 10){
			serailNumber.append(0).append(bigType);
		}else{
			serailNumber.append(bigType);
		}
		//11-12位为交易小类	
		if(bigType < 10){
			serailNumber.append(0).append(smallType);
		}else{
			serailNumber.append(smallType);
		}
		
		//13-18预留位  6
		serailNumber.append("000000");
		 
		//19-23当天交易顺位  5
		count += 1;
		String sequence = String.valueOf(count);
		int length = sequence.length();
		while(length < 5){
			sequence = "0" + sequence; 
			length++;
		}
		serailNumber.append(sequence);
		
//		//24-25加俩位随机数
//		serailNumber.append(getRandom());
		//24-25位为校验位
		int lastNum = getLast(serailNumber.toString());
		if(lastNum < 10){
			serailNumber.append(0).append(lastNum);
		}else{
			serailNumber.append(lastNum);
		}
	
		return 	serailNumber.toString();
	}
	
	
	/** @description 最后俩位随机位不使用
	 * @date 2016年9月1日 上午11:59:32
	 * @return
	 * @return int
	 */
	public static int getRandom(){
		Random random=new Random();
		int num=Math.abs(random.nextInt())%100;
		if(num<10){
			num=getRandom();
		}
		return num;
	}
	
	/** @description 最后俩位校验位使用异或方式处理
	 * @date 2016年9月1日 下午12:00:31
	 * @return
	 * @return int
	 */
	public static int getLast(String serailNumber){
		int num = (int)serailNumber.charAt(0) - '0';
		for(int i = 1 ; i < serailNumber.length();i++){
			num ^= (int)(serailNumber.charAt(i) - '0');
		}
		return num;
	}
	
}
