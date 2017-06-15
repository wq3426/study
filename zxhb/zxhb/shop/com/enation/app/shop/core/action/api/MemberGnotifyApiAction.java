package com.enation.app.shop.core.action.api;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.IGnotifyManager;
import com.enation.framework.action.WWAction;


/**
 * 会员缺货登记
 * @author whj	
 *2014-02-26下午5:00:00
 */
@Component
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("gnotify")
@InterceptorRef(value="apiRightStack")
public class MemberGnotifyApiAction extends WWAction{

	private IGnotifyManager gnotifyManager;
	private Integer gnotify_id;
	private Integer goodsid;
	private Integer gnotify;
	private Integer productid;
	
	public String gnotifyDel() {
		try {
			this.gnotifyManager.deleteGnotify(gnotify_id);
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			this.showErrorJson("删除失败[" + e.getMessage() + "]");

		}
		return WWAction.JSON_MESSAGE;
	}
	
	public String add(){
		try {
			
			int num = this.gnotifyManager.getGnotifyBymember(goodsid, productid);
			if(num==0){
				this.gnotifyManager.addGnotify(goodsid,productid);
				this.showSuccessJson("登记成功");
			}else{
				this.showErrorJson("已经登记过了");
			}
		} catch (Exception e) {
			this.showErrorJson("登记失败，请重试");
		}
		
		return JSON_MESSAGE;
	}
	
	//set get
	
	public IGnotifyManager getGnotifyManager() {
		return gnotifyManager;
	}
	public void setGnotifyManager(IGnotifyManager gnotifyManager) {
		this.gnotifyManager = gnotifyManager;
	}
	public Integer getGnotify_id() {
		return gnotify_id;
	}
	public void setGnotify_id(Integer gnotify_id) {
		this.gnotify_id = gnotify_id;
	}

	public Integer getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(Integer goodsid) {
		this.goodsid = goodsid;
	}

	public Integer getGnotify() {
		return gnotify;
	}

	public void setGnotify(Integer gnotify) {
		this.gnotify = gnotify;
	}

	public Integer getProductid() {
		return productid;
	}

	public void setProductid(Integer productid) {
		this.productid = productid;
	}
	
}
