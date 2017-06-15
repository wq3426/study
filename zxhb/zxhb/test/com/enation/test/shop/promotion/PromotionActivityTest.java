package com.enation.test.shop.promotion;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enation.app.shop.core.model.PromotionActivity;
import com.enation.app.shop.core.service.IPromotionActivityManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.database.ObjectNotFoundException;
import com.enation.framework.database.Page;
import com.enation.framework.test.SpringTestSupport;

/**
 * 促销活动管理类测试
 * @author kingapex
 *2010-4-15下午12:16:10
 */
public class PromotionActivityTest extends SpringTestSupport {
	private IPromotionActivityManager promotionActivityManager;
	protected IDBRouter shopSaasDBRouter;
	@Before
	public void mock(){
		promotionActivityManager = this.getBean("promotionActivityManager");
		shopSaasDBRouter = this.getBean("shopSaasDBRouter");
       

	}
	
	/**
	 * 清除数据并建立表结构
	 */
	private void clean(){

		this.jdbcTemplate.execute("drop table if exists js_promotion_activity_2");
//		shopSaasDBRouter.createTable( "promotion_activity");

	}
	
	
	/**
	 * 测试添加
	 */
	@Test
	public void testAdd(){
		this.clean();
		PromotionActivity  activity = new PromotionActivity();
		activity.setName("test");
		activity.setEnable(1);
		activity.setBegin_time( new Date().getTime());
		activity.setEnd_time(new Date().getTime()) ;
		activity.setBrief("test");
		promotionActivityManager.add(activity);
		int count  = this.jdbcTemplate.queryForInt("select count(0) from js_promotion_activity_2 ");
		/**
		 * 验证数据库记录为1
		 */
		Assert.assertEquals(count, 1);
		
		try{
			PromotionActivity  activity1 = new PromotionActivity();
			promotionActivityManager.add(activity1);
			Assert.fail("参数错误，不应正常执行");
		}catch(RuntimeException e){
			 
		}

		
		try{
			PromotionActivity  activity3 = new PromotionActivity();
			activity3.setName("abc");
			promotionActivityManager.add(activity3);
			Assert.fail("参数错误，不应正常执行");
		}catch(RuntimeException e){
			 
		}		
		
		try{
			PromotionActivity  activity2=null;
			promotionActivityManager.add(activity2);
			Assert.fail("参数错误，不应正常执行");
		}catch(RuntimeException e){
			 
		}
		
	}
	
	
	/**
	 * 测试读取
	 */
	@Test
	public void testGet(){
		this.testAdd(); //模拟数据
		PromotionActivity activity =  promotionActivityManager.get(1);
		Assert.assertEquals(activity.getName(), "test");
		
		try{
			activity =  promotionActivityManager.get(132323);
			Assert.fail("未正常抛出未找到记录异常");
		}catch(ObjectNotFoundException e){
			
		}
	}
	
	/**
	 * 测试修改
	 */
	@Test
	public void testEdit(){
		PromotionActivity  activity = this.promotionActivityManager.get(1);
		activity.setId(1);
		activity.setName("test1");
		activity.setBrief("test1");
		promotionActivityManager.edit(activity);
		PromotionActivity activityDb =   this.promotionActivityManager.get(1);
		Assert.assertEquals(activityDb.getName(), "test1");
		
		PromotionActivity  activity1 = new PromotionActivity();
	 
		activity1.setName("test2");
		activity1.setBrief("test2");
		try{
			promotionActivityManager.edit(activity1);
			Assert.fail("参数错误，不应正常执行");
		}catch (RuntimeException e) {
			
		}
		
		
	}
	
	/**
	 * 测试读取列表
	 */
	@Test
	public void testList(){
		this.testAdd();
		Page page  = this.promotionActivityManager.list(1, 20);
		Assert.assertEquals(page.getTotalCount(), 1);
		Assert.assertEquals( ((List)page.getResult()).size()  , 1);
	}
	
	
	/**
	 * 测试删除
	 */
	@Test
	public void testDelete(){
		this.testAdd();
		PromotionTest promotionTest =new PromotionTest();
		promotionTest.setup();
		promotionTest.mock();
		promotionTest.testAddDiscount();
		
		promotionActivityManager.delete( null );	
		promotionActivityManager.delete(new Integer[]{});	
		promotionActivityManager.delete(new Integer[]{1});	
		int count  = this.jdbcTemplate.queryForInt("select count(0) from js_promotion_activity_2 ");
		Assert.assertEquals(count, 0);

		//验证规则 已清除		
		 count =  this.jdbcTemplate.queryForInt("select count(0) from js_promotion_2");
		Assert.assertEquals(count, 0);
		
		//验证关联会员 已清除
		count =  this.jdbcTemplate.queryForInt("select count(0) from js_pmt_member_lv_2");
		Assert.assertEquals(count,0);
		
		//验证关联 商品 已清除
		count =  this.jdbcTemplate.queryForInt("select count(0) from js_pmt_goods_2");
		Assert.assertEquals(count,0);			
	}
	
}
