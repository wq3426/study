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
@Namespace("/api/test")
@Action("example")
@SuppressWarnings("serial")
public class ExampleTestAction extends WWAction {
	private String username;
	private String password;
	private IDeviceManager deviceManager;
	
	/***
	 * 测试：可在浏览器地址栏输入如下地址模拟get请求进行测试
	 * http://localhost:8080/mall/api/test/example!login.do?username=123456789&password=1234455
	 * http://localhost:8080/mall/api/test/example!login2.do?username=123456789&password=1234455
	 * http://localhost:8080/mall/api/test/example!returnJson.do
	 */
	
	/**
	 * 参数获取方式一：通过框架struts2本身的接收参数功能可直接在类中使用已经定义的和页面参数同名的成员变量，
	 *         但此功能需要为类的属性(如username)提供getter和setter方法，便于框架调用为同名成员变量赋值
	 * @return
	 */
	public String login(){
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 参数获取方式二：通过servlet的request对象的方法直接获取请求参数
	 * @return
	 */
	public String login2(){
		String uname = this.getRequest().getParameter("username");
		String passwd = this.getRequest().getParameter("password");
		return this.JSON_MESSAGE;
	}
		
	/**
	 * 给请求返回JSON对象作为响应
	 * @return
	 */
	public void returnJson(){
		JSONObject obj = new JSONObject();
		obj.put("username", "wq");
		obj.put("password", "123456");
		this.render(obj.toString(), "text/x-json;charset=UTF-8");
	}
	
	/**
	 * 添加/修改方法
	 * @return
	 */
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

	/**
	 * 查询方法
	 * @return
	 */
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


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
}
