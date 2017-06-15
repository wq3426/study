package com.enation.test.shop.promotion;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.enation.app.shop.core.model.Promotion;
import com.enation.app.shop.core.model.support.DiscountPrice;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.app.shop.core.service.promotion.PromotionConditions;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.test.SpringTestSupport;
import com.enation.framework.util.DateUtil;

/**
 * 促销规则测试
 * @author kingapex
 *2010-4-15下午03:08:44
 */
@RunWith(JMock.class)
public class PromotionTest extends SpringTestSupport {
	protected IDBRouter shopSaasDBRouter;
	private IPromotionManager promotionManager ;
	private  Mockery context = new JUnit4Mockery();
	@Before
	public void mock(){
		promotionManager = this.getBean("promotionManager");
		shopSaasDBRouter = this.getBean("shopSaasDBRouter");
       
	}
	
	private void clean(){
		
		//促销表
		this.jdbcTemplate.execute("drop table if exists js_promotion_2");
//		shopSaasDBRouter.createTable( "promotion");
		
		//促销会员关联表
		this.jdbcTemplate.execute("drop table if exists js_pmt_member_lv_2");
//		shopSaasDBRouter.createTable( "pmt_member_lv");
		
		//促销商品关联表
		this.jdbcTemplate.execute("drop table if exists js_pmt_goods_2");
//		shopSaasDBRouter.createTable( "pmt_goods");
		
		
	}

	
	
	/**
	 * 测试添加促销规则<br>
	 * 测试某id为1的促销活动添加一个规则<br>
	 * 规则为商品金额大于100元则赠id为1,2的赠品<br>
	 * 关联了会员级别1,2<br>
	 * 未关联商品
	 * 
	 */
	@Test
	public void testAddGiveGift(){
		
		//模拟request返回赠品id数组
		final HttpServletRequest  request  = context.mock(HttpServletRequest.class);
		context.checking(new Expectations() {{
		    oneOf (request).getParameterValues("giveGift");
		    will(returnValue(  new String[]{"1","2"} ));
		}});
		ThreadContextHolder.setHttpRequest(request);
		
		this.clean();
		Promotion promotion = new Promotion();
		promotion.setPmt_describe("购物车中商品金额大于某金额，赠送某赠品");
		promotion.setOrder_money_from(100D);
		promotion.setOrder_money_to(999999D);
		promotion.setPmts_id("enoughPriceGiveGiftPlugin");
		promotion.setPmt_time_begin(new Date().getTime());
		promotion.setPmt_time_end( DateUtil.toDate("2010-05-20", "yyyy-MM-dd").getTime() ); 
		promotion.setPmt_type(0); //促销活动
		promotion.setPmta_id(1); //促销活动id为1
		promotion.setPmt_basic_type("order");
		
		this.promotionManager.add(promotion, new Integer[]{1,2}, null);
		
		/**************验证*******************/
		//验证插入了一个规则
		int count =  this.jdbcTemplate.queryForInt("select count(0) from js_promotion_2");
		Assert.assertEquals(count, 1);
		
		//验证关联了会员
		count =  this.jdbcTemplate.queryForInt("select count(0) from js_pmt_member_lv_2");
		Assert.assertEquals(count,2);
		
		//验证未关联商品
		count =  this.jdbcTemplate.queryForInt("select count(0) from js_pmt_goods_2");
		Assert.assertEquals(count,0);		
		
		
	}
	  HttpServletRequest  request  ;
	
	
	/**
	 * 添加打拆规则<br>
	 * 对1,2商品直接进行打9折
	 */
	@Test
	public void  testAddDiscount(){
		
		//模拟request返回赠品id数组
		 request  = context.mock(HttpServletRequest.class);
		context.checking(new Expectations() {{
		    oneOf (request).getParameter("discount");
		    will(returnValue( "0.9" ));
		}});
		ThreadContextHolder.setHttpRequest(request);
		
		this.clean();
		Promotion promotion = new Promotion();
		promotion.setPmt_describe("商品1,2直接打折");

		promotion.setPmts_id("goodsDiscountPlugin");
		promotion.setPmt_time_begin(new Date().getTime()); //开始日期为今天
		promotion.setPmt_time_end(DateUtil.toDate("2010-05-20", "yyyy-MM-dd").getTime()); 
		promotion.setPmt_type(0); //促销活动
		promotion.setPmta_id(1); //促销活动id为1
		promotion.setPmt_basic_type("goods");
		
		this.promotionManager.add(promotion,  new Integer[]{1,2}, new Integer[]{1,2});
		
		/**************验证*******************/
		//验证插入了一个规则		
		int count =  this.jdbcTemplate.queryForInt("select count(0) from js_promotion_2");
		Assert.assertEquals(count, 1);
		
		//验证关联了会员
		count =  this.jdbcTemplate.queryForInt("select count(0) from js_pmt_member_lv_2");
		Assert.assertEquals(count,2);
		
		//验证关联了商品
		count =  this.jdbcTemplate.queryForInt("select count(0) from js_pmt_goods_2");
		Assert.assertEquals(count,2);			
	}
	

	/**
	 * 添加打拆规则<br>
	 * 对2,3商品直接进行打8折
	 */
	@Test
	public void  testAddDiscount2(){
		
		//模拟request返回赠品id数组
		//final HttpServletRequest  request  = context.mock(HttpServletRequest.class);
		context.checking(new Expectations() {{
		    oneOf (request).getParameter("discount");
		    will(returnValue( "0.8" ));
		}});
		ThreadContextHolder.setHttpRequest(request);
		
	 
		Promotion promotion = new Promotion();
		promotion.setPmt_describe("商品11,12直接打8折");

		promotion.setPmts_id("goodsDiscountPlugin");
		promotion.setPmt_time_begin(new Date().getTime()); //开始日期为今天
		promotion.setPmt_time_end(DateUtil.toDate("2010-05-20", "yyyy-MM-dd").getTime());  
		promotion.setPmt_type(0); //促销活动
		promotion.setPmta_id(2); //促销活动id为2
		promotion.setPmt_basic_type("goods");
		
		this.promotionManager.add(promotion,  new Integer[]{1,2}, new Integer[]{2,3});
		
		/**************验证*******************/
//		//验证插入了一个规则		
//		int count =  this.jdbcTemplate.queryForInt("select count(0) from js_promotion_2");
//		Assert.assertEquals(count, 1);
//		
//		//验证关联了会员
//		count =  this.jdbcTemplate.queryForInt("select count(0) from js_pmt_member_lv_2");
//		Assert.assertEquals(count,2);
//		
//		//验证关联了商品
//		count =  this.jdbcTemplate.queryForInt("select count(0) from js_pmt_goods_2");
//		Assert.assertEquals(count,2);			
	}
	

	
	
	/**
	 *  测试添加订单满金额就减价规则
	 *  满200减5块
	 */
	@Test
	public void  testAddOrderRendPrice(){
		
		//模拟request返回赠品id数组
		 request  = context.mock(HttpServletRequest.class);
		context.checking(new Expectations() {{
		    oneOf (request).getParameter("lessMoney");
		    will(returnValue( "5" ));
		}});
		ThreadContextHolder.setHttpRequest(request);
		
		this.clean();
		Promotion promotion = new Promotion();
		promotion.setPmt_describe("满200减5块");
		promotion.setOrder_money_from(200D);
		promotion.setOrder_money_to(999999D);
		promotion.setPmts_id("enoughPriceReducePrice");
		promotion.setPmt_time_begin(new Date().getTime()); //开始日期为今天
		promotion.setPmt_time_end(DateUtil.toDate("2010-05-20", "yyyy-MM-dd").getTime()); 
		promotion.setPmt_type(0); //促销活动
		promotion.setPmta_id(1); //促销活动id为1
		promotion.setPmt_basic_type(PromotionConditions.ORDER);
		
		this.promotionManager.add(promotion,  new Integer[]{1,2}, null);
		
		/**************验证*******************/
		//验证插入了一个规则		
		int count =  this.jdbcTemplate.queryForInt("select count(0) from js_promotion_2");
		Assert.assertEquals(count, 1);
		
		//验证关联了会员
		count =  this.jdbcTemplate.queryForInt("select count(0) from js_pmt_member_lv_2");
		Assert.assertEquals(count,2);
		
		//验证未关联了商品
		count =  this.jdbcTemplate.queryForInt("select count(0) from js_pmt_goods_2");
		Assert.assertEquals(count,0);			
	}
	
	/**
	 * 测试修改
	 */
	@Test
	public void testEdit(){
		this.testAddDiscount();

		context.checking(new Expectations() {{
		    oneOf (request).getParameter("discount");
		    will(returnValue( "0.8" ));
		}});
		ThreadContextHolder.setHttpRequest(request);
		
		
		Promotion promotion = new Promotion();
		promotion.setPmt_id(1);
		promotion.setPmt_describe("商品1,2,3直接打折");

		promotion.setPmts_id("goodsDiscountPlugin");
		promotion.setPmt_time_begin(new Date().getTime()); //开始日期为今天
		promotion.setPmt_time_end(DateUtil.toDate("2010-05-20", "yyyy-MM-dd").getTime()); 
		promotion.setPmt_type(0); //促销活动
		promotion.setPmta_id(1); //促销活动id为1
 
		
		this.promotionManager.edit(promotion,  new Integer[]{1,2,3}, new Integer[]{1,2,3});
		
		/**************验证*******************/
		//验证插入了一个规则		
		int count =  this.jdbcTemplate.queryForInt("select count(0) from js_promotion_2");
		Assert.assertEquals(count, 1);
		
		//验证关联了会员
		count =  this.jdbcTemplate.queryForInt("select count(0) from js_pmt_member_lv_2");
		Assert.assertEquals(count,3);
		
		//验证关联了商品
		count =  this.jdbcTemplate.queryForInt("select count(0) from js_pmt_goods_2");
		Assert.assertEquals(count,3);			
	}
	
	/**
	 * 测试读取订单优惠规则
	 */
	@Test
	public void testListOrderPmt(){
		this.testAddGiveGift();
		List<Promotion> list  =promotionManager.list(1000D, 1);
		Assert.assertEquals(list.get(0).getPmt_describe(),"购物车中商品金额大于某金额，赠送某赠品");		
	}
	
	/**
	 * 测试读取某商品的优惠规则
	 */
	@Test
	public void testListGoodsPmt1(){
		this.testAddDiscount(); //添加模拟数据
		
		List<Promotion> list  = promotionManager.list(1, 1);
		Assert.assertEquals(list.get(0).getPmt_describe(),"商品1,2直接打折");		
		
	}
	
	
	
	/**
	 * 测试订单的优惠价格计算
	 */
	@Test
	public void testCountPmtPrice(){
		this.testAddOrderRendPrice();
		Double orderPrice  = new Double(1000);
		Double shipFree = 10D;
		DiscountPrice dp = promotionManager.applyOrderPmt(orderPrice, shipFree,100, 1);
		//System.out.println(dp.getOrderPrice());
		//System.out.println(dp.getShipFee());
		//System.out.println(dp.getPoint());
	}
	
	
	public static void main(String[] args){
	  long now  = new Date().getTime();
	  long newtime =  now+ 60*60 * 60 * 24 *10;
	//System.out.println(  DateUtil.toDate("2010-05-20", "yyyy-MM-dd").getTime() );
	   //System.out.println( DateUtil.toString(new Date(newtime), "yyyy-MM-dd")  );
	   
	   ////System.out.println( new Date().getTime() + 60 * 60 * 24  );
	}
	
}
