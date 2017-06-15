package com.enation.app.base.core.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.service.IAdColumnManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;

/**
 * 广告位管理Action
 * @author lzf 2010-3-2 上午09:46:08 version 1.0
 * @author kanon 2015-9-22 version 1.1 添加注释
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("adColumn")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/adv/adc_list.html"),
	@Result(name="add", type="freemarker", location="/core/admin/adv/adc_input.html"), 
	@Result(name="edit", type="freemarker", location="/core/admin/adv/adc_edit.html") 
})
public class AdColumnAction extends WWAction {
	
	private IAdColumnManager adColumnManager;
	private AdColumn adColumn;
	private Long ac_id;
	private Integer[] acid;
	
	/**
	 * 跳转至广告位列表
	 * @return 广告位列表
	 */
	public String list() {
		return "list";
	}
	
	/**
	 * 跳转至添加广告位
	 * @return 添加广告位页面
	 */
	public String add(){
		return "add";
	}
	
	/**
	 * 跳转至修改广告位页面
	 * @param ac_id 广告位Id
	 * @param adColumn 广告位
	 * @return 修改广告位页面
	 */
	public String edit(){
		adColumn = this.adColumnManager.getADcolumnDetail(ac_id);
		return "edit";
	}
	
	/**
	 * 广告位分页列表
	 * @return 广告位分页列表JSON
	 */
	public String listJson() {
		//获取广告位分页列表
		this.webpage = this.adColumnManager.pageAdvPos(this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	/**
	 * 删除广告位
	 * @param acid 广告位ID
	 * @return 广告位删除状态
	 */
	public String delete(){
		//是否为演示站点
		if(EopSetting.IS_DEMO_SITE){
			for(Integer id:acid){
				if(id<=21){
					this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
					return JSON_MESSAGE;
				}
			}
		}
		
		//删除广告位
		try {
			this.adColumnManager.delAdcs(acid);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.logger.error("删除广告位出错",e);
			this.showErrorJson("删除失败"+e.getMessage());
		}
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 添加广告位
	 * @param adColumn 广告位
	 * @return 添加状态
	 */
	public String addSave() {
		try {
			this.adColumnManager.addAdvc(adColumn);
			this.showSuccessJson("广告位添加成功");
		} catch (Exception e) {
			this.logger.error("添加广告位出错",e);
			this.showErrorJson("添加失败"+e.getMessage());
		}
		return JSON_MESSAGE;
		
	}
	
	/**
	 * 修改广告位
	 * @param adColumn 广告位
	 * @return 修改广告位状态
	 */
	public String editSave(){
		try {
			this.adColumnManager.updateAdvc(adColumn);
			this.showSuccessJson("修改广告位成功");
		} catch (Exception e) {
			this.logger.error("修改广告位出错",e);
			this.showErrorJson("修改失败"+e.getMessage());
		}
		return JSON_MESSAGE;
	}

	public IAdColumnManager getAdColumnManager() {
		return adColumnManager;
	}

	public void setAdColumnManager(IAdColumnManager adColumnManager) {
		this.adColumnManager = adColumnManager;
	}

	public AdColumn getAdColumn() {
		return adColumn;
	}

	public void setAdColumn(AdColumn adColumn) {
		this.adColumn = adColumn;
	}

	public Long getAc_id() {
		return ac_id;
	}
	
	public void setAc_id(Long ac_id) {
		this.ac_id = ac_id;
	}
	
	public Integer[] getAcid() {
		return acid;
	}
	
	public void setAcid(Integer[] acid) {
		this.acid = acid;
	}
}
