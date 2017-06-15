package com.enation.app.shop.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.model.PaymentLog;
import com.enation.app.shop.core.model.PaymentLogType;
import com.enation.app.shop.core.model.SellBackChild;
import com.enation.app.shop.core.model.SellBackGoodsList;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.model.SellBackLog;
import com.enation.app.shop.core.model.SellBackStatus;
import com.enation.app.shop.core.plugin.goods.GoodsStorePluginBundle;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IGoodsStoreManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IOrderMetaManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SellBackManager extends BaseSupport implements ISellBackManager {

	private IOrderManager orderManager;
	private IMemberManager memberManager;
	private IOrderMetaManager orderMetaManager;
	private IMemberPointManger memberPointManger;
	private IMemberLvManager memberLvManager;
	private IGoodsStoreManager goodsStoreManager;
	private OrderPluginBundle orderPluginBundle;
	private IDepotManager depotManager;
	private IProductManager productManager;
	
	private GoodsStorePluginBundle goodsStorePluginBundle;
	

	/**
	 * 退货单列表
	 */
	public Page list(int page, int pageSize,Integer status) {
		String sql = "select * from es_sellback_list where tradestatus=? order by id desc ";
		Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize,status);
		return webpage;
	}
	public Page list(Integer member_id,int page, int pageSize) {
		String sql = "select * from es_sellback_list where member_id=? order by id desc ";
		Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize,member_id);
		return webpage;
	}
	@Override
	public void editStatus(Integer status, Integer id,String seller_remark) {
		String sql = "update es_sellback_list set tradestatus=?,seller_remark=? where id=?";
		this.daoSupport.execute(sql, status,seller_remark,id);
		
		// 2015-11-04 create by sylow  退货审核后 修改订单状态
		String selectSql = "SELECT ordersn FROM es_sellback_list WHERE id = " + id;
		String orderSn = this.daoSupport.queryForString(selectSql);
		Order order = this.orderManager.get(orderSn);
		
		//如果退货审核不通过
		if (status.intValue() == SellBackStatus.cancel.getValue()) {
			order.setStatus(OrderStatus.ORDER_RETURN_REFUSE);
		} else {
			order.setStatus(OrderStatus.ORDER_RETURNED);
		}
		
		this.orderManager.edit(order);
		this.saveLog(id, SellBackStatus.valueOf(status), "审核退货申请");
	}
	/**
	 * 退货搜索
	 */
	public Page search(String keyword, int page, int pageSize) {
		String sql = "select * from es_sellback_list";
		String where = "";
		if (!StringUtil.isEmpty(keyword)) {
			where = " where tradeno like '%" + keyword
					+ "%' or ordersn like '%" + keyword + "%' order by id desc";
		}
		sql = sql + where;
		Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}

	/**
	 * 退货单详细
	 */
	public SellBackList get(String tradeno) {
		String sql = "select * from es_sellback_list where tradeno=?";
		SellBackList result = (SellBackList) this.baseDaoSupport
				.queryForObject(sql, SellBackList.class, tradeno);
		return result;
	}

	public SellBackList get(Integer id) {
		String sql = "select * from es_sellback_list where id=?";
		SellBackList result = (SellBackList) this.baseDaoSupport.queryForObject(sql, SellBackList.class, id);
		return result;
	}

	/**
	 * 保存退货单
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer save(SellBackList data) {
		
		Integer id=0;
		if (data.getId() != null) {
			this.baseDaoSupport.update("es_sellback_list", data,"id=" + data.getId());
			id = data.getId();
		} else {
			this.baseDaoSupport.insert("es_sellback_list", data);
			id = this.baseDaoSupport.getLastId("es_sellback_list");
			//记录退货日志
			if(UserConext.getCurrentAdminUser()==null){
				this.saveLog(id, SellBackStatus.apply, "申请退货","会员："+UserConext.getCurrentMember().getName());
			}else{
				this.saveLog(id, SellBackStatus.apply, "申请退货",UserConext.getCurrentAdminUser().getUsername());
			}
		}
		if (data.getTradestatus() ==0){
			orderPluginBundle.onOrderSellback(data);
			Integer orderid = this.orderManager.get(data.getOrdersn()).getOrder_id();
			baseDaoSupport.execute("update order set status=? where order_id=?",OrderStatus.ORDER_RETURN_APPLY, orderid);
		}
		if (data.getTradestatus() == 1) { // 申请退货
			Integer orderid = this.orderManager.get(data.getOrdersn()).getOrder_id();
			this.log(orderid, "订单申请退货，金额[" + data.getAlltotal_pay() + "]");
		}

		if (data.getTradestatus() == 2) { // 已入库
			syncStore(data);
		}

		if (data.getTradestatus() == 4) { // 取消退货
			Integer orderid = this.orderManager.get(data.getOrdersn()).getOrder_id();
			baseDaoSupport.execute("update order set status=? where order_id=?",OrderStatus.ORDER_SERVECE/*ORDER_SHIP*/, orderid);
			this.log(orderid, "取消退货");
		}

		return id;
	}

	protected void updateMemberLv(Member member, int point) {
		MemberLv lv = this.memberLvManager
				.getByPoint(member.getPoint() + point);
		if (lv != null) {
			if ((member.getLv_id() == null || lv.getLv_id().intValue() < member
					.getLv_id().intValue())) {
				this.memberManager.updateLv(member.getMember_id(),
						lv.getLv_id());
			}
		}
	}

	/**
	 * 应付结算
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void closePayable(int backid, String finance_remark, String logdetail,Double alltotal_pay) {

		SellBackList data = get(backid);
		data.setTradestatus(SellBackStatus.close_payable.getValue()); // 设置为已结算
		data.setFinance_remark(finance_remark); // 设置财务备注

		// 读取当前会员
		Member member = this.memberManager.get(data.getMember_id());
		Order order = this.orderManager.get(data.getOrdersn());
		Integer orderid = order.getOrder_id();

		/**
		 * ------------------------ 添加退款单 -------------------------
		 */

		// 添加退款单
		addPayable(member, alltotal_pay, 0.0, 0.0, order);

		// 保存退货日志
		this.saveLog(data.getId(), SellBackStatus.close_payable, logdetail);

		// 更新订单状态
		daoSupport.execute("update es_order set status=?,ship_status=?,pay_status=? where order_id=?",
						OrderStatus.ORDER_CANCEL_SHIP, OrderStatus.SHIP_CANCEL,
						OrderStatus.PAY_CANCEL, orderid);
		Map map=new HashMap();
		map.put("tradestatus", 3);
		map.put("alltotal_pay", alltotal_pay);
		map.put("finance_remark", finance_remark);
		daoSupport.update("es_sellback_list", map, "id="+backid);
		this.log(orderid, "订单退货，金额[" + alltotal_pay + "]");
		
		//如果退款金额大于 订单 金额，那么这里退还的佣金为 订单金额的百分比
		if(order.getGoods_amount()<alltotal_pay){
			alltotal_pay=order.getOrder_amount();
		}
		
		//退款触发事件金额为负值
		orderPluginBundle.confirm(orderid,-alltotal_pay); 
	}

	/**
	 * 添加退款单
	 * 
	 * @param member
	 * @param money
	 * @param credit
	 * @param mp
	 * @param order
	 */
	private void addPayable(Member member, Double money, Double credit,
			Double mp, Order order) {
		PaymentLog paymentLog = new PaymentLog();

		paymentLog.setMember_id(member.getMember_id());
		paymentLog.setPay_user(member.getUsername());
		paymentLog.setMoney(money);
		paymentLog.setCredit(credit);
		paymentLog.setMarket_point(mp);
		paymentLog.setPay_date(DateUtil.getDateline());
		paymentLog.setOrder_sn(order.getSn());
		paymentLog.setSn("");
		paymentLog.setPay_method(order.getPayment_name());
		paymentLog.setOrder_id(order.getOrder_id());
		paymentLog.setType(PaymentLogType.payable.getValue()); // 应收
		paymentLog.setStatus(1);// 已结算
		paymentLog.setCreate_time(System.currentTimeMillis());

		AdminUser adminUser = UserConext.getCurrentAdminUser();
		if (adminUser != null) {
			paymentLog.setAdmin_user(adminUser.getRealname() + "["
					+ adminUser.getUsername() + "]");
		} else if (member != null) {
			paymentLog.setAdmin_user(member.getName());
		}

		this.daoSupport.insert("es_payment_logs", paymentLog);
	}

	@Override
	public void saveLog(Integer recid, SellBackStatus status, String logdetail) {

		SellBackLog sellBackLog = new SellBackLog();

		sellBackLog.setRecid(recid);
		if ("".equals(logdetail)) {
			logdetail = status.getName();
		}
		sellBackLog.setLogdetail(logdetail);
		sellBackLog.setLogtime(DateUtil.getDateline());
		sellBackLog.setOperator(UserConext.getCurrentAdminUser().getUsername());
		this.daoSupport.insert("es_sellback_log", sellBackLog);
	}
	@Override
	public void saveLog(Integer recid, SellBackStatus status, String logdetail,String memeberName) {

		SellBackLog sellBackLog = new SellBackLog();

		sellBackLog.setRecid(recid);
		if ("".equals(logdetail)) {
			logdetail = status.getName();
		}
		sellBackLog.setLogdetail(logdetail);
		sellBackLog.setLogtime(DateUtil.getDateline());
		sellBackLog.setOperator(memeberName);
		this.daoSupport.insert("es_sellback_log", sellBackLog);
	}


	private void log(Integer order_id, String message) {
		AdminUser adminUser = UserConext.getCurrentAdminUser();
		OrderLog orderLog = new OrderLog();
		orderLog.setMessage(message);
		orderLog.setOp_id(adminUser.getUserid());
		orderLog.setOp_id(1);
		orderLog.setOp_name("test");
		orderLog.setOp_name(adminUser.getUsername());
		orderLog.setOp_time(System.currentTimeMillis());
		orderLog.setOrder_id(order_id);
		this.daoSupport.insert("es_order_log", orderLog);
	}

	/**
	 * 保存退货商品
	 */
	public Integer saveGoodsList(SellBackGoodsList data) {
		if (data.getId() == null) {
			this.daoSupport.insert("es_sellback_goodslist", data);
		} else {
			this.daoSupport.update("es_sellback_goodslist", data,"id=" + data.getId());
		}

		Integer id = this.baseDaoSupport.getLastId("sellback_goodslist");
		
		return id;
	}

	/**
	 * 获取退货商品详细
	 */
	public SellBackGoodsList getSellBackGoods(Integer recid, Integer goodsid) {
		String sql = "select * from es_sellback_goodslist where recid=? and goods_id=?";
		SellBackGoodsList result = (SellBackGoodsList) this.daoSupport.queryForObject(sql, SellBackGoodsList.class, recid, goodsid);
		return result;
	}

	/**
	 * 退货商品列表
	 */
	public List getGoodsList(Integer recid, String sn) {
		String sql = "select i.*,g.return_type,g.return_num,g.goods_id as goodsId,storage_num,goods_remark,g.ship_num,g.is_pack from es_order_items i " +
				"inner join (SELECT s.return_type, s.return_num, s.goods_id, s.storage_num, s.goods_remark, s.ship_num, s.price, g.is_pack FROM es_sellback_goodslist s LEFT JOIN es_goods g ON g.goods_id = s.goods_id where recid=?) g " +
				"on g.goods_id=i.goods_id where i.order_id in (select order_id from es_order where sn=?) order by item_id";
		List<Map> result = this.daoSupport.queryForList(sql,recid,sn);
		for(Map item : result){
			Object obj =  item.get("addon");
			if(obj != null){
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
		}
		return result;
	}

	public List getGoodsList(Integer recid) {
		return this.baseDaoSupport.queryForList("select s.*,g.name,g.is_pack from es_sellback_goodslist s inner join es_goods g on g.goods_id=s.goods_id where recid=?", recid);
	}

	/**
	 * 保存会员账户日志
	 * 
	 * @param log
	 */
	public void saveAccountLog(Map log) {
		this.daoSupport.insert("es_account_log", log);
	}

	/**
	 * 获取退货单id
	 */
	public Integer getRecid(String tradeno) {
		return this.daoSupport.queryForInt("select id from es_sellback_list where tradeno=?", tradeno);
	}

	/**
	 * 修改退货商品数量
	 */
	public void editGoodsNum(Map data) {
		Integer recid = (Integer) data.get("recid");
		Integer goods_id = (Integer) data.get("goods_id");
		this.daoSupport.update("es_sellback_goodslist", data, "recid=" + recid+" and goods_id=" + goods_id);

	}

	/**
	 * 修改入库货品数量
	 */
	public void editStorageNum(Integer recid, Integer goods_id, Integer num) {
		this.daoSupport.execute("update es_sellback_goodslist set storage_num=? where recid=? and goods_id=?", num,recid, goods_id);
	}
	
	/**
	 * 修改入库货品数量
	 */
	public void editChildStorageNum(Integer orderId,Integer parentId,Integer goods_id, Integer num) {
		this.daoSupport.execute("update es_sellback_child set storage_num=? where order_id=? and goods_id=? and parent_id = ?", num,orderId, goods_id,parentId);
	}

	/**
	 * 删除商品
	 */
	public void delGoods(Integer recid, Integer goodsid) {
		this.daoSupport.execute("delete from es_sellback_goodslist where recid=? and goods_id=?",recid, goodsid);
	}

	/**
	 * 操作日志
	 */
	public List sellBackLogList(Integer recid) {
		return this.daoSupport.queryForList("select * from es_sellback_log where recid=? order by id desc",recid);
	}

	
	@Override
	public void syncStore(SellBackList sellback) {
		int depotid = sellback.getDepotid();
		List<Map> goodsList = this.getGoodsList(sellback.getId());
		
		for (Map goods : goodsList) {
			Integer isPack = (Integer) goods.get("is_pack");
			Integer goodsid= (Integer) goods.get("goods_id");
			if(isPack == null){
				isPack = 0;
			}
			/**
			 * 20150716 冯兴隆  增加判断整箱
			 */
			if(isPack != 1){
				
				Integer storage_num = (Integer) goods.get("storage_num");
				Integer productid = (Integer) goods.get("product_id");
				int goodsId = Integer.parseInt(goods.get("goods_id").toString());
				goodsStoreManager.increaseStroe(goodsId, productid, depotid, storage_num);
			}else{
				//IOrderManager orderManager = new OrderManager();
				Order order = orderManager.get(sellback.getOrdersn());
				List<Map> list = this.getSellbackChilds(order.getOrder_id(), goodsid);
				for(Map map : list){
					int goodsId = Integer.parseInt(map.get("goods_id").toString());
					int storageNum = Integer.parseInt(map.get("storage_num").toString());
					int productId = Integer.parseInt(map.get("product_id").toString());
					goodsStoreManager.increaseStroe(goodsId, productId, depotid, storageNum);
				}
				
			}
			
		}
	}

	public List getProduct(int goodsid) {
		String sql = "select product_id,goods_id from es_product p where goods_id=?";
		List list = this.baseDaoSupport.queryForList(sql,goodsid);
		return list;
	}
	
	
	@Override
	public int searchSn(String sn) {
		String sql = "select id from es_sellback_list where ordersn="+sn;
		List<Map> list = this.baseDaoSupport.queryForList(sql);
		int num = 0;
		if(list.size() > 0){
			num = Integer.parseInt(list.get(0).get("id").toString());
		}
		return num;
	}
	@Override
	public void saveSellbackChild(int orderId,int goodsId,int parentId,int returnNum){
		SellBackChild sellBackChild = new SellBackChild();
		sellBackChild.setGoods_id(goodsId);
		sellBackChild.setOrder_id(orderId);
		sellBackChild.setParent_id(parentId);
		sellBackChild.setReturn_num(returnNum);
		sellBackChild.setStorage_num(0);
		this.baseDaoSupport.insert("es_sellback_child", sellBackChild);
	}
	
	@Override
	public void updateSellbackChild(int orderId,int goodsId,int returnNum,int storageNum){
		SellBackChild sellBackChild = new SellBackChild();
		sellBackChild.setGoods_id(goodsId);
		sellBackChild.setOrder_id(orderId);
		sellBackChild.setStorage_num(storageNum);
		sellBackChild.setReturn_num(returnNum);
		this.baseDaoSupport.update("es_sellback_child", sellBackChild,
				" es_sellback_child.order_id = " + orderId
						+ " AND es_sellback_child.goods_id = " + goodsId);
		
		Map goods = this.getGoods(goodsId);
		//响应库存退货入库事件 2015-07-23  冯兴隆
		this.goodsStorePluginBundle.onStockReturn(goods);
		//响应库存变更事件 2015-07-23  冯兴隆
		this.goodsStorePluginBundle.onStockChange(goods);
	}
	
	private Map getGoods(int goodsid) {
		String sql = "select * from goods  where goods_id=?";
		Map goods = this.baseDaoSupport.queryForMap(sql, goodsid);
		return goods;
	}
	@Override
	public SellBackChild getSellbackChild(int orderId,int goodsId){
		String sql = "SELECT * FROM es_sellback_child c WHERE c.order_id = ? AND c.goods_id = ?";
		return (SellBackChild) this.baseDaoSupport.queryForObject(sql,SellBackChild.class, orderId,goodsId);
	}
	@Override
	public List getSellbackChilds(int orderId ,int parentGoodsId){
		String sql = "SELECT c.*,g.`name`,g.price,p.product_id,i.num FROM es_sellback_child c LEFT JOIN es_goods g ON c.goods_id = g.goods_id LEFT JOIN es_product p ON c.goods_id = p.goods_id LEFT JOIN es_order_item_child i ON c.goods_id = i.goodsid AND c.order_id = i.orderid WHERE c.order_id = ? AND c.parent_id = ?  ";
		return this.baseDaoSupport.queryForList(sql, orderId,parentGoodsId);
	}
	@Override
	public void delSellerBackChilds(int orderId){
		String sql = "delete from es_sellback_child WHERE order_id = ? ";
		this.baseDaoSupport.execute(sql, orderId);
	}
	
	@Override
	public List<Map> list(int goods_id) {
		String sql = "select pp.*, g.name as name ,g.sn as sn ,g.p11 as spec from "
				+ this.getTableName("package_product")
				+ " pp inner join "
				+ this.getTableName("goods")
				+ " g on g.goods_id = pp.rel_goods_id";
		sql += " where pp.goods_id = " + goods_id;
		List<Map> list = this.daoSupport.queryForList(sql);
		return list;
	}
	/**
	 * 捆绑销售  插件Manager 暂时移植到核心类里
	 * @param goodsid
	 * @param depotid
	 * @return
	 */
	private Map<String, Integer> getStore(int goodsid, int depotid) {
		String sql = "select store,enable_store from es_product_store where depotid=? and goodsid=?";
		List<Map> storeList = this.daoSupport.queryForList(sql, depotid,
				goodsid);
		if (storeList.isEmpty()) {
			Map<String, Integer> store = new HashMap<String, Integer>();
			store.put("store", 0);
			store.put("enable_store", 0);
			return store;
		}

		return (storeList.get(0));
	}
	/**
	 * 捆绑销售  插件Manager 暂时移植到核心类里
	 * @param goodsid
	 * @param productid
	 * @param depotid
	 * @param store
	 * @param enable_store
	 */
	private void updateStore(int goodsid, int productid, int depotid, int store,
			int enable_store) {

		int count = this.daoSupport.queryForInt("select count(0)  from es_product_store where depotid=? and  goodsid=? and productid=?",
						depotid, goodsid, productid);

		if (count == 0) {
			this.daoSupport.execute("insert into es_product_store(goodsid,productid,depotid,store,enable_store)values(?,?,?,?,?)",
							goodsid, productid, depotid, store, enable_store);
		} else {
			this.daoSupport.execute("update es_product_store set store=? ,enable_store=? where depotid=? and productid=?",
							store, enable_store, depotid, productid);
		}

		this.daoSupport.execute("update es_product set enable_store=enable_store+? where product_id=?",
						store, productid);
		this.daoSupport.execute("update es_goods set enable_store=enable_store+? where goods_id=?",
						store, goodsid);

	}
	/**
	 * 捆绑销售  插件Manager 暂时移植到核心类里
	 * @param goodsId
	 * @param relGoodsId
	 * @return
	 */
	@Override
	public Map getPackInfo(int goodsId,int relGoodsId){
		String sql = "SELECT * FROM es_package_product WHERE goods_id = ? AND rel_goods_id = ?";
		Map map = this.daoSupport.queryForMap(sql, goodsId,relGoodsId);
		return map;
	}
	/**
	 * 捆绑销售  插件Manager 暂时移植到核心类里
	 * @param productid
	 * @return
	 */
	@Override
	public int isPack(int productid) {
		int is_pack = this.daoSupport
				.queryForInt(
						"select is_pack from es_product where product_id =?",
						productid); // 是否是整箱
		return is_pack;
	}
	
	
	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IOrderMetaManager getOrderMetaManager() {
		return orderMetaManager;
	}

	public void setOrderMetaManager(IOrderMetaManager orderMetaManager) {
		this.orderMetaManager = orderMetaManager;
	}

	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public IGoodsStoreManager getGoodsStoreManager() {
		return goodsStoreManager;
	}

	public void setGoodsStoreManager(IGoodsStoreManager goodsStoreManager) {
		this.goodsStoreManager = goodsStoreManager;
	}
	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}
	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}
	public IDepotManager getDepotManager() {
		return depotManager;
	}
	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}
	public IProductManager getProductManager() {
		return productManager;
	}
	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}
	public GoodsStorePluginBundle getGoodsStorePluginBundle() {
		return goodsStorePluginBundle;
	}
	public void setGoodsStorePluginBundle(
			GoodsStorePluginBundle goodsStorePluginBundle) {
		this.goodsStorePluginBundle = goodsStorePluginBundle;
	}
}
