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
 * 获取会员集合
 * @author chopper
 *
 */
@Component
public class CarInsureRecordTag extends  BaseFreeMarkerTag{
	private IStoreMemberManager storeMemberManager;
	private IStoreOrderManager storeOrderManager;
	
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
		String car_id = getRequest().getParameter("car_id");
		JSONObject carInsureObj = storeOrderManager.getCarInsureRecords(car_id);
		
		Map map = new HashMap();
		map.put("carinfo", carInsureObj.get("carinfo"));
		map.put("insureRecords", carInsureObj.get("insureRecords"));
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
