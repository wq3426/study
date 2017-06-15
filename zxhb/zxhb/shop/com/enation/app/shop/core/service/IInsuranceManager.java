package com.enation.app.shop.core.service;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.InsureCompanyModel;
import com.enation.app.shop.core.model.InsureTypeModel;
import com.enation.app.shop.core.model.InsuranceModel;
import com.enation.framework.database.Page;

import net.sf.json.JSONObject;

/**
 * 保险信息接口
 * @author wangqiang 2016年4月1日 下午7:48:26
 *
 */
public interface IInsuranceManager {

	/**
	 * 获取保险公司列表
	 * @return
	 */
	List getInsureCompanyList();
	
	/**
	 * 获取指定保险公司套餐列表
	 * @return
	 */
	List<InsuranceModel> getInsureInfoList(String company);
	
	/**
	 * 根据单个id查询保险公司对象
	 * @return
	 */
	InsuranceModel getInsureById(int insuranceId);
	
	/**
	 * 根据carinfo的保险id查询对应保险套餐
	 * @param insuranceIds
	 * @return
	 */
	List<InsuranceModel> getInsureListByIds(String[] insuranceIds);

	/**
	 * @description 保险公司列表
	 * @date 2016年9月8日 下午3:12:01
	 * @param keyMap    搜索关键字map
	 * @param page      当前页码
	 * @param pageSize  每页记录条数
	 * @param sort      排序字段
	 * @param order     排序方式
	 * @return
	 */
	Page getInsureCompanyListByPage(Map keyMap, int page, int pageSize, String sort, String order);

	/**
	 * @description 查询保险公司是否存在
	 * @date 2016年9月8日 下午5:39:12
	 * @param insureCompany
	 * @return
	 */
	boolean isExistInsureCompany(InsureCompanyModel insureCompany);

	/**
	 * @description 添加保险公司
	 * @date 2016年9月8日 下午5:42:24
	 * @param insureCompany
	 * @param data
	 * @param dataFileName
	 * @return
	 */
	JSONObject addInsureCompany(InsureCompanyModel insureCompany, File data, String dataFileName) throws Exception;

	/**
	 * @description 获取保险公司信息
	 * @date 2016年9月8日 下午6:34:46
	 * @param valueOf
	 * @return
	 */
	InsureCompanyModel getInsureCompany(Integer id);

	/**
	 * @description 修改保险公司信息
	 * @date 2016年9月8日 下午7:05:01
	 * @param insureCompany
	 * @param data
	 * @param dataFileName
	 * @return
	 */
	JSONObject editInsureCompany(InsureCompanyModel insureCompany, File data, String dataFileName) throws Exception;

	/**
	 * @description 删除保险公司
	 * @date 2016年9月8日 下午7:10:35
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	JSONObject deleteInsureCompany(Integer[] id) throws Exception;

	/**
	 * @description 保险公司险种列表
	 * @date 2016年9月8日 下午7:52:52
	 * @param keyMap    搜索关键字map
	 * @param page      当前页码
	 * @param pageSize  每页记录条数
	 * @param sort      排序字段
	 * @param order     排序方式
	 * @return
	 */
	Page getInsureTypeListByPage(Map keyMap, int page, int pageSize, String sort, String order);

	/**
	 * @description 获取保险公司下拉列表数据
	 * @date 2016年9月8日 下午8:38:18
	 * @return
	 */
	List<InsureCompanyModel> getInsureCompanySelectList();

	/**
	 * @description 查询险种是否已存在
	 * @date 2016年9月8日 下午10:08:10
	 * @param insureType
	 * @return
	 */
	boolean isExistInsureType(InsureTypeModel insureType);

	/**
	 * @description 添加保险险种
	 * @date 2016年9月8日 下午10:14:14
	 * @param insureType
	 * @return
	 */
	JSONObject addInsureType(InsureTypeModel insureType);

	/**
	 * @description 获取险种信息
	 * @date 2016年9月8日 下午10:18:30
	 * @param valueOf
	 * @return
	 */
	InsureTypeModel getInsureType(Integer id);

	/**
	 * @description 修改险种信息
	 * @date 2016年9月8日 下午10:20:43
	 * @param insureType
	 * @return
	 */
	JSONObject editInsureType(InsureTypeModel insureType);

	/**
	 * @description 删除险种
	 * @date 2016年9月8日 下午10:23:20
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	JSONObject deleteInsureType(Integer[] id) throws Exception;

	/**
	 * @param carplate 
	 * @description 
	 * @date 2016年9月9日 下午3:48:29
	 * @return
	 */
	JSONObject getInsureCompanies(String carplate);

	/**
	 * @description 店铺-保险公司-险种信息列表
	 * @date 2016年9月13日 下午2:50:27
	 * @param df                 金额格式化formatter
	 * @param store_id           店铺id
	 * @param insure_company_id  保险公司id
	 * @param insureTypePriceMap 险种map集合
	 * @param carplate           车牌号
	 * @return
	 */
	JSONObject getInsuresInfoOfStoreInsureCompany(DecimalFormat df, String store_id, String insure_company_id, Map<Integer, JSONObject> insureTypePriceMap, String carplate);
	
	/**
	 * @description 根据车牌获取车型，计算保险费用
	 * @date 2016年9月12日 下午5:02:46
	 * @param carplate                 车牌号
	 * @param df                       金额格式化formatter
	 * @param glass_type               玻璃破碎险-玻璃类型
	 * @param thirdparty_coverage_id   第三者责任险-保额
	 * @param driver_coverage_id       车上人员-司机责任险-保额
	 * @param passenger_coverage_id    车上人员-乘客责任险-保额
	 * @param scratch_coverage_id      车身划痕险-保额
	 * @param exempt_insure_typeids    不计免赔险-选择的险种id集合
	 * @return
	 */
	public Map<Integer, JSONObject> getCarInsureTypePriceMap(String carplate, DecimalFormat df, String glass_type, String thirdparty_coverage_id, String driver_coverage_id, 
                                                             String passenger_coverage_id, String scratch_coverage_id, String exempt_insure_typeids);


	
}
