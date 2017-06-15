package com.enation.app.b2b2c.core.tag.member;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component; 

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.service.IAppMessageManager; 
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 获取messag详细信息
 * @author chopper
 *
 */
@Component
public class AppMessageListTag extends BaseFreeMarkerTag{

	private IAppMessageManager appMessageManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {

		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		//获得消息lie biao参数
		int pageSize=10; 
		String page = request.getParameter("page")==null?"1": request.getParameter("page");
		
		int storeId = ((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id();
		
		return this.appMessageManager.listPage(Integer.parseInt(page), pageSize, storeId);
	}
	public IAppMessageManager getAppMessageManager() {
		return appMessageManager;
	}
	public void setAppMessageManager(IAppMessageManager appMessageManager) {
		this.appMessageManager = appMessageManager;
	} 
	
}
