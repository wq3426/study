package com.enation.app.shop.core.action.backend;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;


/**
 * 品牌action 负责品牌的添加和修改
 * 
 * @author apexking
 * @author LiFenLong 2014-4-1;4.0版本改造  
 */
public class BrandAction extends WWAction {

	private IBrandManager brandManager;

	private Brand brand;

	private File logo;

	private String logoFileName;

	private String oldlogo;

	private String filePath;

	private String order;

	//private Integer brand_id; // 读取详细时使用

	//private String id; // 批量删除时用
	private Integer brandId;
	private Integer[] brand_id;
	
	private List brandList;
	private List<Map> brand_types;//所有商品类型
	private int type_id;
	private int type;
	private String brandname;
	private String imgPath;
	private Map brandMap;
	private String keyword;
	
	
 
	
	
	public String checkUsed(){
		 if(this.brandManager.checkUsed(brand_id) ){
			 this.json="{result:1}"; 
		 }else{
			 this.json="{result:0}";
		 }
		 return this.JSON_MESSAGE;
	}

	 public String checkname(){
		 if(this.brandManager.checkname(brand.getName(),brand.getBrand_id())){
			 this.json="{result:1}"; //存在返回1
		 }else{
			 this.json="{result:0}";
		 }
		 return this.JSON_MESSAGE;
	 }
	 
	 
	 
	public String add() {
		return "add";
	}
	
	public String edit(){
		brand = this.brandManager.get(brandId);
		if(brand.getLogo()!=null&&!StringUtil.isEmpty(brand.getLogo())){			
			imgPath = UploadUtil.replacePath(brand.getLogo());
			logo = new File(imgPath);
		}

		return "edit";
	}
	
	// 后台品牌列表
	public String list() {
		this.brand_types= brandManager.queryAllTypeNameAndId();
		return "list";
	}
	/**
	 * @description 车辆品牌列表 
	 * @date 2016年10月14日 下午3:04:36
	 * @return String
	 */
	public String carBrandList() {
		this.brand_types= brandManager.queryAllTypeNameAndId();
		return "carBrandList";
	}
	
	/**
	 * @description 品牌列表
	 * @date 2016年10月14日 下午2:19:10
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String listJson(){
		brandMap = new HashMap<>();
		brandMap.put("keyword", keyword);
		brandMap.put("type", type);
		this.webpage = brandManager.searchBrand(brandMap, this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	public String typelistjson(){
		brandList = brandManager.list();
		String s = JSONArray.fromObject(brandList).toString();
		this.json = s.replace("name", "text").replace("brand_id", "id");
		return JSON_MESSAGE;
	}
	
	// 后台品牌列表搜索
	public String search() {
		this.brand_types= brandManager.queryAllTypeNameAndId();
		this.webpage =this.brandManager.search(this.getPage(), this.getPageSize(), brandname, type_id);
		return "list";
	}

	// 后台品牌回收站列表
	public String trash_list() {
		this.webpage = this.brandManager.listTrash(order, this.getPage(),this.getPageSize());
		return "trash_list";
	}

	/**
	 * @description 判断品牌名字是否存在
	 * @date 2016年9月30日 下午2:50:05
	 * @return String
	 */
	public String brandNameIsExit(){
		brand=brandManager.searchBrandNameIsExit(brandname.trim());
		if(brand != null){
			this.showErrorJson("该品牌名字已经存在，请重新输入！");
		}else{
			this.showSuccessJson("");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 
	 * @date 2016年11月2日 下午3:27:55
	 * @return String
	 */
	public String save() {
		if (logo != null) {
			if (FileUtil.isAllowUp(logoFileName)) {
		
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
				return JSON_MESSAGE;
			}
		}
		String brandName = brand.getName();
		String url = brand.getUrl();
		
		//参数检验
		if(StringUtil.isEmpty(url)) {
			this.showErrorJson("请输入品牌url！");
			return JSON_MESSAGE;
		}
		if(StringUtil.isEmpty(brandName)) {
			this.showErrorJson("请输入品牌名称！");
			return JSON_MESSAGE;
		}
		if(url.length() > 255) {
			this.showErrorJson("url长度不能大于255！");
			return JSON_MESSAGE;
		}
		
		Brand newBrand=brandManager.searchBrandNameIsExit(brandName);
		try {
			if(newBrand != null){
				//更新品牌信息
				brand.setBrand_id(newBrand.getBrand_id());
				brand.setDisabled(0);
				brand.setFile(this.logo);
				brand.setFileFileName(this.logoFileName);
				brandManager.update(brand);
			} else {
				brand.setDisabled(0);
				brand.setFile(this.logo);
				brand.setFileFileName(this.logoFileName);
				brandManager.add(brand);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("操作失败");
		}
		this.showSuccessJson("品牌添加成功");
		return JSON_MESSAGE;
	}

	/**
	 * @author xulipeng
	 * @return
	 */
	public String saveEdit() {
		if (logo != null) {
			if (FileUtil.isAllowUp(logoFileName)) {
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp格式文件。");
				return JSON_MESSAGE;
			}
		}
		Brand newBrand = brandManager.get(brand.getBrand_id());
		//如果品牌名称不相等，校验品牌名称是否存在
		String brandName = brand.getName();
		if(!newBrand.getName().equals(brandName)){
			Brand checkBrand = brandManager.searchBrandNameIsExit(brandName);
			if(checkBrand != null) {
				this.showErrorJson("该品牌名字已经存在，请重新输入！");
				return JSON_MESSAGE;
			}
		}
		
		//保存修改后的数据
		try {
			brand.setFile(this.logo);
			brand.setFileFileName(this.logoFileName);
			brandManager.update(brand);
			this.showSuccessJson("品牌修改成功");
		} catch (Exception e) {
			this.showErrorJson("品牌修改失败");
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}

	/**
	 * 将品牌放入回收站
	 * 
	 * @return
	 */
	public String delete() {
		try {
			this.brandManager.delete(brand_id);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.logger.error("删除失败", e);
			this.showErrorJson("删除失败:"+e.getMessage());
		}
		return JSON_MESSAGE;
	}

	/**
	 * 将品牌从回收站中还原
	 * 
	 * @return
	 */
	public String revert() {
		try {
			brandManager.revert(brand_id);
			this.json = "{'result':0,'message':'还原成功'}";
		} catch (RuntimeException e) {
			this.json = "{'result':1,'message':'删除失败'}";
		}
		return this.JSON_MESSAGE; 
	}

	/**
	 * 清空回收站中的品牌
	 * 
	 * @return
	 */
	public String clean() {
		try{
			brandManager.clean(brand_id);
			this.json = "{'result':0,'message':'删除成功'}";
		}catch(RuntimeException e){
			 this.json="{'result':1,'message':'删除失败'}";
		 }
		return this.JSON_MESSAGE;
	}

	
	/*
	 * ----------------------------------------------------------------------------------------
	 * GETTER AND SETTER
	 */
	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public File getLogo() {
		return logo;
	}

	public void setLogo(File logo) {
		this.logo = logo;
	}

	public String getLogoFileName() {
		return logoFileName;
	}

	public void setLogoFileName(String logoFileName) {
		this.logoFileName = logoFileName;
	}


	public void setBrandManager(IBrandManager brandManager) {
		this.brandManager = brandManager;
	}

	public String getOldlogo() {
		return oldlogo;
	}

	public void setOldlogo(String oldlogo) {
		this.oldlogo = oldlogo;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public Integer[] getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(Integer[] brand_id) {
		this.brand_id = brand_id;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public Map getBrandMap() {
		return brandMap;
	}

	public void setBrandMap(Map brandMap) {
		this.brandMap = brandMap;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public List getBrandList() {
		return brandList;
	}

	public void setBrandList(List brandList) {
		this.brandList = brandList;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	public String getBrandname() {
		return brandname;
	}

	public void setBrandname(String brandname) {
		this.brandname = brandname;
	}

	public int getType_id() {
		return type_id;
	}

	public void setType_id(int type_id) {
		this.type_id = type_id;
	}

	public List<Map> getBrand_types() {
		return brand_types;
	}

	public void setBrand_types(List<Map> brand_types) {
		this.brand_types = brand_types;
	}

	public IBrandManager getBrandManager() {
		return brandManager;
	}
	
	
}
