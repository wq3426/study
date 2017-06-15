package com.enation.app.base.core.action.api;

import java.util.List;

import net.sf.json.JSONArray;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IRegionsManager;
import com.enation.framework.action.WWAction;
/**
 * 地区API
 * @author lina 2014-2-21 version 1.0
 * @author kanon 2015-9-22 version 1.1 添加注释
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/base")
@Action("region")
public class RegionApiAction extends WWAction {
	private IRegionsManager regionsManager;
	private Integer regionid;
	/**
	 * 获取该地区的子
	 * @param regionid 地区Id int型
	 * @return 地区子列表JSON
	 */
	public String getChildren(){
		//判断地区ID是否为空
		if(regionid==null){
			this.showErrorJson("缺少参数：regionid");
		}else{
			//获取地区子列表
			List list =regionsManager.listChildrenByid(regionid);
			this.json=JSONArray.fromObject(list).toString();
		}
		return this.JSON_MESSAGE;
	}
	
	public String get4sStoreRegion(){
		try {
			List list = regionsManager.get4sStoreRegion();
			if(list.size() > 0){
				this.json = "{\"result\":1,\"data\":"+JSONArray.fromObject(list).toString()+"}";
			}else{
				this.json = "{\"result\":0,\"message\":"+"没有获取到4s店所在区域"+"}";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}
	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}
	public Integer getRegionid() {
		return regionid;
	}
	public void setRegionid(Integer regionid) {
		this.regionid = regionid;
	}


}
