package com.enation.app.b2b2c.core.action.api.store;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.store.IStoreMerchantManager;
import com.enation.app.base.core.model.MerchantInfo;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;

/**
 * @Description 商家入驻API
 *
 * @createTime 2016年8月25日 下午4:44:52
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("merchantSettled")
public class StoreMerchantSettledApiAction extends WWAction {

	private static final long serialVersionUID = 1L;
	
	private IStoreMerchantManager storeMerchantManager;
	
	/**
	 * @description 校验公司名称是否已经注册
	 * @date 2016年8月30日 上午10:32:33
	 * @param companyName
	 * @return String
	 */
	public String checkCompanyNameIsRepeat() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String companyName = request.getParameter("companyName");
		if(StringUtils.isEmpty(companyName)) {
			this.showErrorJson("公司名称不能为空！");
			return JSON_MESSAGE;
		}
		
		//校验公司名称是否重复
	    int result = storeMerchantManager.checkCompanyNameIsRepeat(companyName);
	    if(result==0) {
			this.showSuccessJson("公司名称可以使用！");
		} else {
			this.showErrorJson("该公司名称已经存在！");
		}
	    
	    ServletActionContext.getResponse().setHeader("Access-Control-Allow-Origin", "*");
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 校验邮箱是否重复
	 * @date 2016年9月3日 下午4:05:58
	 * @return String
	 */
	public String checkEmailIsRepeat() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String email = request.getParameter("email");
		if(StringUtils.isEmpty(email)) {
			this.showErrorJson("邮箱不能为空！");
			return JSON_MESSAGE;
		}
		
		//校验邮箱是否重复
	    int result = storeMerchantManager.checkEmailIsRepeat(email);
	    if(result==0) {
			this.showSuccessJson("邮箱可以使用！");
		} else {
			this.showErrorJson("该邮箱已被注册！");
		}
	    
	    ServletActionContext.getResponse().setHeader("Access-Control-Allow-Origin", "*");
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 保存商家信息
	 * @date 2016年8月25日 下午5:24:45
	 * @return String
	 */
	public String saveMerchantInfo() {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		
		//获取请求数据
		String companyName = request.getParameter("companyName").trim();
		String brand = request.getParameter("brand").trim();
		String contactName = request.getParameter("contactName").trim();
		String contactPhone = request.getParameter("contactPhone").trim();
		String email = request.getParameter("email").trim();
		String address = request.getParameter("address").trim();
		
		//校验公司名称和邮箱是否为空
		if(StringUtils.isEmpty(companyName)) {
			this.showErrorJson("公司名称不能为空！");
			return JSON_MESSAGE;
		}
		if(StringUtils.isEmpty(email)) {
			this.showErrorJson("邮箱不能为空！");
			return JSON_MESSAGE;
		}
		
		try{
		    //封装数据
		    MerchantInfo merchantInfo = new MerchantInfo();
		    merchantInfo.setCompany_name(companyName);
		    merchantInfo.setBrand(brand);
		    merchantInfo.setContact_name(contactName);
		    merchantInfo.setContact_phone(contactPhone);
		    merchantInfo.setEmail(email);
		    merchantInfo.setAddress(address);
		    merchantInfo.setCreate_time(DateUtil.getDateline());
		
		    //保存商户信息到数据库
		    int saveResult = storeMerchantManager.saveMerchantInfo(merchantInfo);
		    if(saveResult != 1) {
		    	this.logger.error("商户信息保存失败");
		    	this.showErrorJson("商户信息保存失败");
		    	return JSON_MESSAGE;
		    }
		    this.logger.info("商户信息保存成功");
		    
		    //跳转页面
		    response.sendRedirect("http://www.obdpay.com/merchantSettled/done.html");
		    
		} catch (Exception e) {
			this.logger.error("商户信息保存失败");
			this.showErrorJson("商户信息保存失败");
			e.getStackTrace();
			
		}		
		return JSON_MESSAGE;
	}
	
	
	
	/*
	 * =========================================================================================
	 * setter and getter
	 */
	public IStoreMerchantManager getStoreMerchantManager() {
		return storeMerchantManager;
	}

	public void setStoreMerchantManager(IStoreMerchantManager storeMerchantManager) {
		this.storeMerchantManager = storeMerchantManager;
	}

}
