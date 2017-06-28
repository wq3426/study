package org.wq.util;

import java.util.regex.Pattern;

/**
 * @Description 正则表达式校验工具类
 *
 * @createTime 2016年10月27日 下午7:40:53
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
public class RegularExpressionUtils {
	/**
	 * 相关资料链接：
	 * 日期正则  http://blog.csdn.net/lxcnn/article/details/4362500
	 */
	
	/**
	 * 正整数     ^\\d+$
	 * 浮点数     ^(-?\\d+)(\\.\\d+)?$
	 * 车牌号     ^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[警京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]{0,1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$
	 * 身份证号码  ^[1-9]{1}[0-9]{14}$|^[1-9]{1}[0-9]{16}([0-9]|[xX])$
	 * 日期格式  yyyy-MM-dd 或 yyyy-M-d ^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$
	 */
	public static final String intPattern = "^\\d+$";
	
	public static final String doublePattern = "^(-?\\d+)(\\.\\d+)?$";
	
	public static final String carplatePattern = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[警京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]{0,1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$";
	
	public static final String identityIDPattern = "^[1-9]{1}[0-9]{14}$|^[1-9]{1}[0-9]{16}([0-9]|[xX])$";
	
	public static final String y_M_d_DatePattern = "^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$";
	
	public static final String mobilePattern = "^((\\d{3}-\\d{8}|\\d{4}-\\d{7,8})|(1[3|5|7|8][0-9]{9}))$";
	
	//yyyy-MM-dd HH:mm:ss
	public static final String timePattern = "(\\d{2}|\\d{4})(?:\\-)?([0]{1}\\d{1}|[1]{1}[0-2]{1})(?:\\-)?([0-2]{1}\\d{1}|[3]{1}[0-1]{1})(?:\\s)?([0-1]{1}\\d{1}|[2]{1}[0-3]{1})(?::)?([0-5]{1}\\d{1})(?::)?([0-5]{1}\\d{1})";
	
	/**
	 * @description 校验正则表达式
	 * @date 2016年10月28日 上午10:27:59
	 * @param pattern
	 * @return
	 */
	public static boolean matches(String matchStr, String patternStr){
		Pattern pattern = Pattern.compile(patternStr);
		
		return pattern.matcher(matchStr).matches();
	}
	
	public static void main(String[] args) {  
//		Pattern pattern = Pattern.compile("^[0-9]{4}-(0?[0-9]|1[0-2])-(0?[1-9]|[12]?[0-9]|3[01])$");
//		boolean match = pattern.matcher("2424-12-14").matches();
//		System.out.println(match);
		System.out.println(matches("JB12345", carplatePattern));
		
		String pattern = "(\\d{2}|\\d{4})(?:\\-)?([0]{1}\\d{1}|[1]{1}[0-2]{1})(?:\\-)?([0-2]{1}\\d{1}|[3]{1}[0-1]{1})(?:\\s)?([0-1]{1}\\d{1}|[2]{1}[0-3]{1})(?::)?([0-5]{1}\\d{1})(?::)?([0-5]{1}\\d{1})";
		String s =    "^$|^null$|"+ pattern;
		String a = "^$|^null$|(\\d{2}|\\d{4})(?:\\-)?([0]{1}\\d{1}|[1]{1}[0-2]{1})(?:\\-)?([0-2]{1}\\d{1}|[3]{1}[0-1]{1})(?:\\s)?([0-1]{1}\\d{1}|[2]{1}[0-3]{1})(?::)?([0-5]{1}\\d{1})(?::)?([0-5]{1}\\d{1})";
		String date = "2012-09-12 11:44:33";
		String d = "0";
		
		System.out.println(matches(d, intPattern));
	} 
	
}
