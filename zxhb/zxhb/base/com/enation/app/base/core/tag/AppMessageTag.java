package com.enation.app.base.core.tag;

import java.util.HashMap;
import java.util.Map;
 
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IAppMessageManager; 
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 获取messag详细信息
 * @author chopper
 *
 */
@Component
public class AppMessageTag extends BaseFreeMarkerTag{

	private IAppMessageManager appMessageManager;
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map result=new HashMap();
		Integer amid = Integer.parseInt(params.get("amid").toString()); 
		Map appMessage=this.appMessageManager.get(amid);
		this.getRequest().setAttribute("appMessage", appMessage);
		result.put("appMessage",appMessage);
		return result;
	}
	public IAppMessageManager getAppMessageManager() {
		return appMessageManager;
	}
	public void setAppMessageManager(IAppMessageManager appMessageManager) {
		this.appMessageManager = appMessageManager;
	} 
	
	
}
