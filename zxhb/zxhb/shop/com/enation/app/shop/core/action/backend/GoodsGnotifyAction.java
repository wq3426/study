package com.enation.app.shop.core.action.backend;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IGnotifyManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("gnotify")
@Results({
	@Result(name="gnotify", type="freemarker", location="/shop/admin/gnotify/gnotify_list.html"),
})
public class GoodsGnotifyAction extends WWAction {

	private IGnotifyManager gnotifyManager;
	
	public String gnotify(){
		return "gnotify";
	}
	
	public String gnotifyJson(){
		Page page =  this.gnotifyManager.getGnotifyList(this.getPage(), this.getPageSize());
		this.showGridJson(page);
		return JSON_MESSAGE;
	}

	public IGnotifyManager getGnotifyManager() {
		return gnotifyManager;
	}

	public void setGnotifyManager(IGnotifyManager gnotifyManager) {
		this.gnotifyManager = gnotifyManager;
	}
	
	
	
	
}
