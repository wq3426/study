package com.enation.app.b2b2c.core.action.api.payment;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.service.ICarInfoManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.OrderType;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * 支付api
 * @author kingapex
 *2013-9-4下午7:21:31
 */
 
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("payment")
public class PaymentApiAction extends WWAction {
	private IPaymentManager paymentManager;
	private IOrderManager orderManager;
	private IStoreOrderManager storeOrderManager;
	private IMemberAddressManager memberAddressManager;
	private IRegionsManager regionsManager; 
	private Integer addrid;
	private ICarInfoManager carInfoManager;
	private IBonusManager bonusManager;
	/**
	 * 跳转到第三方支付页面
	 * @param orderid 订单Id
	 * @return
	 */
	private String[] split;
	public String execute(){
		try{
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			
			//订单id参数
			Integer orderId=  StringUtil.toInt( request.getParameter("orderid") ,null);
			if(orderId == null ){
				showErrorJson("必须传递orderid参数");
				return this.JSON_MESSAGE;
			}
			
			
			//支付方式id参数
			Integer paymentId=  StringUtil.toInt( request.getParameter("paymentid") ,null);
			StoreOrder order = storeOrderManager.get(orderId);
			
			if(order==null){
				showErrorJson("该订单不存在");
				return this.JSON_MESSAGE;
			}
			
			if(order.getStatus() == 2 && order.getPay_status()==2){
				showErrorJson("您已经支付过了");
				return this.JSON_MESSAGE;
			}

			//如果是保养订单，检查是否还有预约保养工位，订单是否可支付
			if(order.getOrder_type() == OrderType.REPAIR){
				JSONObject payObj = storeOrderManager.isPayable(order);
				int result = payObj.getInt("result");
				if(result == 0){
					showErrorJson(payObj.getString("message"));
					return this.JSON_MESSAGE;
				}
			}
			
			//获取车辆在下单店铺的保养币和是否使用保养币
			double repair_coin = 0;
			if(order.getOrder_type() == OrderType.REPAIR){
				repair_coin = Double.valueOf(carInfoManager.getCarRepairCoin(order.getService_store_id(), order.getCarplate()));
			}
			Integer is_use_repair_coin = StringUtil.toInt(request.getParameter("is_use_repair_coin"), 0);
			
			//获取用户使用安全出行奖励和是否使用奖励
			Integer isUseGain = StringUtil.toInt(request.getParameter("isUseGain"),0);
			Double useGain = 0d;
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			decimalFormat.setRoundingMode(RoundingMode.HALF_UP); 
			String bonus_ids = request.getParameter("bonus_ids");//校验用户使用优惠券
			
			//获取用户该车牌奖励额度
			Double totalgain = 0d;
			List carInfoList = carInfoManager.getCarInfoByCarplate(order.getCarplate());
			if(carInfoList != null){
				net.sf.json.JSONObject carInfo = net.sf.json.JSONObject.fromObject(carInfoList.get(0));
				totalgain = carInfo.getDouble("totalgain");
			}
			totalgain = totalgain < 0d ? 0d : totalgain;
			totalgain = StringUtil.toDouble(decimalFormat.format(totalgain),0d);
			Double orderRewordsLimit = 0d; //计算商品最高可用多少			
			Double bonus_price = 0d;
			if(order.getParent_id() == null){//走主订单判断流程
				List<StoreOrder> storeOrders = storeOrderManager.getChildOrder(order.getOrder_id());
				
				if(!StringUtil.isNull(bonus_ids)){//
					String[] bonusSplit = bonus_ids.split(",");
					if(bonusSplit.length > 1){//使用俩张优惠券
						for(int i = 0 ; i < storeOrders.size() ;i++){
							StoreOrder storeOrder= storeOrders.get(i);
							bonusManager.updateOrdersn(storeOrder.getOrder_id(),storeOrder.getSn());
							MemberBonus  memberBonus = bonusManager.getBonus(bonus_ids,storeOrder.getStore_id());
							if(memberBonus==null){
								showErrorJson("您当前选择的优惠券已失效");
								return this.JSON_MESSAGE;
							}
							if(order.getNeedPayMoney() < memberBonus.getMin_goods_amount()){
								showErrorJson("您当前订单金额不够使用[" + memberBonus.getType_name() + "]优惠券");
								return this.JSON_MESSAGE;
							}
							memberBonus.setOrder_id(storeOrder.getOrder_id());
							memberBonus.setOrder_sn(storeOrder.getSn());
							bonusManager.editMemberBonus(memberBonus);
							bonus_price=CurrencyUtil.add(bonus_price, memberBonus.getBonus_money());
							storeOrder.setBonus_money(memberBonus.getBonus_money());
						}
					}else{//使用一张优惠券
						MemberBonus  memberBonus = bonusManager.getBonus(Integer.parseInt(bonus_ids));
						if(memberBonus==null){
							showErrorJson("您当前选择的优惠券已失效");
							return this.JSON_MESSAGE;
						}
						for(int i = 0 ; i < storeOrders.size() ;i++){
							StoreOrder storeOrder= storeOrders.get(i);
							bonusManager.updateOrdersn(storeOrder.getOrder_id(),storeOrder.getSn());
							memberBonus = bonusManager.getBonus(bonus_ids,storeOrder.getStore_id());
							if(memberBonus!=null){
								if(order.getNeedPayMoney() < memberBonus.getMin_goods_amount()){
									showErrorJson("您当前订单金额不够使用[" + memberBonus.getType_name() + "]优惠券");
									return this.JSON_MESSAGE;
								}
								memberBonus.setOrder_id(storeOrder.getOrder_id());
								memberBonus.setOrder_sn(storeOrder.getSn());
								bonusManager.editMemberBonus(memberBonus);
								bonus_price=CurrencyUtil.add(bonus_price, memberBonus.getBonus_money());
								storeOrder.setBonus_money(memberBonus.getBonus_money());
							}
						}
					}
				}
				
				
				if (isUseGain == 1  && totalgain > 0) {
					if (storeOrders != null && storeOrders.size() > 0) {
						for (int orderIndex = 0; orderIndex < storeOrders.size(); orderIndex++) {
							orderRewordsLimit = 0d;
							JSONArray itemList = JSONArray.fromObject(storeOrders.get(orderIndex).getItemList());
							for (int itemIndex = 0; itemIndex < itemList.size(); itemIndex++) {
								JSONObject item = JSONObject.fromObject(itemList.get(itemIndex));
								orderRewordsLimit = CurrencyUtil.add(orderRewordsLimit, CurrencyUtil.mul((Double) item.get("rewards_limit"),(Integer) item.get("num") ));   
							}
							
							Double childUseGain = totalgain < orderRewordsLimit ? totalgain : orderRewordsLimit; 
							Double childBonusMoney = storeOrders.get(orderIndex).getBonus_money();
							double still_pay_money  = CurrencyUtil.sub(storeOrders.get(orderIndex).getNeed_pay_money(),childBonusMoney==null?0d:childBonusMoney);
							if(still_pay_money < childUseGain){
								childUseGain = still_pay_money;
							}
							totalgain = CurrencyUtil.sub(totalgain, childUseGain);
							useGain = CurrencyUtil.add(useGain, childUseGain);
							storeOrderManager.updateGain(childUseGain,storeOrders.get(orderIndex).getOrder_id());
						}
					}
				}
				
			}
			
			
			
			else{//走子订单判断流程
				
				//先使用优惠券
				if(!StringUtil.isNull(bonus_ids)){//
					bonusManager.updateOrdersn(order.getOrder_id(),order.getSn());
					MemberBonus memberBonus = bonusManager.getBonus(bonus_ids,order.getStore_id());
					if(memberBonus==null){
						showErrorJson("优惠券已失效或无法使用");
						return this.JSON_MESSAGE;
					}
					if(order.getNeedPayMoney() < memberBonus.getMin_goods_amount()){
						showErrorJson("您当前订单金额不够使用[" + memberBonus.getType_name() + "]优惠券");
						return this.JSON_MESSAGE;
					}
					memberBonus.setBonus_id(memberBonus.getBonus_id());
					memberBonus.setOrder_id(order.getOrder_id());
					memberBonus.setOrder_sn(order.getSn());
					bonusManager.editMemberBonus(memberBonus);
					bonus_price=CurrencyUtil.add(bonus_price,memberBonus.getBonus_money());
				}
				
				//如果使用了安全驾驶奖励
				if(isUseGain == 1 && totalgain > 0){ //奖励可能会出现负数的情况
					//获取商品可用安全奖励
					orderRewordsLimit = 0d;
					JSONArray itemList = JSONArray.fromObject(order.getItemList());
					for (int itemIndex = 0; itemIndex < itemList.size(); itemIndex++) {
						JSONObject item = JSONObject.fromObject(itemList.get(itemIndex));
						orderRewordsLimit = CurrencyUtil.add(orderRewordsLimit, CurrencyUtil.mul((Double) item.get("rewards_limit"),(Integer) item.get("num") ));   
					}
					double still_pay_money  = CurrencyUtil.sub(order.getNeed_pay_money(), bonus_price);
					useGain = totalgain < orderRewordsLimit ? totalgain : orderRewordsLimit;
					if(still_pay_money < useGain){
						useGain = still_pay_money;
					}
				}
				
				//如果使用保养币(目前只是保养支付时支持)
				if(is_use_repair_coin == 1 && repair_coin > 0){
					double still_pay_money  = CurrencyUtil.sub(order.getNeed_pay_money(), bonus_price);
					repair_coin = still_pay_money < repair_coin ? Math.ceil(still_pay_money) : repair_coin;
				}else{
					repair_coin = 0;
				}
			}
			
			request.setAttribute("bonus_price",decimalFormat.format(bonus_price));
			order.setIsUseGain(isUseGain);
			order.setGain(useGain);
			order.setIs_use_repair_coin(is_use_repair_coin);
			order.setRepair_coin(repair_coin);
			orderManager.edit(order);
			
			//红包金额传到后台
			
			//如果没有传递支付方式id，则使用订单中的支付方式
			if(paymentId==null){
				paymentId = order.getPayment_id(); 
			}
			
			PayCfg payCfg = this.paymentManager.get(paymentId);
	
			IPaymentEvent paymentPlugin = SpringContextHolder.getBean(payCfg.getType());
			String payhtml = "";
			if(CurrencyUtil.sub(CurrencyUtil.sub(CurrencyUtil.sub(order.getNeed_pay_money(),bonus_price),useGain),repair_coin) > 0 ){//支付价格大于0走正常支付
				payhtml = paymentPlugin.onPay(payCfg, order);
				JSONObject result = JSONObject.fromObject(payhtml);
				result.getJSONObject("data").put("isZeroOrder", 0);
				payhtml = result.toString();
			}else{//支付价格为0走零元支付
				payhtml = storeOrderManager.paySuccess(order,"");
				JSONObject result = JSONObject.fromObject(payhtml);
				payhtml = result.toString();
			}
			// 用户更换了支付方式，更新订单的数据
			if (order.getPayment_id().intValue() != paymentId.intValue()) {
				this.orderManager.updatePayMethod(orderId, paymentId, payCfg.getType(), payCfg.getName());
			}
			this.json=(payhtml);
		}catch(Exception e){
			showErrorJson("支付失败");
			this.showErrorJson(e.getMessage());
			e.printStackTrace();
		}
		return this.JSON_MESSAGE;
	}
	
	
	
	
	

	/**
	 * 检查是否支持货到付款
	 * 
	 * @return result result 1.支持.0.不支持
	 */
	public String checkSupportCod() {
		MemberAddress memberAddress =memberAddressManager.getAddress(addrid);
		try {
			if (regionsManager.get(memberAddress.getRegion_id()).getCod() == 1) {
				this.showSuccessJson("支持货到付款");
			} else {
				this.showErrorJson("不支持货到付款");
			}
		} catch (Exception e) {  
			this.showErrorJson("不支持货到付款");
		}
		return this.JSON_MESSAGE;
	}
 

	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}


	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}


	public IOrderManager getOrderManager() {
		return orderManager;
	}


	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}



	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}



	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}



	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}



	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}



	public Integer getAddrid() {
		return addrid;
	}



	public void setAddrid(Integer addrid) {
		this.addrid = addrid;
	}



	public ICarInfoManager getCarInfoManager() {
		return carInfoManager;
	}



	public void setCarInfoManager(ICarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}






	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}






	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}






	public IBonusManager getBonusManager() {
		return bonusManager;
	}






	public void setBonusManager(IBonusManager bonusManager) {
		this.bonusManager = bonusManager;
	}
	
	
	
}
