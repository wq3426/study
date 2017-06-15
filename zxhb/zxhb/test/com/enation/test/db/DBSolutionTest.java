package com.enation.test.db;

import java.beans.PropertyVetoException;

import org.junit.Before;
import org.junit.Test;

import com.enation.app.base.BaseApp;
import com.enation.app.base.core.service.IDataSourceCreator;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.app.base.core.service.dbsolution.IDBSolution;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.test.SpringTestSupport;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBSolutionTest extends SpringTestSupport {
	protected String[] tables;
	protected IDaoSupport baseDaoSupport;
	
	
	
	@Before
	public void prepareData() {
	//	tables = new String[1];
	//	tables[0] = "es_group_buy_cat";
		//tables[1] = "es_spec_values";
	  //tables[1] = "eop_site";
	  //tables[2] = "eop_user";
	}
	
	

	protected void changeToOracle() {
		ComboPooledDataSource dataSource = (ComboPooledDataSource) jdbcTemplate
				.getDataSource();
		try {
			dataSource.setDriverClass("oracle.jdbc.driver.OracleDriver");
			dataSource.setJdbcUrl("jdbc:oracle:thin:@localhost:1521:XE");
			dataSource.setUser("javashop");
			dataSource.setPassword("752513");
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void changeToMySQL() {
		IDataSourceCreator dataSourceCreator = this
				.getBean("dataSourceCreator");
		jdbcTemplate
				.setDataSource(dataSourceCreator
						.createDataSource(
								"com.mysql.jdbc.Driver",
								"jdbc\\:mysql\\://127.0.0.1\\:3306/javashop?useUnicode\\=true&characterEncoding\\=utf8",
								"root", "752513"));
	}
	
	

	protected void testExport() {
		tables = new String[1];
		tables[0] = "es_product_store";
		System.out.println(DBSolutionFactory.dbExport(tables, false, ""));
	}

	protected void testImport() {
		DBSolutionFactory.dbImport("/Users/kingapex/workspace/v40/javamall/WebContent/products/simple/auth.xml","es_");
	}
	
	@Test	
	public void testDBSolution() {
		//testExport();
		testImport();
	}
}
