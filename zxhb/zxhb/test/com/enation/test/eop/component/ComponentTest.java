package com.enation.test.eop.component;

import org.junit.Test;

import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.framework.component.IComponent;
import com.enation.framework.test.SpringTestSupport;

public class ComponentTest extends SpringTestSupport {
	private IComponent component;
	
	public void exportDB(){
		String[] tables = {"receipt"}; 
		//System.out.println(DBSolutionFactory.dbExport(tables, false, "es_"));
	}

	public void componentInstall() {
		component = this.getBean("orderReturnsComponent");
		component.install();
	}
	
	@Test
	public void doTest(){
		//componentInstall();
		exportDB();
	}
}
