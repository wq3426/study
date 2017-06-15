package com.enation.test.shop.delivery;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.enation.app.shop.core.model.DlyType;
import com.enation.app.shop.core.model.support.DlyTypeConfig;
import com.enation.app.shop.core.model.support.TypeAreaConfig;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.test.SpringTestSupport;

public class DeliveryTest extends SpringTestSupport {
	
	private IDlyTypeManager dlyTypeManager;
	private IDBRouter shopSaasDBRouter;
	@Before
	public void mock(){
		this.dlyTypeManager = this.getBean("dlyTypeManager");
		shopSaasDBRouter = this.getBean("shopSaasDBRouter");
       
		
//		this.jdbcTemplate.execute("drop table if exists js_dly_type_2");
//		this.jdbcTemplate.execute("drop table if exists js_dly_type_area_2");
//		shopSaasDBRouter.createTable(2, "dly_type");
//		shopSaasDBRouter.createTable(2, "dly_type_area");
	}
	
	/**
	 * 统一地区配置测试
	 */
	@Test
	public void addSameTest(){
		DlyType type = new DlyType();
		DlyTypeConfig config = new DlyTypeConfig();
 
		
		type.setName("一统快递");
		type.setCorp_id(1);//物流公司
		type.setDetail("一般一天可到");
		type.setDisabled(0);
		type.setHas_cod(1); //支持货到付款
		type.setIs_same(1); //统一设置
		type.setOrdernum(1) ;
		type.setProtect(0);//不支持保价
		
		
		config.setFirstunit(1000); //首重1000克
		config.setContinueunit(500); //续重500克
		
		config.setFirstprice(10D); //首重费用
		config.setContinueprice(5D); //续重费用
	
		
	//	this.dlyTypeManager.add(type, config);
		
		
	}

	/**
	 * 分别配置地区费用
	 */
	@Test
	public void addDiffTest(){
		DlyType type = new DlyType();
		DlyTypeConfig config = new DlyTypeConfig();
	
		type.setName("一统快递");
		type.setCorp_id(1);//物流公司
		type.setDetail("一般一天可到");
		type.setDisabled(0);
		//type.setHas_cod(1); //支持货到付款
		type.setIs_same(0); //统一设置
		type.setOrdernum(1) ;
		type.setProtect(0);//不支持保价
		
		
		config.setFirstunit(1000); //首重1000克
		config.setContinueunit(500); //续重500克		
		config.setDefAreaFee(0);//不开启默认费用设置
		
		/***************配置1************************/
		TypeAreaConfig areaConfig1 = new TypeAreaConfig();
		
		//首重和续重使用统一的设置
		areaConfig1.setContinueunit(config.getContinueunit());
		areaConfig1.setFirstunit(config.getFirstunit());
		areaConfig1.setUseexp(0);
		
		areaConfig1.setAreaId("1,2|close,21,22,23,24");
		areaConfig1.setAreaName("北京,上海");
		
		areaConfig1.setFirstprice(10D);
		areaConfig1.setContinueprice(5D);
		areaConfig1.setHave_cod(0);
		
		
		
		
		
		/***************配置2************************/
		TypeAreaConfig areaConfig2 = new TypeAreaConfig();
		
		//首重和续重使用统一的设置
		areaConfig2.setContinueunit(config.getContinueunit());
		areaConfig2.setFirstunit(config.getFirstunit());
		areaConfig2.setUseexp(0);
		
		areaConfig2.setAreaId("62,63,64,65,104,105,106,107");
		areaConfig2.setAreaName("重庆，安徽");
		areaConfig2.setHave_cod(1);
		
		areaConfig2.setFirstprice(10D);
		areaConfig2.setContinueprice(5D);
		TypeAreaConfig[] configArray={areaConfig1,areaConfig2};
		this.dlyTypeManager.add(type, config, configArray);
		
	}
 
	
	@Test
	public void editSameTest(){
		this.addSameTest();
		DlyType type = new DlyType();
		DlyTypeConfig config = new DlyTypeConfig();
 
		type.setType_id(1);
		type.setName("一统快递1");
		type.setCorp_id(1);//物流公司
		type.setDetail("一般一天可到1");
		type.setDisabled(0);
		type.setHas_cod(1); //支持货到付款
		type.setIs_same(1); //统一设置
		type.setOrdernum(1) ;
		type.setProtect(1);//支持保价
		type.setProtect_rate(0.01f); //费率
		type.setMin_price(10f); //最低保价费用
		
		config.setFirstunit(2000); //首重1000克
		config.setContinueunit(1000); //续重500克
		
		config.setFirstprice(15d); //首重费用
		config.setContinueprice(10D); //续重费用
		
		this.dlyTypeManager.edit(type, config);
	}
	
	
	@Test
	public void editDiffTest(){
		this.addDiffTest();
		DlyType type = new DlyType();
		DlyTypeConfig config = new DlyTypeConfig();
		
		type.setType_id(1);
		type.setName("一统快递1");
		type.setCorp_id(1);//物流公司
		type.setDetail("一般一天可到1");
		type.setDisabled(0);
		//type.setHas_cod(1); //支持货到付款
		type.setIs_same(0); //统一设置
		type.setOrdernum(1) ;
		type.setProtect(1);//支持保价
		type.setProtect_rate(0.01f); //费率
		type.setMin_price(10f); //最低保价费用
		
		
		config.setFirstunit(2000); //首重1000克
		config.setContinueunit(1000); //续重500克		
		config.setDefAreaFee(1);//不开启默认费用设置
		
		 //***************配置1************************//*
		TypeAreaConfig areaConfig1 = new TypeAreaConfig();
		
		//首重和续重使用统一的设置
		areaConfig1.setContinueunit(config.getContinueunit());
		areaConfig1.setFirstunit(config.getFirstunit());
		areaConfig1.setUseexp(0);
		
		areaConfig1.setAreaId("788,789,790,791,814|close");
		areaConfig1.setAreaName("海南,河北");
		
		areaConfig1.setFirstprice(10D);
		areaConfig1.setContinueprice(5D);
		areaConfig1.setHave_cod(0);
		
		
		
		
		
		 //***************配置2************************//*
		TypeAreaConfig areaConfig2 = new TypeAreaConfig();
		
		//首重和续重使用统一的设置
		areaConfig2.setContinueunit(config.getContinueunit());
		areaConfig2.setFirstunit(config.getFirstunit());
		areaConfig2.setUseexp(0);
		
		areaConfig2.setAreaId("998|close,1989|close");
		areaConfig2.setAreaName("河南，内蒙古");
		areaConfig2.setHave_cod(1);
		
		areaConfig2.setFirstprice(10D);
		areaConfig2.setContinueprice(5D);
		TypeAreaConfig[] configArray={areaConfig1,areaConfig2};
		this.dlyTypeManager.edit(type, config, configArray); 
	}
	
	
	/**
	 * 校验公式
	 */
	@Test
	public void testCountateExp(){
		String exp ="5.0+tint(w-500)/500*6.0;";
		Double res  = this.dlyTypeManager.countExp(exp,1000D,30D);
		//System.out.println(res);
	}
	
	/**
	 * 测试下单时读取配送方式
	 */
	@Test
	public void testGetTypeForOrder(){
		List<DlyType> typeList  = this.dlyTypeManager.list(2000D, 100D, "1678");
		for(DlyType type:typeList){
			//System.out.println("name:"+type.getName() +"["+type.getPrice()+ "]");
		}
	}
	
	/**
	 * 测试计算配送费用
	 */
	@Test
	public void testCountPrice(){
		Double[] dlyPrice  =this.dlyTypeManager.countPrice(3, 2D, 1000D, "4");
		//System.out.println(dlyPrice[0]);
		//System.out.println(dlyPrice[1]);
	}
	
	
}
