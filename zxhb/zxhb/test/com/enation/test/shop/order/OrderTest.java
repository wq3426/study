package com.enation.test.shop.order;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.database.Page;
import com.enation.framework.test.SpringTestSupport;

/**
 * 订单测试
 * @author kingapex
 *2010-4-6下午05:04:30
 */
public class OrderTest extends SpringTestSupport {
	protected IOrderManager orderManager;
	private ICartManager cartManager ;
	protected IDBRouter shopSaasDBRouter;
	private String sessionid = "AC95B4420D5CE0C61BC98F99E6700B74";
	@Before
	public void mock(){
		orderManager= this.getBean("orderManager");
		cartManager = this.getBean("cartManager");
		//shopSaasDBRouter = this.getBean("shopSaasDBRouter");
	 
	}
	
	public void clean(){
		this.jdbcTemplate.execute("drop table if exists js_order_2");
		this.jdbcTemplate.execute("drop table if exists js_order_items_2");
		this.jdbcTemplate.execute("drop table if exists js_order_gift_2");
		this.jdbcTemplate.execute("drop table if exists js_order_log_2");
//		shopSaasDBRouter.createTable( "order");
//		shopSaasDBRouter.createTable( "order_items");
//		shopSaasDBRouter.createTable( "order_log");
//		shopSaasDBRouter.createTable( "order_gift");
	}
	
	/**
	 * 测试添加订单
	 */
	@Test
	public void testAdd(){
		this.clean();
		
		cartManager.clean(sessionid);
		Cart cart = new Cart();
		cart.setProduct_id(22);
		cart.setSession_id(sessionid);
		cart.setNum(1);
		cart.setItemtype(0);
		cartManager.add(cart);

		Cart cart1 = new Cart();
		cart1.setProduct_id(28);
		cart1.setSession_id(sessionid);
		cart1.setNum(2);
		cart.setItemtype(0);
		cartManager.add(cart1);
		

		Cart cart2 = new Cart();
		cart2.setProduct_id(1);
		cart2.setSession_id(sessionid);
		cart2.setNum(1);
		cart2.setItemtype(2);
		cartManager.add(cart2);
		
		Order order = new Order() ;
		order.setShipping_id(3); //配送方式为北京同城
		order.setPayment_id(1);//支付方式为支付宝
		order.setRegionid(4); //北京市的一个区
		
		order.setShip_addr("回龙观龙腾三区14#1单元401");
		order.setShip_email("kingapex@163.com");
		order.setShip_mobile("13718880644");
		order.setShip_tel("61750928");
		order.setShip_zip("100020");
		order.setShipping_area("北京-北京市-昌平区");
		order.setShip_name("王峰");
		
		order.setShip_day("任意日期");
		order.setShip_time("任意时段");
		
		order.setRemark("发货后请电话通知");
//		orderManager.add(order, sessionid);
		
	}
	
	
	/**
	 * 测试订单列表读取（后台）
	 */
//	@Test
//	public void testPageList(){
//		Page page  = orderManager.list(1, 20,0, null, null, null);
//		List list  = (List)page.getResult();
//		for(int i=0,len=list.size();i<len;i++){
//			//System.out.println( ((Order)list.get(i) ).getSn());
//		}
//	}

	
	/**
	 * 测试删除
	 */
	@Test
	public void testDelete(){
		this.testAdd();
		this.orderManager.delete(new Integer[]{1});
		Order order = this.orderManager.get(1);
		Assert.assertEquals(order.getDisabled().intValue(),1);
	}
	
	/**
	 * 测试清除
	 */
//	@Test
//	public void testClean(){
//		this.testAdd();
//		this.orderManager.clean(new Integer[]{1});
//		
//		//验证订单货物明细被清除
//		String sql  ="select count(0) from js_order_items_2 where order_id=1";
//		int count = this.jdbcTemplate.queryForInt(sql);
//		Assert.assertEquals(0,count);
//		
//		//验证订单日志被清除
//		sql  ="select count(0) from js_order_log_2 where order_id=1";
//		count = this.jdbcTemplate.queryForInt(sql);
//		Assert.assertEquals(0,count);
//		
//		//验证订单收退款单据被清除
//		sql  ="select count(0) from js_payment_logs_2 where order_id=1";
//		count = this.jdbcTemplate.queryForInt(sql);
//		Assert.assertEquals(0,count);
//		
//		//验证订单发退货单据被清除
//		sql  ="select count(0) from js_delivery_2 where order_id=1";
//		count = this.jdbcTemplate.queryForInt(sql);
//		Assert.assertEquals(0,count);
//		
//		//验证订单发退货明细被清除
//		sql  ="select count(0) from js_delivery_item_2  where delivery_id  in  (select delivery_id from js_delivery_2 where order_id=1)";
//		count = this.jdbcTemplate.queryForInt(sql);
//		Assert.assertEquals(0,count);		
//		
//		//验证订单被清除
//		Page page  = orderManager.list(1, 20,0, null, null, null);
//		List list  = (List)page.getResult();
//		Assert.assertEquals(0,list.size());
//		
//	}
	
	/**
	 * 测试还原
	 */
	@Test
	public void testRevert(){
		this.testDelete();
		this.orderManager.revert(new Integer[]{1});
		Order order = this.orderManager.get(1);
		Assert.assertEquals(order.getDisabled().intValue(),0);		
	}
	
	
	@Test
	public void testExport(){
		this.orderManager.export(null, null);
	}
}
