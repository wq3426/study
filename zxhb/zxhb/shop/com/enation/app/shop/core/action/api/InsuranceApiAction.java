/**
 * 版权：Copyright (C) 2016  中安信博（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：设备api  
 * 修改人：
 * 修改时间：2016-03-14
 * 修改内容: 
 */
package com.enation.app.shop.core.action.api;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.InsuranceModel;
import com.enation.app.shop.core.service.IInsuranceManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



/**
 * 保险信息api
 * 
 * 实体类：InsuranceModel
 * @author 
 * @date 2016-03-14
 * @version v0.1
 * @since 
 */
//curl -X GET 'http://localhost:8080/mall/api/mobile/insurances!getInsureCompanyList.do'
//curl -X GET 'http://localhost:8080/mall/api/mobile/insurances!getInsurancesInfoList.do?company=平安'

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("insurances")
@SuppressWarnings("serial")
public class InsuranceApiAction extends WWAction {
	private IInsuranceManager insuranceManager;
	
	/**
	 * 获取保险公司名称列表
	 * 查询数据库返回：[,]
	 * 返给前端格式：[,]
	 * @return
	 */
	public String getInsureCompanyList(){
		List companyList = insuranceManager.getInsureCompanyList();
		JSONArray resultArray = JSONArray.fromObject(companyList);
		for(int i=0; i<resultArray.size(); i++){
			JSONObject obj = resultArray.getJSONObject(i);
			obj.put("companyimage", UploadUtil.replacePath(obj.getString("companyimage")));
		}
		this.json = "{\"result\":1,\"data\":"+resultArray+"}";
		this.logger.debug("getInsureCompanyList:" + this.json);
		return JSON_MESSAGE;
	}
	
	/**
	 * 根据保险公司名称获取具体保险套餐信息列表
	 * 查询数据库返回：[{},{},{},{}]
	 * 返给前端格式：[{},{},{},{}]
	 * @return
	 */
	public String getInsureInfoList(){
		String company = getRequest().getParameter("company");
		List<InsuranceModel> insurancesInfoList = insuranceManager.getInsureInfoList(company);
		JSONArray resultArray = JSONArray.fromObject(insurancesInfoList);
		for(int i=0; i<resultArray.size(); i++){
			JSONObject obj = resultArray.getJSONObject(i);
			obj.put("companyimage", UploadUtil.replacePath(obj.getString("companyimage")));
		}
		this.json = "{\"result\":1,\"data\":"+resultArray+"}";
		this.logger.debug("getInsurancesInfoList:" + this.json);
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取店铺保险公司列表
	 * @date 2016年9月9日 下午3:46:18
	 * @return
	 */
	public String getInsureCompanies(){
		try {
			String carplate = getRequest().getParameter("carplate");
			
			JSONObject obj = insuranceManager.getInsureCompanies(carplate);
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，获取店铺保险公司失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 显示险种列表信息
	 * @date 2016年9月9日 下午7:56:29
	 * @return
	 */
	public String showInsuresInfo(){
		try {
			String carplate = getRequest().getParameter("carplate");//车牌号
			String store_id = getRequest().getParameter("store_id");//店铺id
			String insure_company_id = getRequest().getParameter("insure_company_id");//保险公司id
			
			DecimalFormat df = new DecimalFormat("0.00");
			
			//获取车辆险种价格map集合
			Map<Integer, JSONObject> insureTypePriceMap = insuranceManager.getCarInsureTypePriceMap(carplate, df, null, null, null, null, null, null);
			
			JSONObject obj = insuranceManager.getInsuresInfoOfStoreInsureCompany(df, store_id, insure_company_id, insureTypePriceMap, carplate);
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，获取保险公司险种列表失败");
		}
		
		return JSON_MESSAGE;
	}
	
	public IInsuranceManager getInsuranceManager() {
		return insuranceManager;
	}

	public void setInsuranceManager(IInsuranceManager insuranceManager) {
		this.insuranceManager = insuranceManager;
	}
}
