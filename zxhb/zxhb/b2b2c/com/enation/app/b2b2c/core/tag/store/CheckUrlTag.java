package com.enation.app.b2b2c.core.tag.store;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 检测URL是否正确
 * @author xulipeng
 *2015年1月4日14:25:02
 */

@Component
public class CheckUrlTag extends BaseFreeMarkerTag {

	@Override
	protected Object exec(Map params) throws TemplateModelException {
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String show_c_str = request.getParameter("show_c");
		String store_id_str = request.getParameter("store_id");
		
		if(!show_c_str.matches("\\d+") || !store_id_str.matches("\\d+")){
			throw new UrlNotFoundException();
		}else{
			
		}
		return null;
	}

}
