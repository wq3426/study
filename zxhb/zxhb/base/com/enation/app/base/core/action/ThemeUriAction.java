package com.enation.app.base.core.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.resource.IThemeUriManager;
import com.enation.eop.resource.model.ThemeUri;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("themeUri")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/uri/list.html"),
	@Result(name="edit", type="freemarker", location="/core/admin/uri/edit.html"), 
	@Result(name="add", type="freemarker", location="/core/admin/uri/add.html") 
})
/**
 * uri映射action
 * @author Kanon 2015-11-25 version 1.1 添加注释
 */
public class ThemeUriAction extends WWAction {
	private IThemeUriManager themeUriManager;
	private List uriList;
	private ThemeUri themeUri;
	private int id;
	private int[] ids;
	private String[] uri;
	private String[] path;
	private String[] pagename;
	private int[] point;
	private int[] httpcache;
	private String keyword;
	
	/**
	 * 跳转至uri映射列表
	 * @return uri映射列表
	 */
	public String list(){
		return "list";
	}
	
	/**
	 * 获取uri映射列表JSON
	 * @param keyword 关键字
	 * @param uriList uri 列表
	 * @return uri映射列表JSON
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String listJson(){
		Map map = new HashMap();
		map.put("keyword", keyword);
		uriList  = themeUriManager.list(map);
		this.showGridJson(uriList);
		return JSON_MESSAGE;
	}
	
	/**
	 * 跳转至uri映射添加页面
	 * @return uri映射添加页面
	 */
	public String add(){
		return  "add";
	}
	
	/**
	 * 跳转至uri映射修改页面
	 * @param id uri映射Id
	 * @param themeUri uri映射
	 * @return uri映射修改页面
	 */
	public String edit(){
		themeUri = this.themeUriManager.get(id);
		return "edit";
	}
	
	/**
	 * 新增uri映射
	 * @param themeUri uri映射
	 * @return 新增状态
	 */
	public String saveAdd(){
		try{
			this.themeUriManager.add(themeUri);
			this.showSuccessJson("添加成功");
		}catch(RuntimeException e){
			this.showErrorJson("失败:"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 保存修改uri映射
	 * @param themeUri uri映射
	 * @return 修改状态 
	 */
	public String saveEdit(){
		
		//判断是否为演示站点
		if(EopSetting.IS_DEMO_SITE){
			if(id<=6){
				this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
				return JSON_MESSAGE;
			}
		}
		
		//保存修改uri映射
		try{
			this.themeUriManager.edit(themeUri);
			this.showSuccessJson("修改成功");
		}catch(RuntimeException e){
			this.showErrorJson("修改失败:"+e.getMessage());
		}		
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 批量修改
	 * @param uri uri映射数组
	 * @param ids uriId数组
	 * @param path  
	 * @param pagename 页面名称
	 * @return
	 */
	public String batchEdit(){
		try{
			List<ThemeUri> uriList  = new ArrayList<ThemeUri>();
			if(uri!=null ){
				for(int i=0, len=uri.length;i<len;i++){
					ThemeUri themeUri  = new ThemeUri();
					themeUri.setUri( uri[i] );
					themeUri.setId(ids[i]);
					themeUri.setPath(path[i]);
					themeUri.setPagename(pagename[i]);
					if (point != null) {
						themeUri.setPoint(point[i]);
					}
					if (httpcache != null) {
						themeUri.setHttpcache(httpcache[i]);
					}
					uriList.add(themeUri);
				}
			}
			this.themeUriManager.edit(uriList);
			this.showSuccessJson("保存修改成功");
		}catch(RuntimeException e){
			e.printStackTrace();
			this.showErrorJson("失败:"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	
	public String delete(){
		
		if(EopSetting.IS_DEMO_SITE){
			if(id<=6){
				this.showErrorJson("抱歉，当前为演示站点，以不能删除这些示例数据，请下载安装包在本地体验这些功能！");
				return JSON_MESSAGE;
			}
		}
		
		try{
			this.themeUriManager.delete(id);
			this.showSuccessJson("删除成功");
		}catch(RuntimeException e){
			this.showErrorJson("删除失败:"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	public IThemeUriManager getThemeUriManager() {
		return themeUriManager;
	}

	public void setThemeUriManager(IThemeUriManager themeUriManager) {
		this.themeUriManager = themeUriManager;
	}

	public List getUriList() {
		return uriList;
	}

	public void setUriList(List uriList) {
		this.uriList = uriList;
	}

	public ThemeUri getThemeUri() {
		return themeUri;
	}

	public void setThemeUri(ThemeUri themeUri) {
		this.themeUri = themeUri;
	}

	public String[] getUri() {
		return uri;
	}

	public void setUri(String[] uri) {
		this.uri = uri;
	}

	public String[] getPath() {
		return path;
	}

	public void setPath(String[] path) {
		this.path = path;
	}

	public String[] getPagename() {
		return pagename;
	}

	public void setPagename(String[] pagename) {
		this.pagename = pagename;
	}

	public int[] getPoint() {
		return point;
	}

	public void setPoint(int[] point) {
		this.point = point;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int[] getIds() {
		return ids;
	}

	public void setIds(int[] ids) {
		this.ids = ids;
	}


	public int[] getHttpcache() {
		return httpcache;
	}


	public void setHttpcache(int[] httpcache) {
		this.httpcache = httpcache;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
}
