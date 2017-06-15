package com.enation.app.b2b2c.core.tag.member;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 获取会员集合
 * @author chopper
 *
 */
@Component
public class DiscontractMemberListTag extends  BaseFreeMarkerTag{
	private IMemberManager memberManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		int pageSize = 3; 
		String page = request.getParameter("page") == null ? "1" : request.getParameter("page");
		String time_list = request.getParameter("time_list");
		String cost = request.getParameter("cost");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		
		Map map = new HashMap();
		map.put("time_list", time_list);
		map.put("cost", cost);
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		Page pageObj = memberManager.discontractMemberList(Integer.parseInt(page), pageSize, ((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id(), map);
		pageObj.setCurrentPageNo(Integer.valueOf(page));
		map.put("pageList", pageObj);
		return map;
	}
	
	
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

}
