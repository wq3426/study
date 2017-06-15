package com.enation.test.shop.goods;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.test.SpringTestSupport;

public class GoodsCatTest extends SpringTestSupport {
	
	private IGoodsCatManager goodsCatManager;
	
	@Before
	public void mock(){
		
		goodsCatManager = this.getBean("goodsCatManager");
	 
		
	}
	
	@Test
	public void testGetParents(){
		List<Cat> parentList = this.goodsCatManager.getParents(4);
		for(Cat cat :parentList){
			//System.out.println(cat.getName());
		}
	}

}
