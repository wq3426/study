package com.enation.app.b2b2c.core.action.backend.settlement;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.settlement.impl.IAdminSettlementManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;

import net.sf.json.JSONObject;

/** @Description 发票管理
 *
 * @createTime 2016年9月12日 上午10:48:32
 *
 * @author <a href="mailto:huashixin@trans-it.cn">huashixin</a>
 */
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({ @Result(name = "list", type = "freemarker", location = "/b2b2c/admin/settlement/invoice_list.html")})
@Action("adminInvoice")
public class AdminInvoiceAction extends WWAction {
	
	private IAdminSettlementManager adminSettlementManager;
	
	private Map result;
	
	public String list(){
		return "list";
	}
	
	public String listJSON(){
		HttpServletRequest request = this.getRequest();
		int pageSize=10;
		String pageNo = request.getParameter("pageNo")==null?"1": request.getParameter("pageNo");
		Map result = new HashMap();
		String store_name = request.getParameter("store_name");
		result.put("store_name",store_name);
		Page invoicePage = adminSettlementManager.listInvoice(Integer.parseInt(pageNo), pageSize,result);
		invoicePage.setCurrentPageNo(Integer.parseInt(pageNo));
		result.put("invoicePage", invoicePage);
		String data =JSONObject.fromObject(invoicePage).toString();
		this.json = "{\"result\" : \"1\",\"data\" : " + data +"}"; 
		return JSON_MESSAGE;
	}

	public IAdminSettlementManager getAdminSettlementManager() {
		return adminSettlementManager;
	}

	public void setAdminSettlementManager(IAdminSettlementManager adminSettlementManager) {
		this.adminSettlementManager = adminSettlementManager;
	}

	public Map getResult() {
		return result;
	}

	public void setResult(Map result) {
		this.result = result;
	}
	
	
}
