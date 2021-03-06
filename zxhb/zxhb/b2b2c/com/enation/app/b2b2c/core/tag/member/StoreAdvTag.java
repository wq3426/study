package com.enation.app.b2b2c.core.tag.member;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.app.base.core.service.ISiteMenuManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 商家广告信息tag
 * @author Sylow
 * @version v1.0, 2016年2月28日
 * @since v5.2
 */
@Component
@Scope("prototype")
public class StoreAdvTag extends BaseFreeMarkerTag {
	private ISiteMenuManager siteMenuManager;
	private IAdvManager advManager;
	/**
	 * @param parentid 上一级菜单id 选填  默认为0
	 * @return list 菜单列表
	 */
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		List<Adv> advs = new ArrayList<Adv>();
		int aId= Integer.parseInt(params.get("aid").toString());
		advs = this.advManager.getAdvDetail(aId);
		return (advs == null) ? new ArrayList<Adv>() : advs;
	}
	public ISiteMenuManager getSiteMenuManager() {
		return siteMenuManager;
	}
	public void setSiteMenuManager(ISiteMenuManager siteMenuManager) {
		this.siteMenuManager = siteMenuManager;
	}
	public IAdvManager getAdvManager() {
		return advManager;
	}
	public void setAdvManager(IAdvManager advManager) {
		this.advManager = advManager;
	}

}
