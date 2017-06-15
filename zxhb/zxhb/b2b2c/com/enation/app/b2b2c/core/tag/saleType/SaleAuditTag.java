package com.enation.app.b2b2c.core.tag.saleType;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.saleType.ISaleAuditManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 免费申请/购买营销类型列表
 * @author Yunbs
 * @version v1.0, 2016年8月17日
 * @since v5.2
 */
@Component
@Scope("prototype")
public class SaleAuditTag extends BaseFreeMarkerTag {
	private IStoreMemberManager storeMemberManager;
	private ISaleAuditManager saleAuditManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		StoreMember storeMember =storeMemberManager.getStoreMember() ;
		Integer store_id = storeMember.getStore_id();
		
		String page = request.getParameter("page");
		page = (page == null || page.equals("")) ? "1" : page;
		int pageSize=10;
		Map map=new HashMap();
		
		String saleType=request.getParameter("saleType");
		String isFree=request.getParameter("isFree");
		String status=request.getParameter("status");
		String startTime=request.getParameter("startTime");
		String endTime=request.getParameter("endTime");
		
		map.put("saleType", saleType);
		map.put("isFree", isFree);
		map.put("status", status);
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		
		Page auditList=saleAuditManager.getAllAudit(Integer.parseInt(page), pageSize, map,store_id);
		//获取总记录数
		Long totalCount = auditList.getTotalCount();
		
		map.put("page", page);
		map.put("pageSize", pageSize);
		map.put("totalCount", totalCount);
		map.put("auditList", auditList);
		
		return map;
	}
	
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public ISaleAuditManager getSaleAuditManager() {
		return saleAuditManager;
	}

	public void setSaleAuditManager(ISaleAuditManager saleAuditManager) {
		this.saleAuditManager = saleAuditManager;
	}

}
