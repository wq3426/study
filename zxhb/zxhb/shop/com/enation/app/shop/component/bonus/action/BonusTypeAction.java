package com.enation.app.shop.component.bonus.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 优惠卷类型管理
 * @author kingapex
 *2013-8-17下午12:02:39
 */
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Results({
	 @Result(name="list", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_type_list.html") ,
	 @Result(name="add", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_type_add.html"),
	 @Result(name="edit", type="freemarker", location="/com/enation/app/shop/component/bonus/action/html/bonus_type_edit.html") 
})
public class BonusTypeAction extends WWAction {
	
	private IBonusTypeManager bonusTypeManager;
	private BonusType bonusType;
	private String useTimeStart;
	private String useTimeEnd;
	private String sendTimeStart;
	private String sendTimeEnd;
	private int typeid;
	private Integer[] type_id;
	
	public String list(){
		return "list";
	}
	
	public String listJson(){
		this.webpage = this.bonusTypeManager.list(this.getPage(),this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	
	public String add(){
		
		return "add";
	}

	
	public String edit(){
		
		this.bonusType = this.bonusTypeManager.get(typeid);
		return "edit";
	}
	
	public String saveAdd(){
		
		if( StringUtil.isEmpty( bonusType.getRecognition() )){
			this.showErrorJson("请输入优惠卷识别码");
			return this.JSON_MESSAGE;
		}
		
		if( StringUtil.isEmpty( bonusType.getType_name() )){
			this.showErrorJson("请输入类型名称");
			return this.JSON_MESSAGE;
		}
		
		
		if( bonusType.getType_money() ==null  ){
			this.showErrorJson("请输入金额");
			return this.JSON_MESSAGE;
		}
		
		if( StringUtil.isEmpty(useTimeStart)){
			this.showErrorJson("请输入使用起始日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setUse_start_date(  DateUtil.getDateline(useTimeStart));
		
		if( StringUtil.isEmpty(useTimeEnd)){
			this.showErrorJson("请输入使用结束日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setUse_end_date( DateUtil.getDateline(useTimeEnd));

		if( DateUtil.getDateline(useTimeEnd)<= DateUtil.getDateline(useTimeStart)){
			this.showErrorJson("结束日期不能小于起始日期");
			return this.JSON_MESSAGE;
		}
		if(!StringUtil.isEmpty(sendTimeStart)){
			bonusType.setSend_start_date(DateUtil.getDateline(sendTimeStart));
		}
		
		if(!StringUtil.isEmpty(sendTimeEnd)){
			bonusType.setSend_end_date(DateUtil.getDateline(sendTimeEnd));
		}
		
		bonusType.setBelong(1);
		try {
			this.bonusTypeManager.add(bonusType);
			this.showSuccessJson("保存优惠卷类型成功");
		} catch (Throwable e) {
			this.logger.error("保存优惠卷类型出错", e);
			this.showErrorJson("保存优惠卷类型出错"+e.getMessage());
		}

		
		return this.JSON_MESSAGE;
	}
	
	
	public String saveEdit(){
		
		

		if( StringUtil.isEmpty( bonusType.getRecognition() )){
			this.showErrorJson("请输入优惠卷识别码");
			return this.JSON_MESSAGE;
		}
		
		if( StringUtil.isEmpty( bonusType.getType_name() )){
			this.showErrorJson("请输入类型名称");
			return this.JSON_MESSAGE;
		}
		
		
		if( bonusType.getType_money() ==null  ){
			this.showErrorJson("请输入金额");
			return this.JSON_MESSAGE;
		}
		
		if( StringUtil.isEmpty(this.useTimeStart)){
			this.showErrorJson("请输入使用起始日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setUse_start_date(  DateUtil.getDateline(useTimeStart));
		
		if( StringUtil.isEmpty(this.useTimeEnd)){
			this.showErrorJson("请输入使用结束日期");
			return this.JSON_MESSAGE;
		}
		if( DateUtil.getDateline(useTimeEnd)<= DateUtil.getDateline(useTimeStart)){
			this.showErrorJson("结束日期不能小于起始日期");
			return this.JSON_MESSAGE;
		}
		bonusType.setUse_end_date( DateUtil.getDateline(useTimeEnd));
		
		if(!StringUtil.isEmpty(sendTimeStart)){
			bonusType.setSend_start_date(DateUtil.getDateline(sendTimeStart));
		}
		
		if(!StringUtil.isEmpty(sendTimeEnd)){
			bonusType.setSend_end_date(DateUtil.getDateline(sendTimeEnd));
		}
		
		bonusType.setBelong(1);
		try {
			bonusTypeManager.update(bonusType);
			this.showSuccessJson("保存优惠卷类型成功");
		} catch (Throwable e) {
			this.logger.error("保存优惠卷类型出错", e);
			this.showErrorJson("保存优惠卷类型出错"+e.getMessage());
		}
		
		
		return this.JSON_MESSAGE;
	}
	
	
	public String delete(){
		
		try {
			this.bonusTypeManager.delete(type_id);
			this.showSuccessJson("删除优惠卷类型成功");
		} catch (Throwable e) {
			this.logger.error("删除优惠卷类型出错", e);
			this.showErrorJson("删除优惠卷类型出错"+e.getMessage());
		}
		
		return this.JSON_MESSAGE;
	}


	public IBonusTypeManager getBonusTypeManager() {
		return bonusTypeManager;
	}


	public void setBonusTypeManager(IBonusTypeManager bonusTypeManager) {
		this.bonusTypeManager = bonusTypeManager;
	}


	public String getUseTimeStart() {
		return useTimeStart;
	}


	public void setUseTimeStart(String useTimeStart) {
		this.useTimeStart = useTimeStart;
	}


	public String getUseTimeEnd() {
		return useTimeEnd;
	}


	public void setUseTimeEnd(String useTimeEnd) {
		this.useTimeEnd = useTimeEnd;
	}


	public String getSendTimeStart() {
		return sendTimeStart;
	}


	public void setSendTimeStart(String sendTimeStart) {
		this.sendTimeStart = sendTimeStart;
	}


	public String getSendTimeEnd() {
		return sendTimeEnd;
	}


	public void setSendTimeEnd(String sendTimeEnd) {
		this.sendTimeEnd = sendTimeEnd;
	}


	public int getTypeid() {
		return typeid;
	}


	public void setTypeid(int typeid) {
		this.typeid = typeid;
	}


	public BonusType getBonusType() {
		return bonusType;
	}


	public void setBonusType(BonusType bonusType) {
		this.bonusType = bonusType;
	}

	public Integer[] getType_id() {
		return type_id;
	}

	public void setType_id(Integer[] type_id) {
		this.type_id = type_id;
	}



}
