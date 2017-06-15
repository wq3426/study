/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：团购统计Action
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
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.statistics.IGroupbuyStatisticsManager;
import com.enation.framework.action.WWAction;
/**
 * 团购统计Action
 * @author Kanon
 * @version v1.0,2015-09-23
 * @since v4.0
 */
@Component
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("groupbuyStatistics")
@Results({
	@Result(name="list",type="freemarker",  location="/shop/admin/statistics/sales/groupbuy.html")
})
public class GroupbuyStatisticsAction extends WWAction{
	private String keyword;
	private IGroupbuyStatisticsManager groupbuyStatisticsManager;
	
	public String groupbuyStatisticsList(){
		return "list";
	}
	
	public String groupbuyStatisticsListJson(){
		this.webpage=groupbuyStatisticsManager.groupbuyStatisticsList(keyword,  this.getPage(),this.getPageSize());
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public IGroupbuyStatisticsManager getGroupbuyStatisticsManager() {
		return groupbuyStatisticsManager;
	}

	public void setGroupbuyStatisticsManager(
			IGroupbuyStatisticsManager groupbuyStatisticsManager) {
		this.groupbuyStatisticsManager = groupbuyStatisticsManager;
	}
}
