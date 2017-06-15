package com.enation.app.b2b2c.core.service.order.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.shop.ShopApp;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.InsureRepairSpec;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.impl.CarInfoManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component
public class SelfStoreOrderManager implements IOrderManager {

	
	@Override
	public JSONArray getInsurancesOrderReadyInfo(String productId) {
		// TODO Auto-generated method stub
		return null;
	}

	private IDaoSupport  daoSupport;
	
	@Override
	public Page listOrder(Map map, int page, int pageSize, String other,String order) {
		
		String sql = createTempSql(map, other,order);
		Page webPage = this.daoSupport.queryForPage(sql, page, pageSize);
	 
		return webPage;
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
 
		sql.append("select * from es_order o where disabled=0 and parent_id is NOT NULL and store_id="+ShopApp.self_storeid); //只查询出子订单

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
		
//		System.out.println(sql.toString());
		return sql.toString();
	}
	
	
	
	@Override
	public void log(Integer order_id, String message, Integer op_id, String op_name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void savePrice(double price, int orderid) {
		// TODO Auto-generated method stub

	}

	@Override
	public double saveShipmoney(double shipmoney, int orderid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean saveAddrDetail(String addr, int orderid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean saveShipInfo(String remark, String ship_day, String ship_name, String ship_tel, String ship_mobile,
			String ship_zip, int orderid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refuseReturn(String orderSn) {
		// TODO Auto-generated method stub

	}

	@Override
	public Order add(Order order, List<CartItem> cartItemList, String sessionid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public JSONObject isOrder(String store_id, Long order_date, String time_region_id, String carplate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InsureRepairSpec addRepairOrderTimeRegionRecord(String store_id, String time_region_id, Long order_date,
			String carplate, String repair_item_ids) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void edit(Order order) {
		// TODO Auto-generated method stub

	}

	@Override
	public Page list(int page, int pageSize, int disabled, String order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page list(int page, int pageSize, int status, int depotid, String order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page listConfirmPay(int pageNo, int pageSize, String sort, String order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order get(Integer orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order updateRecordStatus(Order order, int record_update_status) {
		// TODO Auto-generated method stub
		return order;
	}

	@Override
	public Order getNext(String next, Integer orderId, Integer status, int disabled, String sn, String logi_no,
			String uname, String ship_name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Order get(String ordersn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OrderItem> listGoodsItems(Integer orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getOrderItem(int order_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List listLogs(Integer orderId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(Integer[] orderId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clean(Integer[] orderId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void revert(Integer[] orderId) {
		// TODO Auto-generated method stub

	}

	@Override
	public List listOrderByMemberId(int member_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map mapOrderByMemberId(int member_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map> listAdjItem(Integer orderid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map censusState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String export(Date start, Date end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OrderItem getItem(int itemid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMemberOrderNum(int member_id, int payStatus) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Map> getItemsByOrderid(Integer order_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateOrderPrice(double price, int orderid) {
		// TODO Auto-generated method stub

	}

	@Override
	public String queryLogiNameById(Integer logi_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page searchForGuest(int page, int pageSize, String ship_name, String ship_tel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page listByStatus(int pageNo, int pageSize, int status, int memberid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page listByStatus(int pageNo, int pageSize, int memberid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Order> listByStatus(int status, int memberid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMemberOrderNum(int member_id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Page search(int pageNO, int pageSize, int disabled, String sn, String logi_no, String uname,
			String ship_name, int status) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page search(int pageNO, int pageSize, int disabled, String sn, String logi_no, String uname,
			String ship_name, int status, Integer paystatus) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page listbyshipid(int pageNo, int pageSize, int status, int shipping_id, String sort, String order) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delItem(Integer itemid, Integer itemnum) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updatePayMethod(int orderid, int payid, String paytype, String payname) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean checkProInOrder(int productid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkGoodsInOrder(int goodsid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String createSn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List listByOrderIds(Integer[] orderids, String order) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void saveDepot(int orderid, int depotid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void savePayType(int orderId, int paytypeid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveShipType(int orederId, int shiptypeid) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(Order order) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveAddr(int orderId, int ship_provinceid, int ship_cityid, int ship_regionid, String Attr) {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer getOrderGoodsNum(int order_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getOrderItemDetail(int item_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List listGoodsItemsByItemId(Integer itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getOrderByMemberid(String sn, Integer memberid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer orderStatusNum(Integer status) {
		// TODO Auto-generated method stub
		return null;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	@Override
	public void updateInsureAndRepairInfo(Order order, CarInfoManager carInfoManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateIncomeUseHistory(Order order) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JSONObject checkCreateOrderStatus(String carplate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateGainById(Double gain, int order_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InsureRepairSpec getInsureOrRepairSpec(int type, String carplate, Object... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InsureRepairSpec getInsureOrRepairSpecById(int spec_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveLogistics(String order_id, String logi_id, String ship_no) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public InsureRepairSpec addInsureOrderRecord(Map<Integer, JSONObject> insureTypePriceMap, String store_id,
			String insure_company_id, String select_insure_typeids, String carplate, String applicant,
			String applicant_id, long insure_starttime, long insure_endtime, String params) {
		// TODO Auto-generated method stub
		return null;
	}


}
