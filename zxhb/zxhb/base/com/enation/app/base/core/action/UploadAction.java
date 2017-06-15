
package com.enation.app.base.core.action;

import java.io.File;

import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;

/**
 * 附件上传
 * 
 * @author kingapex 2010-3-10下午04:24:47
 * @author Kanon 2015-12-16 version 1.1 添加注释
 */
public class UploadAction extends WWAction {

	private String fileFileName;
	private File file;
	// 是否创建 缩略图,默认不创建
	private int createThumb = 0;
	// 子目录地址
	private String subFolder;
	private int ajax;
	private String type;
	private String picname;
	private int width;
	private int height;
	//	用于派生类获取路径
	protected String path = null;
	
	/**
	 * 上传图片
	 * @return 跳转至上传图片的页面
	 */
	public String execute() {
		return WWAction.INPUT;
	}
	
	/**
	 * 上传附件页面   冯兴隆 2015-07-28
	 * @return
	 */
	public String fileUI(){
		return "input_file";
	}
	/**
	 * 上传附件
	 * @param file 附件
	 * @param fileFileName 附件名称
	 * @param subFolder 附件存放文件夹
	 * @param path 上传后的图片路径
	 * @param ajax 是否为异步提交
	 * @return
	 */
	public String uploadFile(){
		if (file != null && fileFileName != null) {
			try{
				path = UploadUtil.uploadFile(file, fileFileName, subFolder);
			}catch(Exception e){
				this.showErrorJson(e.getMessage());
				return this.JSON_MESSAGE;
			}
			// 将本地附件路径换为静态资源服务器的地址
			if (path != null)
				path = UploadUtil.replacePath(path);

			if (ajax == 1) {
				this.json = "{\"result\":1,\"path\":\"" + path + "\",\"filename\":\"" + fileFileName + "\"}";
				return this.JSON_MESSAGE;
			}
			this.showSuccessJson("上传成功");
		}else{
			this.showErrorJson("没有文件");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 上传图片
	 * @param file 附件
	 * @param fileFileName 附件名称
	 * @param createThumb 是否生成缩略图
	 * @param subFolder 上传文件夹
	 * @param width 缩略图宽度
	 * @param height 缩略图高度
	 * @param path 上传后本地路径
	 * @param ajax 是否为异步上传
	 * @return
	 */
	public String upload() {
		try{
			if (file != null && fileFileName != null) {
				try{
					if (subFolder == null) {
						subFolder = "";
					}

					if (this.createThumb == 1) {
						path = UploadUtil.upload(file, fileFileName, subFolder, width, height)[0];
					} else {
						path = UploadUtil.uploadFile(file, fileFileName, subFolder);
					}
				}catch(IllegalArgumentException e){
					this.showErrorJson(e.getMessage());
					return this.JSON_MESSAGE;
				}
				// 将本地图片路径换为静态资源服务器的地址
				if (path != null)
					path = UploadUtil.replacePath(path);

				if (ajax == 1) {
					this.json = "{\"result\":1,\"path\":\"" + path + "\",\"thumbnail\":\"" + UploadUtil.getThumbPath(path, "_thumbnail") + "\",\"filename\":\"" + fileFileName + "\"}";
					return WWAction.JSON_MESSAGE;
				}
			}
		} catch(Exception e) {
			this.logger.error("上传图片出错:"+e);
		}
		this.showErrorJson("请选择文件");
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 删除图片
	 * 根据图片路径删除图片
	 * @param picname  图片路径
	 * @return 删除状态
	 */
	public String delete() {
		UploadUtil.deleteFile(picname);
		this.json = "{'result':0}";
		return WWAction.JSON_MESSAGE;
	}

	//get set
	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getCreateThumb() {
		return createThumb;
	}

	public void setCreateThumb(int createThumb) {
		this.createThumb = createThumb;
	}

	public String getSubFolder() {
		return subFolder;
	}

	public void setSubFolder(String subFolder) {
		this.subFolder = subFolder;
	}

	public int getAjax() {
		return ajax;
	}

	public void setAjax(int ajax) {
		this.ajax = ajax;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	public String getPicname() {
		return picname;
	}
	public void setPicname(String picname) {
		this.picname = picname;
	}
}
