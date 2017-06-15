package com.enation.app.b2b2c.core.tag.member;

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

import freemarker.template.TemplateModelException;
import net.sf.json.JSONObject;

/**
 * @Description 保养订单记录查询
 *
 * @createTime 2016年9月13日 上午11:39:44
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
@Component
public class CarRepairRecordTag extends  BaseFreeMarkerTag{
	private IStoreMemberManager storeMemberManager;
	private IStoreOrderManager storeOrderManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		//session中获取会员信息,判断用户是否登录
		StoreMember member=storeMemberManager.getStoreMember();
		if(member==null){
			HttpServletResponse response= ThreadContextHolder.getHttpResponse();
			try {
				response.sendRedirect("login.html");
			} catch (IOException e) {
				throw new UrlNotFoundException();
			}
		}
		String car_id = getRequest().getParameter("car_id");
		JSONObject carRepairObj = storeOrderManager.getCarRepairRecords(car_id);
		
		Map map = new HashMap();
		map.put("carinfo", carRepairObj.get("carinfo"));
		map.put("repairRecords", carRepairObj.get("repairRecords"));
		return map;
	}
	
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}
	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}
}
