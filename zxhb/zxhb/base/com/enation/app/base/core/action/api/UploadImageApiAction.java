package com.enation.app.base.core.action.api;

import java.io.File;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IMemberManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/base")
@Action("upload-image")
@Results({
})
/**
 * 图片上传API
 * @author kanon 2015-9-22 version 1.1 添加注释
 */
public class UploadImageApiAction extends WWAction {

		private File image ;//上传的文件
	 	private String imageFileName;//上传的文件名
	 	private String subFolder;//上传文件保存路径
	    private String imageContentType; // 上传的文件类型
	 	private String username;
	 	private IMemberManager memberManager;
	 	
	 	/**
	 	 *  上传图片
	 	 * @param image 图片
	 	 * @param imageFileName 图片名称
	 	 * @param subFolder 存放文件夹名称
	 	 * @return 上传成功返回： 图片地址，失败返回上传图片错误信息
	 	 */
	 	public String execute(){
	 		try{
	 			String fsImgPath = UploadUtil.uploadFile(image, imageFileName,  subFolder);
	 			String path="{\"img\":\""+UploadUtil.replacePath(fsImgPath)+"\",\"fsimg\":\""+fsImgPath+"\"}";
	 			this.json=path;
	 		}catch(Throwable e){
	 			this.showErrorJson("上传出错"+e.getLocalizedMessage());
	 			e.printStackTrace();
	 		}
	 		return this.JSON_MESSAGE;
	 	}
	 	
	 	/**
	 	 * 上传用户头像face，并存到数据库
	 	 * @return
	 	 */
	 	public String uploadUserFace(){
	 		try {
	 			String suffix = imageFileName.substring(imageFileName.indexOf(".")+1);
		 		if("gif".equals(suffix) || "jpg".equals(suffix) || "png".equals(suffix)){
		 			String fsImgPath = UploadUtil.uploadFile(image, imageFileName, subFolder);
			 		String imgUrl = UploadUtil.replacePath(fsImgPath);
			 		memberManager.updateUserFace(username, fsImgPath);
			 		this.json = "{\"result\":1,\"data\":\""+ imgUrl + "\"}";
		 		}else{
		 			this.json = "{\"result\":0,\"message\":\"上传文件类型只能是.gif、.jpg、.png\"}";;
		 		}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				this.showErrorJson("上传出错"+e.getLocalizedMessage());
			}
	 		
	 		return this.JSON_MESSAGE;
	 	}
	 	
	 	/**
	 	 * 上传店铺设置相关图片
	 	 * @return
	 	 */
	 	public String storeImgUpload(){
	 		try {
	 			String fsImgPath = UploadUtil.uploadFile(image, imageFileName, subFolder);
		 		String imgUrl = UploadUtil.replacePath(fsImgPath);
		 		this.json = "{\"result\":1,\"imgUrl\":\""+ imgUrl + "\",\"fsImg\":\""+ fsImgPath + "\"}";
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				this.showErrorJson("上传出错"+e.getLocalizedMessage());
			}
	 		
	 		return this.JSON_MESSAGE;
	 	}
	 	
		public File getImage() {
			return image;
		}
		public void setImage(File image) {
			this.image = image;
		}
		public String getImageFileName() {
			return imageFileName;
		}
		public void setImageFileName(String imageFileName) {
			this.imageFileName = imageFileName;
		}
		public String getSubFolder() {
			return subFolder;
		}
		public void setSubFolder(String subFolder) {
			this.subFolder = subFolder;
		}
		public String getImageContentType() {
			return imageContentType;
		}
		public void setImageContentType(String imageContentType) {
			this.imageContentType = imageContentType;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public IMemberManager getMemberManager() {
			return memberManager;
		}
		public void setMemberManager(IMemberManager memberManager) {
			this.memberManager = memberManager;
		}
}
