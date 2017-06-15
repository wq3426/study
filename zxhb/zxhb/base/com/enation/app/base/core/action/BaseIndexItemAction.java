package com.enation.app.base.core.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.resource.model.EopSite;
import com.enation.framework.action.WWAction;

/**
 * 首页项(基本)Action
 * @author kingapex
 * 2010-10-13下午05:16:45
 * @author kanon 2015-9-24 version 1.1 添加注释
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("indexItem")
@Results({
	@Result(name="base", type="freemarker", location="/core/admin/index/base.html"),
	@Result(name="access", type="freemarker", location="/core/admin/index/access.html"),
	@Result(name="point", type="freemarker", location="/core/admin/index/point.html")
})
public class BaseIndexItemAction extends WWAction {
	private EopSite site;
	
	/**
	 * 显示首页基本项信息
	 * @param site 站点基本信息
	 * @return 首页基本项信息页面
	 */
	public String base() {
		site =EopSite.getInstance();
		return "base";
	}

	public EopSite getSite() {
		return site;
	}

	public void setSite(EopSite site) {
		this.site = site;
	}
}
