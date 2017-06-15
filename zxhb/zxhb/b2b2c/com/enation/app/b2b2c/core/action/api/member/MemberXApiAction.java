package com.enation.app.b2b2c.core.action.api.member;
 
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStorePromotionManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.AppMessage;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.IMemberMessageManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;

@Component 
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("memberx")
public class MemberXApiAction extends WWAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3208137137117077528L;
	private IMemberMessageManager memberMessageManager; 
	private IStorePromotionManager storePromotionManager;
	
	private IMemberManager memberManager;
	private Integer[] member_ids;	//会员id集合
	private int status;				//状态
	private AppMessage appmessage;	//message
	private int bonusid;//	优惠券id
	private int messageid;//消息id
	private double price;// 价格
	private String time;// 过期时间
	private String detail;//维护内容
	private int	distance;//路程 
	private int member_id;//会员id
	private String insuranceName;//保险
	/**
	 * 发送消息
	 * @return
	 */
	public String sendMessage(){
		try {
			if(member_ids!=null){
				for (Integer mid : member_ids) { 
					memberMessageManager.sendmessage(messageid, mid);
				}
			}
			this.showSuccessJson("发送成功");
		} catch (Exception e) {
			
			this.showSuccessJson("服务器异常");
		} 
		return this.JSON_MESSAGE; 
	}
	
	/**
	 * 修改状态
	 * @return
	 */
	public String updateStatus(){
		try {
			memberManager.updateStatus(member_ids, status);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("异常，请稍后重试");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 发送优惠券
	 * @return
	 */
	public String sendBouns(){
		
		if(ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)==null){
//			如果当前管理员不为空
			this.showErrorJson("错误的操作");
		}
		
		try {
			if(member_ids!=null){
				for (Integer mid : member_ids) {
					storePromotionManager.receive_bonus(mid, ((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id(), bonusid);
				}
			} 
			this.showSuccessJson("发送成功");
		} catch (Exception e) { 
			e.printStackTrace();
			this.showErrorJson("发送失败");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 保养
	 * @return
	 */
	public String maintain(){ 
		try {
			memberManager.maintain(price, DateUtil.getDateline(), detail, distance, member_id);
			this.showSuccessJson("保存成功");
		} catch (Exception e) { 
			this.showErrorJson("保存失败，请稍后再试");
		}
		return this.JSON_MESSAGE; 
	}
	/**
	 * 保险
	 * @return
	 */
	public String insurance(){ 
		try { 
			memberManager.insurance(insuranceName, price, DateUtil.getDateline(time), member_id); 
			this.showSuccessJson("保存成功");
		} catch (Exception e) { 
			this.showErrorJson("保存失败，请稍后再试");
		}
		return this.JSON_MESSAGE; 
	}
	
	/**
	 * 获取用户所有未使用的优惠卷
	 * @return
	 */
	public String getBonusList(){
		
		try {
			int memberId = UserConext.getCurrentMember().getMember_id();
			List<StoreBonus> list = this.storePromotionManager.getBonusByMemberId(memberId);
			this.json = JsonMessageUtil.getListJson(list);
		} catch(RuntimeException e) {
			this.showErrorJson("获取失败");
		}
		
		return this.JSON_MESSAGE;
	}
	

	public IMemberMessageManager getMemberMessageManager() {
		return memberMessageManager;
	}

	public void setMemberMessageManager(IMemberMessageManager memberMessageManager) {
		this.memberMessageManager = memberMessageManager;
	}

	public IStorePromotionManager getStorePromotionManager() {
		return storePromotionManager;
	}

	public void setStorePromotionManager(
			IStorePromotionManager storePromotionManager) {
		this.storePromotionManager = storePromotionManager;
	}

	public Integer[] getMember_ids() {
		return member_ids;
	}

	public void setMember_ids(Integer[] member_ids) {
		this.member_ids = member_ids;
	} 
	public AppMessage getAppmessage() {
		return appmessage;
	}

	public void setAppmessage(AppMessage appmessage) {
		this.appmessage = appmessage;
	}

	public int getMessageid() {
		return messageid;
	}

	public void setMessageid(int messageid) {
		this.messageid = messageid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getBonusid() {
		return bonusid;
	}

	public void setBonusid(int bonusid) {
		this.bonusid = bonusid;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getMember_id() {
		return member_id;
	}

	public void setMember_id(int member_id) {
		this.member_id = member_id;
	}

	public String getInsuranceName() {
		return insuranceName;
	}

	public void setInsuranceName(String insuranceName) {
		this.insuranceName = insuranceName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
 
	
}
