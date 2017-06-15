package com.enation.eop.sdk.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.ParseException;

/**
 * @create 2007-7-9
 */
public class StringUtil {
	/**
	 * use to add zero behand the code which length is less then length.
	 * 
	 * @param oldStr
	 *            String 原字符串
	 * @param length
	 *            int 指定长度
	 * @return String 新字符串
	 */
	public static String addZero(String oldStr, int length) {
		String newStr = "";
		newStr = oldStr;

		while (newStr.length() < length) {
			newStr = "0" + newStr;
		}
		return newStr;
	}
	
	/**
	 * use to add blank after the code which length is less then length.
	 * 
	 * @param oldStr
	 *            String 原字符串
	 * @param length
	 *            int 指定长度
	 * @return String 新字符串
	 */
	public static String addBlankAfter(String oldStr, int length) {
		StringBuffer newStr = new StringBuffer();
		newStr.append(oldStr);

		while (newStr.length() < length) {
			newStr.append(" ");
		}
		return newStr.toString();
	}
	/**
	 * 将NULL的对象转换为空串
	 * 
	 * @param obj：要转换的对象
	 * @return
	 */
	public static Object null2blank(Object obj) {
		if (obj == null)
			return "";
		else
			return obj;
	}
	
	

	/**
	 * 将NULL的字符串转换为空串
	 * 
	 * @param str：要转换的字符串
	 * @return
	 */
	public static String null2Zero(Object str) {
		if (str == null||"".equals(str))
			return "0";
		else
			return str.toString();
	}
	/**
     * 将NULL或""的字符串转换为0
     * 
     * @param str：要转换的字符串
     * @return
     */
    public static String null2blank(String str) {
        if (str == null)
            return "";
        else
            return str;
    }
	private static String temp = "";

	/**
	 * 同String replaceAll(String str1, String str2)
	 * 
	 * @param str：要操作的串
	 * @param oldStr：原串
	 * @param newStr：替换串
	 * @return
	 */
	public static String replaceAll(String str, String oldStr, String newStr) {
		int index = str.indexOf(oldStr);
		if (index < 0) {
			return temp + str;
		} else {
			String first = str.substring(0, index);
			// String second = str.substring(index, index + oldStr.length());
			String third = str.substring(index + oldStr.length());

			// String rtVal = first + newStr + third;
			temp += first + newStr;
			return replaceAll(third, oldStr, newStr);
		}
	}

	/**
	 * 将数组转为指定分割符分割的字符串
	 * 
	 * @param strs：数组
	 * @param delim：分割符
	 * @return
	 */
	public static String array2String(String[] strs, String delim) {
		if (strs == null || strs.length < 1)
			return "";
		if (delim == null)
			delim = ",";

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strs.length; i++) {
			sb.append(strs[i] + delim);
		}

		String str = sb.toString();
		return str.substring(0, str.length() - delim.length());
	}

	public static String changEncode(String html) {
		if (html == null) {
			html = "";
		}
		StringBuffer sb = new StringBuffer(html.length() + html.length() / 4);
		for (int i = 0; i < html.length(); i++) {
			char ch = html.charAt(i);
			switch (ch) {
			case '\n':
				sb.append("<br>");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case ' ':
				sb.append("&nbsp;");
				break;
			default:
				sb.append(ch);
			}
		}
		String value = sb.toString();
		return value;
	}

	/**
	 * 字符串是否是整数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 将空串转换为NULL的字符串
	 * 
	 * @param str：要转换的字符串
	 * @return
	 */
	public static String blank2null(String str) {
		if (str != null && str.trim().length() == 0)
			return null;
		else
			return str;
	}

	public static Date StrToDate(String args) {
		Date date1 = null;
		try {
			date1 = Date.valueOf(args);
			return date1;
		} catch (Exception e) {
			try {
				throw new ParseException("转换字符串无法解析为日期错误");
			} catch (ParseException e1) {
				e1.getStackTrace();
			}
			return null;
		}

	}
	
	public static String lpadStr(String str,int length,String padStr){
		String desStr = "";
		if(str==null||"".equals(str)){
			for(int i = 0; i < length; ++i){
				desStr += padStr;
			}
			return desStr;
		}
		else if(str.length()>=length){
			desStr = str.substring(0, length);
			return desStr;
		}
		else{
			desStr = str;
			for(int i = 0; i < length-str.length(); ++i){
				desStr = padStr+desStr;
			}
			return desStr;
		}
	}
	
	
	
	
	/**
	 * 功能：判断字符串是否为空串或空值
	 * @param targetStr
	 * @return 返回：非空返回true，否则为false。
	 */
	public static boolean isNull(String targetStr){
		if (targetStr==null||"".equals(targetStr)) {
			return true;
		}
		return false;
	}
	
	
	
	/**
	 * 功能：判断字符串是否为空串或空值
	 * @param targetStr
	 * @return 返回：非空返回true，否则为false。
	 */
	public static boolean notNull(String targetStr){
		if (targetStr==null||"".equals(targetStr)) {
			return false;
		}
		return true;
	}
	
	
	
	
	/**
	 * 功能：判断字符串是否为0
	 * @param str
	 * @return
	 */
	public static boolean isZero(String str){
		str = str==null?"1":str;
		BigDecimal decimal = new BigDecimal(str);
		return decimal.compareTo(new BigDecimal(0))==0?true:false;
	}

	
	
	
	/**
	 * 功能：判断字符串是否不为0
	 * @param str
	 * @return
	 */
	public static boolean isNotZero(String str){
		str = str==null?"1":str;
		BigDecimal decimal = new BigDecimal(str);
		return decimal.compareTo(new BigDecimal(0))==0?false:true;
	}
	
	
	
	/**
	 * 功能：求两个数的合
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String addFen(String str1 , String str2){
		return new BigDecimal(str1).add(new BigDecimal(str2)).toBigInteger().toString();
	}
	
	
	

	/**
	 * 功能：求两个数的合
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String addYuan(String str1 , String str2){
		return new BigDecimal(str1).add(new BigDecimal(str2)).toString();
	}

	
	/**
	 * 比较两个字符串金额，第一个是否比第二个大。
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean whoBig(String str1, String str2){
		BigDecimal b1 = new BigDecimal(str1);
		BigDecimal b2 = new BigDecimal(str2);
		return b1.compareTo(b2)>0?true:false;
	}
	
	
	
	
	/**
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String calcCha(String str1, String str2){
		BigDecimal b1 = new BigDecimal(str1);
		BigDecimal b2 = new BigDecimal(str2);
		return b1.subtract(b2).toString();
	}
	
	
	
	/**
	 * 解码GET方法传递的参数
	 * @param param
	 * @return
	 */
	public static String decodeForGetRequest(String param){
		try {
			return new String(param.getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.getStackTrace();
		}
		return "";
	}
	
	
	
	/**
	 * 去除千分位（逗号）
	 * @param amount
	 * @return
	 */
	public static String unQFW(String amount){
		return amount.replaceAll(",", "");
	}
	
	
	
	/**
	 * @funcDesc：ids转换（将List转换为1,2,3……形式）
	 * @developer：jun.liu
	 * @date：2015年6月5日 上午11:33:31
	 * @param ids
	 * @return
	 * String
	 */
	public static String convertIds(List<String> ids){
		return ids.toString().replaceAll("\\[|\\]", "");
	}
	
	
	/**
	 * 功能描述：ids转换（将List转换为1,2,3……形式）
	 * @developer：jun.liu
	 * @date：2016年1月8日 下午6:03:33
	 * @param list
	 * @param splitFlag 指定分隔符
	 * @param addFlag 是否追加单引号
	 * @return
	 * String
	 */
	public static String splitByList(List<String> list,String splitFlag,boolean addFlag){
		String content = "";
		if(!addFlag){
			content = list.toString().replaceAll("\\[|\\]", "");
			content.replaceAll(",", splitFlag);
		}else {
			StringBuilder sb = new StringBuilder();
			for (String str : list) {
				sb.append("'").append(str).append("'").append(splitFlag);
			}
			content = removeLastSplitFlag(sb);
		}
		return content;
	}
	
	
	/**
	 * 
	 * @功能描述：如果参数不为空，则将用原参数+剩余占位符补齐长度；为空则用占位符填充
	 * @developer：Hao.Pang
	 * @date：2015年7月10日 上午11:17:41
	 * @param zhanWeiFu 占位符
	 * @param str  原字符串
	 * @return
	 * String
	 */
	public static String replaceZhanWeiFuToStr(String zhanWeiFu, String str){
		if(ValidateUtils.isEmpty(str)){
			return zhanWeiFu;
		}else{
			int strLength = str.length();
			return str+zhanWeiFu.substring(strLength);
		}
	}
	
	
	
	
	/**
	 * 功能描述：去除最后一个分隔符
	 * 备注：常用于stringbuilder，去除逗号。
	 * @developer：jun.liu
	 * @date：2015年12月25日 上午11:57:35
	 * @param content
	 * @return
	 * String
	 */
	public static String removeLastSplitFlag(String content){
		return content.substring(0, content.length()-1);
	}
	
	/**
	 * 功能描述：去除最后一个分隔符
	 * 备注：常用于stringbuilder，去除逗号。
	 * @developer：jun.liu
	 * @date：2015年12月25日 上午11:57:35
	 * @param content
	 * @return
	 * String
	 */
	public static String removeLastSplitFlag(StringBuilder sb){
		String content = sb.toString();
		return removeLastSplitFlag(content);
	}
	
	/**
	 * 功能描述：去除最后一个分隔符
	 * 备注：常用于stringbuilder，去除逗号。
	 * @developer：jun.liu
	 * @date：2015年12月25日 上午11:57:35
	 * @param content
	 * @return
	 * String
	 */
	public static String removeLastSplitFlag(StringBuffer sb){
		String content = sb.toString();
		return removeLastSplitFlag(content);
	}
}
