package com.enation.app.b2b2c.core.action.api.store;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStorePromotionManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.CarInfo;
import com.enation.app.shop.core.service.ICarInfoManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;

import net.sf.json.JSONObject;

/**
 * 店铺促销管理
 * @author xulipeng
 *	2015年1月12日23:02:42
 */

@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("promotion")
@Results({
	 @Result(name="edit", type="freemarker", location="/themes/default/b2b2c/storesite/navication_edit.html") 
})
@InterceptorRef(value="apiRightStack",params={"apiRightInterceptor.excludeMethods",""})
public class StorePromotionApiAction extends WWAction {

	private IStorePromotionManager storePromotionManager;
	private IStoreMemberManager storeMemberManager;
	private ICarInfoManager carInfoManager;
	private String type_name;
	private Double min_goods_amount;
	private Double type_money;
	private String useTimeStart;
	private String useTimeEnd;
	private String img_bonus;
	private Integer store_id;
	private Integer type_id;
	private Integer limit_num;
	private Integer is_given;
	private Integer create_num;
	private Integer[] member_id;
	private Integer status;
	private String carplate;//用户车牌号
	private Double payMoney;
	private Integer bonusPage;
	private Integer bonus_id;
	private StoreBonus storeBonus;
	/**
	 * 添加优惠卷
	 * @param member 店铺会员,StoreMember
	 * @param type_name
	 * @param type_money
	 * @param min_goods_amount
	 * @param useTimeStart
	 * @param useTimeEnd
	 * @param bonus StoreBonus
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String add_fullSubtract(){
		Calendar calendar = Calendar.getInstance();
		StoreBonus bonus = this.setParam();
		bonus.setBelong(2);
		if(bonus.getUse_end_date()<bonus.getUse_start_date()){ 
			this.showErrorJson("结束时间不得小于开始时间");
			return this.JSON_MESSAGE;
		} 
		try {
			calendar.setTimeInMillis(bonus.getUse_end_date()-1000);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			bonus.setUse_end_date(calendar.getTimeInMillis());
			bonus.setSend_start_date(bonus.getUse_start_date());
			bonus.setSend_end_date(calendar.getTimeInMillis());
			bonus.setCreate_date(new Date().getTime()/1000);
			this.storePromotionManager.add_FullSubtract(bonus);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 发布/凍結优惠券信息
	 * @return 
	 */
	public String changeStatus(){
		storeBonus = this.storePromotionManager.getBonus(type_id);
			if(storeBonus != null || !storeBonus.equals("")){
				if(status == 1){
					storeBonus.setStatus(2);
				}else if(status == 0){
					storeBonus.setStatus(1);
				}else{
					this.showErrorJson("操作失败");
				}
			}
		try {
			this.storePromotionManager.changeStatus(storeBonus);
			this.showSuccessJson("操作成功!");
		} catch (RuntimeException e) {
			e.getStackTrace();
			this.showErrorJson("数据异常，操作失败");
		}catch (Exception e) {
			this.showErrorJson("数据异常，操作失败");
		}
		return JSON_MESSAGE;
		
	}
 	
	/**
	 * 修改优惠劵
	 * @return
	 */
	public String edit_fullSubtract(){
		Calendar calendar = Calendar.getInstance();
		StoreBonus old = storePromotionManager.getBonus(type_id);
		
		StoreBonus bonus = this.setParam();
		bonus.setType_id(type_id);
		bonus.setBelong(2);  
		bonus.setCreate_date(old.getCreate_date());
		if(bonus.getUse_end_date()<bonus.getUse_start_date()){ 
			this.showErrorJson("结束时间不得小于开始时间");
			return this.JSON_MESSAGE;
		} 
		try {
			calendar.setTimeInMillis(bonus.getUse_end_date()-1000);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			bonus.setUse_end_date(calendar.getTimeInMillis());
			this.storePromotionManager.edit_FullSubtract(bonus);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 用户领取优惠卷
	 * @return
	 */
	public String receiveBonus(){
		Member member=storeMemberManager.getStoreMember();
		try {
			this.storePromotionManager.receive_bonus(member.getMember_id(),store_id, type_id);
			this.showSuccessJson("领取成功!");
		} catch (Exception e) {
			this.showErrorJson(e.getMessage());
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 用户删除优惠劵
	 * @return
	 */
	public String deleteBonus(){
		try {
			this.storePromotionManager.deleteBonus(type_id,bonus_id);
			this.showSuccessJson("删除成功!");
		} catch (RuntimeException e) {
			this.showErrorJson(e.getMessage());
		} catch (Exception e) {
			this.showErrorJson("删除失败!");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 *app端获取该用户绑定4s店发布的优惠券信息 
	 *store_id用户绑定的4s店id
	 */
	public String achieveBonus(){
		Map<String, Object> data = new HashMap<String,Object>();
		Member member=storeMemberManager.getStoreMember();
		bonusPage = (bonusPage == null || bonusPage.equals("")) ? 1 : bonusPage;
		int pageSize = 10;
		Page StoreBonusPage = null;
		int storeId ;
		try {
			
			List<CarInfo> carInfoList=carInfoManager.getCarInfoByCarplate(carplate);
			if(carInfoList.size() > 0){
				net.sf.json.JSONObject carInfo = net.sf.json.JSONObject.fromObject(carInfoList.get(0));
				storeId = carInfo.getInt("repair4sstoreid");
				StoreBonusPage=storePromotionManager.getBonusByStoreIdAndPage(storeId,member,bonusPage,pageSize);
			}else{
				StoreBonusPage=storePromotionManager.getBonusByStoreIdAndPage(1,member,bonusPage,pageSize);
			}
			Object  storeBonusList = JSONObject.fromObject(StoreBonusPage).get("result");
			Long totalCount = StoreBonusPage.getTotalCount();
			data.put("totalCount", totalCount);
			data.put("pageSize", pageSize);
			data.put("page", bonusPage);
			data.put("storeBonusList", storeBonusList);// 信息服务列表
			this.json = "{result : 1,data : " + data + "}";
		} catch (RuntimeException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.error(e.getStackTrace());
			}
		}
		return WWAction.JSON_MESSAGE;
	}
	
	
	/**
	 *app端用户领取的优惠券信息 ，1：已经领取 2：没有使用的优惠券
	 *store_id用户绑定的4s店id
	 */
	public String getBonus(){
		Map<String, Object> data = new HashMap<String,Object>();
		Member member=storeMemberManager.getStoreMember();
		bonusPage = (bonusPage == null || bonusPage.equals("")) ? 1 : bonusPage;
		int pageSize = 10;
		Page StoreBonusPage = null;
		try {
			if(member.getMember_id().toString() != ""){
				StoreBonusPage=storePromotionManager.getBonusBymemberId(member.getMember_id(),bonusPage,pageSize);
			}
			Object  storeBonusList = JSONObject.fromObject(StoreBonusPage).get("result");
			Long totalCount = StoreBonusPage.getTotalCount();
			data.put("totalCount", totalCount);
			data.put("pageSize", pageSize);
			data.put("page", bonusPage);
			data.put("storeBonusList", storeBonusList);// 信息服务列表
			this.json = "{result : 1,data : " + data + "}";
		} catch (RuntimeException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.error(e.getStackTrace());
			}
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 *app端用户支付时加载优惠券信息 ，
	 *1：已经领取  2：绑定的4s店铺
	 *3：最低消费金额小于等于支付金额的优惠券4：没有使用的优惠券
	 *store_id用户绑定的4s店id
	 *payMoney用户支付的金额
	 */
	public String getBonusInPay(){
		Map<String, Object> data = new HashMap<String,Object>();
		Member member=storeMemberManager.getStoreMember();
		List<Map> storeBonusList = null;
		long nowTime = System.currentTimeMillis();
		try {
			if(member.getMember_id().toString() != ""){
				storeBonusList=storePromotionManager.getBonusInPay(member.getMember_id(),store_id,payMoney,nowTime);
			}
			storeBonusList = storeBonusList == null ? new ArrayList<Map>() : storeBonusList;
			data.put("storeBonusList", storeBonusList);// 信息服务列表
			this.json = JsonMessageUtil.getObjectJson(data);
		} catch (RuntimeException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.error(e.getStackTrace());
			}
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 设置优惠卷参数
	 * @return
	 */
	private StoreBonus setParam(){
		
		StoreMember member= this.storeMemberManager.getStoreMember();
		StoreBonus bonus = new StoreBonus();
		bonus.setType_money(type_money);
		bonus.setType_name(type_name);
		bonus.setMin_goods_amount(min_goods_amount);
		bonus.setUse_start_date(DateUtil.getDatelineTwo(useTimeStart));
		bonus.setUse_end_date(DateUtil.getDatelineTwo(useTimeEnd));
		bonus.setStore_id(member.getStore_id());
		bonus.setCreate_num(create_num);
		bonus.setLimit_num(limit_num);
	//	bonus.setIs_given(is_given);                页面已经被注销，不明原因。   本处注销，如果需要，请优先处理本处。whj  2015-05-22
		bonus.setStatus(status);
		return bonus;
	}
	
	//set get

	public IStorePromotionManager getStorePromotionManager() {
		return storePromotionManager;
	}

	public void setStorePromotionManager(
			IStorePromotionManager storePromotionManager) {
		this.storePromotionManager = storePromotionManager;
	}


	public String getType_name() {
		return type_name;
	}


	public void setType_name(String type_name) {
		this.type_name = type_name;
	}


	public Double getMin_goods_amount() {
		return min_goods_amount;
	}


	public void setMin_goods_amount(Double min_goods_amount) {
		this.min_goods_amount = min_goods_amount;
	}


	public Double getType_money() {
		return type_money;
	}


	public void setType_money(Double type_money) {
		this.type_money = type_money;
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


	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}


	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}


	public String getImg_bonus() {
		return img_bonus;
	}


	public void setImg_bonus(String img_bonus) {
		this.img_bonus = img_bonus;
	}

	public Integer getType_id() {
		return type_id;
	}

	public void setType_id(Integer type_id) {
		this.type_id = type_id;
	}

	public Integer getLimit_num() {
		return limit_num;
	}

	public void setLimit_num(Integer limit_num) {
		this.limit_num = limit_num;
	}

	public Integer getIs_given() {
		return is_given;
	}

	public void setIs_given(Integer is_given) {
		this.is_given = is_given;
	}

	public Integer getCreate_num() {
		return create_num;
	}

	public void setCreate_num(Integer create_num) {
		this.create_num = create_num;
	}

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}


	public Integer[] getMember_id() {
		return member_id;
	}


	public void setMember_id(Integer[] member_id) {
		this.member_id = member_id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public ICarInfoManager getCarInfoManager() {
		return carInfoManager;
	}

	public void setCarInfoManager(ICarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}

	public String getCarplate() {
		return carplate;
	}

	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}

	public Double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}

	public Integer getBonusPage() {
		return bonusPage;
	}

	public void setBonusPage(Integer bonusPage) {
		this.bonusPage = bonusPage;
	}

	public Integer getBonus_id() {
		return bonus_id;
	}

	public void setBonus_id(Integer bonus_id) {
		this.bonus_id = bonus_id;
	}

	public StoreBonus getStoreBonus() {
		return storeBonus;
	}

	public void setStoreBonus(StoreBonus storeBonus) {
		this.storeBonus = storeBonus;
	}

}
