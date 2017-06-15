package com.enation.app.b2b2c.core.tag.reckoning;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
public class ReckoningThisPeriodTag extends BaseFreeMarkerTag {
	private IReckoningManager reckoningManager;
	private IStoreMemberManager storeMemberManager;
	private IStoreManager storeManager;

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		try{
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
			String userInfo = request.getParameter("userInfo");
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			String order_type = request.getParameter("order_type");
			result.put("order_type", order_type);
			result.put("startTime", startTime);
			result.put("endTime", endTime);
			result.put("userInfo", userInfo);
			result.put("pageType", "thisPeriod");
			result.put("store_id", member.getStore_id());
			Page thisPeriodReckoningList = reckoningManager.getThisPeriodReckoning(Integer.parseInt(page), pageSize,member.getStore_id(), result);
			// 获取4S店收入
			Map income = reckoningManager.getReckoningByIncome(result);
			// 获取4S店支出
			Map pay = reckoningManager.getReckoningByPay(result);
			
			//获取总记录数
			Long totalCount = thisPeriodReckoningList.getTotalCount();
			result.put("page", page);
			result.put("pageSize", pageSize);
			result.put("totalCount", totalCount);
			result.put("thisPeriodList", thisPeriodReckoningList);
			result.put("income", income.get("income"));
			result.put("pay", pay.get("pay"));
			return result;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
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
