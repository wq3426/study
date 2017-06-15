package com.enation.app.shop.core.service;

import java.util.Map;

import com.enation.app.shop.core.model.C4sModel;
import com.enation.framework.database.Page;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 4s店信息管理
 * @author wangqiang 2016年4月1日 下午7:55:28
 *
 */
public interface I4sStoresManager {
	
	/**
	 * @description 获取4s店信息分页
	 * @date 2016年9月27日 下午8:28:57
	 * @param pageNo
	 * @param pageSize
	 * @param carmodel_id
	 * @param store_cityid
	 * @param repair4sstoreid
	 * @return
	 */
	Page get4sStoresInfoPage(int pageNo,int pageSize, String carmodel_id,String store_cityid,String repair4sstoreid);
	/**
	 * 根据4s店id获取4s店实体类
	 * @param repair4sstoreid
	 * @return
	 */
	C4sModel get4sStoresById(int repair4sstoreid);

	/**
	 * 根据车牌号获取车辆签约和非签约的4s店信息(分页)
	 * @param i 
	 * @param carplate
	 * @param repair4sstoreid 
	 * @param area 
	 * @return
	 */
	JSONObject getRepairInfoByCar(int i, String carplate, String store_cityid, String repair4sstoreid);


	/**
	 * 获取4S店实体
	 * @param repair4sstoreid
	 * @return
	 */
	JSONArray  get4sStoreById(int repair4sstoreid);

	Page listComment(int page,int pageSize,int store_id);
	
	/**
	 * 获取服务评分
	 * @param store_id
	 * @return
	 */
	public Map get4sGrades(int store_id);
	
	/**
	 * @description 版本0.3  保养服务店铺列表
	 * @date 2016年9月2日 下午3:27:57
	 * @param parseInt
	 * @param carplate
	 * @param store_cityid
	 * @param repair4sstoreid
	 * @return
	 */
	JSONObject getCarRepairStoreList(int pageNo, String carplate, String store_cityid, String repair4sstoreid);
	
	/**
	 * @description 获取店铺保养服务详情
	 * @date 2016年9月2日 下午5:34:50
	 * @param store_id
	 * @param carplate
	 * @return
	 */
	JSONObject getStoreRepairDetail(String store_id, String carplate, String order_time);
	
}
