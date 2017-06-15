/**
 * 版权：Copyright (C) 2016  中安信博（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：设备api  
 * 修改人：
 * 修改时间：2016-03-14
 * 修改内容: 
 */
package com.enation.app.car.mobile.action.device;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.core.model.Attribute;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.Specification;
import com.enation.app.shop.core.model.support.ParamGroup;
import com.enation.app.shop.core.service.GoodsTypeUtil;
import com.enation.app.shop.core.service.IDeviceManager;
import com.enation.app.shop.core.service.IFavoriteManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IGoodsSearchManager;
import com.enation.app.shop.core.service.IGoodsTypeManager;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.app.shop.mobile.model.ApiGoods;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.TestUtil;
import org.apache.log4j.Logger;

import org.json.JSONObject;
import org.json.JSONArray;
import java.io.BufferedReader;

/**
 * 商品api
 * 
 * @author 
 * @date 2016-03-14
 * @version v0.1
 * @since 
 */
//curl -H 'Content-Type:application/json' -X PUT -d '[{"timestamp":"20160311"}, {"carNo": "888888"}]' 'http://localhost:8080/api/mobile/device!updateJSONArray.do'

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("device")
@SuppressWarnings("serial")
public class DeviceApiAction extends WWAction {	
	private IDeviceManager deviceManager;
	
	@SuppressWarnings("rawtypes")
	public String updateDevice() {
		try {
			HttpServletRequest request = getRequest();
			String dev_addr = (String) request.getParameter("dev_addr");
			String dev_alias = (String) request.getParameter("dev_alias");
			String dev_isBinded =  request.getParameter("dev_isBinded");
			String user_id =  request.getParameter("user_id");
			String car_no = (String) request.getParameter("car_no");
			
			Map<String, Comparable> deviceMap = new HashMap<String, Comparable>();
			if(dev_addr != null) 
			{
				deviceMap.put("dev_addr", dev_addr);
				if(dev_alias != null)
					deviceMap.put("dev_alias", dev_alias);
				if(dev_isBinded != null)
					deviceMap.put("dev_isBinded", Integer.parseInt(dev_isBinded));
				if(user_id != null)
					deviceMap.put("user_id", Integer.parseInt(user_id));
				if(car_no != null)
					deviceMap.put("car_no", car_no);
			
				String result = deviceManager.updateDevice(deviceMap);
				this.logger.debug(result);
				JSONObject json = new JSONObject(deviceMap);
				this.json = json.toString();
				this.logger.debug("json results: " + this.json);
			}
		} catch (RuntimeException e) {
			this.logger.error("updateParams happens errors", e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	

	@SuppressWarnings("rawtypes")
	public String queryDevices() {
		String results = null;
		try {
			HttpServletRequest request = getRequest();
			String devices = (String) request.getParameter("devices");
			this.logger.debug("devices: " + devices);
			if (devices != null) {
				List<String> devList = new ArrayList<String>();
				String[] tmp = devices.split(",");
				for(int i = 0; i < tmp.length; i++) {
					devList.add(tmp[i]);
				}
				this.logger.debug("devices: " + devList.toString());
				results = deviceManager.getDevicesStatus(devList);
				this.logger.debug("results: " + results);
			}
		} catch (RuntimeException e) {
			this.logger.error("updateJSONArray has errors", e);
			this.showErrorJson(e.getMessage());
		}
		this.json = results;
		return WWAction.JSON_MESSAGE;
	}
	
	public String getParams() {
		JSONObject obj0 = new JSONObject();
		obj0.put("name", "Jack");
		obj0.put("carNo", "123456");
		this.json = obj0.toString();
		this.logger.debug("getJSON: " + this.json);
		return WWAction.JSON_MESSAGE;
	}
	
	public void getTmp() {
		//List list = goodsManager.queryDrivingRecord(userid, timestamp);
		//this.json = JsonMessageUtil.getListJson(list);
	}
	
	public String updateJSONArray() {
		String drivingRecords = null;
		try {
			HttpServletRequest request = getRequest();
			try{
				drivingRecords = getBody(request);
				logger.error("addDrivingRecord:" + drivingRecords);
			} catch (IOException ex) {
				
			}
			
			if (drivingRecords != null) {
				JSONArray jsonArray = new JSONArray(drivingRecords);
				for(int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					if(jsonObject.has("timestamp"))
						this.logger.debug("updateJSONArray-->timestamp:" + jsonObject.getString("timestamp"));
					if(jsonObject.has("carNo"))
						this.logger.debug("updateJSONArray-->carNo: " + jsonObject.getString("carNo"));
					
				}
				JSONObject object = new JSONObject();
				object.put("totalRecords", jsonArray.length());
			
				this.json = drivingRecords;
				this.logger.error("json: " + this.json);
			}

		} catch (RuntimeException e) {
			this.logger.error("updateJSONArray has errors", e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	public String getJSONArray() {
		JSONArray jsonArray = new JSONArray();
		JSONObject obj0 = new JSONObject();
		obj0.put("name", "Jack");
		jsonArray.put(0, obj0);
		JSONObject obj1 = new JSONObject();
		obj1.put("no", "123456");
		jsonArray.put(1, obj1);
		this.json = jsonArray.toString();
		
		this.logger.debug("getJSONArray: " + this.json);
		return WWAction.JSON_MESSAGE;
	}

	public IDeviceManager getDeviceManager() {
		return deviceManager;
	}

	public void setDeviceManager(IDeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}
}
