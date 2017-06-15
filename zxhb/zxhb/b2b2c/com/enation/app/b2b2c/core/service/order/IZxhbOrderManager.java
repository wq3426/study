package com.enation.app.b2b2c.core.service.order;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.RepairOrderDetail;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.model.zxhb.OrderDetail;
import com.enation.app.b2b2c.core.model.zxhb.Shipping;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.database.Page;

import net.sf.json.JSONObject;
/** @Description 中信海博发卡订单
 *
 * @createTime 2016年12月14日 上午11:46:45
 *
 * @author <a href="mailto:huashixin@trans-it.cn">huashixin</a>
 */
public interface IZxhbOrderManager {
	
	public void addShipping(Shipping shipping);

	/** @description  显示发卡订单列表
	 * @date 2016年12月14日 下午3:03:41
	 * @param orderMap
	 * @param page
	 * @param pageSize
	 * @param sort
	 * @param order
	 * @return
	 * @return Page
	 */
	public Page listOrder(Map orderMap, int page, int pageSize, String sort, String order);

	/** @description 获取所有快递公司 
	 * @date 2016年12月15日 上午11:45:01
	 * @return 
	 * @return List
	 */
	public List listShipping();

	/** @description  获取发卡订单详情
	 * @date 2016年12月15日 上午11:49:14
	 * @param order_sn
	 * @return
	 * @return OrderDetail
	 */
	public OrderDetail getOrderDetailBySn(String order_sn);

	/** @description 
	 * @date 2016年12月15日 下午4:51:32
	 * @param orderDetail
	 * @return void
	 */
	public void editOrderDetail(OrderDetail orderDetail);
	
	
	/** @description 获取百度api快递接口
	 * @date 2016年12月19日 下午4:51:35
	 * @return
	 * @return JSONObject
	 */
	public JSONObject getBaiduShippingApi(String shipping_type, String shipping_no);

	/** @description 订单详情
	 * @date 2016年12月21日 上午11:14:46
	 * @param order_sn
	 * @return
	 * @return Map
	 */
	public Map getSpecificsBySn(String order_sn);



}
