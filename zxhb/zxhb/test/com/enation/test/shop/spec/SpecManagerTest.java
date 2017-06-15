package com.enation.test.shop.spec;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.enation.app.shop.component.spec.service.ISpecManager;
import com.enation.app.shop.component.spec.service.ISpecValueManager;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.SpecValue;
import com.enation.app.shop.core.model.Specification;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.test.SpringTestSupport;
public class SpecManagerTest extends SpringTestSupport {
	@Before
	public void mock(){
        EopSite site = EopSite.getInstance();
        
	}
	
	@Test
	public void testAdd(){
		
		this.jdbcTemplate.execute("truncate table js_specification_2");
		this.jdbcTemplate.execute("truncate table js_spec_values_2");
		
		Specification spec = new Specification();
		
		spec.setSpec_name("颜色");
		spec.setSpec_memo("上衣颜色");
		spec.setSpec_show_type(1);
		spec.setSpec_type(1);
		
		List<SpecValue> valueList  = new ArrayList<SpecValue>();
		SpecValue value1 = new SpecValue();
		value1.setSpec_value("红色");
		value1.setSpec_image("http://kingapex.eop.com/attachemnet/hong.jpg");
		
		SpecValue value2 = new SpecValue();
		value2.setSpec_value("黑色");
		value2.setSpec_image("http://kingapex.eop.com/attachemnet/hei.jpg");
		
		
		valueList.add(value1);
		valueList.add(value2);
		
		ISpecManager specManager = this.getBean("specManager");
	
		specManager.add(spec, valueList);
	}
	
	//添加测试的断言
	public void assertAdd(){
		String sql  ="select count(0) from js_specification_2";
		int count  = this.jdbcTemplate.queryForInt(sql);
		assertEquals(count, 1);
		
		sql ="select count(0) from js_spec_values_2";
		count  = this.jdbcTemplate.queryForInt(sql);
		assertEquals(count, 2);		
	}
	
	
	/**
	 * 更新测试
	 */
	@Test
	public void testEdit(){
		testAdd();
		
		Specification spec = new Specification();
		spec.setSpec_id(1);
		spec.setSpec_name("颜色1");
		spec.setSpec_memo("上衣颜色1");
		spec.setSpec_show_type(1);
		spec.setSpec_type(1);
		
		List<SpecValue> valueList  = new ArrayList<SpecValue>();
		SpecValue value1 = new SpecValue();
		value1.setSpec_value("红色1");
		value1.setSpec_image("http://kingapex.eop.com/attachemnet/hong.jpg");
		
		SpecValue value2 = new SpecValue();
		value2.setSpec_value("黑色1");
		value2.setSpec_image("http://kingapex.eop.com/attachemnet/hei.jpg");
		
		
		valueList.add(value1);
		valueList.add(value2);
		
		ISpecManager specManager = this.getBean("specManager");	
		specManager.edit(spec, valueList);
	}
	
	
	/**
	 * 修改测试断言
	 */
	public void assertEdit(){
		
		String sql  ="select count(0) from js_specification_2 where spec_name=?";
		this.jdbcTemplate.queryForInt(sql, new Object[]{"颜色1"});
		int count  = this.jdbcTemplate.queryForInt(sql);
		assertEquals(count, 1);
		
		sql ="select count(0) from js_spec_values_2 where spec_name=?";
		count  = this.jdbcTemplate.queryForInt(sql, new Object[]{"红色1"});
		assertEquals(count, 1);
	}
	
	/**
	 * 读取列表测试
	 */
	@Test
	public void testList(){
		ISpecManager specManager = this.getBean("specManager");	
		List specList  = specManager.list();
		assertEquals(specList.size(),1);
	}
	
	/**
	 * 删除测试
	 */
	@Test
	public void testDelete(){
		
		ISpecManager specManager = this.getBean("specManager");	
		specManager.delete(new Integer[]{1});
		
		String sql  ="select count(0) from js_specification_2";
		int count  = this.jdbcTemplate.queryForInt(sql);
		assertEquals(count, 0);
		
		sql ="select count(0) from js_spec_values_2";
		count  = this.jdbcTemplate.queryForInt(sql);
		assertEquals(count, 0);		
	}
	
	/**
	 * 测试规格读取
	 */
	@Test
	public void testGet(){
		ISpecManager specManager = this.getBean("specManager");	
		Map spec = specManager.get(1);
		assertEquals("颜色", spec.get("spec_name"));		
	}
	
	/**
	 * 测试规格值列表读取
	 */
	@Test
	public void testValueList(){
		testAdd();
		ISpecValueManager specValueManager = this.getBean("specValueManager");	
		List valueList =specValueManager.list(1); 
		assertEquals(valueList.size(),2);		
		
	}
	
	
	@Test
	public void testGoodsSpecs(){
		IProductManager productManager = this.getBean("productManager");
		List<Specification> specList= productManager.listSpecs(9);
		for(Specification spec : specList){
			//System.out.println("------------------");
			//System.out.println(spec.getSpec_name()+"["+spec.getSpec_id()+"]:");
			//System.out.println("------------------");
			List<SpecValue> valueList  = spec.getValueList();
			for(SpecValue value:valueList){
				//System.out.println(value.getSpec_value() +"["+value.getSpec_value_id()+"]");
			}
			
		}
	}
	
	
	@Test
	public void testListProduct(){
		IProductManager productManager = this.getBean("productManager");
		List<Product> list = productManager.list(9);
		for(Product pro:list){
			//System.out.println(pro.getProduct_id());
			//System.out.println("------------------");
			List<SpecValue> valueList  = pro.getSpecList();
			for(SpecValue val : valueList){
				//System.out.println(val.getSpec_value_id());
			}
		}
	}
	
}
