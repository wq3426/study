package com.enation.app.b2b2c.core.tag.store;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeStatus;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeType;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberCommentManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.eop.sdk.utils.DateUtil;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
import net.sf.json.JSONObject;
@Component
/**
 * 我的店铺其他信息Tag
 * @author LiFenLong
 *
 */
public class MyStoreDetailOtherTag extends BaseFreeMarkerTag{
	private IStoreOrderManager storeOrderManager;
	private IStoreGoodsManager storeGoodsManager;
	private IStoreMemberCommentManager storeMemberCommentManager;
	private IStoreMemberManager storeMemberManager;
	private Integer store_id;
	private IReckoningManager reckoningManager;
	private IStoreManager storeManager;
	private IMemberManager memberManager;
	
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		StoreMember member = storeMemberManager.getStoreMember();
		Map result = new HashMap();

		/**
		 * 店铺账户金额区域
		 */
		//获取当前店铺已提现金额（包含未处理和已处理），待结算总额，账户总额
		double settlementCountHistory = reckoningManager.getSettlementCount(ReckoningTradeType.draw_money.getIndex(),null,member.getStore_id());
		double noSettlementCount = reckoningManager.getSettlementCount(ReckoningTradeType.settle_accounts.getIndex(),ReckoningTradeStatus.no_settle_accounts.getIndex(),member.getStore_id());
		double yetSettlementCount = storeManager.getBalance(member.getStore_id());
		double yetSeetlementOrder = reckoningManager.getSettlementCount(ReckoningTradeType.settle_accounts.getIndex(),ReckoningTradeStatus.yet_settle_accounts.getIndex(),member.getStore_id());
		result.put("settlementCountHistory", settlementCountHistory);
		result.put("noSettlementCount", noSettlementCount);
		result.put("yetSettlementCount", yetSettlementCount);
		result.put("yetSeetlementOrder", yetSeetlementOrder);
		/**
		 * 店铺提示区域
		 */
		int notMarket = storeGoodsManager.getStoreGoodsNum(0);				//店铺仓库中商品数量
		int ingMarket = storeGoodsManager.getStoreGoodsNum(1);				//店铺中出售中的商品数量
		int notReply = storeMemberCommentManager.getCommentCount(2,member.getStore_id());//卖家未处理得商品留言
		int repairOrder = storeOrderManager.getRepairOrderCount();   //今日需要保养的订单
		
		result.put("notMarket", notMarket);
		result.put("ingMarket", ingMarket);
		result.put("notReply", notReply);
		result.put("repairOrder", repairOrder);
		result.put("todayDate", DateUtil.toString(new Date(), "yyyy-MM-dd"));
		
		/**
		 * 签约用户区域
		 */
		JSONObject memberObj = memberManager.getUserContractInfo(member.getStore_id());
		result.put("carTotalCount", memberObj.get("carTotalCount"));//签约用户总数量
		result.put("contractCurrentWeek", memberObj.get("contractCurrentWeek"));//本周增加用户
		result.put("discontractCurrentWeek", memberObj.get("discontractCurrentWeek"));//本周流失用户
		result.put("consumeRatio", memberObj.get("consumeRatio"));//用户消费率
		result.put("activeRatio", memberObj.get("activeRatio"));//用户活跃率
		
		/**
		 * 店铺订单区域
		 */
		int storeAllOrder = storeOrderManager.getStoreOrderNum(-999);		//店铺全部订单
		int orderNotPay = storeOrderManager.getStoreOrderNum("0,9", "0");	//未付款订单'0,9','0'
		int orderNotShip = storeOrderManager.getStoreOrderNum("2,5,6", "2");	//等待服务
		int orderShiped = storeOrderManager.getStoreOrderNum("7", "2");	//待评价、已完成的订单
		int orderCancle = storeOrderManager.getStoreOrderNum(8);			//已取消订单
		
		result.put("storeAllOrder", storeAllOrder);
		result.put("orderNotPay", orderNotPay);
		result.put("orderNotShip", orderNotShip);
		result.put("orderShiped", orderShiped);
		result.put("orderCancle", orderCancle);
		
		
		/**
		 * 消息中心区域
		 */
		
		
		return result;
	}
	
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public IStoreMemberCommentManager getStoreMemberCommentManager() {
		return storeMemberCommentManager;
	}

	public void setStoreMemberCommentManager(
			IStoreMemberCommentManager storeMemberCommentManager) {
		this.storeMemberCommentManager = storeMemberCommentManager;
	}

	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}
	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}
	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}
	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public IReckoningManager getReckoningManager() {
		return reckoningManager;
	}

	public void setReckoningManager(IReckoningManager reckoningManager) {
		this.reckoningManager = reckoningManager;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
}
