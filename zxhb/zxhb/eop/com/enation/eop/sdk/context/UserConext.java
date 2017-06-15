package com.enation.eop.sdk.context;

import com.enation.app.base.core.model.Member;
import com.enation.eop.resource.model.AdminUser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;

/**
 * 用户上下文
 * @author kingapex
 *
 */
public abstract class UserConext {
	public static final String CURRENT_MEMBER_KEY="curr_member";
	public static final String CURRENT_ADMINUSER_KEY="curr_adminuser";
	
	/**
	 * 获取当前登录的会员
	 * @return 如果没有登录返回null
	 */
	public static Member getCurrentMember(){
		
		WebSessionContext<Member> sessonContext = ThreadContextHolder.getSessionContext();
		return sessonContext.getAttribute(UserConext.CURRENT_MEMBER_KEY);
	}
	
	/**
	 * 获取当前登录的管理员
	 * @return 如果没有登录返回null
	 */
	public static AdminUser getCurrentAdminUser(){
		
		WebSessionContext<AdminUser> sessonContext = ThreadContextHolder.getSessionContext();
		return sessonContext.getAttribute(UserConext.CURRENT_ADMINUSER_KEY);
	}
}
