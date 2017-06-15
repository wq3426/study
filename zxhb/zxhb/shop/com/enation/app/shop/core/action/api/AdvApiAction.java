package com.enation.app.shop.core.action.api;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("adv")
public class AdvApiAction extends WWAction {

	private IAdvManager advManager;
	private Adv adv;
	private File pic;
	private String picFileName;
	private Integer aid;
	private Integer isClose;
	/**
	 * 
	 * @return
	 */
	public String add() {
		try {
			if (pic != null) {
				HttpServletRequest request = ThreadContextHolder.getHttpRequest();
				
				
				if (FileUtil.isAllowUp(picFileName)) {
					String path = UploadUtil.uploadFile(this.pic,this.picFileName, "adv");
					adv.setAtturl(path);
				} else {
					this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件。");
					return JSON_MESSAGE;
				}
			} else {
				this.showErrorJson("请上传一张图片");
				return this.JSON_MESSAGE;
			}
			adv.setDisabled("true");
		
			if(adv.getStore_id() != 0) {
				this.advManager.disableStoreAdv(adv.getStore_id());
			}
			adv.setBegintime(DateUtil.getDateline());
			this.advManager.addAdv(adv);
			this.showSuccessJson("新增广告成功");
		} catch (Exception e) {
			this.showErrorJson("新增广告失败");
			logger.error("新增广告失败", e);
		}
		return this.JSON_MESSAGE;
	}
	public String edit() {
		try {
			//adv.setStore_id(((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id());

			if(pic!=null){
				 
				//判断文件类型
				String allowTYpe = "gif,jpg,bmp,png";
				if (!picFileName.trim().equals("") && picFileName.length() > 0) {
					String ex = picFileName.substring(picFileName.lastIndexOf(".") + 1, picFileName.length());
					if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0){
						this.showErrorJson("对不起,只能上传gif,jpg,bmp,png格式的图片！");
						return this.JSON_MESSAGE;
					}
				}
				
				//判断文件大小
				
				if(pic.length() > 2000 * 1024){
					this.showErrorJson("'对不起,图片不能大于2000K！");
					return this.JSON_MESSAGE;
				}
				
				String imgPath=	UploadUtil.uploadFile(pic, picFileName, "faceFile");
				adv.setAtturl(imgPath);
			}
			advManager.updateAdv(adv);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showSuccessJson("发生异常，请重试");
		}
		return this.JSON_MESSAGE;

	}
	/**
	 * 
	 * @return
	 */
	public String startAdv(){
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			int storeId = Integer.parseInt(request.getParameter("store_id").toString());
			int advId = Integer.parseInt(request.getParameter("adv_id").toString());
			
			this.advManager.disableStoreAdv(storeId);
			this.advManager.startAdv(advId);
			this.showSuccessJson("启用成功");
		} catch (RuntimeException e) {
			this.showErrorJson("启用广告失败");
			logger.error("启用广告失败", e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 删除广告
	 * @return
	 */
	public String delete() {
		try {
			if (aid == null || aid.equals("")){
				this.showSuccessJson("删除失败");
			}else{
				advManager.delAdv(aid);
				this.showSuccessJson("删除成功");
			}
		} catch (Exception e) {
			this.showSuccessJson("发生异常，请重试");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 发布或者撤回广告
	 * @param aid 广告ID
	 * @param isClose 广告状态
	 * @return
	 */
	public String changeStatus() {
		adv = this.advManager.getAdv(aid);
		if(adv != null || !adv.equals("")){
			if(isClose == 1){
				adv.setIsclose(0);
				adv.setEndtime(DateUtil.getDateline());
			}else if(isClose == 0){
				adv.setIsclose(1);
				adv.setBegintime(DateUtil.getDateline());
			}else{
				this.showErrorJson("操作失败");
			}
		}
		try {
			this.advManager.updateAdv(adv);
			this.showSuccessJson("操作成功");
		} catch (RuntimeException e) {
			this.showErrorJson("操作失败");
			logger.error("开启广告失败", e);
		} catch (Exception e) {
			this.showErrorJson("操作失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	public IAdvManager getAdvManager() {
		return advManager;
	}

	public void setAdvManager(IAdvManager advManager) {
		this.advManager = advManager;
	}

	public Adv getAdv() {
		return adv;
	}

	public void setAdv(Adv adv) {
		this.adv = adv;
	}

	public File getPic() {
		return pic;
	}

	public void setPic(File pic) {
		this.pic = pic;
	}

	public String getPicFileName() {
		return picFileName;
	}

	public void setPicFileName(String picFileName) {
		this.picFileName = picFileName;
	}

	public Integer getAid() {
		return aid;
	}

	public void setAid(Integer aid) {
		this.aid = aid;
	}

	public Integer getIsClose() {
		return isClose;
	}

	public void setIsClose(Integer isClose) {
		this.isClose = isClose;
	}

}
