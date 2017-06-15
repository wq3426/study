package com.enation.app.shop.core.action.backend;
 

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component; 

import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.service.IBrandManager;
import com.enation.app.shop.core.service.IBrandsTagManager;
import com.enation.app.shop.core.service.ITagManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.IDaoSupport;

@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("brandsShow")
@Results({
	@Result(name="taglist", type="freemarker", location="/shop/admin/brandsshow/taglist.html"),
	@Result(name="brandlist", type="freemarker", location="/shop/admin/brandsshow/brandlist.html"), 
	@Result(name="search_list", type="freemarker", location="/shop/admin/brandsshow/search_list.html") 
})
/**
 * 
 * 平拍标签关联
 * @author	Chopper
 * @version	v1.0, 2016-1-6 下午5:52:58
 * @since
 */
public class BrandsShowAction extends WWAction {
	 
	private static final long serialVersionUID = 2019019647138496361L;
	//分类id 
	private int id;
	  
	
	// 标签id 
	private int tag_id;
	/**
	 * 品牌manager
	 */
	private IBrandManager brandManager;
	private ITagManager tagManager;  
	private IBrandsTagManager brandsTagManager;
	//品牌
	 
	private List<Brand> brands;
	
	private int[] brand_id;
	private int[] brand_num;
	
	/**
	 * 分类页跳转
	 * @return
	 */
	public String list(){
		return "taglist";
	}
	/**
	 * 品牌页跳转
	 * @return
	 */
	public String brandlist(){ 
		return "brandlist";
	}

	/**
	 * 返回json
	 * @return
	 */
	public String listJson(){ 
		showGridJson(tagManager.list(this.getPage(),this.getPageSize(),1));
		return this.JSON_MESSAGE;
	}
	/**
	 * 返回json
	 * @return
	 */
	public String listJsonBrand(){ 
		showGridJson(brandManager.listBrands(tag_id, this.getPage(), this.getPageSize()));
		return this.JSON_MESSAGE;
	}
	
	
	/**
	 * 删除关联品牌
	 * @return
	 */
	public String del(){
		try {
			brandsTagManager.del(tag_id, id); 
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			this.showErrorJson("删除失败");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 添加品牌页面
	 * @return
	 */
	public String search(){
		return "search_list";
	} 
	
	/**
	 * 添加品牌
	 */
	public String add(){
		
		try {
			brandsTagManager.add(tag_id, brand_id);
			this.showSuccessJson("操作成功");
		} catch (Exception e) {  
			this.showErrorJson("操作失败");
		}
		
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 保存排序
	 * @return
	 */
	public String saveOrder(){
		try {
			brandsTagManager.saveOrder(tag_id,brand_id , brand_num);
			this.showSuccessJson("保存成功");
		} catch (Exception e) {
			this.showErrorJson("保存失败");
		}
		
		return  this.JSON_MESSAGE;
	} 
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id; 
	}
	public IBrandManager getBrandManager() {
		return brandManager;
	}
	public void setBrandManager(IBrandManager brandManager) {
		this.brandManager = brandManager;
	}
	public List<Brand> getBrands() {
		return brands;
	}
	public void setBrands(List<Brand> brands) {
		this.brands = brands;
	}
 
	public int[] getBrand_id() {
		return brand_id;
	}
	public void setBrand_id(int[] brand_id) {
		this.brand_id = brand_id;
	}
	public int[] getBrand_num() {
		return brand_num;
	}
	public void setBrand_num(int[] brand_num) {
		this.brand_num = brand_num;
	}
	public ITagManager getTagManager() {
		return tagManager;
	}
	public void setTagManager(ITagManager tagManager) {
		this.tagManager = tagManager;
	}
	public int getTag_id() {
		return tag_id;
	}
	public void setTag_id(int tag_id) {
		this.tag_id = tag_id;
	} 
	public IBrandsTagManager getBrandsTagManager() {
		return brandsTagManager;
	}
	public void setBrandsTagManager(IBrandsTagManager brandsTagManager) {
		this.brandsTagManager = brandsTagManager;
	} 
	
}
