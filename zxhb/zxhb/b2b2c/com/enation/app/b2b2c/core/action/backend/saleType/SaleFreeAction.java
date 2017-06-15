package com.enation.app.b2b2c.core.action.backend.saleType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.sale.SaleType;
import com.enation.app.b2b2c.core.model.store.StoreLevel;
import com.enation.app.b2b2c.core.service.saleType.ISaleFreeManager;
import com.enation.app.b2b2c.core.service.saleType.ISaleTypeManager;
import com.enation.app.b2b2c.core.service.store.IStoreLevelManager;
import com.enation.app.base.core.model.SaleFree;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="saleFree",type="freemarker", location="/b2b2c/admin/saleType/sale_free_list.html"),
	 @Result(name="addinfo",type="freemarker", location="/b2b2c/admin/saleType/sale_free_addinfo.html"),
	 @Result(name="editinfo",type="freemarker", location="/b2b2c/admin/saleType/sale_free_editinfo.html"),
	 @Result(name="audit_list",type="freemarker", location="/b2b2c/admin/saleType/audit_list.html")
})
@Action("salefree")
@SuppressWarnings("unchecked")
public class SaleFreeAction extends WWAction{

	private ISaleFreeManager saleFreeManager;
	private ISaleTypeManager saleTypeManager;
	private IStoreLevelManager storeLevelManager;
	private SaleType saleType;
	private Integer id;
	private List<SaleFree> saleFreeList;
	private List<StoreLevel> storeLevelList;
	private List<SaleType> saleTypeList;
	private SaleFree saleFree;
	private String storeId;
	private String typeId;
	private Integer auditNum;
	private Integer sign;
	
	private Integer stype;
	private String keyword;
	private String storeName;
	private Integer storeLevel;
	private Integer isFree;
	private String auditStatus;
	private Map saleFreemap;
	/**
	 * 营销管理免费模板列表
	 */
	public String saleFree_list(){
		saleTypeList = saleTypeManager.saleTypeList();
		storeLevelList = storeLevelManager.storeLevelList();
		return "saleFree";
	}
	public String saleFreeJson(){
		HttpServletRequest requst = ThreadContextHolder.getHttpRequest();
		saleFreemap = new HashMap();
		saleFreemap.put("stype", stype);
		saleFreemap.put("typeId", typeId);
		saleFreemap.put("keyword", keyword);
		saleFreemap.put("storeLevel", storeLevel);
		saleFreemap.put("isFree", isFree);
		this.webpage = this.saleFreeManager.saleFreeList(saleFreemap, this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 跳转到营销管理免费模板详细页
	 * @return
	 */
	public String list(){
		return "storelevel";
	}
	
	/**
	 * 跳转到营销管理审核列表
	 * @return
	 */
	public String audit_list(){
		saleTypeList = saleTypeManager.saleTypeList();
		storeLevelList = storeLevelManager.storeLevelList();
		return "audit_list";
	}
	public String saleAuditListJson(){
		
		HttpServletRequest requst = ThreadContextHolder.getHttpRequest();
		saleFreemap = new HashMap();
		saleFreemap.put("stype", stype);
		saleFreemap.put("typeId", typeId);
		saleFreemap.put("keyword", keyword);
		saleFreemap.put("storeName", storeName);
		saleFreemap.put("storeLevel", storeLevel);
		saleFreemap.put("auditStatus", auditStatus);
		this.webpage = this.saleFreeManager.saleAuditList(saleFreemap, this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 审核通过
	 * @return
	 */
	public String audit_pass(){
		try {
			if(sign == 0){
				saleFreeManager.audit_fail(sign,id);
			}else{
				saleFreeManager.audit_pass(id,storeId,typeId,auditNum);
			}
			this.showSuccessJson("操作成功");
		} catch (Exception e) {
			this.showErrorJson("审核失败");
			this.logger.error("审核失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	
	
	public String add(){
		saleTypeList = saleTypeManager.saleTypeList();
		storeLevelList = storeLevelManager.storeLevelList();
		return "addinfo";
	}
	public String edit(){
		saleFree=saleFreeManager.getSaleFree(id);
		saleTypeList = saleTypeManager.saleTypeList();
		storeLevelList = storeLevelManager.storeLevelList();
		return "editinfo";
	}
	
	/**
	 * 删除营销管理免费模板
	 * @return
	 */
	public String delSaleFree(){
		try {
			saleFreeManager.delSaleFree(id);
			this.showSuccessJson("删除营销管理免费模板成功");
		} catch (Exception e) {
			this.showErrorJson("删除营销管理免费模板失败");
			this.logger.error("删除营销管理免费模板失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 添加营销管理免费模板 
	 * @return
	 */
	public String addSaleFree(){
		try {
			saleFreeManager.addSaleFree(saleFree);
			this.showSuccessJson("添加营销管理免费模板成功");
		} catch (Exception e) {
			this.showErrorJson("添加营销管理免费模板失败");
			this.logger.error("添加营销管理免费模板失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 修改营销管理免费模板
	 * @return
	 */
	public String editSaleFree(){
		try {
			saleFreeManager.editSaleFree(saleFree);
			this.showSuccessJson("修改营销管理免费模板成功");
		} catch (Exception e) {
			this.showErrorJson("修改营销管理免费模板失败");
			this.logger.error("修改营销管理免费模板失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	
	public ISaleFreeManager getSaleFreeManager() {
		return saleFreeManager;
	}
	public void setSaleFreeManager(ISaleFreeManager saleFreeManager) {
		this.saleFreeManager = saleFreeManager;
	}
	public ISaleTypeManager getSaleTypeManager() {
		return saleTypeManager;
	}
	public void setSaleTypeManager(ISaleTypeManager saleTypeManager) {
		this.saleTypeManager = saleTypeManager;
	}
	public SaleType getSaleType() {
		return saleType;
	}
	public void setSaleType(SaleType saleType) {
		this.saleType = saleType;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public List<SaleFree> getSaleFreeList() {
		return saleFreeList;
	}
	public void setSaleFreeList(List<SaleFree> saleFreeList) {
		this.saleFreeList = saleFreeList;
	}
	public List<StoreLevel> getStoreLevelList() {
		return storeLevelList;
	}
	public void setStoreLevelList(List<StoreLevel> storeLevelList) {
		this.storeLevelList = storeLevelList;
	}
	public List<SaleType> getSaleTypeList() {
		return saleTypeList;
	}
	public void setSaleTypeList(List<SaleType> saleTypeList) {
		this.saleTypeList = saleTypeList;
	}
	public IStoreLevelManager getStoreLevelManager() {
		return storeLevelManager;
	}
	public void setStoreLevelManager(IStoreLevelManager storeLevelManager) {
		this.storeLevelManager = storeLevelManager;
	}
	public SaleFree getSaleFree() {
		return saleFree;
	}
	public void setSaleFree(SaleFree saleFree) {
		this.saleFree = saleFree;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public Map getSaleFreemap() {
		return saleFreemap;
	}
	public void setSaleFreemap(Map saleFreemap) {
		this.saleFreemap = saleFreemap;
	}
	public Integer getAuditNum() {
		return auditNum;
	}
	public void setAuditNum(Integer auditNum) {
		this.auditNum = auditNum;
	}
	public Integer getSign() {
		return sign;
	}
	public void setSign(Integer sign) {
		this.sign = sign;
	}
	public Integer getStype() {
		return stype;
	}
	public void setStype(Integer stype) {
		this.stype = stype;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Integer getStoreLevel() {
		return storeLevel;
	}
	public void setStoreLevel(Integer storeLevel) {
		this.storeLevel = storeLevel;
	}
	public Integer getIsFree() {
		return isFree;
	}
	public void setIsFree(Integer isFree) {
		this.isFree = isFree;
	}
	public String getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
}
