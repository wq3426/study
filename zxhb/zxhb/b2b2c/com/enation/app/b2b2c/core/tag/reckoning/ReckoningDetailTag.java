package com.enation.app.b2b2c.core.tag.reckoning;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeStatus;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeType;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.member.impl.StoreMemberManager;
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.b2b2c.core.service.store.impl.StoreManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;
import net.sf.json.JSONArray;

@Component
public class ReckoningDetailTag extends BaseFreeMarkerTag {
	private IReckoningManager reckoningManager;
	private IStoreMemberManager storeMemberManager;
	private IStoreManager storeManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {

		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		StoreMember member = storeMemberManager.getStoreMember();
		// session中获取会员信息,判断用户是否登陆
		if (member == null) {
			HttpServletResponse response = ThreadContextHolder.getHttpResponse();
			try {
				response.sendRedirect("login.html");
			} catch (IOException e) {
				throw new UrlNotFoundException();
			}
		}
		int pageSize=10;
		Map result = new HashMap();
		String page = request.getParameter("page")==null?"1": request.getParameter("page");
		String trade_type = request.getParameter("trade_type");
		String trade_status = request.getParameter("trade_status");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		
		result.put("trade_type", trade_type);
		result.put("trade_status", trade_status);
		result.put("startTime", startTime);
		result.put("endTime", endTime);
		result.put("pageType","transactionDetail");
		result.put("store_id", member.getStore_id());
		Page reckoningList = reckoningManager.getReckoningList(Integer.parseInt(page), pageSize,member.getStore_id(), result);
		
		//获取4S店收入
		Map income = reckoningManager.getReckoningByIncome(result);
		//获取4S店支出
		Map pay = reckoningManager.getReckoningByPay(result);
		
		//获取总记录数
		Long totalCount = reckoningList.getTotalCount();
		result.put("page", page);
		result.put("pageSize", pageSize);
		result.put("totalCount", totalCount);
		result.put("reckoningList", reckoningList);
		result.put("income",income.get("income"));
		result.put("pay",pay.get("pay"));
	
		return result;
	}

	public IReckoningManager getReckoningManager() {
		return reckoningManager;
	}

	public void setReckoningManager(IReckoningManager reckoningManager) {
		this.reckoningManager = reckoningManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}

}
