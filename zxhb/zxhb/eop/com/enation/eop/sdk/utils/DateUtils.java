package com.enation.eop.sdk.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class DateUtils {
	
	public static final String _YYYYMMDDHHMMSSSS_STR="yyyyMMddHHmmssSS";
	public static final String _YYYYMMDDHHMMSS_STR="yyyyMMddHHmmss";
	public static final String _YYYYMMDD_STR="yyyyMMdd";
	public static final String _YYYYMM_STR="yyyyMM";
	public static final String _YYYY_STR="yyyy";
	
	public static final String YYYYMMDDHHMMSS_STR1="yyyy-MM-dd HH:mm:ss";
	public static final String _YYYYMMDD_STR1="yyyy-MM-dd";
	
	public static final String _DEFAULT_FROMAT="yyyyMMdd";//默认时间格式
	
	public static final int _DEFAULT_DAYS = 0;

	public static String countDate(String sdate, int days, String dateFormat)
			throws ParseException {
		if(ValidateUtils.isEmpty(sdate) || ValidateUtils.isEmpty(dateFormat)){
			//不处理直接返回
			return sdate ;
		}
		// 1.转换日期格式 (string -> java.util.date)
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = sdf.parse(sdate);

		// 2.设置日历时间
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		cal.add(Calendar.DAY_OF_MONTH, days);// 取当前日期的前几天

		// 通过格式化输出日期
		return sdf.format(cal.getTime());
	}

	/**
	 * 判断时间是否过期
	 * 
	 * @param srcTime
	 *            : 源时间
	 * @param days
	 *            : 几天之后
	 * @return
	 */
	public static boolean isOverdue(String srcTime, int days) {
		
		if(ValidateUtils.isEmpty(srcTime)){
			return false;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar cd = Calendar.getInstance();

		try {
			cd.setTime(sdf.parse(srcTime));
		} catch (ParseException e) {
			e.getStackTrace();
		}
		cd.add(Calendar.DATE, days);

		String endate = sdf.format(cd.getTime());
		System.out.println(" limit: " + sdf.format(cd.getTime()));

		Date now = new Date();
		String today = sdf.format(now);

		int _today = Integer.parseInt(today);
		int _endate = Integer.parseInt(endate);
		if (_today >= _endate) {
			System.out.println("已到期");
			return true;
		} else {
			System.out.println("还没到期");
			return false;
		}
	}

	/**
	 * 计算日期差
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int countDaysBetween(Calendar d1, Calendar d2) {
		if (d1.after(d2)) { // swap dates so that d1 is start and d2 is end
			java.util.Calendar swap = d1;
			d1 = d2;
			d2 = swap;
		}
		int days = d2.get(java.util.Calendar.DAY_OF_YEAR)
				- d1.get(java.util.Calendar.DAY_OF_YEAR);
		int y2 = d2.get(java.util.Calendar.YEAR);
		if (d1.get(java.util.Calendar.YEAR) != y2) {
			d1 = (java.util.Calendar) d1.clone();
			do {
				days += d1.getActualMaximum(java.util.Calendar.DAY_OF_YEAR);
				d1.add(java.util.Calendar.YEAR, 1);
			} while (d1.get(java.util.Calendar.YEAR) != y2);
		}
		return days;
	}
	
	public static long dateDiff(String startTime, String endTime, String format) {
		// 按照传入的格式生成一个simpledateformate对象
		SimpleDateFormat sd = new SimpleDateFormat(format);
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数
		long diff;
		long day=0;
		try {
			// 获得两个时间的毫秒时间差异
			diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
			day = diff / nd;// 计算差多少天
			
		} catch (ParseException e) {
			e.getStackTrace();
		}
		return day;
	}
	
	/**
	 * 计算日期差
	 * @param d1
	 * @param d2
	 * @return
	 * @throws ParseException 
	 */
	public static int countDaysBetween(String sdate, String edate, String dateFormat) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date dateStart = sdf.parse(sdate);
		Date dateEnd = sdf.parse(edate);
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		startCal.setTime(dateStart);
		endCal.setTime(dateEnd);
		return countDaysBetween(startCal, endCal);
	}
	
	/**
	 * 得到当前日期字符串yyyyMMdd
	 * 
	 * @return String
	 */

	public static String getNowDate() {
		Date nowTime = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
		String sysDate = time.format(nowTime);
		return sysDate;
	}
	
	/**
	 * 得到当前时间字符串HHmmss
	 * 
	 * @return String
	 */
	
	public static String getNowOnlyTime() {
		String nowTime = getNowTime();
		return nowTime.substring(8);
	}
	
	
	/**
	 * 得到当前时间字符串yyyyMMddHHmmss
	 * 
	 * @return String
	 */
	public static String getNowTime() {
		Date nowTime = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
		String sysDate = time.format(nowTime);
		return sysDate;
	}
	/**
	 * 得到当前时间字符串yyyyMM
	 * 
	 * @return String
	 */
	public static String getNowMouth() {
		return getNowMouth("yyyyMM");
	}
	public static String getNowMouth(String pattern) {
		Date nowTime = new Date();
		SimpleDateFormat time = new SimpleDateFormat(pattern);
		String sysDate = time.format(nowTime);
		return sysDate;
	}
	/**
	 * 得到当前时间字符串yyyy
	 * 
	 * @return String
	 */
	public static String getNowYeer() {
		Date nowTime = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyyy");
		String sysDate = time.format(nowTime);
		return sysDate;
	}
	/**
	 * 得到当前时间字符串yyyyMMddHHmmssSS 毫秒级别
	 * 
	 * @return String
	 */
	public static String getNowTimeInMillis() {
		Date nowTime = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmssSS");
		String sysDate = time.format(nowTime);
		return sysDate;
	}
	
	
	private static Integer maxValue=999;
	
	private static Integer initializeValue = 100;
	
	/**
	 * 返回格式为"yyyyMMddHHmmssSS"的唯一当前时间
	 * @throws InterruptedException 
	 */
	public synchronized static String uniqueTime(){
		
		
		//此处睡眠1毫秒无意义，方法加了同步锁
		//同时进入该方法的线程只有一个,
		//建议去除此处睡眠线程代码块
//		try {
//			Thread.sleep(1);
//		} catch (InterruptedException e) {
//			LogWapper.error(businessNameEnums, e);
//		}
		SimpleDateFormat uniqueTimeFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
		Date date = new Date();
		String time = uniqueTimeFormat.format(date)+initializeValue.toString();
		if(initializeValue.intValue()==maxValue.intValue()){
			initializeValue=100;
		}else{
			initializeValue++;
		}
		return time;
	}
	
	public static boolean isRegularDate(String str_input,String rDateFormat){
		if (str_input!=null && !"".equals(str_input)) {
		         SimpleDateFormat formatter = new SimpleDateFormat(rDateFormat);
		         formatter.setLenient(false);
		         try {
		             formatter.format(formatter.parse(str_input));
		         } catch (Exception e) {
		             return false;
		         }
		         return true;
		}
		else{
			return false;
		}
		

	}
	
	public static boolean isDate(String str_input,String rDateFormat){

		if(ValidateUtils.isEmpty(str_input) || ValidateUtils.isEmpty(rDateFormat)){
			return false;
		}
		String datePattern1 = "";
		if("yyyyMMdd".equals(rDateFormat)){
			datePattern1 = "\\d{4}\\d{2}\\d{2}";
		}
		else if("yyyy-MM-dd".equals(rDateFormat)){
			datePattern1 = "\\d{4}-\\d{2}-\\d{2}";
		}
		else{
			datePattern1 = "\\d{4}\\d{2}\\d{2}";
		}
	     String datePattern2 = "^((\\d{2}(([02468][048])|([13579][26]))"
	             + "[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|"
	             + "(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?"
	             + "((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?("
	             + "(((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?"
	             + "((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
	     if ((str_input != null)) {
	         Pattern pattern = Pattern.compile(datePattern1);
	         Matcher match = pattern.matcher(str_input);
	         if (match.matches()) {
	             pattern = Pattern.compile(datePattern2);
	             match = pattern.matcher(str_input);
	             return match.matches();
	         }
	         else {
	             return false;
	         }
	     }
	     return false;
		

	}
	
	public static String getDateStr(String dateStr) throws RuntimeException{
		try {
			if(ValidateUtils.isEmpty(dateStr)){
				return dateStr;
			}
			SimpleDateFormat sdf_s = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdf_d = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf_s.parse(dateStr);
			// 通过格式化输出日期
			return sdf_d.format(date);
		} catch (Exception e) {
			e.getStackTrace();
			throw new RuntimeException("日期格式化失败！");
		}
	}
	/**
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static String getDateStrOther(String dateStr) throws ParseException {
		if(ValidateUtils.isEmpty(dateStr)){
			return dateStr;
		}
		SimpleDateFormat sdf_s = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf_d = new SimpleDateFormat("yyyyMMdd");
		Date date = sdf_s.parse(dateStr);
		// 通过格式化输出日期
		return sdf_d.format(date);
	}
	public static String getMouthStr(String dateStr) throws ParseException {
		if(ValidateUtils.isEmpty(dateStr)){
			return dateStr;
		}
		SimpleDateFormat sdf_s = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat sdf_d = new SimpleDateFormat("yyyy-MM");
		Date date = sdf_s.parse(dateStr);
		// 通过格式化输出日期
		return sdf_d.format(date);
	}
	public static String getTimeStr(String dateStr) throws ParseException {
		if(ValidateUtils.isEmpty(dateStr)){
			return dateStr;
		}
		SimpleDateFormat sdf_s = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf_d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = sdf_s.parse(dateStr);
		// 通过格式化输出日期
		return sdf_d.format(date);
	}
	public static String getTime14Str(String dateStr) throws ParseException {
		if(ValidateUtils.isEmpty(dateStr)){
			return dateStr;
		}
		SimpleDateFormat sdf_s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf_d = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = sdf_s.parse(dateStr);
		// 通过格式化输出日期
		return sdf_d.format(date);
	}
	
	
	
	/**
	 * 功能描述：time转时间Date
	 * @developer：jun.liu
	 * @date：2015年11月13日 下午8:57:38
	 * @param time
	 * @return
	 * String
	 */
	public static String getTimeToDate(String time) {
		try {
			SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date date = timeFormat.parse(time);
			return dateFormat.format(date);
		} catch (ParseException e) {
			e.getStackTrace();
		}
		return "";
	}
	
	
	
	
	/**
	 * 功能：将YYYY-MM-dd格式转化为YYYYMMDD格式
	 * @param dateStr 待转换的日期字符串
	 * @return	已YYYYMMDD格式返回
	 * @throws ParseException 
	 */
	public static String dateFormat(String dateStr) throws ParseException{
		if(ValidateUtils.isEmpty(dateStr)){
			return dateStr;
		}
		SimpleDateFormat   from=new   SimpleDateFormat( "yyyy-MM-dd"); 
    	SimpleDateFormat   to=new   SimpleDateFormat( "yyyyMMdd"); 
    	return to.format(from.parse(dateStr));
	}
	
	/**
	 * 功能：将YYYY-MM格式转化为YYYYMM格式
	 * @param dateStr 待转换的日期字符串
	 * @return	已YYYYMMDD格式返回
	 * @throws ParseException 
	 */
	public static String mouthFormat(String dateStr) throws ParseException{
		if(ValidateUtils.isEmpty(dateStr)){
			return dateStr;
		}
		SimpleDateFormat   from=new   SimpleDateFormat( "yyyy-MM"); 
    	SimpleDateFormat   to=new   SimpleDateFormat( "yyyyMM"); 
    	return to.format(from.parse(dateStr));
	}
	
	/**
	 * @return 返回："yyyy-MM-dd hh:mm:ss"格式的当前时间
	 */
	public static String getCurrentTime(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	
	
	/**
	 * @return 返回："yyyyMMddHHmmss"格式的当前时间
	 */
	public static String getCurrentTimeNotSlipt(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(date);
	}
	/**
	 * 获取当前时间几天后的时间
	 * @param data
	 * @return
	 */
	public static String getDateAdd(int data){
		java.util.Calendar Cal=java.util.Calendar.getInstance();
		DateFormat df=new SimpleDateFormat("yyyyMMdd");


		Cal.add(Calendar.DATE, data); //
		
		return df.format(Cal.getTime());
	}
	
	
	
	
	
	/**
	 * 获取当前时间几天前的日期
	 * @param data
	 * @return
	 */
	public static String getDateSub(int data){
		java.util.Calendar Cal=java.util.Calendar.getInstance();
		DateFormat df=new SimpleDateFormat("yyyyMMdd");


		Cal.add(Calendar.DATE, data); //
		
		return df.format(Cal.getTime());
	}

	/**
	 * 计算两个月之间的差值
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
   public static int getMonthDiffer(String beginTime,String endTime){
	   int difMonth =0;
       int beginYear = Integer.parseInt(beginTime.substring(0,4));
       int beginMonth = Integer.parseInt(beginTime.replace("-", "").substring(4,6));

       int endYear = Integer.parseInt(endTime.substring(0,4));
       int endMonth = Integer.parseInt(endTime.replace("-", "").substring(4,6));
	   difMonth = (endYear-beginYear)*12+(endMonth-beginMonth);
       return difMonth;
   }
   
   /**
    * 取得当前日期的前一个月
    * @return
    */
   public static String getBeforeMonth(){

	    //设置日期格式
	    SimpleDateFormat df = new SimpleDateFormat("yyyyMM"); 
	    //取得上月时间
	    java.util.Calendar   calendar=java.util.Calendar.getInstance();  
	    calendar.setTime(new java.util.Date());
	    //设置为上个月
	    calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH)-1);
	    return df.format(calendar.getTime());
   }

   /**
    * 取得当前日期的前N个月
    * @return
    */
   public static String getBeforeMonth(int month){

	    //设置日期格式
	    SimpleDateFormat df = new SimpleDateFormat("yyyyMM"); 
	    //取得上月时间
	    java.util.Calendar   calendar=java.util.Calendar.getInstance();  
	    calendar.setTime(new java.util.Date());
	    //设置为N个月
	    calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH)-month);
	    return df.format(calendar.getTime());
   }
   
   public static String getMonthAdd(String sdate,int month) throws ParseException{
	    //设置日期格式
	    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd"); 
		Date date = df.parse(sdate);
		// 2.设置日历时间
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		cal.add(Calendar.MONTH, -month);// 取当前日期的前几天
		return df.format(cal.getTime());
  }
   

	
	
	public static int bettwenDay (String date1,String date2) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		    long to = df.parse(date1).getTime();
		    long from = df.parse(date2).getTime();
		    return (int) ((to - from) / (1000 * 60 * 60 * 24));
		} catch (Exception e) {
			e.getStackTrace();
			throw new RuntimeException("");
		}
	}
	
	
	public static String bettwenMonth (String maxDate,String minDate) {
		try {
			int days = bettwenDay(maxDate,minDate);
			int month = days/30;
			return month+"";
		} catch (Exception e) {
			e.getStackTrace();
			throw new RuntimeException("");
		}
	}
	
	
	
	
	
	/**
	 * 获得当前日期之前或之后第几个月的日期（格式：yyyyMMdd）
	 * @param flag INT型整数，-1代表之前，1代表之后。
	 * @return
	 */
	public static String getMonth(int flag){
		Calendar cal =Calendar.getInstance();
        cal.add(Calendar.MONTH, flag);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String targetDate = sdf.format(cal.getTime());
        return targetDate;
	}
	
	
	
	/**
	 * 获得当前日期之前或之后第几天的日期（格式：yyyyMMdd）
	 * @param flag INT型整数，-1代表之前，1代表之后。
	 * @return
	 */
	public static String getDate(int flag){
		Calendar cal =Calendar.getInstance();
        cal.add(Calendar.DATE, flag);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String targetDate = sdf.format(cal.getTime());
        return targetDate;
	}
	
	
	
	/**
	 * 获得当前日期之前或之后第几周的日期（格式：yyyyMMdd）
	 * @param flag INT型整数，-1代表之前，1代表之后。
	 * @return
	 */
	public static String getWeek(int flag){
		Calendar cal =Calendar.getInstance();
        cal.add(Calendar.WEDNESDAY, flag);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String targetDate = sdf.format(cal.getTime());
        return targetDate;
	}
	
	
	
	
	/**
	 * 得到当前月份，格式：9 或 10
	 * 
	 * @return String
	 */
	public static String getCurrentMouth() {
		Calendar cal =Calendar.getInstance();
		cal.setTime(new Date());
		String currentMonth = cal.get(Calendar.MONTH)+1+"";
		return currentMonth;
	}
	
	
	
	/**
	 * @return 返回："yyyyMMddHHmmss"格式的当前时间
	 */
	public synchronized static String getCurrentTimeMode1(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(date);
	}
	
	
	/**
	 * 得到当前日期字符串yyyy-MM-dd
	 * 
	 * @return String
	 */
	public static String getNowDateFmt() {
		Date nowTime = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd");
		String sysDate = time.format(nowTime);
		return sysDate;
	}
	
	/**
	 * 得到当前日期字符串，用指定的分隔符来划分 如：分隔符为"/" 则结果为 yyyy/MM/dd 
	 * 
	 * @return String
	 */
	public static String getNowDateFmtByFenGeFu(String fenGeFu) {
		Date nowTime = new Date();
		SimpleDateFormat time = new SimpleDateFormat("yyyy"+ fenGeFu +"MM"+ fenGeFu +"dd");
		String sysDate = time.format(nowTime);
		return sysDate;
	}
	
	/**
	 * 获得当前日期之前或之后第几个月的日期（格式：yyyy-MM-dd）
	 * @param value INT型整数，-1代表之前，1代表之后。
	 * @return
	 */
	public static String getDateByMonthValueFormat(int value){
		Calendar cal =Calendar.getInstance();
        cal.add(Calendar.MONTH, value);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String targetDate = sdf.format(cal.getTime());
        return targetDate;
	}
	
	
	
	
	
	/**
	 * 获得当前日期之前或之后第几个月的日期（格式：yyyyMMdd）
	 * @param value INT型整数，-1代表之前，1代表之后。
	 * @return
	 */
	public static String getDateByMonthValue(int value){
		Calendar cal =Calendar.getInstance();
        cal.add(Calendar.MONTH, value);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String targetDate = sdf.format(cal.getTime());
        return targetDate;
	}
	
	
	/**
	 * @funcDesc：比较两个日期
	 * @developer：jun.liu
	 * @date：2015年7月6日 下午4:02:47
	 * @param date1
	 * @param date2
	 * @return 小于返回-1 等于返回0 大于返回 1
	 * int
	 */
	public static int compareDate(String date1,String date2){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date dateOne;
		try {
			dateOne = sdf.parse(date1);
			Date dateTwo = sdf.parse(date2);
			return dateOne.compareTo(dateTwo);
		} catch (ParseException e) {
			e.getStackTrace();
			throw new RuntimeException("日期格式转换错误");
		}
	}
	
	

	/**
	 * @funcDesc：比较两个时间
	 * @developer：jun.liu
	 * @date：2015年7月6日 下午4:02:47
	 * @param time1
	 * @param time2
	 * @return 小于返回-1 等于返回0 大于返回 1
	 * int
	 */
	public static int compareTime(String time1,String time2){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date dateOne;
		try {
			dateOne = sdf.parse(time1);
			Date dateTwo = sdf.parse(time2);
			return dateOne.compareTo(dateTwo);
		} catch (ParseException e) {
			e.getStackTrace();
			throw new RuntimeException("时间格式转换错误");
		}
	}
	
	
	
	/**
	 * @funcDesc：获得当前小时，格式：24小时制
	 * @developer：jun.liu
	 * @date：2015年11月11日 下午12:29:04
	 * @return
	 * String
	 */
	public static String getCurrentHour() {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    return calendar.get(Calendar.HOUR_OF_DAY)+"";
	}

	
	
	/**
	 * 功能描述：格式化日期：yyyy-mm-dd
	 * @developer：jun.liu
	 * @date：2015年11月19日 下午3:57:40
	 * @param date
	 * @return
	 * String
	 */
	public static String formatDate(String date){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date temp = sdf.parse(date);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(temp);
		} catch (ParseException e) {
			e.getStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 功能描述：格式化时间：yyyy-MM-dd HH:mm:ss
	 * @developer：jun.liu
	 * @date：2015年11月19日 下午3:57:40
	 * @param date
	 * @return
	 * String
	 */
	public static String formatTime(String time){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			Date temp = sdf.parse(time);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return format.format(temp);
		} catch (ParseException e) {
			e.getStackTrace();
		}
		return null;
	}



	/**
	 * 功能描述：获取之前或之后秒，的时间
	 * @developer：jun.liu
	 * @date：2015年12月10日 上午11:58:40
	 * @param sec -1代表之前 0 代表现在 1代表之后
	 * @return
	 * String
	 */
	public static String getTimeBySecond(int sec) {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.add(Calendar.SECOND, sec);
	    return getTimeByDate(calendar.getTime());
	}
	
	/**
	 * 功能描述：根据日期获取时间（yyyyMMddHHmmss）
	 * @developer：jun.liu
	 * @date：2015年12月10日 上午11:58:18
	 * @param date
	 * @return
	 * String
	 */
	public static String getTimeByDate(Date date){
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return timeFormat.format(date);
	}
	
	public static void main(String[] args) {
		System.out.println(getTimeBySecond(60));
	}
	
	
	
	/**
	 * 
	 * 功能描述：根据两个日期参数（入参不分顺序）获取日期列表
	 * @developer：庞皓
	 * @date：2016年5月13日 下午6:29:21
	 * @param firstDate
	 * @param secondDate
	 * @return
	 * @throws ParseException
	 * List<String>
	 */
	public static List<String> calculateDatesBetweenFirstDateAndSecondDate(String firstDate,String secondDate) throws ParseException{
		List<String> returnList = new ArrayList<>();
        boolean isReversedDate = MathUtils.daYu(firstDate, secondDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");  
        Date dateTypeBegin = simpleDateFormat.parse(isReversedDate == true ? secondDate : firstDate);  
        Date dateTypeEnd = simpleDateFormat.parse(isReversedDate == true ? firstDate : secondDate);  
        List<Date> dateList = DateUtils.handleCalculateDatesBetweenDateTypeBeginAndDateTypeEnd(dateTypeBegin, dateTypeEnd);  
        for (Date date : dateList) {  
        	returnList.add(simpleDateFormat.format(date));
        }  
        return returnList;
	}

	/**
	 * 
	 * 功能描述：计算两个日期之间的每个日期
	 * @developer：庞皓
	 * @date：2016年5月13日 下午6:28:50
	 * @param dateTypeBegin
	 * @param dateTypeEnd
	 * @return
	 * List<Date>
	 */
    private static List<Date> handleCalculateDatesBetweenDateTypeBeginAndDateTypeEnd(Date dateTypeBegin, Date dateTypeEnd) {  
        List<Date> listDate = new ArrayList<Date>();  
        listDate.add(dateTypeBegin);  
        Calendar calendarBegin = Calendar.getInstance();  
        // 使用给定的 Date 设置此 Calendar 的时间    
        calendarBegin.setTime(dateTypeBegin);  
        Calendar calendarEnd = Calendar.getInstance();  
        // 使用给定的 Date 设置此 Calendar 的时间    
        calendarEnd.setTime(dateTypeEnd);  
        // 测试此日期是否在指定日期之后    
        while (dateTypeEnd.after(calendarBegin.getTime())) {  
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量    
            calendarBegin.add(Calendar.DAY_OF_MONTH, 1);  
            listDate.add(calendarBegin.getTime());  
        }  
        return listDate;  
    }  

    
    /**
     * 
     * 功能描述：计算某一日期(yyyyMMdd)前后N天的日期
     * @developer：庞皓
     * @date：2016年5月19日 下午6:20:02
     * @param date 日期
     * @param operator 天数（int）：如1、-3
     * @return
     * String
     */
    public static String calculateDateAddDays(String date,int operator){ 
    	Calendar calendar = Calendar.getInstance(); 
    	Date thisDate=null; 
    	try { 
    		thisDate = new SimpleDateFormat("yyyy-MM-dd").parse(date); 
    	} catch (ParseException e) { 
    		e.getStackTrace(); 
    	} 
    	calendar.setTime(thisDate); 
    	int day=calendar.get(Calendar.DATE); 
    	calendar.set(Calendar.DATE,day+operator); 
    	String dayAfter=new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()); 
    	return dayAfter; 
    } 
    
    
    
    /**
     * 功能描述：获得日期类型
     * @developer：jun.liu
     * @date：2016年5月31日 下午3:25:02
     * @param stringDate
     * @return
     * Date
     */
    public static Date getDateByStringDate(String stringDate){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	try {
			return sdf.parse(stringDate);
		} catch (ParseException e) {
			e.getStackTrace(); 
			return null;
		}
    }
	
    /**
	 * 功能描述：计算两个时间，相差秒数
	 * @developer：jun.liu
	 * @date：2016年5月11日 下午4:58:03
	 * @param firstTime
	 * @param secondTime
	 * @return
	 * @throws ParseException
	 * String
	 */
	public static String calcChaSecByTime(String firstTime, String secondTime) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date1 = sdf.parse(firstTime);
			Date date2 = sdf.parse(secondTime);
			long sec = (date1.getTime() - date2.getTime())/1000;
			return sec+"";
		} catch (Exception e) {
			e.getStackTrace(); 
		}
		return "";
	}
}
