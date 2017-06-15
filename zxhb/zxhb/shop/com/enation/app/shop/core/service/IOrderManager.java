package com.enation.app.shop.core.service;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.InsureRepairSpec;
import com.enation.app.shop.core.service.impl.CarInfoManager;
import com.enation.framework.database.ObjectNotFoundException;
import com.enation.framework.database.Page;

import net.sf.json.JSONObject;

/**
 * 订单管理
 * 
 * @author kingapex 2010-4-6上午11:09:53
 * @author LiFenLong 2014-4-22;4.0订单流程改版
 */
/**
 * Descriptiion:
 * @author xinzai
 * @date 2016年4月21日下午8:24:28
 */
public interface IOrderManager {
	/**
	 * 记录订单操作日志
	 * @author LiFenLong
	 * @param order_id
	 * @param message
	 * @param op_id
	 * @param op_name
	 */
	public void log(Integer order_id, String message, Integer op_id,String op_name);
	/**
	 * 修改订单价格
	 * @param price 订单总金额
	 * @param orderid
	 */
	public void savePrice(double price,int orderid);
	
	/**
	 * 修改订单配送费用
	 * @param shipmoney
	 * @param orderid
	 * @return
	 */
	public double saveShipmoney(double shipmoney, int orderid);
	
	/**
	 * 根据订单id修改订单详细地址
	 * @param addr
	 * @param orderid
	 * @return
	 */
	public boolean saveAddrDetail(String addr, int orderid);
	
	/**
	 * 根据订单id修改配送信息
	 * @param remark
	 * @param ship_day
	 * @param ship_name
	 * @param ship_tel
	 * @param ship_mobile
	 * @param ship_zip
	 * @param orderid
	 * @return
	 */
	public boolean saveShipInfo(String remark,String ship_day,String ship_name,String ship_tel,String ship_mobile,String ship_zip, int orderid);//修改收货人信息
	
	/**
	 * 拒绝退货
	 */
	public void refuseReturn(String orderSn); 
	
	/**
	 * 创建订单 计算如下业务逻辑：</br> <li>为订单创建唯一的sn(订单号)</li> <li>
	 * 根据sessionid读取购物车计算订商品价格及商品重量，填充以下信息:</br> goods_amount
	 * 商品总额,shipping_amount 配送费用,order_amount 订单总额,weight商品重量,商品数量：goods_num</li>
	 * <li>根据shipping_id(配送方式id)、regionid(收货地区id)及is_protect(是否保价) 计算
	 * protect_price</li> <li>根据payment_id(支付方式id)计算paymoney(支付费用)</li> <li>
	 * 读取当前买家是否为会员或匿名购买并填充member_id字段</li> <li>计算获得积分和消费积分</li>
	 * 
	 * @param order
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
	 * @param cartItemList 2015-08-20新增by kingapex
	 *        购物车列表<br>
	 *        以前是通过sessionid获取，现改为通过接口传递参数方式<br>
	 *        目的是解决比如在多店中需要创建子订单时需要传递某个店铺的购物列表，而不是全部的购物列表
	 *        
	 * @param sessionid
	 *            会员的sessionid
	 *            
	 *            
	 * @throws IllegalStateException 会员尚未登录,不能兑换赠品!   
	 *         
	 * @return 创建的新订单实体，已经赋予order id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Order add(Order order,List<CartItem> cartItemList, String sessionid);
	
	/**
	 * 修改订单信息
	 * @param order
	 */
	public void edit(Order order);

	/**
	 * 分页读取订单列表
	 * 
	 * @param page
	 *            页数
	 * @param pageSize
	 *            每页显示数量
	 * @param disabled
	 *            是否读回收站列表(1为读取回收站列表,0为读取正常订单列表)
	 * @param order
	 *            排序值
	 * @return 订单分页列表对象
	 */
	public Page list(int page, int pageSize, int disabled,  String order);
	/**
	 * 分页读取订单列表
	 * @param page页数
	 * @param pageSize 每页显示数量
	 * @param status 订单状态
	 * @param depotid 仓库标识
	 * @param order  排序值
	 * @return 订单分页列表对象
	 */
	public Page list(int page,int pageSize,int status,int depotid,String order);

	/**
	 * 查询确认付款订单
	 * @param pageNo
	 * @param pageSize
	 * @param order
	 * @return
	 */
	public Page listConfirmPay(int pageNo,int pageSize,String sort,String order);
	/**
	 * 根据订单id获取订单详细
	 * 
	 * @param orderId
	 *            订单id
	 * @return 订单实体 对象
	 * @throws ObjectNotFoundException
	 *             当订单不存在时
	 */
	public Order get(Integer orderId);

	
	

	
	/**
	 * 查询上一订单或者下一订单
	 * @param orderId
	 * @return
	 */
	public Order getNext(String next,Integer orderId,Integer status,int disabled, String sn,
			String logi_no, String uname, String ship_name);
	
	/**
	 * 根据订单号获取订单
	 * @param ordersn
	 * @return
	 */
	public Order get(String ordersn);
	
	
	
	/**
	 * 读取某个订单的商品货物列表
	 * 
	 * @param orderId
	 *            订单id
	 * @return list中为map，对应order_items表
	 */
	public List<OrderItem> listGoodsItems(Integer orderId);

	/**
	 * 读取某个订单的商品货物列表(包含es_goods表信息)
	 * 
	 * @param orderId
	 *            订单id
	 * @return list中为map，对应order_items表
	 */
	public List getOrderItem(int order_id);
	
 
	
	
	
	/**
	 * 读取某订单的订单日志
	 * 
	 * @param orderId
	 * @return lisgt中为map ，对应order_log表
	 */
	public List listLogs(Integer orderId);

	/**
	 * 批量将某些订单放入回收站<br>
	 * 
	 * @param orderId
	 *            要删除的订单Id数组
	 */
	public boolean delete(Integer[] orderId);

	/**
	 * 彻底删除某些订单 <br>
	 * 同时删除以下信息： <li>订单购物项</li> <li>订单日志</li> <li>订单支付、退款数据</li> <li>订单发货、退货数据</li>
	 * 
	 * @param orderId
	 *            要删除的订单Id数组
	 */
	public void clean(Integer[] orderId);

	/**
	 * 批量还原某些订单
	 * 
	 * @param orderId
	 */
	public void revert(Integer[] orderId);

	/**
	 * 列表某会员的订单<br/>
	 * lzf add
	 * 
	 * @param member_id
	 * @return
	 */
	public List listOrderByMemberId(int member_id);
	
	/**
	 * 取某一会员的关于订单的统计信息
	 * @param member_id
	 * @return
	 */
	public Map mapOrderByMemberId(int member_id);

	
	
	/**
	 * 读取某订单的配件发货项
	 * @param orderid
	 * @return
	 */
	public List<Map> listAdjItem(Integer orderid);
	
	//已废弃，使用CartManager.countPrice
	//public OrderPrice countPrice(String sessionid,Integer shippingid,String regionid,boolean isProtected );
	
	
	/**
	 * 统计订单状态
	 * @return key为状态值 ，value为此状态订单的数量
	 */
	public Map  censusState();


	/**
	 * 导出订单为excel
	 * @param start 下单日期范围开始
	 * @param end   下单日期范围结束 
	 * @return 返回导出的excel下载路径
	 */
	public String export(Date start,Date end);



	
	
	/**
	 * 获取某个订单货物项
	 * @param itemid
	 * @return 订单货物项
	 */
	public OrderItem getItem(int itemid);
	
	
	
	/**
	 * 取某一会员未付款的订单数
	 * @param member_id
	 * @param status
	 * @return
	 */
	public int getMemberOrderNum(int member_id, int payStatus);
	
	/**
	 * 根据订单ID查所有货物
	 * @param order_id订单ID
	 * @return
	 */
	public List<Map> getItemsByOrderid(Integer order_id);
	
	/**
	 * 更新订单价格
	 */
	public void updateOrderPrice(double price,int orderid);
	
	/**
	 * 根据id查询物流公司名字
	 */
	public String queryLogiNameById(Integer logi_id);
	
	/**
	 * 游客订单查询
	 * @param page
	 * @param pageSize
	 * @param ship_name
	 * @param ship_tel	手机或固定电话
	 * @return
	 */
	public Page searchForGuest(int page, int pageSize, String ship_name,String ship_tel);
	
	/**
	 * 查询某一用户某一状态下的订单列表
	 * @param status
	 * @param memberid
	 * @return
	 */
	public Page listByStatus(int pageNo, int pageSize,int status,int memberid);
	
	/**
	 * 查询某一用户的订单列表
	 * @param status
	 * @param memberid
	 * @return
	 */
	public Page listByStatus(int pageNo, int pageSize,int memberid);
	
	
	/**
	 * 读取某会员某状态的订单列表
	 * @param status
	 * @param memberid
	 * @return
	 */
	public List<Order> listByStatus(int status,int memberid);
	
	
	/**
	 * 查询某一用户的所有订单数
	 * @param member_id
	 * @return
	 */
	public int getMemberOrderNum(int member_id);
	
	/**
	 * 
	 * @param pageNO页数
	 * @param pageSize页面行数
	 * @param disabled是否作废0是正常
	 * @param sn订单编号
	 * @param logi_no物流单号
	 * @param uname会员用户名
	 * @param ship_name收货人姓名
	 * @return
	 */
	public Page search(int pageNO, int pageSize,int disabled, String sn,String logi_no, String uname,String ship_name,int status);
	public Page search(int pageNO, int pageSize, int disabled, String sn, String logi_no, String uname, String ship_name, int status,Integer paystatus);
	
	public Page listbyshipid(int pageNo,int pageSize,int status,int shipping_id,String sort,String order);
	
	
	public boolean delItem(Integer itemid,Integer itemnum);
	
	
	/**
	 * 更新付款方式 
	 * @param orderid
	 * @param payid
	 * @param paytype
	 */
	public void updatePayMethod(int orderid,int payid,String paytype,String payname);
	
	
	/**
	 * 检测某个货品是否有订单使用
	 * @param productid
	 * @return
	 */
	public boolean checkProInOrder(int productid);
	
	
	/**
	 * 检测某个货品是否有订单使用
	 * @param goodsid
	 * @return
	 */
	public boolean checkGoodsInOrder(int goodsid);
	
	public String createSn();
	
	public List listByOrderIds(Integer[] orderids,String order);
	
	/**
	 * 查询订单列表
	 * @author xulipeng 2014年5月15日11:18
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
	 * @param page
	 * @param pageSize
	 * @param sortField 排序字段
	 * @param sortType 排序方式
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED) 
	public Page listOrder(Map map,int page,int pageSize,String sortField,String sortType);
	/**
	 * 保存库房
	 * @author LiFenLong
	 * @param orderid
	 * @param depotid
	 */
	public void saveDepot(int orderid, int depotid);
	/**
	 * 保存付款方式
	 * @author LiFenLong
	 * @param orderId
	 * @param paytypeid
	 */
	public void savePayType(int orderId,int paytypeid);
	/**
	 *  保存配送方式
	 *  @author LiFenLong
	 * @param orederId
	 * @param shiptypeid
	 */
	public void saveShipType(int orederId,int shiptypeid);
	
	/**
	 * 测试用
	 * @param order
	 * @author xulipeng
	 * @return 
	 */
	public void add(Order order);
	/**
	 * 修改配送地区
	 * @param ship_provinceid
	 * @param ship_cityid
	 * @param ship_regionid
	 */
	public void saveAddr(int orderId, int ship_provinceid,int ship_cityid,int ship_regionid,String Attr);
	
	/**
	 * 通过订单ID，获得该订单ID下的商品个数
	 */
	
	public Integer getOrderGoodsNum(int order_id);
	
	/**
	 * 获取申请退货订单商品列表
	 * @return
	 */
	public List getOrderItemDetail(int item_id);
	
	/**
	 * 通过itemid 获取订单项  为了增加is_pack属性
	 * @param itemId
	 * @return
	 */
	public List listGoodsItemsByItemId(Integer itemId);
	
	/**
	 * 根据某一个订单是否是当前登录会员的订单
	 * @param sn
	 * @param memberid
	 * @return
	 */
	public boolean getOrderByMemberid(String sn,Integer memberid);
	
	
	/**
	 * 获得该会员订单在各个状态的个数
	 * whj    2015-08-11
	 */
	public Integer orderStatusNum(Integer status);
	
	/**
	 * 获取保险订单准备页面
	 * @param productId
	 * @return 
	 */
	public List getInsurancesOrderReadyInfo(String productId);
	
	/**
	 * //保险订单支付完成，更新下次购买保险时间 年份加1
	 * //保养订单支付完成，更新店铺工位
	 * @param order
	 */
	public void updateInsureAndRepairInfo(Order order, CarInfoManager carInfoManager);
	
	/**
	 * 查询保险订单是否能够下单
	 * @param carplate
	 * @return
	 */
	public JSONObject checkCreateOrderStatus(String carplate); 
	
	
	/**
	 * 根据订单id更新使用奖励
	 */
	public void updateGainById(Double gain ,int order_id);
	
	/**
	 * 获取保险/保养特殊属性
	 * @param i
	 * @param carplate
	 * @param args
	 * @return
	 */
	public InsureRepairSpec getInsureOrRepairSpec(int type, String carplate, Object... args);
	
	/**
	 * 根据id获取保险保养订单的特殊属性内容
	 * @param spec_id
	 * @return
	 */
	public InsureRepairSpec getInsureOrRepairSpecById(int spec_id);
	
	/**
	 * 更新记录更新状态，如果订单状态为已完成，则触发自动结算操作
	 * @param i
	 * @param status
	 */
	public Order updateRecordStatus(Order order, int record_update_status);
	
	/**
	 * @description 查询车辆是否可以预约
	 * @date 2016年9月22日 上午11:01:52
	 * @param store_id
	 * @param order_date
	 * @param time_region_id
	 * @param carplate
	 * @return
	 */
	JSONObject isOrder(String store_id, Long order_date, String time_region_id, String carplate);
	
	/**
	 * @description 添加保养预约记录到店铺保养预约表
	 * @date 2016年9月5日 下午3:08:08
	 * @param store_id
	 * @param time_region_id
	 * @param order_date
	 * @param repair_item_ids 
	 * @param carplate 
	 */
	InsureRepairSpec addRepairOrderTimeRegionRecord(String store_id, String time_region_id, Long order_date, String carplate, String repair_item_ids);
	
	
	/** @description 
	 * @date 2016年9月8日 上午11:20:28
	 * @param order_id
	 * @param logi_id
	 * @param ship_no
	 * @return void
	 */
	public void saveLogistics(String order_id, String logi_id, String ship_no);
	
	
	
	/**
	 * @description 添加保险下单记录到保险订单详情表
	 * @date 2016年9月14日 上午10:54:01
	 * @param insureTypePriceMap     根据车牌号获取的险种价格map集合
	 * @param store_id               店铺id
	 * @param insure_company_id      保险公司id
	 * @param select_insure_typeids  选择的险种类型id字符串
	 * @param carplate               车牌号
	 * @param applicant              投保人
	 * @param applicant_id           投保人身份证
	 * @param insure_starttime       订单生效起始时间
	 * @param insure_endtime         订单生效结束时间
	 * @param params                 选择的险种的参数集合JSONArray字符串
	 * @return
	 */
	public InsureRepairSpec addInsureOrderRecord(Map<Integer, JSONObject> insureTypePriceMap, String store_id, String insure_company_id, 
			                                     String select_insure_typeids, String carplate, String applicant, String applicant_id, 
			                                     long insure_starttime, long insure_endtime, String params);
	
	/**
	 * @description 更新订单收益使用记录
	 * @date 2016年9月23日 下午9:08:15
	 * @param order
	 */
	public void updateIncomeUseHistory(Order order);
	
}
