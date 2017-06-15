package com.enation.app.shop.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.GoodsStores;
import com.enation.app.shop.core.model.support.GoodsEditDTO;
import com.enation.framework.database.Page;

/**
 * 设备管理接口
 * @author kingapex
 *
 */
public interface IDeviceManager {
 
	/**
	 * 读取设备的详细
	 * @param devices
	 * @return String
	 */
	public String getDevicesStatus(List<String> devices);
	
	/**
	 * 更新设备状态
	 * @param device 商品Id
	 * @return String
	 */
	public String updateDevice(Map device);
	
	/**
	 * 解除绑定
	 * @param dev_addr
	 * @return String
	 */
	public String deviceUnbonded(String dev_addr);
	
	/**
	 * 得到用户的设备列表
	 * @param user_id
	 * @return String
	 */
	public String getDevices(int user_id);
}