package com.enation.app.b2b2c.core.action.backend.store;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.store.IStoreLevelManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Store;
import com.enation.app.base.core.model.StoreAudit;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.eop.sdk.utils.ValidateUtils;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;

import cn.jiguang.commom.utils.StringUtils;

/**
 * @Description 商家店铺管理
 *
 * @createTime 2016年9月18日 下午2:36:56
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/b2b2c/admin")
@Action("storeadmin")
@Results({
	@Result(name="audit", type="freemarker", location="/shop/admin/store/store_audit.html"),
	@Result(name="audit_reject", type="freemarker", location="/shop/admin/store/store_reject.html"),
	@Result(name="audit_list", type="freemarker", location="/shop/admin/store/store_list_audit.html")
})
public class StoreAdminAction extends WWAction {
	
	private static final long serialVersionUID = 1L;
	private IStoreManager storeManager;
	private Store store;
	private List level_list;
	private IStoreLevelManager storeLevelManager;
	/**
	 * @description 显示审核列表
	 * @date 2016年9月14日 上午11:33:47
	 * @return String
	 */
	public String showStoreAuditList() {	
		return "audit_list";
	}
	
	/**
	 * @description 查询经销商及其店铺信息（分页）
	 * @date 2016年8月26日 下午2:52:15
	 * @return String
	 */
	public String storeListJson() {
		
		//获取基础数据
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String page = request.getParameter("page")==null?"1": request.getParameter("page");
		
		int pageSize = this.getPageSize();;//每页显示的条数
		int pageNum = Integer.parseInt(page);	
		
		//查询商户信息List
		Page storeInfo = storeManager.queryStoreOfUnAudit(pageNum, pageSize);
		
		//响应数据
		this.showGridJson(storeInfo);
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 显示审核页面
	 * @date 2016年9月18日 下午5:20:05
	 * @return String
	 */
	public String showStoreAudit() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		int store_id = Integer.parseInt(request.getParameter("storeId"));
		store = storeManager.getStore(store_id);
		String bank_license_img = store.getBank_license_img();
		String store_logo = store.getStore_logo();
		String id_img = store.getId_img();
		String corporation_id_img = store.getCorporation_id_img();
		String license_img = store.getLicense_img();
		String contact_idimg_back = store.getContact_idimg_back();
		String corporate_idimg_back = store.getCorporate_idimg_back();
		
		//TODO 以后使用FastDFS要去掉下面的代码
		if(StringUtils.isNotEmpty(bank_license_img)) {
			bank_license_img = UploadUtil.replacePath(bank_license_img);
			store.setBank_license_img(bank_license_img);
		}
		if(StringUtils.isNotEmpty(store_logo)) {
			store_logo = UploadUtil.replacePath(store_logo);
			store.setStore_logo(store_logo);
		}
		if(StringUtils.isNotEmpty(id_img)) {
			id_img = UploadUtil.replacePath(id_img);
			store.setId_img(id_img);
		}
		if(StringUtils.isNotEmpty(corporation_id_img)) {
			corporation_id_img = UploadUtil.replacePath(corporation_id_img);
			store.setCorporation_id_img(corporation_id_img);
		}
		if(StringUtils.isNotEmpty(license_img)) {
			license_img = UploadUtil.replacePath(license_img);
			store.setLicense_img(license_img);
		}
		if(StringUtils.isNotEmpty(corporate_idimg_back)) {
			corporate_idimg_back = UploadUtil.replacePath(corporate_idimg_back);
			store.setCorporate_idimg_back(corporate_idimg_back);
		}
		if(StringUtils.isNotEmpty(contact_idimg_back)) {
			contact_idimg_back = UploadUtil.replacePath(contact_idimg_back);
			store.setContact_idimg_back(contact_idimg_back);
		}
		
		level_list = storeLevelManager.storeLevelList();
		//-----------------------------------------------------------
		return "audit";
	}
	
	/**
	 * @description 显示驳回页面
	 * @date 2016年9月20日 上午10:39:01
	 * @return String
	 */
	public String showStoreAuditReject(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		int store_id = Integer.parseInt(request.getParameter("storeId"));
		store = storeManager.getStore(store_id);
		return "audit_reject";
	}
	
	/**
	 * @description 审核通过保存数据
	 * @date 2016年9月20日 下午3:22:12
	 * @return String
	 */
	public String auditPass() {
		
		//获取请求数据
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		int store_id = Integer.parseInt(request.getParameter("storeId"));
		String docker = request.getParameter("docker");
		String docker_job_number = request.getParameter("docker_job_number");
		String docker_tel = request.getParameter("docker_tel");
		String storeLevel = request.getParameter("store_level");
		String settlementPeriod = request.getParameter("settlement_period");
		String discountcontract_string = request.getParameter("discountcontract");
		String discountnoncontract_string = request.getParameter("discountnoncontract");
		if(StringUtils.isEmpty(docker)) {
			this.showErrorJson("请填写对接人姓名");
			return JSON_MESSAGE;
		}
		if(StringUtils.isEmpty(docker_job_number)) {
			this.showErrorJson("请填写对接人工号");
			return JSON_MESSAGE;
		}
		if(StringUtils.isEmpty(docker_tel)) {
			this.showErrorJson("请填写对接人联系方式");
			return JSON_MESSAGE;
		}		
		if(!ValidateUtils.isMobile(docker_tel)) {
			if(!ValidateUtils.isPhone(docker_tel)){
				this.showErrorJson("请填写正确的对接人联系方式");
				return JSON_MESSAGE;
			}			
		}
		if(StringUtils.isEmpty(storeLevel)) {
			this.showErrorJson("请选择用户等级");
			return JSON_MESSAGE;
		}
		if(StringUtils.isEmpty(settlementPeriod)) {
			this.showErrorJson("请设置结算周期");
			return JSON_MESSAGE;
		}
		if(StringUtils.isEmpty(discountcontract_string)) {
			this.showErrorJson("请设置签约用户折扣");
			return JSON_MESSAGE;
		}
		if(StringUtils.isEmpty(discountnoncontract_string)) {
			this.showErrorJson("非签约用户折扣");
			return JSON_MESSAGE;
		}
		
		int store_level = Integer.parseInt(storeLevel);
		int settlement_period = Integer.parseInt(settlementPeriod);
		Double discountcontract = Double.parseDouble(discountcontract_string);
		Double discountnoncontract = Double.parseDouble(discountnoncontract_string);
		
		//封装数据
		Store store = new Store();
		store.setStore_id(store_id);
		store.setAuditstatus("2"); //已审核
		store.setDocker(docker);
		store.setDocker_job_number(docker_job_number);
		store.setDocker_tel(docker_tel);
		store.setStore_level(store_level);
		store.setSettlement_period(settlement_period);
		store.setDiscountcontract(discountcontract);
		store.setDiscountnoncontract(discountnoncontract);
		
		try {
			//更新店铺（Store）数据
			int result = storeManager.updateStoreOfpassAudit(store);
			if(result == 1) {
				//保存审核记录
				StoreAudit storeAudit = new StoreAudit();
				storeAudit.setStore_id(store_id);
				storeAudit.setAudit_result("1");
				storeAudit.setRemark("");
				
				//检查审核结果是否已经存在
				Map<String, Integer> auditIdMap = storeManager.queryStoreAuditResultIsExist(store_id);
				int saveResult = 0;
				int audit_id = 0;
				if(ValidateUtils.isNotEmpty(auditIdMap)) {
					audit_id = auditIdMap.get("id");
				}
				
				if(audit_id == 0) {
					saveResult = storeManager.saveStoreAuditResult(storeAudit);
				} else {
					storeAudit.setId(audit_id);
					saveResult = storeManager.updateStoreAuditResult(storeAudit);
				}
				if(saveResult == 1) {
					this.showSuccessJson("审核通过，数据保存成功");
				} else {
					this.showSuccessJson("数据保存失败");
				}
			} else {
				this.showErrorJson("店铺数据更新失败");
			}
		} catch (Exception e) {
			this.showErrorJson("操作失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 审核驳回
	 * @date 2016年9月20日 上午10:52:36
	 * @return String
	 */
	public String showAuditReject() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		int store_id = Integer.parseInt(request.getParameter("storeId"));
		String remark1 = request.getParameter("remark1")+"<br />";
		String remark2 = request.getParameter("remark2")+"<br />";
		String remark3 = request.getParameter("remark3")+"<br />";
		String remark4 = request.getParameter("remark4")+"<br />";
		String remark5 = request.getParameter("remark5")+"<br />";
			
		String remark = remark1 + remark2 + remark3 + remark4 + remark5;
		StoreAudit storeAudit = new StoreAudit();
		storeAudit.setStore_id(store_id);
		storeAudit.setRemark(remark);	//驳回理由
		storeAudit.setAudit_result("0");//驳回
		
		//检查审核结果是否已经存在
		try {
			int saveResult = 0;
			int audit_id = 0;
			Map<String, Integer> auditIdMap = storeManager.queryStoreAuditResultIsExist(store_id);
			if(ValidateUtils.isNotEmpty(auditIdMap)) {
				audit_id = auditIdMap.get("id");
			}
			if(audit_id == 0) {
				saveResult = storeManager.saveStoreAuditResult(storeAudit);
			} else {
				storeAudit.setId(audit_id);
				saveResult = storeManager.updateStoreAuditResult(storeAudit);
			}
			
			//更新店铺审核状态
			int updateResult = storeManager.updateStoreAuditStatus(store_id);
			
			if(saveResult == 1 && updateResult == 1) {
				this.showSuccessJson("已驳回");
			} else {
				this.showSuccessJson("驳回失败");
			}
		} catch (Exception e) {
			this.showErrorJson("操作失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	
	
	
	
	
	
	
	
	
	/*
	 * ===================================================================================
	 * getter and setter
	 */	
	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public List getLevel_list() {
		return level_list;
	}

	public void setLevel_list(List level_list) {
		this.level_list = level_list;
	}

	public IStoreLevelManager getStoreLevelManager() {
		return storeLevelManager;
	}

	public void setStoreLevelManager(IStoreLevelManager storeLevelManager) {
		this.storeLevelManager = storeLevelManager;
	}
	
	
	
	
}
