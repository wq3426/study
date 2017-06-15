package com.enation.app.shop.core.action.backend.statistics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.statistics.IGoodsStatisticsManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：商品分析 Action
 *  修改人：xulipeng
 *  修改时间：2015-09-27
 *  修改内容：制定初版
 *  
 */

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("goodsStatis")
@Results({
	@Result(name="price_sales", type="freemarker", location="/shop/admin/statistics/goodsanalysis/price_sales_list.html"),
	@Result(name="hot_goods_money", type="freemarker", location="/shop/admin/statistics/goodsanalysis/hot_goods_money.html"),
	@Result(name="hot_goods_num", type="freemarker", location="/shop/admin/statistics/goodsanalysis/hot_goods_num.html"),
	@Result(name="goods_sales_detail", type="freemarker", location="/shop/admin/statistics/goodsanalysis/goods_sales_detail.html"),
})
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class GoodsStatisticsAction extends WWAction {

	private IGoodsStatisticsManager goodsStatisticsManager;
	private IGoodsCatManager goodsCatManager;
	private Integer cat_id;
	private Integer cycle_type; 	//周期模式 1为月，反之则为年
	private Integer statis_type;		//统计模式  1为下单金额，反之为下单量
	private Integer year;
	private Integer month;
	private String money_json;
	private String num_json;
	private String name;
	
	
	/**
	 * 跳转价格销量展示页
	 * @return
	 */
	public String priceSales(){
		return "price_sales";
	}
	
	/**
	 * 读取价格销量统计的json
	 * @return
	 */
	public String getPriceSalesJson(){

		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		String minprices = request.getParameter("minprice");		//最小价格的数据
		String maxprices = request.getParameter("maxprice");		//最大价格的数据
		
		String [] minp = minprices.split(",");
		String [] maxp = maxprices.split(",");
		
		//把数据存放在list
		List list = new ArrayList();
		for(int i =0;i<minp.length;i++){
			Map map = new HashMap();
			if(!minp[i].equals("")){
				map.put("minprice", minp[i]);
				map.put("maxprice", maxp[i]);
				list.add(map);
			}
		}
		
		if(cycle_type==null){
			cycle_type=1;
		}
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		
		//当cat_id为空时，为其赋值0，目的是查询全部分类！ 修改人:DMRain 2015-12-07
		if(cat_id == null){
			cat_id = 0;
		}
		
		List data_list = new ArrayList();
		if(cycle_type.intValue()==1){
			
			int day = getDaysByYearMonth(year, month);
			long start_time = DateUtil.getDateline(year+"-"+month+"-01 00:00:00");
			long end_time = DateUtil.getDateline(year+"-"+month+"-"+day+" 23:59:59");
			//System.out.println(start_time+"___"+end_time);
			data_list = this.goodsStatisticsManager.getPriceSalesList(start_time, end_time, cat_id, list, null);
			
		}else{
			
			long start_time = DateUtil.getDateline(year+"-01-01 00:00:00");
			long end_time = DateUtil.getDateline(year+"-12-31 23:59:59");  
			data_list = this.goodsStatisticsManager.getPriceSalesList(start_time, end_time, cat_id, list, null);
		}
		
		this.json = JsonMessageUtil.getListJson(data_list);
		
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 跳转热卖商品页
	 * @return
	 */
	public String hotgoods(){
		
		if(cycle_type==null){
			cycle_type=1;
		}
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		if(cat_id==null){
			cat_id=0;
		}
		
		if(statis_type==null || statis_type==1){	//下单金额
			
			Page moneyPage = getHotGoodsMoneyList(cycle_type, year, month, cat_id, null);
			List<Map> moneyList = (List) moneyPage.getResult();
			List money_list = new ArrayList();
			for (Map map : moneyList) {
				Map money_map = new HashMap();
				money_map.put("name", map.get("oiname"));
				money_map.put("y", map.get("t_money"));
				money_list.add(money_map);
			}
			
			money_json = JSONArray.fromObject(money_list).toString();
			return "hot_goods_money";
		}else{
			
			Page numPage = getHotGoodsNumList(cycle_type, year, month, cat_id, null);
			List<Map> numList = (List) numPage.getResult();
			List num_list = new ArrayList();
			for (Map map : numList) {
				Map money_map = new HashMap();
				money_map.put("name", map.get("oiname"));
				money_map.put("y", map.get("t_num"));
				num_list.add(money_map);
			}
			
			num_json = JSONArray.fromObject(num_list).toString();
			return "hot_goods_num";
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getHotGoodsMoneyJson(){
		if(cycle_type==null){
			cycle_type=1;
		}
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		Page moneyPage= this.getHotGoodsMoneyList(cycle_type, year, month, cat_id, null);
		this.showGridJson(moneyPage);
		return JSON_MESSAGE;
	}
	
	public String getHotGoodsNumJson(){
		
		if(cycle_type==null){
			cycle_type=1;
		}
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		Page numPage =  this.getHotGoodsNumList(cycle_type, year, month, cat_id, null);
		this.showGridJson(numPage);
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 热卖商品金额top list
	 * @param cycle_type
	 * @param year
	 * @param month
	 * @param cat_id
	 * @param map
	 * @return
	 */
	public Page getHotGoodsMoneyList(Integer cycle_type, int year,int month,Integer cat_id,Map map){
		List moneyList = new ArrayList();
		Page moneyPage = new Page();
		
		this.rows = 50;
		
		if(cycle_type.intValue()==1){
			
			int day = getDaysByYearMonth(year, month);
			long start_time = DateUtil.getDateline(year+"-"+month+"-01 00:00:00");
			long end_time = DateUtil.getDateline(year+"-"+month+"-"+day+" 23:59:59");
			moneyPage = this.goodsStatisticsManager.getHotGoodsMoney(start_time, end_time, this.getPage(), this.getPageSize(), cat_id, map);
			
		}else{
			
			long start_time = DateUtil.getDateline(year+"-01-01 00:00:00");
			long end_time = DateUtil.getDateline(year+"-12-31 23:59:59");  
			
			moneyPage = this.goodsStatisticsManager.getHotGoodsMoney(start_time, end_time, this.getPage(), this.getPageSize(), cat_id, map);
		}
		return moneyPage;
	}
	
	/**
	 * 热卖商品数量top list
	 * @param cycle_type
	 * @param year
	 * @param month
	 * @param cat_id
	 * @param map
	 * @return
	 */
	public Page getHotGoodsNumList(Integer cycle_type, int year,int month,Integer cat_id,Map map){
		List numList = new ArrayList();
		Page numPage = new Page();
		
		this.rows = 50;
		if(cycle_type.intValue()==1){
			
			int day = getDaysByYearMonth(year, month);
			long start_time = DateUtil.getDateline(year+"-"+month+"-01 00:00:00");
			long end_time = DateUtil.getDateline(year+"-"+month+"-"+day+" 23:59:59");
			numPage = this.goodsStatisticsManager.getHotGoodsNum(start_time, end_time, this.getPage(), this.getPageSize(), cat_id, map);
			
		}else{
			
			long start_time = DateUtil.getDateline(year+"-01-01 00:00:00");
			long end_time = DateUtil.getDateline(year+"-12-31 23:59:59");  
			
			numPage = this.goodsStatisticsManager.getHotGoodsNum(start_time, end_time, this.getPage(), this.getPageSize(), cat_id, map);
		}
		return numPage;
	}
	
	/**
	 * 商品销售明细
	 * @return
	 */
	public String goodsSalesDetail(){
		if(cycle_type==null){
			cycle_type=1;
		}
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		return "goods_sales_detail";
	}
	
	/**
	 * 商品销售明细的json
	 * @return
	 */
	public String goodsSalesDetailJson(){
		if(cycle_type==null){
			cycle_type=1;
		}
		Calendar cal = Calendar.getInstance();
		if(year==null){
			year = cal.get(Calendar.YEAR);
		}
		if(month==null){
			month = cal.get(Calendar.MONTH )+1;
		}
		
		Page webpage = new Page();
		
		if(cycle_type.intValue()==1){
			
			int day = getDaysByYearMonth(year, month);
			long start_time = DateUtil.getDateline(year+"-"+month+"-01 00:00:00");
			long end_time = DateUtil.getDateline(year+"-"+month+"-"+day+" 23:59:59");
			webpage = this.goodsStatisticsManager.getgoodsSalesDetail(start_time, end_time, this.getPage(), this.getPageSize(), cat_id, name, null);
			
		}else{
			
			long start_time = DateUtil.getDateline(year+"-01-01 00:00:00");
			long end_time = DateUtil.getDateline(year+"-12-31 23:59:59");  
			webpage = this.goodsStatisticsManager.getgoodsSalesDetail(start_time, end_time, this.getPage(), this.getPageSize(), cat_id, name, null);
		}
		this.showGridJson(webpage);
		
		return JSON_MESSAGE;
	}
	

	//获取当前年月的最大的天数
	public int getDaysByYearMonth(int year, int month) {  
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, year);  
        a.set(Calendar.MONTH, month - 1);  
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
    }
	
	//set get 
	
	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

	public Integer getCat_id() {
		return cat_id;
	}

	public void setCat_id(Integer cat_id) {
		this.cat_id = cat_id;
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

	public IGoodsStatisticsManager getGoodsStatisticsManager() {
		return goodsStatisticsManager;
	}

	public void setGoodsStatisticsManager(
			IGoodsStatisticsManager goodsStatisticsManager) {
		this.goodsStatisticsManager = goodsStatisticsManager;
	}

	public Integer getStatis_type() {
		return statis_type;
	}

	public void setStatis_type(Integer statis_type) {
		this.statis_type = statis_type;
	}

	public String getMoney_json() {
		return money_json;
	}

	public void setMoney_json(String money_json) {
		this.money_json = money_json;
	}

	public String getNum_json() {
		return num_json;
	}

	public void setNum_json(String num_json) {
		this.num_json = num_json;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
