package com.enation.app.base.core.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.enation.eop.resource.ISiteManager;
import com.enation.eop.resource.IThemeManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.resource.model.Theme;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 站点主题管理
 * 
 * @author lzf
 *         <p>
 *         2009-12-30 上午11:01:08
 *         </p>
 * @version 1.0
 * @author Kanon 2015-11-16 version 1.1 添加注释
 */
public class SiteThemeAction extends WWAction {

	private List<Theme> listTheme;
	private Theme theme;
	private IThemeManager themeManager;
	private EopSite eopSite;
	private ISiteManager siteManager;
	private String previewpath;
	private String previewBasePath;
	private Integer themeid;

	//2014-7-10 @author LiFenLong
	private Theme themeinfo;
	
	/**
	 * 获取站点主题列表
	 * @param ctx 虚拟目录
	 * @param site 站点信息
	 * @param themeinfo 当前模板
	 * @param listTheme 模板列表
	 * @param previewpath 模板主题图片
	 */
	public String execute() throws Exception {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String ctx = request.getContextPath();
		EopSite site  =EopSite.getInstance();
		previewBasePath = ctx+ "/themes/";

		themeinfo = themeManager.getTheme( site.getThemeid());
		listTheme = themeManager.list();
		previewpath = previewBasePath + themeinfo.getPath() + "/preview.png";
		return SUCCESS;
	}
	
	/**
	 * 跳转添加模板主题
	 * @return
	 */
	public String add(){
		return this.INPUT;
	}
	
	/**
	 * 保存模板主题
	 * @param theme 模板
	 * @return
	 */
	public String save(){
		//this.msgs.add("模板创建成功");
		//this.urls.put("模板列表", "siteTheme.do");
		try {
			this.showSuccessJson("模板创建成功");
			this.themeManager.addBlank(theme);
		} catch (Exception e) {
			this.showErrorJson("模板创建失败");
		}
		return this.MESSAGE;
	}
	
	/**
	 * 更换主题
	 * @param themeid 模板id
	 */
	public String change()throws Exception {
		themeManager.changetheme(themeid);
		return this.execute(); 
	}

	public List<Theme> getListTheme() {
		return listTheme;
	}

	public void setListTheme(List<Theme> listTheme) {
		this.listTheme = listTheme;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}

	public IThemeManager getThemeManager() {
		return themeManager;
	}

	public void setThemeManager(IThemeManager themeManager) {
		this.themeManager = themeManager;
	}

	public EopSite getEopSite() {
		return eopSite;
	}

	public void setEopSite(EopSite eopSite) {
		this.eopSite = eopSite;
	}

	public ISiteManager getSiteManager() {
		return siteManager;
	}

	public void setSiteManager(ISiteManager siteManager) {
		this.siteManager = siteManager;
	}

	public String getPreviewpath() {
		return previewpath;
	}

	public void setPreviewpath(String previewpath) {
		this.previewpath = previewpath;
	}

	public String getPreviewBasePath() {
		return previewBasePath;
	}

	public void setPreviewBasePath(String previewBasePath) {
		this.previewBasePath = previewBasePath;
	}

	public Integer getThemeid() {
		return themeid;
	}

	public void setThemeid(Integer themeid) {
		this.themeid = themeid;
	}
	public Theme getThemeinfo() {
		return themeinfo;
	}
	public void setThemeinfo(Theme themeinfo) {
		this.themeinfo = themeinfo;
	}
}
