package com.enation.app.b2b2c.core.service.store;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.RepairTimeRegion;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.Store;
import com.enation.app.base.core.model.StoreAudit;
import com.enation.framework.database.Page;

import net.sf.json.JSONObject;


/**
 * 店铺管理类
 * @author LiFenLong
 *2014-9-11
 */
public interface IStoreManager {
	/**
	 * 申请店铺
	 * @param store
	 */
	public void apply(Store store);
	/**
	 * 店铺审核
	 * @param member_id 会员ID
	 * @param storeId 店铺ID
	 * @param pass	是否通过：1，通过 0未通过
	 * @param commission 平台佣金
	 * @param name_auth 店主认证
	 * @param store_auth 店铺认证
	 */
	public void audit_pass(Integer member_id,Integer storeId,Integer pass,Integer name_auth,Integer store_auth,Double commission);
	
	/**
	 * 店铺列表
	 * @param other
	 * @param disabled
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page store_list(Map other,Integer disabled,int pageNo,int pageSize);
	/**
	 * 禁用店铺
	 * @param storeId
	 */
	public void disStore(Integer storeId);
	/**
	 * 恢复店铺使用
	 * @param storeId
	 */
	public void useStore(Integer storeId);
	/**
	 * 获取一个店铺
	 * @param storeId
	 * @return Store
	 */
	public Store getStore(Integer storeId);
	/**
	 * 检查店铺名称是否重复
	 * @param storeName
	 * @return boolean
	 */
	public boolean checkStoreName(String storeName);
	/**
	 * 修改店铺信息
	 * @param store
	 */
	public void editStore(Store store);
	/**
	 * 修改店铺信息-后台使用
	 * @param store
	 */
	public void editStoreInfo(Store store);
	/**
	 * 修改店铺信息
	 * @param store
	 */
	public void editStore(Map store);
	/**
	 *	检查会员是否已经申请了店铺
	 * @return
	 */
	public boolean checkStore();
	
	/**
	 * 新增店铺
	 * @param store
	 */
	public void save(Store store);
	
	/***
	 * 检查身份证信息
	 * @author LiFenLong
	 * @param idNumber
	 */
	public Integer checkIdNumber(String idNumber);
	/**
	 * 修改店铺中的某一个值
	 * @param key
	 * @param value
	 */
	public void editStoreOnekey(String key,String value);
	
	/**
	 * 增加收藏数量
	 * @param storeid
	 */
	public void addcollectNum(Integer storeid);
	
	/**
	 * 减少收藏数量
	 * @param storeid
	 */
	public void reduceCollectNum(Integer storeid);
	
	/**
	 * 审核执照
	 * @author LiFenLong
	 * @param storeid
	 * @param id_img
	 * @param license_img
	 * @param store_auth
	 * @param name_auth
	 */
	public void saveStoreLicense(Integer storeid,String id_img,String license_img,Integer store_auth,Integer name_auth);
	/**
	 * 审核信息列表
	 * @author LiFenLong
	 * @param other
	 * @param disabled
	 * @param pageNo
	 * @param pageSize
	 * @return 店铺待审核信息列表
	 */
	public Page auth_list(Map other,Integer disabled,int pageNo,int pageSize);
	/**
	 * 审核认证新
	 * @param store_id
	 * @param name_auth
	 * @param store_auth
	 */
	public void auth_pass(Integer store_id,Integer name_auth,Integer store_auth);
	/**
	 * 根据会员Id获取店铺
	 * @param memberId
	 * @return
	 */
	public Store getStoreByMember(Integer memberId);
	/**
	 * 重新申请店铺
	 * @param store
	 */
	public void reApply(Store store);
	
	/**
	 * 后台注册店铺
	 * @param store 店铺对象
	 * @param member 会员对象
	 */
	public void registStore(Store store, Member member);
	
	
	/**根据车牌获取签约4s信息
	 * @param carplate
	 * @return 
	 */
	
	public Store getSignStore(String carplate);
	
	/**
	 * 设置店铺金额初始值
	 * @param store
	 */
	public void addStoreBalance(Store store);
	
	
	
	/**
	 * 修改店铺金额，提现或结算并且更新本期结算单
	 * @param sn 
	 * @param balance
	 */
	public void updateBalance(String sn, double balance,Integer store_id);
	
	
	/** @description 修改店铺金额，提现或结算
	 * @date 2016年9月6日 下午2:27:35
	 * @param trade_money
	 * @param store_id
	 * @return void
	 */
	void updateBalance(double trade_money, Integer store_id);
	/**
	 * @param store_id
	 * @return
	 */
	public double getBalance(Integer store_id);
	/**
	 * @description 获取店铺保养时间段设置
	 * @date 2016年8月31日 下午5:46:10
	 * @param store_id
	 * @return
	 */
	public List getStoreCarRepairTimeList(Integer store_id);
	
	/**
	 * @description 判断添加的时间段是否存在
	 * @date 2016年8月31日 下午7:52:14
	 * @param repairTimeRegion
	 * @return
	 */
	public boolean isExistRegion(RepairTimeRegion repairTimeRegion);
	
	/**
	 * @description 添加保养时间段
	 * @date 2016年8月31日 下午7:52:22
	 * @param repairTimeRegion
	 * @return
	 */
	public JSONObject addRepairTimeRegion(RepairTimeRegion repairTimeRegion);
	
	/**
	 * @description 根据id获取保养时间段
	 * @date 2016年8月31日 下午8:33:06
	 * @param id
	 * @return
	 */
	public RepairTimeRegion getRepairTimeRegion(String id);
	
	/**
	 * @description 修改保养时间段
	 * @date 2016年10月21日 下午12:14:26
	 * @param repairTimeRegion
	 * @param repair_time_range
	 * @return
	 */
	public JSONObject editRepairTimeRegionSave(RepairTimeRegion repairTimeRegion, String repair_time_range);
	
	/**
	 * @description 删除保养时间段
	 * @date 2016年8月31日 下午8:54:03
	 * @param id
	 * @return
	 */
	public JSONObject delRepairTimeRegion(String id);
	
	/**
	 * @description 店铺车型保养价格导入
	 * @date 2016年9月1日 下午12:01:36
	 * @param data
	 * @param dataFileName
	 * @return
	 */
	public JSONObject importStoreRepairItem(File data, String dataFileName, int store_id) throws Exception;

	/**
	 * @description 获取店铺车型列表
	 * @date 2016年9月1日 下午7:12:02
	 * @param store_id
	 * @return
	 */
	public Page storeCarModelList(int page, int pageSize, Integer store_id, Map map);
	
	/**
	 * @description 删除店铺保养项
	 * @date 2016年9月2日 下午5:10:36
	 * @param id
	 * @return
	 */
	public JSONObject delStoreRepairItem(String id);
	
	/**
	 * @description 修改店铺保养项目价格
	 * @date 2016年9月6日 下午6:32:15
	 * @param item_price
	 * @param repair_price
	 * @param id
	 * @return
	 */
	public JSONObject editStoreRepairItem(String item_price, String repair_price, String id);
	
	
	/**

	 * @description 查询店铺保险公司设置信息
	 * @date 2016年9月13日 上午9:10:27
	 * @param store_id
	 * @return
	 */
	public JSONObject getStoreInsureCompanyInfo(Integer store_id);
	
	/**
	 * @description 保存店铺保险设置
	 * @date 2016年9月17日 下午1:00:47
	 * @param company_ids             合作的保险公司id集合
	 * @param insure_income_discount  可用安全奖励百分比
	 * @return
	 */
	public JSONObject updateStoreInsureCompanyInfo(int store_id, String company_ids, String insure_income_discount);
	
	/**
	 * @description 根据商铺ID查询提现密码
	 * @date 2016年9月14日 下午5:43:35
	 * @param store_id
	 * @return String
	 */
	public String queryCashingoutPasswordByMemberName(int storeId);
	
	/**
	 * @description 更新提现密码
	 * @date 2016年9月14日 下午5:49:35
	 * @param newPassword
	 * @param store_id
	 * @return int
	 */
	public int updateCashingoutPassword(String newPassword, int storeId);
	
	/**
	 * @description 查询所有未审核的店铺
	 * @date 2016年9月18日 下午2:43:09
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public Page queryStoreOfUnAudit(int pageNum, int pageSize);
	
	/**
	 * @description 更新店铺信息
	 * @date 2016年9月20日 下午3:28:02
	 * @param store
	 * @return
	 */
	public int updateStoreOfpassAudit(Store store);
	
	/**
	 * @description 保存审核结果
	 * @date 2016年9月20日 下午4:16:16
	 * @param storeAudit
	 * @return int
	 */
	public int saveStoreAuditResult(StoreAudit storeAudit);
	
	/**
	 * @description 查询品牌名称
	 * @date 2016年9月20日 下午9:22:33
	 * @param brand
	 * @return String
	 */
	public String queryBrandNameByBrandId(int brand);
	
	/**
	 * @description 更新店铺数据
	 * @date 2016年9月20日 下午9:32:32
	 * @param store
	 * @return int
	 */
	public int updateStoreInfo(Store store);
	
	/**
	 * @description 检查提现人手机号码是否已经存在
	 * @date 2016年9月21日 上午11:31:52
	 * @param mobile
	 * @return boolean
	 */
	public boolean checkCashingouterPhoneIsExist(String mobile);
	
	/**
	 * @description 检查提现人手机号码是否正确
	 * @date 2016年9月21日 下午3:29:43
	 * @param cashingouterPhone
	 * @param store_id
	 * @return boolean
	 */
	public boolean checkCashingouterPhoneIsCorrect(String cashingouterPhone, int store_id);
	
	/**
	 * @description 修改提现密码
	 * @date 2016年9月21日 下午7:51:49
	 * @param cashingouterPhone
	 * @param store_id
	 * @return int
	 */
	public int modifyCashingoutPassword(Store store);
	
	/**
	 * @description 更新店铺审核状态
	 * @date 2016年9月21日 下午8:35:32
	 * @param store_id
	 * @return int
	 */
	public int updateStoreAuditStatus(int store_id);
	
	/**
	 * @description 更新店铺状态
	 * @date 2016年9月22日 上午1:03:29
	 * @param store
	 * @return int
	 */
	public int updateStore(Store store);
	/**
	 * @description 根据店铺等级修改storeCost表
	 * @date 2016年9月26日 下午2:53:05
	 * @param store
	 * @return void
	 */
	public void editStoreUseData(Store store);
	
	/**
	 * @description 获取店铺车型保养项目价格列表
	 * @date 2016年9月30日 上午11:58:12
	 * @param map
	 * @return
	 */
	public JSONObject storeCarRepairPriceEditList(Map map);
	
	/**
	 * @description 查询店铺列表
	 * @date 2016年10月9日 上午11:43:35
	 * @param other
	 * @param disabled
	 * @param page
	 * @param pageSize
	 * @return Page
	 */
	public Page queryStoreList(Map other, int disabled, int page, int pageSize);
	
	/**
	 * @description 查询店铺品牌
	 * @date 2016年10月9日 下午6:58:54
	 * @return
	 */
	public List queryBrandList();

	/**
	 * @description 查询所有已审核店铺的Id,name
	 * @date 2016年10月26日 下午3:43:14
	 * @return
	 * @return List<net.sf.ehcache.store.Store>
	 */
	public List<Store> queryStoreList();
	
	/**
	 * @description 查询店铺审核结果
	 * @date 2016年10月26日 下午5:33:55
	 * @param store_id
	 * @return Map<String, String>
	 */
	public Map<String, String> queryStoreAuditResult(int store_id);
	
	/**
	 * @description 检查审核结果是否已经存在
	 * @date 2016年10月26日 下午6:02:33
	 * @param store_id
	 * @return boolean
	 */
	public Map<String, Integer> queryStoreAuditResultIsExist(int store_id);
	
	/**
	 * @description 更新审核结果
	 * @date 2016年10月26日 下午6:10:52
	 * @param storeAudit
	 * @return int
	 */
	public int updateStoreAuditResult(StoreAudit storeAudit);
	
	/**
	 * @description 
	 * @date 2016年10月26日 下午8:03:27
	 * @param store_id
	 * @return String
	 */
	public String queryStoreAuditReason(int store_id);

	
}
