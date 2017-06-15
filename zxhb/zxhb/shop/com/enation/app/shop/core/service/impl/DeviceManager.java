package com.enation.app.shop.core.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.GoodsStores;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.support.GoodsEditDTO;
import com.enation.app.shop.core.plugin.goods.GoodsDataFilterBundle;
import com.enation.app.shop.core.plugin.goods.GoodsPluginBundle;
import com.enation.app.shop.core.service.IDepotMonitorManager;
import com.enation.app.shop.core.service.IDeviceManager;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberPriceManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.app.shop.core.service.ITagManager;
import com.enation.app.shop.core.service.SnDuplicateException;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;

/**
 * Device管理
 * 
 * @author kingapex 2010-1-13下午12:07:07
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DeviceManager extends BaseSupport implements IDeviceManager {
	
	/**
	 * 读取设备的详细
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public String getDevicesStatus(List<String> devices)
	{
		List list = new ArrayList();
		try {
			String sql = "select * from " + this.getTableName("device") + " r  where ";
			for(int i = 0; i < devices.size(); i++) {
				sql += "r.dev_addr='" + devices.get(i) + "'";
				if(devices.size() > 1 && i != (devices.size() - 1)){
					sql += " or ";
				}
			}
			
			this.logger.debug("getDevicesStatus:" + sql);
			list = this.daoSupport.queryForList(sql);
		} catch (RuntimeException e) {
			if (e instanceof SnDuplicateException) {
				throw e;
			}
		}
		
		JSONArray jsonarray = JSONArray.fromObject(list);
		this.logger.debug("getDevicesStatus:" + jsonarray.toString());
		return jsonarray.toString();
	}
	
	/**
	 * 更新设备状态
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public String updateDevice(Map device)
	{
		try{
			List list = new ArrayList();
			String sql = "select * from " + this.getTableName("device") + " r";
			sql += " where r.dev_addr='" + device.get("dev_addr") + "'";
			list = this.daoSupport.queryForList(sql);
			if(list.size() == 0) 
				this.baseDaoSupport.insert("device", device);
			else
			{
				if(device.containsKey("dev_alias")){
					this.baseDaoSupport.execute("update device set " + "dev_alias" + "=? where dev_addr=?", device.get("dev_alias"), device.get("dev_addr"));
				}
				if(device.containsKey("dev_isBinded")){
					this.baseDaoSupport.execute("update device set " + "dev_isBinded" + "=? where dev_addr=?", device.get("dev_isBinded"), device.get("dev_addr"));
				}
				if(device.containsKey("user_id")){
					this.baseDaoSupport.execute("update device set " + "user_id" + "=? where dev_addr=?", device.get("user_id"), device.get("dev_addr"));
				}
				if(device.containsKey("car_no")){
					this.baseDaoSupport.execute("update device set " + "car_no" + "=? where dev_addr=?", device.get("car_no"), device.get("dev_addr"));
				}
			}
		} catch (RuntimeException e) {
			if (e instanceof SnDuplicateException) {
				throw e;
			}
			
			e.printStackTrace();
		}
		
		return device.toString();
	}
	
	/**
	 * 解除绑定
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public String deviceUnbonded(String dev_addr)
	{
		return "";
	}
	
	/**
	 * 得到用户的设备列表
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public String getDevices(int user_id)
	{
		return "";
	}
}
