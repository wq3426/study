package com.enation.test.db;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.test.SpringTestSupport;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DaoSupportTest extends SpringTestSupport {
	private IDaoSupport<User> daoSupport;
	
	
	@Test
	public void mytest() throws SQLException{
		JdbcTemplate jdbcTemplate = this.getBean("jdbcTemplate");
	
		for(int i=1;i<=19;i++){
		
			 ComboPooledDataSource ds =(ComboPooledDataSource ) jdbcTemplate.getDataSource();
			 Connection con = DataSourceUtils.getConnection( ds);
	 
			 con.close();
	 
			//System.out.println(i+"->"+con);
			
		}
	}
	
	/**
	 * 建立表结构
	 * 
	 */
	// @Before
	public void beforeTest() {
		daoSupport= (IDaoSupport)this.getBean("daoSupport");
		this.jdbcTemplate.execute("drop table if exists enation_user;");
		this.jdbcTemplate
				.execute("create table enation_user(user_id int not null auto_increment,username  varchar(50),password  varchar(50),primary key (user_id));");
	}
	

	/**
	 * 插入测试的断言
	 * 
	 */
	private void assertInsert() {
		Map user_map = this.jdbcTemplate
				.queryForMap("select * from enation_user");
		assertEquals("王峰", user_map.get("username"));
		assertEquals("test", user_map.get("password"));
	}
	
	

	/**
	 * 更新测试的断言
	 * 
	 */
	private void assertUpdate() {
		Map user_map = this.jdbcTemplate
				.queryForMap("select * from enation_user");
		assertEquals("王峰1", user_map.get("username"));
		assertEquals("test1", user_map.get("password"));
	}
	
	
	/**
	 * 测试读取一个实体
	 */
	//@Test
	public void testGet(){
 		this.testIntertPo();
		User user = daoSupport.queryForObject("select * from enation_user where user_id=?", User.class, 1);
		assertEquals("王峰",user.getUsername());
	}
	
	
	

	/**
	 * 测试可以将一个Po实体的相应数据插入数据库中的情况
	 * 
	 */
	@Test
	public void testIntertPo() {

		User user = new User(); 
		user.setUsername("王峰");
		user.setPassword("test");
 
		daoSupport.insert("enation_user", user);

		assertInsert();

	}
	

	/**
	 * 测试可将一个map形式的数据插入到数据库
	 * 
	 */
	//@Test
	public void testIntertMap() {

		Map data = new HashMap();
		data.put("username", "王峰");
		data.put("password", "test");

		this.daoSupport.insert("enation_user", data);
	}

	
	/**
	 * 测试更新数据库
	 *数据通过Map传递 ，条件通过字串传递
	 */
	//@Test
	public void testUpdate1() {
		 
		HashMap data = new HashMap();
		data.put("username", "王峰1");
		data.put("password", "test1");

		 
		daoSupport.update("enation_user", data, "user_id=1");
		assertUpdate();
		
	}
	
	
	/**
	 * 测试更新数据库
	 *数据通过Map传递 ，条件通过Map递
	 */
	//@Test
	public void testUpdate2() {
	 
		HashMap data = new HashMap();
		data.put("username", "王峰1");
		data.put("password", "test1");

		HashMap where =new HashMap();
		where.put("user_id", "1");
		
		 
		daoSupport.update("enation_user", data, where);
		assertUpdate();
		
	}
	
	

	/**
	 * 测试更新数据库
	 *数据通过po实体传递 ，条件通字串传递
	 */
	//@Test
	public void testUpdate3() {
	 
		User user = new User();
		user.setUsername("王峰1");
		user.setPassword("test1");
		
		 
		daoSupport.update("enation_user", user, "user_id=1");
		assertUpdate();
		
	}
	
	
	/**
	 * 测试更新数据库
	 *数据通过po实体传递 ，条件通Map传递
	 */
	//@Test
	public void testUpdate4() {
		
		User user = new User();
		user.setUsername("王峰1");
		user.setPassword("test1");
		
		HashMap where =new HashMap();
		where.put("user_id", "1");
	 
		daoSupport.update("enation_user", user, where);
		assertUpdate();
		
	}
	

	
	//@Test
	public void testPage(){
 
		Page page = daoSupport.queryForPage("select * from demo_weixiu t", 1, 5);
		assertEquals( ((List)page.getResult()).size() ,5);
	}
		
	
	public void beforeTestList(){
		for(int i=0;i<10;i++){
			User user = new User(); 
			user.setUsername("王峰"+i);
			user.setPassword("test");
			daoSupport.insert("enation_user", user);
		}
	}
	
	
	/**
	 * 测试列表获取
	 */
	@Test
	public void testList(){
		beforeTestList();
		
		String sql  ="select * from enation_user";
		List<User> userList = this.daoSupport.queryForList(sql, User.class);
		assertEquals( userList.size() ,10);
		
		for(User user:userList){
			//System.out.println("user["+ user.getUsername()+"]");
		}
		
	}

}
