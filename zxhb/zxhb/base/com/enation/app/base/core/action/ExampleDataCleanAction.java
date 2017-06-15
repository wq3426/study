
package com.enation.app.base.core.action;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.base.core.service.IExampleDataCleanManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;

/**
 * 
 * 示例数据清除action
 * @author kingapex
 *2015-6-2
 * @author Kanon 2015-9-30 version 1.1 添加注释
 */

@ParentPackage("eop_default")
@Namespace("/core/admin")
@Results({
	@Result(name="input", type="freemarker", location="/core/admin/data/clean.html")
})
public class ExampleDataCleanAction extends WWAction {
	
	private IExampleDataCleanManager exampleDataCleanManager;
	private String[] moudels;
	
	/**
	 * 跳转至清除演示数据页面
	 * @return 清除演示数据页面
	 */
	public String execute(){
		return this.INPUT;
	}
	
	/**
	 * 清除演示数据
	 * @param EopSetting.IS_DEMO_SITE 是否为演示站点
	 * @param moudels 清除表
	 * @return 清除状态
	 */
	public String clean(){
		try {
			//判断是否为演示站点
			if(EopSetting.IS_DEMO_SITE){
				this.showErrorJson(EopSetting.DEMO_SITE_TIP);
				return this.JSON_MESSAGE;
			}
			//清除演示数据
			this.exampleDataCleanManager.clean(moudels);
			this.showSuccessJson("清除成功");
		} catch (Exception e) {
			this.logger.error("清除失败", e);
			this.showErrorJson("清除失败");
		}
		return this.JSON_MESSAGE;
	}

	public IExampleDataCleanManager getExampleDataCleanManager() {
		return exampleDataCleanManager;
	}

	public void setExampleDataCleanManager(
			IExampleDataCleanManager exampleDataCleanManager) {
		this.exampleDataCleanManager = exampleDataCleanManager;
	}

	public String[] getMoudels() {
		return moudels;
	}

	public void setMoudels(String[] moudels) {
		this.moudels = moudels;
	}
	
	
	
}
