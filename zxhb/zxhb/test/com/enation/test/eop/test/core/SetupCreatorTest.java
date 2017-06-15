package com.enation.test.eop.test.core;

import java.util.List;

import org.dom4j.Document;
import org.junit.Before;
import org.junit.Test;

import com.enation.app.base.core.service.solution.ISetupCreator;
import com.enation.framework.test.SpringTestSupport;

public class SetupCreatorTest extends SpringTestSupport {
	
	private ISetupCreator setupCreator;
	
	@Before
	public void mock(){
		setupCreator = this.getBean("setupCreator");
	}
	
	@Test
	public void testAddClean(){
		Document document = setupCreator.createSetup("d:/test.xml");
		setupCreator.addTable(document, "clean", "es_test");
		setupCreator.addTable(document, "clean", "es_site");
		setupCreator.addTable(document, "clean", "es_menu");
		setupCreator.save(document, "d:/test.xml");
	}
	
	@Test
	public void testList(){
		Document document = setupCreator.createSetup("d:/test.xml");
		List listClean = document.getRootElement().element("clean").elements();
		for(Object o:listClean){
			org.dom4j.Element table = (org.dom4j.Element)o;
			//System.out.println(table.getText());
		}
	}

}
