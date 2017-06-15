/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：订单Api
 * 修改人：  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.order;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.IStoreDlyTypeManager;
import com.enation.app.b2b2c.core.service.IStoreMemberAddressManager;
import com.enation.app.b2b2c.core.service.IStoreTemplateManager;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.component.receipt.service.IReceiptManager;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.InsureRepairSpec;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.service.I4sStoresManager;
import com.enation.app.shop.core.service.ICarInfoManager;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.ICommentManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IInsuranceManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IMemberOrderManager;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.InsureType;
import com.enation.app.shop.core.service.OrderType;
import com.enation.app.shop.core.utils.RegularExpressionUtils;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.TestUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 订单Api
 * 提供
 * @author Sylow
 * @version v1.0
 * @since v1.0
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("order")
public class MobileOrderApiAction extends WWAction {

    private IOrderManager orderManager;
    private IMemberAddressManager memberAddressManager;
    private IPaymentManager paymentManager;
    private ICartManager cartManager;
    private IDlyTypeManager dlyTypeManager;
    private IMemberOrderManager memberOrderManager;
    private IReceiptManager receiptManager;
    private IOrderFlowManager orderFlowManager;
    private IMemberManager memberManager;
    private IStoreCartManager storeCartManager;
    private IStoreMemberAddressManager storeMemberAddressManager;
    private IStoreTemplateManager storeTemplateManager;
    private IStoreDlyTypeManager storeDlyTypeManager;
    private IStoreOrderManager storeOrderManager;
    private IProductManager productManager;
    private CartPluginBundle cartPluginBundle;
    private ICarInfoManager carInfoManager;
    private IInsuranceManager insuranceManager;
    private I4sStoresManager c4sStoresManager;
    private ICommentManager commentManager;
    //private final int PAGE_SIZE = 20;

    /**
     * 获取支付方式
     * @return
     */
    public String payment(){
        //支付方式
        

        //配送方式
        /*HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
        String sessionid =  request.getSession().getId();
        Double orderPrice = cartManager.countGoodsTotal(sessionid);
        Double weight = cartManager.countGoodsWeight(sessionid);
        int regionId = NumberUtils.toInt(request.getParameter("regionid"), 0);
        List<DlyType> dlyTypeList = this.dlyTypeManager.list(weight, orderPrice,"" + regionId);*/
    	try {
    		
    		List paymentList  = this.paymentManager.list();
            Map data = new HashMap();
            data.put("payment", paymentList);
            //data.put("shipping", dlyTypeList);
            this.json = JsonMessageUtil.getObjectJson(data);
            
    	} catch (RuntimeException e) {
    		this.logger.error("获取支付方式出错", e);
			this.showErrorJson("获取支付方式出错[" + e.getMessage() + "]");
    	}
    	return WWAction.JSON_MESSAGE;
    }
    
    /**
     * 获取购物车所有商品、商品所对应的商家、商家的配送方式等信息
     * @return
     */
    public String storeCartGoods(){
    	try {
    		HttpServletRequest request =ThreadContextHolder.getHttpRequest();
    		String sessionid  = request.getSession().getId();
    		
    		Member member = UserConext.getCurrentMember();
    		if (member == null) {
    			this.showErrorJson("未登录");
    			return WWAction.JSON_MESSAGE;
    		}
    		
    		this.storeCartManager.countPrice("yes");
			List<Map> list = StoreCartContainer.getStoreCartListFromSession();
			
			List<CartItem> cartList  = cartManager.listGoods(sessionid);
			//计算订单价格
			OrderPrice orderPrice  =this.cartManager.countPrice(cartList, null,null);
			
			//激发价格计算事件
			orderPrice  = this.cartPluginBundle.coutPrice(orderPrice);
			
			Map map = new HashMap();
			map.put("store_list", list);
			map.put("order_price", orderPrice);
			
			String listStr = JsonUtil.MapToJson(map);			
			this.json =  "{\"result\":1,\"data\":" + listStr + "}";

    	} catch(RuntimeException e) {
    		TestUtil.print(e);
    		this.logger.error("获取商品信息出错", e);
			this.showErrorJson("获取商品信息出错[" + e.getMessage() + "]");
    	}
    	return WWAction.JSON_MESSAGE;
    }
    
    
    /**
     * 获得订单总价
     * @param address_id 必填 地址id
     * @param shipping_id 必填 配送方式id
     * @return
     */
    public String getTotalPrice(){
    	
    	try {
    		
    		HttpServletRequest request =ThreadContextHolder.getHttpRequest();
    		String sessionid  = request.getSession().getId();
    		
    		List<CartItem> cartList  = cartManager.listGoods(sessionid);
    		//计算订单价格
    		OrderPrice orderprice  =this.cartManager.countPrice(cartList, null,null);
    		
    		//激发价格计算事件
    		orderprice  = cartPluginBundle.coutPrice(orderprice);
    		
    		this.json = JsonMessageUtil.getObjectJson(orderprice);
    	} catch(RuntimeException e) {
    		TestUtil.print(e);
    		this.logger.error("获取订单总价出错", e);
			this.showErrorJson("获取订单总价出错[" + e.getMessage() + "]");
    		
    	}
    	
    	return WWAction.JSON_MESSAGE;
    }
    
    /**
     * 提交订单
     * @return
     */
    public String create() {
        //Member member = UserConext.getCurrentMember();
    	try{
    		Member member = UserConext.getCurrentMember();
	        if (member == null) {
	            this.showErrorJson("您没有登录或登录过期！");
	            return JSON_MESSAGE;
	        }
	        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	
	        Integer shippingId = StringUtil.toInt(request.getParameter("typeId"), null);
	        if (shippingId == null){
	            this.showErrorJson("配送方式不能为空");
	            return JSON_MESSAGE;
	        }
	
	        Integer paymentId = StringUtil.toInt(request.getParameter("paymentId"), 0);
	        if (paymentId == 0) {
	            this.showErrorJson("支付方式不能为空");
	            return JSON_MESSAGE;
	        }
	
	        Order order = new Order();
	        order.setShipping_id(shippingId); //配送方式
	        order.setPayment_id(paymentId);//支付方式
	
	        //收货地址
	        int addressId = NumberUtils.toInt(request.getParameter("addressId"), 0);
	        MemberAddress address = memberAddressManager.getAddress(addressId);
	        if(address == null){
	            this.showErrorJson("请选择收货地址！");
	            return JSON_MESSAGE;
	        }
	
	        order.setShip_provinceid(address.getProvince_id());
	        order.setShip_cityid(address.getCity_id());
	        order.setShip_regionid(address.getRegion_id());
	
	        order.setShip_addr(address.getAddr());
	        order.setShip_mobile(address.getMobile());
	        order.setShip_tel(address.getTel());
	        order.setShip_zip(address.getZip());
	        order.setShipping_area(address.getProvince() + "-" + address.getCity() + "-" + address.getRegion());
	        order.setShip_name(address.getName());
	        order.setRegionid(address.getRegion_id());
	
	        order.setMemberAddress(address);
	        order.setShip_day(request.getParameter("shipDay"));
	        order.setShip_time(request.getParameter("shipTime"));
	        order.setRemark(request.getParameter("remark"));
	        order.setAddress_id(address.getAddr_id());//保存本订单的会员id
	       // order = this.orderManager.add(order, request.getSession().getId());
	
	        this.json = JsonMessageUtil.getObjectJson(order, "order");
    	} catch(RuntimeException e) {
    		TestUtil.print(e);
			this.logger.error("提交订单出错", e);
			this.showErrorJson("提交订单出错[" + e.getMessage() + "]");
    	}
        return JSON_MESSAGE;
    }

    /**
     * 订单列表
     * @return
     */
    public String list(){
		try {
			Member member = UserConext.getCurrentMember();
			if (member == null) {
				this.showErrorJson("您没有登录或登录过期！");
				return JSON_MESSAGE;
			}
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String carplate = request.getParameter("carplate");
			if(StringUtil.isNull(carplate)){
				this.showErrorJson("没有获取到车辆信息！");
				return JSON_MESSAGE;
			}
			JSONObject data = new JSONObject();
			String page = request.getParameter("page");
			page = (page == null || page.equals("")) ? "1" : page;
			int pageSize = 10;
			String status = request.getParameter("status");
			String keyword = request.getParameter("keyword");
			Page ordersPage = storeOrderManager.pageChildOrders(Integer.valueOf(page), pageSize, status, keyword);
			Long totalCount = ordersPage.getTotalCount();
			
			data.put("totalCount", totalCount);
			data.put("pageSize", pageSize);
			data.put("page", page);
			String orderList = UploadUtil.replacePath(JSONObject.fromObject(ordersPage).toString());
			//命名规范
			data.put("ordersList", orderList);
			data.put("message", "获取数据成功");
			this.json = "{result : 1,data : " + data + "}";
		} catch (RuntimeException e) {
			TestUtil.print(e);
			this.logger.error("获取订单列表出错", e);
			this.showErrorJson(e.getMessage());
		}

		return WWAction.JSON_MESSAGE;
    }
    
    /**
     * @description app订单列表
     * @date 2016年9月9日 下午7:19:21
     * @return
     */
    public String orderList(){
		try {
			Member member = UserConext.getCurrentMember();
			if (member == null) {
				this.showErrorJson("您没有登录或登录过期！");
				return JSON_MESSAGE;
			}
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String carplate = request.getParameter("carplate");
			if(StringUtil.isNull(carplate)){
				this.showErrorJson("没有获取到车辆信息！");
				return JSON_MESSAGE;
			}
			JSONObject data = new JSONObject();
			String page = request.getParameter("page");
			page = (page == null || page.equals("")) ? "1" : page;
			int pageSize = 10;
			String status = request.getParameter("status");
			String keyword = request.getParameter("keyword");

			Page ordersPage = storeOrderManager.pageChildOrderList(Integer.valueOf(page), pageSize, status, keyword);
			Long totalCount = ordersPage.getTotalCount();
			
			data.put("totalCount", totalCount);
			data.put("pageSize", pageSize);
			data.put("page", page);
			String orderList = UploadUtil.replacePath(JSONObject.fromObject(ordersPage).toString());
			//命名规范
			data.put("ordersList", orderList);
			data.put("message", "获取数据成功");
			this.json = "{result : 1,data : " + data + "}";
		} catch (Exception e) {
			this.logger.error("获取订单列表出错", e);
			this.showErrorJson(e.getMessage());
			e.printStackTrace();
		}

		return WWAction.JSON_MESSAGE;
    }

    /**
     * 订单详细
     * @return
     */
    public String detail(){
        Member member =UserConext.getCurrentMember();

        if (member == null) {
            this.showErrorJson("您没有登录或登录过期！");
            return JSON_MESSAGE;
        }

        HttpServletRequest request = getRequest();
        int orderId  = NumberUtils.toInt(request.getParameter("orderid"), 0);
        Order order = orderManager.get(orderId);
        if(order == null){
            this.showErrorJson("此订单不存在！");
            return JSON_MESSAGE;
        }
        if(!order.getMember_id().equals(member.getMember_id())){
            this.showErrorJson("您没有权限进行此项操作！");
            return JSON_MESSAGE;
        }

        Map dataMap = new HashMap();
        dataMap.put("order", order);
        dataMap.put("receipt", receiptManager.getByOrderid(order.getOrder_id()));

        this.json = JsonMessageUtil.getObjectJson(dataMap);

        return JSON_MESSAGE;
    }

    /**
     * 取消订单
     * @param sn:订单序列号.String型，必填项
     *
     * @return 返回json串
     * result  为1表示添加成功0表示失败 ，int型
     * message 为提示信息
     */

    public String cancel() {
        Member member =UserConext.getCurrentMember();

        if (member == null) {
            this.showErrorJson("您没有登录或登录过期！");
            return JSON_MESSAGE;
        }

        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        try {
            String sn = request.getParameter("sn");
            String reason = request.getParameter("reason");
            this.orderFlowManager.cancel(sn, reason);
            this.showSuccessJson("取消订单成功");
        } catch (RuntimeException re) {
            this.showErrorJson(re.getMessage());
        }
        return JSON_MESSAGE;
    }

    /**
     * 确认收货
     * @param orderId:订单id.String型，必填项
     *
     * @return 返回json串
     * result  为1表示添加成功0表示失败 ，int型
     * message 为提示信息
     */
    public String rogConfirm() {
        Member member =UserConext.getCurrentMember();

        if (member == null) {
            this.showErrorJson("您没有登录或登录过期！");
            return JSON_MESSAGE;
        }

        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        try {
            String orderId = request.getParameter("orderid");
            this.orderFlowManager.rogConfirm(Integer.parseInt(orderId), member.getMember_id(), member.getUsername(), member.getUsername(), DateUtil.getDateline());
            this.showSuccessJson("确认收货成功");
        } catch (Exception e) {
            this.showErrorJson("数据库错误");
        }
        return JSON_MESSAGE;
    }
    
    /**
     * @description 是否可以创建保险订单
     * @date 2016年9月12日 上午10:27:52
     * @return
     */
    public String ifCanCreateInsureOrder() {
		try {
			String carplate = getRequest().getParameter("carplate");
			JSONObject obj = orderManager.checkCreateOrderStatus(carplate);
			boolean flag = obj.getBoolean("flag");
			if(!flag){
				this.json = "{\"result\":0,\"data\": \"\",\"message\":" + obj.getString("message") + "}";
			}else{
				this.json = "{\"result\":1,\"data\":\"\",\"message\":\"可以下单\"}";
			}
		} catch (Exception e) {
			this.logger.error("保险订单准备页", e);
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 保险、保养下单
	 * @date 2016年9月3日 下午3:51:25
	 * @return
	 */
	public String createOrder(){
		try {
			JSONObject obj = new JSONObject();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String order_type = getRequest().getParameter("order_type");
			String carplate = getRequest().getParameter("carplate");//车牌号
			String store_id = getRequest().getParameter("store_id");//预约店铺id
			
			String errMsg = "";
			
			if(StringUtil.isNull(order_type)){
				errMsg = "订单类型为空，请检查";
			}else{
				boolean match = RegularExpressionUtils.matches(order_type, RegularExpressionUtils.intPattern);
				if(!match){
					errMsg = "订单类型应为正整数";
				}
			}
			if(StringUtil.isNull(store_id)){
				errMsg = "下单店铺id为空，请检查";
			}else{
				boolean match = RegularExpressionUtils.matches(store_id, RegularExpressionUtils.intPattern);
				if(!match){
					errMsg = "店铺id应为正整数";
				}
			}
			if(StringUtil.isNull(carplate)){
				errMsg = "车牌号为空，请检查";
			}else if(carInfoManager.getCarInfoByCarplate(carplate).size() == 0){
				errMsg = "该车牌号不存在，请检查";
			}
			
			if(!StringUtil.isNull(errMsg)){
				obj.put("result", 0);
				obj.put("message", errMsg);
			}else{
				Product product = productManager.getProductByCarplateAndType(carplate, order_type);//获取车辆对应的product对象，用于添加购物车，然后生成订单
				product.setEnable_store(1);
				productManager.update(product);
				InsureRepairSpec order_spec = null;
				
				//保险预约下单
				if(order_type.equals(OrderType.ZA_INSURANCE+"") || order_type.equals(OrderType.STORE_INSURANCE+"")){
					String insure_company_id = getRequest().getParameter("insure_company_id");//保险公司id
					String applicant = getRequest().getParameter("applicant");//投保人
					String applicant_id = getRequest().getParameter("applicant_id");//投保人身份证
					String insure_starttime = getRequest().getParameter("insure_starttime");//保险生效日期-起始日期
					String insure_endtime = getRequest().getParameter("insure_endtime");//保险生效日期-结束日期
					String params = getRequest().getParameter("params");
					
					String glass_type = "";//玻璃破碎险-玻璃类型id
					String thirdparty_coverage_id = "";//第三者责任险-保额id
					String driver_coverage_id = "";//车上人员-司机责任险-保额id
					String passenger_coverage_id = "";//车上人员-乘客责任险-保额id
					String scratch_coverage_id = "";//车身划痕险-保额id
					String exempt_insure_typeids = "";//不计免赔险-选择的险种id集合
					String select_insure_typeids = "";//选择的险种类型id集合
				
					if(!StringUtil.isEmpty(params)){
						JSONArray insure_paramsArray = new JSONArray();
						
						JSONArray paramsArray = JSONArray.fromObject(params);
						for(int i=0; i<paramsArray.size(); i++){
							JSONObject insureObj = paramsArray.getJSONObject(i);
							int param_type = insureObj.getInt("param_type");
							String param_value = insureObj.getString("param_value");
							
							select_insure_typeids += param_type + ",";
							
							if(param_type == InsureType.glass_breakage_insure){
								glass_type = param_value;
							}
							if(param_type == InsureType.thirdparty_insure){
								thirdparty_coverage_id = param_value;
							}
							if(param_type == InsureType.driver_insure){
								driver_coverage_id = param_value;
							}
							if(param_type == InsureType.passenger_insure){
								passenger_coverage_id = param_value;
							}
							if(param_type == InsureType.scratch_insure){
								scratch_coverage_id = param_value;
							}
							if(param_type == InsureType.exempt_insure){
								exempt_insure_typeids = param_value;
							}

							if(!StringUtil.isEmpty(param_value) && param_type != InsureType.compulsory_insure &&  param_type != InsureType.travel_tax){//获取有参数值的json对象
								insure_paramsArray.add(insureObj);
							}
						}
						params = insure_paramsArray.toString();
						select_insure_typeids = select_insure_typeids.length() > 0 ? select_insure_typeids.substring(0, select_insure_typeids.length() - 1) : select_insure_typeids;
					}
					
					if(StringUtil.isEmpty(insure_company_id)){
						errMsg = "请输入保险公司id";
					}else if(!RegularExpressionUtils.matches(insure_company_id, RegularExpressionUtils.intPattern)){
						errMsg = "保险公司id应为正整数";
					}else if(StringUtil.isNull(select_insure_typeids)){
						errMsg = "请选择您要购买的保险";
					}else if(StringUtil.isNull(applicant)){
						errMsg = "请填写投保人";
					}else if(StringUtil.isNull(applicant_id)){
						errMsg = "请填写投保人身份证号码";
					}else if(!RegularExpressionUtils.matches(applicant_id, RegularExpressionUtils.identityIDPattern)){
						errMsg = "请填写正确格式的身份证号码";
					}else if(StringUtil.isNull(insure_starttime)){
						errMsg = "请输入保险生效日期-起始日期";
					}else if(!RegularExpressionUtils.matches(insure_starttime, RegularExpressionUtils.y_M_d_DatePattern)){
						errMsg = "保险生效日期-起始日期格式不正确";
					}else if(StringUtil.isNull(insure_endtime)){
						errMsg = "请输入保险生效日期-结束日期";
					}else if(!RegularExpressionUtils.matches(insure_endtime, RegularExpressionUtils.y_M_d_DatePattern)){
						errMsg = "保险生效日期-结束日期格式不正确";
					}else{
						JSONObject orderFlagObj = orderManager.checkCreateOrderStatus(carplate);//检查是否可以下单
						boolean flag = orderFlagObj.getBoolean("flag");
						
						if(!flag){//保险未到期或有未支付订单，不能下单
							errMsg = orderFlagObj.getString("message");
						}else{
							//获取车辆险种价格map集合
							Map<Integer, JSONObject> insureTypePriceMap = insuranceManager.getCarInsureTypePriceMap(carplate, new DecimalFormat("0.00"), glass_type, thirdparty_coverage_id, driver_coverage_id, passenger_coverage_id, scratch_coverage_id, exempt_insure_typeids);
							if(insureTypePriceMap == null){
								errMsg = "根据车牌号没有查找到车辆记录";
							}else{
								long starttime = sdf.parse(getRequest().getParameter("insure_starttime")).getTime();//订单生效起始时间
								long endtime = sdf.parse(getRequest().getParameter("insure_endtime")).getTime();//订单生效结束时间
								order_spec = orderManager.addInsureOrderRecord(insureTypePriceMap, store_id, insure_company_id, select_insure_typeids, carplate, applicant, applicant_id, starttime, endtime, params);
							}
						}
					}
				}
				
				//保养预约下单
				if(order_type.equals(OrderType.REPAIR+"")){
					String time_region_id = getRequest().getParameter("time_region_id");//时间段id
					String order_date = getRequest().getParameter("order_date");//预约日期
					String repair_item_ids = getRequest().getParameter("repair_item_ids");//保养项目Id集合

					if("".equals(repair_item_ids)){
						errMsg = "请选择您要保养的项目";
					}else if(StringUtil.isNull(time_region_id)){
						errMsg = "请输入预约时间段id";
					}else if(!RegularExpressionUtils.matches(time_region_id, RegularExpressionUtils.intPattern)){
						errMsg = "预约时间段id应该为正整数";
					}else if(StringUtil.isNull(order_date)){
						errMsg = "请输入预约日期";
					}else if(!RegularExpressionUtils.matches(order_date, RegularExpressionUtils.y_M_d_DatePattern)){
						errMsg = "预约日期格式不正确，应该为yyyy-MM-dd";
					}else{
						Long orderDate = sdf.parse(getRequest().getParameter("order_date")).getTime();
						JSONObject isOrderObj = orderManager.isOrder(store_id, orderDate, time_region_id, carplate);
						int result = isOrderObj.getInt("result");
						if(result == 0){//已经预约，提示用户
							errMsg = isOrderObj.getString("message");
						}else{
							order_spec = orderManager.addRepairOrderTimeRegionRecord(store_id, time_region_id, orderDate, carplate, repair_item_ids);
						}
					}
				}
				
				if("".equals(errMsg)){
					if(order_spec != null){
						//添加product到购物车
						int cart_id = this.addProductToCart(product, order_spec, Integer.valueOf(store_id), Integer.valueOf(order_type), 1);
						if(cart_id == -1){
							this.showErrorJson("抱歉！您所选择的货品库存不足。");
							return JSON_MESSAGE;
						}
						this.storeCartManager.putCartListToSession(StringUtil.toString(cart_id));
						
						//生成保险/保养订单
						Order order = this.innerCreateOrder(carplate, order_type, cart_id+"");
						JSONObject order_detail = storeOrderManager.getInsureAndRepairOrderDetail(Integer.valueOf(store_id), order, order_type, carplate);
						
						obj.put("result", 1);
						obj.put("data", order_detail);
						obj.put("message", "预约成功");
					}
				}else{//出现重复下单或其他问题，弹出提示
					obj.put("result", 0);
					obj.put("message", errMsg);
				}
			}
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，预约失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取车辆保险、保养记录
	 * @date 2016年9月7日 下午8:09:00
	 * @return
	 */
	public String getInsureAndRepairOrderHistory(){
		try {
			String carplate = getRequest().getParameter("carplate");
			String order_type = getRequest().getParameter("order_type");
			
			JSONObject obj = storeOrderManager.getInsureAndRepairOrderHistory(carplate, order_type);
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，获取数据失败");
		}
		
		return JSON_MESSAGE;
	}
	
    /**
	 * 得到保险订单准备信息
	 * 
	 * @param product_id
	 */
	public String getInsurancesOrderReadyInfo() {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String productId = request.getParameter("product_id");
			String carplate = request.getParameter("carplate");
			JSONObject obj = orderManager.checkCreateOrderStatus(carplate);
			boolean flag = obj.getBoolean("flag");
			List list = orderManager.getInsurancesOrderReadyInfo(productId);
			JSONArray resultArray = JSONArray.fromObject(list);
			if(!flag){
				this.json = "{\"result\":0,\"data\":" + resultArray +",\"message\":" + obj.getString("message") + "}";
			}else{
				this.json = "{\"result\":1,\"data\":" + resultArray + "}";
			}
		} catch (Exception e) {
			this.logger.error("保险订单准备页", e);
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 立即下单 (添加购物车、立即下单)
	 * @return
	 */
	public String createInsureOrRepairOrderImmediate(){
		try {
			int isInsureOrRepair = Integer.valueOf(getRequest().getParameter("isInsureOrRepair"));
			String carplate = getRequest().getParameter("carplate");
			if(isInsureOrRepair == 1){//保险下单添加限制
				JSONObject obj = orderManager.checkCreateOrderStatus(carplate);
				if(!obj.getBoolean("flag")){
					this.json = "{\"result\":0,\"message\":" + obj.getString("message") + "}";
					return JSON_MESSAGE;
				}
			}
			int num = Integer.valueOf(getRequest().getParameter("num"));
			int store_id = Integer.valueOf(getRequest().getParameter("repair4sstoreid"));
			Product product = productManager.get(Integer.valueOf(getRequest().getParameter("product_id")));
			InsureRepairSpec insureOrRepairSpec = null;
			if(isInsureOrRepair == 1){//添加保险产品到购物车
				double insureestimatedmaxgain = Double.valueOf(getRequest().getParameter("insureestimatedmaxgain"));
				String applicant = getRequest().getParameter("applicant");
				String applicant_id = getRequest().getParameter("applicant_id");
				double price = Double.valueOf(getRequest().getParameter("price"));
				long insure_starttime = Long.valueOf(getRequest().getParameter("insure_starttime"));
				long insure_endtime = Long.valueOf(getRequest().getParameter("insure_endtime"));
				insureOrRepairSpec = orderManager.getInsureOrRepairSpec(1, carplate, applicant, applicant_id, insure_starttime, insure_endtime);
				product.setEnable_store(1);
				productManager.update(product);
			}
			if(isInsureOrRepair == 2){//添加保养产品到购物车
				long order_time = Long.valueOf(getRequest().getParameter("order_time"));
				int time_region_id = Integer.valueOf(getRequest().getParameter("time_region_id"));
				double price = Double.valueOf(getRequest().getParameter("price"));
				double repairestimatedmaxgain = Double.valueOf(getRequest().getParameter("repairestimatedmaxgain"));
				insureOrRepairSpec = orderManager.getInsureOrRepairSpec(2, carplate, time_region_id, store_id, order_time);
				product.setEnable_store(1);
				productManager.update(product);
			}
			int cart_id = this.addProductToCart(product, insureOrRepairSpec, store_id, isInsureOrRepair, num);
			if(cart_id == -1){
				this.showErrorJson("抱歉！您所选择的货品库存不足。");
				return JSON_MESSAGE;
			}
			this.storeCartManager.putCartListToSession(StringUtil.toString(cart_id));
			this.createInsureOrRepairOrder(StringUtil.toString(cart_id));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 添加货品的购物车
	 * @param product
	 * @param insureOrRepairSpec 
	 * @param isInsureOrRepair 
	 * @return
	 */
	private Integer addProductToCart(Product product, InsureRepairSpec insureOrRepairSpec, int store_id, int isInsureOrRepair, int num){
		try {
			String sessionid = ThreadContextHolder.getHttpRequest().getSession().getId();
			String carplate = getRequest().getParameter("carplate");
			
			if(product!=null){
				try{
					/**
					 * 保险、保养商品去掉库存校验
					 */
//					int enableStore = product.getEnable_store();
//					if (enableStore < num) {
//						throw new RuntimeException("抱歉！您所选选择的货品库存不足。");
//					}
					//查询已经存在购物车里的商品
//					Cart tempCart = cartManager.getCartByProductId(product.getProduct_id(), sessionid);
//					if(tempCart != null){
//						int tempNum = tempCart.getNum();
//						if (enableStore < num + tempNum) {
//							throw new RuntimeException("抱歉！您所选选择的货品库存不足。");
//						}
//					}
					Member member = UserConext.getCurrentMember();
					
					Cart cart = new Cart();
					cart.setGoods_id(product.getGoods_id());
					cart.setProduct_id(product.getProduct_id());
					cart.setSession_id(sessionid);
					cart.setNum(num);
					cart.setItemtype(0); //0为product和产品 ，当初是为了考虑有赠品什么的，可能有别的类型。
					cart.setWeight(product.getWeight());
					cart.setPrice(insureOrRepairSpec.getPrice());
					cart.setName(product.getName());
//					if(isInsureOrRepair == 1){//保险下单是在初装4s店下单
//						int original_storeid = (JSONObject.fromObject(carInfoManager.getCarInfoByCarplate(carplate).get(0))).getInt("original_storeid");
//						cart.setStore_id(original_storeid);
//					}else{
//						cart.setStore_id(store_id);
//					}
					cart.setStore_id(store_id);
					cart.setCarplate(carplate);
					cart.setMember_id(member.getMember_id());
					cart.setRewards_limit(insureOrRepairSpec.getRewards_limit());
					cart.setInsure_repair_specid(insureOrRepairSpec.getSpec_id());
					
					return this.cartManager.add(cart);
					
				}catch(Exception e){
					this.logger.error("将货品添加至购物车出错",e);
					this.showErrorJson(e.getMessage());
					e.printStackTrace();
					throw new RuntimeException("将货品添加至购物车出错。");
				}
			}else{
				this.showErrorJson("该货品不存在，未能添加到购物车");
				throw new RuntimeException("该货品不存在，未能添加到购物车");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 立即下单
	 * 
	 * @return
	 */
	public String createInsureOrRepairOrder(String cart_id) {
		try {
			String order_type = getRequest().getParameter("type");
			String carplate = getRequest().getParameter("carplate");
			String repair4sstoreid = getRequest().getParameter("repair4sstoreid");
			Order order = this.innerCreateOrder(carplate, order_type, cart_id);
			JSONObject obj = storeOrderManager.getOrderDetailJson(order, order_type, carplate, repair4sstoreid);
			this.json = "{\"result\":1,\"data\":"+ obj + "}";
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("创建订单出错", e);
			this.showErrorJson(e.getMessage());
		}
		
		return JSON_MESSAGE;
	}

	private Order innerCreateOrder(String carplate, String order_type, String cart_id) {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			
			// 计算价格
//			this.storeCartManager.countPrice("yes");
			
			Integer shippingId = 0; // 主订单没有配送方式

			Integer paymentId = StringUtil.toInt(request.getParameter("paymentId"), 1);
			Order order = new Order();
			order.setShipping_id(shippingId); // 配送方式
			order.setPayment_id(paymentId);// 支付方式

			// 用户选中的地址
			MemberAddress address = StoreCartContainer.getUserSelectedAddress();
			if (address == null) {
//				throw new RuntimeException("收货地址不能为空");
				address = new MemberAddress();
				address.setRegion_id(1);
			}
			Member member = UserConext.getCurrentMember();
			if(member == null){
				throw new RuntimeException("请求超时，请重新登录！");
			}
			order.setShip_name(member.getFullname()); //更改为用户全名
			order.setShip_tel(member.getTel());		  //电话
			order.setShip_mobile(member.getUsername()); //手机号
			order.setShip_provinceid(address.getProvince_id());
			order.setShip_cityid(address.getCity_id());
			order.setShip_regionid(address.getRegion_id());
			order.setShip_addr(address.getAddr());
			order.setShip_zip(address.getZip());
			order.setShipping_area(address.getProvince() + "-" + address.getCity() + "-" + address.getRegion());
			order.setRegionid(address.getRegion_id());
			order.setMemberAddress(address);
//			order.setShip_day(request.getParameter("shipDay"));
//			order.setShip_time(request.getParameter("shipTime"));
//			order.setRemark(request.getParameter("remark"));
			order.setAddress_id(address.getAddr_id());
			order.setCarplate(carplate);
			order.setOrder_type(Integer.valueOf(order_type));
//			String sessionid = request.getSession().getId();
//			order = this.storeOrderManager.createOrder(order, sessionid);
			order = this.storeOrderManager.createMainOrder(order, cart_id);
			
			return order;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			this.showErrorJson(e.getMessage());
			return null;
		}
	}
	
	
	/**
	 * 订单评价（旧版本）
	 * @return json
	 */
	public String saveComment(){
		try{
			Member member = UserConext.getCurrentMember();
			if(member == null){
				this.showErrorJson("请求超时，请重新登录！");
				return this.json;
			}
			commentManager.saveComment(member);
			showSuccessJson("评价成功");
		}catch (Exception e) {
			this.showErrorJson("服务器响应失败，请稍后再试");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 订单评价
	 * @return json
	 */
	public String saveOrderComment(){
		try{
			String orderComment = getBody(this.getRequest());
			Member member = UserConext.getCurrentMember();
			if(member == null){
				this.showErrorJson("请求超时，请重新登录！");
				return this.json;
			}
			commentManager.saveOrderComment(member,orderComment);
			showSuccessJson("评价成功");
		}catch(Exception e){
			this.showErrorJson("服务器未响应，请稍后再试");;
		}
		return JSON_MESSAGE;
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

    public IPaymentManager getPaymentManager() {
        return paymentManager;
    }

    public void setPaymentManager(IPaymentManager paymentManager) {
        this.paymentManager = paymentManager;
    }

    public ICartManager getCartManager() {
        return cartManager;
    }

    public void setCartManager(ICartManager cartManager) {
        this.cartManager = cartManager;
    }

    public IDlyTypeManager getDlyTypeManager() {
        return dlyTypeManager;
    }

    public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
        this.dlyTypeManager = dlyTypeManager;
    }

    public IMemberOrderManager getMemberOrderManager() {
        return memberOrderManager;
    }

    public void setMemberOrderManager(IMemberOrderManager memberOrderManager) {
        this.memberOrderManager = memberOrderManager;
    }

    public IReceiptManager getReceiptManager() {
        return receiptManager;
    }

    public void setReceiptManager(IReceiptManager receiptManager) {
        this.receiptManager = receiptManager;
    }

    public IOrderFlowManager getOrderFlowManager() {
        return orderFlowManager;
    }

    public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
        this.orderFlowManager = orderFlowManager;
    }

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IStoreCartManager getStoreCartManager() {
		return storeCartManager;
	}

	public void setStoreCartManager(IStoreCartManager storeCartManager) {
		this.storeCartManager = storeCartManager;
	}

	public IStoreMemberAddressManager getStoreMemberAddressManager() {
		return storeMemberAddressManager;
	}

	public void setStoreMemberAddressManager(
			IStoreMemberAddressManager storeMemberAddressManager) {
		this.storeMemberAddressManager = storeMemberAddressManager;
	}

	public IStoreTemplateManager getStoreTemplateManager() {
		return storeTemplateManager;
	}

	public void setStoreTemplateManager(IStoreTemplateManager storeTemplateManager) {
		this.storeTemplateManager = storeTemplateManager;
	}

	public IStoreDlyTypeManager getStoreDlyTypeManager() {
		return storeDlyTypeManager;
	}

	public void setStoreDlyTypeManager(IStoreDlyTypeManager storeDlyTypeManager) {
		this.storeDlyTypeManager = storeDlyTypeManager;
	}

	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}

	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}

	public CartPluginBundle getCartPluginBundle() {
		return cartPluginBundle;
	}

	public void setCartPluginBundle(CartPluginBundle cartPluginBundle) {
		this.cartPluginBundle = cartPluginBundle;
	}

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	public ICarInfoManager getCarInfoManager() {
		return carInfoManager;
	}

	public void setCarInfoManager(ICarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}

	public IInsuranceManager getInsuranceManager() {
		return insuranceManager;
	}

	public void setInsuranceManager(IInsuranceManager insuranceManager) {
		this.insuranceManager = insuranceManager;
	}

	public I4sStoresManager getC4sStoresManager() {
		return c4sStoresManager;
	}

	public void setC4sStoresManager(I4sStoresManager c4sStoresManager) {
		this.c4sStoresManager = c4sStoresManager;
	}

	public ICommentManager getCommentManager() {
		return commentManager;
	}

	public void setCommentManager(ICommentManager commentManager) {
		this.commentManager = commentManager;
	}



}
