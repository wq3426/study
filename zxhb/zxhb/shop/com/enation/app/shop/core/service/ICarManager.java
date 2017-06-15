package com.enation.app.shop.core.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.model.CarModel;
import com.enation.app.shop.core.model.CarmodelRepairItem;
import com.enation.app.shop.core.model.RepairItem;
import com.enation.framework.database.Page;

import net.sf.json.JSONObject;

/**
 * @Description 车辆信息接口
 *
 * @createTime 2016年8月25日 上午11:26:28
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
public interface ICarManager {

	/**
	 * 获取汽车品牌列表
	 * @return
	 */
	List getCarBrandList();

	/**
	 * 根据汽车品牌获取车系
	 * @param brandName
	 * @return
	 */
	List getCarSeries(String brandName);

	/**
	 * 根据品牌brand、类型type、车系series获取车款
	 * @return
	 */
	List getCarModelsList(String brand, String type, String series);

	/**
	 * @description 分页查询车型列表数据
	 * @date 2016年8月22日 下午2:59:25
	 * @param keyMap   搜索关键字
	 * @param page     当前页码
	 * @param pageSize 每页显示数据条数
	 * @param sort     排序字段
	 * @param order    升序/降序
	 * @return
	 */
	Page getListByPage(Map keyMap, int page, int pageSize, String sort, String order);

	/**
	 * @description 导入excel文件数据到车型表
	 * @date 2016年8月22日 下午7:09:43
	 * @param data
	 * @param dataFileName
	 * @return
	 */
	JSONObject importExcelData(File data, String dataFileName) throws Exception;
	
	/**
	 * @description 获取车辆品牌-品牌id的map
	 * @date 2016年8月24日 下午6:18:53
	 * @return
	 */
	List<Brand> getCarBrandSelectList();

	/**
	 * @description 添加车型
	 * @date 2016年8月24日 下午9:18:55
	 * @param carmodel 车型对象
	 * @param carmodel_type 车辆类型 1 国产 2 进口
	 * @param data 车型图片
	 * @param dataFileName 车型图片名
	 * @return
	 */
	JSONObject addCarmodel(CarModel carmodel, String carmodel_type, File data, String dataFileName) throws Exception;
	
	/**
	 * @description 查询车型是否已存在
	 * @date 2016年8月25日 上午11:26:01
	 * @param carmodel
	 * @return
	 */
	boolean isExist(CarModel carmodel);

	/**
	 * @description 根据id获取车型对象
	 * @date 2016年8月25日 下午3:00:41
	 * @param id
	 * @return
	 */
	CarModel getCarmodel(String id);

	/**
	 * @description 车型信息修改保存
	 * @date 2016年8月25日 下午5:58:22
	 * @param carmodel
	 * @param carmodel_type
	 * @param data
	 * @param dataFileName
	 * @return
	 */
	JSONObject editSave(CarModel carmodel, String carmodel_type, File data, String dataFileName) throws Exception;

	/**
	 * @description 删除车型记录
	 * @date 2016年8月25日 下午8:20:26
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	JSONObject delete(Integer[] id) throws Exception;

	/**
	 * @description 获取保养项目列表
	 * @date 2016年8月26日 下午6:39:14
	 * @param keyMap
	 * @param page
	 * @param pageSize
	 * @param sort
	 * @param order
	 * @return
	 */
	Page getRepairItemListByPage(Map keyMap, int page, int pageSize, String sort, String order);

	/**
	 * @description 判断保养项目是否已经存在
	 * @date 2016年8月26日 下午8:06:20
	 * @param item_name
	 * @return
	 */
	boolean isExistRepairItem(String item_name);

	/**
	 * @description 保养项目添加
	 * @date 2016年8月26日 下午8:09:20
	 * @param item_name
	 * @param item_sort
	 * @return
	 */
	JSONObject addRepairItem(RepairItem repairItem);

	/**
	 * @description 获取要编辑的保养项目对象
	 * @date 2016年8月26日 下午8:35:07
	 * @param id
	 * @return
	 */
	RepairItem getRepairItem(String id);

	/**
	 * @description 修改保养项目
	 * @date 2016年8月26日 下午8:47:50
	 * @param repairItem
	 * @return
	 */
	JSONObject editRepairItemSave(RepairItem repairItem);

	/**
	 * @description 删除保养项目
	 * @date 2016年8月26日 下午9:14:12
	 * @param id
	 * @return
	 */
	JSONObject deleteRepairItem(Integer[] id);

	/**
	 * @description 导入车型保养项目excel表
	 * @date 2016年8月27日 下午4:51:13
	 * @param data
	 * @param dataFileName
	 * @return
	 */
	JSONObject importCarRepairItemExcelData(File data, String dataFileName);

	/**
	 * @description 车型保养项目列表
	 * @date 2016年8月30日 上午10:43:49
	 * @param keyMap
	 * @param page
	 * @param pageSize
	 * @param sort
	 * @param order
	 * @return
	 */
	Page getCarmodelRepairItemListByPage(Map keyMap, int page, int pageSize, String sort, String order);

	/**
	 * @description 获取车型保养添加页面的下拉项列表
	 * @date 2016年8月30日 下午8:59:16
	 * @param brand_id
	 * @return
	 */
	List<String> getCarmodelRelateList(String key, Object... args);

	/**
	 * @description 获取车型保养项目列表
	 * @date 2016年8月31日 上午9:14:32
	 * @return
	 */
	List<CarmodelRepairItem> getCarmodelRepairItemList();

	/**
	 * @description 判断车型保养项目是否已存在
	 * @date 2016年8月31日 上午10:33:29
	 * @param carmodelRepairItem
	 * @return
	 */
	boolean isExistCarmodelRepairItem(CarmodelRepairItem carmodelRepairItem);

	/**
	 * @description 添加车型保养项目
	 * @date 2016年8月31日 上午10:36:42
	 * @param carmodelRepairItem
	 * @return
	 */
	JSONObject addCarmodelRepairItem(CarmodelRepairItem carmodelRepairItem);

	/**
	 * @description 根据id获取车型保养项目
	 * @date 2016年8月31日 上午11:05:39
	 * @param id
	 * @return
	 */
	CarmodelRepairItem getCarmodelRepairItem(String id);

	/**
	 * @description 车型保养项目修改
	 * @date 2016年8月31日 下午2:02:58
	 * @param carmodelRepairItem
	 * @return
	 */
	JSONObject editCarmodelRepairItemSave(CarmodelRepairItem carmodelRepairItem);

	/**
	 * @description 车型保养项目删除
	 * @date 2016年8月31日 下午2:14:51
	 * @param id
	 * @return
	 */
	JSONObject deleteCarmodelRepairItem(Integer[] id);

	
}