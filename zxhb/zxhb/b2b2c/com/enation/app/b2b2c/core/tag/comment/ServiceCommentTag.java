package com.enation.app.b2b2c.core.tag.comment;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.shop.core.model.Comment;
import com.enation.app.shop.core.service.ICommentManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/** @Description 订单服务评价详情
 *
 * @createTime 2016年8月29日 下午4:30:49
 *
 * @author <a href="mailto:huashixin@trans-it.cn">huashixin</a>
 */
@Component
@Scope("prototype")
public class ServiceCommentTag extends BaseFreeMarkerTag{
	
	public ICommentManager CommentManager;
	
	public IStoreMemberManager storeMemberManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		//session中获取会员信息,判断用户是否登陆
		String ordersn = request.getParameter("ordersn");
		List<Map> comments = CommentManager.getCommentByOrderId(ordersn);
		return comments;
	}

	public ICommentManager getCommentManager() {
		return CommentManager;
	}

	public void setCommentManager(ICommentManager commentManager) {
		CommentManager = commentManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	
	
	
}
