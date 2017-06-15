/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：会员统计Action
 *  修改人：Sylow
 *  修改时间：2015-09-23
 *  修改内容：制定初版
 */
package com.enation.app.shop.core.action.backend.statistics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.statistics.IMemberStatisticsManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 会员统计Action
 * 
 * @author Sylow
 * @version v1.0,2015-09-23
 * @since v4.0
 */
@Component
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("memberStatistics")
@Results({ 
	@Result(name = "member_analysis", type = "freemarker", location = "/shop/admin/statistics/member/member_analysis.html"),
	@Result(name = "order_num_statistics", type = "freemarker", location = "/shop/admin/statistics/member/order_num_statistics.html"),
	@Result(name = "goods_num_statistics", type = "freemarker", location = "/shop/admin/statistics/member/goods_num_statistics.html"),
	@Result(name = "order_price_statistics", type = "freemarker", location = "/shop/admin/statistics/member/order_price_statistics.html"),
	@Result(name = "buy_analysis", type = "freemarker", location = "/shop/admin/statistics/member/buy_analysis.html"),
	@Result(name = "add_member", type = "freemarker", location = "/shop/admin/statistics/member/add_member.html")
	
})
public class MemberStatisticsAction extends WWAction {

	private IMemberStatisticsManager memberStatisticsManager;

	/**
	 * serialVersionUID 自动生成
	 */
	private static final long serialVersionUID = 8343728100814743621L;

	/**
	 * 排名名次 默认15
	 */
	private Integer top_num = 15;
	
	/**
	 * 开始时间
	 */
	private String start_date;
	
	/**
	 * 结束时间
	 */
	private String end_date;
	
	/**
	 * 上月开始时间
	 */
	private String lastStart_date;
	
	/**
	 * 上月结束时间
	 */
	private String lastEnd_date;
	
	/**
	 * 搜索类型  如果是2，按年搜索，
	 */
	private Integer type;
	
	/**
	 * 区间数组
	 */
	private Integer[] sections;

	/**
	 * 获得会员分析页
	 * 
	 * @return result name
	 */
	public String memberAnalysisHtml() {
		return "member_analysis";
	}
	
	/**
	 * 获取下单量统计页
	 * @return result name
	 */
	public String orderNumStatisticsHtml(){
		return "order_num_statistics";
	}
	
	/**
	 * 获取下单商品件数统计页
	 * @return result name
	 */
	public String goodsNumStatisticsHtml(){
		
		return "goods_num_statistics";
	}
	

	/**
	 * 获取下单金额统计页
	 * @return result name
	 */
	public String orderPriceStatisticsHtml(){
		
		return "order_price_statistics";
	}

	
	/**
	 * 获取购买分析html页
	 * @return result name
	 */
	public String buyAnalysisHtml(){
		return "buy_analysis";
	}
	
	/**
	 * 获得新增会员
	 * @return result name
	 */
	public String addMemberNumHtml(){
		
		return "add_member";
	}
	
	/**
	 * 获取会员下单排行
	 * 
	 * @author Sylow
	 * @param top_num
	 *            排名名次 <b>必填</b>
	 * @param start_date 开始时间 可为空
	 * @param start_date 结束时间 可为空
	 * @return Json格式的字符串 result = 1 代表成功 否则失败
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getOrderNumTop() {
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
			
			Map map = new HashMap();
			map.put("start_date", startDateStamp);
			
			// 2.获取数据
			List<Map<String, Object>> list = this.memberStatisticsManager.getOrderNumTop(top_num, startDateStamp, endDateStamp);

			this.json = JsonMessageUtil.getListJson(list);

		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取用户下单排行出错", e);
			this.showErrorJson("获取用户下单排行出错:" + e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 获取会员下单商品排行
	 * 
	 * @author Sylow
	 * @param top_num
	 *            排名名次 <b>必填</b>
	 * @param start_date 开始时间 可为空
	 * @param start_date 结束时间 可为空
	 * @return Json格式的字符串 result = 1 代表成功 否者失败
	 */
	public String getGoodsNumTop() {
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
			List<Map<String, Object>> list = this.memberStatisticsManager.getGoodsNumTop(top_num, startDateStamp, endDateStamp);

			this.json = JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取用户下单商品数量排行出错", e);
			this.showErrorJson("获取用户下单商品数量排行出错:" + e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 获取会员下单总金额排行
	 * 
	 * @author Sylow
	 * @param top_num
	 *            排名名次 <b>必填</b>
	 * @param start_date 开始时间 可为空
	 * @param start_date 结束时间 可为空
	 * @return Json格式的字符串 result = 1 代表成功 否者失败
	 */
	public String getOrderPriceTop() {
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
			List<Map<String, Object>> list = this.memberStatisticsManager.getOrderPriceTop(top_num, startDateStamp, endDateStamp);
			this.json = JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取用户下单总额排行出错", e);
			this.showErrorJson("获取用户下单总额排行出错:" + e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 获取客单价分布数据
	 * 
	 * @author Sylow
	 * @param start_date 开始时间 可为空
	 * @param start_date 结束时间 可为空
	 * @return Json格式的字符串 result = 1 代表成功 否者失败
	 */
	public String getOrderPriceDis() {
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
			List<Map<String, Object>> list = this.memberStatisticsManager.getOrderPriceDis(Arrays.asList(sections), startDateStamp, endDateStamp);
			this.json = JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			this.logger.error("获取用户客单价分布数据出错", e);
			this.showErrorJson("获取用户客单价分布数据出错:" + e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 获取用户购买频次数据
	 * @author Sylow
	 * @param start_date 开始时间 可为空
	 * @param start_date 结束时间 可为空
	 * @return Json格式的字符串 result = 1 代表成功 否者失败
	 */
	public String getBuyFre() {
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
			List<Map<String, Object>> list = this.memberStatisticsManager.getBuyFre(startDateStamp, endDateStamp);
			this.json = JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取用户购买频次出错", e);
			this.showErrorJson("获取用户购买频次出错:" + e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 获取用户购买时段分布数据
	 * @author Sylow
	 * @param start_date 开始时间 可为空
	 * @param start_date 结束时间 可为空
	 * @return Json格式的字符串 result = 1 代表成功 否者失败
	 */
	public String getBuyTimeDis() {
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
			List<Map<String, Object>> list = this.memberStatisticsManager.getBuyTimeDis(startDateStamp, endDateStamp);
			this.json = JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取用户购买时段分布出错", e);
			this.showErrorJson("获取用户购买时段分布出错:" + e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}


	/**
	 * 获得新增会员
	 * 
	 * @author whj
	 * @return Json格式的字符串 result = 1 代表成功 否则失败
	 */
	public String getAddMemberNum() {
		try {
			
			String startDateStamp = "";		//本月开始时间戳
			String endDateStamp = "";		//本月结束时间戳
			String lastStartDateStamp = "";		//本月开始时间戳
			String lastEndDateStamp = "";		//本月结束时间戳
			
			// 1.判断并赋值
			if (start_date != null && !"".equals(start_date)) {
				startDateStamp = String.valueOf(DateUtil.getDateline(start_date));
			}
			if (end_date != null && !"".equals(end_date)) {
				endDateStamp = String.valueOf(DateUtil.getDateline(end_date));
			}
			
			if (lastStart_date != null && !"".equals(lastStart_date)) {
				lastStartDateStamp = String.valueOf(DateUtil.getDateline(lastStart_date));
			}
			if (lastEnd_date != null && !"".equals(lastEnd_date)) {
				lastEndDateStamp = String.valueOf(DateUtil.getDateline(lastEnd_date));
			}
			
			if(type==2){
				// 2.获取本月数据
				List<Map<String, Object>> list = this.memberStatisticsManager.getAddYearMemberNum(startDateStamp, endDateStamp);

				// 3.获取上月数据
				List<Map<String, Object>> lastList = this.memberStatisticsManager.getLastAddYearMemberNum(lastStartDateStamp, lastEndDateStamp);
				//4.放到map中
				Map result = new HashMap();
				result.put("list", list);
				result.put("lastList", lastList);
				this.json = JsonMessageUtil.getObjectJson(result);
			}else{
				// 2.获取本月数据
				List<Map<String, Object>> list = this.memberStatisticsManager.getAddMemberNum(startDateStamp, endDateStamp);

				// 3.获取上月数据
				List<Map<String, Object>> lastList = this.memberStatisticsManager.getAddMemberNum(lastStartDateStamp, lastEndDateStamp);
				//4.放到map中
				Map result = new HashMap();
				result.put("list", list);
				result.put("lastList", lastList);
				this.json = JsonMessageUtil.getObjectJson(result);
			}
			
			

		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取用户数据出错", e);
			this.showErrorJson("获取用户数据出错:" + e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	// getter setter
	public IMemberStatisticsManager getMemberStatisticsManager() {
		return memberStatisticsManager;
	}

	public void setMemberStatisticsManager(
			IMemberStatisticsManager memberStatisticsManager) {
		this.memberStatisticsManager = memberStatisticsManager;
	}

	public Integer getTop_num() {
		return top_num;
	}

	public void setTop_num(Integer top_num) {
		this.top_num = top_num;
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

	public Integer[] getSections() {
		return sections;
	}

	public void setSections(Integer[] sections) {
		this.sections = sections;
	}

	public String getLastStart_date() {
		return lastStart_date;
	}

	public void setLastStart_date(String lastStart_date) {
		this.lastStart_date = lastStart_date;
	}

	public String getLastEnd_date() {
		return lastEnd_date;
	}

	public void setLastEnd_date(String lastEnd_date) {
		this.lastEnd_date = lastEnd_date;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
