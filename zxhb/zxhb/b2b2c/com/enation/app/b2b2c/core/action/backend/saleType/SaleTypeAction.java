package com.enation.app.b2b2c.core.action.backend.saleType;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.sale.SaleType;
import com.enation.app.b2b2c.core.service.saleType.ISaleTypeManager;
import com.enation.framework.action.WWAction;
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="saleType",type="freemarker", location="/b2b2c/admin/saleType/sale_type_list.html"),
	 @Result(name="info",type="freemarker", location="/b2b2c/admin/saleType/sale_type_info.html")
})
@Action("saletype")
public class SaleTypeAction extends WWAction{

	private ISaleTypeManager saleTypeManager;
	private SaleType saleType;
	private Integer type_id;
	/**
	 * 营销类型列表
	 */
	public String saleType_list(){
		return "saleType";
	}
	public String saleTypeJson(){
		this.showGridJson(saleTypeManager.saleTypeList());
		return this.JSON_MESSAGE;
	}
	/**
	 * 跳转到营销类型详细页
	 * @return
	 */
	public String list(){
		return "storelevel";
	}
	public String add(){
		return "info";
	}
	public String edit(){
		saleType=saleTypeManager.getSaleType(type_id);
		return "info";
	}
	
	/**
	 * 删除营销类型
	 * @return
	 */
	public String delSaleType(){
		try {
			saleTypeManager.delSaleType(type_id);
			this.showSuccessJson("删除营销类型成功");
		} catch (Exception e) {
			this.showErrorJson("删除营销类型失败");
			this.logger.error("删除营销类型失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 添加营销类型 
	 * @return
	 */
	public String addSaleType(){
		try {
			saleTypeManager.addSaleType(saleType);
			this.showSuccessJson("添加营销类型成功");
		} catch (Exception e) {
			this.showErrorJson("添加营销类型失败");
			this.logger.error("添加营销类型失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 修改营销类型
	 * @return
	 */
	public String editSaleType(){
		try {
			saleTypeManager.editSaleType(saleType);
			this.showSuccessJson("修改营销类型成功");
		} catch (Exception e) {
			this.showErrorJson("修改营销类型失败");
			this.logger.error("修改营销类型失败:"+e);
		}
		return this.JSON_MESSAGE;
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
	public Integer getType_id() {
		return type_id;
	}
	public void setType_id(Integer type_id) {
		this.type_id = type_id;
	}
	
}
