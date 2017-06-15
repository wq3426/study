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

/**
 * @Description 获取店铺车型列表
 *
 * @createTime 2016年9月1日 下午7:05:12
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
@Component
public class StoreCarModelListTag extends BaseFreeMarkerTag{
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
			int pageSize = 10; 
			String page = request.getParameter("page") == null ? "1" : request.getParameter("page");
			String car_series = request.getParameter("car_series");
			String car_nk = request.getParameter("car_nk");
			String car_sales_name = request.getParameter("car_sales_name");
			
			map.put("car_series", car_series);
			map.put("car_nk", car_nk);
			map.put("car_sales_name", car_sales_name);
			
			Page pageObj = storeManager.storeCarModelList(Integer.parseInt(page), pageSize, ((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id(), map);
			
			pageObj.setCurrentPageNo(Integer.valueOf(page));
			map.put("pageList", pageObj);
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
