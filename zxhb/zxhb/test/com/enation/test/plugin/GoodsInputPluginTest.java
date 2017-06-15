package com.enation.test.plugin;

import org.junit.Before;
import org.junit.Test;

import com.enation.app.shop.core.model.support.GoodsEditDTO;
import com.enation.app.shop.core.plugin.goods.IGetGoodsAddHtmlEvent;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.test.SpringTestSupport;

/**
 * 商品后台输入页面插件测试
 * @author kingapex
 * 2010-2-19下午02:32:15
 */
public class GoodsInputPluginTest extends SpringTestSupport {
	
	 
	@Before
	public void mock(){
	 
	}
	
	
	@Test
	public void tagPluginAddInputHtml(){
		IGetGoodsAddHtmlEvent event = this.getBean("goodsTagPlugin");
		String html  = event.getAddHtml(null);
		//System.out.println(html);
		
	}
	
	@Test
	public void seoPluginAddInputTest(){
		IGetGoodsAddHtmlEvent event = this.getBean("goodsSeoPlugin");
		String html  = event.getAddHtml(null);
		//System.out.println(html);
	}
	
	
	@Test
	public void pluginEditInputTest(){
		 IGoodsManager goodsManager= this.getBean("goodsManager");
		 GoodsEditDTO editDTO = goodsManager.getGoodsEditData(2);
//		 List<String> htmlList = editDTO.getHtmlList();
//		 for(String html:htmlList){
//			 //System.out.println(html);
//			 //System.out.println("---------------------------------------------------------");
//		 }
	}
 
	
}
