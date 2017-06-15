package com.enation.app.b2b2c.core.action.api.member;

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
public class BreakMemberListTag extends  BaseFreeMarkerTag{
	private IMemberManager memberManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		int pageSize=10; 
		String page = request.getParameter("page")==null?"1": request.getParameter("page");
		
		return memberManager.MemberListBreak(Integer.parseInt(page), pageSize, ((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id());
	}
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

}
