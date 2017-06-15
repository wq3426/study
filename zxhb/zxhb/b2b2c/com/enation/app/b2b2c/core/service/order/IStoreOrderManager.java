package com.enation.app.b2b2c.core.service.order;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.RepairOrderDetail;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.database.Page;

import net.sf.json.JSONObject;

/**
 * 店铺订单管理类
 * @author LiFenLong
 *@version v2.0 modify by kingapex-2015-08-21
 *v2.0 修改了以下内容：
 *增加创建订单的接口，因为要将单店和多店创建订单分开
 */
public interface IStoreOrderManager {
	

	/**
	 * 创建订单<br>
	 * 在这里首先要通过order核心api来创建主订单，然后创建子订单。<br>
	 * 和单店系统另外的区别是子订单价格计算事件调用另外的事件
	 * @param order 要创建的订单
	 *            订单实体:<br/>
	 *            <li>shipping_id(配送方式id):需要填充用户选择的配送方式id</li> <li>
	 *            regionid(收货地区id)</li> <li>是否保价is_protect</li>
	 *            shipping_area(配送地区):需要填充以下格式数据：北京-北京市-昌平区 </li>
	 * 
	 *            <li>
	 *            payment_id(支付方式id):需要填充为用户选择的支付方式</li>
	 * 
	 *            <li>填充以下收货信息：</br> ship_name(收货人姓名)</br> ship_addr(收货地址)</br>
	 *            ship_zip(收货人邮编)</br> ship_email(收货人邮箱 ) ship_mobile( 收货人手机)
	 *            ship_tel (收货人电话 ) ship_day (送货日期 ) ship_time ( 送货时间 )
	 * 
	 *            </li> <li>remark为买家附言</li>
	 *            
	 * @param shippingIds 
	 *   		配送方式id数据，根据购物车中店铺顺序形成           
	 * @param sessionid
	 *  	  会员的sessionid
	 * @return
	 *        创建的新订单实体，已经赋予order id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Order createOrder(Order order,String sessionid) ;
	
	
	
	/**
	 * 创建父订单<br>
	 * 在这里首先要通过order核心api来创建主订单，然后创建店铺关系子订单。<br>
	 * 暂时只有payment_id(支付方式id),配送方式、地址都没有
	 * @param order
	 * @param cartIds
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Order createMainOrder(Order order, String cartIds);
	
	
	/**
	 * 查看店铺订单<br>
	 * 获取卖家的订单
	 * @param pageNo
	 * @param pageSize
	 * @param storeid
	 * @param map
	 * @return 店铺订单列表
	 */
	public Page storeOrderList(Integer pageNo,Integer pageSize,Integer storeid,Map map);
	/**
	 * 查看店铺子订单
	 * @param parent_id
	 * @return 店铺子订单列表
	 */
	public List<StoreOrder> storeOrderList(Integer parent_id);
	/**
	 * 获取一个订单
	 * @param orderId
	 * @return StoreOrder
	 */
	public StoreOrder get(Integer orderId);
	/**
	 * 修改收货人信息
	 * @param remark
	 * @param ship_day
	 * @param ship_name
	 * @param ship_tel
	 * @param ship_mobile
	 * @param ship_zip
	 * @param orderid
	 * @return boolean
	 */
	public boolean saveShipInfo(String remark,String ship_day, String ship_name,String ship_tel, String ship_mobile, String ship_zip, int orderid);
	 
	
	
	/**
	 * 查询买家订单<br>
	 * 只查子订单，订单商品项通过order.itemList来显示<br>
	 * @author kingapex
	 * 2015-11-15
	 * @see Order#getItemList()
	 * @param pageNo 当前页码
	 * @param pageSize 页大小
	 * @param status 订单状态 {@link OrderStatus}
	 * @param keyword 关键字
	 * @return 订单分页列表
	 */
	public Page pageBuyerOrders(int pageNo, int pageSize,String status, String keyword);
	
	
	/**
	 * 根据订单编号查看订单
	 * @param ordersn
	 * @return StoreOrder
	 */
	public StoreOrder get(String ordersn);
	/**
	 * 根据订单状态获取店铺订单数量
	 * @param status
	 * @author LiFenLong
	 * @return
	 */
	public int getStoreOrderNum(int status);
	
	/**
	 * 根据订单状态获取店铺订单数量
	 * @param status
	 * @author LiFenLong
	 * @return
	 */
	public int getStoreOrderNum(String status, String pay_status);
	
	
	/**
	 * 查询所有商家的订单列表<br>
	 * 只查询子订单
	 * @author LiFenLong 
	 * @param map 过滤条件<br>
	 * <li>stype:搜索类型(integer,0为基本搜索)</li>
	 * <li>keyword:关键字(String)</li>
	 * <li>order_state:订单状态特殊查询(String型，可以是如下的值：
	 * wait_ship:待发货
	 * wait_pay:待付款
	 * wait_rog:待收货
	 * )</li>
	 * <li>start_time:(开始时间,String型，如2015-10-10 )</li>
	 * <li>end_time(结束时间,String型，如2015-10-10 )</li>
	 * <li>status:订单状态(int型，对应status字段，{@link OrderStatus})</li>
	 * <li>paystatus:付款状态(int型，对应pay_status字段，{@link OrderStatus})</li>
	 * <li>shipstatus发货状态(int型，对应ship_status字段，{@link OrderStatus})</li>
	 * <li>sn:订单编号(String)</li>
	 * <li>ship_name:收货人(String 对应ship_name字段)</li>
	 * <li>shipping_type:配送方式(Integer，对应shipping_id字段)</li>
	 * <li>payment_id:支付方式(Integer 对应payment_id字段)</li>
	 * <li>depotid:仓库id(Integer 对应depotid字段)</li>
	 * <li>store_name:店铺名称(String 会联合es_store表查询)</li> 
	 * <li>store_id:店铺id(Integer 对应store_id字段)</li> 
	 * @param page
	 * @param pageSize
	 * @param sortField 排序字段
	 * @param sortType 排序方式
	 * @return 订单的分页列表
	 */
	public Page listOrder(Map map,int page,int pageSize,String sortField,String sortType);
	
	
 
	
	
	/**
	 * 获取订单状态的json
	 * @return 订单状态Json
	 */
	public Map getStatusJson();
	/**
	 * 获取付款状态的json
	 * @return 付款状态Json
	 */
	public Map getpPayStatusJson();
	/**
	 * 获取配送状态的json
	 * @return 配送状态Json
	 */
	public Map getShipJson();
	/**
	 * 发货
	 * @param order_id 订单ID
	 * @param logi_id 物流公司id
	 * @param logi_name 物流公司名称
	 * @param shipNo 运单号
	 */
	public void saveShipNo(Integer[] order_id,Integer logi_id,String logi_name,String shipNo);
	
	/**
	 * 获得该会员订单在各个状态的个数
	 * 
	 */
	public Integer orderStatusNum(Integer status);
	
	/**
	 * 通过商铺ID，获得该商铺下的商品个数
	 * @param store_id
	 * @return
	 */
	public Integer getStoreGoodsNum(int store_id);


	/**
	 * 获取会员所有子订单
	 * @param pageNo
	 * @param pageSize
	 * @param status
	 * @param keyword
	 * @return
	 */
	public Page pageChildOrders(int pageNo, int pageSize, String status, String keyword);
	
	
	/**
	 * 统计订单状态
	 * @return key为状态值 ，value为此状态订单的数量
	 */
	public Map  censusState();
	
	/**
	 * 根据父订单id获取所有子订单
	 * @param parentOrderId 父订单id
	 * @return StoreOrder集合
	 */
	public List<StoreOrder> getChildOrder(int parentOrderId);

	/**
	 * 根据主订单、订单类型和车牌号获取订单详情json返回
	 * @param order
	 * @param type
	 * @param carplate 
	 * @return
	 */
	public JSONObject getOrderDetailJson(Order order, String type, String carplate, String repair4sstoreid);

	/**
	 * 修改备货状态
	 * @param orderId
	 */
	public void stockUpComplete(String orderId);

	/**
	 * 修改服务状态
	 * @param orderId
	 */
	public void confirmService(String orderId);

	/**
	 * 商铺完成订单后卖家确认后调用方法
	 * @param orderId
	 */
	public void finishOrder(String orderId);


	/**
	 * 0元支付流程
	 * @param sn
	 * @param string
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public String paySuccess(StoreOrder sn, String trade_no);


	@Transactional(propagation = Propagation.REQUIRED)
	public void updateGain(Double useGain, Integer order_id);


	/**
	 * 获取保险订单详情
	 * @param order_id
	 * @return
	 */
	public JSONObject getInsureOrderDetail(String order_id);


	/**
	 * 获取保养订单详细信息
	 * @param order_id
	 * @return
	 */
	public JSONObject getRepairOrderDetail(String order_id);


	/**
	 * 保存保养订单详细信息
	 * @param repairDetail
	 * @return
	 */
	public RepairOrderDetail saveRepairOrderDetailInfo(RepairOrderDetail repairDetail);


	/**
	 * 获取车辆保险历史记录
	 * @param carplate
	 * @return
	 */
	public JSONObject getCarInsureRecords(String car_id);


	/**
	 * 获取车辆保养历史记录
	 * @param car_id
	 * @return
	 */
	public JSONObject getCarRepairRecords(String car_id);


	/**
	 * 获取今日保养订单数
	 * @return
	 */
	public int getRepairOrderCount();


    /**
     * @param store_id 
     * @description 获取保险/保养订单详情
     * @date 2016年9月5日 下午7:53:45
     * @param order 主订单对象
     * @param order_type 订单类型  1 4 保险  2 保养
     * @param carplate  车牌号
     * @return
     */
	public JSONObject getInsureAndRepairOrderDetail(Integer store_id, Order order, String order_type, String carplate);


    /**
     * @description 获取车辆保险、保养订单历史记录
     * @date 2016年9月7日 下午8:11:22
     * @param carplate  车牌号
     * @param order_type 订单类型
     * @return
     */
	public JSONObject getInsureAndRepairOrderHistory(String carplate, String order_type);

	/**
	 * @description app获取订单列表
	 * @date 2016年9月9日 下午7:21:16
	 * @param valueOf
	 * @param pageSize
	 * @param status
	 * @param keyword
	 * @return
	 */
	public Page pageChildOrderList(Integer pageNo, int pageSize, String status, String keyword);

	/**
	 * @description 保存保养订单的保养币、保单号
	 * @date 2016年9月26日 上午9:48:04
	 * @param order       订单id
	 * @param spec_id     保险订单详情表id
	 * @param repair_coin 保养币
	 * @param policy_no   保单号
	 */
	public void saveInsureOrderDetailInfo(Order order, String spec_id, String repair_coin, String policy_no);



	/** @description 修改收获地址
	 * @date 2016年9月21日 上午9:53:25
	 * @return void
	 */
	public void updateAddress();

	/**
	 * @description 检查保养订单是否支付
	 * @date 2016年9月22日 下午1:53:24
	 * @param order
	 * @return
	 */
	public JSONObject isPayable(StoreOrder order);

}
