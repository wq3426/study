/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：地区api
 * 修改人：Sylow  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 地区api
 * @author Sylow
 * @version v1.0 , 2015-08-24
 * @since v1.0
 */
@Component("mobileRegionApiAction")
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("region")
public class RegionApiAction extends WWAction {

    private IRegionsManager regionsManager;

    /**
     * 省级单位列表
     * @return
     */
    public String listProvince(){
    	
    	try {
    		List list = new ArrayList();
    		list = regionsManager.listProvince();
    		this.json = JsonMessageUtil.getListJson(list);
    	} catch(RuntimeException e) {
    		this.logger.error("获取省级地区列表出错", e);
			this.showErrorJson("获取省级地区列表出错[" + e.getMessage() + "]");
    	}
    	
    	return WWAction.JSON_MESSAGE;
    }
    
    /**
     * 根据parentid获取地区列表
     * @return
     */
    public String list(){
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			int parentId = NumberUtils.toInt(request.getParameter("parentid"));
			List list =regionsManager.listChildrenByid(parentId);
			this.json = JsonMessageUtil.getListJson(list);

		} catch (RuntimeException e) {
			this.logger.error("获取地区列表出错", e);
			this.showErrorJson("获取地区列表出错[" + e.getMessage() + "]");
		}

		return WWAction.JSON_MESSAGE;
    }

    public IRegionsManager getRegionsManager() {
        return regionsManager;
    }

    public void setRegionsManager(IRegionsManager regionsManager) {
        this.regionsManager = regionsManager;
    }
}
