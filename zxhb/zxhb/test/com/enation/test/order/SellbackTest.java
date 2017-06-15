package com.enation.test.order;

import java.util.List;

import org.junit.Test;
import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;

import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.framework.test.SpringTestSupport;

public class SellbackTest extends SpringTestSupport {
	
	@Test
	public void test1(){
		ISellBackManager sellBackManager=this.getBean("sellBackManager");
		for (int i=1;i<=100;i++) {
			SellBackList data = new SellBackList();
			data.setTradestatus(1);
			data.setOrdersn("xxx"+i);
			data.setAlltotal_pay(32.00);
			int id = sellBackManager.save(data);
			if(id<790){
				//System.out.println("OK----");
			}
			System.out.println(id);
			//SellBackList sellBackList = sellBackManager.get(id);
		}
	}
	
	
	public void addtest(){
		IOrderManager orderManager =this.getBean("orderManager");
		for (int i=1;i<=100;i++) {
			Order order = new Order();
			order.setSn("xxx"+i);
			orderManager.add(order);
		}
	}
}
