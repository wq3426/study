package com.enation.app.b2b2c.core.action.backend.car;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.InsureCompanyModel;
import com.enation.app.shop.core.model.InsureTypeModel;
import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.model.CarModel;
import com.enation.app.shop.core.model.CarmodelRepairItem;
import com.enation.app.shop.core.model.RepairItem;
import com.enation.app.shop.core.service.ICarManager;
import com.enation.app.shop.core.service.IInsuranceManager;
import com.enation.app.shop.core.service.InsureType;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONObject;


/**
 * @Description 后台车型管理
 *
 * @createTime 2016年8月22日 下午6:08:40
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/b2b2c/admin/carmodel/carmodel_list.html"),
	 @Result(name="add",type="freemarker", location="/b2b2c/admin/carmodel/carmodel_input.html"),
	 @Result(name="toimport",type="freemarker", location="/b2b2c/admin/carmodel/carmodel_import.html"),
	 @Result(name="edit",type="freemarker", location="/b2b2c/admin/carmodel/carmodel_edit.html"),
	 
	 @Result(name="item_list",type="freemarker", location="/b2b2c/admin/repairitem/repairitem_list.html"),
	 @Result(name="addRepairItem",type="freemarker", location="/b2b2c/admin/repairitem/repairitem_input.html"),
	 @Result(name="editRepairItem",type="freemarker", location="/b2b2c/admin/repairitem/repairitem_edit.html"),
	 
	 @Result(name="carmodelRepairItemList",type="freemarker", location="/b2b2c/admin/carmodel_repairitem/carmodel_repairitem_list.html"),
	 @Result(name="to_car_repairitem_import",type="freemarker", location="/b2b2c/admin/carmodel_repairitem/carmodel_repairitem_import.html"),
	 @Result(name="addCarmodelRepairItem",type="freemarker", location="/b2b2c/admin/carmodel_repairitem/carmodel_repairitem_input.html"),
	 @Result(name="editCarmodelRepairItem",type="freemarker", location="/b2b2c/admin/carmodel_repairitem/carmodel_repairitem_edit.html"),
	 
	 @Result(name="insure_company_list",type="freemarker", location="/b2b2c/admin/insurecompany/insure_company_list.html"),
	 @Result(name="addInsureCompany",type="freemarker", location="/b2b2c/admin/insurecompany/insure_company_input.html"),
	 @Result(name="editInsureCompany",type="freemarker", location="/b2b2c/admin/insurecompany/insure_company_edit.html"),
	 
	 @Result(name="insure_type_list",type="freemarker", location="/b2b2c/admin/insuretype/insure_type_list.html"),
	 @Result(name="addInsureType",type="freemarker", location="/b2b2c/admin/insuretype/insure_type_input.html"),
	 @Result(name="editInsureType",type="freemarker", location="/b2b2c/admin/insuretype/insure_type_edit.html")
	 
})
@Action("carInsureAndRepair")
@SuppressWarnings("unchecked")
public class CarInsureAndRepairAction extends WWAction{
	private Map keyMap;
	private String keyword;
	private Integer stype;
	private ICarManager carManager;
	private File data ;//上传的文件
 	private String dataFileName;//上传的文件名
    private String dataContentType; // 上传的文件类型
    private List<Brand> carbrandList;//车辆品牌列表
    private CarModel carmodel;//车型对象
    private Integer[] id;
	
    private RepairItem repairItem;//保养项目对象
    
    private List<String> cartypeList;//类型列表
    private List<String> seriesList;//车系列表
    private List<String> nkList;//年款列表
    private List<String> modelList;//车型列表
    private List<CarmodelRepairItem> carmodelRepairItemList;//车型保养项目列表
    
    private CarmodelRepairItem carmodelRepairItem;//车型保养项目对象
    
    private IInsuranceManager insuranceManager;
    private InsureCompanyModel insureCompany;//保险公司对象
    private List<InsureCompanyModel> insureCompanyList;//保险公司列表
    private Map<Integer, String> insureTypes = InsureType.getInsureTypeMap();//险种map对象
    private InsureTypeModel insureType;//险种对象
    
	/**
	 * @description 跳转到车型列表
	 * @date 2016年8月22日 上午10:36:09
	 * @return
	 */
	public String list(){
		return "list";
	}
	
	/**
	 * @description 列表查询车型数据
	 * @date 2016年8月22日 下午5:59:02
	 * @return
	 */
	public String listJson(){
		keyMap = new HashMap();
		keyMap.put("keyword", keyword);
		keyMap.put("stype", stype);
		this.webpage = carManager.getListByPage(keyMap, this.getPage(), this.getPageSize(), this.getSort(),this.getOrder());
		this.showGridJson(webpage);
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 跳转到导入页面
	 * @date 2016年8月22日 下午5:59:32
	 * @return
	 */
	public String toImportExcel(){
		return "toimport";
	}
	
	/**
	 * @description 读取excel文件数据导入到车型表
	 * @date 2016年8月22日 下午6:07:55
	 * @return
	 */
	public String importExcelData(){
		try {
			JSONObject returnObj = carManager.importExcelData(data, dataFileName);
			this.json = returnObj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson(e.getMessage());
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 跳转到车型添加页面
	 * @date 2016年8月24日 下午8:29:25
	 * @return
	 */
	public String add(){
		carbrandList = carManager.getCarBrandSelectList();
		return "add";
	}
	
	/**
	 * @description 保存添加的车型
	 * @date 2016年8月24日 下午8:30:07
	 * @return
	 */
	public String addSave(){
		try {
			String carmodel_type = getRequest().getParameter("carmodel_type");
			JSONObject obj = new JSONObject();
			if(!carManager.isExist(carmodel)){
				obj = carManager.addCarmodel(carmodel, carmodel_type, data, dataFileName);
			}else{
				obj.put("result", 0);
				obj.put("message", "该款车已存在，请勿重复添加");
			}
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("添加失败，服务器出现错误，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 车型修改
	 * @date 2016年8月26日 下午8:28:22
	 * @return
	 */
	public String edit(){
		try {
			String id = getRequest().getParameter("id");
			carbrandList = carManager.getCarBrandSelectList();
			carmodel = carManager.getCarmodel(id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "edit";
	}
	
	/**
	 * @description 车型资料修改保存
	 * @date 2016年8月25日 下午5:08:16
	 * @return
	 */
	public String editSave(){
		try {
			String carmodel_type = getRequest().getParameter("carmodel_type");
			JSONObject obj = carManager.editSave(carmodel, carmodel_type, data, dataFileName);
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，修改失败，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 删除车型记录
	 * @date 2016年8月25日 下午8:03:41
	 * @return
	 */
	public String delete(){
		try {
			JSONObject obj = carManager.delete(id);
			
			json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，删除失败，请稍后重试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 跳转到保养项目列表页面
	 * @date 2016年8月26日 下午5:21:23
	 * @return
	 */
	public String repairItemList(){
		return "item_list";
	}
	
	/**
	 * @description 列表查询保养项目
	 * @date 2016年8月22日 下午5:59:02
	 * @return
	 */
	public String listRepairItemJson(){
		keyMap = new HashMap();
		keyMap.put("keyword", keyword);
		this.webpage = carManager.getRepairItemListByPage(keyMap, this.getPage(), this.getPageSize(), this.getSort(),this.getOrder());
		this.showGridJson(webpage);
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 跳转到保养项目添加页面
	 * @date 2016年8月24日 下午8:29:25
	 * @return
	 */
	public String addRepairItem(){
		return "addRepairItem";
	}
	
	/**
	 * @description 保存添加的保养项目
	 * @date 2016年8月26日 下午8:03:54
	 * @return
	 */
	public String addRepairItemSave(){
		try {
			JSONObject obj = new JSONObject();
			if(!carManager.isExistRepairItem(repairItem.getItemname())){
				obj = carManager.addRepairItem(repairItem);
			}else{
				obj.put("result", 0);
				obj.put("message", "该项目名称已存在，请勿重复添加");
			}
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("添加失败，服务器出现错误，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}

	/**
	 * @description 跳转到保养项目修改页面
	 * @date 2016年8月26日 下午8:28:50
	 * @return
	 */
	public String editRepairItem(){
		try {
			String id = getRequest().getParameter("id");
			repairItem = carManager.getRepairItem(id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "editRepairItem";
	}
	
	/**
	 * @description 保养项目修改保存
	 * @date 2016年8月26日 下午8:55:23
	 * @return
	 */
	public String editRepairItemSave(){
		try {
			JSONObject obj = carManager.editRepairItemSave(repairItem);
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("修改失败，服务器出现错误，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 保养项目删除
	 * @date 2016年8月26日 下午9:09:03
	 * @return
	 */
	public String deleteRepairItem(){
		try {
			JSONObject obj = carManager.deleteRepairItem(id);
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，删除失败，请稍后重试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 跳转到车型保养项目列表页面
	 * @date 2016年8月27日 下午2:46:14
	 * @return
	 */
	public String carmodelRepairItemList(){
		return "carmodelRepairItemList";
	}
	
	/**
	 * @description 列表查询车型保养项目
	 * @date 2016年8月30日 上午10:38:36
	 * @return
	 */
	public String listCarmodelRepairItemJson(){
		keyMap = new HashMap();
		keyMap.put("keyword", keyword);
		keyMap.put("stype", stype);
		this.webpage = carManager.getCarmodelRepairItemListByPage(keyMap, this.getPage(), this.getPageSize(), this.getSort(),this.getOrder());
		this.showGridJson(webpage);
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 车型保养项目导入
	 * @date 2016年8月27日 下午3:00:58
	 * @return
	 */
	public String toImportCarRepairItemExcel(){
		return "to_car_repairitem_import";
	}
	
	/**
	 * @description 导入车型保养项目excel表
	 * @date 2016年8月27日 下午4:50:21
	 * @return
	 */
	public String importCarRepairItemExcelData(){
		try {
			JSONObject returnObj = carManager.importCarRepairItemExcelData(data, dataFileName);
			this.json = returnObj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("导入失败，出现解析错误");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 添加车型保养项目
	 * @date 2016年8月30日 下午8:15:24
	 * @return
	 */
	public String addCarmodelRepairItem(){
		carbrandList = carManager.getCarBrandSelectList();
		carmodelRepairItemList = carManager.getCarmodelRepairItemList();
		
		return "addCarmodelRepairItem";
	}
	
	/**
	 * @description 获取车型保养项目添加相关的下拉列表数据
	 * @date 2016年8月30日 下午9:04:24
	 * @return
	 */
	public String getCarmodelRelateList(){
		try {
			JSONObject obj = new JSONObject();
			
			String getparam = getRequest().getParameter("getparam");
			String brand_id = getRequest().getParameter("brand_id");
			String type = getRequest().getParameter("type");
			String series = getRequest().getParameter("series");
			String nk = getRequest().getParameter("nk");
			String sales_name = getRequest().getParameter("sales_name");

			//根据品牌获取类型列表
			if("type".equals(getparam) && !StringUtil.isEmpty(brand_id)){
				cartypeList = carManager.getCarmodelRelateList("brand_id", brand_id);
				
				obj.put("data", cartypeList);
			}
			
			//根据品牌、类型获取车系列表
			if("series".equals(getparam) && !StringUtil.isEmpty(brand_id) && !StringUtil.isEmpty(type)){
				String type_value = "";
				if("1".equals(type)){
					type_value = "国产";
				}
				if("2".equals(type)){
					type_value = "进口";
				}
				seriesList = carManager.getCarmodelRelateList("type", brand_id, type_value);
				
				obj.put("data", seriesList);
			}
			
			//根据品牌、类型、车系获取年款列表
			if("nk".equals(getparam) && !StringUtil.isEmpty(brand_id) && !StringUtil.isEmpty(type) && !StringUtil.isEmpty(series)){
				String type_value = "";
				if("1".equals(type)){
					type_value = "国产";
				}
				if("2".equals(type)){
					type_value = "进口";
				}
			
				nkList = carManager.getCarmodelRelateList("series", brand_id, type_value, series);
				
				obj.put("data", nkList);
			}
			
			//根据品牌、类型、车系、年款获取车型列表
			if("sales_name".equals(getparam) && !StringUtil.isEmpty(brand_id) && !StringUtil.isEmpty(type) && !StringUtil.isEmpty(series) && !StringUtil.isEmpty(nk)){
				String type_value = "";
				if("1".equals(type)){
					type_value = "国产";
				}
				if("2".equals(type)){
					type_value = "进口";
				}
				
				modelList = carManager.getCarmodelRelateList("nk", brand_id, type_value, series, nk);
				
				obj.put("data", modelList);
			}
			
			//根据品牌、类型、车系、年款、车型获取车型id
			if("carmodel_id".equals(getparam) && !StringUtil.isEmpty(brand_id) && !StringUtil.isEmpty(type) && !StringUtil.isEmpty(series) && !StringUtil.isEmpty(nk) && !StringUtil.isEmpty(sales_name)){
				String type_value = "";
				if("1".equals(type)){
					type_value = "国产";
				}
				if("2".equals(type)){
					type_value = "进口";
				}
				
				modelList = carManager.getCarmodelRelateList("sales_name", brand_id, type_value, series, nk, sales_name);
				
				String carmodel_id = JSONObject.fromObject(modelList.get(0)).getString("id");
				
				obj.put("data", carmodel_id);
			}
			
			obj.put("result", 1);
			obj.put("message", "查询成功");
			
			this.json = obj.toString();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 保存添加的车型保养项目
	 * @date 2016年8月30日 下午8:21:15
	 * @return
	 */
	public String addCarmodelRepairItemSave(){
		try {
			JSONObject obj = new JSONObject();
			if(!carManager.isExistCarmodelRepairItem(carmodelRepairItem)){
				obj = carManager.addCarmodelRepairItem(carmodelRepairItem);
			}else{
				obj.put("result", 0);
				obj.put("message", "该车型的保养项目已存在，请勿重复添加");
			}
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("添加失败，服务器出现错误，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 车型保养项目修改
	 * @date 2016年8月31日 上午10:57:59
	 * @return
	 */
	public String editCarmodelRepairItem(){
		try {
			String id = getRequest().getParameter("id");
			carmodelRepairItem = carManager.getCarmodelRepairItem(id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "editCarmodelRepairItem";
	}
	
	/**
	 * @description 车型保养项目修改
	 * @date 2016年8月31日 下午12:14:06
	 * @return
	 */
	public String editCarmodelRepairItemSave(){
		try {
			JSONObject obj = carManager.editCarmodelRepairItemSave(carmodelRepairItem);
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("修改失败，服务器出现错误，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 车型保养项目删除
	 * @date 2016年8月31日 下午2:14:17
	 * @return
	 */
	public String deleteCarmodelRepairItem(){
		try {
			JSONObject obj = carManager.deleteCarmodelRepairItem(id);
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，删除失败，请稍后重试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 保险公司列表
	 * @date 2016年9月8日 下午2:54:51
	 * @return
	 */
	public String insureCompanyList(){
		
		return "insure_company_list";
	}
	
    /**
     * @description 列表查询保险公司
     * @date 2016年9月8日 下午3:03:56
     * @return
     */
	public String listInsureCompanyJson(){
		keyMap = new HashMap();
		keyMap.put("keyword", keyword);
		this.webpage = insuranceManager.getInsureCompanyListByPage(keyMap, this.getPage(), this.getPageSize(), this.getSort(),this.getOrder());
		this.showGridJson(webpage);
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 跳转到保险公司添加页面
	 * @date 2016年9月8日 下午4:21:49
	 * @return
	 */
	public String addInsureCompany(){
		
		return "addInsureCompany";
	}
	
	/**
	 * @description 添加保险公司
	 * @date 2016年9月8日 下午5:17:11
	 * @return
	 */
	public String addInsureCompanySave(){
		try {
			JSONObject obj = new JSONObject();
			
			if(!insuranceManager.isExistInsureCompany(insureCompany)){
				obj = insuranceManager.addInsureCompany(insureCompany, data, dataFileName);
			}else{
				obj.put("result", 0);
				obj.put("message", "该保险公司已存在，请勿重复添加");
			}
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，添加失败，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 跳转到保险公司信息修改页面
	 * @date 2016年9月8日 下午5:17:53
	 * @return
	 */
	public String editInsureCompany(){
		try {
			String id = getRequest().getParameter("id");
			insureCompany = insuranceManager.getInsureCompany(Integer.valueOf(id));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "editInsureCompany";
	}
	
	/**
	 * @description 保险公司信息修改
	 * @date 2016年9月8日 下午5:19:00
	 * @return
	 */
	public String editInsureCompanySave(){
		try {
			JSONObject obj = insuranceManager.editInsureCompany(insureCompany, data, dataFileName);
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，修改失败，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 删除保险公司
	 * @date 2016年9月8日 下午5:24:47
	 * @return
	 */
	public String deleteInsureCompany(){
		try {
			JSONObject obj = insuranceManager.deleteInsureCompany(id);
			
			json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，删除失败，请稍后重试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 跳转到险种列表
	 * @date 2016年9月8日 下午7:41:43
	 * @return
	 */
	public String insureTypeList(){
		
		return "insure_type_list";
	}
	
	/**
	 * @description 险种列表查询
	 * @date 2016年9月8日 下午7:49:34
	 * @return
	 */
	public String listInsureTypeJson(){
		keyMap = new HashMap();
		keyMap.put("keyword", keyword);
		this.webpage = insuranceManager.getInsureTypeListByPage(keyMap, this.getPage(), this.getPageSize(), this.getSort(),this.getOrder());
		this.showGridJson(webpage);
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 跳转到险种添加页面
	 * @date 2016年9月8日 下午7:45:59
	 * @return
	 */
	public String addInsureType(){
		try {
			insureCompanyList = insuranceManager.getInsureCompanySelectList();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "addInsureType";
	}
	
	/**
	 * @description 添加险种
	 * @date 2016年9月8日 下午7:47:42
	 * @return
	 */
	public String addInsureTypeSave(){
		try {
			JSONObject obj = new JSONObject();
			
			if(!insuranceManager.isExistInsureType(insureType)){
				obj = insuranceManager.addInsureType(insureType);
			}else{
				obj.put("result", 0);
				obj.put("message", "该保险公司的险种类型:"+ insureType.getInsure_type() +" "+ insureTypes.get(insureType.getInsure_type()) +"已存在，请勿重复添加");
			}
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，添加失败，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 跳转到险种修改页面
	 * @date 2016年9月8日 下午7:46:33
	 * @return
	 */
	public String editInsureType(){
		try {
			String id = getRequest().getParameter("id");
			insureType = insuranceManager.getInsureType(Integer.valueOf(id));
			insureCompanyList = insuranceManager.getInsureCompanySelectList();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "editInsureType";
	}
	
	/**
	 * @description 修改险种
	 * @date 2016年9月8日 下午7:48:29
	 * @return
	 */
	public String editInsureTypeSave(){
		try {
			JSONObject obj = new JSONObject();
			
			if(!insuranceManager.isExistInsureType(insureType)){
				obj = insuranceManager.editInsureType(insureType);
			}else{
				obj.put("result", 0);
				obj.put("message", "该保险公司的险种类型:"+ insureType.getInsure_type() +" "+ insureTypes.get(insureType.getInsure_type()) +"已存在，数据重复");
			}
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，修改失败，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 删除险种
	 * @date 2016年9月8日 下午7:48:45
	 * @return
	 */
	public String deleteInsureType(){
		try {
			JSONObject obj = insuranceManager.deleteInsureType(id);
			
			json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，删除失败，请稍后重试");
		}
		
		return JSON_MESSAGE;
	}
	
	public ICarManager getCarManager() {
		return carManager;
	}

	public void setCarManager(ICarManager carManager) {
		this.carManager = carManager;
	}

	public Map getKeyMap() {
		return keyMap;
	}

	public void setKeyMap(Map keyMap) {
		this.keyMap = keyMap;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getStype() {
		return stype;
	}

	public void setStype(Integer stype) {
		this.stype = stype;
	}

	public File getData() {
		return data;
	}

	public void setData(File data) {
		this.data = data;
	}

	public String getDataFileName() {
		return dataFileName;
	}

	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}

	public String getDataContentType() {
		return dataContentType;
	}

	public void setDataContentType(String dataContentType) {
		this.dataContentType = dataContentType;
	}

	public List<Brand> getCarbrandList() {
		return carbrandList;
	}

	public void setCarbrandList(List<Brand> carbrandList) {
		this.carbrandList = carbrandList;
	}

	public CarModel getCarmodel() {
		return carmodel;
	}

	public void setCarmodel(CarModel carmodel) {
		this.carmodel = carmodel;
	}

	public Integer[] getId() {
		return id;
	}

	public void setId(Integer[] id) {
		this.id = id;
	}

	public RepairItem getRepairItem() {
		return repairItem;
	}

	public void setRepairItem(RepairItem repairItem) {
		this.repairItem = repairItem;
	}

	public List<String> getSeriesList() {
		return seriesList;
	}

	public void setSeriesList(List<String> seriesList) {
		this.seriesList = seriesList;
	}

	public List<String> getNkList() {
		return nkList;
	}

	public void setNkList(List<String> nkList) {
		this.nkList = nkList;
	}

	public List<String> getModelList() {
		return modelList;
	}

	public void setModelList(List<String> modelList) {
		this.modelList = modelList;
	}

	public List<String> getCartypeList() {
		return cartypeList;
	}

	public void setCartypeList(List<String> cartypeList) {
		this.cartypeList = cartypeList;
	}

	public List<CarmodelRepairItem> getCarmodelRepairItemList() {
		return carmodelRepairItemList;
	}

	public void setCarmodelRepairItemList(List<CarmodelRepairItem> carmodelRepairItemList) {
		this.carmodelRepairItemList = carmodelRepairItemList;
	}

	public CarmodelRepairItem getCarmodelRepairItem() {
		return carmodelRepairItem;
	}

	public void setCarmodelRepairItem(CarmodelRepairItem carmodelRepairItem) {
		this.carmodelRepairItem = carmodelRepairItem;
	}

	public IInsuranceManager getInsuranceManager() {
		return insuranceManager;
	}

	public void setInsuranceManager(IInsuranceManager insuranceManager) {
		this.insuranceManager = insuranceManager;
	}

	public InsureCompanyModel getInsureCompany() {
		return insureCompany;
	}

	public void setInsureCompany(InsureCompanyModel insureCompany) {
		this.insureCompany = insureCompany;
	}

	public List<InsureCompanyModel> getInsureCompanyList() {
		return insureCompanyList;
	}

	public void setInsureCompanyList(List<InsureCompanyModel> insureCompanyList) {
		this.insureCompanyList = insureCompanyList;
	}

	public Map<Integer, String> getInsureTypes() {
		return insureTypes;
	}

	public void setInsureTypes(Map<Integer, String> insureTypes) {
		this.insureTypes = insureTypes;
	}

	public InsureTypeModel getInsureType() {
		return insureType;
	}

	public void setInsureType(InsureTypeModel insureType) {
		this.insureType = insureType;
	}
	
}
