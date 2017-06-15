package com.enation.app.base.core.action;

import com.enation.app.base.core.service.ISystemSettingManager;
import com.enation.eop.SystemSetting;
import com.enation.framework.action.WWAction;


/**
 * 系统设置action
 * @author kingapex
 * @author Kanon 2015-11-16 version 1.1 添加注释
 */
public class SystemSettingAction extends WWAction {
	
	
	private SystemSetting systemSetting;
	private ISystemSettingManager systemSettingManager;
	
	
	public String execute(){
		systemSetting = this.systemSettingManager.getSetting();
		return this.INPUT;
	}
	
	
	

	public SystemSetting getSystemSetting() {
		return systemSetting;
	}

	public void setSystemSetting(SystemSetting systemSetting) {
		this.systemSetting = systemSetting;
	}


	public ISystemSettingManager getSystemSettingManager() {
		return systemSettingManager;
	}


	public void setSystemSettingManager(ISystemSettingManager systemSettingManager) {
		this.systemSettingManager = systemSettingManager;
	}
	
	
	
	
}
