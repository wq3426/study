/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：流量统计Action
 *  修改人：Sylow
 *  修改时间：2015-10-05
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.action.backend.statistics;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.statistics.IFlowStatisticsManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;


/**
 * 流量统计Action
 * 记录并统计流量详情
 * @author Sylow
 * @version v1.0,2015-10-05
 * @since v4.0
 */
@Component
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("flowStatistics")
@Results({ 
	@Result(name = "flow_statistics", type = "freemarker", location = "/shop/admin/statistics/flow/flow_statistics.html"),
	@Result(name = "goods_flow_statistics", type = "freemarker", location = "/shop/admin/statistics/flow/goods_flow_statistics.html")
})
public class FlowStatisticsAction extends WWAction {
	
	
	/**
	 * serialVersionUID 自动生成
	 */
	private static final long serialVersionUID = 6114082792296425570L;
	
	private IFlowStatisticsManager flowStatisticsManager;

	/**
	 * 开始时间
	 */
	private String start_date;
	
	/**
	 * 结束时间
	 */
	private String end_date;
	
	/**
	 * 排名名次 默认30
	 */
	private int top_num = 30;
	
	/**
	 * 得到总流量统计html 
	 * @return result name
	 */
	public String flowStatisticsHtml(){
		return "flow_statistics";	
	}
	
	/**
	 * 得到总流量统计html 
	 * @return result name
	 */
	public String goodsFlowStatisticsHtml(){
		return "goods_flow_statistics";	
	}
	
	/**
	 * 获取总流量统计数据
	 * @param startDate 开始时间[可为空]
	 * @param endDate	结束时间[可为空]
	 * @param statistics_type 统计类型[可为空]
	 * @return Json格式的字符串 result = 1 代表成功 否则失败
	 */
	public String getFlowStatistics(){
		try {
			HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
			String startDateStamp = "";		//开始时间戳
			String endDateStamp = "";		//结束时间戳
			String statisticsType = request.getParameter("statistics_type");	//统计类型 0=按月统计 1=按年统计
			
			// 如果统计类型为空
			if (statisticsType == null || "".equals(statisticsType)) {
				statisticsType = "1";
			}
			
			// 1.判断并赋值
			if (start_date != null && !"".equals(start_date)) {
				startDateStamp = String.valueOf(DateUtil.getDateline(start_date, "yyyy-MM-dd HH:mm:ss"));
			}
			if (end_date != null && !"".equals(end_date)) {
				endDateStamp = String.valueOf(DateUtil.getDateline(end_date, "yyyy-MM-dd HH:mm:ss"));
			}
			
			// 2.获取数据
			List<Map<String, Object>> list = this.flowStatisticsManager.getFlowStatistics(statisticsType, startDateStamp, endDateStamp);

			this.json = JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取总流量统计出错", e);
			this.showErrorJson("获取总流量统计出错:" + e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 获取商品访问流量统计数据
	 * @param startDate 开始时间[可为空]
	 * @param endDate	结束时间[可为空]
	 * @return Json格式的字符串 result = 1 代表成功 否则失败
	 */
	public String getGoodsFlowStatistics(){
		try {

			String startDateStamp = "";		//开始时间戳
			String endDateStamp = "";		//结束时间戳
			
			// 1.判断并赋值
			if (start_date != null && !"".equals(start_date)) {
				startDateStamp = String.valueOf(DateUtil.getDateline(start_date, "yyyy-MM-dd HH:mm:ss"));
			}
			if (end_date != null && !"".equals(end_date)) {
				endDateStamp = String.valueOf(DateUtil.getDateline(end_date, "yyyy-MM-dd HH:mm:ss"));
			}
			
			// 2.获取数据
			List<Map<String, Object>> list = this.flowStatisticsManager.getGoodsFlowStatistics(top_num, startDateStamp, endDateStamp);

			this.json = JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取总流量统计出错", e);
			this.showErrorJson("获取总流量统计出错:" + e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	
	// getter/setter
	public IFlowStatisticsManager getFlowStatisticsManager() {
		return flowStatisticsManager;
	}

	public void setFlowStatisticsManager(
			IFlowStatisticsManager flowStatisticsManager) {
		this.flowStatisticsManager = flowStatisticsManager;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public int getTop_num() {
		return top_num;
	}

	public void setTop_num(int top_num) {
		this.top_num = top_num;
	}
	
	
}
