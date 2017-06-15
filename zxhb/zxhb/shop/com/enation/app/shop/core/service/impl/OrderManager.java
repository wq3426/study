package com.enation.app.shop.core.service.impl;

import java.io.File;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.IRoleManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.core.model.CarInfo;
import com.enation.app.shop.core.model.DepotUser;
import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.Logi;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.InsureRepairSpec;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IOrderAllocationManager;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.OrderType;
import com.enation.eop.SystemSetting;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.DoubleMapper;
import com.enation.framework.database.Page;
import com.enation.framework.database.StringMapper;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.ExcelUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 订单管理
 * 
 * @author kingapex 2010-4-6上午11:16:01
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class OrderManager extends BaseSupport implements IOrderManager {

	private ICartManager cartManager;
	private IDlyTypeManager dlyTypeManager;
	private IPaymentManager paymentManager;

	private IPromotionManager promotionManager;
	private OrderPluginBundle orderPluginBundle;
	private IPermissionManager permissionManager;
	private IAdminUserManager adminUserManager;
	private IRoleManager roleManager;
	private IGoodsManager goodsManager;
	private IOrderAllocationManager orderAllocationManager;
	private IDepotManager depotManager;
	private CartPluginBundle cartPluginBundle;
	private ProductManager productManager;
	private IMemberManager memberManager;
	private IMemberAddressManager memberAddressManager;
	private ILogiManager logiManager;
	
	public IOrderAllocationManager getOrderAllocationManager() {
		return orderAllocationManager;
	}
	public void setOrderAllocationManager(
			IOrderAllocationManager orderAllocationManager) {
		this.orderAllocationManager = orderAllocationManager;
	}
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	public IDepotManager getDepotManager() {
		return depotManager;
	}
	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}
	@Transactional(propagation = Propagation.REQUIRED)
	public void savePrice(double price, int orderid) {
		Order order = this.get(orderid);
		double amount = order.getOrder_amount();
		// double discount= amount-price;
		double discount = CurrencyUtil.sub(amount, price);
		if(order.getOrder_type() == OrderType.ZA_INSURANCE || order.getOrder_type() == OrderType.STORE_INSURANCE){//保险订单，更改订单状态为待支付
			this.baseDaoSupport.execute("update order set need_pay_money=?, status=? where order_id=?",price, OrderStatus.ORDER_NOT_PAY, orderid);
		}else{
			this.baseDaoSupport.execute("update order set need_pay_money=? where order_id=?",price, orderid);
		}
		
		//修改收款单价格 
		String sql="update es_payment_logs set money=? where order_id=?";
		this.daoSupport.execute(sql, price,orderid);
		
		this.baseDaoSupport.execute("update order set discount=? where order_id=?", discount, orderid);
		
		//修改价格时判断如果订单商品总奖励大于订单价格，则不给予使用商品奖励
		/*if(checkOrderPriceChange(order,price)){
			this.daoSupport.execute("update es_order_items set rewards_limit = ?  where order_id = ? ",0,orderid);
			List<OrderItem> orderItemList = this.getOrderItem(orderid);
			String itemsJson  = JSONArray.fromObject(orderItemList).toString();
			this.daoSupport.execute("update es_order set items_json=? where order_id=?", itemsJson,orderid);
		}else{
			int reword_limit = this.daoSupport.queryForInt("select sum(e.rewards_limit) from es_order_items e where  order_id = ?", orderid);
			if(reword_limit <= 0){//如果订单价格修改后,reword_limit还是为0则将对应的product奖励放入
				JSONArray orderItems = JSONArray.fromObject(order.getItems_json());
				for(int i = 0 ; i < orderItems.size() ; i++){
					JSONObject obj= JSONObject.fromObject(orderItems.get(i));
					if(order.getOrder_type() == OrderType.ZA_INSURANCE || order.getOrder_type() == OrderType.STORE_INSURANCE || order.getOrder_type() == OrderType.REPAIR){//保险、保养商品原来的奖励使用额度，从表es_insure_repair_spec中取
						int insure_repair_specid = obj.getInt("insure_repair_specid");
						InsureRepairSpec spec = getInsureOrRepairSpecById(insure_repair_specid);
						this.daoSupport.execute("update es_order_items set rewards_limit = ?  where  item_id= ? ",spec.getRewards_limit(),obj.get("item_id"));
					}else{
						Product product = productManager.get(obj.getInt("product_id"));
						this.daoSupport.execute("update es_order_items set rewards_limit = ?  where  item_id= ? ",product.getRewards_limit(),obj.get("item_id"));
					}
				}
				List<OrderItem> orderItemList = this.getOrderItem(orderid);
				String itemsJson  = JSONArray.fromObject(orderItemList).toString();
				this.daoSupport.execute("update es_order set items_json=? where order_id=?", itemsJson.toString(),orderid);
			}
		
		}	*/
	}
	
	private boolean checkOrderPriceChange(Order order,double price) {
		String order_Items = order.getItems_json();
		JSONArray jsonArray = JSONArray.fromObject(order_Items);
		Double count = 0d;
		for(int i = 0 ; i < jsonArray.size();i++){
			JSONObject object = jsonArray.getJSONObject(i);
			if(order.getOrder_type() == OrderType.ZA_INSURANCE || order.getOrder_type() == OrderType.STORE_INSURANCE || order.getOrder_type() == OrderType.REPAIR){
				InsureRepairSpec spec = getInsureOrRepairSpecById(object.getInt("insure_repair_specid"));
				count += ( spec.getRewards_limit() * object.getDouble("num"));
			}else{
				Product product = productManager.get(object.getInt("product_id"));
				count += ( product.getRewards_limit() * object.getDouble("num"));
			}
		}
		if(price <= count){
			return true;
		}
		return false;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public double saveShipmoney(double shipmoney, int orderid) {
		Order order = this.get(orderid);
		double currshipamount = order.getShipping_amount();
		// double discount= amount-price;
		double  shortship = CurrencyUtil.sub(shipmoney, currshipamount);
		double discount = CurrencyUtil.sub(currshipamount, shipmoney);
		//2014-9-18 配送费用修改 @author LiFenLong
		this.baseDaoSupport.execute(
				"update order set order_amount=order_amount+?,need_pay_money=need_pay_money+?,shipping_amount=?,discount=discount+? where order_id=?", shortship,shortship,shipmoney,discount,
				orderid);
		//2014-9-12 LiFenLong 修改配送金额同时修改收款单
		this.daoSupport.execute("update es_payment_logs set money=money+? where order_id=?",shortship,orderid);
		return this.get(orderid).getOrder_amount();
	}
	/**
	 * 记录订单操作日志
	 * 
	 * @param order_id
	 * @param message
	 * @param op_id
	 * @param op_name
	 */
	public void log(Integer order_id, String message, Integer op_id,
			String op_name) {
		OrderLog orderLog = new OrderLog();
		orderLog.setMessage(message);
		orderLog.setOp_id(op_id);
		orderLog.setOp_name(op_name);
		orderLog.setOp_time(com.enation.framework.util.DateUtil.getDateline());
		orderLog.setOrder_id(order_id);
		this.baseDaoSupport.insert("order_log", orderLog);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Order add(Order order,List<CartItem> itemList, String sessionid) {
		String opname = "游客";

		if (order == null)
			throw new RuntimeException("error: order is null");

		/************************** 用户信息 ****************************/
		Member member = UserConext.getCurrentMember();
		
//		Member data_member = memberManager.get(member.getMember_id());
//		//判断用户数据是否被手动删除
//		if(data_member==null){
//			throw new RuntimeException("创建订单失败，当前用户不存在，请联系平台管理员！");
//		}
		
		// 非匿名购买
		if (member != null) {
			order.setMember_id(member.getMember_id());
			opname = member.getUsername();
		}

		
		// 配送方式名称
		DlyType dlyType = new DlyType();
		if (dlyType != null && order.getShipping_id()!=0){
			dlyType = dlyTypeManager.getDlyTypeById(order.getShipping_id());
		}else{
			dlyType.setName("");
		}
		order.setShipping_type(dlyType.getName());
		
		
		/************ 支付方式价格及名称*************/
		PayCfg payCfg = this.paymentManager.get(order.getPayment_id());
		//此方法实现体为空注释掉
		order.setPaymoney(this.paymentManager.countPayPrice(order.getOrder_id()));
		order.setPayment_name(payCfg.getName());
		order.setPayment_type(payCfg.getType());

		/************ 创建订单 ************************/
		order.setCreate_time(com.enation.framework.util.DateUtil.getDateline());
		
		//判断订单号是否为空,为空则生成订单号
		if(StringUtil.isEmpty(order.getSn())){
			order.setSn(this.createSn());
		}
		order.setStatus(OrderStatus.ORDER_NOT_CONFIRM);
		order.setDisabled(0);
		order.setPay_status(OrderStatus.PAY_NO);
		order.setShip_status(OrderStatus.SHIP_NO);
		order.setOrderStatus(OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_CONFIRM));
//		order.setOrderStatus("订单已生效");
		
		//给订单添加仓库 ------仓库为默认仓库
		Integer depotId= this.baseDaoSupport.queryForInt("select id from depot where choose=1");
		order.setDepotid(depotId);
		/************ 写入订单货物列表 ************************/
 
		
		/**检测商品库存  Start**/
		boolean result = true;	//用于判断购买量是否超出库存
		String productName = "";
		for(CartItem item : itemList){
			int productId = item.getProduct_id();
			Product product = productManager.get(productId);
			int enableStore = product.getEnable_store();
			int itemNum = item.getNum();
			if(itemNum > enableStore){
				result = false;
				productName = product.getName();
				break;
			}
		}
		if(!result){
			throw new RuntimeException("创建订单失败，您购买的商品("+productName+")库存不足");
		}
		/**检测商品库存  End**/
		this.orderPluginBundle.onBeforeCreate(order,itemList, sessionid);
		this.baseDaoSupport.insert("es_order", order);

//		if (itemList.isEmpty() )
//			throw new RuntimeException("创建订单失败，购物车为空");

		Integer orderId = this.baseDaoSupport.getLastId("es_order");
		order.setOrder_id(orderId);
		
		this.saveGoodsItem(itemList, order);


		/************ 写入订单日志 ************************/
		OrderLog log = new OrderLog();
		log.setMessage("订单创建");
		log.setOp_name(opname);
		log.setOrder_id(orderId);
		this.addLog(log);
		order.setOrder_id(orderId);
		
		this.orderPluginBundle.onAfterCreate(order,itemList, sessionid);
		
		//因为在orderFlowManager中已经注入了orderManager，不能在这里直接注入
		//将来更好的办法是将订单创建移到orderFlowManager中
		//下单则自动改为已确认
		IOrderFlowManager flowManager = SpringContextHolder.getBean("orderFlowManager");
		flowManager.confirmOrder(orderId,null);
		
		//只有b2c产品清空session
		if(EopSetting.PRODUCT.equals("b2c")){
			cartManager.clean(sessionid);
		}
//		HttpCacheManager.sessionChange();
	
		return order;
	}

	/**
	 * 添加订单日志
	 * 
	 * @param log
	 */
	private void addLog(OrderLog log) {
		log.setOp_time(com.enation.framework.util.DateUtil.getDateline());
		this.baseDaoSupport.insert("order_log", log);
	}

	/**
	 * 保存商品订单项
	 * 
	 * @param itemList
	 * @param order_id
	 */
	private void saveGoodsItem(List<CartItem> itemList, Order order) {
		try {
			
			List<OrderItem> orderItemList = new ArrayList<OrderItem>();
			
			Integer order_id = order.getOrder_id();
			for (int i = 0; i < itemList.size(); i++) {

				OrderItem orderItem = new OrderItem();

				CartItem cartItem = (CartItem) itemList.get(i);
				
				orderItem.setPrice(cartItem.getCoupPrice());
				orderItem.setName(cartItem.getName());
				orderItem.setNum(cartItem.getNum());

				orderItem.setGoods_id(cartItem.getGoods_id());
				orderItem.setShip_num(0);
				orderItem.setProduct_id(cartItem.getProduct_id());
				orderItem.setOrder_id(order_id);
				orderItem.setGainedpoint(cartItem.getPoint());
				orderItem.setAddon(cartItem.getAddon());
				
				//3.0新增的三个字段
				orderItem.setSn(cartItem.getSn());
				orderItem.setImage(cartItem.getImage_default());
				orderItem.setCat_id(cartItem.getCatid());
				orderItem.setUnit(cartItem.getUnit());
				orderItem.setRewards_limit(cartItem.getRewards_limit());
				orderItem.setInsure_repair_specid(cartItem.getInsure_repair_specid());
				this.baseDaoSupport.insert("order_items", orderItem);
				int itemid = this.baseDaoSupport.getLastId("order_items");
				orderItem.setItem_id(itemid);
				orderItemList.add(orderItem);
				this.orderPluginBundle.onItemSave(order,orderItem);
			}
			
			String itemsJson  = JSONArray.fromObject(orderItemList).toString();
			this.daoSupport.execute("update es_order set items_json=? where order_id=?", itemsJson,order_id);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 保存赠品项
	 * 
	 * @param itemList
	 * @param orderid
	 * @throws IllegalStateException
	 *             会员尚未登录,不能兑换赠品!
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	private void saveGiftItem(List<CartItem> itemList, Integer orderid) {
		Member member = UserConext.getCurrentMember();
		if (member == null) {
			throw new IllegalStateException("会员尚未登录,不能兑换赠品!");
		}

		int point = 0;
		for (CartItem item : itemList) {
			point += item.getSubtotal().intValue();
			this.baseDaoSupport
					.execute(
							"insert into order_gift(order_id,gift_id,gift_name,point,num,shipnum,getmethod)values(?,?,?,?,?,?,?)",
							orderid, item.getProduct_id(), item.getName(),
							item.getPoint(), item.getNum(), 0, "exchange");
		}
		if (member.getPoint().intValue() < point) {
			throw new IllegalStateException("会员积分不足,不能兑换赠品!");
		}
		member.setPoint(member.getPoint() - point); // 更新session中的会员积分
		this.baseDaoSupport.execute(
				"update member set point=? where member_id=? ",
				member.getPoint(), member.getMember_id());

	}

	public Page listbyshipid(int pageNo, int pageSize, int status,
			int shipping_id,String sort, String order) {
		order = " ORDER BY "+sort+" "+order;
		String sql = "select * from order where disabled=0 and status="
				+ status + " and shipping_id= " + shipping_id;
		sql += " order by " + order;
		Page page = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize,
				Order.class);
		return page;
	}

	public Page listConfirmPay(int pageNo, int pageSize,String sort, String order) {
		order = " order_id";
		String sql = "select * from order where disabled=0 and ((status = "
				+ OrderStatus.ORDER_SERVECE/*ORDER_SHIP*/ + " and payment_type = 'cod') or status= "
				+ OrderStatus.ORDER_PAY + "  )";
		sql += " order by " + order;
		//System.out.println(sql);
		Page page = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize,Order.class);
		return page;
	}

	public Order get(Integer orderId) {
		String sql = "select * from order where order_id=?";
		Order order = (Order) this.baseDaoSupport.queryForObject(sql,
				Order.class, orderId);
		return order;
	}
	
	public Order get(String ordersn) {
		String sql  ="select * from es_order where sn='"+ordersn+"'";
		Order order  = (Order)this.baseDaoSupport.queryForObject(sql, Order.class);
		return order;
		 
	}
	
	
	public List<OrderItem> listGoodsItems(Integer orderId) {

		String sql = "select * from " + this.getTableName("order_items");
		sql += " where order_id = ?";
		List<OrderItem > itemList = this.daoSupport.queryForList(sql,OrderItem.class, orderId);
		this.orderPluginBundle.onFilter(orderId, itemList);
		return itemList;
	}
	
	@Override
	public List listGoodsItemsByItemId(Integer itemId) {

		String sql = "select i.*,g.is_pack from " + this.getTableName("order_items");
		sql += " i LEFT JOIN es_goods g on i.goods_id = g.goods_id where i.item_id = ?";
		List itemList = this.daoSupport.queryForList(sql, itemId);
		return itemList;
	}
	
	//获取申请退货订单商品列表
	public List getOrderItem(int order_id) {
		String sql="select o.*,g.name,g.is_pack from es_goods g INNER JOIN es_order_items o on o.goods_id=g.goods_id where o.order_id = ? ";
		List<Map> items = this.baseDaoSupport.queryForList(sql, order_id);
		for(Map item : items){
			Object obj = item.get("addon");
			if(obj == null){
				obj = "";
			}
			String addon = obj.toString();
			if(!StringUtil.isEmpty(addon)){
				
				List<Map<String,Object>> specList = JsonUtil.toList(addon);
				
				FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
				freeMarkerPaser.setClz(this.getClass());
				freeMarkerPaser.putData("specList",specList);
				freeMarkerPaser.setPageName("order_item_spec");
				String html = freeMarkerPaser.proessPageContent(); 
				
				item.put("other", html);
			}
		}
		return items;
	}
	

	public List listGiftItems(Integer orderId) {
		String sql = "select * from order_gift where order_id=?";
		return this.baseDaoSupport.queryForList(sql, orderId);
	} 

	/**
	 * 读取订单日志
	 */

	public List listLogs(Integer orderId) {
		String sql = "select * from order_log where order_id=?";
		return this.baseDaoSupport.queryForList(sql, orderId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void clean(Integer[] orderId) {
		String ids = StringUtil.arrayToString(orderId, ",");
		String sql = "delete from order where order_id in (" + ids + ")";
		this.baseDaoSupport.execute(sql);

		sql = "delete from order_items where order_id in (" + ids + ")";
		this.baseDaoSupport.execute(sql);

		sql = "delete from order_log where order_id in (" + ids + ")";
		this.baseDaoSupport.execute(sql);

		sql = "delete from payment_logs where order_id in (" + ids + ")";
		this.baseDaoSupport.execute(sql);

		sql = "delete from " + this.getTableName("delivery_item")
				+ " where delivery_id in (select delivery_id from "
				+ this.getTableName("delivery") + " where order_id in (" + ids
				+ "))";
		this.daoSupport.execute(sql);

		sql = "delete from delivery where order_id in (" + ids + ")";
		this.baseDaoSupport.execute(sql);
		
		orderAllocationManager.clean(orderId);

		
		/**
		 * -------------------
		 * 激发订单的删除事件
		 * -------------------
		 */
		this.orderPluginBundle.onDelete(orderId);
		

	}
	
	private boolean exec(Integer[] orderId, int disabled) {
		if(cheack(orderId)){
			String ids = StringUtil.arrayToString(orderId, ",");
			String sql = "update order set disabled = ? where order_id in (" + ids
					+ ")";
			this.baseDaoSupport.execute(sql, disabled);
			return true;
		}else{
			return false;
		}
	}
	private boolean cheack(Integer[] orderId){
		boolean i=true;
		for (int j = 0; j < orderId.length; j++) {
			if(this.baseDaoSupport.queryForInt("select status from es_order where order_id=?",orderId[j])!=OrderStatus.ORDER_CANCELLATION){
				i=false;
			}
		}
		return i;
	}
	public boolean delete(Integer[] orderId) {
		return exec(orderId, 1);

	}

	public void revert(Integer[] orderId) {
		exec(orderId, 0);

	}
	/**
	 * 创建订单号（日期+两位随机数）
	 */
	public String createSn() {
		boolean isHave = true;  //数据库中是否存在该订单
		String sn = "";			//订单号
		
		//如果存在当前订单
		while(isHave) {
			StringBuffer  snSb = new StringBuffer(DateUtil.getDateline()+"") ;
			snSb.append(getRandom());
			String sql = "SELECT count(order_id) FROM es_order WHERE sn = '" + snSb.toString() + "'";
			int count = this.baseDaoSupport.queryForInt(sql);
			if(count == 0) {
				sn = snSb.toString();
				isHave = false;
			}
		}
		

		return sn;
	}
	/**
	 * 获取随机数
	 * @return
	 */
	public  int getRandom(){
		Random random=new Random();
		int num=Math.abs(random.nextInt())%100;
		if(num<10){
			num=getRandom();
		}
		return num;
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

	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}

	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	public List listOrderByMemberId(int member_id) {
		String sql = "select * from order where member_id = ? order by create_time desc";
		List list = this.baseDaoSupport.queryForList(sql, Order.class,
				member_id);
		return list;
	}

	public Map mapOrderByMemberId(int memberId) {
		Integer buyTimes = this.baseDaoSupport.queryForInt(
				"select count(0) from order where member_id = ?", memberId);
		Double buyAmount = (Double) this.baseDaoSupport.queryForObject(
				"select sum(paymoney) from order where member_id = ?",
				new DoubleMapper(), memberId);
		Map map = new HashMap();
		map.put("buyTimes", buyTimes);
		map.put("buyAmount", buyAmount);
		return map;
	}

	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}

	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}

	public void edit(Order order) {
		this.baseDaoSupport.update("order", order,
				"order_id = " + order.getOrder_id());

	}

	public List<Map> listAdjItem(Integer orderid) {
		String sql = "select * from order_items where order_id=? and addon!=''";
		return this.baseDaoSupport.queryForList(sql, orderid);
	}


	/**
	 * 统计订单状态
	 */
	public Map censusState() {

		// 构造一个返回值Map，并将其初始化：各种订单状态的值皆为0
		Map<String, Integer> stateMap = new HashMap<String, Integer>(7);
		String[] states = { "cancel_ship", "cancel_pay",  "pay","ship", "complete", "allocation_yes" };
		for (String s : states) {
			stateMap.put(s, 0);
		}

		// 分组查询、统计订单状态
		String sql = "select count(0) num,status  from "
				+ this.getTableName("order")
				+ " where disabled = 0 group by status";
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
//		// 将list转为map
		for (Map<String, Integer> state : list) {
			stateMap.put(this.getStateString(state.get("status")),state.get("num"));
		}
		
		sql = "select count(0) num  from " + this.getTableName("order") + " where disabled = 0  and status=0 ";
		int count=this.daoSupport.queryForInt(sql);
		stateMap.put("wait", 0);
		
		sql = "select count(0) num  from " + this.getTableName("order") + " where disabled = 0  ";
		sql+=" and ( ( payment_type!='cod' and  status="+OrderStatus.ORDER_NOT_PAY +") ";//非货到付款的，未付款状态的可以结算
		sql+=" or ( payment_id=8 and status!="+OrderStatus.ORDER_NOT_PAY+"  and  pay_status!="+OrderStatus.PAY_CONFIRM +")" ; 
		sql+=" or ( payment_type='cod' and  (status="+OrderStatus.ORDER_SERVECE/*ORDER_SHIP*/ +" or status="+OrderStatus.ORDER_APPRAISE/*ORDER_ROG*/+" )  ) )";//货到付款的要发货或收货后才能结算
		count=this.daoSupport.queryForInt(sql);
		stateMap.put("not_pay", count);
		
		sql="select count(0) from es_order where disabled=0  and ( ( payment_type!='cod' and payment_id!=8  and  status=2)  or ( payment_type='cod' and  status=0))";
		count=this.baseDaoSupport.queryForInt(sql);
		stateMap.put("allocation_yes", count);
		
		return stateMap;
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


	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}

	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}

	@Override
	public String export(Date start, Date end) {
		String sql  ="select * from order where disabled=0 ";
		if(start!=null){
			sql+=" and create_time>"+ start.getTime();
		}
		
		if(end!=null){
			sql+="  and create_timecreate_time<"+ end.getTime();
		}
		
		List<Order> orderList  = this.baseDaoSupport.queryForList(sql,Order.class);
		
		
		//使用excel导出流量报表
		ExcelUtil excelUtil = new ExcelUtil(); 
		
		//流量报表excel模板在类包中，转为流给excelutil
		InputStream in =FileUtil.getResourceAsStream("com/enation/app/shop/core/service/impl/order.xls");
		
		excelUtil.openModal( in );
		int i=1;
		for(Order order :orderList){
			
			excelUtil.writeStringToCell(i, 0,order.getSn()); //订单号
			excelUtil.writeStringToCell(i, 1,DateUtil.toString(new Date(order.getCreate_time()), "yyyy-MM-dd HH:mm:ss")  ); //下单时间
			excelUtil.writeStringToCell(i, 2,order.getOrderStatus() ); //订单状态
			excelUtil.writeStringToCell(i, 3,""+order.getOrder_amount() ); //订单总额
			excelUtil.writeStringToCell(i, 4,order.getShip_name() ); //收货人
			excelUtil.writeStringToCell(i, 5,order.getPayStatus()); //付款状态
			excelUtil.writeStringToCell(i, 6,order.getShipStatus()); //发货状态
			excelUtil.writeStringToCell(i, 7,order.getShipping_type()); //配送方式
			excelUtil.writeStringToCell(i, 8,order.getPayment_name()); //支付方式
			i++;
		}
		//String target= EopSetting.IMG_SERVER_PATH;
		//saas 版导出目录用户上下文目录access文件夹
		String filename ="/order";
		String static_server_path= SystemSetting.getStatic_server_path();
		File file  = new File(static_server_path+ filename);
		if(!file.exists())file.mkdirs();
		
		filename =filename+ "/order"+com.enation.framework.util.DateUtil.getDateline()+".xls";
		excelUtil.writeToFile(static_server_path+filename);
		String static_server_domain= SystemSetting.getStatic_server_domain();

		return static_server_domain +filename ;
	}


	@Override
	public OrderItem getItem(int itemid) {

		String sql = "select items.*,p.store as store from "
				+ this.getTableName("order_items") + " items ";
		sql += " left join " + this.getTableName("product")
				+ " p on p.product_id = items.product_id ";
		sql += " where items.item_id = ?";

		OrderItem item = (OrderItem) this.daoSupport.queryForObject(sql,
				OrderItem.class, itemid);

		return item;
	}


	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public IRoleManager getRoleManager() {
		return roleManager;
	}

	public void setRoleManager(IRoleManager roleManager) {
		this.roleManager = roleManager;
	}



	/**
	 * 
	 */
	public int getMemberOrderNum(int member_id, int payStatus) {
		return this.baseDaoSupport
				.queryForInt(
						"SELECT COUNT(0) FROM order WHERE member_id=? AND pay_status=?",
						member_id, payStatus);
	}

	/**
	 * by dable
	 */
	@Override
	public List<Map> getItemsByOrderid(Integer order_id) {
		String sql = "select * from order_items where order_id=?";
		return this.baseDaoSupport.queryForList(sql, order_id);
	}

	@Override
	public void refuseReturn(String orderSn) {
		this.baseDaoSupport.execute("update order set state = -5 where sn = ?",
				orderSn);
	}

	/**
	 * 更新订单价格
	 */
	@Override
	public void updateOrderPrice(double price, int orderid) {
		this.baseDaoSupport
				.execute(
						"update order set order_amount = order_amount-?,goods_amount = goods_amount- ? where order_id = ?",
						price, price, orderid);
	}

	/**
	 * 根据id查询物流公司
	 */
	@Override
	public String queryLogiNameById(Integer logi_id) {
		return (String) this.baseDaoSupport.queryForObject(
				"select name from logi_company where id=?", new StringMapper(),
				logi_id);
	}

	/**
	 * 游客订单查询
	 */
	public Page searchForGuest(int pageNo, int pageSize, String ship_name,
			String ship_tel) {
		String sql = "select * from order where ship_name=? AND (ship_mobile=? OR ship_tel=?) and member_id is null ORDER BY order_id DESC";
		Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo,pageSize, Order.class, ship_name, ship_tel, ship_tel);
		return page;
	}

	public Page listByStatus(int pageNo, int pageSize, int status, int memberid) {
		String filedname = "status";
		if (status == 0) {
			// 等待付款的订单 按付款状态查询
			filedname = " status!=" + OrderStatus.ORDER_CANCELLATION
					+ " AND pay_status";
		}
		String sql = "select * from order where " + filedname
				+ "=? AND member_id=? ORDER BY order_id DESC";
		Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo,
				pageSize, Order.class, status, memberid);
		return page;
	}
	
	public Page listByStatus(int pageNo, int pageSize, int memberid) {
		String sql="";
		if(EopSetting.PRODUCT.equals("b2c")){
			sql= "select e.sn,e.`status`,e.pay_status,e.create_time from es_order e where member_id=? ORDER BY order_id DESC";	
		}else{
			sql = "select e.sn,e.`status`,e.pay_status,e.create_time from es_order e where member_id=? and parent_id is not null ORDER BY order_id DESC";
		}
		
		Page page = this.baseDaoSupport.queryForPage(sql.toString(), pageNo,
				pageSize, Order.class, memberid);
		return page;
	}
	
	
	public List<Order> listByStatus(int status,int memberid){
		String filedname = "status";
		if (status == 0) {
			// 等待付款的订单 按付款状态查询
			filedname = " status!=" + OrderStatus.ORDER_CANCELLATION
					+ " AND pay_status";
		}
		String sql = "select * from order where " + filedname
				+ "=? AND member_id=? ORDER BY order_id DESC";
		
		return this.baseDaoSupport.queryForList(sql,status,memberid);
		
	}

	public int getMemberOrderNum(int member_id) {
		return this.baseDaoSupport.queryForInt(
				"SELECT COUNT(0) FROM order WHERE member_id=?", member_id);
	}
	@Override
	public Page search(int pageNO, int pageSize, int disabled, String sn,
			String logi_no, String uname, String ship_name, int status,Integer paystatus){

		StringBuffer sql = new StringBuffer(
				"select * from " + this.getTableName("order") + " where disabled=?  ");
		if (status != -100) {
			if(status==-99){
				/*
				 * 查询未处理订单
				 * */
			sql.append(" and ((payment_type='cod' and status=0 )  or (payment_type!='cod' and status=1 )) ");
			}
			else
				sql.append(" and status = " + status + " ");
			
		}
		if (paystatus!=null && paystatus != -100) {
				sql.append(" and pay_status = " + paystatus + " ");
		}
		
		if (!StringUtil.isEmpty(sn)) {
			sql.append(" and sn = '" + sn + "' ");
		}
		if (!StringUtil.isEmpty(uname)) {
			sql.append(" and member_id  in ( SELECT  member_id FROM " + this.getTableName("member") + " where username = '"
					+ uname + "' )  ");
		}
		if (!StringUtil.isEmpty(ship_name)) {
			sql.append(" and  ship_name = '" + ship_name + "' ");
		}
		if (!StringUtil.isEmpty(logi_no)) {
			sql.append(" and order_id in (SELECT order_id FROM " + this.getTableName("delivery") + " where logi_no = '"
					+ logi_no + "') ");
		}
		sql.append(" order by create_time desc ");
		Page page = this.daoSupport.queryForPage(sql.toString(), pageNO,
				pageSize, Order.class, disabled);
		return page;
	
	}
	@Override
	public Page search(int pageNO, int pageSize, int disabled, String sn,
			String logi_no, String uname, String ship_name, int status) {
		return search( pageNO,  pageSize,  disabled,  sn,
			 logi_no,  uname,  ship_name,  status,null);
	}

	public Order getNext(String next, Integer orderId, Integer status,
			int disabled, String sn, String logi_no, String uname,
			String ship_name) {
		StringBuffer sql = new StringBuffer(
				"select * from " + this.getTableName("order") + " where  1=1  ");
		
		StringBuffer depotsql = new StringBuffer("  ");
		AdminUser adminUser = UserConext.getCurrentAdminUser();
		if (adminUser.getFounder() != 1) { // 非超级管理员加过滤条件
			boolean isShiper = permissionManager.checkHaveAuth(PermissionConfig.getAuthId("depot_admin")); //检测是否是发货员
			
			
			boolean haveOrder = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("order"));// 订单管理员权限
			if (isShiper && !haveOrder) {
				DepotUser depotUser = (DepotUser) adminUser;
				int depotid = depotUser.getDepotid();
				depotsql.append(" and depotid=" + depotid+"  ");
			}
		}
		
		StringBuilder sbsql = new StringBuilder("  ");
		if (status != null && status != -100) {
			sbsql.append(" and status = " + status + " ");
		}
//		if (!StringUtil.isEmpty(sn)) {
//			sbsql.append(" and sn = '" + sn.trim() + "' ");
//		}
		if (!StringUtil.isEmpty(uname)&&!uname.equals("undefined")) {
			sbsql.append(" and member_id  in ( SELECT  member_id FROM " + this.getTableName("member") + " where username = '"
					+ uname + "' )  ");
		}
		if (!StringUtil.isEmpty(ship_name)) {
			sbsql.append("  and  ship_name = '" + ship_name.trim() + "'  ");
		}
		if (!StringUtil.isEmpty(logi_no)&&!logi_no.equals("undefined")) {
			sbsql.append("  and order_id in (SELECT order_id FROM " + this.getTableName("delivery") + " where logi_no = '"	+ logi_no +"')  ");
		}
		if (next.equals("previous")) {
			sql.append("  and order_id IN (SELECT CASE WHEN SIGN(order_id - "
					+ orderId
					+ ") < 0 THEN MAX(order_id)  END AS order_id FROM " + this.getTableName("order") + " WHERE order_id <> "
					+ orderId + depotsql.toString()+" and disabled=? "+sbsql.toString()+" GROUP BY SIGN(order_id - " + orderId
					+ ") ORDER BY SIGN(order_id - " + orderId + "))   ");
			//TODO MAX 及SIGN 函数经试验均可在mysql及oracle中通过，但mssql未验证
		} else if (next.equals("next")) {
			sql.append("  and  order_id in (SELECT CASE WHEN SIGN(order_id - "
					+ orderId
					+ ") > 0 THEN MIN(order_id) END AS order_id FROM " + this.getTableName("order") + " WHERE order_id <> "
					+ orderId + depotsql.toString()+ " and disabled=? "+sbsql.toString()+" GROUP BY SIGN(order_id - " + orderId
					+ ") ORDER BY SIGN(order_id - " + orderId + "))   ");
		} else {
			return null;
		}
		sql.append(" order by create_time desc ");
		////System.out.println(sql);
		Order order = (Order) this.daoSupport.queryForObject(sql.toString(),
				Order.class,disabled);
		return order;
	}

	/**
	 * 获取订单中商品的总价格
	 * 
	 * @param sessionid
	 * @return
	 */
	private double getOrderTotal(String sessionid) {
		List goodsItemList = cartManager.listGoods(sessionid);
		double orderTotal = 0d;
		if (goodsItemList != null && goodsItemList.size() > 0) {
			for (int i = 0; i < goodsItemList.size(); i++) {
				CartItem cartItem = (CartItem) goodsItemList.get(i);
				orderTotal += cartItem.getCoupPrice() * cartItem.getNum();
			}
		}
		return orderTotal;
	}

	




	private OrderItem getOrderItem(Integer itemid){
		return (OrderItem)this.baseDaoSupport.queryForObject("select * from order_items where item_id = ?", OrderItem.class, itemid);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean delItem(Integer itemid,Integer itemnum) {//删除订单货物
		OrderItem item = this.getOrderItem(itemid);
		Order order =  this.get(item.getOrder_id());
		boolean flag  = false;
		int paymentid = order.getPayment_id();
		int status = order.getStatus();
		if((paymentid == 1 ||paymentid == 3||paymentid == 4 ||paymentid ==5)&& (status == 0 ||status == 1 ||status == 2 ||status == 3 ||status == 4   )){
			flag=true;
		}
		if((paymentid == 2)&& (status == 0 ||status == 9 ||status == 3 ||status == 4  )){
			flag=true;
		}
		if(flag){
			try {
				if(itemnum.intValue() <= item.getNum().intValue()){
					Goods goods = goodsManager.getGoods(item.getGoods_id());
					double order_amount =  order.getOrder_amount();
					double itemprice =item.getPrice().doubleValue() * itemnum.intValue();
					double leftprice = CurrencyUtil.sub(order_amount, itemprice);
					int difpoint = (int)Math.floor(leftprice);
					Double[] dlyprice = this.dlyTypeManager.countPrice(order.getShipping_id(), order.getWeight()-(goods.getWeight().doubleValue() * itemnum.intValue() ), leftprice, order.getShip_regionid().toString());
					double sumdlyprice = dlyprice[0];
					this.baseDaoSupport.execute("update order set goods_amount = goods_amount- ?,shipping_amount = ?,order_amount =  ?,weight =  weight - ?,gainedpoint =  ? where order_id = ?"
							, itemprice,sumdlyprice,leftprice,(goods.getWeight().doubleValue() * itemnum.intValue() ),difpoint,order.getOrder_id());
					this.baseDaoSupport.execute("update freeze_point set mp =?,point =?  where orderid = ? and type = ?", difpoint,difpoint,order.getOrder_id(),"buygoods");
					if(itemnum.intValue() == item.getNum().intValue()){
						this.baseDaoSupport.execute("delete from order_items where item_id = ?", itemid);
					}else{
						this.baseDaoSupport.execute("update order_items set num = num - ? where item_id = ?", itemnum.intValue() ,itemid);
					}
					
				}else{
					return false;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}
		return flag;
	}

	
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean saveAddrDetail(String addr, int orderid) {
		if(addr ==null || StringUtil.isEmpty(addr)){
			return false;
		}else{
			this.baseDaoSupport.execute("update order set ship_addr=?  where order_id=?", addr,	orderid);
			return true;
		}
	}
	@Override
	public boolean saveShipInfo(String remark,String ship_day, String ship_name,
			String ship_tel, String ship_mobile, String ship_zip, int orderid) {
		Order order = this.get(orderid);
		try {
			if(ship_day !=null && !StringUtil.isEmpty(ship_day)){
				this.baseDaoSupport.execute("update order set ship_day=?  where order_id=?", ship_day,	orderid);
				if(remark !=null && !StringUtil.isEmpty(remark)&&!remark.equals("undefined")){
					StringBuilder sb = new StringBuilder("");
					sb.append("【配送时间：");
					sb.append(remark.trim());
					sb.append("】");
					this.baseDaoSupport.execute("update order set remark= concat(remark,'"+sb.toString()+"')   where order_id=?",	orderid);
				}
					return true;
			}
			if(ship_name !=null && !StringUtil.isEmpty(ship_name)){
				this.baseDaoSupport.execute("update order set ship_name=?  where order_id=?", ship_name,	orderid);
					return true;
			}
			if(ship_tel !=null && !StringUtil.isEmpty(ship_tel)){
				this.baseDaoSupport.execute("update order set ship_tel=?  where order_id=?", ship_tel,	orderid);
					return true;
			}
			if(ship_mobile !=null && !StringUtil.isEmpty(ship_mobile)){
				this.baseDaoSupport.execute("update order set ship_mobile=?  where order_id=?", ship_mobile,	orderid);
					return true;
			}
			if(ship_zip !=null && !StringUtil.isEmpty(ship_zip)){
				this.baseDaoSupport.execute("update order set ship_zip=?  where order_id=?", ship_zip,	orderid);
					return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	@Override
	public void updatePayMethod(int orderid, int payid, String paytype,String payname) {
		
		this.baseDaoSupport.execute("update order set payment_id=?,payment_type=?,payment_name=? where order_id=?",payid,paytype,payname,orderid);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.javashop.core.service.IOrderManager#checkProInOrder(int)
	 */
	@Override
	public boolean checkProInOrder(int productid) {
		String sql ="select count(0) from order_items where product_id=?";
		return this.baseDaoSupport.queryForInt(sql, productid)>0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.javashop.core.service.IOrderManager#checkGoodsInOrder(int)
	 */
	@Override
	public boolean checkGoodsInOrder(int goodsid) {
		String sql ="select count(0) from order_items where goods_id=?";
		return this.baseDaoSupport.queryForInt(sql, goodsid)>0;
	}
	
	@Override
	public List listByOrderIds(Integer[] orderids,String order) {
		try {
			StringBuffer sql = new StringBuffer("select * from es_order where disabled=0 ");
			
			if(orderids!=null && orderids.length>0)
				sql.append(" and order_id in ("+StringUtil.arrayToString(orderids, ",")+")");
			
			if(StringUtil.isEmpty(order)){
				order="create_time desc";
			}
			sql.append(" order by  "+order);
			return  this.daoSupport.queryForList(sql.toString(), Order.class);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public Page list(int pageNo, int pageSize, int disabled,  String order) {

		StringBuffer sql = new StringBuffer( "select * from es_order where disabled=? ");
		
		AdminUser adminUser = UserConext.getCurrentAdminUser();
		if (adminUser.getFounder() != 1) { // 非超级管理员加过滤条件
			boolean isShiper = permissionManager.checkHaveAuth(PermissionConfig.getAuthId("depot_ship")); //检测是否是发货员
			boolean haveAllo = this.permissionManager
					.checkHaveAuth(PermissionConfig.getAuthId("allocation")); // 配货下达权限
			boolean haveOrder = this.permissionManager
					.checkHaveAuth(PermissionConfig.getAuthId("order"));// 订单管理员权限
			if (isShiper && !haveAllo && !haveOrder) {
				DepotUser depotUser = (DepotUser) adminUser;
				int depotid = depotUser.getDepotid();
				sql.append(" and depotid=" + depotid);
			}
		}

		order = StringUtil.isEmpty(order) ? "order_id desc" : order;
		sql.append(" order by " + order);
		Page webpage = this.baseDaoSupport.queryForPage(sql.toString(), pageNo,pageSize, disabled);
		return webpage;
	}

	public Integer orderStatusNum(Integer status) {
		Member member = UserConext.getCurrentMember();
		String sql = "select count(0) from es_order where status =? and member_id=?";
		return this.baseDaoSupport.queryForInt(sql, status,member.getMember_id());
	}
	
	
	public Page list(int pageNo, int pageSize, int status, int depotid,
			String order) {
		order = StringUtil.isEmpty(order) ? "order_id desc" : order;
		String sql = "select * from order where disabled=0 and status="
				+ status;
		if (depotid > 0) {
			sql += " and depotid=" + depotid;
		}
		sql += " order by " + order;
		Page page = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize,Order.class);
		return page;
	}
	
	@Transactional(propagation = Propagation.REQUIRED) 
	@Override
	public Page listOrder(Map map, int page, int pageSize, String other,String order) {
		this.cancelOrder();
		String sql = createTempSql(map, other,order);
		Page webPage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
		return webPage;
	}
	
	/**
	 * 检查订单是否过期，若已过期，将其状态置为取消  
	 * 添加人：DMRain 2015-12-08
	 */
	private void cancelOrder(){
		String sql = "select * from es_order where status = 0 and pay_status = 0 and ship_status = 2 and payment_type != 'cod'";
		List<Map> list = this.daoSupport.queryForList(sql);
		
		OrderLog orderLog = new OrderLog();
		if(list != null){
			for(Map order : list){
				long createTime = (Long) order.get("create_time");
				long nowTime = DateUtil.getDateline();
				if((nowTime - createTime) > 259200){
					Integer order_id = (Integer) order.get("order_id");
					this.daoSupport.execute("update es_order set status = 8,cancel_reason = '订单过期，系统自动将其取消' where order_id = ?", order_id);
					
					//添加订单日志
					orderLog.setOrder_id(order_id);
					orderLog.setOp_name("平台系统");
					orderLog.setMessage("订单过期，系统自动将其取消");
					this.addLog(orderLog);
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private String  createTempSql(Map map,String other,String order){
		
		Integer stype = (Integer) map.get("stype");
		String keyword = (String) map.get("keyword");
		String orderstate =  (String) map.get("order_state");//订单状态
		String start_time = (String) map.get("start_time");
		String end_time = (String) map.get("end_time");
		Integer status = (Integer) map.get("status");
		String sn = (String) map.get("sn");
		String ship_name = (String) map.get("ship_name");
		Integer paystatus = (Integer) map.get("paystatus");
		Integer shipstatus = (Integer) map.get("shipstatus");
		Integer shipping_type = (Integer) map.get("shipping_type");
		Integer payment_id = (Integer) map.get("payment_id");
		Integer depotid = (Integer) map.get("depotid");
		String complete = (String) map.get("complete");
		
		StringBuffer sql =new StringBuffer();
		sql.append("select * from order where disabled=0 ");
		
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
			sql.append(" and ship_name like '"+ship_name+"'");
		}
		
		if(paystatus!=null){
			sql.append(" and pay_status="+paystatus);
		}
		
		if(shipstatus!=null){
			sql.append(" and ship_status="+shipstatus);
		}
		
		if(shipping_type!=null){
			sql.append(" and shipping_id="+shipping_type);
		}
		
		if(payment_id!=null){
			sql.append(" and payment_id="+payment_id);
		}
		
		if (depotid!=null && depotid > 0) {
			sql.append(" and depotid=" + depotid);
		}
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = com.enation.framework.util.DateUtil.getDateline(start_time+" 00:00:00","yyyy-MM-dd HH:mm:ss");
			sql.append(" and create_time>"+stime);
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = com.enation.framework.util.DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql.append(" and create_time<"+etime);
		}
		if( !StringUtil.isEmpty(orderstate)){
			if(orderstate.equals("wait_ship") ){ //对待发货的处理
				sql.append(" and ( ( payment_type!='cod'  and  status="+OrderStatus.ORDER_PAY_CONFIRM +") ");//非货到付款的，要已结算才能发货
				sql.append(" or ( payment_type='cod' and  status="+OrderStatus.ORDER_NOT_PAY +")) ");//货到付款的，新订单（已确认的）就可以发货
			}else if(orderstate.equals("wait_pay") ){
				sql.append(" and ( ( payment_type!='cod' and  status="+OrderStatus.ORDER_NOT_PAY +") ");//非货到付款的，未付款状态的可以结算
				sql.append(" or (   status!="+OrderStatus.ORDER_NOT_PAY+"  and  pay_status!="+OrderStatus.PAY_CONFIRM +")" ); 
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
		
		sql.append(" ORDER BY "+other+" "+order);
		
		//System.out.println(sql.toString());
		return sql.toString();
	}
	public void saveDepot(int orderid, int depotid) {
		this.orderPluginBundle.onOrderChangeDepot(this.get(orderid), depotid,this.listGoodsItems(orderid));
		this.daoSupport.execute("update es_order set depotid=?  where order_id=?", depotid,orderid);
	}
	public void savePayType(int orderid, int paytypeid) {
		PayCfg cfg  =  this.paymentManager.get(paytypeid);
		String typename =cfg.getName();
		String paytype= cfg.getType();
		this.daoSupport.execute("update es_order set payment_id=?,payment_name=?,payment_type=? where order_id=?", paytypeid,typename,paytype,orderid);
	}
	public void saveShipType(int orderid, int shiptypeid) {
		String typename = this.dlyTypeManager.getDlyTypeById(shiptypeid).getName();
		this.daoSupport.execute("update es_order set shipping_id=?,shipping_type=? where order_id=?", shiptypeid,typename,orderid);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(Order order) {
		this.baseDaoSupport.insert("es_order", order);
	}
	@Override
	public void saveAddr(int orderId,int ship_provinceid,int ship_cityid,int ship_regionid,String Attr){
		this.daoSupport.execute("update es_order set ship_provinceid=?,ship_cityid=?,ship_regionid=?,shipping_area=? where order_id=?",ship_provinceid,ship_cityid,ship_regionid,Attr,orderId);
	}
	@Override
	public Integer getOrderGoodsNum(int order_id) {
		String sql = "select count(0) from order_items where order_id =?";
		return this.baseDaoSupport.queryForInt(sql, order_id);
	}
	@Override
	public List getOrderItemDetail(int item_id) {
		String sql="SELECT c.*,g.mktprice from es_order_item_child c INNER JOIN es_goods g ON g.goods_id=c.goodsid where itemid=? ORDER BY c.goodsid";
		return this.baseDaoSupport.queryForList(sql, item_id);
	}

	
	@Override
	public boolean getOrderByMemberid(String sn, Integer memberid) {
		boolean flag=false;
		String sql ="select count(0) from es_order where member_id=? and sn=?";
		Integer num = this.daoSupport.queryForInt(sql, memberid,sn);
		if(num==1){
			flag=true;
		}
		return flag;
	}

	
	@Override
	public List getInsurancesOrderReadyInfo(String productId) {
		String sql = "SELECT t1.totalgain,t1.insureestimatedfee FROM es_carinfo t1,es_product t2 WHERE  t2.product_id = ? and  t2.`name` = t1.carplate ";
		return this.daoSupport.queryForList(sql, productId);
	}
	
	@Override
	public JSONObject checkCreateOrderStatus(String carplate) {
		JSONObject obj = new JSONObject();
		try {
			boolean flag = true;
			String message = "";
			String sql = "select count(*) from es_order where carplate=? and order_type in ("+ OrderType.ZA_INSURANCE +","+ OrderType.STORE_INSURANCE +") AND parent_id IS NOT NULL and status IN(0,9) and pay_status=0";
			int count = daoSupport.queryForInt(sql, carplate);
			if(count > 0){
				flag = false;
				message = "您有未支付的保险订单，请不要重复下单";
				obj.put("flag", flag);
				obj.put("message", message);
				return obj;
			}
			sql = "select count(*) from es_order where carplate=? and order_type in ("+ OrderType.ZA_INSURANCE +","+ OrderType.STORE_INSURANCE +") AND parent_id IS NOT NULL and pay_status=2";
			count = daoSupport.queryForInt(sql, carplate);
			if(count > 0){
				sql = "select insurenextbuytime from es_carinfo where carplate=?";
				long insurenextbuytime = daoSupport.queryForLong(sql, carplate);
				Calendar time = Calendar.getInstance();
				time.setTimeInMillis(insurenextbuytime);
				time.add(Calendar.MONTH, -3);
				if(new Date().getTime() < time.getTimeInMillis()){
					flag = false;
					message = "您之前购买的保险还未到期，请在保险到期之前三个月内购买下次保险";
					obj.put("flag", flag);
					obj.put("message", message);
					return obj;
				}
			}
			obj.put("flag", flag);
			obj.put("message", message);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}
	
	@Override
	public void updateInsureAndRepairInfo(Order order, CarInfoManager carInfoManager) {
		try {
			if(order.getOrder_type() == OrderType.ZA_INSURANCE || order.getOrder_type() == OrderType.STORE_INSURANCE){//保险订单支付完成，更新下次购买保险时间 年份加1,且更新本次获得的赠送的保养币到车辆信息中
				//更新下次保险购买时间
				JSONObject obj = JSONObject.fromObject(carInfoManager.getCarInfoByCarplate(order.getCarplate()).get(0));
				long insurenextbuytime = obj.getLong("insurenextbuytime");
				Calendar time = Calendar.getInstance();
				time.setTime(new Date(insurenextbuytime));
				time.add(Calendar.YEAR, 1);
				JSONObject carinfo = new JSONObject();
				carinfo.put("carplate", obj.get("carplate"));
				carinfo.put("carowner", obj.get("carowner"));
				carinfo.put("insurenextbuytime", time.getTimeInMillis());
				carInfoManager.inputCarInfo(carinfo.toString());
				
				//更新车辆在店铺的保养币
				JSONObject order_item = JSONArray.fromObject(order.getItems_json()).getJSONObject(0);
				int spec_id = order_item.getInt("insure_repair_specid");
				String sql = "SELECT t1.store_id, t1.repair_coin FROM es_insure_order_spec t1, es_insurance_company_info t2 WHERE t1.insure_company_id=t2.id AND t1.spec_id=?";
				List insure_spec_list = daoSupport.queryForList(sql, spec_id);
				if(insure_spec_list.size() > 0){
					JSONObject insure_spec_obj = JSONObject.fromObject(insure_spec_list.get(0));
					int store_id = insure_spec_obj.getInt("store_id");
					long repair_coin = insure_spec_obj.getLong("repair_coin");
					
					sql = "SELECT repair_total_coin FROM es_car_repair_coin WHERE store_id=? AND carplate=?";
					List coinList = daoSupport.queryForList(sql, store_id, order.getCarplate());
					
					if(coinList.size() > 0){
						JSONObject coinObj = JSONObject.fromObject(coinList.get(0));
						long repair_total_coin = coinObj.getLong("repair_total_coin");
						repair_total_coin += repair_coin;
						sql = "update es_car_repair_coin set repair_total_coin=? WHERE store_id=? AND carplate=?";
						daoSupport.execute(sql, repair_total_coin, store_id, order.getCarplate());
					}else{
						sql = "insert into es_car_repair_coin set store_id=?, carplate=?, repair_total_coin=?, create_time=?";
						daoSupport.execute(sql, store_id, order.getCarplate(), repair_coin, new Date().getTime());
					}
				}
			}
			if(order.getOrder_type() == OrderType.REPAIR){//更新下单店铺保养工位、扣除车辆在店铺的保养币
				//更新下单店铺保养工位
				JSONObject specObj = JSONArray.fromObject(order.getItems_json()).getJSONObject(0);
				int spec_id = specObj.getInt("insure_repair_specid");
				String sql = "SELECT store_id, order_date, time_region_id, carplate FROM es_repair_order_spec WHERE spec_id=?";
				List resultList = daoSupport.queryForList(sql, spec_id);
				if(resultList.size() > 0){
					JSONObject repairObj = JSONObject.fromObject(resultList.get(0));
					sql = "UPDATE es_store_repair_timeregion SET order_status=1 WHERE store_id=? AND order_date=? AND time_region_id=? AND carplate=?";
					daoSupport.execute(sql, repairObj.getInt("store_id"), repairObj.getLong("order_date"), repairObj.getInt("time_region_id"), repairObj.getString("carplate"));
					
					//支付成功，扣除车辆在店铺的保养币
					if(order.getIs_use_repair_coin() == 1 && order.getRepair_coin() > 0){
						double repair_coin = order.getRepair_coin();
						sql = "UPDATE es_car_repair_coin SET repair_total_coin=repair_total_coin-? WHERE store_id=? AND carplate=?";
						daoSupport.execute(sql, repair_coin, repairObj.getInt("store_id"), repairObj.getString("carplate"));
						
						//记录保养币使用历史记录
						sql = "INSERT INTO es_car_repair_coin_history SET TYPE=?, query_id=?, store_id=?, carplate=?, reward=?, timeline=?, detail=?";
						String detail = "订单号："+order.getSn();
						daoSupport.execute(sql, CarInfo.ORDER_REPAIR_COIN_USE, order.getOrder_id(), repairObj.getInt("store_id"), order.getCarplate(), repair_coin, new Date().getTime(), detail);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void updateIncomeUseHistory(Order order) {
		try {
			//更新订单类型为 0 3 普通商品  2 保养  的订单的收益使用记录    1保险目前不支持使用收益
			int isUseGain = order.getIsUseGain();
			String detail = "";
			if(isUseGain == 1 && order.getGain() > 0){//订单有使用收益，记录到收益记录表
				String sql = "INSERT INTO es_car_gain_history SET TYPE=?, query_id=?, carplate=?, reward=?, timeline=?, detail=?";
				detail = "订单号："+order.getSn();
				daoSupport.execute(sql, CarInfo.ORDER_INCOME_USE, order.getOrder_id(), order.getCarplate(), order.getGain(), new Date().getTime(), detail);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
	}
	
	@Override
	public void updateGainById(Double gain,int order_id) {
		this.daoSupport.execute("update es_order set gain = ?,isUseGain=1  where order_id = ?", gain,order_id);
		
	}
	
	@Override
	public InsureRepairSpec getInsureOrRepairSpec(int type, String carplate, Object... args) {
		InsureRepairSpec insureOrRepairSpec = new InsureRepairSpec();
		if(type == 1){//保险
			insureOrRepairSpec.setApplicant(args[0].toString());
			insureOrRepairSpec.setApplicant_id(args[1].toString());
			insureOrRepairSpec.setInsure_starttime((Long)args[2]);
			insureOrRepairSpec.setInsure_endtime((Long)args[3]);
			String sql = "select insureestimatedfee, insureestimatedmaxgain from es_carinfo where carplate=?";
			JSONObject obj = JSONObject.fromObject(daoSupport.queryForList(sql, carplate).get(0));
			insureOrRepairSpec.setPrice(obj.getDouble("insureestimatedfee"));
			insureOrRepairSpec.setRewards_limit(obj.getDouble("insureestimatedmaxgain"));
		}
		if(type == 2){//保养
			int time_region_id = (Integer)args[0];
			insureOrRepairSpec.setTime_region_id(time_region_id);
			String sql = "SELECT s.`discountcontract`, s.`discountnoncontract`, c.`repair4sstoreid`, c.`repairestimatedfee` FROM es_store s, es_carinfo c WHERE s.store_id=c.`repair4sstoreid` and c.carplate=?";
			JSONObject obj = JSONObject.fromObject(daoSupport.queryForList(sql, carplate).get(0));
			double price = obj.getDouble("repairestimatedfee");
			int store_id = (Integer)args[1];
			if(store_id == obj.getInt("repair4sstoreid")){
				insureOrRepairSpec.setRewards_limit(price * (1 - obj.getDouble("discountcontract")));
			}else{
				insureOrRepairSpec.setRewards_limit(price * (1 - obj.getDouble("discountnoncontract")));
			}
			price = (time_region_id == 1 || time_region_id == 4) ? price * 0.8 : price;
			insureOrRepairSpec.setPrice(price);
			long order_time = (Long)args[2];
			insureOrRepairSpec.setOrder_time(order_time);
		}
		daoSupport.insert("es_insure_repair_spec", insureOrRepairSpec);
		int spec_id = daoSupport.getLastId("es_insure_repair_spec");
		insureOrRepairSpec.setSpec_id(spec_id);
		return insureOrRepairSpec;
	}
	
	public InsureRepairSpec getInsureOrRepairSpecById(int spec_id){
		try {
			String sql = "select * from es_insure_repair_spec where spec_id=?";
			InsureRepairSpec spec = (InsureRepairSpec) daoSupport.queryForObject(sql, InsureRepairSpec.class, spec_id);
			return spec;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Order updateRecordStatus(Order order, int record_update_status) {
		try {
			String sql = "update es_order set record_update_status=? where order_id=?";
			daoSupport.execute(sql, record_update_status, order.getStatus());
			order.setRecord_update_status(1);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return order;
	}
	
	@Override
	public JSONObject isOrder(String store_id, Long order_date, String time_region_id, String carplate) {
		JSONObject obj = new JSONObject();
		try {
			//当天只能预约一次
			String sql = "SELECT COUNT(*) FROM es_repair_order_spec t1, es_order t2 WHERE t1.order_id=t2.order_id AND t1.store_id=? AND t1.order_date=? AND t1.carplate=? AND t2.status<>?";
			int count = daoSupport.queryForInt(sql, store_id, order_date, carplate, OrderStatus.ORDER_CANCELLATION);
			if(count > 0){
				obj.put("result", 0);
				obj.put("message", "您当天已有预约，不能再次预约");
				return obj;
			}
			obj.put("result", 1);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public InsureRepairSpec addInsureOrderRecord(Map<Integer, JSONObject> insureTypePriceMap, String store_id, String insure_company_id, 
			                                     String select_insure_typeids, String carplate, String applicant, String applicant_id,
			                                     long insure_starttime, long insure_endtime, String params) {
		try {
			//计算订单险种价格、可用收益额度
			DecimalFormat df = new DecimalFormat("0.00");
			double price = 0.0;
			String[] insure_type_idArray = select_insure_typeids.split(",");
			for(int i=0; i<insure_type_idArray.length; i++){
				price += insureTypePriceMap.get(Integer.valueOf(insure_type_idArray[i])).getDouble("insure_price");
			}
			double rewards_limit = price * 0.15;
			
			InsureRepairSpec insure_spec = new InsureRepairSpec();
			insure_spec.setStore_id(Integer.valueOf(store_id));
			insure_spec.setCarplate(carplate);
			insure_spec.setInsure_company_id(Integer.valueOf(insure_company_id));
			insure_spec.setSelect_insure_typeids(select_insure_typeids);
			insure_spec.setInsure_params(params);
			insure_spec.setApplicant(applicant);
			insure_spec.setApplicant_id(applicant_id);
			insure_spec.setInsure_starttime(insure_starttime);
			insure_spec.setInsure_endtime(insure_endtime);
			insure_spec.setPrice(Double.valueOf(df.format(price)));;
			insure_spec.setRewards_limit(Double.valueOf(df.format(rewards_limit)));
			
			daoSupport.insert("es_insure_order_spec", insure_spec);
			insure_spec.setSpec_id(daoSupport.getLastId("es_insure_order_spec"));
			
			return insure_spec;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public InsureRepairSpec addRepairOrderTimeRegionRecord(String store_id, String time_region_id, Long order_date, String carplate, String repair_item_ids) {
		try {
			/**
			 * 修改店铺保养预约记录 order_status 0 未预约成功  1 预约成功
			 */
			String sql = "SELECT COUNT(*) FROM es_store_repair_timeregion WHERE store_id=? AND time_region_id=? AND order_date=? AND carplate=?";
			int count = daoSupport.queryForInt(sql, store_id, time_region_id, order_date, carplate);
			if(count > 0){
				sql = "UPDATE es_store_repair_timeregion SET station=station+1 WHERE store_id=? AND time_region_id=? AND order_date=? AND carplate=?";
			}else{
				sql = "INSERT INTO es_store_repair_timeregion SET store_id=?, time_region_id=?, order_date=?, carplate=?, station=1, order_status=0";
			}
			daoSupport.execute(sql, store_id, time_region_id, order_date, carplate);
			
			/**
			 * 计算订单保养价格和可使用收益额度, 保存预约订单信息
			 */
			
			//计算订单保养价格和可使用收益额度
			sql = "SELECT t1.carmodelid, t1.repair4sstoreid, t1.repairlastmile, t2.repairinterval, t3.`discountcontract`, t3.`discountnoncontract`, t3.attr "
			    + "FROM es_carinfo t1, es_carmodels t2, es_store t3 WHERE t1.carmodelid=t2.id AND t1.repair4sstoreid=t3.store_id AND t1.carplate=?";
			List list = daoSupport.queryForList(sql, carplate);
			JSONObject infoObj = JSONObject.fromObject(list.get(0));
			String repair_source = infoObj.getString("attr");
			int carmodelid = infoObj.getInt("carmodelid");
			int repair4sstoreid = infoObj.getInt("repair4sstoreid");
			long repairlastmile = infoObj.getInt("repairlastmile");//上次保养里程
			long repairinterval = infoObj.getInt("repairinterval");//保养间隔里程
			
			//计算本次保养里程
			long nextmile = ((repairlastmile % repairinterval) > (repairinterval / 2)) ? ((repairlastmile/repairinterval) + 2)*repairinterval : ((repairlastmile/repairinterval) + 1)*repairinterval;
		
			double discountcontract = infoObj.getDouble("discountcontract");
			double discountnoncontract = infoObj.getDouble("discountnoncontract");
			double discount = 0.0;
			if(repair4sstoreid == Integer.valueOf(store_id)){//签约折扣
				discount = discountcontract;
			}else{//非签约折扣
				discount = discountnoncontract;
			}

			//计算折扣后的价格 price * 签约/非签约折扣
			sql = "SELECT SUM(item_price+repair_price) * "+ discount +" price, SUM(item_price) * "+ discount +" items_price, SUM(repair_price) * "+ discount +" repair_total_price FROM es_store_repairitem WHERE carmodel_id="+ carmodelid +" AND store_id="+ store_id +" AND repair_item_id IN ("+ repair_item_ids +")";
			List priceList = daoSupport.queryForList(sql);
			double price = 0.0;
			double items_price = 0.0;
			double repair_total_price = 0.0;
			if(priceList.size() > 0){
				JSONObject priceObj = JSONObject.fromObject(priceList.get(0));
				price = priceObj.getDouble("price");
				items_price = priceObj.getDouble("items_price");
				repair_total_price = priceObj.getDouble("repair_total_price");
			}
			double rewards_limit = price * (1 - discount);//计算可用收益额度
			sql = "SELECT "+ items_price +"+"+ repair_total_price +" * t.ratio price FROM (SELECT * FROM es_repair_timeregion WHERE time_region_id="+ time_region_id +") t";
			price = StringUtil.isEmpty(daoSupport.queryForString(sql)) ? 0 : Double.valueOf(daoSupport.queryForString(sql));//计算时间段折扣之后的价格 item_price + repair_price * 时间段折扣费率

			DecimalFormat df = new DecimalFormat("0.00");
			InsureRepairSpec repair_spec = new InsureRepairSpec();
			repair_spec.setStore_id(Integer.valueOf(store_id));
			repair_spec.setRepair_item_ids(repair_item_ids);
			repair_spec.setOrder_date(order_date);
			repair_spec.setTime_region_id(Integer.valueOf(time_region_id));
			repair_spec.setCarplate(carplate);
			repair_spec.setPrice(Double.valueOf(df.format(price)));
			repair_spec.setRepair_price(price);
			repair_spec.setRewards_limit(Double.valueOf(df.format(rewards_limit)));
			repair_spec.setRepair_mile(nextmile);
			repair_spec.setRepair_source(repair_source);
			
			daoSupport.insert("es_repair_order_spec", repair_spec);
			int spec_id = daoSupport.getLastId("es_repair_order_spec");
			repair_spec.setSpec_id(spec_id);
			
			return repair_spec;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
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
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveLogistics(String order_id, String logi_id, String ship_no) {
		try{
			Logi logi = null;
			if(!logi_id.equals("0")){
				logi = logiManager.getLogiById(Integer.parseInt(logi_id));
				daoSupport.execute("update es_order set  logi_id = ?,logi_name=?,logi_code=?,ship_no=? where order_id = ?", logi_id,logi.getName(),logi.getCode(),ship_no,order_id);
			}else{
				daoSupport.execute("update es_order set  logi_id = ?,logi_name=?,logi_code=?,ship_no=? where order_id = ?", null,null,null,null,order_id);
			}
			Order order = get(Integer.parseInt(order_id));
			if(order.getStatus() == OrderStatus.ORDER_PAY_CONFIRM &&//上传物流信息时，如果是已支付状态，上传后变成已发货
					( order.getOrder_type() == OrderType.GOODS || order.getOrder_type() == OrderType.ZA_GOODS)){
				daoSupport.execute("update es_order o set o.status = ?,o.ship_time = ? where o.status = ? and o.order_id = ?", OrderStatus.ORDER_SHIP,DateUtil.getCurrentTime(),OrderStatus.ORDER_PAY_CONFIRM,order_id);
			}
			
			if(order.getStatus() == OrderStatus.ORDER_SERVECE &&//上传物流信息时，如果是保险订单且是保单生成状态，上传后变成6保单寄出
					( order.getOrder_type() == OrderType.STORE_INSURANCE || order.getOrder_type() == OrderType.ZA_INSURANCE)){
				daoSupport.execute("update es_order o set o.status = ?,o.ship_time = ? where o.status = ? and o.order_id = ?", OrderStatus.ORDER_APPRAISE,DateUtil.getCurrentTime(),OrderStatus.ORDER_SERVECE,order_id);
			}
		}catch (Exception e){
			throw e;
		}
	}
	public ProductManager getProductManager() {
		return productManager;
	}
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}
	public CartPluginBundle getCartPluginBundle() {
		return cartPluginBundle;
	}
	public void setCartPluginBundle(CartPluginBundle cartPluginBundle) {
		this.cartPluginBundle = cartPluginBundle;
	}
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}
	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}
	public ILogiManager getLogiManager() {
		return logiManager;
	}
	public void setLogiManager(ILogiManager logiManager) {
		this.logiManager = logiManager;
	}

}
	
	
