package com.enation.app.b2b2c.core.tag.order;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.DateUtil;

import freemarker.template.TemplateModelException;
import net.sf.json.JSONObject;
/**
 * 获取店铺订单<br>
 * 是获取卖家的订单
 * @author LiFenLong
 *
 */
@Component
public class RepairOrderTag extends BaseFreeMarkerTag{
	private IStoreOrderManager storeOrderManager;
	private IStoreMemberManager storeMemberManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		//session中获取会员信息,判断用户是否登陆
		StoreMember member=storeMemberManager.getStoreMember();
		if(member==null){
			HttpServletResponse response= ThreadContextHolder.getHttpResponse();
			try {
				response.sendRedirect("login.html");
			} catch (IOException e) {
				throw new UrlNotFoundException();
			}
		}
		//根据订单号获取保险订单信息
		String order_id =  (Integer) params.get("order_id")+"";
		
		Map result = new HashMap();
		JSONObject repairInfo = storeOrderManager.getRepairOrderDetail(order_id);
		
		result.put("spec_id", repairInfo.get("spec_id"));
		result.put("orderRepairItems", repairInfo.get("orderRepairItems"));
		result.put("storeRepairItems", repairInfo.get("storeRepairItems"));
		result.put("items", repairInfo.get("items"));
		result.put("repair_mile", repairInfo.get("repair_mile"));
		result.put("repair_price", repairInfo.get("repair_price"));
		result.put("repair_source", repairInfo.get("repair_source"));
		result.put("service_timelength", repairInfo.get("service_timelength"));
		result.put("engineer", repairInfo.get("engineer"));
		result.put("repair_time", DateUtil.toString("".equals(repairInfo.get("repair_time")) ? 0 : repairInfo.getLong("repair_time"), "yyyy-MM-dd HH:mm:ss"));
		result.put("repair_remarks", repairInfo.get("repair_remarks"));
		result.put("order_date", DateUtil.toString("".equals(repairInfo.get("order_date")) ? 0 : repairInfo.getLong("order_date"), "yyyy-MM-dd"));
		result.put("starttime", repairInfo.get("starttime"));
		result.put("endtime", repairInfo.get("endtime"));
		result.put("brand", repairInfo.get("brand"));
		result.put("series", repairInfo.get("series"));
		result.put("nk", repairInfo.get("nk"));
		result.put("discharge", repairInfo.get("discharge"));
		result.put("repair_total_price", repairInfo.get("repair_total_price"));
		
		return result;
	}
	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}
	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
