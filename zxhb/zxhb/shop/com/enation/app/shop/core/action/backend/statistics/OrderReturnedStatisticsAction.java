/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：会员统计Action
 *  修改人：Kanon
 *  修改时间：2015-09-23
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.action.backend.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.statistics.IReturnedStatisticsManager;
import com.enation.framework.action.WWAction;
/**
 * 退款统计Action
 * @author kanon
 * @version v1.0,2015-09-23
 * @since v4.0
 *
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("orderReturnedStatistics")
@Results({
	@Result(name="list",type="freemarker",  location="/shop/admin/statistics/sales/tuikuan.html")
})
public class OrderReturnedStatisticsAction extends WWAction{
	
	private Integer cycle_type;			//周期模式	1为月，反之则为年
	private Integer year;				//年
	private Integer month;				//月
	private IReturnedStatisticsManager returnedStatisticsManager;
	
	/**
	 * 获取退款统计列表
	 * @author kanon
	 * @param cycle_type 周期模式
	 * @param year 年
	 * @param month 月
	 * @return 退款统计列表页面
	 */
	public String returnedStatistics(){
		Map map  = new HashMap();
		map.put("cycle_type", cycle_type);
		map.put("year", year);
		map.put("month", month);
		return "list";
	}
	
	/**
	 * 获取退款统计列表JSON列表
	 * @author kanon
	 * @param cycle_type 周期模式 
	 * @param year 年
	 * @param month 月
	 * @return 退款统计列表JSON列表
	 */
	public String returnedStatisticsJson(){
		String message ="[";
		//如果月的周期模式 
		if(cycle_type.intValue()==1){
			List<Map> list= returnedStatisticsManager.statisticsMonth_Amount( year, month);
			message+=getMessage(cycle_type, "t_money", list);
		}else{
		//如果年的周期模式
			List<Map> list= returnedStatisticsManager.statisticsYear_Amount( year);
			message+=getMessage(cycle_type, "t_money", list);
		}
		message=message.substring(0, message.length()-1)+"]";
		this.json="{\"result\":1,\"message\":"+message+"}";
		return JSON_MESSAGE;
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

	public IReturnedStatisticsManager getReturnedStatisticsManager() {
		return returnedStatisticsManager;
	}

	public void setReturnedStatisticsManager(
			IReturnedStatisticsManager returnedStatisticsManager) {
		this.returnedStatisticsManager = returnedStatisticsManager;
	}
}
