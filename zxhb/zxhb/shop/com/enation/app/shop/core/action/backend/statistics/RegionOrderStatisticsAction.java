/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：区域统计Action
 *  修改人：Kanon
 *  修改时间：2015-09-23
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.action.backend.statistics;


import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.statistics.IRegionStatisticsManager;
import com.enation.framework.action.WWAction;
/**
 * 区域统计Action
 * @author kanon
 * @version v1.0
 * @since v1.0
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("regionOrderStatistics")
@Results({
	@Result(name="list",type="freemarker",  location="/shop/admin/statistics/sales/quyu.html")
})
public class RegionOrderStatisticsAction extends WWAction{
	
	private String data;
	private Integer type;
	private String sort;
	private Integer cycle_type;			//周期模式	1为月，反之则为年
	private Integer year;				//年
	private Integer month;				//月
	private IRegionStatisticsManager regionStatisticsManager;
	
	/**
	 * 区域分析页面
	 * @return 区域分析页面
	 */
	public String regionList(){

		return  "list";  
	}
	
	/**
	 * 区域分析JSON
	 * @param type 1.下单会员数、2.下单量、3.下单金额
	 * @param cycle_type 周期模式	1为月，反之则为年
	 * @param year 年
	 * @param month 月
	 * @param data 区域分析JSON
	 * @return 区域分析JSON
	 */
	public String regionTypeListJson(){
		try {
			data=regionStatisticsManager.getRegionStatistics(type,cycle_type,year,month);
			this.json= "{\"message\":"+data+"}";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 获取区域分析列表JSON
	 * @param type 1.下单会员数、2.下单量、3.下单金额
	 * @param sort 排序方式,正序、倒序
	 * @return 区域分析列表JSON
	 */
	public String regionListJson(){
		try {
			this.showGridJson(regionStatisticsManager.regionStatisticsList(type, " desc ",cycle_type,year,month));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.JSON_MESSAGE;
	}
	
	//get set
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public IRegionStatisticsManager getRegionStatisticsManager() {
		return regionStatisticsManager;
	}
	public void setRegionStatisticsManager(
			IRegionStatisticsManager regionStatisticsManager) {
		this.regionStatisticsManager = regionStatisticsManager;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
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
}
