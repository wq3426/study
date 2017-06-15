package com.enation.app.base.core.action;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.service.IAdColumnManager;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * 广告Action
 * @author lzf 2010-3-2 上午09:46:16 version 1.0
 * @author kanon 2015-9-24 version 1.1 添加注释
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("adv")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/adv/adv_list.html"),
	@Result(name="add", type="freemarker", location="/core/admin/adv/adv_input.html"), 
	@Result(name="edit", type="freemarker", location="/core/admin/adv/adv_edit.html") 
})
public class AdvAction extends WWAction {

	private IAdColumnManager adColumnManager;
	private IAdvManager advManager;
	private List<AdColumn> adColumnList;
	private Adv adv;
	private Long acid;
	private String advname; //搜索的广告名
	private Long advid;
	private Integer[] aid;
	private File pic;
	private String picFileName;
	private Date mstartdate;
	private Date menddate;
	private String order;
	private File url;
	private String imgPath;
	
	/**
	 * 跳转至广告列表
	 * @return 广告列表页面
	 */
	public String list() {
		return "list";
	}
	
	/**
	 * 跳转至广告添加页面
	 * @param adColumnList 广告位列表
	 * @return 广告添加页面
	 */
	public String add() {
		adColumnList = this.adColumnManager.listAllAdvPos();
		return "add";
	}
	
	/**
	 * 跳转至广告详细页面
	 * @param adColumnList 广告位列表
	 * @param advid 广告ID
	 * @param adv 广告
	 * @param imgPath 广告图片地址
	 * @return 广告详细页面
	 */
	public String edit() {
		adColumnList = this.adColumnManager.listAllAdvPos();
		adv = this.advManager.getAdvDetail(advid);
		if(adv.getAtturl()!=null&&!StringUtil.isEmpty(adv.getAtturl())){			
			imgPath = UploadUtil.replacePath(adv.getAtturl());
			
		}
		return "edit";
	}
	
	/**
	 * 获取广告分页列表JSON
	 * @param adColumnList 广告位列表
	 * @param advname 广告位名称
	 * @return 广告分页列表JSON
	 */
	public String listJson() {
		adColumnList = this.adColumnManager.listAllAdvPos();
		this.webpage = advManager.search(advname, this.getPage(), this.getPageSize(),this.order);
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	/**
	 * 删除广告
	 * @param aid 广告Id
	 * @return 删除状态
	 */
	public String delete() {
		if(EopSetting.IS_DEMO_SITE){
			for(Integer id:aid){
				if(id<=21){
					this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
					return JSON_MESSAGE;
				}
			}
		}
		
		try {
			this.advManager.delAdvs(aid);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败");
			logger.error("广告删除失败", e);
		}
		
		return this.JSON_MESSAGE;
	}

	
	/**
	 * 保存新增广告
	 * @param pic 广告图片
	 * @param picFileName 广告图片名称 
	 * @param adv 广告
	 * @return 保存状态
	 */
	public String addSave() {
		if (pic != null) {

			if (FileUtil.isAllowUp(picFileName)) {
				String path = UploadUtil.upload(this.pic,this.picFileName, "adv");
				adv.setAtturl(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件。");
				return JSON_MESSAGE;
			}
		}
		if(menddate.getTime()<mstartdate.getTime()){
			this.showSuccessJson("截止时间小于开始时间");
			return JSON_MESSAGE;
		}
		adv.setBegintime(mstartdate.getTime()/1000);
		adv.setEndtime(menddate.getTime()/1000);
		adv.setDisabled("false");
		try {
			this.advManager.addAdv(adv);
			this.showSuccessJson("新增广告成功");
		} catch (RuntimeException e) {
			this.showErrorJson("新增广告失败");
			logger.error("新增广告失败", e);
		}
		return JSON_MESSAGE;
	}
	/**
	 * 保存修改广告
	 * @param pic 图片地址
	 * @param picFileName 图片名称
	 * @param mstartdate 广告开始时间
	 * @param menddate 广告结束时间
	 * @return 修改广告状态
	 */
	public String editSave() {
		if (pic != null) {
			if (FileUtil.isAllowUp(picFileName)) {
				String path = UploadUtil.upload(this.pic,this.picFileName, "adv");
				adv.setAtturl(path);
			} else {
				this.showErrorJson("不允许上传的文件格式，请上传gif,jpg,bmp,swf格式文件。");
				return JSON_MESSAGE;
			}
		}
		adv.setBegintime(mstartdate.getTime()/1000);
		adv.setEndtime(menddate.getTime()/1000);
		try {
			this.advManager.updateAdv(adv);
			this.showSuccessJson("修改广告成功");
		} catch (Exception e) {
			this.showErrorJson("修改广告失败");
			logger.error("修改广告失败", e);
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 停止广告
	 * @param advid 广告ID
	 * @param adv 广告
	 * @return 停止状态
	 */
	public String stop() {
		adv = this.advManager.getAdvDetail(advid);
		adv.setIsclose(1);
		try {
			this.advManager.updateAdv(adv);
			this.showSuccessJson("操作成功");
		} catch (RuntimeException e) {
			this.showErrorJson("操作失败");
			logger.error("停止广告失败", e);
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("数据异常，操作失败");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 开启广告
	 * @param advid 广告ID
	 * @param adv 广告
	 * @return 开启广告状态
	 */
	public String start() {
		adv = this.advManager.getAdvDetail(advid);
		adv.setIsclose(0);
		try {
			this.advManager.updateAdv(adv);
			this.showSuccessJson("操作成功");
		} catch (RuntimeException e) {
			this.showErrorJson("操作失败");
			logger.error("开启广告失败", e);
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("数据异常，操作失败");
		}
		return JSON_MESSAGE;
	}

	public IAdColumnManager getAdColumnManager() {
		return adColumnManager;
	}

	public void setAdColumnManager(IAdColumnManager adColumnManager) {
		this.adColumnManager = adColumnManager;
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

	public Long getAcid() {
		return acid;
	}

	public void setAcid(Long acid) {
		this.acid = acid;
	}

	public Long getAdvid() {
		return advid;
	}

	public void setAdvid(Long advid) {
		this.advid = advid;
	}

	

	public Integer[] getAid() {
		return aid;
	}

	public void setAid(Integer[] aid) {
		this.aid = aid;
	}

	public List<AdColumn> getAdColumnList() {
		return adColumnList;
	}

	public void setAdColumnList(List<AdColumn> adColumnList) {
		this.adColumnList = adColumnList;
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

	public Date getMstartdate() {
		return mstartdate;
	}

	public void setMstartdate(Date mstartdate) {
		this.mstartdate = mstartdate;
	}

	public Date getMenddate() {
		return menddate;
	}

	public void setMenddate(Date menddate) {
		this.menddate = menddate;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getAdvname() {
		return advname;
	}

	public void setAdvname(String advname) {
		this.advname = advname;
	}

	public File getUrl() {
		return url;
	}

	public void setUrl(File url) {
		this.url = url;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

}
