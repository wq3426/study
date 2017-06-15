package com.enation.app.b2b2c.core.tag.reckoning;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeType;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

@Component
public class ReckoningTypeTag extends BaseFreeMarkerTag{
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map result = new LinkedHashMap();
		result = ReckoningTradeType.getTradeTypeMap();
		return result;
	}
	
}
