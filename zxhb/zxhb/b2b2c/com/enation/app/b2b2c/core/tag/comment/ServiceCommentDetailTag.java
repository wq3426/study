package com.enation.app.b2b2c.core.tag.comment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
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
public class ServiceCommentDetailTag extends BaseFreeMarkerTag{
	
	public ICommentManager CommentManager;
	
	public IStoreMemberManager storeMemberManager;

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
		String order_sn = request.getParameter("order_sn");
		String userInfo = request.getParameter("userInfo");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String storeId = String.valueOf(member.getStore_id());
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("order_sn",order_sn);
		result.put("userInfo", userInfo);
		result.put("startTime", startTime);
		result.put("storeId", storeId);
		result.put("endTime", endTime);
		String page = request.getParameter("page")==null?"1": request.getParameter("page");
		result.put("page", page);
		result.put("pageSize", "10");
		Page serviceCommentList= CommentManager.listServiceComment(result);
		long totalCount = serviceCommentList.getTotalCount();
		serviceCommentList.setCurrentPageNo(Integer.parseInt(page));
		//获取总记录数
		Object data = serviceCommentList.getResult();
		String repData = UploadUtil.replacePath(JSONArray.fromObject(data).toString());
		data = JSONArray.toList(JSONArray.fromObject(repData));
		serviceCommentList.setResult(data);
		result.put("totalCount", totalCount);
		result.put("pageList",serviceCommentList);
		return result;
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
