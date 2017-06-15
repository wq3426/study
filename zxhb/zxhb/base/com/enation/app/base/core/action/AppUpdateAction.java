package com.enation.app.base.core.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;

import net.sf.json.JSONObject;

/**
 * 数据导出Action
 * @author kingapex
 * 2015-5-6下午3:07:46
 * @author Kanon 2015-9-24 version 1.1 添加注释
 */

@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("app-update")
@Results({
	@Result(name="input", type="freemarker", location="/core/admin/app_update.html"),
	@Result(name="list", type="freemarker", location="/core/admin/app_list.html"),
	@Result(name="download", type="stream", params = {  
	        "contentType", "application/octet-stream", "inputName", "inputStream", 
	        "contentDisposition", "attachment;filename=\"${fileName}\"", "bufferSize", "4096" })
})
public class AppUpdateAction extends WWAction {
	
	private File app ;//上传的文件
 	private String appFileName;//上传的文件名
    private String appContentType; // 上传的文件类型
    private String res;//要下载的资源
    private String version_No;//版本号
	private Map keyMap;
	private String keyword;
	private Integer[] app_id;
	private Integer release_app_id;
	private String versionNoArray;
    
    private IAdminUserManager adminUserManager;
	
	/**
	 * 跳转至数据导出页面
	 * @return 数据导出页面
	 */
	public String execute(){
		return this.INPUT;
	}
	
	/**
	 * 跳转到app更新列表
	 * @return
	 */
	public String list(){
		versionNoArray = adminUserManager.getVersionNoArray();
		
		return "list";
	}
	
	/**
	 * 获取app列表数据
	 * @return
	 */
	public String listJson(){
		keyMap = new HashMap();
		keyMap.put("keyword", keyword);
		this.webpage = adminUserManager.searchApp(keyMap, this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	/**
	 * .apk文件上传
	 * @return
	 */
	public String updateApp(){
		try {
			String vNumber = getRequest().getParameter("vNumber");
			String suffix = appFileName.substring(appFileName.indexOf(".")+1);
	 		if("apk".equals(suffix)){
	 			String fsAppUrl = UploadUtil.uploadAppFile(app, appFileName, "app_file/"+vNumber, vNumber);
//	 			String fsAppUrl = UploadUtil.uploadFile(app, appFileName, "app_file/"+vNumber);
		 		String appUrl = UploadUtil.replacePath(fsAppUrl);
		 		adminUserManager.addAppUpdateRecord(vNumber, fsAppUrl);//添加app版本记录到数据库表
		 		this.json = "{\"result\":1,\"data\":\""+ appUrl + "\"}";
	 		}else{
	 			this.json = "{\"result\":0,\"message\":\"上传文件类型只能是.apk文件\"}";
	 		}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
 		return JSON_MESSAGE;
	}
	
	/**
	 * apk安装包下载
	 * @return
	 */
	public String getAppUrlByVersionNo(){
		try {
			String versionNo = getRequest().getParameter("versionNo");
			JSONObject obj = adminUserManager.checkAppVersion(versionNo);
			if(obj != null){
				if(obj.getBoolean("flag")){
					version_No = obj.getString("version_no");
					res = obj.getString("app_url").replace(EopSetting.FILE_STORE_PREFIX, SystemSetting.getFile_path());
					this.json = "{\"result\":1,\"data\":\""+ UploadUtil.replacePath(obj.getString("app_url")) + "\"}";
//					return "download";
				}else{
					this.json = "{\"result\":0,\"message\":\"您的app当前已经是最新版本\"}";
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 批量删除app记录
	 * @return
	 */
	public String delete() {
		try {
			this.adminUserManager.deleteApp(app_id);
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("删除失败", e);
			this.showErrorJson("删除失败:"+e.getMessage());
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 指定发布的版本，并更新apk到主页二维码扫描得到的安装目录
	 * @return
	 */
	public String releaseApp(){
		try {
			JSONObject obj = adminUserManager.releaseApp(release_app_id);
			if(obj != null){
				this.json = "{\"result\":1, \"data\":\""+ obj.getInt("app_id") + "\",\"message\":\"版本发布成功，版本号："+ obj.getString("version_no") +"\"}";
			}else{
				this.json = "{\"result\":0,\"message\":\"更新失败\"}";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	public InputStream getInputStream(){
		try {
			return new FileInputStream(res); //返回下载资源的输入流
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
	public String getFileName(){
		return "yingjia_"+ version_No +".apk";
	}

	public File getApp() {
		return app;
	}

	public void setApp(File app) {
		this.app = app;
	}

	public String getAppFileName() {
		return appFileName;
	}

	public void setAppFileName(String appFileName) {
		this.appFileName = appFileName;
	}

	public String getAppContentType() {
		return appContentType;
	}

	public void setAppContentType(String appContentType) {
		this.appContentType = appContentType;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
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
	public Integer[] getApp_id() {
		return app_id;
	}
	public void setApp_id(Integer[] app_id) {
		this.app_id = app_id;
	}
	public Integer getRelease_app_id() {
		return release_app_id;
	}
	public void setRelease_app_id(Integer release_app_id) {
		this.release_app_id = release_app_id;
	}
	public String getVersionNoArray() {
		return versionNoArray;
	}
	public void setVersionNoArray(String versionNoArray) {
		this.versionNoArray = versionNoArray;
	}
}
