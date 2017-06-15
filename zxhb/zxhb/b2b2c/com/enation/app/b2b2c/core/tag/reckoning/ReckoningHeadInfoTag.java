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
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
public class ReckoningHeadInfoTag extends BaseFreeMarkerTag {
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
		Map result = new HashMap();
		// 获取当前店铺已结算历史总额（包括已结算和提取的），待结算总额，账户总额
		double settlementCountHistory = reckoningManager.getSettlementCount(ReckoningTradeType.draw_money.getIndex(),null,member.getStore_id());
		double noSettlementCount = reckoningManager.getSettlementCount(ReckoningTradeType.settle_accounts.getIndex(),ReckoningTradeStatus.no_settle_accounts.getIndex(),member.getStore_id());
		double yetSettlementCount = storeManager.getBalance(member.getStore_id());
		result.put("settlementCountHistory", settlementCountHistory);
		result.put("noSettlementCount", noSettlementCount);
		result.put("yetSettlementCount", yetSettlementCount);
	
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
