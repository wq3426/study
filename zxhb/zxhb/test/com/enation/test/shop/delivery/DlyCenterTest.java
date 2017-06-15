package com.enation.test.shop.delivery;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enation.app.shop.core.model.DlyCenter;
import com.enation.app.shop.core.service.IDlyCenterManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.test.SpringTestSupport;

/**
 * 发货中心测试类
 * @author lzf<br/>
 * 2010-4-30上午10:27:51<br/>
 * version 1.0
 */
public class DlyCenterTest extends SpringTestSupport {
	
	private IDlyCenterManager dlyCenterManager;
	private IDBRouter shopSaasDBRouter;
	
	@Before
	public void mock(){
		this.dlyCenterManager = this.getBean("dlyCenterManager");
		shopSaasDBRouter = this.getBean("shopSaasDBRouter");
      
	}
	
	/**
	 * 清除数据并建立表结构
	 */
	private void clean(){

		this.jdbcTemplate.execute("drop table if exists js_dly_center_2");
//		shopSaasDBRouter.createTable(  "dly_center");

	}
	
	/**
	 * 测试添加
	 */
	@Test
	public void testAdd(){
		
		this.clean();
		DlyCenter dlyCenter = new DlyCenter();
		dlyCenter.setName("易族智汇");
		dlyCenter.setUname("刘先生");
		dlyCenter.setAddress("名佳花园3区51号楼1单元401室");
		dlyCenter.setProvince("北京");
		dlyCenter.setProvince_id(1);
		dlyCenter.setCity("北京市");
		dlyCenter.setCity_id(2);
		dlyCenter.setRegion("昌平区");
		dlyCenter.setRegion_id(15);
		dlyCenter.setZip("102209");
		dlyCenter.setCellphone("010-61750491");
		dlyCenter.setPhone("13331002660");
		dlyCenter.setSex("male");
		dlyCenter.setDisabled("false");
		this.dlyCenterManager.add(dlyCenter);
		
		//断言
		DlyCenter dlyCenterDb = dlyCenterManager.get(1);
		Assert.assertEquals(dlyCenterDb.getRegion(), "昌平区");
		Assert.assertEquals(dlyCenterDb.getAddress(), "名佳花园3区51号楼1单元401室");
		Assert.assertEquals(dlyCenterDb.getDisabled(),"false");
	}
	
	/**
	 * 测试修改
	 */
	@Test
	public void testEdit(){
		this.testAdd(); //模拟数据
		DlyCenter dlyCenter = new DlyCenter();
		dlyCenter.setDly_center_id(1);
		dlyCenter.setAddress("名佳花园4区7号楼1单元401室");
		dlyCenterManager.edit(dlyCenter);
		//断言
		DlyCenter dlyCenterDb = dlyCenterManager.get(1);
		Assert.assertEquals(dlyCenterDb.getRegion(), "昌平区");
		Assert.assertEquals(dlyCenterDb.getAddress(), "名佳花园4区7号楼1单元401室");
		Assert.assertEquals(dlyCenterDb.getDisabled(),"false");
	}
	
	/**
	 * 测试删除
	 */
	@Test
	public void testDelete(){
		
		this.testAdd();
		dlyCenterManager.delete(null);
		
		//断言
		DlyCenter dlyCenterDb = dlyCenterManager.get(1);
		Assert.assertEquals(dlyCenterDb.getRegion(), "昌平区");
		Assert.assertEquals(dlyCenterDb.getAddress(), "名佳花园3区51号楼1单元401室");
		Assert.assertEquals(dlyCenterDb.getDisabled(),"false");
		
		dlyCenterManager.delete(new Integer[]{1});
		int count  = this.jdbcTemplate.queryForInt("select count(0) from js_dly_center_2 where disabled = 'false'");
		Assert.assertEquals(count,0);
		
	}
	
	@Test
	public void listTest(){
		this.testAdd();
		List<DlyCenter> list = dlyCenterManager.list();
		Assert.assertEquals(list.get(0).getRegion(), "昌平区");
		Assert.assertEquals(list.get(0).getAddress(), "名佳花园3区51号楼1单元401室");
		Assert.assertEquals(list.get(0).getDisabled(),"false");
	}

	public IDlyCenterManager getDlyCenterManager() {
		return dlyCenterManager;
	}

	public void setDlyCenterManager(IDlyCenterManager dlyCenterManager) {
		this.dlyCenterManager = dlyCenterManager;
	}
	public IDBRouter getShopSaasDBRouter() {
		return shopSaasDBRouter;
	}
	public void setShopSaasDBRouter(IDBRouter shopSaasDBRouter) {
		this.shopSaasDBRouter = shopSaasDBRouter;
	}
	
	
	

}
