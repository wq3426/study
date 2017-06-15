package com.enation.app.shop.core.action.backend.statistics;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.statistics.ISalesStatisticsManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.JsonMessageUtil;

/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：销售统计 Action
 *  修改人：xulipeng
 *  修改时间：2015-09-23
 *  修改内容：制定初版
 *  
 */

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("salesStatis")
@Results({
	@Result(name="order_money", type="freemarker", location="/shop/admin/statistics/sales/order_money.html"),
	@Result(name="order_num", type="freemarker", location="/shop/admin/statistics/sales/order_num.html"),
	@Result(name="sales_statis", type="freemarker", location="/shop/admin/statistics/sales/sales_list.html"),
})
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class SalesStatisticsAction extends WWAction{
	
	private OrderPluginBundle orderPluginBundle;
	private ISalesStatisticsManager salesStatisticsManager;
	private Map<Integer,String> pluginTabs;		//选项卡标题
	private Map<Integer,String> pluginHtmls;	//选项卡内容
	private Integer order_status;		//订单状态
	private Integer cycle_type;			//周期模式	1为月，反之则为年
	private Integer year;				//年
	private Integer month;				//月
	private Map statusMap;				//订单状态的集合
	private String status_Json;			//订单状态的json
	private Double receivables;			//收款金额
	private Double refund;				//退款金额
	private Double paid;				//实收金额
	private Integer order_statis_type;	//下单统计类型，1为下单金额，反之为为下单量
	
	/**
	 * 跳转到订单统计页面
	 * @author xulipeng
	 * @return
	 */
	public String orderStatis(){
		
		
		if(cycle_type==null){
			cycle_type=1;
		}
		if(order_status==null){
			order_status=99;
		}
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		
		if(statusMap==null){
			statusMap = new HashMap();
			statusMap = getStatusJson();
			String p= JSONArray.fromObject(statusMap).toString();
			status_Json=p.replace("[", "").replace("]", "");
		}
		
		if(order_statis_type==null){
			order_statis_type=1;
		}
		
		if(order_statis_type.intValue()==1){
			return "order_money";
		}else{
			return "order_num";
		}
		
		
	}
	
	/**
	 * 获取销售量统计的甘特图json
	 * @author xulipeng
	 * @return
	 */
	public String getSaleNumJson(){
		String message ="[";
		if(cycle_type.intValue()==1){
			List<Map> list= salesStatisticsManager.statisticsMonth_Amount(order_status, year, month);
			message+=getMessage(cycle_type, "t_num", list);
		}else{
			List<Map> list= salesStatisticsManager.statisticsYear_Amount(order_status, year);
			message+=getMessage(cycle_type, "t_num", list);
		}
		message=message.substring(0, message.length()-1)+"]";
		this.json="{\"result\":1,\"message\":"+message+"}";
		return JSON_MESSAGE;
	}
	
	/**
	 * 获取销售金额统计的甘特图json
	 * @author xulipeng
	 * @return
	 */
	public String getSaleMoneyJson(){
		
		String message ="[";
		if(cycle_type.intValue()==1){
			List<Map> list= salesStatisticsManager.statisticsMonth_Amount(order_status, year, month);
			message+=getMessage(cycle_type, "t_money", list);
		}else{
			List<Map> list= salesStatisticsManager.statisticsYear_Amount(order_status, year);
			message+=getMessage(cycle_type, "t_money", list);
		}
		message=message.substring(0, message.length()-1)+"]";
		this.json="{\"result\":1,\"message\":"+message+"}";
		return JSON_MESSAGE;
	}
	
	public static void main(String[] args) {
		
	}
	
	/**
	 * 判断周期模式（按年或者按月），并返回相应的字串
	 * @author xulipeng
	 * @param cycle_type 	周期模式
	 * @param param		t_num：总订单数，t_money：总金额
	 * @param list	数据集合
	 * @return
	 */
	private String getMessage(int cycle_type,String param,List<Map> list){
		int num = 0;
		if(cycle_type==1){
			num=31;
		}else{
			num=12;
		}

		String message = "";
		for (int i = 1; i <= num; i++) {
			boolean flag = true;
			for (int j =0;j<list.size();j++) {
				Map map = list.get(j);
				if(!map.get("month").toString().equals("0") && i==Integer.parseInt(map.get("month").toString())){
					message = message+map.get(param).toString()+",";
					flag = false;
				}
			}
			if(flag){
				message = message+"0,";
			}
		}
		return message;
	}
	
	
	/**
	 * 销售收入统计
	 * @author xulipeng
	 * @return
	 */
	public String saleIncome(){
		year = 2015;
		month = 05;
		receivables =  this.salesStatisticsManager.getReceivables(year, month, null);
		refund = this.salesStatisticsManager.getRefund(year, month, null);
		paid = CurrencyUtil.sub(receivables, refund);
		return "sales_statis";
	}
	
	/**
	 * 销售收入统计json数据
	 * @return
	 */
	public String saleIncomeJson(){
		
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		
		List list = this.salesStatisticsManager.getSalesIncome(year, month, this.getPage(), this.getPageSize(), null);
		this.showGridJson(list);
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 销售收入统计总览json
	 * @return
	 */
	public String saleIncomeTotleJson(){
		receivables =  this.salesStatisticsManager.getReceivables(year, month, null);
		refund = this.salesStatisticsManager.getRefund(year, month, null);
		paid = CurrencyUtil.sub(receivables, refund);
		
		Map map = new HashMap();
		map.put("receivables", receivables);
		map.put("refund", refund);
		map.put("paid", paid);
		
		this.json = JsonMessageUtil.getObjectJson(map);
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 订单状态集合
	 * @author xulipeng
	 * @return 
	 */
	private Map getStatusJson(){
		Map orderStatus = new  HashMap();
		
		orderStatus.put(""+OrderStatus.ORDER_NOT_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_PAY));
		orderStatus.put(""+OrderStatus.ORDER_PAY_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY_CONFIRM));
//		orderStatus.put(""+OrderStatus.ORDER_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SHIP));
//		orderStatus.put(""+OrderStatus.ORDER_ROG, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ROG));
		orderStatus.put(""+OrderStatus.ORDER_SERVECE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SERVECE));
		orderStatus.put(""+OrderStatus.ORDER_APPRAISE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_APPRAISE));
		orderStatus.put(""+OrderStatus.ORDER_CANCEL_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_SHIP));
		orderStatus.put(""+OrderStatus.ORDER_COMPLETE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_COMPLETE));
		orderStatus.put(""+OrderStatus.ORDER_CANCEL_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_PAY));
		orderStatus.put(""+OrderStatus.ORDER_CANCELLATION, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCELLATION));
		orderStatus.put(""+OrderStatus.ORDER_CHANGED, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGED));
		orderStatus.put(""+OrderStatus.ORDER_CHANGE_APPLY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGE_APPLY));
		orderStatus.put(""+OrderStatus.ORDER_RETURN_APPLY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_RETURN_APPLY));
		
		//暂停使用的订单状态
		//orderStatus.put(""+OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));
		//orderStatus.put(""+OrderStatus.ORDER_NOT_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_CONFIRM)); 
		//orderStatus.put(""+OrderStatus.ORDER_ALLOCATION_YES, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ALLOCATION_YES));
		return orderStatus;
	}
	
	
	
	// set get
	
	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}

	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}

	public Map<Integer, String> getPluginTabs() {
		return pluginTabs;
	}

	public void setPluginTabs(Map<Integer, String> pluginTabs) {
		this.pluginTabs = pluginTabs;
	}

	public Map<Integer, String> getPluginHtmls() {
		return pluginHtmls;
	}

	public void setPluginHtmls(Map<Integer, String> pluginHtmls) {
		this.pluginHtmls = pluginHtmls;
	}

	public Integer getOrder_status() {
		return order_status;
	}

	public void setOrder_status(Integer order_status) {
		this.order_status = order_status;
	}

	public Integer getCycle_type() {
		return cycle_type;
	}

	public void setCycle_type(Integer cycle_type) {
		this.cycle_type = cycle_type;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}


	public Map getStatusMap() {
		return statusMap;
	}

	public void setStatusMap(Map statusMap) {
		this.statusMap = statusMap;
	}

	public String getStatus_Json() {
		return status_Json;
	}

	public void setStatus_Json(String status_Json) {
		this.status_Json = status_Json;
	}

	public ISalesStatisticsManager getSalesStatisticsManager() {
		return salesStatisticsManager;
	}

	public void setSalesStatisticsManager(
			ISalesStatisticsManager salesStatisticsManager) {
		this.salesStatisticsManager = salesStatisticsManager;
	}

	public Double getReceivables() {
		return receivables;
	}

	public void setReceivables(Double receivables) {
		this.receivables = receivables;
	}

	public Double getRefund() {
		return refund;
	}

	public void setRefund(Double refund) {
		this.refund = refund;
	}

	public Double getPaid() {
		return paid;
	}

	public void setPaid(Double paid) {
		this.paid = paid;
	}

	public Integer getOrder_statis_type() {
		return order_statis_type;
	}

	public void setOrder_statis_type(Integer order_statis_type) {
		this.order_statis_type = order_statis_type;
	}
	
}
