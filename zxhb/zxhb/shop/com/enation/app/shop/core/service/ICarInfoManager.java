package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.base.core.model.CarInfoVin;
import com.enation.app.shop.core.model.CarInfo;
import com.enation.app.shop.core.model.CarModel;
import com.enation.app.shop.core.model.TrafficRestriction;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 汽车信息管理
 * @author linsen 2016年4月5日
 *
 */
public interface ICarInfoManager {
	/**
	 * 获取预计保费及最大收益
	 * @return
	 */
	public String getEstimatedInsureFeeAndMaxGain(int carModelID, String carUseCharacter, long carFirstBuyTime, String InsureSet);
	
	/**
	 * 获取已经绑定的设备列表
	 * @param carowner 
	 * @return
	 */
	public List queryBondedObds(List<String> obds, String carowner);
	
	/**
	 * 获取当前用户下的汽车信息列表
	 * @return
	 */
	JSONArray getCarInfoList(String owner);
	
	/**
	 * 根据车牌获取当前用户下的汽车信息
	 * @return
	 */
	List getCarInfoByCarplate(String carPlate);
	
	/**
	 * 如果需要添加的车辆已在当前用户下，则更新车辆信息；如果是一辆新车，则对当前用户添加一辆新车
	 * @return
	 */
	JSONArray updateCarInfo(String carInfoJson);

	/**
	 * 变更车辆的保险套餐、保养套餐
	 * @param carPlate
	 * @param insuranceId
	 * @param repair4sStoreId
	 * @param insureestimatedmaxgain  预估保费
	 * @param insureestimatedfee  最高收益
	 * @return
	 */
	public JSONObject updateInsureAndRepair4sInfo(String carPlate, String insuranceId, String repair4sStoreId, String insureestimatedfee, String insureestimatedmaxgain);

	/**
	 * 解除车辆obd绑定
	 * @param carplate
	 * @return
	 */
	public boolean unboundObdByCarplate(String car_id);

	/**
	 * 更新车辆保养信息
	 * @param carPlate
	 * @param repairlastmile
	 * @param repairlasttime
	 * @param repairlastprice
	 * @param repair_source
	 * @return
	 */
	public boolean updateCarRepairInfo(String carplate, String repairlastmile, String repairlasttime, String repairlastprice, String repair_source);

	/**
	 * 获取车辆信息
	 * @param upperCase
	 * @return
	 */
	public JSONArray getCarInfoMobile(String carplate);

	/**
	 * @description 获取车辆保养手册
	 * @date 2016年9月2日 下午9:10:48
	 * @param carplate
	 * @return
	 */
	public JSONObject getCarRepairManual(String carplate);

	/**
	 * @description 添加/修改车辆信息
	 * @date 2016年9月14日 上午11:30:37
	 * @param carInfo
	 * @return
	 */
	public JSONArray inputCarInfo(String carInfo);

	/**
	 * @description 获取车辆在签约店铺的保养币
	 * @date 2016年9月27日 下午4:42:23
	 * @param store_id
	 * @param carplate
	 * @return
	 */
	public long getCarRepairCoin(Integer store_id, String carplate);
	
	/**
	 * @description 查询vin车辆信息
	 * @date 2016年9月22日 下午3:18:05
	 * @param vin
	 * @return	CarInfoVin
	 */
	public CarInfoVin queryCarInfoVin(String vin);

	/**
	 * @description 保存vin车辆信息
	 * @date 2016年9月22日 下午3:31:09
	 * @param carInfoVin
	 * @return int
	 */
	public int saveCarInfoVin(CarInfoVin carInfoVin);

	/**
	 * @description 获取车辆收益使用历史记录
	 * @date 2016年9月23日 上午10:07:40
	 * @param carplate    车牌号
	 * @param pageSize    分页  每页显示记录条数
	 * @param startIndex  起始记录数
	 * @return
	 */
	public JSONObject getCarHodometerIncomeHistory(String carplate, int startIndex, String pageSize);

	/**
	 * @description 获取车辆保养币使用历史记录
	 * @date 2016年9月24日 下午1:16:53
	 * @param carplate    车牌号
	 * @param store_id 
	 * @param startIndex  分页  每页显示记录条数
	 * @param pageSize    起始记录数
	 * @return
	 */
	public JSONObject getCarRepairCoinHistory(String carplate, String store_id, int startIndex, String pageSize);

	/**
	 * @description 获取vin解析的车型数据，匹配数据库，如果能匹配到，更新车型表数据，再返回更新后的车型信息
	 * @date 2016年9月24日 上午11:39:36
	 * @param carInfoVin
	 * @return
	 */
	public CarModel getCarmodelInfo(CarInfoVin carInfoVin);

	/**
	 * @description 更新carInfoVin的carModelId
	 * @date 2016年9月24日 下午12:46:19
	 * @param carInfoVinId
	 * @param carModelId
	 */
	public void updateCarInfoVin(int carInfoVinId, int carModelId);

	/**
	 * @description 获取用户签约4s店列表
	 * @date 2016年9月24日 下午3:24:08
	 * @param carplate
	 * @return
	 */
	public JSONObject getCarSignStoreList(String carplate);

	/** @description 获取第三方限行信息
	 * @date 2016年9月26日 下午5:42:40
	 * @param city
	 * @param limit_date
	 * @return
	 * @return TrafficRestriction
	 */
	public TrafficRestriction getTrafficRestriction(String city, String limit_date);

	/** @description 添加车辆限制到表中
	 * @date 2016年9月26日 下午8:04:13
	 * @return void
	 */
	public void addTrafficRestriction(TrafficRestriction trafficRestriction);

	/**
	 * @description 更新车辆签约信息
	 * @date 2016年9月27日 下午3:35:50
	 * @param carplate
	 * @param repair4sstoreid
	 * @param repairlastmile
	 * @param repairlasttime
	 * @return
	 */
	public JSONObject updateContractRepair4sStoreInfo(String carplate, String repair4sstoreid, String repairlastmile, String repairlasttime);

	/** @description 判断该device_id是否绑定车辆
	 * @date 2016年9月29日 下午6:44:51
	 * @param device_id
	 * @return void
	 */
	public int getDeviceIsBanding(String device_id);

	/**
	 * @description obd绑定校验
	 * @date 2016年10月13日 下午12:10:34
	 * @param obdmacaddr
	 * @return
	 */
	public JSONObject verifyObdMacAddr(String obdmacaddr);

	/**
	 * @description 获取车辆位置信息  经纬度坐标  时间 车辆品牌图片 品牌  车系
	 * @date 2016年10月14日 下午3:21:44
	 * @param carplate
	 * @return
	 */
	public JSONObject getCarPositionInfo(String carplate) throws Exception;
	
	/**
	 * @description  获取车辆错误信息
	 * @date 2016年10月21日 下午2:47:46
	 * @param carplate
	 * @return
	 * @return JSONObject
	 */
	public JSONObject getCarErrorCodeInfo(String carplate);

	
}