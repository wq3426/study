package com.enation.test.db;

import org.junit.Test;

import com.enation.app.base.core.model.MultiSite;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.test.SpringTestSupport;

public class GetLastIdTest extends SpringTestSupport {
	
	@Test
	public void test(){
		String sql ="select max(code) code from es_site_1_1 where parentid=? ";
		IDaoSupport  baseDaoSupport = this.getBean("daoSupport");
		int code = baseDaoSupport.queryForInt(sql,2);
		//System.out.println(code);
		
//		MultiSite site  = new MultiSite();
//		site.setCode(10000);
//		site.setDomain("www.173.co");
//		site.setName("test");
//		site.setParentid(0);
//		site.setThemeid(1);
//		
//		baseDaoSupport.insert("site", site);
//		int siteid = baseDaoSupport.getLastId("site");
//		//System.out.println(siteid);
	}
}
