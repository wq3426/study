package com.enation.app.cms.core.tag;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.cms.core.service.IDataManager;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;


/**
 * 页面热门关键字搜索
 * 
 * @author whj
 * @version v1.0,   2015-12-18
 * @since trunk
 */
@Component
@Scope("prototype")
public class HotKeyWordSearchTag extends BaseFreeMarkerTag{
	
	private IDataManager dataManager;
	private Integer catid;
	
	/**
	 *  文章列表标签
	 *  @param catid:文章分类ID
	 *  @return 该ID下的文章列表
	 */
	
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Integer catid = (Integer)params.get("catid");
		return dataManager.list(catid);
	}

	
	public IDataManager getDataManager() {
		return dataManager;
	}

	public void setDataManager(IDataManager dataManager) {
		this.dataManager = dataManager;
	}

	public Integer getCatid() {
		return catid;
	}

	public void setCatid(Integer catid) {
		this.catid = catid;
	}

	
}
