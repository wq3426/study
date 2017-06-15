package com.enation.app.base.core.action;

import java.util.List;
import com.enation.eop.resource.IAdminThemeManager;
import com.enation.eop.resource.IMenuManager;
import com.enation.eop.resource.model.AdminTheme;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;

/**
 * 后台界面Action
 * @author kingapex
 * @author kanon 2015-9-24 version 1.1 添加注释
 * 
 */
@SuppressWarnings("serial")
public class BackendUiAction extends WWAction {

	

	private String theme;
	private EopSite site;
	private String version;
	private AdminUser user;
	private List menuList;
	private String ctx;
	private String product_type;
	
	private IAdminThemeManager adminThemeManager;
	private IMenuManager menuManager;

	/**
	 * 后台登陆界面
	 * 
	 * @return 后台登陆界面
	 */
	public String login() {
		// 存放站点信息
		putCommonData();
		return "login_page";
	}

	/**
	 * 跳转至后台主页面
	 * @param user 后台管理员
	 * @param version 版本
	 * @param product_type 程序模型：b2c、b2b2c、o2o
	 * @param ctx 虚拟目录
	 * @return 后台主页面
	 */
	public String main() {
		user = UserConext.getCurrentAdminUser();
		// 存放站点信息
		putCommonData();
		version = EopSetting.VERSION;
		product_type = EopSetting.PRODUCT;

		// 判断当前管理员是否为超级管理员, 获取权限点列表
		if (user.getFounder() != 1) {
			this.menuList = this.menuManager.newMenutree(0, user);
		} else {
			this.menuList = this.menuManager.getMenuTree(0);
		}
		// 获取虚拟目录
		this.ctx = this.getRequest().getContextPath();
		if ("/".equals(ctx)) {
			ctx = "";
		}

		return "main_page";
	}

	/**
	 * 存放站点信息
	 * @param site 站点信息
	 */
	private void putCommonData() {
		site = EopSite.getInstance().getInstance();
		// 读取后台使用的模板
		AdminTheme theTheme = adminThemeManager.get(site.getAdminthemeid());
		theme = "default";
		if (theTheme != null) {
			theme = theTheme.getPath();
		}
		// 获取虚拟目录
		this.ctx = this.getRequest().getContextPath();
		if ("/".equals(ctx)) {
			ctx = "";
		}
	}

	public IAdminThemeManager getAdminThemeManager() {
		return adminThemeManager;
	}

	public void setAdminThemeManager(IAdminThemeManager adminThemeManager) {
		this.adminThemeManager = adminThemeManager;
	}

	public IMenuManager getMenuManager() {
		return menuManager;
	}

	public void setMenuManager(IMenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public EopSite getSite() {
		return site;
	}

	public void setSite(EopSite site) {
		this.site = site;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public AdminUser getUser() {
		return user;
	}

	public void setUser(AdminUser user) {
		this.user = user;
	}

	public List getMenuList() {
		return menuList;
	}

	public void setMenuList(List menuList) {
		this.menuList = menuList;
	}

	public String getCtx() {
		return ctx;
	}

	public void setCtx(String ctx) {
		this.ctx = ctx;
	}

	public String getProduct_type() {
		return product_type;
	}

	public void setProduct_type(String product_type) {
		this.product_type = product_type;
	}

}
