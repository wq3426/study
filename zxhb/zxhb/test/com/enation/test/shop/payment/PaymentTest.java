package com.enation.test.shop.payment;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.plugin.IPlugin;
import com.enation.framework.test.SpringTestSupport;
/**
 * 支付方式测试
 * @author kingapex
 *2010-4-4下午02:47:27
 */
public class PaymentTest extends SpringTestSupport {
	
	private IPaymentManager paymentManager;
	
	@Before
	public void mock(){
		this.paymentManager = this.getBean("paymentManager");
        EopSite site = EopSite.getInstance();
       
		FreeMarkerPaser.set(new FreeMarkerPaser());
	}
		
	
	@Test
	public void testList(){
		
		List<PayCfg> list  = this.paymentManager.list();
		assertEquals(list.size(), 2);
		for(PayCfg pay :list){
			//System.out.println(pay.getName());
		}
	}
	
	/**
	 * 测试安装支付方式
	 */
	@Test
	public void testAdd(){
		Map<String,String> params = new HashMap<String, String>(); 
		params.put("chnid", "52560956"); 
		params.put("key", "020202029298282"); 
		this.paymentManager.add("财付通(中介担保)", "tenpay_med", "teset", params);
		
	}
	
	
	/**
	 * 测试修改一个支付方式
	 */
	@Test
	public void testEdit(){
		Map<String,String> params = new HashMap<String, String>(); 
		params.put("chnid", "52560956123"); 
		params.put("key", "020202029298282");
	//	this.paymentManager.edit(3,"财付通(中介担保)", "teset", params);
		params = this.paymentManager.getConfigParams(3);
		Assert.assertEquals("52560956123", params.get("chnid"));
	}
	
	/**
	 * 测试读取支付方式配置参数
	 * 
	 */
	@Test
	public void testGetParams(){
		Map<String,String> params = this.paymentManager.getConfigParams(3);
		Assert.assertEquals("52560956", params.get("chnid"));
	}
	
	
	/**
	 * 测试删除支付方式
	 */
	@Test
	public void testDelete(){
		paymentManager.delete(new Integer[]{1})	;
	}
	
	
	/**
	 * 测试读取支付插件列表
	 */
	@Test
	public void testListPlugins(){
		List<IPlugin> plugins  = this.paymentManager.listAvailablePlugins();
		for(IPlugin plugin:plugins){
		//	//System.out.println(plugin.getName());
		}
	}
	
	
	/**
	 * 测试读取支付插件安装html
	 * 
	 */
	@Test
	public void testGetHtml(){
		String html = paymentManager.getPluginInstallHtml("tenpay_med", 3);
		//System.out.println(html);
		 paymentManager.getPluginInstallHtml("tenpay_med", null);
			//System.out.println(html);
		
	}
	
	
}
