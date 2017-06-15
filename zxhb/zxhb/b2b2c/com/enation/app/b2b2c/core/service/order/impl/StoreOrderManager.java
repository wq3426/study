package com.enation.app.b2b2c.core.service.order.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.RepairOrderDetail;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.pluin.order.StoreCartPluginBundle;
import com.enation.app.b2b2c.core.service.IStoreOrderFlowManager;
import com.enation.app.b2b2c.core.service.IStorePromotionManager;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.StoreCartKeyEnum;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.base.core.model.Store;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.model.CarInfo;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PaymentDetail;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderReportManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.InsureType;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.OrderType;
import com.enation.app.shop.core.service.impl.CarInfoManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * 多店铺订单管理类<br>
 * 负责多店铺订单的创建、查询
 * @author kingapex
 * @version 2.0: 对价格逻辑进行改造
 * 2015年8月21日下午1:49:27
 */
@Component
public class StoreOrderManager extends BaseSupport implements IStoreOrderManager{
	private IStoreCartManager storeCartManager;
	private ICartManager cartManager;
	private IDlyTypeManager dlyTypeManager;
	private IPaymentManager paymentManager;
	private OrderPluginBundle orderPluginBundle;
	private IPromotionManager promotionManager;
	private IStoreGoodsManager storeGoodsManager;
	private IStoreMemberManager storeMemberManager;
	private IOrderManager orderManager;
	private CartPluginBundle  cartPluginBundle;
	private StoreCartPluginBundle storeCartPluginBundle;
	private IStorePromotionManager storePromotionManager;
	private IBonusManager bonusManager;
	private CarInfoManager carInfoManager;
	private IStoreOrderFlowManager storeOrderFlowManager;
	private IOrderReportManager orderReportManager;
	private IStoreManager storeManager;
	private IMemberAddressManager memberAddressManager;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#createOrder(com.enation.app.shop.core.model.Order, java.lang.String, java.lang.String[])
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Order createOrder(Order order, String sessionid) {
		
		//读取所有的购物项，用于创建主订单
		List<CartItem> cartItemList = this.cartManager.listGoods(sessionid);
		
		//调用核心api计算总订单的价格，商品价：所有商品，商品重量：
		OrderPrice orderPrice = cartManager.countPrice(cartItemList, order.getShipping_id(),""+order.getRegionid());
		
		//激发总订单价格事件
		orderPrice  = this.cartPluginBundle.coutPrice(orderPrice);
		
		//设置订单价格，自动填充好各项价格，商品价格，运费等
		order.setOrderprice(orderPrice); 
		order.setWeight(orderPrice.getWeight());
		
		//调用核心api创建主订单
		Order mainOrder = this.orderManager.add(order, new ArrayList<CartItem>(), sessionid);
		//创建子订单
		this.createChildOrder(mainOrder, sessionid);
		
		//创建完子订单再清空session
		cartManager.clean(sessionid);
		StoreCartContainer.cleanSession();
		
		//返回主订单
		return mainOrder;
	}
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Order createMainOrder(Order order, String cartIds) {
		try{
			//读取所有的购物项，用于创建主订单
			List<CartItem> cartItemList = this.cartManager.listGoodsByCartids(cartIds);
			
			//调用核心api计算总订单的价格，商品价：所有商品，商品重量：
			OrderPrice orderPrice = cartManager.countPrice(cartItemList, order.getShipping_id(),""+order.getRegionid());
			
			//激发总订单价格事件
			orderPrice  = this.cartPluginBundle.coutPrice(orderPrice);
			
			//设置订单价格，自动填充好各项价格，商品价格
			order.setOrderprice(orderPrice); 
			order.setWeight(orderPrice.getWeight());
			
			
			//调用核心api创建主订单,主订单下不关联货物,sessionID可以不填
			Order mainOrder = this.orderManager.add(order, new ArrayList<CartItem>(), null);
			
			//创建子订单,sessionId可以不填
			this.createChildOrder(mainOrder, null);
			
			
			//创建完子订单再删除对应的购物车选项
			Member member = UserConext.getCurrentMember();
			cartManager.cleanCart(cartIds,member);	
			StoreCartContainer.cleanSession();
			
			//返回主订单
			return mainOrder;
		}catch(Exception e){
			throw e;
		}
	}
	
	
	
	
	
	/**
	 * 创建店铺子订单
	 * @param order 主订单
	 * @param sessionid 用户sessionid 
	 * @param shippingIds 配送方式数组,是按在结算页中的店铺顺序形成
	 */
	private void createChildOrder(Order order,String sessionid) {
		try {
			
			//获取以店铺id分类的购物车列表 
			List<Map> storeGoodsList=StoreCartContainer.getStoreCartListFromSession();
			int num=1;
			
			//以店铺分单位循环购物车列表
			for (Map map : storeGoodsList) {
				
				//当前店铺的配送方式,中安没有配送方式,默认设置为0
	//			Integer shippingId = (Integer)map.get(StoreCartKeyEnum.shiptypeid.toString());
				Integer shippingId = 0;
				Integer shippingMethod = 2;
				
				
				//先将主订单的信息copy一份
				StoreOrder storeOrder =this.copyOrder(order);
				
				//如果copy属性异常，则抛出异常
				if(storeOrder==null){
					throw new RuntimeException("创建子订单出错，原因为：beanutils copy属性出错。");
				}
				
				//获取此店铺id
				int store_id =(Integer)map.get(StoreCartKeyEnum.store_id.toString());
				
				//获取店铺名称
				String store_name =(String) map.get(StoreCartKeyEnum.store_name.toString());
				
				
				int service_store_id = store_id;
				String service_store_name = store_name;
				if(storeOrder.getOrder_type() == null){
					storeOrder.setOrder_type(OrderType.GOODS);
				}
				if(store_id == 1){//中安商品对签约4S下单，4S店提供服务
					if(storeOrder.getOrder_type() != OrderType.ZA_INSURANCE){
						Store store = storeManager.getSignStore(storeOrder.getCarplate());
						if(store!=null){
							service_store_id  = store.getStore_id();
							service_store_name = store.getStore_name();
						}
						storeOrder.setOrder_type(OrderType.ZA_GOODS);
					}
				}
				//服务机构id
				storeOrder.setService_store_id(service_store_id);
				
				//服务机构名称
				storeOrder.setService_store_name(service_store_name);
				
				//设置订单为未结算
				storeOrder.setBill_status(0);
				
				
				//设置店铺的id
				storeOrder.setStore_id(store_id);
				
				
				
				//店铺名称
				storeOrder.setStore_name(store_name);
				
				//配送方式id
				storeOrder.setShipping_id(shippingId);
				
				//配送方法，邮寄还是4s店
				storeOrder.setShipping_method(shippingMethod);
				
				//设置父订id
				storeOrder.setParent_id(order.getOrder_id());
				
				//取得此店铺的购物列表
				List itemlist=(List) map.get(StoreCartKeyEnum.goodslist.toString());
				
				//调用核心api计算总订单的价格，商品价：所有商品，商品重量：
				OrderPrice orderPrice  =(OrderPrice)map.get(StoreCartKeyEnum.storeprice.toString());
				
				//设置订单价格，自动填充好各项价格，商品价格，运费等
				storeOrder.setOrderprice(orderPrice); 			
				
				// 设置为子订单
				storeOrder.setIs_child_order(true);
				storeOrder.setSn(order.getSn()+num);//子订单不带横杠 但是比主订单多一个字
				//调用订单核心类创建子订单
				this.orderManager.add(storeOrder, itemlist, sessionid);
				num++;
			}
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	
	/**
	 * copy一个订单的属性 生成新的订单
	 * @param order  主订单
	 * @return 新的子订单
	 */
	private StoreOrder copyOrder(Order order){
		StoreOrder store_order = new StoreOrder();
		try {
			BeanUtils.copyProperties(store_order,order);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return store_order;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#storeOrderList(java.lang.Integer, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Page storeOrderList(Integer pageNo, Integer pageSize,Integer storeid,Map map) {
		String order_state=String.valueOf(map.get("order_state"));
		String keyword=String.valueOf(map.get("keyword"));
		String buyerName=String.valueOf(map.get("buyerName"));
		String startTime=String.valueOf(map.get("startTime"));
		String endTime=String.valueOf(map.get("endTime"));
		String order_type = String.valueOf(map.get("order_type"));
		StringBuffer sql = new StringBuffer();
		if (storeid == 1) {
			sql.append("select * from es_order o where store_id =" + storeid + " and disabled=0");
		} else {// 中安店铺只看store_id
			sql.append("select * from es_order o where service_store_id =" + storeid + " and disabled=0");
		}

		if(!StringUtil.isEmpty(order_state)&&!order_state.equals("all")){
			if(order_state.equals("wait_ship") ){ //对待服务的处理 cod为货到付款
				 sql.append(" and ( ( payment_type!='cod' and payment_id!=8  and  status IN("+OrderStatus.ORDER_PAY_CONFIRM +","+OrderStatus.ORDER_SERVECE+","+OrderStatus.ORDER_SHIP+") AND pay_status = "+OrderStatus.PAY_CONFIRM+"   ) ");//非货到付款的，要已结算才能发货
				 sql.append(" or ( payment_type='cod' and  status="+OrderStatus.ORDER_NOT_PAY +")) ");//货到付款的，新订单（已确认的）就可以发货
			// 等待收款处理 货到付款 已收货状态
			} else if(order_state.equals("wait_pay")) {//待支付状态为
				
				sql.append(" AND pay_status = "+OrderStatus.PAY_NO+" AND o.status IN ("+OrderStatus.ORDER_NOT_PAY+","+OrderStatus.ORDER_NOT_CONFIRM+") ");
//				sql.append(" AND payment_type = 'cod' AND payment_id = 3 AND pay_status = 0 AND status = " + OrderStatus.ORDER_APPRAISE/*ORDER_ROG*/);				
				// 之前的逻辑注释 add_by_sylow 2016-01-12
				//非货到付款的，未付款状态的可以结算
				//sql.append(" and ( ( payment_type != 'cod' and  status = " + OrderStatus.ORDER_NOT_PAY + ") ");
				//sql.append(" or ( status != " + OrderStatus.ORDER_NOT_PAY + "  and  pay_status!=" + OrderStatus.PAY_CONFIRM  + ")" );
				
				//货到付款的要发货或收货后才能结算
				//sql.append(" or ( payment_type='cod' and  (status=" + OrderStatus.ORDER_SHIP + " or status="+OrderStatus.ORDER_ROG + " )  ) )");
				
			}else if(order_state.equals("wait_rog") ){ 
				sql.append(" and status="+OrderStatus.ORDER_SERVECE/*ORDER_SHIP*/  ); 
			}else if(order_state.equals("yet_service")){
				sql.append(" and pay_status="+OrderStatus.PAY_CONFIRM+" AND o.status IN ("+OrderStatus.ORDER_APPRAISE+","+OrderStatus.ORDER_COMPLETE+")");
			}
			else{
				sql.append(" and status="+order_state);
			}
		}
		if(!StringUtil.isNull(keyword)){
			sql.append(" AND o.sn like '%" + keyword + "%'");
		}
		if(!StringUtil.isNull(buyerName)){
			sql.append(" AND (o.ship_name LIKE '%"+buyerName+"%'  or o.ship_mobile  LIKE '%"+buyerName+"%') ");
		}
		if(!StringUtil.isNull(startTime)){
			sql.append(" AND o.create_time >"+DateUtil.getDateline(startTime));
		}
		
		if(!StringUtil.isNull(endTime)){
			//2015-11-04 add by sylow 
			endTime += " 23:59:59";
			sql.append(" AND o.create_time <"+DateUtil.getDateline(endTime, "yyyy-MM-dd HH:mm:ss"));
		}
		
		if(!StringUtil.isNull(order_type)){
			sql.append(" AND o.order_type = ").append(order_type);	
		}
		sql.append (" order by o.create_time desc");
		Page rpage = this.daoSupport.queryForPage(sql.toString(),pageNo, pageSize, Order.class);
		return rpage;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#storeOrderList(java.lang.Integer)
	 */
	@Override
	public List storeOrderList(Integer parent_id) {
		StringBuffer sql=new StringBuffer("SELECT * from es_order WHERE  disabled=0 AND parent_id="+parent_id);
		return this.daoSupport.queryForList(sql.toString(), StoreOrder.class);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#get(java.lang.Integer)
	 */
	@Override
	public StoreOrder get(Integer orderId) {
		String sql = "select * from es_order where order_id=?";
		StoreOrder order = (StoreOrder) this.daoSupport.queryForObject(sql,StoreOrder.class, orderId);
		return order;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#saveShipInfo(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean saveShipInfo(String remark, String ship_day,
			String ship_name, String ship_tel, String ship_mobile,
			String ship_zip, int orderid) {
		Order order = this.get(orderid);
		try {
			if(ship_day !=null && !StringUtil.isEmpty(ship_day)&&!ship_day.equals(order.getShip_day())){
				this.baseDaoSupport.execute("update order set ship_day=?  where order_id=?", ship_day,	orderid);
			}
			if(remark !=null && !StringUtil.isEmpty(remark)&&!remark.equals("undefined")&&!remark.equals(order.getRemark())){
				this.baseDaoSupport.execute("update order set remark= ?  where order_id=?",	remark,orderid);
			}
			if(ship_name !=null && !StringUtil.isEmpty(ship_name)&&!ship_name.equals(order.getShip_name())){
				this.baseDaoSupport.execute("update order set ship_name=?  where order_id=?", ship_name,	orderid);
			}
			if(ship_tel !=null && !StringUtil.isEmpty(ship_tel)&&!ship_tel.equals(order.getShip_tel())){
				this.baseDaoSupport.execute("update order set ship_tel=?  where order_id=?", ship_tel,	orderid);
			}
			if(ship_mobile !=null && !StringUtil.isEmpty(ship_mobile)&&!ship_mobile.equals(order.getShip_mobile())){
				this.baseDaoSupport.execute("update order set ship_mobile=?  where order_id=?", ship_mobile,	orderid);
			}
			if(ship_zip !=null && !StringUtil.isEmpty(ship_zip)&&!ship_zip.equals(order.getShip_zip())){
				this.baseDaoSupport.execute("update order set ship_zip=?  where order_id=?", ship_zip,	orderid);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	@Override
	public Page pageBuyerOrders(int pageNo, int pageSize, String status, String keyword){
		
		/**
		 * 准备查询的参数，将来要转换为Object[] 传给jdbc
		 */
		List argsList  = new ArrayList();
		
		
		/**
		 * 查询当前会员的订单
		 */
		StoreMember member = storeMemberManager.getStoreMember();
		StringBuffer sql = new StringBuffer("SELECT * FROM es_order o where o.parent_id is NOT NULL and  member_id=?");
		argsList.add(member.getMember_id());
		
		
		
		/**
		 * 按状态查询
		 */
		if(!StringUtil.isEmpty(status)){
			int statusNumber = StringUtil.toInt(status,-999);
			//等待付款的订单 按付款状态查询
			if(statusNumber==0){
				//货到付款
				sql.append(" AND status!="+OrderStatus.ORDER_CANCELLATION+" AND pay_status="+OrderStatus.PAY_NO);
			}else{
				sql.append(" AND status=?");
				argsList.add(status); //将状态压入 参数list
			}
		}
		
		
		
		
		/**
		 * 按关键字查询
		 */
		if(!StringUtil.isEmpty(keyword)){
			sql.append(" AND order_id in (SELECT i.order_id FROM es_order_items i INNER JOIN es_order o ON i.order_id=o.order_id WHERE o.member_id=?"
					+ " AND (i.name like ? OR o.sn LIKE ?))");
			
			argsList.add(member.getMember_id()); 
			argsList.add("%"+keyword+"%"); //将关键字做为name参数查询 压入参数list
			argsList.add("%"+keyword+"%"); //将关键字做为sn参数查询 压入参数list
			
		}
		
		sql.append(" order by o.create_time desc");
		
		/**
		 * 将参数list 转为Object[]
		 */
		int size =argsList.size();
		Object[] args = argsList.toArray(new Object[size]);
		
		/**
		 * 分页查询买家订单
		 */
		Page webPage  = this.daoSupport.queryForPage(sql.toString(), pageNo,pageSize,Order.class,  args);
		
		return webPage;
	}
	 
	
	@Override
	public Page pageChildOrderList(Integer pageNo, int pageSize, String status, String keyword) {
			try {
				HttpServletRequest request = ThreadContextHolder.getHttpRequest();
				StoreMember member = storeMemberManager.getStoreMember();
				StringBuffer sql = new StringBuffer(
						"SELECT	o.*, s.attr as service_store_attr,s.customer_phone as service_store_tel,c.totalgain FROM 	es_order o, es_store s ,es_carinfo c WHERE o.service_store_id = s.store_id  and o.carplate = c.carplate and	o.member_id = ? AND o.disabled = 0");
				if (!StringUtil.isEmpty(status)) {
					//等待付款的订单 按付款状态查询
					sql.append(" AND status in(" + status + ")");
				} else {
					sql.append(
							" AND status!=" + OrderStatus.ORDER_CANCELLATION + " AND pay_status=" + OrderStatus.PAY_NO);
				}
				String carplate = request.getParameter("carplate");
				if (!StringUtil.isNull(carplate)) {
					sql.append(" AND o.carplate = '" + carplate + "'");
				}
				//		if(!StringUtil.isEmpty(keyword)){//搜索暂时不用
				//			sql.append(" AND order_id in (SELECT i.order_id FROM " + this.getTableName("order_items") + " i LEFT JOIN "+this.getTableName("order")+" o ON i.order_id=o.order_id WHERE o.member_id='"+member.getMember_id()+"' AND i.name like '%" + keyword + "%')");
				//		}
				sql.append(" AND o.parent_id is NOT NULL order by o.create_time desc");

				Page rpage = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, member.getMember_id());
				rpage = this.getOrderStoreBonus(rpage);//根据订单状态(未支付：获取最优优惠券)和(支付后：订单使用的优惠券)
				rpage = getInsureOrRepairOrderDetail(rpage);//获取保险/保养订单详情
				return rpage;
			} catch (Exception e) {
				throw  e;
			}
			
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#pageOrders(int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public Page pageChildOrders(int pageNo, int pageSize, String status, String keyword) {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		StoreMember member = storeMemberManager.getStoreMember();
		StringBuffer sql =new StringBuffer("SELECT	o.*, s.attr as service_store_attr,s.after_phone as service_store_tel,c.totalgain FROM 	es_order o, es_store s ,es_carinfo c WHERE o.service_store_id = s.store_id  and o.carplate = c.carplate and	o.member_id = ? AND o.disabled = 0");
		if(!StringUtil.isEmpty(status)){
			//等待付款的订单 按付款状态查询
				sql.append(" AND status in(" + status + ")");
		}else{
			sql.append(" AND status!="+OrderStatus.ORDER_CANCELLATION+" AND pay_status="+OrderStatus.PAY_NO);
		}
		String carplate = request.getParameter("carplate");
		if(!StringUtil.isNull(carplate)){
			sql.append(" AND o.carplate = '" + carplate +"'");
		}
//		if(!StringUtil.isEmpty(keyword)){//搜索暂时不用
//			sql.append(" AND order_id in (SELECT i.order_id FROM " + this.getTableName("order_items") + " i LEFT JOIN "+this.getTableName("order")+" o ON i.order_id=o.order_id WHERE o.member_id='"+member.getMember_id()+"' AND i.name like '%" + keyword + "%')");
//		}
		sql.append(" AND o.parent_id is NOT NULL order by o.create_time desc");
		Page rpage = this.daoSupport.queryForPage(sql.toString(),pageNo, pageSize, member.getMember_id());
		rpage = this.getOrderStoreBonus(rpage);//根据订单状态(未支付：获取最优优惠券)和(支付后：订单使用的优惠券)
		rpage = getInsureAndRepairOrderDetail(rpage);//获取保险/保养订单详情
		return rpage;
	}
	
	private Page getOrderStoreBonus(Page rpage) {
		try {
			JSONArray jsonArray = JSONArray.fromObject(rpage.getResult());
			JSONArray returnArray = new JSONArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject obj = JSONObject.fromObject(jsonArray.get(i));
				String order_sn = obj.getString("sn").toString();

				int order_id = obj.getInt("order_id");
				int order_type = obj.getInt("order_type");
				int status = obj.getInt("status");
				int store_id = obj.getInt("store_id");
				double need_pay_money = obj.getDouble("need_pay_money");
				int member_id = obj.getInt("member_id");
				if (order_type == OrderType.GOODS || order_type == OrderType.ZA_GOODS) {//当订单为普通商品时绑定优惠券
					if (status == OrderStatus.ORDER_NOT_PAY || status == OrderStatus.PAY_NO) {//当订单status状态为未付款时，匹配最佳选择
						List<Map> storeBonusList = storePromotionManager.getBonusInPay(member_id, store_id,
								need_pay_money, System.currentTimeMillis());
						Map storeBonus = (storeBonusList != null && storeBonusList.size() > 0) ? storeBonusList.get(0)
								: null;
						obj.put("storeBonus", storeBonus);
					} else {//付款后则选择该订单绑定的
						Map storeBonus = storePromotionManager.getStoreBonusByOrder(member_id, store_id, order_id);
						obj.put("storeBonus", storeBonus);
					}
				} else {
					obj.put("storeBonus", null);
				}
				returnArray.add(obj);
			}
			rpage.setResult(returnArray);
			return rpage;
		} catch (Exception e) {
			throw e;
		}
	}

	private Page getInsureOrRepairOrderDetail(Page rpage){
		try {
			JSONArray jsonArray = JSONArray.fromObject(rpage.getResult());
			JSONArray returnArray = new JSONArray();
			for(int i=0; i<jsonArray.size(); i++){
				JSONObject obj = JSONObject.fromObject(jsonArray.get(i));
				JSONArray items_json = new JSONArray();
				JSONObject item_json = JSONArray.fromObject(obj.get("items_json")).getJSONObject(0);
				int insure_repair_specid = item_json.getInt("insure_repair_specid");
				int order_type = obj.getInt("order_type");
				String carplate = obj.getString("carplate");
				int store_id = obj.getInt("service_store_id");
				
				//车辆信息
				String sql = "SELECT t1.carmodelid, t1.`repair4sstoreid`, t1.`totalgain` FROM es_carinfo t1, es_carmodels t2 WHERE t1.`carmodelid`=t2.id AND t1.carplate=?";
				List carinfoList = daoSupport.queryForList(sql, carplate);
				JSONObject carObj = JSONObject.fromObject(carinfoList.get(0));
				int repair4sstoreid = carObj.getInt("repair4sstoreid");
				int carmodelid = carObj.getInt("carmodelid");
				
				if(order_type == OrderType.ZA_INSURANCE || order_type == OrderType.STORE_INSURANCE){//保险详情
					sql = "SELECT t1.select_insure_typeids, t1.insure_params, t1.price, t2.company_name, REPLACE(t2.logo,?,?) logo, t2.telephone, t1.repair_coin FROM es_insure_order_spec t1, es_insurance_company_info t2 WHERE t1.`insure_company_id`=t2.`id` AND t1.spec_id=?";
					List insureList = daoSupport.queryForList(sql, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_domain(), insure_repair_specid);
					JSONObject insure_info_Obj = JSONObject.fromObject(insureList.get(0));
					String select_insure_typeids = insure_info_Obj.getString("select_insure_typeids");
					Map<Integer, Object> insure_params_valueMap = InsureType.getInsureParamValueMap(JSONArray.fromObject(insure_info_Obj.getString("insure_params")));
					String[] typeArray = select_insure_typeids.split(",");
					String insure_content = "";
					for(int j=0; j<typeArray.length; j++){
						int key = Integer.valueOf(typeArray[j]);
						insure_content += InsureType.getInsureTypeMap().get(key) + (insure_params_valueMap.get(key) == null || "".equals(insure_params_valueMap.get(key)) ? "" : "("+insure_params_valueMap.get(key)+")") + ",";
					}
					
					JSONObject insureObj = new JSONObject();
					insureObj.put("insure_content", insure_content.subSequence(0, insure_content.length() - 1));
					insureObj.put("price", insure_info_Obj.getString("price"));
					insureObj.put("company_name", insure_info_Obj.getString("company_name"));
					insureObj.put("logo", insure_info_Obj.getString("logo"));
					insureObj.put("telephone", insure_info_Obj.getString("telephone"));
					insureObj.put("repair_coin", insure_info_Obj.get("repair_coin"));
					
					
					//返回订单详情
					item_json.put("totalgain", carObj.getInt("totalgain"));
					item_json.put("insureObj", insureObj);
					item_json.put("order_type", order_type);
					
					items_json.add(item_json);
					
					obj.put("items_json", items_json);
				}
	
				if(order_type == OrderType.REPAIR){//保养详情
					
					//店铺信息
					sql = "select REPLACE(store_logo,?,?) store_logo, discountcontract, discountnoncontract from es_store where store_id=?";
					List list = daoSupport.queryForList(sql, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_domain(), store_id);
					JSONObject storeInfoObj = JSONObject.fromObject(list.get(0));
					
					//保养详细信息
					double discountcontract = storeInfoObj.getDouble("discountcontract");
					double discountnoncontract = storeInfoObj.getDouble("discountnoncontract");
					double discount = 0.0;
					
					if(repair4sstoreid == store_id){//签约折扣
						discount = discountcontract;
					}else{//非签约折扣
						discount = discountnoncontract;
					}
					
					//获取订单日期、时间段、费率、保养项目内容id集合
					sql = "SELECT t1.repair_mile, FROM_UNIXTIME(t1.order_date/1000, '%Y-%m-%d') order_date, t2.`starttime`, t2.`endtime`, t1.repair_item_ids, t2.ratio "
					    + "FROM es_repair_order_spec t1, es_repair_timeregion t2 WHERE t1.time_region_id=t2.time_region_id AND t1.spec_id=?";
					JSONObject repairInfoObj = JSONObject.fromObject(daoSupport.queryForList(sql, insure_repair_specid).get(0));
					String repair_item_ids = repairInfoObj.getString("repair_item_ids");
					double ratio = repairInfoObj.getDouble("ratio");
					
					//获取保养项目内容（项目名称、价格）
					sql = "SELECT t2.itemname, t1.item_price * "+ discount +" item_price "
						+ "FROM es_store_repairitem t1, es_repair_items t2 WHERE t1.repair_item_id=t2.id "
						+ "AND store_id=? AND carmodel_id=? AND t1.repair_item_id IN "+ "("+ repair_item_ids +")";
					List repairitemList = daoSupport.queryForList(sql, store_id, carmodelid);
					
					//获取工时费合计
					sql = "SELECT SUM(t1.repair_price * "+ discount +"* "+ ratio +") servicetime_total_price "
						+ "FROM es_store_repairitem t1, es_repair_items t2 WHERE t1.repair_item_id=t2.id "
						+ "AND store_id=? AND carmodel_id=? AND t1.repair_item_id IN "+ "("+ repair_item_ids +")";
					List repair_price_list = daoSupport.queryForList(sql, store_id, carmodelid);
					
					JSONObject repairObj = new JSONObject();
					repairObj.put("order_date", repairInfoObj.getString("order_date"));
					repairObj.put("repair_mile", repairInfoObj.getString("repair_mile"));
					repairObj.put("starttime", repairInfoObj.getString("starttime"));
					repairObj.put("endtime", repairInfoObj.getString("endtime"));
					repairObj.put("repairitemList", JSONArray.fromObject(repairitemList));
					repairObj.put("servicetime_total_price", JSONObject.fromObject(repair_price_list.get(0)).getDouble("servicetime_total_price"));
					repairObj.put("store_logo", storeInfoObj.getString("store_logo"));
					
					//查询车辆在下单店铺可使用的保养币
					sql = "SELECT repair_total_coin FROM es_car_repair_coin WHERE store_id=? AND carplate=?";
					List coinList = daoSupport.queryForList(sql, store_id, carplate);
					long repair_total_coin = 0;
					if(coinList.size() > 0){
						repair_total_coin = JSONObject.fromObject(coinList.get(0)).getLong("repair_total_coin");
					}
					
					//返回订单详情
					item_json.put("totalgain", carObj.getDouble("totalgain"));
					item_json.put("repairObj", repairObj);
					item_json.put("order_type", order_type);
					item_json.put("repair_total_coin", repair_total_coin);
					item_json.put("repair_coin_amount", repair_total_coin / 1);//保养币抵用金额，目前是1:1
	
					items_json.add(item_json);
					
					obj.put("items_json", items_json);
				}
	
				returnArray.add(obj);
			}
			rpage.setResult(JSONArray.toList(returnArray));
			return rpage;
		} catch (Exception e) {
			throw e;
		}
	}

	private Page getInsureAndRepairOrderDetail(Page rpage) {
		JSONArray jsonArray = JSONArray.fromObject(rpage.getResult());
		JSONArray returnArray = new JSONArray();
		for(int i=0; i<jsonArray.size(); i++){
			JSONObject obj = JSONObject.fromObject(jsonArray.get(i));
			int order_type = obj.getInt("order_type");
			String carplate = obj.getString("carplate");
			if(order_type == OrderType.ZA_INSURANCE || order_type == OrderType.STORE_INSURANCE){//保险详情
				String sql = "select insureestimatedfee mkprice, totalgain securitygain, insuresetid from es_carinfo where carplate=?";
				List tmplist = daoSupport.queryForList(sql, carplate);
				JSONObject carinfoJson = JSONObject.fromObject(tmplist.get(0));
				sql = "select * from es_insurances where id in("+ carinfoJson.getString("insuresetid") +")";
				tmplist = daoSupport.queryForList(sql);
				net.sf.json.JSONArray insureArray = JSONArray.fromObject(tmplist);
				String insureContent = "";
				String insureCompany = "";
				String telephone = "";
				for(int j=0; j<insureArray.size(); j++){
					insureContent += insureArray.getJSONObject(j).getString("insurance") + ",";
					insureCompany = insureArray.getJSONObject(j).getString("company");
					telephone = insureArray.getJSONObject(j).getString("telephone");
				}
				insureContent = insureContent.substring(0, insureContent.lastIndexOf(","));
				
				JSONArray item_json = JSONArray.fromObject(obj.get("items_json"));
				item_json.getJSONObject(0).put("companyname", insureCompany);
				item_json.getJSONObject(0).put("insurecontent", insureContent);
				item_json.getJSONObject(0).put("telephone", telephone);
				item_json.getJSONObject(0).put("order_type", order_type);
				
				obj.put("items_json", item_json);
			}
			if(order_type == OrderType.REPAIR){//保养详情
				String sql = "SELECT c1.repair4sstoreid, c1.repairlastmile, c1.carmodelid, c1.totalgain," 
						   + " c2.repairinterval, c2.brand"
						   + " FROM es_carinfo c1, es_carmodels c2 WHERE c1.carmodelid=c2.id and c1.carplate=?";
				List list = daoSupport.queryForList(sql, carplate);
				JSONObject tmpObj = JSONObject.fromObject(list.get(0));
				long repairlastmile = tmpObj.getInt("repairlastmile");//上次保养里程
				long repairinterval = tmpObj.getInt("repairinterval");//保养间隔里程
				String brand = tmpObj.getString("brand");
				//计算下次保养里程
				long nextmile = ((repairlastmile % repairinterval) > (repairinterval / 2)) ? ((repairlastmile/repairinterval) + 2)*repairinterval : ((repairlastmile/repairinterval) + 1)*repairinterval;
				
				JSONArray item_json = JSONArray.fromObject(obj.get("items_json"));
				int insure_repair_specid = item_json.getJSONObject(0).getInt("insure_repair_specid");
				sql = "SELECT a.order_time, b.time_region_id, b.time_region FROM es_insure_repair_spec a, es_repair_timeregion b WHERE a.`time_region_id`=b.`time_region_id` AND a.`spec_id`=?";
				JSONObject regionObj = JSONObject.fromObject(daoSupport.queryForList(sql, insure_repair_specid).get(0));
				int time_region_id = regionObj.getInt("time_region_id");
				String time_region = regionObj.getString("time_region");
				String order_time = DateUtil.toString(regionObj.getLong("order_time"), "yyyy-MM-dd");
				
				item_json.getJSONObject(0).put("brand", brand);
				item_json.getJSONObject(0).put("nextmile", nextmile);//保养服务里程
				item_json.getJSONObject(0).put("order_time", order_time);//保养预约时间
				item_json.getJSONObject(0).put("time_region_id", time_region_id);//保养时间段
				item_json.getJSONObject(0).put("time_region", time_region);
				item_json.getJSONObject(0).put("name", brand+nextmile+"公里保养服务");
				item_json.getJSONObject(0).put("order_type", order_type);
				
				obj.put("items_json", item_json);
			}
			returnArray.add(obj);
		}
		rpage.setResult(JSONArray.toList(returnArray));
		return rpage;
	}


	public Integer getTotalSize(String status,String keyword,StoreMember member){
		StringBuffer sql =new StringBuffer("select count(order_id) from es_order where member_id = '" + member.getMember_id() + "' and disabled=0");
		if(!StringUtil.isEmpty(status)){
			int statusNumber = -999;
			statusNumber = StringUtil.toInt(status);
			//等待付款的订单 按付款状态查询
			if(statusNumber==0){
				sql.append(" AND status!="+OrderStatus.ORDER_CANCELLATION+" AND pay_status="+OrderStatus.PAY_NO);
			}else{
				sql.append(" AND status='" + statusNumber + "'");
			}
		}
		if(!StringUtil.isEmpty(keyword)){
			sql.append(" AND order_id in (SELECT i.order_id FROM es_order_items i LEFT JOIN es_order o ON i.order_id=o.order_id WHERE o.member_id='"+member.getMember_id()+"' "
					+ "AND (i.`name` like '%"+keyword+"%' OR o.sn LIKE '%"+keyword+"%'))");
		}
		sql.append(" AND parent_id is not null order by create_time desc ");
		
		return this.daoSupport.queryForInt(sql.toString());
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#getStoreOrderNum(int)
	 */
	@Override
	public int getStoreOrderNum(int status) {
		StoreMember member = storeMemberManager.getStoreMember();
		String sql = "select count(order_id) from es_order o where o.service_store_id ="+member.getStore_id()+"  and o.disabled=0 ";
		if(status != -999){
			sql=sql+" AND o.status in("+status+") ";
		}
		return this.daoSupport.queryForInt(sql);
	}
	
	@Override
	public int getStoreOrderNum(String status, String pay_status) {
		StoreMember member = storeMemberManager.getStoreMember();
		String sql = "select count(order_id) from es_order o where o.service_store_id ="+member.getStore_id()+" and o.disabled=0 AND o.status in("+status+") AND o.pay_status="+pay_status;
		return this.daoSupport.queryForInt(sql);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#get(java.lang.String)
	 */
	@Override
	public StoreOrder get(String ordersn) {
		String sql  ="select * from es_order where sn='"+ordersn+"'";
		StoreOrder order  = (StoreOrder)this.daoSupport.queryForObject(sql, StoreOrder.class);
		return order;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#listOrder(java.util.Map, int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public Page listOrder(Map map, int page, int pageSize, String other,String order) {
		
		String sql = createTempSql(map, other,order);
		Page webPage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
		
		return webPage;
	}
	
	/**
	 * 生成查询sql
	 * @param map
	 * @param sortField
	 * @param sortType
	 * @return
	 */
	private String  createTempSql(Map map,String sortField,String sortType){
		
		Integer stype = (Integer) map.get("stype");
		String keyword = (String) map.get("keyword");
		String orderstate =  (String) map.get("order_state");//订单状态特殊查询
		String start_time = (String) map.get("start_time");
		String end_time = (String) map.get("end_time");
		Integer status = (Integer) map.get("status");
		String sn = (String) map.get("sn");
		String ship_name = (String) map.get("ship_name");
		String ship_mobile = (String) map.get("ship_mobile");
		Integer paystatus = (Integer) map.get("paystatus");
//		Integer shipstatus = (Integer) map.get("shipstatus");
//		Integer shipping_type = (Integer) map.get("shipping_type");
		Integer payment_id = (Integer) map.get("payment_id");
		Integer depotid = (Integer) map.get("depotid");
		String complete = (String) map.get("complete");
		String store_name= (String) map.get("store_name");
//		Integer store_id=(Integer)map.get("store_id");
		String parent_sn=(String)map.get("parent_sn");
		String order_type = (String)map.get("order_type");
		StringBuffer sql =new StringBuffer();
 
		sql.append("select * from order o where disabled=0 and parent_id is NOT NULL "); //只查询出子订单
 
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql.append(" and (sn like '%"+keyword+"%'");
				sql.append(" or ship_name like '%"+keyword+"%')");
			}
		}
		
		if(status!=null){
			sql.append("and status="+status);
		}
		
		if(sn!=null && !StringUtil.isEmpty(sn)){
			sql.append(" and sn like '%"+sn+"%'");
		}
		
		if(ship_name!=null && !StringUtil.isEmpty(ship_name)){
			sql.append(" and ship_name like '%"+ship_name+"%'");
		}
		if(ship_mobile!=null && !StringUtil.isEmpty(ship_mobile)){
			sql.append(" and ship_mobile like '%"+ship_mobile+"%'");
		}
		
		if(paystatus!=null){
			sql.append(" and pay_status="+paystatus);
		}
		if(!StringUtil.isNull(order_type)){
			sql.append(" and order_type="+order_type);
		}
//		if(shipstatus!=null){
//			sql.append(" and ship_status="+shipstatus);
//		}
//		
//		if(shipping_type!=null){
//			sql.append(" and shipping_id="+shipping_type);
//		}
		
		if(payment_id!=null){
			sql.append(" and payment_id="+payment_id);
		}
		
		if (depotid!=null && depotid > 0) {
			sql.append(" and depotid=" + depotid);
		}
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = com.enation.framework.util.DateUtil.getDateline(start_time+" 00:00:00");
			sql.append(" and create_time>"+stime);
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = com.enation.framework.util.DateUtil.getDateline(end_time +" 23:59:59");
			sql.append(" and create_time<"+etime);
		}
		if( !StringUtil.isEmpty(orderstate)){
			if(orderstate.equals("wait_ship") ){ //对待发货的处理
				sql.append(" and ( ( payment_type!='cod' and  status="+OrderStatus.ORDER_PAY_CONFIRM +") ");//非货到付款的，要已结算才能发货
				sql.append(" or ( payment_type='cod' and  status="+OrderStatus.ORDER_NOT_PAY +")) ");//货到付款的，新订单（已确认的）就可以发货
			}else if(orderstate.equals("wait_pay") ){
				sql.append(" and ( ( payment_type!='cod' and  status="+OrderStatus.ORDER_NOT_PAY +") ");//非货到付款的，未付款状态的可以结算
				sql.append(" or ( status!="+OrderStatus.ORDER_NOT_PAY+"  and  pay_status!="+OrderStatus.PAY_CONFIRM +")" ); 
				sql.append(" or ( payment_type='cod' and  (status="+OrderStatus.ORDER_SERVECE/*ORDER_SHIP*/ +" or status="+OrderStatus.ORDER_APPRAISE/*ORDER_ROG*/+" )  ) )");//货到付款的要发货或收货后才能结算
			
			}else if(orderstate.equals("wait_rog") ){ 
				sql.append(" and status="+OrderStatus.ORDER_SERVECE/*ORDER_SHIP*/  ); 
			}else{
				sql.append(" and status="+orderstate);
			}
		
		}
		
		if(!StringUtil.isEmpty(complete)){
			sql.append(" and status="+OrderStatus.ORDER_COMPLETE);
		}
		if(!StringUtil.isEmpty(store_name)){
			sql.append(" and o.store_id in(select store_id from es_store where store_name like '%"+store_name+"%')");
		}
		/*if(store_id!=null){
			sql.append(" and o.store_id="+store_id);
		}*/
		if(!StringUtil.isEmpty(parent_sn)){
			sql.append(" AND parent_id=(SELECT order_id FROM es_order WHERE sn='"+parent_sn+"')");
		}
		sql.append(" ORDER BY "+sortField+" "+sortType);
		
		return sql.toString();
	}
	@Override
	public Map getStatusJson() {
		Map orderStatus = new  HashMap();
		
		orderStatus.put(""+OrderStatus.ORDER_NOT_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_PAY));
		//orderStatus.put(""+OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));
		orderStatus.put(""+OrderStatus.ORDER_NOT_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_CONFIRM));
		orderStatus.put(""+OrderStatus.ORDER_PAY_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY_CONFIRM));
		orderStatus.put(""+OrderStatus.ORDER_ALLOCATION_YES, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ALLOCATION_YES));
//		orderStatus.put(""+OrderStatus.ORDER_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SHIP));
//		orderStatus.put(""+OrderStatus.ORDER_ROG, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ROG));
		orderStatus.put(""+OrderStatus.ORDER_SERVECE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SERVECE));
		orderStatus.put(""+OrderStatus.ORDER_APPRAISE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_APPRAISE));
		orderStatus.put(""+OrderStatus.ORDER_CANCEL_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_SHIP));
		orderStatus.put(""+OrderStatus.ORDER_COMPLETE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_COMPLETE));
		orderStatus.put(""+OrderStatus.ORDER_CANCEL_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_PAY));
		orderStatus.put(""+OrderStatus.ORDER_CANCELLATION, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCELLATION));

		return orderStatus;
	}
	@Override
	public Map getpPayStatusJson() {
		Map pmap = new HashMap();
		pmap.put(""+OrderStatus.PAY_NO, OrderStatus.getPayStatusText(OrderStatus.PAY_NO));
	//	pmap.put(""+OrderStatus.PAY_YES, OrderStatus.getPayStatusText(OrderStatus.PAY_YES));
		pmap.put(""+OrderStatus.PAY_CONFIRM, OrderStatus.getPayStatusText(OrderStatus.PAY_CONFIRM));
		pmap.put(""+OrderStatus.PAY_CANCEL, OrderStatus.getPayStatusText(OrderStatus.PAY_CANCEL));
		pmap.put(""+OrderStatus.PAY_PARTIAL_PAYED, OrderStatus.getPayStatusText(OrderStatus.PAY_PARTIAL_PAYED));

		return pmap;
	}
	@Override
	public Map getShipJson() {
		Map map = new HashMap();
		map.put(""+OrderStatus.SHIP_ALLOCATION_NO, OrderStatus.getShipStatusText(OrderStatus.SHIP_ALLOCATION_NO));
		map.put(""+OrderStatus.SHIP_ALLOCATION_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_ALLOCATION_YES));
		map.put(""+OrderStatus.SHIP_NO, OrderStatus.getShipStatusText(OrderStatus.SHIP_NO));
		map.put(""+OrderStatus.SHIP_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_YES));
		map.put(""+OrderStatus.SHIP_CANCEL, OrderStatus.getShipStatusText(OrderStatus.SHIP_CANCEL));
		map.put(""+OrderStatus.SHIP_PARTIAL_SHIPED, OrderStatus.getShipStatusText(OrderStatus.SHIP_PARTIAL_SHIPED));
		map.put(""+OrderStatus.SHIP_YES, OrderStatus.getShipStatusText(OrderStatus.SHIP_YES));
		map.put(""+OrderStatus.SHIP_CANCEL, OrderStatus.getShipStatusText(OrderStatus.SHIP_CANCEL));
		map.put(""+OrderStatus.SHIP_CHANED, OrderStatus.getShipStatusText(OrderStatus.SHIP_CHANED));
		map.put(""+OrderStatus.SHIP_ROG, OrderStatus.getShipStatusText(OrderStatus.SHIP_ROG));
		return map;
	}
	
	//set  get 
	
	public Integer orderStatusNum(Integer status) {
		StoreMember member = storeMemberManager.getStoreMember();
		if(status==99){
			String sql = "select count(0) from es_order where member_id=? and parent_id is not null";
			return this.baseDaoSupport.queryForInt(sql,member.getMember_id());
		}else{
			String sql = "select count(0) from es_order where status =? and member_id=? and parent_id is not null";
			return this.baseDaoSupport.queryForInt(sql, status,member.getMember_id());
		}
		
	}
	
	@Override
	public Integer getStoreGoodsNum(int store_id) {
		String sql = "select count(0) from goods where store_id=?";
		return this.baseDaoSupport.queryForInt(sql, store_id);
	}
	
	
	@Override
	public void saveShipNo(Integer[] order_id, Integer logi_id,String logi_name, String shipNo) {
		Map map=new HashMap();
		map.put("ship_no", shipNo);
		map.put("logi_id", logi_id);
		map.put("logi_name", logi_name);
		
		this.daoSupport.update("es_order", map, "order_id="+order_id[0]);
	}
	@Override
	public Map censusState() {
		// 构造一个返回值Map，并将其初始化：各种订单状态的值皆为0
		Map<String, Integer> stateMap = new HashMap<String, Integer>(7);
		String[] states = { "cancel_ship", "cancel_pay",  "pay","ship", "complete", "allocation_yes" };
		for (String s : states) {
			stateMap.put(s, 0);
		}

		// 分组查询、统计订单状态
		String sql = "select count(0) num,status  from es_order where disabled = 0 AND parent_id is NOT NULL group by status";
		List<Map<String, Integer>> list = this.daoSupport.queryForList(sql,
				new RowMapper() {
					public Object mapRow(ResultSet rs, int arg1) throws SQLException {
						Map<String, Integer> map = new HashMap<String, Integer>();
						map.put("status", rs.getInt("status"));
						map.put("num", rs.getInt("num"));
						return map;
					}
				});
//
//				// 将list转为map
		for (Map<String, Integer> state : list) {
			stateMap.put(this.getStateString(state.get("status")),state.get("num"));
		}
		
		sql = "select count(0) num  from es_order where disabled = 0  and status=0 AND parent_id is NOT NULL ";
		int count=this.daoSupport.queryForInt(sql);
		stateMap.put("wait", 0);
		
		sql = "select count(0) num  from es_order where disabled = 0  AND parent_id is NOT NULL ";
		sql+=" and ( ( payment_type!='cod' and  status="+OrderStatus.ORDER_NOT_PAY +") ";//非货到付款的，未付款状态的可以结算
		sql+=" or (  status!="+OrderStatus.ORDER_NOT_PAY+"  and  pay_status!="+OrderStatus.PAY_CONFIRM +")" ; 
		sql+=" or ( payment_type='cod' and  (status="+OrderStatus.ORDER_SERVECE/*ORDER_SHIP*/ +" or status="+OrderStatus.ORDER_APPRAISE/*ORDER_ROG*/+" )  ) )";//货到付款的要发货或收货后才能结算
		count=this.daoSupport.queryForInt(sql);
		stateMap.put("not_pay", count);
		
		sql="select count(0) from es_order where disabled=0  and ( ( payment_type!='cod' and payment_id!=8  and  status=2)  or ( payment_type='cod' and  status=0)) AND parent_id is NOT NULL ";
		count=this.daoSupport.queryForInt(sql);
		stateMap.put("allocation_yes", count);
		
		return stateMap;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.order.IStoreOrderManager#getChildOrder(int)
	 */
	public List<StoreOrder> getChildOrder(int parentOrderId) {
		String sql = "SELECT * FROM es_order WHERE parent_id = ?";
		
		return this.daoSupport.queryForList(sql, StoreOrder.class, parentOrderId);
	}
	
	@Override
	public JSONObject getOrderDetailJson(Order order, String type, String carplate, String repair4sstoreid) {
		JSONObject returnObj = new JSONObject();
		List<StoreOrder> childOrderList = getChildOrder(order.getOrder_id());
		Order childorder = childOrderList.get(0);
		JSONObject items_json = JSONArray.fromObject(childorder.getItems_json()).getJSONObject(0);
		if("1".equals(type)){//保险订单详情
			//"sn": "","price": 0,"securitygain": "","insureimage": 0,"companyname": 0,"insurecontent": "","telephone": "","coupon": 335,"mkprice": 1
			String sql = "select insureestimatedfee mkprice, totalgain, insuresetid from es_carinfo where carplate=?";
			List list = daoSupport.queryForList(sql, carplate);
			JSONObject carinfoJson = JSONObject.fromObject(list.get(0));
			sql = "select * from es_insurances where id in("+ carinfoJson.getString("insuresetid") +")";
			list = daoSupport.queryForList(sql);
			net.sf.json.JSONArray insureArray = JSONArray.fromObject(list);
			String insureContent = "";
			String insureCompany = "";
			String insureCompanyImage = "";
			String telephone = "";
			for(int j=0; j<insureArray.size(); j++){
				insureContent += insureArray.getJSONObject(j).getString("insurance") + ",";
				insureCompany = insureArray.getJSONObject(j).getString("company");
				telephone = insureArray.getJSONObject(j).getString("telephone");
				insureCompanyImage = insureArray.getJSONObject(j).getString("companyimage");
			}
			insureContent = insureContent.substring(0, insureContent.lastIndexOf(","));
			returnObj.put("orderSn", childorder.getSn());
			returnObj.put("orderStatus", childorder.getStatus());
			returnObj.put("totalgain", carinfoJson.get("totalgain"));
			items_json.put("image", UploadUtil.replacePath(insureCompanyImage));
			items_json.put("price", carinfoJson.get("mkprice"));
			items_json.put("companyname", insureCompany);
			items_json.put("insurecontent", insureContent);
			items_json.put("telephone", telephone);
			items_json.put("coupon", 0);
			
			//修改order_item下的保险订单的image
			sql = "update es_order_items set image=? where item_id=?";
			daoSupport.execute(sql, insureCompanyImage, items_json.get("item_id"));
			sql = "select * from es_order_items where item_id=?";
			List itemlist = daoSupport.queryForList(sql, items_json.get("item_id"));
			String itemsJson = JSONArray.fromObject(itemlist).toString();
			sql = "update es_order set items_json=? where order_id=?";
			daoSupport.execute(sql, itemsJson, items_json.get("order_id"));
			
			returnObj.put("items_json", items_json);
			return returnObj;
		}
		if("2".equals(type)){//保养订单详情
			String sql = "SELECT c1.repair4sstoreid, c1.repairlastmile, c1.carmodelid, c1.totalgain," 
					   + " c2.repairinterval"
					   + " FROM es_carinfo c1, es_carmodels c2 WHERE c1.carmodelid=c2.id and c1.carplate=?";
			List list = daoSupport.queryForList(sql, carplate);
			JSONObject obj = JSONObject.fromObject(list.get(0));
			String carmodelid = obj.getString("carmodelid");//车型ID
			long repairlastmile = obj.getInt("repairlastmile");//上次保养里程
			long repairinterval = obj.getInt("repairinterval");//保养间隔里程
			double totalgain = obj.getDouble("totalgain");//可用保养收益
			int signStoreId = obj.getInt("repair4sstoreid");//签约的4s店id
			int selectedStoreId = Integer.valueOf(repair4sstoreid);//选择保养的4s店id
			//计算下次保养里程
			long nextmile = ((repairlastmile % repairinterval) > (repairinterval / 2)) ? ((repairlastmile/repairinterval) + 2)*repairinterval : ((repairlastmile/repairinterval) + 1)*repairinterval;
			
			sql = "select price from es_repairintervalitem where carmodelid=? and repairinterval=?";
			list = daoSupport.queryForList(sql, carmodelid, nextmile);
			obj = JSONObject.fromObject(list.get(0));
			long mkprice = obj.getLong("price");//获取下次保养价格
			
			sql = "select * from es_store where store_id=?";//根据选择保养的4s店信息
			list = daoSupport.queryForList(sql, selectedStoreId);
			JSONObject storeobj = JSONObject.fromObject(list.get(0));
			
			int brand_id = storeobj.getInt("brand_id");
			sql = "select name from es_brand where brand_id=?";
			list = daoSupport.queryForList(sql, brand_id);
			String brand = JSONObject.fromObject(list.get(0)).getString("name");
			String address = storeobj.getString("attr");
			String telephone = storeobj.getString("after_phone");
			int insure_repair_specid = items_json.getInt("insure_repair_specid");
			sql = "SELECT a.order_time, b.time_region_id, b.time_region FROM es_insure_repair_spec a, es_repair_timeregion b WHERE a.`time_region_id`=b.`time_region_id` AND a.`spec_id`=?";
			JSONObject regionObj = JSONObject.fromObject(daoSupport.queryForList(sql, insure_repair_specid).get(0));
			int time_region_id = regionObj.getInt("time_region_id");
			String time_region = regionObj.getString("time_region");
			String order_time = DateUtil.toString(regionObj.getLong("order_time"), "yyyy-MM-dd");
			
			// sn price brand+nextmile name address telephone mkprice coupon repairtotalgain  
			returnObj.put("orderSn", childorder.getSn());
			returnObj.put("storeAttr", address);
			returnObj.put("storeName", childorder.getStore_name());
			returnObj.put("storeTel", telephone);
			returnObj.put("orderStatus", childorder.getStatus());
			returnObj.put("totalgain", totalgain);//可用安全奖励
			items_json.put("image", UploadUtil.replacePath(storeobj.getString("store_logo")));
			items_json.put("brand", brand);
			items_json.put("name", brand+nextmile+"公里保养服务");
			items_json.put("nextmile", nextmile);//保养服务里程
			items_json.put("order_time", order_time);
			items_json.put("time_region_id", time_region_id);
			items_json.put("time_region", time_region);
			
			//修改order_item下的保养订单的image
			sql = "update es_order_items set image=? where item_id=?";
			daoSupport.execute(sql, storeobj.getString("store_logo"), items_json.get("item_id"));
			sql = "select * from es_order_items where item_id=?";
			List itemlist = daoSupport.queryForList(sql, items_json.get("item_id"));
			String itemsJson = JSONArray.fromObject(itemlist).toString();
			sql = "update es_order set items_json=? where order_id=?";
			daoSupport.execute(sql, itemsJson, items_json.get("order_id"));

			returnObj.put("items_json", items_json);
			return returnObj;
		}
		return null;
	}
	
	@Override
	public JSONObject getInsureAndRepairOrderDetail(Integer store_id, Order order, String order_type, String carplate) {
		JSONObject returnObj = new JSONObject();
		List<StoreOrder> childOrderList = getChildOrder(order.getOrder_id());
		Order childorder = childOrderList.get(0);
		JSONObject items_json = JSONArray.fromObject(childorder.getItems_json()).getJSONObject(0);
		int insure_repair_specid = items_json.getInt("insure_repair_specid");
		
		//车辆信息
		String sql = "SELECT t1.carmodelid, t1.`repair4sstoreid`, t1.`totalgain` FROM es_carinfo t1, es_carmodels t2 WHERE t1.`carmodelid`=t2.id AND t1.carplate=?";
		List carinfoList = daoSupport.queryForList(sql, carplate);
		JSONObject carObj = JSONObject.fromObject(carinfoList.get(0));
		int repair4sstoreid = carObj.getInt("repair4sstoreid");
		int carmodelid = carObj.getInt("carmodelid");
		
		//店铺信息
		sql = "select store_name, REPLACE(store_logo,?,?) store_logo, attr, customer_phone phone, discountcontract, discountnoncontract from es_store where store_id=?";
		List list = daoSupport.queryForList(sql, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_domain(), store_id);
		JSONObject storeInfoObj = JSONObject.fromObject(list.get(0));
		
		//保险订单详情
		if(order_type.equals(OrderType.ZA_INSURANCE+"") || order_type.equals(OrderType.STORE_INSURANCE+"")){
			//更新订单order_id到保险spec表
			sql = "UPDATE es_insure_order_spec SET order_id=?, create_time=? WHERE spec_id=?";
			daoSupport.execute(sql, childorder.getOrder_id(), childorder.getCreate_time()*1000, insure_repair_specid);
			
			sql = "SELECT t1.select_insure_typeids, t1.insure_params, t1.price, t2.company_name, REPLACE(t2.logo,?,?) logo, t2.telephone, t1.repair_coin FROM es_insure_order_spec t1, es_insurance_company_info t2 WHERE t1.`insure_company_id`=t2.`id` AND t1.spec_id=?";
			List insureList = daoSupport.queryForList(sql, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_domain(), insure_repair_specid);
			JSONObject insure_info_Obj = JSONObject.fromObject(insureList.get(0));
			String select_insure_typeids = insure_info_Obj.getString("select_insure_typeids");
			Map<Integer, Object> insure_params_valueMap = InsureType.getInsureParamValueMap(JSONArray.fromObject(insure_info_Obj.getString("insure_params")));
			String[] typeArray = select_insure_typeids.split(",");
			String insure_content = "";
			for(int i=0; i<typeArray.length; i++){
				int key = Integer.valueOf(typeArray[i]);
				insure_content += InsureType.getInsureTypeMap().get(key) + (insure_params_valueMap.get(key) == null || "".equals(insure_params_valueMap.get(key)) ? "" : "("+insure_params_valueMap.get(key)+")") + ",";
			}
			
			JSONObject insureObj = new JSONObject();
			insureObj.put("insure_content", insure_content.subSequence(0, insure_content.length() - 1));
			insureObj.put("price", insure_info_Obj.getString("price"));
			insureObj.put("company_name", insure_info_Obj.getString("company_name"));
			insureObj.put("logo", insure_info_Obj.getString("logo"));
			insureObj.put("telephone", insure_info_Obj.getString("telephone"));
			insureObj.put("repair_coin", insure_info_Obj.get("repair_coin"));
			
			//返回订单详情
			returnObj.put("order_id", childorder.getOrder_id());
			returnObj.put("sn", childorder.getSn());
			returnObj.put("need_pay_money", childorder.getNeed_pay_money());
			returnObj.put("totalgain", carObj.getDouble("totalgain"));
			returnObj.put("rewards_limit", items_json.getDouble("rewards_limit"));
			returnObj.put("goods_num", childorder.getGoods_num());
			returnObj.put("store_name", storeInfoObj.getString("store_name"));
			returnObj.put("service_store_tel", storeInfoObj.getString("phone"));
			returnObj.put("insureObj", insureObj);

			//修改order_item下的保险订单的image
			sql = "update es_order_items set image=? where item_id=?";
			daoSupport.execute(sql, insure_info_Obj.getString("logo"), items_json.get("item_id"));
			sql = "select * from es_order_items where item_id=?";
			List itemlist = daoSupport.queryForList(sql, items_json.get("item_id"));
			sql = "update es_order set items_json=? where order_id=?";
			daoSupport.execute(sql, JSONArray.fromObject(itemlist).toString(), childorder.getOrder_id());
			return returnObj;
		}
		
		//保养订单详情
		if(order_type.equals(OrderType.REPAIR+"")){
			//更新订单order_id到保养spec表
			sql = "UPDATE es_repair_order_spec SET order_id=?, create_time=? WHERE spec_id=?";
			daoSupport.execute(sql, childorder.getOrder_id(), childorder.getCreate_time()*1000, insure_repair_specid);
			
			JSONObject storeObj = new JSONObject();
			storeObj.put("store_name", storeInfoObj.getString("store_name"));
			storeObj.put("attr", storeInfoObj.getString("attr"));
			storeObj.put("phone", storeInfoObj.getString("phone"));
			
			//保养详细信息
			double discountcontract = storeInfoObj.getDouble("discountcontract");
			double discountnoncontract = storeInfoObj.getDouble("discountnoncontract");
			double discount = 0.0;
			
			if(repair4sstoreid == Integer.valueOf(store_id)){//签约折扣
				discount = discountcontract;
			}else{//非签约折扣
				discount = discountnoncontract;
			}
			
			//获取订单日期、时间段、费率、保养项目内容id集合
			sql = "SELECT t1.repair_mile, FROM_UNIXTIME(t1.order_date/1000, '%Y-%m-%d') order_date, t2.`starttime`, t2.`endtime`, t1.repair_item_ids, t2.ratio "
			    + "FROM es_repair_order_spec t1, es_repair_timeregion t2 WHERE t1.time_region_id=t2.time_region_id AND t1.spec_id=?";
			JSONObject repairInfoObj = JSONObject.fromObject(daoSupport.queryForList(sql, insure_repair_specid).get(0));
			String repair_item_ids = repairInfoObj.getString("repair_item_ids");
			double ratio = repairInfoObj.getDouble("ratio");
			
			//获取保养项目内容（项目名称、价格）
			sql = "SELECT t2.itemname, t1.item_price * "+ discount +" item_price "
				+ "FROM es_store_repairitem t1, es_repair_items t2 WHERE t1.repair_item_id=t2.id "
				+ "AND store_id=? AND carmodel_id=? AND t1.repair_item_id IN "+ "("+ repair_item_ids +")";
			List repairitemList = daoSupport.queryForList(sql, store_id, carmodelid);
			
			//获取工时费合计
			sql = "SELECT SUM(t1.repair_price * "+ discount +"* "+ ratio +") servicetime_total_price "
				+ "FROM es_store_repairitem t1, es_repair_items t2 WHERE t1.repair_item_id=t2.id "
				+ "AND store_id=? AND carmodel_id=? AND t1.repair_item_id IN "+ "("+ repair_item_ids +")";
			List repair_price_list = daoSupport.queryForList(sql, store_id, carmodelid);
			
			JSONObject repairObj = new JSONObject();
			repairObj.put("order_date", repairInfoObj.getString("order_date"));
			repairObj.put("repair_mile", repairInfoObj.getString("repair_mile"));
			repairObj.put("starttime", repairInfoObj.getString("starttime"));
			repairObj.put("endtime", repairInfoObj.getString("endtime"));
			repairObj.put("repairitemList", JSONArray.fromObject(repairitemList));
			repairObj.put("servicetime_total_price", JSONObject.fromObject(repair_price_list.get(0)).getDouble("servicetime_total_price"));
			
			//查询车辆在下单店铺可使用的保养币
			sql = "SELECT repair_total_coin FROM es_car_repair_coin WHERE store_id=? AND carplate=?";
			List coinList = daoSupport.queryForList(sql, store_id, carplate);
			long repair_total_coin = 0;
			if(coinList.size() > 0){
				repair_total_coin = JSONObject.fromObject(coinList.get(0)).getLong("repair_total_coin");
			}
			
			//返回订单详情
			returnObj.put("order_id", childorder.getOrder_id());
			returnObj.put("sn", childorder.getSn());
			returnObj.put("need_pay_money", childorder.getNeed_pay_money());
			returnObj.put("totalgain", carObj.getDouble("totalgain"));
			returnObj.put("rewards_limit", items_json.getDouble("rewards_limit"));
			returnObj.put("repair_total_coin", repair_total_coin);
			returnObj.put("repair_coin_amount", repair_total_coin / 1);//保养币抵用金额，目前是1:1
			returnObj.put("goods_num", childorder.getGoods_num());
			returnObj.put("storeObj", storeObj);
			returnObj.put("repairObj", repairObj);

			//修改order_item下的保险订单的image
			sql = "update es_order_items set image=? where item_id=?";
			daoSupport.execute(sql, storeInfoObj.getString("store_logo"), items_json.get("item_id"));
			sql = "select * from es_order_items where item_id=?";
			List itemlist = daoSupport.queryForList(sql, items_json.get("item_id"));
			sql = "update es_order set items_json=? where order_id=?";
			daoSupport.execute(sql, JSONArray.fromObject(itemlist).toString(), childorder.getOrder_id());
			
			return returnObj;
		}
		
		return null;
	}


	/**
	 * 根据订单状态值获取状态字串，如果状态值不在范围内反回null。
	 * 
	 * @param state
	 * @return
	 */
	private String getStateString(Integer state) {
		String str = null;
		switch (state.intValue()) {
		case -2:
			str = "cancel_ship";
			break;
		case -1:
			str = "cancel_pay";
			break;
		case 1:
			str = "pay";
			break;
		case 2:
			str = "ship";
			break;
		case 4:
			str = "allocation_yes";
			break;
		case 7:
			str = "complete";
			break;
		default:
			str = null;
			break;
		}
		return str;
	}
	
	

	@Override
	public void stockUpComplete(String orderId) {
		Order order = orderManager.get(Integer.parseInt(orderId));
		if(order==null || (order.getStatus() != OrderStatus.ORDER_PAY_CONFIRM && order.getStatus() != OrderStatus.ORDER_SHIP)){throw new RuntimeException("订单不存在或订单状态错误");}
	    if((order.getOrder_type() == OrderType.ZA_INSURANCE || order.getOrder_type() == OrderType.STORE_INSURANCE) && order.getShipping_method() == 2){
	    	this.daoSupport.execute("update es_order o set o.status = "+OrderStatus.ORDER_APPRAISE+" where o.order_id = ? ", orderId);
	    }else{
	    	this.daoSupport.execute("update es_order o set o.status = "+OrderStatus.ORDER_SERVECE+" where o.order_id = ? ", orderId);
	    }
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void confirmService(String orderId) {
		try{
			Order order = orderManager.get(Integer.parseInt(orderId));
			Long time = DateUtil.getDateline();
			if(order==null || order.getStatus() != OrderStatus.ORDER_SERVECE){throw new RuntimeException("订单不存在或订单状态错误");}
			this.daoSupport.execute("update es_order  o set o.status = "+OrderStatus.ORDER_APPRAISE+",o.service_time = ? where o.order_id = ? ",time, orderId);
		}catch(Exception e){
			throw e;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void finishOrder(String orderId) {
		try{
			Order order = orderManager.get(Integer.parseInt(orderId));
			Long time = DateUtil.getDateline();
			order.setComplete_time(time);
			this.daoSupport.execute("update es_order  o set o.status = "+OrderStatus.ORDER_COMPLETE+",o.complete_time= ? where o.order_id = ? ",time, orderId);
			//调用生成对账单插件
			orderPluginBundle.confirmService(order);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public RepairOrderDetail saveRepairOrderDetailInfo(RepairOrderDetail repairDetail) {
		try {
			String sql = "select IFNULL(order_id,0) order_id, time_region_id, repair_item_ids, price from es_repair_order_spec where spec_id=?";
			List list = daoSupport.queryForList(sql, repairDetail.getSpec_id());
			JSONObject specObj = JSONObject.fromObject(list.get(0));
			int order_id = specObj.getInt("order_id");
			int time_region_id = specObj.getInt("time_region_id");
			String item_ids = specObj.getString("repair_item_ids");
		
			StringBuffer updatesql = new StringBuffer();
			updatesql.append("update es_repair_order_spec set ");
			
			
			/**
			//如果保养项目有更新，则更新订单保养内容、价格和可用收益额度
			if(!"".equals(repairDetail.getRepair_item_ids()) && !repairDetail.getRepair_item_ids().equals(item_ids)){
				
				//重新计算订单保养价格和可使用收益额度
				Order order = get(Integer.valueOf(repairDetail.getOrder_id()));
				
				sql = "SELECT t1.`carmodelid`, t1.`repair4sstoreid`, t2.`discountcontract`, t2.`discountnoncontract` FROM es_carinfo t1, es_store t2 WHERE t1.repair4sstoreid = t2.store_id AND t1.carplate=?";
				list = daoSupport.queryForList(sql, order.getCarplate());
				JSONObject storeObj = JSONObject.fromObject(list.get(0));
				int repair4sstoreid = storeObj.getInt("repair4sstoreid");
				int carmodelid = storeObj.getInt("carmodelid");
				double discountcontract = storeObj.getDouble("discountcontract");
				double discountnoncontract = storeObj.getDouble("discountnoncontract");
				
				double discount = 0.0;
				if(repair4sstoreid == Integer.valueOf(order.getService_store_id())){//签约折扣
					discount = discountcontract;
				}else{//非签约折扣
					discount = discountnoncontract;
				}

				sql = "SELECT SUM(item_price+repair_price)*"+ discount +" price FROM es_store_repairitem WHERE carmodel_id="+ carmodelid +" AND store_id="+ order.getService_store_id() +" AND repair_item_id IN ("+ repairDetail.getRepair_item_ids() +")";
				double price = StringUtil.isEmpty(daoSupport.queryForString(sql)) ? 0 : Double.valueOf(daoSupport.queryForString(sql));//计算折扣后的价格 price * 签约/非签约折扣
				double rewards_limit = price * (1 - discount);//计算可用收益额度
				sql = "SELECT "+ price +" * t.ratio price FROM (SELECT * FROM es_repair_timeregion WHERE time_region_id="+ time_region_id +") t";
				price = StringUtil.isEmpty(daoSupport.queryForString(sql)) ? 0 : Double.valueOf(daoSupport.queryForString(sql));//计算时间段折扣之后的价格 price * 时间段折扣费率
				
				DecimalFormat df = new DecimalFormat("0.00");
				
				//更新订单保养内容、价格、可用收益额度
				updatesql.append("repair_item_ids='"+repairDetail.getRepair_item_ids()+"', price="+df.format(price)+", rewards_limit="+df.format(rewards_limit)+", ");
			}
			**/
			
			updatesql.append("repair_mile=?, repair_price=?, repair_source=?, service_timelength=?, ");
			updatesql.append("engineer=?, repair_time=?, repair_remarks=?, update_time=? ");
			updatesql.append("where spec_id=?");

			daoSupport.execute(updatesql.toString(), repairDetail.getRepair_mile(), repairDetail.getRepair_price(), repairDetail.getRepair_source(), 
					           repairDetail.getService_timelength(), repairDetail.getEngineer(), repairDetail.getRepair_time(), repairDetail.getRepair_remarks(), repairDetail.getUpdate_time(), 
					           repairDetail.getSpec_id());
			
			//更新保养订单状态为服务中状态
			this.daoSupport.execute("update es_order o set o.status = "+OrderStatus.ORDER_SERVECE+" where o.order_id = ? ", repairDetail.getOrder_id());
			
			return repairDetail;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public JSONObject getInsureAndRepairOrderHistory(String carplate, String order_type) {
		JSONObject obj = new JSONObject();
		try {
			//查询保险订单历史记录
			if(order_type.equals(OrderType.ZA_INSURANCE+"") || order_type.equals(OrderType.STORE_INSURANCE+"")){
				String sql = "SELECT t1.select_insure_typeids, t1.insure_params, t1.price, FROM_UNIXTIME(t1.insure_starttime/1000, '%Y-%m-%d') insure_starttime, FROM_UNIXTIME(t1.insure_endtime/1000, '%Y-%m-%d') insure_endtime, t1.policy_no, t1.repair_coin, t2.company_name, t3.`paymoney` need_pay_money "
						   + "FROM es_insure_order_spec t1, es_insurance_company_info t2, es_order t3 WHERE t1.`insure_company_id`=t2.`id` AND t1.`order_id`=t3.`order_id` AND t1.carplate=? AND t3.status=7";
				List insureList = daoSupport.queryForList(sql, carplate);
				if(insureList.size() > 0){
					JSONArray historyArray = new JSONArray();
					for(int i=0; i<insureList.size(); i++){
						JSONObject historyObj = new JSONObject();
						
						JSONObject insure_info_Obj = JSONObject.fromObject(insureList.get(i));
						String select_insure_typeids = insure_info_Obj.getString("select_insure_typeids");
						Map<Integer, Object> insure_params_valueMap = InsureType.getInsureParamValueMap(JSONArray.fromObject(insure_info_Obj.getString("insure_params")));
						String[] typeArray = select_insure_typeids.split(",");
						List<String> insureContentList = new ArrayList<>();
						for(int j=0; j<typeArray.length; j++){
							int key = Integer.valueOf(typeArray[j]);
							insureContentList.add(InsureType.getInsureTypeMap().get(key) + (insure_params_valueMap.get(key) == null || "".equals(insure_params_valueMap.get(key)) ? "" : "("+insure_params_valueMap.get(key)+")"));
						}
						
						historyObj.put("company_name", insure_info_Obj.getString("company_name"));
						historyObj.put("policy_no", insure_info_Obj.getString("policy_no"));
						historyObj.put("insure_price", insure_info_Obj.getString("price"));
						historyObj.put("need_pay_money", insure_info_Obj.getString("need_pay_money"));
						historyObj.put("repair_coin", insure_info_Obj.getString("repair_coin"));
						historyObj.put("insure_starttime", insure_info_Obj.getString("insure_starttime"));
						historyObj.put("insure_endtime", insure_info_Obj.getString("insure_endtime"));
						historyObj.put("insure_content_list", insureContentList);
						
						historyArray.add(historyObj);
					}

					obj.put("data", historyArray.toString());
				}
			}
			
			//查询保养订单历史记录
			if(order_type.equals(OrderType.REPAIR+"")){
				String sql = "select t1.repair_mile, t1.repair_item_ids, t1.repair_time, t1.engineer, t1.repair_price, t2.store_name, t3.sn, t3.paymoney need_pay_money, t3.repair_coin "
						   + "FROM es_repair_order_spec t1, es_store t2, es_order t3 WHERE t1.store_id=t2.store_id AND t1.order_id=t3.order_id "
						   + "AND t1.carplate=? AND t3.status=7";
				List list = daoSupport.queryForList(sql, carplate);
				if(list.size() > 0){
					JSONArray historyArray = JSONArray.fromObject(list);
					for(int i=0; i<historyArray.size(); i++){
						JSONObject historyObj = historyArray.getJSONObject(i);
						String repair_item_ids = historyObj.getString("repair_item_ids");
						String itemSql = "select itemname from es_repair_items where id in ("+ repair_item_ids +")";
						List<String> itemList = daoSupport.queryForList(itemSql);
						historyObj.put("items", itemList);
					}
					obj.put("data", historyArray.toString());
				}
			}
			
			obj.put("result", 1);
			obj.put("message", "查询成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}


	@Override
	public JSONObject getCarInsureRecords(String car_id) {
		JSONObject obj = new JSONObject();
		try {
			//车辆信息
			StringBuffer sqlBuild = new StringBuffer();
			sqlBuild.append("SELECT t1.`carowner`, UPPER(t1.`carplate`) carplate, FROM_UNIXTIME(t1.car_register_time/1000, '%Y-%m-%d') car_register_time, t2.`fullname`, t3.brand, t3.`series`, t3.`sales_name` ");
			sqlBuild.append("FROM es_carinfo t1, es_member t2, es_carmodels t3 ");
			sqlBuild.append("WHERE t1.`carowner`=t2.`username` AND t1.`carmodelid`=t3.`id` ");
			sqlBuild.append("AND t1.id=?");

			List list = daoSupport.queryForList(sqlBuild.toString(), car_id);
			JSONObject carInfo = JSONObject.fromObject(list.get(0));
			obj.put("carinfo", carInfo);
			
			//保险历史记录
			String carplate = carInfo.getString("carplate");
			StringBuffer historySql = new StringBuffer();
			historySql.append("SELECT t3.company_name, t1.*, FROM_UNIXTIME(t1.`create_time`/1000, '%Y-%m-%d') order_create_time, FROM_UNIXTIME(t1.`insure_starttime`/1000, '%Y-%m-%d') start_time, FROM_UNIXTIME(t1.`insure_endtime`/1000, '%Y-%m-%d') end_time ");
			historySql.append("FROM es_insure_order_spec t1, es_order t2, es_insurance_company_info t3 WHERE t1.order_id=t2.order_id AND t1.insure_company_id=t3.id AND t1.carplate=?");
			historySql.append("ORDER BY t1.create_time DESC");
			
			List insureRecordsList = daoSupport.queryForList(historySql.toString(), carplate);
			JSONArray insureRecords = new JSONArray();
			if(insureRecordsList.size() > 0){
				insureRecords = JSONArray.fromObject(insureRecordsList);
				for(int i=0; i<insureRecords.size(); i++){
					JSONObject insureRecord = insureRecords.getJSONObject(i);
					String select_insure_typeids = insureRecord.getString("select_insure_typeids");
					String insure_params = insureRecord.getString("insure_params");
					Map<Integer, Object> insure_params_valueMap = InsureType.getInsureParamValueMap(JSONArray.fromObject(insure_params));
					String[] typeArray = select_insure_typeids.split(",");
					List insureContentList = new ArrayList<>();
					for(int j=0; j<typeArray.length; j++){
						int key = Integer.valueOf(typeArray[j]);
						insureContentList.add(InsureType.getInsureTypeMap().get(key) + (insure_params_valueMap.get(key) == null || "".equals(insure_params_valueMap.get(key)) ? "" : "("+insure_params_valueMap.get(key)+")"));
					}
					
					insureRecord.put("insure_content", insureContentList);
				}
			}
			obj.put("insureRecords", insureRecords);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public JSONObject getCarRepairRecords(String car_id) {
		JSONObject obj = new JSONObject();
		try {
			//车辆信息
			StringBuffer sqlBuild = new StringBuffer();
			sqlBuild.append("SELECT t1.`carowner`, UPPER(t1.`carplate`) carplate, t1.repairnexttime, FROM_UNIXTIME(t1.repairnexttime/1000, '%Y-%m-%d') repair_nexttime, FROM_UNIXTIME(t1.car_register_time/1000, '%Y-%m-%d') car_register_time, t2.`fullname`, t3.brand, t3.`series`, t3.`sales_name` ");
			sqlBuild.append("FROM es_carinfo t1, es_member t2, es_carmodels t3 ");
			sqlBuild.append("WHERE t1.`carowner`=t2.`username` AND t1.`carmodelid`=t3.`id` ");
			sqlBuild.append("AND t1.id=?");

			List list = daoSupport.queryForList(sqlBuild.toString(), car_id);
			JSONObject carInfo = JSONObject.fromObject(list.get(0));
			long repairnexttime = carInfo.getLong("repairnexttime");
			String repair_nexttime = carInfo.getString("repair_nexttime");
			obj.put("carinfo", carInfo);
			
			//保养历史记录
			String carplate = carInfo.getString("carplate");
			StringBuffer historySql = new StringBuffer();
			historySql.append("SELECT t1.*, FROM_UNIXTIME(t1.`create_time`/1000, '%Y-%m-%d') order_create_time, FROM_UNIXTIME(t1.`repair_time`/1000, '%Y-%m-%d') service_time ");
			historySql.append("FROM es_repair_order_spec t1, es_order t2 WHERE t1.order_id=t2.order_id AND t1.carplate=? AND t2.status=? ");
			historySql.append("ORDER BY t1.create_time DESC");

			List repairRecords = daoSupport.queryForList(historySql.toString(), carplate, OrderStatus.ORDER_COMPLETE);
			if(repairRecords.size() > 0){
				JSONArray repairArray = JSONArray.fromObject(repairRecords);
				for(int i=0; i<repairArray.size(); i++){
					JSONObject repairObj = repairArray.getJSONObject(i);
					String repair_remarks = repairObj.getString("repair_remarks");
					repair_remarks = StringUtil.isNull(repair_remarks) ? "" : repair_remarks;
					repairObj.put("repair_remarks", repair_remarks);
					
					long repair_time = repairObj.getLong("repair_time");
					String items = repairObj.getString("repair_item_ids");
					String sql = "select itemname, is_necessary from es_repair_items where id in ("+ items +")";

					List itemList = daoSupport.queryForList(sql);
					if(itemList.size() > 0){
						List<String> baseList = new ArrayList<String>();
						List<String> extendList = new ArrayList<String>();
						JSONArray itemArray = JSONArray.fromObject(itemList);
						for(int j=0; j<itemArray.size(); j++){
							String itemname = itemArray.getJSONObject(j).getString("itemname");
							int is_necessary = itemArray.getJSONObject(j).getInt("is_necessary");
							if(is_necessary == 1){
								baseList.add(itemname);
							}else{
								extendList.add(itemname);
							}
						}
						repairObj.put("baseItems", baseList.toString().replace("[", "").replace("]", ""));
						repairObj.put("extendItems", extendList.toString().replace("[", "").replace("]", ""));
						
						repairObj.put("repairnexttime", repair_nexttime);
						//添加是否按约购买标记
						if(repairnexttime >= repair_time){
							repairObj.put("isOnTime", 1);
						}else{
							repairObj.put("isOnTime", 0);
						}
					}
				}
				obj.put("repairRecords", repairArray);
			}else{
				obj.put("repairRecords", new JSONArray());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}


	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveInsureOrderDetailInfo(Order order, String spec_id, String repair_coin, String policy_no) {
		try {
			String sql = "update es_insure_order_spec set update_time="+ new Date().getTime() +",";
			if(!StringUtil.isNull(repair_coin)){
				sql += " repair_coin=" + repair_coin + ",";
			}
			if(!StringUtil.isNull(policy_no)){
				sql += " policy_no='" + policy_no + "',";
			}
			
			sql = sql.lastIndexOf(",") == (sql.length() - 1) ? sql.substring(0, sql.length() - 1) : sql;

			sql += " where spec_id=" + spec_id;

			daoSupport.execute(sql);
			
			//保单号更新成功，记录保养币获取历史记录
			if(!StringUtil.isEmpty(policy_no)){
				sql = "SELECT t1.store_id, t1.repair_coin, t2.company_name FROM es_insure_order_spec t1, es_insurance_company_info t2 WHERE t1.insure_company_id=t2.id AND t1.spec_id=?";
				List specList = daoSupport.queryForList(sql, spec_id);
				if(specList.size() > 0){
					JSONObject specObj = JSONObject.fromObject(specList.get(0));
					long insure_repair_coin = specObj.getInt("repair_coin");
					if(insure_repair_coin > 0){
						sql = "INSERT INTO es_car_repair_coin_history SET TYPE=?, query_id=?, store_id=?, carplate=?, reward=?, timeline=?, detail=?";
						String detail = "订单号:"+ order.getSn() +", 保险单号："+ policy_no +", 保险公司：" + specObj.getString("company_name");
						daoSupport.execute(sql, CarInfo.INSURE_PAY_REPAIR_COIN_GET, order.getOrder_id(), specObj.getInt("store_id"), order.getCarplate(), specObj.getInt("repair_coin"), new Date().getTime(), detail);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
	}


	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}

	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}

	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}

	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}

	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}

	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}

	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}

	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}

	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}

	public IStoreCartManager getStoreCartManager() {
		return storeCartManager;
	}

	public void setStoreCartManager(IStoreCartManager storeCartManager) {
		this.storeCartManager = storeCartManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}


	public ICartManager getCartManager() {
		return cartManager;
	}


	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}
	
	
	public CartPluginBundle getCartPluginBundle() {
		return cartPluginBundle;
	}


	public void setCartPluginBundle(CartPluginBundle cartPluginBundle) {
		this.cartPluginBundle = cartPluginBundle;
	}


	public StoreCartPluginBundle getStoreCartPluginBundle() {
		return storeCartPluginBundle;
	}


	public void setStoreCartPluginBundle(StoreCartPluginBundle storeCartPluginBundle) {
		this.storeCartPluginBundle = storeCartPluginBundle;
	}


	public IStorePromotionManager getStorePromotionManager() {
		return storePromotionManager;
	}


	public void setStorePromotionManager(IStorePromotionManager storePromotionManager) {
		this.storePromotionManager = storePromotionManager;
	}


	@Transactional(propagation = Propagation.REQUIRED)
	public String paySuccess(StoreOrder order, String trade_no) {//0元支付流程
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		double payMoney= 0d;
		double orderGain = order.getGain(); //订单总优惠价格
		
		try {
			if (order.getParent_id() == null) {// 如果为主订单，走主订单流程
				this.mainOrderFlow(order);
 
			} else {// 走子订单流程
				this.childOrderFlow(order);
			}
			
			//统一更新订单状态
			this.storeOrderFlowManager.payConfirm(order.getOrder_id());
			
			//更改保险信息
			orderManager.updateInsureAndRepairInfo(order,carInfoManager);
			
			//更新用户车牌号对应奖励额度
			this.daoSupport.execute("update es_carinfo set totalgain = totalgain-? where carplate = ?", orderGain,order.getCarplate());
			
			return "{\"result\":1,\"data\":{orderInfo:\"\",\"isZeroOrder\":1}}";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		
	}
	
	/**
	 * 主订单确认付款流程
	 * @param order
	 */
	private void mainOrderFlow(StoreOrder order) {
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		Double orderGain = order.getGain(); //订单总优惠价格
		Integer isUseGain = order.getIsUseGain();//是否使用优惠
		List<StoreOrder> storeOrders = this.getChildOrder(order.getOrder_id());
		
		if (isUseGain == 1 && orderGain > 0) {
			for (int orderIndex = 0; orderIndex < storeOrders.size(); orderIndex++) {
				StoreOrder childOrder = storeOrders.get(orderIndex);
				// 更新每个orderItem的使用用余额
				double childGain = childOrder.getGain();
				JSONArray itemList = JSONArray.fromObject(childOrder.getItemList());
				if (itemList != null && itemList.size() > 0) {
					for (int i = 0; i < itemList.size(); i++) {
						JSONObject item = JSONObject.fromObject(itemList.get(i));
						Double rewards_limit = ((Double) item.get("rewards_limit") *  item.getInt("num"));
						if (rewards_limit <= childGain) {// 如果小于则扣去
							this.daoSupport.execute(
									"update es_order_items set used_rewards = ? where item_id = ?",
									rewards_limit, (Integer) item.get("item_id"));
							childGain = CurrencyUtil.sub(childGain, rewards_limit);
						} else {
							this.daoSupport.execute(
									"update es_order_items set used_rewards = ? where item_id = ?", childGain,
									(Integer) item.get("item_id"));
							childGain = CurrencyUtil.sub(childGain, childGain);
							break;
						}
					}
				}
			}
		}
		
		// 循环生成子订单明细,并修改优惠券为已支付
		for (int i = 0; i < storeOrders.size(); i++) {
			StoreOrder childOrder = storeOrders.get(i);
			int paymentid = orderReportManager.getPaymentLogId(storeOrders.get(i).getOrder_id());
			PaymentDetail paymentdetail = new PaymentDetail();
			paymentdetail.setAdmin_user("系统");
			paymentdetail.setPay_date(new Date().getTime());
			paymentdetail.setPay_money(CurrencyUtil.sub(childOrder.getNeed_pay_money(), childOrder.getGain()));// 如果有优惠券要减去优惠券
			paymentdetail.setPayment_id(paymentid);
			orderReportManager.addPayMentDetail(paymentdetail);
			double payMoney = 0d;
			this.daoSupport.execute("update es_order set paymoney=?,isUseGain=? where order_id=?",
					payMoney,isUseGain,
					childOrder.getOrder_id());
			this.daoSupport.execute("update es_payment_logs set paymoney=? where payment_id=?", 
					payMoney,
					paymentid);
			
			//修改优惠券为已使用
			this.daoSupport.execute("update es_member_bonus set used_time = ?,used = ? where order_id = ? and order_sn = ?",
					System.currentTimeMillis(),
					1,
					childOrder.getOrder_id(),
					childOrder.getSn());
			
			//生成结算单和流水号
			orderPluginBundle.onConfirmPay(childOrder,"");
			
		}
		
	}
	
	
	/**
	 * 子订单确认付款流程
	 * @param order
	 */
	private void childOrderFlow(StoreOrder order) {
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		Double payMoney= CurrencyUtil.round(StringUtil.toDouble(request.getParameter("total_fee")), 2); //在线支付的金额
		Double orderGain = CurrencyUtil.round(order.getGain(),2); //订单总优惠价格
		Integer isUseGain = order.getIsUseGain();//是否使用优惠
		
		if (isUseGain == 1 && orderGain > 0) {//计算used_reward
			JSONArray itemList = JSONArray.fromObject(order.getItemList());
			if (itemList != null && itemList.size() > 0) {
				for (int i = 0; i < itemList.size(); i++) {
					JSONObject item = JSONObject.fromObject(itemList.get(i));
					Double rewards_limit = CurrencyUtil.mul((Double)item.get("rewards_limit"), item.getInt("num"));
					if (rewards_limit <= orderGain) {// 如果小于则扣去
						this.daoSupport.execute(
								"update es_order_items set used_rewards = ? where item_id = ?",
								rewards_limit,
								(Integer) item.get("item_id"));
						orderGain = CurrencyUtil.sub(orderGain, rewards_limit) ;
					} else {
						this.daoSupport.execute(
								"update es_order_items set used_rewards = ? where item_id = ?",
								orderGain,
								(Integer) item.get("item_id"));
						orderGain = CurrencyUtil.sub(orderGain, orderGain);;
						break;
					}
				}
			}
		}
		
		//生成支付详情
		int paymentid = orderReportManager.getPaymentLogId(order.getOrder_id());
		PaymentDetail paymentdetail=new PaymentDetail();
		paymentdetail.setAdmin_user("系统");
		paymentdetail.setPay_date(new Date().getTime());
		paymentdetail.setPay_money(payMoney);
		paymentdetail.setPayment_id(paymentid);
		orderReportManager.addPayMentDetail(paymentdetail);
		
		
		//修改子订单log
		this.daoSupport.execute("update es_payment_logs set paymoney=? where payment_id=?",payMoney,paymentid);
		
		//更新子订单付款金额
		this.daoSupport.execute("update es_order set paymoney=?  where order_id=?",payMoney,order.getOrder_id());
		
		//修改优惠券为已使用
		this.daoSupport.execute("update es_member_bonus set used_time = ?,used = ? where order_id = ? and order_sn = ?", System.currentTimeMillis(),1,order.getOrder_id(),order.getSn());
		
		//生成流水号和结算单
		orderPluginBundle.onConfirmPay(order,"");
		
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateGain(Double useGain, Integer order_id) {
		this.daoSupport.execute("update es_order set gain =? ,isUseGain=1 where order_id = ? ", useGain,order_id);
	}
	
	@Override
	public JSONObject getInsureOrderDetail(String order_id) {
		JSONObject obj = new JSONObject();
		try {
			//查询保险订单信息
			StoreOrder order = get(Integer.valueOf(order_id));
			JSONObject order_item = JSONArray.fromObject(order.getItems_json()).getJSONObject(0);
			int spec_id = order_item.getInt("insure_repair_specid");
			String sql = "SELECT t1.carplate, t1.spec_id, FROM_UNIXTIME(t1.insure_starttime/1000, '%Y-%m-%d') insure_starttime, FROM_UNIXTIME(t1.insure_endtime/1000, '%Y-%m-%d') insure_endtime, t1.applicant, t1.applicant_id, t1.price, t1.select_insure_typeids, t1.insure_params, t1.policy_no, t1.repair_coin, "
					   + "t2.carvin, t2.carengineno, t3.company_name, t4.brand, t4.series, t4.nk, t4.discharge "
					   + "FROM es_insure_order_spec t1, es_carinfo t2, es_insurance_company_info t3, es_carmodels t4 "
					   + "WHERE t1.carplate=t2.carplate AND t1.insure_company_id=t3.id AND t2.carmodelid=t4.id AND t1.spec_id=?";
			List insureInfoList = daoSupport.queryForList(sql, spec_id);
			obj = JSONObject.fromObject(insureInfoList.get(0));
			String select_insure_typeids = obj.getString("select_insure_typeids");
			Map<Integer, Object> insure_params_valueMap = InsureType.getInsureParamValueMap(JSONArray.fromObject(obj.getString("insure_params")));
			String[] typeArray = select_insure_typeids.split(",");
			List insureContentList = new ArrayList<>();
			for(int i=0; i<typeArray.length; i++){
				int key = Integer.valueOf(typeArray[i]);
				insureContentList.add(InsureType.getInsureTypeMap().get(key) + (insure_params_valueMap.get(key) == null || "".equals(insure_params_valueMap.get(key)) ? "" : "("+insure_params_valueMap.get(key)+")"));
			}
			
			String applicant_id = obj.getString("applicant_id");
			String hideStr = applicant_id.substring(6, 14);
			applicant_id = applicant_id.replaceAll(hideStr, "***");
			obj.put("applicant_id", applicant_id);
			
			obj.put("insure_content", insureContentList);
			obj.put("need_pay_money", order.getNeed_pay_money());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}
	
	@Override
	public JSONObject getRepairOrderDetail(String order_id) {
		JSONObject obj = new JSONObject();
		try {
			//查询店铺支持的该车型的所有保养项目
			Order order = get(Integer.valueOf(order_id));
			String sql = "SELECT t2.id, t2.itemname FROM es_store_repairitem t1, es_repair_items t2 WHERE t1.`repair_item_id`=t2.`id` "
					   + "AND t1.store_id=? AND carmodel_id=(SELECT carmodelid FROM es_carinfo WHERE carplate=?) ORDER BY t2.sort";

			sql = "select t.id, t.itemname from ("+ sql +") t";

			List list = daoSupport.queryForList(sql, order.getService_store_id(), order.getCarplate());
			JSONArray storeRepairItems = JSONArray.fromObject(list);
			obj.put("storeRepairItems", storeRepairItems);
			
			//查询订单保养记录
			JSONObject item_json = JSONArray.fromObject(order.getItems_json()).getJSONObject(0);
			int insure_repair_specid = item_json.getInt("insure_repair_specid");
			sql = "SELECT t1.*, t2.starttime, t2.endtime, t2.ratio FROM es_repair_order_spec t1, es_repair_timeregion t2 WHERE t1.time_region_id=t2.time_region_id AND t1.spec_id=?";
			List repairOrderList = daoSupport.queryForList(sql, insure_repair_specid);
			if(repairOrderList.size() > 0){
				JSONObject repairOrderObj = JSONObject.fromObject(repairOrderList.get(0));
				obj.put("spec_id", repairOrderObj.getString("spec_id"));
				obj.put("items", repairOrderObj.getString("repair_item_ids"));
				obj.put("repair_mile", repairOrderObj.getString("repair_mile"));
				obj.put("repair_price", repairOrderObj.getString("repair_price"));
				obj.put("repair_source", repairOrderObj.getString("repair_source"));
				obj.put("service_timelength", repairOrderObj.getString("service_timelength"));
				obj.put("engineer", repairOrderObj.getString("engineer"));
				obj.put("repair_time", repairOrderObj.getString("repair_time"));
				obj.put("repair_remarks", StringUtil.isNull(repairOrderObj.getString("repair_remarks")) ? "" : repairOrderObj.getString("repair_remarks"));
				obj.put("order_date", repairOrderObj.getString("order_date"));
				obj.put("starttime", repairOrderObj.getString("starttime"));
				obj.put("endtime", repairOrderObj.getString("endtime"));
				
				//查询车辆信息
				sql = "SELECT t1.repair4sstoreid, t2.brand, t2.series, t2.nk, t2.discharge FROM es_carinfo t1, es_carmodels t2 WHERE t1.carmodelid=t2.id AND t1.carplate=?";
				List carinfoList = daoSupport.queryForList(sql, order.getCarplate());
				if(carinfoList.size() > 0){
					JSONObject carObj = JSONObject.fromObject(carinfoList.get(0));
					obj.put("brand", carObj.getString("brand"));
					obj.put("series", carObj.getString("series"));
					obj.put("nk", carObj.getString("nk"));
					obj.put("discharge", carObj.getString("discharge"));
					int repair4sstoreid = carObj.getInt("repair4sstoreid");
					double discount = 0.0;
					sql = "SELECT discountcontract, discountnoncontract FROM es_store WHERE store_id=?";
					List storeinfoList = daoSupport.queryForList(sql, order.getService_store_id());
					if(storeinfoList.size() > 0){
						JSONObject storeObj = JSONObject.fromObject(storeinfoList.get(0));
						double discountcontract = storeObj.getDouble("discountcontract");
						double discountnoncontract = storeObj.getDouble("discountnoncontract");
						if(repair4sstoreid == Integer.valueOf(order.getService_store_id())){//签约折扣
							discount = discountcontract;
						}else{//非签约折扣
							discount = discountnoncontract;
						}
					}
					
					//查询订单保养项目集合
					sql = "SELECT t2.id, t2.itemname, FORMAT(t1.item_price * "+ discount +", 2) item_price FROM es_store_repairitem t1, es_repair_items t2 WHERE t1.`repair_item_id`=t2.`id` AND t1.store_id=? "
						+ "AND t1.carmodel_id=(SELECT carmodelid FROM es_carinfo WHERE carplate=?) "
						+ "AND t1.repair_item_id IN ("+ repairOrderObj.getString("repair_item_ids") +") "
						+ "ORDER BY t2.sort";
					List itemList = daoSupport.queryForList(sql, order.getService_store_id(), order.getCarplate());
					JSONArray orderRepairItems = new JSONArray();
					if(itemList.size() > 0){
						orderRepairItems = JSONArray.fromObject(itemList);
					}
					obj.put("orderRepairItems", orderRepairItems);
					
					//查询工时费合计
					double time_region_ratio = repairOrderObj.getDouble("ratio");
					sql = "SELECT FORMAT(SUM(t1.repair_price * "+ discount +" * "+ time_region_ratio +"), 2) repair_price FROM es_store_repairitem t1, es_repair_items t2 WHERE t1.`repair_item_id`=t2.`id` AND t1.store_id="+ order.getService_store_id() +" "
						+ "AND t1.carmodel_id=(SELECT carmodelid FROM es_carinfo WHERE carplate='"+ order.getCarplate() +"') "
						+ "AND t1.repair_item_id IN ("+ repairOrderObj.getString("repair_item_ids") +") "
						+ "ORDER BY t2.sort";
					String repair_total_price = daoSupport.queryForString(sql);
					obj.put("repair_total_price", repair_total_price);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public int getRepairOrderCount() {
		try {
			String sql = "SELECT COUNT(*) FROM es_order_items t1, es_repair_order_spec t2 WHERE t1.`insure_repair_specid` = t2.`spec_id` "
					   + "AND t1.order_id IN (SELECT order_id FROM es_order WHERE parent_id IS NOT NULL AND order_type = "+ OrderType.REPAIR +" AND STATUS = 2) "
					   + "AND FROM_UNIXTIME(t2.`create_time`/1000, '%Y-%m-%d')='"+DateUtil.toString(new Date(), "yyyy-MM-dd")+"'";

			return daoSupport.queryForInt(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void updateAddress() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		try{
			String shipping_method =  request.getParameter("shipping_method");//用户地址为1，到店为2 
			String order_ids = request.getParameter("order_ids");
			int addr_id = StringUtil.toInt(request.getParameter("addr_id"),0);
			MemberAddress address =  memberAddressManager.getAddress(addr_id);
			List<Order> orders = getOrders(order_ids);
			if(orders==null){
				throw new RuntimeException("该订单不存在");
			}
			if(Integer.parseInt(shipping_method) == 2 ){
				address = new MemberAddress();
				Store store = storeManager.getStore(orders.get(0).getService_store_id());
				address.setProvince_id(store.getStore_provinceid());
				address.setProvince(store.getStore_province());
				address.setCity_id(store.getStore_cityid());
				address.setCity(store.getStore_city());
				address.setRegion_id(store.getStore_regionid());
				address.setRegion(store.getStore_region());
				address.setZip(store.getZip());
				address.setAddr(store.getAttr());
				Member member = UserConext.getCurrentMember();
				address.setName(member.getFullname());
				address.setMobile(member.getUsername());
			}
			if(address==null){
				throw new RuntimeException("该收货地址不存在");
			}
			
			
			if(orders.size()>0){
				for(Order order : orders){
					order.setShipping_method(Integer.parseInt(shipping_method));
					order.setShip_provinceid(address.getProvince_id());
					order.setShip_cityid(address.getCity_id());
					order.setShip_regionid(address.getRegion_id());
					order.setShip_addr(address.getAddr());
					order.setShip_zip(address.getZip());
					order.setRegionid(address.getRegion_id());
					order.setShipping_area(address.getProvince() + "-" + address.getCity()
					+ "-" + address.getRegion() + "-" + address.getAddr());
					order.setShip_name(address.getName()); //更改订单配送名称
					order.setShip_mobile(address.getMobile()); //手机号
					order.setMemberAddress(address);
					order.setAddress_id(address.getAddr_id());
					orderManager.edit(order);
				}
			}
			
		}catch(Exception e){
			throw e;
		}
	}
	
	@Override
	public JSONObject isPayable(StoreOrder order) {
		JSONObject obj = new JSONObject();
		try {
			JSONObject specObj = JSONArray.fromObject(order.getItems_json()).getJSONObject(0);
			int spec_id = specObj.getInt("insure_repair_specid");
			String sql = "SELECT store_id, order_date, time_region_id FROM es_repair_order_spec WHERE spec_id=?";
			List resultList = daoSupport.queryForList(sql, spec_id);
			if(resultList.size() > 0){
				JSONObject repairObj = JSONObject.fromObject(resultList.get(0));
				sql = "SELECT (t1.station - IFNULL(t2.station, 0)) usable_station FROM es_repair_timeregion t1 "
					+ "LEFT JOIN (SELECT time_region_id, order_date, SUM(station) station FROM es_store_repair_timeregion WHERE store_id=? AND order_date=? AND order_status=1 GROUP BY time_region_id, order_date) t2 "
					+ "ON t1.time_region_id=t2.time_region_id WHERE t1.store_id=? AND t1.time_region_id=? ORDER BY t1.starttime";
				int usable_station = daoSupport.queryForInt(sql, repairObj.getInt("store_id"), repairObj.getLong("order_date"), repairObj.getInt("store_id"), repairObj.getInt("time_region_id"));
				if(usable_station == 0){//工位约满，取消该订单，提示用户该订单不可支付，已取消
					//工位已满，取消该订单
					sql = "update es_order set status=8 where order_id=?";
					daoSupport.execute(sql, order.getOrder_id());
					obj.put("result", 0);
					obj.put("message", "您预约的该时间段的店铺工位已约满，系统已自动为您取消该订单，您可以选择预约该店铺其他时间段的工位进行车辆保养");
				}else{
					obj.put("result", 1);
				}
			}else{
				obj.put("result", 1);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}

	
	/** @description 获取多个订单对象
	 * @date 2016年9月8日 下午4:31:42
	 * @return
	 * @return List<Order>
	 */
	private List<Order> getOrders(String orders) {
		if(orders.lastIndexOf(",")==orders.length()-1){
			orders = orders.substring(0,orders.lastIndexOf(","));
		}
		StringBuffer sql  = new StringBuffer();
		sql.append("select *from es_order where order_id in(").append(orders).append(")");
		return daoSupport.queryForList(sql.toString(),Order.class);
		
	}
	public IBonusManager getBonusManager() {
		return bonusManager;
	}


	public void setBonusManager(IBonusManager bonusManager) {
		this.bonusManager = bonusManager;
	}


	public CarInfoManager getCarInfoManager() {
		return carInfoManager;
	}


	public void setCarInfoManager(CarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}


	public IStoreOrderFlowManager getStoreOrderFlowManager() {
		return storeOrderFlowManager;
	}


	public void setStoreOrderFlowManager(IStoreOrderFlowManager storeOrderFlowManager) {
		this.storeOrderFlowManager = storeOrderFlowManager;
	}


	public IOrderReportManager getOrderReportManager() {
		return orderReportManager;
	}


	public void setOrderReportManager(IOrderReportManager orderReportManager) {
		this.orderReportManager = orderReportManager;
	}


	public IStoreManager getStoreManager() {
		return storeManager;
	}


	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}


	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}


	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}
	
}

