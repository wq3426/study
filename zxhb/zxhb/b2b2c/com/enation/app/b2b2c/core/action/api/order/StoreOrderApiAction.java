
package com.enation.app.b2b2c.core.action.api.order;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.RepairOrderDetail;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.pluin.order.StoreCartPluginBundle;
import com.enation.app.b2b2c.core.service.IStoreDlyTypeManager;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.StoreCartKeyEnum;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.impl.StorePromotionManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.base.core.model.Store;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.ICarInfoManager;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderPrintManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.OrderType;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 店铺订单API
 * 
 * @author LiFenlong
 * 
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/store")
@Action("storeOrder")
@InterceptorRef(value="apiRightStack",params={"apiRightInterceptor.excludeMethods",""})
public class StoreOrderApiAction extends WWAction {
	private IOrderManager orderManager;
	private IStoreOrderManager storeOrderManager;
	private IOrderFlowManager orderFlowManager;
	private IMemberAddressManager memberAddressManager;
	private IOrderPrintManager orderPrintManager;
	private IStoreCartManager storeCartManager;
	private IStoreMemberManager storeMemberManager;
	private IStoreDlyTypeManager storeDlyTypeManager;
	private ICartManager cartManager;
	private StoreCartPluginBundle storeCartPluginBundle;
	private IBonusManager bonusManager;
	private IBonusTypeManager bonusTypeManager;
	private IStoreManager storeManager;
	private IMemberManager memberManager;
	private ICarInfoManager carInfoManager;
	private StorePromotionManager storePromotionManager;
	private IReckoningManager reckoningManager;
	private Integer orderId;
	private Integer[] order_id;
	private Integer paymentId;
	private Double payMoney;
	private Double shipmoney;
	private String remark;
	private String ship_day;
	private String ship_name;
	private String ship_tel;
	private String ship_mobile;
	private String ship_zip;
	private String storeids;
	private String shippingids;
	private Integer regionid;
	private String addr;
	private String[] shipNos;
	private String bonusid;
	private Integer[] logi_id;
	private String[] logi_name;
	
	// 店铺id
	private int store_id;

	// 配送方式模板id
	private int type_id;

	// 收货地址id
	private int address_id;

	// 优惠券id
	private int bonus_id;
	
	//添加购物车参数
	private int productid;
	private int num;//要向购物车中活加的货品数量
	private IProductManager productManager ;
	
	/**
	 * 创建订单，需要购物车中有商品
	 * 
	 * @param address_id
	 *            :收货地址id.int型，必填项
	 * @param payment_id
	 *            :支付方式id，int型，必填项
	 * @param shipDay
	 *            ：配送时间，String型 ，可选项
	 * @param shipTime
	 *            ，String型 ，可选项
	 * @param remark
	 *            ，String型 ，可选项
	 * 
	 * @return 返回json串 result 为1表示添加成功0表示失败 ，int型 message 为提示信息
	 * 
	 */
	public String create() {
		try {
			String carplate = this.getRequest().getParameter("carplate");
			if(StringUtils.isEmpty(carplate)){
				this.showErrorJson("没有得到用户车辆信息");
				return JSON_MESSAGE;
			}
			List carinfoList = carInfoManager.getCarInfoByCarplate(carplate);
			if(carinfoList==null||carinfoList.size()==0){
				this.showErrorJson("没有得到用户车辆信息");
				return JSON_MESSAGE;
			}
		/*	int store_id = JSONObject.fromObject(carinfoList.get(0)).getInt("repair4sstoreid");
			if(store_id == 0){
				this.showErrorJson("该车辆没签约4s店");
				return JSON_MESSAGE;
			}*/
			Member member = UserConext.getCurrentMember();
			if (member == null) {
				this.showErrorJson("您没有登录或登录过期！");
				return JSON_MESSAGE;
			}
			String cartIds = this.getRequest().getParameter("cartIds");
			if(StringUtil.isNull(cartIds)){
				this.showErrorJson("您购物车里没东西哟！");
				return JSON_MESSAGE;
			}
			if(cartIds.lastIndexOf(',')==cartIds.length()-1){
				cartIds = cartIds.substring(0,cartIds.length()-1);
			}
			
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			this.storeCartManager.putCartListToSession(cartIds);//将购物车数据放入session中
			Order order = this.innerCreateOrder(cartIds);//得到父类订单
			order = orderManager.get(order.getSn());
			List<StoreOrder> storeOrderList = storeOrderManager.getChildOrder(order.getOrder_id());//获得子订单列表
			JSONArray mainOrderArray = new JSONArray();
			for(int i = 0 ; i < storeOrderList.size() ; i++){
				JSONObject orderJson = new JSONObject();
				StoreOrder storeOrder = storeOrderList.get(i);
				//得到itemjson对象
				String orderSn = storeOrder.getSn();//订单号后台显示为子订单的单号 所以提供子订单号
				orderJson.put("orderItem",UploadUtil.replacePath(storeOrder.getItems_json()));
				//获取服务机构信息
				Store serviceStore =  storeManager.getStore(Integer.valueOf(storeOrder.getService_store_id()));
				orderJson.put("orderSn",orderSn);
				orderJson.put("order_id",storeOrder.getOrder_id());
				orderJson.put("storeName",storeOrder.getStore_name());
				orderJson.put("serviceStoreAttr", serviceStore.getAttr());
				orderJson.put("serviceStoreName", serviceStore.getStore_name());
				orderJson.put("store_id", storeOrder.getStore_id());
				orderJson.put("serviceStoreTel",serviceStore.getCustomer_phone());
				orderJson.put("paymoney", Double.parseDouble(decimalFormat.format(storeOrder.getNeed_pay_money())));//子订单支付金额
				orderJson.put("orderStatus",storeOrder.getStatus());
				orderJson.put("goods_num",storeOrder.getGoods_num());
				List<Map> storeBonusList=storePromotionManager.getBonusInPay(member.getMember_id(),storeOrder.getStore_id(),Double.parseDouble(decimalFormat.format(storeOrder.getNeed_pay_money())),System.currentTimeMillis());
				Map storeBonus = (storeBonusList!=null&&storeBonusList.size()>0) ? storeBonusList.get(0): null; 
				orderJson.put("storeBonus", storeBonus);
				mainOrderArray.add(orderJson);
			}
			JSONObject orderStores = new JSONObject();
			orderStores.put("storeList", mainOrderArray);
			orderStores.put("orderSn", order.getSn());
			orderStores.put("order_id", order.getOrder_id());
			orderStores.put("orderStatus",order.getStatus());
			orderStores.put("paymoney", order.getNeed_pay_money());//总支付金额
			orderStores.put("goods_num",order.getGoods_num());
			Double totalgain = 0d; //获取当前车的安全出行奖励;
			if(!StringUtils.isEmpty(carplate)){
				List carInfoList=carInfoManager.getCarInfoByCarplate(carplate);
				if(carInfoList != null){
					net.sf.json.JSONObject carInfo = net.sf.json.JSONObject.fromObject(carInfoList.get(0));
					totalgain = carInfo.getDouble("totalgain");
				}
			}
			totalgain = totalgain < 0d ? 0d : totalgain;
			orderStores.put("totalgain", totalgain);//用户安全出行奖励抵扣
			this.json = "{\"result\":1,\"data\":"+orderStores.toString()+"}";
			/*// 获取红包，使用红包
			Map<Integer, MemberBonus> map = (Map) ThreadContextHolder.getSessionContext().getAttribute(BonusSession.B2B2C_SESSIONKEY);
			if (map != null) {
				for (MemberBonus mb : map.values()) {
					if (mb != null) {
						bonusManager.use(mb.getBonus_id(),
								order.getMember_id(), order.getOrder_id(),
								order.getSn(), mb.getBonus_type_id());
					}
				}
			}*/
		} catch (RuntimeException e) {
			 e.printStackTrace();
			this.logger.error("创建订单出错", e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	private Order innerCreateOrder(String cartIds) {
		try{
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			Member member = UserConext.getCurrentMember();
	
			Integer paymentId = StringUtil.toInt(request.getParameter("paymentId"),
					0);
			if (paymentId == 0){
				throw new RuntimeException("支付方式不能为空");
			}
			Order order = new Order();
			
			order.setPayment_id(paymentId);// 支付方式
	
			// 获取默认地址
	//		MemberAddress address = memberAddressManager.getMemberDefault(member.getMember_id());
			MemberAddress address = null;
			order.setShipping_id(0);
			order.setShipping_method(2);//默认配送到4S店
			if (address == null) {//如果没有默认地址取4s店为默认地址
					address = new MemberAddress();
					//获取用户签约店铺地址
					String carpalte = request.getParameter("carplate");
					List list = carInfoManager.getCarInfoByCarplate(carpalte);
					if(list!=null && list.size()>0){
						JSONObject object = JSONObject.fromObject(list.get(0));
						int repair4sstoreid = (int)object.get("repair4sstoreid");
						if(repair4sstoreid!=0){
							Store store  = storeManager.getStore(repair4sstoreid);
							address.setProvince_id(store.getStore_provinceid());
							address.setCity_id(store.getStore_cityid());
							address.setRegion_id(store.getStore_regionid());
							address.setAddr(store.getAttr());
							address.setZip(store.getZip());
							address.setProvince(store.getStore_province());
							address.setCity(store.getStore_city());
							address.setRegion(store.getStore_region());
							address.setName(member.getFullname());
							address.setMobile(member.getUsername());	
						}
						
					}
			}
	
			order.setShip_provinceid(address.getProvince_id());
			order.setShip_cityid(address.getCity_id());
			order.setShip_regionid(address.getRegion_id());
			order.setShip_addr(address.getAddr());
			order.setShip_zip(address.getZip());
			order.setRegionid(address.getRegion_id());
			order.setShipping_area(address.getProvince() + "-" + address.getCity()
			+ "-" + address.getRegion() + "-" + address.getAddr());
			order.setShip_name(address.getName()); //更改订单配送名称
			order.setShip_tel(member.getTel());		  //电话
			order.setShip_mobile(address.getMobile()); //手机号
			order.setMemberAddress(address);
			order.setShip_day(request.getParameter("shipDay"));
			order.setShip_time(request.getParameter("shipTime"));
			order.setRemark(request.getParameter("remark"));
			order.setAddress_id(address.getAddr_id());
			order.setCarplate(request.getParameter("carplate"));
	//		String sessionid = request.getSession().getId();
	//		order = this.storeOrderManager.createOrder(order, sessionid);
			//使用新逻辑创建父类订单
			order = this.storeOrderManager.createMainOrder(order, cartIds);
			return order;
		}catch(Exception e){
			throw e;
		}
		
	}
	

	/**
	 * 改变店铺的配送方式以及红包<br>
	 * 调用此api时必须已经访问过购物车列表<br>
	 * 
	 * @return 含有价格信息的json串
	 */
	public String changeArgsType() {

		// 由购物车列表中获取此店铺的相关信息
		Map storeData = StoreCartContainer.getStoreData(store_id);

		// 获取此店铺的购物列表
		List list = (List) storeData.get(StoreCartKeyEnum.goodslist.toString());

		// 配送地区
		String regionid_str = regionid == null ? "" : regionid + "";

		// 计算此配送方式时的店铺相关价格
		OrderPrice orderPrice = this.cartManager.countPrice(list, type_id,
				regionid_str);

		// 激发计算子订单价格事件
		orderPrice = storeCartPluginBundle.countChildPrice(orderPrice);
		if (bonus_id != 0) {

			MemberBonus bonuss = bonusManager.getBonus(bonus_id);
			changeBonus(orderPrice, bonuss, store_id);

		}
		// 重新压入此店铺的订单价格和配送方式id
		storeData.put(StoreCartKeyEnum.storeprice.toString(), orderPrice);
		storeData.put(StoreCartKeyEnum.shiptypeid.toString(), type_id);

		this.json = JsonMessageUtil.getObjectJson(orderPrice, "storeprice");

		return this.JSON_MESSAGE;
	}

	/**
	 * 修改优惠券选项
	 * 
	 * @author liushuai
	 * @version v1.0,2015年9月22日18:21:33
	 * @param bonuss
	 * @since v1.0
	 */
	private OrderPrice changeBonus(OrderPrice orderprice, MemberBonus bonus,
			int storeid) {

		// set 红包
		BonusSession.use(storeid, bonus);
		
		// 如果优惠券面额大于商品优惠价格的话 那么优惠价格为商品价格 
		if (orderprice.getNeedPayMoney() <= bonus.getBonus_money()) { 
			orderprice.setDiscountPrice(orderprice.getNeedPayMoney()); 
			orderprice.setNeedPayMoney(0.0);
		} else {
			// 计算需要支付的金额
			orderprice.setNeedPayMoney(CurrencyUtil.add(
					orderprice.getNeedPayMoney(), -bonus.getBonus_money()));
			
			orderprice.setDiscountPrice(bonus.getBonus_money());
		}
		return orderprice;

	}

	/**
	 * 改变收货地址<br>
	 * 调用此api时会更改session中的用户选中的地址
	 * 
	 * @return
	 */
	public String changeAddress() {
		try {
			// 根据id得到地址后压入session
			
			MemberAddress address = this.memberAddressManager
					.getAddress(address_id);
			if(address!=null){
				StoreCartContainer.putSelectedAddress(address);
			}

			// 重新计算价格
			this.storeCartManager.countPrice("yes");

			// 由session中获取店铺购物车数据，已经是计算过费用的了
			List<Map> storeCartList = StoreCartContainer
					.getStoreCartListFromSession();

			List newList = new ArrayList();
			for (Map map : storeCartList) {
				Map jsonMap = new HashMap();
				jsonMap.putAll(map);
				jsonMap.remove(StoreCartKeyEnum.goodslist.toString());
				newList.add(jsonMap);
			}

			this.json = JsonMessageUtil.getListJson(newList);
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 订单确认
	 * 
	 * @param orderId
	 *            订单Id,Integer
	 * @return 返回json串 result 为1表示调用成功0表示失败
	 */
	public String confirm() {
		try {
			String backstage = super.getRequest().getParameter("backstage");
			this.orderFlowManager.confirmOrder(orderId,backstage);
			Order order = this.orderManager.get(orderId);
			// this.orderFlowManager.addCodPaymentLog(order);
			this.showSuccessJson("'订单[" + order.getSn() + "]成功确认'");
		} catch (RuntimeException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			this.showErrorJson("订单确认失败" + e.getMessage());
			
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 订单支付
	 * 
	 * @param orderId
	 *            订单Id,Integer
	 * @param member
	 *            店铺会员,StoreMember
	 * @param paymentId
	 *            结算单Id,Integer
	 * @param payMoney
	 *            付款金额,Double
	 * @return 返回json串 result 为1表示调用成功0表示失败
	 */
	public String pay() {
		try {
			// 获取当前操作者
			StoreMember member = storeMemberManager.getStoreMember();
			Order order = this.orderManager.get(orderId);
			// 调用执行添加收款详细表
			if (orderFlowManager.pay(paymentId, orderId, payMoney,
					member.getUsername())) {
				showSuccessJson("订单[" + order.getSn() + "]收款成功");
			} else {
				showErrorJson("订单[" + order.getSn() + "]收款失败,您输入的付款金额合计大于应付金额");
			}
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
			showErrorJson("确认付款失败:" + e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 订单发货
	 * 
	 * @param order_id
	 *            订单Id,Integer[]
	 * @return 返回json串 result 为1表示调用成功0表示失败
	 */
	public String ship() {
		try {
			storeOrderManager.saveShipNo(order_id, logi_id[0], logi_name[0],
					shipNos[0]);
			String is_ship = orderPrintManager.ship(order_id);
			if (is_ship.equals("true")) {
				this.showSuccessJson("发货成功");
			} else {
				this.showErrorJson(is_ship);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson(e.getMessage());
			this.logger.error("发货出错", e);
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 修改配送费用
	 * 
	 * @param orderId
	 *            订单Id,Integer
	 * @param currshipamount
	 *            修改前价格,Double
	 * @param member
	 *            店铺会员,StoreMember
	 * @return 返回json串 result 为1表示调用成功0表示失败
	 */
	public String saveShipPrice() {
		try {
			// 修改前价格
			double currshipamount = orderManager.get(this.orderId)
					.getShipping_amount();
			double price = this.orderManager.saveShipmoney(shipmoney,
					this.orderId);
			// 获取操作人，记录日志
			StoreMember member = storeMemberManager.getStoreMember();
			// this.orderManager.log(this.orderId, "运费从"+currshipamount+"修改为" +
			// price, null, member.getUname());
			this.showSuccessJson("保存成功");
		} catch (RuntimeException e) {
			this.logger.error(e.getMessage(), e);
			this.showErrorJson("保存失败");
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 修改订单金额
	 * 
	 * @param orderId
	 *            订单Id,Integer
	 * @param amount
	 *            修改前价格,Double
	 * @param member
	 *            店铺会员,StoreMember
	 * @return 返回json串 result 为1表示调用成功0表示失败
	 */
	public String savePrice() {
		try {
			
			if(payMoney == null ||  payMoney <= 0){
				this.showErrorJson("订单价格不能为空或小于0");
				return this.JSON_MESSAGE;
			}
			// 修改前价格
//			double amount = orderManager.get(this.orderId).getOrder_amount();
			this.orderManager.savePrice(payMoney, this.orderId);
			// 获取操作人，记录日志
//			StoreMember member = storeMemberManager.getStoreMember();
			// orderManager.log(this.orderId, "运费从"+amount+"修改为" + payMoney,
			// null, member.getUname());
			this.showSuccessJson("修改订单价格成功");
		} catch (Exception e) {
			this.showErrorJson("修改订单价格失败");
			
			e.printStackTrace();
			this.logger.error(e);
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 修改收货人信息
	 * 
	 * @param orderId
	 *            订单Id,Integer
	 * @param member
	 *            店铺会员,StoreMember
	 * @param oldShip_day
	 *            修改前收货日期,String
	 * @param oldship_name
	 *            修改前收货人姓名,String
	 * @param oldship_tel
	 *            修改前收货人电话
	 * @param oldship_mobile
	 *            修改前收货人手机号
	 * 
	 * @param remark
	 *            订单备注,String
	 * @param ship_day
	 *            收货时间,String
	 * @param ship_name
	 *            收货人姓名,String
	 * @param ship_tel
	 *            收货人电话,String
	 * @param ship_mobile
	 *            收货人手机号,String
	 * @param ship_zip
	 *            邮政编号
	 * @return 返回json串 result 为1表示调用成功0表示失败
	 */
	public String saveConsigee() {
		try {
			Order order = this.orderManager.get(this.getOrderId());
			StoreMember member = storeMemberManager.getStoreMember();
			String oldShip_day = order.getShip_day();
			String oldship_name = order.getShip_name();
			String oldship_tel = order.getShip_tel();
			String oldship_mobile = order.getShip_mobile();
			String oldship_zip = order.getShip_zip();

			boolean addr = this.storeOrderManager.saveShipInfo(remark,
					ship_day, ship_name, ship_tel, ship_mobile, ship_zip,
					this.getOrderId());
			// 记录日志
			/*
			 * if(ship_day !=null && !StringUtil.isEmpty(ship_day)){
			 * this.orderManager.log(this.getOrderId(),
			 * "收货日期从['"+oldShip_day+"']修改为['" + ship_day+"']", null,
			 * member.getUname()); }if(ship_name !=null &&
			 * !StringUtil.isEmpty(ship_name)){
			 * this.orderManager.log(this.getOrderId(),
			 * "收货人姓名从['"+oldship_name+"']修改为['" + ship_name+"']",
			 * null,member.getUname()); }if(ship_tel !=null &&
			 * !StringUtil.isEmpty(ship_tel)){
			 * this.orderManager.log(this.getOrderId(),
			 * "收货人电话从['"+oldship_tel+"']修改为['" + ship_tel+"']",
			 * null,member.getUname()); }if(ship_mobile !=null &&
			 * !StringUtil.isEmpty(ship_mobile)){
			 * this.orderManager.log(this.getOrderId(),
			 * "收货人手机从['"+oldship_mobile+"']修改为['" + ship_mobile+"']",
			 * null,member.getUname()); }if(ship_zip !=null &&
			 * !StringUtil.isEmpty(ship_zip)){
			 * this.orderManager.log(this.getOrderId(),
			 * "收货人邮编从['"+oldship_zip+"']修改为['" + ship_zip+"']",
			 * null,member.getUname()); }
			 */
			this.saveAddr();
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("修改失败");
			logger.error(e);
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * 修改配送地区
	 * 
	 * @param province
	 *            省,String
	 * @param city
	 *            城市,String
	 * @param region
	 *            区,String
	 * @param Attr
	 *            详细地址,String
	 * 
	 * @param province_id
	 *            省Id,String
	 * @param city_id
	 *            城市Id,String
	 * @param region_id
	 *            区Id,String
	 * 
	 * @param oldAddr
	 *            修改前详细地址,String
	 * @param orderId
	 *            订单Id,Integer
	 * @return void
	 */
	private void saveAddr() {
		// 获取地区
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String province = request.getParameter("province");
		String city = request.getParameter("city");
		String region = request.getParameter("region");
		String Attr = province + "-" + city + "-" + region;
		// 获取地区Id
		String province_id = request.getParameter("province_id");
		String city_id = request.getParameter("city_id");
		String region_id = request.getParameter("region_id");
		// 记录日志，获取当前操作人
		String oldAddr = this.orderManager.get(this.orderId).getShip_addr();
		StoreMember member = storeMemberManager.getStoreMember();
		this.orderManager.saveAddr(this.orderId,
				StringUtil.toInt(province_id, true),
				StringUtil.toInt(city_id, true),
				StringUtil.toInt(region_id, true), Attr);
		this.orderManager.saveAddrDetail(this.getAddr(), this.getOrderId());

		// this.orderManager.log(this.orderId, "收货人详细地址从['"+oldAddr+"']修改为['" +
		// this.getAddr()+"']", null, member.getUname());
	}

	/**
	 * 会员地址
	 * 
	 * @param address_id
	 *            地址Id,String
	 * @return MemberAddress
	 */
	private MemberAddress memberAddress() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String address_id = request.getParameter("addressId");
		MemberAddress address = this.memberAddressManager.getAddress(Integer
				.valueOf(address_id));
		return address;
	}

	/**
	 * 获取订单价格信息
	 * 
	 * @param storeid
	 *            店铺Id,String[]
	 * @param typeid
	 *            类型,String[]
	 * @param storeGoodsList
	 *            购物车商品列表, List<Map>
	 * @param goodsprice
	 *            商品价格,Double
	 * @param shippingprice
	 *            配送费用,Double
	 * @param totleprice
	 *            打折费用,Double
	 * @return json
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getOrderPrice() {
		/*
		 * String [] storeid = storeids.split(","); String [] typeid =
		 * shippingids.split(","); String [] bonus = bonusid.split(",");
		 * 
		 * List<Map> storeGoodsList= new ArrayList<Map>(); HttpServletRequest
		 * request = ThreadContextHolder.getHttpRequest(); String sessionid =
		 * request.getSession().getId();
		 * storeGoodsList=storeCartManager.storeListGoods(sessionid);
		 * 
		 * String storeprices=""; String discountprice=""; Double
		 * totle_discountprice=0.0; Double totleprice=0.0d; Double
		 * goodsprice=0.0d; Double shippingprice=0.0d; Double
		 * b2b2c_plateform_price=0.0d;
		 * 
		 * for(Map map : storeGoodsList){ Integer store_id= (Integer)
		 * map.get("store_id"); List list = (List) map.get("goodslist"); for(int
		 * i=0;i<storeid.length;i++){
		 * if(store_id.equals(Integer.valueOf(storeid[i]))){ Map maps = new
		 * HashMap(); maps.put("storeid", store_id); maps.put("bonusid",
		 * Integer.valueOf(bonus[i])); OrderPrice orderPrice =
		 * this.storeCartManager.countPrice(list, regionid+"",
		 * Integer.valueOf(typeid[i]), false,maps);
		 * //System.out.println(orderPrice.getNeedPayMoney()); storeprices=
		 * storeprices+","+orderPrice.getOrderPrice(); discountprice =
		 * discountprice+","+orderPrice.getDiscountPrice(); totle_discountprice
		 * = CurrencyUtil.add(totle_discountprice,
		 * orderPrice.getDiscountPrice()); totleprice =
		 * CurrencyUtil.add(totleprice, orderPrice.getNeedPayMoney());
		 * shippingprice= CurrencyUtil.add(shippingprice,
		 * orderPrice.getShippingPrice()); goodsprice =
		 * CurrencyUtil.add(goodsprice,orderPrice.getGoodsPrice());
		 * b2b2c_plateform_price =
		 * orderPrice.getB2b2c_plateform_discountPrice(); break; } } }
		 * storeprices = storeprices.substring(1, storeprices.length());
		 * discountprice = discountprice.substring(1, discountprice.length());
		 * Map pricemap = new HashMap(); pricemap.put("result", 1);
		 * pricemap.put("storeprice", storeprices); pricemap.put("totleprice",
		 * CurrencyUtil.sub(totleprice, b2b2c_plateform_price));
		 * pricemap.put("goodsprice", goodsprice); pricemap.put("shippingprice",
		 * shippingprice); pricemap.put("discountprice", discountprice);
		 * pricemap.put("totle_discountprice",
		 * CurrencyUtil.add(totle_discountprice, b2b2c_plateform_price));
		 * 
		 * JSONArray jsons = JSONArray.fromObject(pricemap);
		 * this.json=jsons.toString().substring(1, jsons.toString().length()-1);
		 */

		return JSON_MESSAGE;
	}
	
	
	/**
	 *	立即下单接口，直接在里面添加购物车然后下单
	 * @return
	 */
	
	public String immediatelyGoodsCreate(){
		try {
			String carplate = this.getRequest().getParameter("carplate");
			if(StringUtils.isEmpty(carplate)){
				this.showErrorJson("没有得到用户车辆信息");
				return JSON_MESSAGE;
			}
			List carinfoList = carInfoManager.getCarInfoByCarplate(carplate);
			if(carinfoList==null||carinfoList.size()==0){
				this.showErrorJson("没有得到用户车辆信息");
				return JSON_MESSAGE;
			}
			/*int store_id = JSONObject.fromObject(carinfoList.get(0)).getInt("repair4sstoreid");
			if(store_id == 0){
				this.showErrorJson("该车辆没签约4s店");
				return JSON_MESSAGE;
			}*/
			Member member = UserConext.getCurrentMember();
			if (member == null) {
				this.showErrorJson("您没有登录或登录过期！");
				return JSON_MESSAGE;
			}
			Product product = productManager.get(productid);
			int cartid =  this.cartManager.addProductToCart(product,num,true);
			if(cartid == -1){
				this.showErrorJson("抱歉！您所选择的货品库存不足。");
				return JSON_MESSAGE;
			}
			if(cartid == 0){
				this.showErrorJson("抱歉！您所选择的货品订单未生成，已经加入购物车中。");
				return JSON_MESSAGE;
			}
//			this.storeCartManager.countPrice("yes");
			
			this.storeCartManager.putCartListToSession(StringUtil.toString(cartid));
			
			Order order = this.innerCreateOrder(StringUtil.toString(cartid));
			List<StoreOrder> storeOrderList = storeOrderManager.getChildOrder(order.getOrder_id());
			//获取商品详情
			Double paymoney = order.getNeed_pay_money();//总订单支付金额
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			String formatPaymoney = decimalFormat.format(paymoney);
			JSONObject orderJson = new JSONObject();
			//得到orderItem,从orderitem中获取商品名称和价格数量
			StoreOrder storeOrder = storeOrderList.get(0);
			//得到itemjson对象
			String orderSn = storeOrder.getSn();//订单号后台显示为子订单的单号 所以提供子订单号
			orderJson.put("orderSn",orderSn);
			orderJson.put("paymoney", Double.parseDouble(formatPaymoney));//总支付金额
			orderJson.put("orderStatus",storeOrder.getStatus());
			orderJson.put("orderItem",UploadUtil.replacePath(storeOrder.getItems_json()));
			orderJson.put("store_id",storeOrder.getStore_id());
			orderJson.put("storeName", storeOrder.getStore_name());
			//获取服务机构信息
			Store service_store = storeManager.getStore(Integer.valueOf(storeOrder.getService_store_id()));
			orderJson.put("serviceStoreAttr", service_store.getAttr());
			orderJson.put("serviceStoreName", service_store.getStore_name());
			orderJson.put("serviceStoreTel",service_store.getCustomer_phone());
			
			Double totalgain = 0d; //获取当前车的安全出行奖励;
			if(!StringUtils.isEmpty(carplate)){
				List carInfoList=carInfoManager.getCarInfoByCarplate(carplate);
				if(carInfoList != null && carInfoList.size()>0){
					net.sf.json.JSONObject carInfo = net.sf.json.JSONObject.fromObject(carInfoList.get(0));
					totalgain = carInfo.getDouble("totalgain");
				}
			}
			//获取收获地址
			orderJson.put("address", order.getMemberAddress());
			totalgain = totalgain < 0d ? 0d : totalgain;
			orderJson.put("totalgain", totalgain);//用户安全出行奖励抵扣
			List<Map> storeBonusList = storePromotionManager.getBonusInPay(member.getMember_id(),storeOrder.getStore_id(),Double.parseDouble(formatPaymoney),System.currentTimeMillis());
			Map storeBonus = (storeBonusList!=null&&storeBonusList.size()>0) ? storeBonusList.get(0): null; 
			orderJson.put("storeBonus", storeBonus);
			this.json =  "{\"result\":1,\"data\":"+orderJson.toString()+"}";
			// 获取红包，使用红包
			/*Map<Integer, MemberBonus> map = (Map) ThreadContextHolder.getSessionContext().getAttribute(BonusSession.B2B2C_SESSIONKEY);
			if (map != null) {
				for (MemberBonus mb : map.values()) {
					if (mb != null) {
						bonusManager.use(mb.getBonus_id(),
								order.getMember_id(), order.getOrder_id(),
								order.getSn(), mb.getBonus_type_id());
					}
				}
			}*/
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("创建订单出错", e);
			this.showErrorJson(e.getMessage());
		}
		return JSON_MESSAGE;
	}
	/**
	 * 确认备货
	 * @return
	 */
	public String stockUpComplete(){
		try{
			String orderId = this.getRequest().getParameter("orderId");
			storeOrderManager.stockUpComplete(orderId);
			Order order = this.orderManager.get(Integer.parseInt(orderId));
			this.showSuccessJson("该订单["+order.getSn()+"]备货成功");
		}catch(Exception e){
			e.printStackTrace();
			this.logger.error("备货完成出错",e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	} 
	
	
	/**
	 * 确认服务
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public String confirmService(){
		try{
			String orderId = this.getRequest().getParameter("orderId");
			storeOrderManager.confirmService(orderId);
			Order order = this.orderManager.get(Integer.parseInt(orderId));
			if(order.getOrder_type() == OrderType.ZA_INSURANCE || order.getOrder_type() == OrderType.STORE_INSURANCE){
				this.showSuccessJson("该订单["+order.getSn()+"]保单已寄出");
			}else{
				this.showSuccessJson("该订单["+order.getSn()+"]服务完成");
			}
		}catch(Exception e){
			e.printStackTrace();
			this.logger.error("服务完成出错",e);
			this.showErrorJson("服务完成出错");
		}
		return this.JSON_MESSAGE;
	} 
	
	/**
	 * 订单完成
	 * @return
	 */
	public String finishOrder(){
		try{
			String order_id = this.getRequest().getParameter("order_id");
			storeOrderManager.finishOrder(order_id);
			Order order = this.orderManager.get(Integer.parseInt(order_id));
			
			if(order.getOrder_type() == OrderType.REPAIR){//保养订单完成，更新下次保养时间
				JSONObject obj = JSONObject.fromObject(carInfoManager.getCarInfoByCarplate(order.getCarplate()).get(0));
				long repairlasttime = obj.getLong("repairlasttime");
				long repairintervaltime = obj.getLong("repairintervaltime");
				long repairnexttime = repairlasttime + repairintervaltime;
				repairnexttime = repairnexttime == 0 ? new Date().getTime() : repairnexttime;
				
				//更新下次保养时间
				JSONObject carinfo = new JSONObject();
				carinfo.put("carplate", obj.get("carplate"));
				carinfo.put("carowner", obj.get("carowner"));
				carinfo.put("repairnexttime", repairnexttime);
				carInfoManager.inputCarInfo(carinfo.toString());
			}
			
			this.showSuccessJson("该订单["+order.getSn()+"]完成");
			
		}catch(Exception e){
			e.printStackTrace();
			this.logger.error("订单完成出错",e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	} 
	
	/**
	 * 保存保险订单详细信息
	 * @return
	 */
	public String saveInsureInfo(){
		try{
			HttpServletRequest request=ThreadContextHolder.getHttpRequest();
			//session中获取会员信息,判断用户是否登陆
			StoreMember member=storeMemberManager.getStoreMember();
			if(member==null){
				HttpServletResponse response= ThreadContextHolder.getHttpResponse();
				try {
					response.sendRedirect("login.html");
				} catch (IOException e) {
					throw new UrlNotFoundException();
				}
			}
			
			String order_id = getRequest().getParameter("order_id");
			String spec_id = getRequest().getParameter("spec_id");
			String policy_no = getRequest().getParameter("policy_no");
			String repair_coin = getRequest().getParameter("repair_coin");
			String insure_need_pay_money = getRequest().getParameter("insure_need_pay_money");
			double need_pay_money = (insure_need_pay_money == null || "".equals(insure_need_pay_money)) ? 0 : Double.valueOf(insure_need_pay_money.replace(",", ""));
			Order order = this.orderManager.get(Integer.parseInt(order_id));
			String message = "";

			if(!StringUtil.isEmpty(insure_need_pay_money)){//更新保险订单价格和赠送保养币，订单状态跳转到待支付
				if(need_pay_money <= 0){
					this.showErrorJson("订单价格不能小于0");
					return this.JSON_MESSAGE;
				}else{//修改保险订单价格
					this.orderManager.savePrice(need_pay_money, Integer.valueOf(order_id));
				}
				message = "更新保险订单["+order.getSn()+"]保养币和价格成功";
			}

			storeOrderManager.saveInsureOrderDetailInfo(order, spec_id, repair_coin, policy_no);

			if(!StringUtil.isEmpty(policy_no)){//更新保单号，订单状态流转到保单生成,如果是到店领取的订单，直接变为保单已寄出
				storeOrderManager.stockUpComplete(order_id);
				message = "更新保险订单["+order.getSn()+"]保单号成功";
			}
		
			/*Order new_order = orderManager.updateRecordStatus(order, 1);
			if ((new_order.getStatus() == 6 || new_order.getStatus() == 7)
					&& new_order.getRecord_update_status() != null && new_order.getRecord_update_status() == 1) {// 服务完成，且已经更新记录，进入结算流程
				Reckoning reckoning = reckoningManager.getReckoningByOrder(order.getSn());
				double balance = storeManager.getBalance(new_order.getService_store_id());
				reckoningManager.orderSettlement(reckoning, balance);
			}*/
	
			this.showSuccessJson(message);
		}catch(Exception e){
			e.printStackTrace();
			this.logger.error("备货完成出错",e);
			this.showErrorJson(e.getMessage());
		}
		
		return this.JSON_MESSAGE;
	} 
	
	/**
	 * 保存保养订单详细信息
	 * @return
	 */
	public String saveRepairInfo(){
		try{
			HttpServletRequest request=ThreadContextHolder.getHttpRequest();
			//session中获取会员信息,判断用户是否登陆
			StoreMember member=storeMemberManager.getStoreMember();
			if(member==null){
				HttpServletResponse response= ThreadContextHolder.getHttpResponse();
				try {
					response.sendRedirect("login.html");
				} catch (IOException e) {
					throw new UrlNotFoundException();
				}
			}
			
			String spec_id = getRequest().getParameter("repair_spec_id");
			String order_id = getRequest().getParameter("order_id");
			String[] items = getRequest().getParameterValues("items");
			String repair_mile = getRequest().getParameter("repair_mile");
			String repair_price = getRequest().getParameter("repair_price").replace(",", "");
			String repair_source = getRequest().getParameter("repair_source");
			String service_timelength = getRequest().getParameter("service_timelength");
			String engineer = getRequest().getParameter("engineer");
			String repair_time = getRequest().getParameter("repair_time");
			String repair_remarks = getRequest().getParameter("repair_remarks");
		
			Order order = this.orderManager.get(Integer.parseInt(order_id));
			
			RepairOrderDetail repairDetail = new RepairOrderDetail();
			repairDetail.setSpec_id(Integer.valueOf(spec_id));
			repairDetail.setOrder_id(order.getOrder_id());
			repairDetail.setRepair_item_ids(StringUtil.arrayToString(items, ","));
			repairDetail.setRepair_mile("".equals(repair_mile) ? 0 : Long.valueOf(repair_mile));
			repairDetail.setRepair_price("".equals(repair_price) ? 0.0 : Double.valueOf(repair_price));
			repairDetail.setRepair_source(repair_source);
			repairDetail.setService_timelength("".equals(service_timelength) ? 0 : Double.valueOf(service_timelength));
			repairDetail.setEngineer(engineer);
			repairDetail.setRepair_time("".equals(repair_time) ? 0 : DateUtil.getDateline(repair_time, "yyyy-MM-dd HH:mm:ss")*1000);
			repairDetail.setRepair_remarks(repair_remarks);
			repairDetail.setUpdate_time(new Date().getTime());

			if(order.getCreate_time() > repairDetail.getRepair_time()){
				this.showErrorJson("服务时间不能早于订单生成时间，请重新输入");
				return this.JSON_MESSAGE;
			}
			repairDetail = storeOrderManager.saveRepairOrderDetailInfo(repairDetail);
			carInfoManager.updateCarRepairInfo(order.getCarplate(), repairDetail.getRepair_mile().toString(), repair_time, repairDetail.getRepair_price().toString(), repairDetail.getRepair_source());
			
			/**
			Order new_order = orderManager.updateRecordStatus(order, 1);
			if ((new_order.getStatus() == 6 || new_order.getStatus() == 7)
					&& new_order.getRecord_update_status() != null && new_order.getRecord_update_status() == 1) {// 服务完成，且已经更新记录，进入结算流程
				Reckoning reckoning = reckoningManager.getReckoningByOrder(order.getSn());
				double balance = storeManager.getBalance(new_order.getService_store_id());
				reckoningManager.orderSettlement(reckoning, balance);
			}
			**/

			this.showSuccessJson("保存保养订单["+order.getSn()+"]详细信息成功");
		}catch(Exception e){
			e.printStackTrace();
			this.logger.error("保存记录出错",e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	} 
	
	
	/** @description  物流信息
	 * @date 2016年9月8日 上午10:57:03
	 * @return
	 * @return String
	 */
	public String saveLogistics(){
		try{
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String order_id = request.getParameter("order_id");
			String logi_id = request.getParameter("logi_id");
			String ship_no = request.getParameter("ship_no");
			orderManager.saveLogistics(order_id,logi_id,ship_no);
			showSuccessJson("编辑成功");
		}catch(Exception e){
			e.printStackTrace();
			showErrorJson("编辑失败");
		}
		return JSON_MESSAGE;
	}
	
	/** @description 选择收获地址
	 * @date 2016年9月8日 下午4:07:11
	 * @return
	 * @return String
	 */
	public String updateAddress(){
		try{
			storeOrderManager.updateAddress();
			showSuccessJson("选择成功");
		}catch(Exception e){
			e.printStackTrace();
			showErrorJson("修改失败");
		}
		return JSON_MESSAGE;
	}
	
	
	// set get
	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}

	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}

	public void setMemberAddressManager(
			IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}

	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}

	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IOrderPrintManager getOrderPrintManager() {
		return orderPrintManager;
	}

	public void setOrderPrintManager(IOrderPrintManager orderPrintManager) {
		this.orderPrintManager = orderPrintManager;
	}

	public IStoreCartManager getStoreCartManager() {
		return storeCartManager;
	}

	public void setStoreCartManager(IStoreCartManager storeCartManager) {
		this.storeCartManager = storeCartManager;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer[] getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer[] order_id) {
		this.order_id = order_id;
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public Double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}

	public Double getShipmoney() {
		return shipmoney;
	}

	public void setShipmoney(Double shipmoney) {
		this.shipmoney = shipmoney;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getShip_day() {
		return ship_day;
	}

	public void setShip_day(String ship_day) {
		this.ship_day = ship_day;
	}

	public String getShip_name() {
		return ship_name;
	}

	public void setShip_name(String ship_name) {
		this.ship_name = ship_name;
	}

	public String getShip_tel() {
		return ship_tel;
	}

	public void setShip_tel(String ship_tel) {
		this.ship_tel = ship_tel;
	}

	public String getShip_mobile() {
		return ship_mobile;
	}

	public void setShip_mobile(String ship_mobile) {
		this.ship_mobile = ship_mobile;
	}

	public String getShip_zip() {
		return ship_zip;
	}

	public void setShip_zip(String ship_zip) {
		this.ship_zip = ship_zip;
	}

	public String getStoreids() {
		return storeids;
	}

	public void setStoreids(String storeids) {
		this.storeids = storeids;
	}

	public String getShippingids() {
		return shippingids;
	}

	public void setShippingids(String shippingids) {
		this.shippingids = shippingids;
	}

	public Integer getRegionid() {
		return regionid;
	}

	public void setRegionid(Integer regionid) {
		this.regionid = regionid;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String[] getShipNos() {
		return shipNos;
	}

	public void setShipNos(String[] shipNos) {
		this.shipNos = shipNos;
	}

	public String getBonusid() {
		return bonusid;
	}

	public void setBonusid(String bonusid) {
		this.bonusid = bonusid;
	}

	public Integer[] getLogi_id() {
		return logi_id;
	}

	public void setLogi_id(Integer[] logi_id) {
		this.logi_id = logi_id;
	}

	public String[] getLogi_name() {
		return logi_name;
	}

	public void setLogi_name(String[] logi_name) {
		this.logi_name = logi_name;
	}

	public int getStore_id() {
		return store_id;
	}

	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}

	public IStoreDlyTypeManager getStoreDlyTypeManager() {
		return storeDlyTypeManager;
	}

	public void setStoreDlyTypeManager(IStoreDlyTypeManager storeDlyTypeManager) {
		this.storeDlyTypeManager = storeDlyTypeManager;
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}

	public StoreCartPluginBundle getStoreCartPluginBundle() {
		return storeCartPluginBundle;
	}

	public void setStoreCartPluginBundle(
			StoreCartPluginBundle storeCartPluginBundle) {
		this.storeCartPluginBundle = storeCartPluginBundle;
	}

	public int getType_id() {
		return type_id;
	}

	public void setType_id(int type_id) {
		this.type_id = type_id;
	}

	public int getAddress_id() {
		return address_id;
	}

	public void setAddress_id(int address_id) {
		this.address_id = address_id;
	}

	public int getBonus_id() {
		return bonus_id;
	}

	public void setBonus_id(int bonus_id) {
		this.bonus_id = bonus_id;
	}

	public IBonusManager getBonusManager() {
		return bonusManager;
	}

	public void setBonusManager(IBonusManager bonusManager) {
		this.bonusManager = bonusManager;
	}

	public IBonusTypeManager getBonusTypeManager() {
		return bonusTypeManager;
	}

	public void setBonusTypeManager(IBonusTypeManager bonusTypeManager) {
		this.bonusTypeManager = bonusTypeManager;
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

	public ICarInfoManager getCarInfoManager() {
		return carInfoManager;
	}

	public void setCarInfoManager(ICarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}

	public int getProductid() {
		return productid;
	}

	public void setProductid(int productid) {
		this.productid = productid;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	public StorePromotionManager getStorePromotionManager() {
		return storePromotionManager;
	}

	public void setStorePromotionManager(StorePromotionManager storePromotionManager) {
		this.storePromotionManager = storePromotionManager;
	}

	public IReckoningManager getReckoningManager() {
		return reckoningManager;
	}

	public void setReckoningManager(IReckoningManager reckoningManager) {
		this.reckoningManager = reckoningManager;
	}

	

}
