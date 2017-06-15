package com.enation.app.b2b2c.core.tag.store;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
import net.sf.json.JSONObject;

/**
 * @Description 获取店铺车型保养项目列表
 *
 * @createTime 2016年9月30日 上午11:22:44
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
@Component
public class StoreCarRepairPriceEditListTag extends BaseFreeMarkerTag{
	private IStoreManager storeManager;
	private IStoreMemberManager storeMemberManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Map map = new HashMap();
		
		try {
			HttpServletRequest request=ThreadContextHolder.getHttpRequest();
			//session中获取会员信息,判断用户是否登陆
			StoreMember member=storeMemberManager.getStoreMember();
			if(member==null){
				HttpServletResponse response= ThreadContextHolder.getHttpResponse();
				try {
					response.sendRedirect("login.html");
				} catch (IOException e) {
					throw new UrlNotFoundException();
				}
			}
			
			String store_id = request.getParameter("store_id");
			String carmodel_id = request.getParameter("carmodel_id");
			
			map.put("store_id", store_id);
			map.put("carmodel_id", carmodel_id);
			
			JSONObject resultObj = storeManager.storeCarRepairPriceEditList(map);
			
			map.put("result", resultObj);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return map;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
