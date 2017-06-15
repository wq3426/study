package com.enation.test.shop.cart;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.Promotion;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.test.SpringTestSupport;
import com.enation.framework.util.StringUtil;
import com.enation.test.shop.promotion.PromotionTest;
/**
 * 购物车测试
 * @author kingapex
 *2010-3-25上午10:06:03
 */
public class CartTest extends SpringTestSupport {
	private ICartManager cartManager ;
	private String sessionid = "AC95B4420D5CE0C61BC98F99E6700B74";
	
	@Before
	public void mock(){
		cartManager = this.getBean("cartManager");
	 
	}
	
	/**
	 * 测试清空购物车
	 */
	@Test
	public void testClean(){
		cartManager.clean(sessionid);
		List list =cartManager.listGoods(sessionid);
		assertEquals(list.size(), 0);
	}
	
	/**
	 * 测试向购物车添加一项
	 */
	@Test
	public void testAdd(){
		cartManager.clean(sessionid);
		Cart cart = new Cart();
		cart.setProduct_id(3);
		cart.setSession_id(sessionid);
		cart.setNum(1);
		cartManager.add(cart);
	}
	
	
	/**
	 * 测试添加赠品
	 */
	@Test
	public void testAddGift(){
		cartManager.clean(sessionid);
		Cart cart = new Cart();
		cart.setProduct_id(1);
		cart.setSession_id(sessionid);
		cart.setNum(1);
		cart.setItemtype(2);
		cartManager.add(cart);		
	}
	
	

	
	
	/**
	 * 测试读取购物车中项列表
	 */
	@Test
	public void testList(){
		this.testAdd();
		List list =cartManager.listGoods(sessionid);
		assertEquals(list.size(), 1);
		
	}
	
	
	/**
	 * 测试带有优惠规则的读取
	 */
	@Test
	public void testListWithPmt(){
		PromotionTest promotionTest = new PromotionTest();
		promotionTest.setup();
		promotionTest.mock();
		promotionTest.testAddDiscount();
		promotionTest.testAddDiscount2();
		
		cartManager.clean(sessionid);
		Cart cart = new Cart();
		cart.setProduct_id(2); //goodsid:1
		cart.setSession_id(sessionid); 
		cart.setNum(1);
		cartManager.add(cart);

		Cart cart1 = new Cart();
		cart1.setProduct_id(11); //goods_id:3
		cart1.setSession_id(sessionid);
		cart1.setNum(2);
		cartManager.add(cart1);
		
		List<CartItem> list =cartManager.listGoods(sessionid);
		for(CartItem item :list){
			//System.out.println("商品"+item.getName() +"price:["+item.getPrice()+"]"+"-coupPrice["+item.getCoupPrice()+"]");
			//System.out.println("----------------------");
			List<Promotion> pmtList  =  item.getPmtList();
			for(Promotion pmt:pmtList){
				//System.out.println(pmt.getPmt_describe());
			}
			//System.out.println("\n\n");
		}
		
	}
	
	
	
	/**
	 * 测试更新购买数量
	 */
	@Test
	public void testUpdateNum(){
		List list =cartManager.listGoods(sessionid);
		assertEquals(list.size(), 1);
		cartManager.updateNum(sessionid, ( (CartItem)list.get(0) ).getId(),2);
		 list =cartManager.listGoods(sessionid);
		 assertEquals(list.size(), 1);
		 assertEquals( ( (CartItem)list.get(0) ).getNum(),2);
			
	}
	
	/**
	 * 测试删除购物车中的一项
	 */
	@Test
	public void testDelete(){
		
		List list =cartManager.listGoods(sessionid);
		assertEquals(list.size(), 1);
		cartManager.delete(sessionid, ( (CartItem)list.get(0) ).getId());
		list =cartManager.listGoods(sessionid);
		assertEquals(list.size(), 0);
	}
	
	
	
	/**
	 * 测试计算购物总价
	 */
	@Test 
	public void testCountPrice(){
		cartManager.clean(sessionid);
		Cart cart = new Cart();
		cart.setProduct_id(2);
		cart.setSession_id(sessionid);
		cart.setNum(1);
		cartManager.add(cart);

		Cart cart1 = new Cart();
		cart1.setProduct_id(11);
		cart1.setSession_id(sessionid);
		cart1.setNum(2);
		cartManager.add(cart1);
		
		Double price  = cartManager.countGoodsTotal(sessionid);
		assertTrue(price.doubleValue()==440d);
	}
	

	
	/**
	 * 测试计算购物车中商品的重量
	 */
	@Test
	public void testCountWeight(){
		cartManager.clean(sessionid);
		Cart cart = new Cart();
		cart.setProduct_id(2);
		cart.setSession_id(sessionid);
		cart.setNum(1);
		cartManager.add(cart);

		Cart cart1 = new Cart();
		cart1.setProduct_id(11);
		cart1.setSession_id(sessionid);
		cart1.setNum(2);
		cartManager.add(cart1);
		Double weight = cartManager.countGoodsWeight(sessionid);
		assertTrue(weight.doubleValue()==3000);
	}
	
	@Test
	public void t(){
		//System.out.println(StringUtil.md5("admin"));
	}
}
