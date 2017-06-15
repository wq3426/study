package com.enation.app.b2b2c.core.model.zxhb;

/**
 * @Description 订单状态实体类
 *
 * @createTime 2016年12月7日 下午9:35:56
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
public class OrderStatus {
	
	//0 待支付  1 已支付，待发货  2 配送中 3 已送达  4 已签收  8 已取消
	//订单状态
	public static final int NOT_PAY = 0;//0 待支付 
	public static final int PAY = 1;//1 已支付，待发货
	public static final int IN_DELIVERY = 2;//配送中
	public static final int HAS_DELIVERY = 3;//已送达，未签收
	public static final int CONFIRM = 4;//已签收
	public static final int CANCEL = 8;//已取消
}
