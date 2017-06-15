package com.enation.app.base.core.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.SiteMenu;
import com.enation.app.base.core.service.ISiteMenuManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;


/**
 * 站点菜单
 * @author kingapex
 * @author Kanon 2015-11-16 version 1.1 添加注释
 *
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("siteMenu")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/sitemenu/menu_list.html"),
	@Result(name="input", type="freemarker", location="/core/admin/sitemenu/menu_input.html") 
})
public class SiteMenuAction extends WWAction {
	private ISiteMenuManager siteMenuManager ;
	private List menuList;
	private Integer[] sortArray;
	private Integer[] menuidArray;
	private Integer menuid;
	private SiteMenu siteMenu;
	private boolean isEdit;
	
	/**
	 * 跳转至菜单列表
	 * @return
	 */
	public String list(){
		return "list";
	}
	/**
	 * 菜单列表
	 * @param menuList 菜单列表
	 * @return
	 */
	public String listJson(){
		menuList  = siteMenuManager.list(0);
		this.showGridJson(menuList);
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 更改排序
	 * @param menuidArray 菜单ID列表
	 * @param sortArray 排序列表
	 * @return
	 */
	public String updateSort(){
		//添加非空判断，以防报错  add by DMRain 2016-1-20
		if (menuidArray != null) {
			try {
				siteMenuManager.updateSort(menuidArray, sortArray);
				this.showSuccessJson("保存排序成功");
			} catch (Exception e) {
				e.printStackTrace();
				this.showErrorJson("保存排序失败:"+e.getMessage());
			}
		} else {
			this.showErrorJson("数据为空，暂时无法排序！");
		}
		
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 添加菜单
	 * @param isEdit 是否为修改
	 * @param menuList 菜单列表
	 * @param siteMenu 菜单
	 * @return
	 */
	public String add(){
		isEdit =false;
		this.menuList = this.siteMenuManager.list(0);
		siteMenu= new SiteMenu();
		return this.INPUT;
	}
	
	/**
	 * 添加子菜单
	 * @param isEdit 是否为修改
	 * @param menuList 菜单列表
	 * @param menuid 菜单ID
	 * @param siteMenu 菜单
	 */
	public String addchildren(){
		isEdit=false;
		this.menuList = this.siteMenuManager.list(0);
		menuid =siteMenuManager.get(menuid).getMenuid();
		siteMenu= new SiteMenu();
		return this.INPUT;
	}
	
	/**
	 * 跳转修改菜单
	 * @param isEdit 是否为修改
	 * @param menuList 菜单列表
	 * @param siteMenu 菜单
	 */
	public String edit(){
		isEdit=true;
		this.menuList = this.siteMenuManager.list(0);
		siteMenu  =siteMenuManager.get(menuid);
		return this.INPUT;
	}
	/**
	 * 保存菜单
	 * @param menuid 菜单id
	 * @param siteMenu 菜单
	 * @return 添加状态
	 */
	public String save(){
		//判断是否为演示站点
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能添加这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		//判断菜单id是否为空如果为空则为添加菜单
		if(menuid==null){
			this.siteMenuManager.add(siteMenu);
			this.showSuccessJson("菜单添加成功");
		}else{
			siteMenu.setMenuid(menuid);
			this.siteMenuManager.edit(siteMenu);
			this.showSuccessJson("菜单修改成功");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 删除菜单
	 * @param menuid 菜单Id
	 * @return 删除状态
	 */
	public String delete(){
		//判断是否为演示站点
		if(EopSetting.IS_DEMO_SITE){
			if(menuid<=21){
				this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
				return JSON_MESSAGE;
			}
		}
		//删除菜单
		try {
			this.siteMenuManager.delete(menuid);
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败:"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	//get set
	public ISiteMenuManager getSiteMenuManager() {
		return siteMenuManager;
	}
	public void setSiteMenuManager(ISiteMenuManager siteMenuManager) {
		this.siteMenuManager = siteMenuManager;
	}
	public List getMenuList() {
		return menuList;
	}
	public void setMenuList(List menuList) {
		this.menuList = menuList;
	}

	public Integer[] getSortArray() {
		return sortArray;
	}

	public void setSortArray(Integer[] sortArray) {
		this.sortArray = sortArray;
	}

	public Integer[] getMenuidArray() {
		return menuidArray;
	}

	public void setMenuidArray(Integer[] menuidArray) {
		this.menuidArray = menuidArray;
	}

	public Integer getMenuid() {
		return menuid;
	}

	public void setMenuid(Integer menuid) {
		this.menuid = menuid;
	}

	public SiteMenu getSiteMenu() {
		return siteMenu;
	}

	public void setSiteMenu(SiteMenu siteMenu) {
		this.siteMenu = siteMenu;
	}

	public boolean getIsEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}
	
}
