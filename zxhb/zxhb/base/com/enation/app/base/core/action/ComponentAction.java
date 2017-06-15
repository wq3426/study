package com.enation.app.base.core.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.sf.json.JSONArray;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;
import com.enation.framework.component.ComponentView;
import com.enation.framework.component.IComponentManager;

/**
 * 组件管理Action
 * @author kingapex
 * @author kanon 2015-9-24 version1.1 添加注释
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("component")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/component/list.html")
})
public class ComponentAction extends WWAction {

	private IComponentManager componentManager;
	private List<ComponentView> componentList;
	private String componentid;
	/**
	 * 跳转至组件列表页面
	 * @return 组件列表页面
	 */
	public String list() {
		return "list";
	}
	
	/**
	 * 获取组件列表JSON
	 * @param componentList 组件列表
	 * @return 组件列表JSON
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String listJson() {
		componentList = this.componentManager.list();
		
		List l = new ArrayList();
		for(ComponentView view : componentList){
			Map map  = new HashMap();
			map.put("id", view.getId());
			map.put("name", view.getName());
			map.put("install_state", view.getInstall_state());
			map.put("enable_state", view.getEnable_state());
			map.put("error_message", view.getError_message());
			map.put("componentid", view.getComponentid());
			
			int size = view.getPluginList().size();
			if(size!=0){
				map.put("state", "closed");
				map.put("children", view.getPluginList());
			}
			l.add(map);
		}
		
		this.json = JSONArray.fromObject(l).toString();
		return JSON_MESSAGE;
	}

	/**
	 * 安装组件
	 * @param componentid 组件ID
	 * @return 安装状态
	 */
	public String install() {
		//检测是否为演示站点
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		//安装组件
		try {
			this.componentManager.install(componentid);
			this.showSuccessJson("安装成功");
		} catch (RuntimeException e) {
			this.logger.error("安装组件[" + componentid + "]", e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 卸载组件
	 * @param componentid 组件ID
	 * @return 卸载状态
	 */
	public String unInstall() {
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		try {
			this.componentManager.unInstall(componentid);
			this.showSuccessJson("卸载成功");
		} catch (RuntimeException e) {
			this.logger.error("卸载组件[" + componentid + "]", e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 启用组件
	 * @param componentid 组件ID
	 * @return 启动状态
	 */
	public String start() {
		
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		
		try {
			this.componentManager.start(componentid);
			this.showSuccessJson("启动成功");
		} catch (RuntimeException e) {
			this.logger.error("启动组件[" + componentid + "]", e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 停用组件
	 * @param componentid 组件ID
	 * @return 停用状态
	 */
	public String stop() {
		
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		
		try {
			this.componentManager.stop(componentid);
			this.showSuccessJson("停用成功");
		} catch (RuntimeException e) {
			this.logger.error("停用组件[" + componentid + "]", e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	public IComponentManager getComponentManager() {
		return componentManager;
	}

	public void setComponentManager(IComponentManager componentManager) {
		this.componentManager = componentManager;
	}

	public List<ComponentView> getComponentList() {
		return componentList;
	}

	public void setComponentList(List<ComponentView> componentList) {
		this.componentList = componentList;
	}

	public String getComponentid() {
		return componentid;
	}

	public void setComponentid(String componentid) {
		this.componentid = componentid;
	}

}
