/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：行业统计Action
 *  修改人：liushuai
 *  修改时间：2015-09-23
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.action.backend.statistics;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.action.backend.statistics.model.Collect;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.statistics.IIndustryStatisticsManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 行业统计Action
 * 
 * @author liushuai
 * @version v1.0,2015-09-23
 * @since v4.0
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("industryStatistics")
@Results({
		@Result(name = "order_price", type = "freemarker", location = "/shop/admin/statistics/industry/industrystatistics_price.html"),
		@Result(name = "order_order", type = "freemarker", location = "/shop/admin/statistics/industry/industrystatistics_order.html"),
		@Result(name = "order_goods", type = "freemarker", location = "/shop/admin/statistics/industry/industrystatistics_goods.html"),
		@Result(name = "showpage", type = "freemarker", location = "/shop/admin/statistics/industry/index.html"),
		@Result(name = "showcollect", type = "freemarker", location = "/shop/admin/statistics/industry/collect.html") })
public class IndustryStatisticsAction extends WWAction {

	/**
	 * 自动生成
	 */
	private static final long serialVersionUID = -7223376808957119819L;
	
	/**
	 * manager
	 */
	private IIndustryStatisticsManager industryStatisticsManager;
	private IGoodsCatManager goodsCatManager;
	
	/**
	 * 行业集合
	 */
	private List<Cat> cats;
	/**
	 * 查询分类 1：月/2：年
	 */
	private int type;
	/**
	 * 查询年份
	 */
	private int year;
	/**
	 * 查询月份
	 */
	private int month;
	/**
	 * 查询行业类型id
	 */
	private int cat_id;
	/**
	 * 自定义行业统计结果集合
	 */
	private List<Collect> collects;

	/**
	 * 显示统计主页面
	 * 
	 * @return page
	 */
	public String showPage() {
		return "showpage";
	}

	/**
	 * 显示行业总览界面
	 * 
	 * @return page
	 */
	public String showCollect() {
		cats = this.goodsCatManager.listAllChildren(0);
		
		return "showcollect";
	}

	/**
	 * 总览的表格数据
	 * 
	 * @return
	 */
	public String collectData(){
		cats = this.goodsCatManager.listAllChildren(0);
		// 如果没有选择。那么显示默认第一个
		if (cat_id == 0) {
			cat_id = cats.get(0).getCat_id();
		}
		try {
			collects = industryStatisticsManager.listCollect(cat_id,
					goodsCatManager.listAllChildren(cat_id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.showGridJson(collects);
		return JSON_MESSAGE;

	}
	/**
	 * 
	 */
	
	/**
	 * 获取价格 统计页面
	 * 
	 * @return page
	 */
	public String IndustryPrice() {
		try {
			firstExec();
			List<Map> list = this.industryStatisticsManager.statistics_price(
					type, year, month);
			this.json = JsonMessageUtil.getListJson(list);
			StringBuffer tree = new StringBuffer();
			StringBuffer data = new StringBuffer();
			for (Map map : list) {
				for (Object key : map.keySet()) {
					tree.append("'" + key.toString() + "',");
					data.append("" + map.get(key) + ",");
				}
			}
			ThreadContextHolder.getHttpRequest().setAttribute("tree",
					tree.substring(0, tree.length() - 1));
			ThreadContextHolder.getHttpRequest().setAttribute("data",
					data.substring(0, data.length() - 1));
			if (type == 2) {
				ThreadContextHolder.getHttpRequest().setAttribute("date",
						year + "");
			} else {
				ThreadContextHolder.getHttpRequest().setAttribute("date",
						year + "-" + month);
			}
			this.json = JsonMessageUtil.getListJson(list);

		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取数据出错", e);
			this.showErrorJson("获取数据出错:" + e.getMessage());
		}
		return "order_price";
	}

	/**
	 * 获取商品统计页面
	 * 
	 * @return page
	 */
	public String IndustryGoods() {
		try {
			firstExec();
			List<Map> list = this.industryStatisticsManager.statistics_goods(
					type, year, month);
			this.json = JsonMessageUtil.getListJson(list);
			StringBuffer tree = new StringBuffer();
			StringBuffer data = new StringBuffer();
			for (Map map : list) {
				for (Object key : map.keySet()) {
					tree.append("'" + key.toString() + "',");
					data.append("" + map.get(key) + ",");
				}
			}
			ThreadContextHolder.getHttpRequest().setAttribute("tree",
					tree.substring(0, tree.length() - 1));
			ThreadContextHolder.getHttpRequest().setAttribute("data",
					data.substring(0, data.length() - 1));
			if (type == 2) {
				ThreadContextHolder.getHttpRequest().setAttribute("date",
						year + "");
			} else {
				ThreadContextHolder.getHttpRequest().setAttribute("date",
						year + "-" + month);
			}

		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取数据出错", e);
			this.showErrorJson("获取数据出错:" + e.getMessage());
		}
		return "order_goods";
	}

	/**
	 * 获取下单量统计
	 * 
	 * @return page
	 */
	public String IndustryOrder() {
		try {
			firstExec();
			List<Map> list = this.industryStatisticsManager.statistics_order(
					type, year, month);
			this.json = JsonMessageUtil.getListJson(list);
			StringBuffer tree = new StringBuffer();
			StringBuffer data = new StringBuffer();
			for (Map map : list) {
				for (Object key : map.keySet()) {
					tree.append("'" + key.toString() + "',");
					data.append("" + map.get(key) + ",");
				}
			}
			ThreadContextHolder.getHttpRequest().setAttribute("tree",
					tree.substring(0, tree.length() - 1));
			ThreadContextHolder.getHttpRequest().setAttribute("data",
					data.substring(0, data.length() - 1));
			if (type == 2) {
				ThreadContextHolder.getHttpRequest().setAttribute("date",
						year + "");
			} else {
				ThreadContextHolder.getHttpRequest().setAttribute("date",
						year + "-" + month);
			}

		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取数据出错", e);
			this.showErrorJson("获取数据出错:" + e.getMessage());
		}
		return "order_order";
	}

	/**
	 * 如果第一次执行。没有时间参数的话，我们赋值现在的时间给查询条件
	 */
	private void firstExec() {
		// 如果没有选择类型。那么代表是第一次访问所以没有时间，读取系统时间按照第一种方式查询结果集
		if (type == 0) {
			type = 1;
			Date date = new Date();
			year = date.getYear() + 1900;
			month = date.getMonth() + 1;
		}
	}

	public IIndustryStatisticsManager getIndustryStatisticsManager() {
		return industryStatisticsManager;
	}

	public void setIndustryStatisticsManager(
			IIndustryStatisticsManager industryStatisticsManager) {
		this.industryStatisticsManager = industryStatisticsManager;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

	public List<Cat> getCats() {
		return cats;
	}

	public void setCats(List<Cat> cats) {
		this.cats = cats;
	}

	public int getCat_id() {
		return cat_id;
	}

	public void setCat_id(int cat_id) {
		this.cat_id = cat_id;
	}

	public List<Collect> getCollects() {
		return collects;
	}

	public void setCollects(List<Collect> collects) {
		this.collects = collects;
	}

}
