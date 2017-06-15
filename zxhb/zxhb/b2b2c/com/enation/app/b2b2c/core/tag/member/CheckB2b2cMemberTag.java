package com.enation.app.b2b2c.core.tag.member;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.Member;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
@Component
/**
 * 检查b2b2c会员
 * @author LiFenLong
 *
 */
public class CheckB2b2cMemberTag extends BaseFreeMarkerTag{
	private IStoreMemberManager storeMemberManager;
	@Override
	/**
	 * 获取当前登录会员
	 */
	protected Object exec(Map params) throws TemplateModelException {
		String ctx = this.getRequest().getContextPath();
		if("/".equals(ctx)){
			ctx="";
		}
		StoreMember member = storeMemberManager.getStoreMember();
		
		if (member == null) {
			HttpServletResponse response = ThreadContextHolder.getHttpResponse();
			try {
				response.sendRedirect(ctx+"/store/login.html");
				return new Member();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return member;
		}
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
