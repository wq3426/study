package com.enation.eop.sdk.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MathUtils {

	public static String RoundHalfUpGetBigInteger(BigDecimal number) {
		return number.setScale(0, BigDecimal.ROUND_HALF_UP).toBigInteger()
				.toString();
	}

	public static String RoundHalfUp2Scale(BigDecimal number) {
		return number.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}

	public static String RoundDown3Scale(BigDecimal number) {
		return number.setScale(3, BigDecimal.ROUND_DOWN).toString();
	}

	public static String RoundDown2Scale(BigDecimal number) {
		return number.setScale(2, BigDecimal.ROUND_DOWN).toString();
	}

	public static String RoundHalfUpScale(BigDecimal number) {
		return number.setScale(0, BigDecimal.ROUND_HALF_UP).toString();
	}

	public static String RoundUpScale(BigDecimal number) {
		return number.setScale(0, BigDecimal.ROUND_UP).toString();
	}

	public static boolean isPositiveInteger(String str) {
		Pattern pattern = Pattern.compile("^\\d+$");
		return pattern.matcher(str).matches();
	}

	public static boolean is2ScaleNumber(String numStr) {
		Pattern pattern = Pattern
				.compile("^[+]?(([1-9]\\d*[.]?)|(0.))(\\d{0,2})?$");
		Matcher matcher = pattern.matcher(numStr);
		return matcher.matches();
	}

	public static boolean is3ScaleNumber(String numStr) {
		Pattern pattern = Pattern
				.compile("^[+]?(([1-9]\\d*[.]?)|(0.))(\\d{0,3})?$");
		Matcher matcher = pattern.matcher(numStr);
		return matcher.matches();
	}

	/**
	 * 检查字符是否为数字
	 * 
	 * @param numStr
	 * @return true 为数字,false非数字
	 */
	public static boolean isNumber(String numStr) {
		Pattern pattern = Pattern.compile("^(\\d+\\.)?\\d+$");
		Matcher matcher = pattern.matcher(numStr);
		return matcher.matches();
	}

	public static BigDecimal getUpBigInteger(BigDecimal b) {
		return new BigDecimal(new BigDecimal(Math.ceil(b.doubleValue()))
				.toBigInteger().toString());
	}

	public static BigDecimal max(String a, String b) {
		System.out.println(StringUtil.null2Zero(a));
		System.out.println(StringUtil.null2Zero(b));
		double max = Math.max(Double.parseDouble(StringUtil.null2Zero(a)),
				Double.parseDouble(StringUtil.null2Zero(b)));
		return new BigDecimal(max + "");
	}

	public static BigDecimal min(String a, String b) {
		double min = Math.min(Double.parseDouble(a), Double.parseDouble(b));
		return new BigDecimal(min + "");
	}

	public static String decimalFormat(String a) {
		BigDecimal rateBig = new BigDecimal(a);
		DecimalFormat decimalFormat = new DecimalFormat(
				"###,###,###,###.##");
		return decimalFormat.format(rateBig).toString();
	}

	/**
	 * 功能：元转换为分
	 * 
	 * @param amount
	 * @return
	 */
	public static String yuanToFen(String amount) {
		if (ValidateUtils.isEmpty(amount)) {
			return amount;
		}
		BigDecimal tempAmount = new BigDecimal(amount);
		tempAmount = tempAmount.multiply(new BigDecimal("100"));
		return tempAmount.toBigInteger().toString();
	}

	/**
	 * 功能：分转换为元
	 * 
	 * @param amount
	 * @return
	 */
	public static String fenToYuan(String amount) {
		if (ValidateUtils.isEmpty(amount)) {
			return amount;
		}
		BigDecimal tempAmount = new BigDecimal(amount);
		tempAmount = tempAmount.divide(new BigDecimal("100")).setScale(2,
				BigDecimal.ROUND_DOWN);
		return tempAmount.toString();
	}
	
	/**
	 * 功能：万转换为元
	 * 
	 * @param amount
	 * @return
	 */
	public static String wanToYuan(String amount) {
		if (ValidateUtils.isEmpty(amount)) {
			return amount;
		}
		BigDecimal tempAmount = new BigDecimal(amount);
		tempAmount = tempAmount.multiply(new BigDecimal("10000"));
		return tempAmount.toBigInteger().toString();
	}

	/**
	 * 功能：元转换为万
	 * 
	 * @param amount
	 * @return
	 */
	public static String yuanToWan(String amount, int scale) {
		if (ValidateUtils.isEmpty(amount)) {
			return amount;
		}
		BigDecimal tempAmount = new BigDecimal(amount);
		tempAmount = tempAmount.divide(new BigDecimal("10000")).setScale(scale, BigDecimal.ROUND_DOWN);
		return tempAmount.toString();
	}

	/**
	 * 功能：两个字符串金额相加
	 * 
	 * @param amount
	 * @return
	 */
	public static String calcAdd(String amount1, String amount2) {
		BigDecimal b1 = new BigDecimal(amount1);
		BigDecimal b2 = new BigDecimal(amount2);
		BigDecimal result = new BigDecimal("0");
		result = b1.add(b2);
		return result.toString();
	}

	/**
	 * 功能：两个字符串金额相减
	 * 
	 * @param amount
	 * @return
	 */
	public static String calcSub(String amount1, String amount2) {
		BigDecimal b1 = new BigDecimal(amount1);
		BigDecimal b2 = new BigDecimal(amount2);
		BigDecimal result = new BigDecimal("0");
		result = b1.subtract(b2);
		return result.toString();
	}

	/**
	 * 功能：乘法
	 * 
	 * @param amount
	 * @return
	 */
	public static String calcMultiplication(String amount, String quotiety) {
		// 校验非空
		if (StringUtil.isNull(amount) || StringUtil.isNull(quotiety)) {
			throw new RuntimeException("参数不能为空，格式错误！");
		}
		// 校验基数
		boolean isQuotiety = quotiety
				.matches("^(-){0,1}\\d+((\\.{0})|(\\.{1}[0-9]{1,}))$");
		if (!isQuotiety) {
			throw new RuntimeException("乘法系数，格式错误！");
		}
		// 校验系数
		boolean isAmount = amount.matches("^\\d+((\\.{0})|(\\.{1}[0-9]{1,}))$");
		if (!isAmount) {
			throw new RuntimeException("金额参数格式错误！");
		}
		return new BigDecimal(amount).multiply(new BigDecimal(quotiety))
				.toString();
	}

	/**
	 * 乘法（可指定精度）
	 * 
	 * @param amount
	 *            金额1
	 * @param quotiety
	 *            金额2
	 * @param scale
	 *            保留小数几位
	 * @param mode
	 *            保留精度取舍模式
	 * @return 按照指定精度返回，指定小数位的乘积.
	 */
	public static String calcMultiplication(String amount, String quotiety,
			int scale, RoundingEnum mode) {
		// 校验非空
		if (StringUtil.isNull(amount) || StringUtil.isNull(quotiety)) {
			throw new RuntimeException("参数不能为空，格式错误！");
		}
		// 校验基数
		boolean isQuotiety = quotiety
				.matches("^(-){0,1}\\d+((\\.{0})|(\\.{1}[0-9]{1,}))$");
		if (!isQuotiety) {
			throw new RuntimeException("乘法系数，格式错误！");
		}
		// 校验系数
		boolean isAmount = amount.matches("^\\d+((\\.{0})|(\\.{1}[0-9]{1,}))$");
		if (!isAmount) {
			throw new RuntimeException("金额参数格式错误！");
		}

		return new BigDecimal(amount).multiply(new BigDecimal(quotiety))
				.setScale(scale, mode.getRoundingMode()).toString();
	}

	public enum RoundingEnum {
		将精度位之后的近位("向上进位 例：100.123 -》100.13", "0",RoundingMode.UP), 
		将精度位之后的舍去("向下进位 例：100.123 -》100.12 -》100.13", "1",RoundingMode.DOWN), 
		将精度位之后的五舍六入("向下进位 例：100.123 -》100.12 100.125 -》100.12 100.127 -》100.13",	"5",RoundingMode.HALF_DOWN), 
		将精度位之后的四舍五入(" 向下进位 例：100.123 -》100.12 100.125 -》100.13 100.127 -》100.13","4",RoundingMode.HALF_UP);

		private String value;
		private String name;
		private RoundingMode roundingMode;

		private RoundingEnum(String name, String value,RoundingMode mode) {
			this.name = name;
			this.value = value;
			this.roundingMode = mode;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public RoundingMode getRoundingMode() {
			return roundingMode;
		}

		public void setRoundingMode(RoundingMode roundingMode) {
			this.roundingMode = roundingMode;
		}
		
	}

	/**
	 * 功能：除法
	 * 
	 * @param amount
	 * @return
	 */
	public static String calcDivide(String amount, String quotiety, int scale,
			RoundingEnum mode) {
		// 校验非空
		if (StringUtil.isNull(amount) || StringUtil.isNull(quotiety)) {
			throw new RuntimeException("参数不能为空，格式错误！");
		}
		// 校验基数
		boolean isQuotiety = quotiety
				.matches("^(-){0,1}\\d+((\\.{0})|(\\.{1}[0-9]{1,}))$");
		if (!isQuotiety) {
			throw new RuntimeException("乘法系数，格式错误！");
		}
		// 校验系数
		boolean isAmount = amount.matches("^\\d+((\\.{0})|(\\.{1}[0-9]{1,}))$");
		if (!isAmount) {
			throw new RuntimeException("金额参数格式错误！");
		}
		
		return new BigDecimal(amount).divide(new BigDecimal(quotiety), scale,mode.getRoundingMode()).toString();
	}

	/**
	 * 功能：除法
	 * 
	 * @param amount
	 * @return
	 */
	public static String calcDivide(String amount, String quotiety) {
		// 校验非空
		if (StringUtil.isNull(amount) || StringUtil.isNull(quotiety)) {
			throw new RuntimeException("参数不能为空，格式错误！");
		}
		// 校验基数
		boolean isQuotiety = quotiety
				.matches("^(-){0,1}\\d+((\\.{0})|(\\.{1}[0-9]{1,}))$");
		if (!isQuotiety) {
			throw new RuntimeException("乘法系数，格式错误！");
		}
		// 校验系数
		boolean isAmount = amount.matches("^\\d+((\\.{0})|(\\.{1}[0-9]{1,}))$");
		if (!isAmount) {
			throw new RuntimeException("金额参数格式错误！");
		}
		return new BigDecimal(amount).divide(new BigDecimal(quotiety))
				.toString();
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。舍入模式采用用户指定舍入模式
	 * 
	 * @param v1
	 * @param v2
	 * @param scale
	 *            表示需要精确到小数点以后几位
	 * @param round_mode
	 *            表示用户指定的舍入模式
	 * @return 两个参数的商，以字符串格式返回
	 */
	public static String calcDivide(String v1, String v2, int scale,
			int round_mode) {
		if (scale < 0) {
			throw new IllegalArgumentException("指定精确到小数点值不能小于0");
		}
		BigDecimal b1 = new BigDecimal(v1);
		BigDecimal b2 = new BigDecimal(v2);
		return b1.divide(b2, scale, round_mode).toString();
	}
	
	
	
	
	/**
	 * 比较第一个值，是否比第二个值大。
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean firstBig(String str1, String str2){
		BigDecimal b1 = new BigDecimal(str1);
		BigDecimal b2 = new BigDecimal(str2);
		return b1.compareTo(b2)>0?true:false;
	}
	
	
	/**
	 * 比较第一个值，是否大于或等于第二个值。
	 * @param str1
	 * @param str2
	 * @author jun.liu
	 * @return
	 */
	public static boolean firstBigOrEquals(String str1, String str2){
		BigDecimal b1 = new BigDecimal(str1);
		BigDecimal b2 = new BigDecimal(str2);
		return b1.compareTo(b2)>=0?true:false;
	}
	
	
	
	/**
	 * 比较第一个值，是否小于或等于第二个值。
	 * @param str1
	 * @param str2
	 * @author jun.liu
	 * @return
	 */
	public static boolean firstSmallOrEquals(String str1, String str2){
		BigDecimal b1 = new BigDecimal(str1);
		BigDecimal b2 = new BigDecimal(str2);
		return b1.compareTo(b2)<=0?true:false;
	}
	

	
	/**
	 * 取出三个值中最大值
	 * @param str1
	 * @param str2
	 * @param str3
	 * @author jun.liu
	 * @return
	 */
	public static String maxValue(String a, String b, String c){
		BigDecimal b1 = new BigDecimal(a);
		BigDecimal b2 = new BigDecimal(b);
		BigDecimal b3 = new BigDecimal(c);
		
		BigDecimal result = null;
		result = b1.compareTo(b2)>=0?b1:b2;
		result = result.compareTo(b3)>=0?result:b3;
		return result.toString();
	}

	/**
	 * 去掉小数部分代表精度的无效‘0’ 例如3.300 -> 3.3
	 * @param amount
	 * @return
	 */
	public static String trimScale0(String amount) { 
		if (amount.indexOf(".") != -1 && amount.charAt(amount.length() - 1) == '0') { 
			return trimScale0(amount.substring(0, amount.length() - 1)); 
		} else { 
			return amount.charAt(amount.length() - 1) == '.' ? amount.substring(0, amount.length() - 1) : amount; 
		} 
	}
	
	
	
	/**
	 * 第一个值是否大于等于第二个值
	 * @param first
	 * @param second
	 * @return
	 */
	public static boolean daYuDengYu(String first, String second){
		BigDecimal bd1 = new BigDecimal(first);
		BigDecimal bd2 = new BigDecimal(second);
		
		return bd1.compareTo(bd2)>=0?true:false;
	}
	
	
	/**
	 * 第一个值是否大于第二个值
	 * @param first
	 * @param second
	 * @return
	 */
	public static boolean daYu(String first, String second){
		BigDecimal bd1 = new BigDecimal(first);
		BigDecimal bd2 = new BigDecimal(second);
		
		return bd1.compareTo(bd2)>0?true:false;
	}
	
	
	
	/**
	 * 第一个值是否小于等于第二个值
	 * @param first
	 * @param second
	 * @return
	 */
	public static boolean xiaoYuDengYu(String first, String second){
		BigDecimal bd1 = new BigDecimal(first);
		BigDecimal bd2 = new BigDecimal(second);
		
		return bd1.compareTo(bd2)<=0?true:false;
	}
	
	
	/**
	 * 第一个值是否小于第二个值
	 * @param first
	 * @param second
	 * @return
	 */
	public static boolean xiaoYu(String first, String second){
		BigDecimal bd1 = new BigDecimal(first);
		BigDecimal bd2 = new BigDecimal(second);
		
		return bd1.compareTo(bd2)<0?true:false;
	}
	
	
	/**
	 * 第一个值是否等于第二个值
	 * @param first
	 * @param second
	 * @return
	 */
	public static boolean dengYu(String first, String second){
		BigDecimal bd1 = new BigDecimal(first);
		BigDecimal bd2 = new BigDecimal(second);
		
		return bd1.compareTo(bd2)==0?true:false;
	}
	
	
	
	/**
	 * @funcDesc：将金额反转（正变负， 负变正）
	 * @developer：jun.liu
	 * @date：2015年6月10日 下午12:06:31
	 * @param amount
	 * @return
	 * String
	 */
	public static String reverseAmount(String amount){
		boolean isZero = isZero(amount);
		if(isZero){
			return amount;
		}
		
		if(daYu(amount, BigDecimal.ZERO.toString())){
			return "-"+amount;
		}else{
			return amount.replaceAll("-", "");
		}
	}
	
	
	
	public static void main(String[] args) {
		System.out.println(yuanToFen(""));
	}
	
	
	/**
	 * @funcDesc：是否为0
	 * @developer：jun.liu
	 * @date：2015年6月10日 上午11:27:27
	 * @param str
	 * @return
	 * boolean
	 */
	public static boolean isZero(String amount){
		BigDecimal decimal = new BigDecimal(amount);
		return decimal.compareTo(new BigDecimal(0))==0?true:false;
	}
	

	/**
	 * 功能：是否不为0
	 * @param str
	 * @return
	 */
	public static boolean isNotZero(String amount){
		BigDecimal decimal = new BigDecimal(amount);
		return decimal.compareTo(new BigDecimal(0))==0?false:true;
	}
}
