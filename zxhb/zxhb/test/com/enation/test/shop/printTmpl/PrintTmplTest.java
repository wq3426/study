package com.enation.test.shop.printTmpl;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enation.app.shop.core.model.PrintTmpl;
import com.enation.app.shop.core.service.IPrintTmplManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.test.SpringTestSupport;

/**
 * 打印模板测试
 * 
 * @author lzf<br/>
 *         2010-5-4上午11:10:03<br/>
 *         version 1.0
 */
public class PrintTmplTest extends SpringTestSupport {

	private IPrintTmplManager printTmplManager;
	private IDBRouter shopSaasDBRouter;

	@Before
	public void mock() {
		this.printTmplManager = this.getBean("printTmplManager");
		shopSaasDBRouter = this.getBean("shopSaasDBRouter");
	 
	}

	/**
	 * 清除数据并建立表结构
	 */
	private void clean() {

		this.jdbcTemplate.execute("drop table if exists js_print_tmpl_2");
//		shopSaasDBRouter.createTable( "print_tmpl");

	}

	/**
	 * 测试添加
	 */
	@Test
	public void testAdd() {

		this.clean();
		PrintTmpl printTmpl = new PrintTmpl();
		printTmpl.setDisabled("false");
		printTmpl.setPrt_tmpl_title("EMS");
		printTmpl.setPrt_tmpl_width("250");
		printTmpl.setPrt_tmpl_height("150");
		printTmpl.setShortcut("true");
		printTmpl
				.setPrt_tmpl_data("<printer picpos='0:0'><items><name>收货人-姓名</name><ucode>ship_name</ucode><font>Times New Roman</font><fontsize>14</fontsize><letterspace>0</letterspace><bold>false</bold><italic>false</italic><align>left</align><position>489:129:106.25:26.25</position></items></printer>");
		this.printTmplManager.add(printTmpl);

		// 断言
		PrintTmpl printTmplDB = printTmplManager.get(1);
		Assert.assertEquals(printTmplDB.getPrt_tmpl_title(), "EMS");
		Assert.assertEquals(printTmplDB.getShortcut(), "true");
		Assert.assertEquals(printTmplDB.getDisabled(), "false");
	}

	/**
	 * 测试修改
	 */
	@Test
	public void testEdit() {
		this.testAdd(); // 模拟数据

		PrintTmpl printTmpl = new PrintTmpl();
		printTmpl.setPrt_tmpl_id(1);
		printTmpl.setPrt_tmpl_title("顺丰速运");
		this.printTmplManager.edit(printTmpl);

		// 断言
		PrintTmpl printTmplDB = printTmplManager.get(1);
		Assert.assertEquals(printTmplDB.getPrt_tmpl_title(), "顺丰速运");
		Assert.assertEquals(printTmplDB.getShortcut(), "true");
		Assert.assertEquals(printTmplDB.getDisabled(), "false");
	}

	/**
	 * 测试删除
	 */
	@Test
	public void testDelete() {

		this.testAdd();
		printTmplManager.delete(null);

		// 断言
		PrintTmpl printTmplDB = printTmplManager.get(1);
		Assert.assertEquals(printTmplDB.getPrt_tmpl_title(), "EMS");
		Assert.assertEquals(printTmplDB.getShortcut(), "true");
		Assert.assertEquals(printTmplDB.getDisabled(), "false");

		printTmplManager.delete(new Integer[] { 1 });
		int count = this.jdbcTemplate
				.queryForInt("select count(0) from js_print_tmpl_2 where disabled = 'false'");
		Assert.assertEquals(count, 0);

	}

	/**
	 * 测试清除
	 */
	@Test
	public void testClean() {

		this.testDelete();

		printTmplManager.clean(new Integer[] { 1 });
		int count = this.jdbcTemplate
				.queryForInt("select count(0) from js_print_tmpl_2");
		Assert.assertEquals(count, 0);

	}

	/**
	 * 测试还原
	 */
	@Test
	public void testRevert() {

		this.testDelete();

		printTmplManager.revert(new Integer[] { 1 });
		int count = this.jdbcTemplate
				.queryForInt("select count(0) from js_print_tmpl_2 where disabled = 'false'");
		Assert.assertEquals(count, 1);

	}

	/**
	 * 测试列表
	 */
	@Test
	public void testList() {

		this.testAdd();

		List list = printTmplManager.list();
		Assert.assertEquals(((PrintTmpl) list.get(0)).getPrt_tmpl_title(),
				"EMS");

	}

	/**
	 * 测试回收站列表
	 */
	@Test
	public void testTrash() {

		this.testDelete();
		List list = printTmplManager.trash();
		Assert.assertEquals(((PrintTmpl) list.get(0)).getPrt_tmpl_title(),
				"EMS");

	}

	/**
	 * 测试启用模板列表
	 */
	@Test
	public void testListCanUse() {

		this.testAdd();
		PrintTmpl printTmpl = new PrintTmpl();
		printTmpl.setDisabled("false");
		printTmpl.setPrt_tmpl_title("顺丰速运");
		printTmpl.setPrt_tmpl_width("250");
		printTmpl.setPrt_tmpl_height("150");
		printTmpl.setShortcut("true");
		printTmpl
				.setPrt_tmpl_data("<printer picpos='0:0'><items><name>收货人-姓名</name><ucode>ship_name</ucode><font>Times New Roman</font><fontsize>14</fontsize><letterspace>0</letterspace><bold>false</bold><italic>false</italic><align>left</align><position>489:129:106.25:26.25</position></items></printer>");
		this.printTmplManager.add(printTmpl);
		List list = printTmplManager.listCanUse();
		Assert.assertEquals(((PrintTmpl) list.get(0)).getPrt_tmpl_title(),
				"EMS");
		Assert.assertEquals(((PrintTmpl) list.get(1)).getPrt_tmpl_title(),
				"顺丰速运");

	}
	
	@Test
	public void formatTest(){
		//Double d = new Double(12345678.9);
		////System.out.println(StringUtil.toCurrency(d));
		Integer i = null;
		//System.out.println(String.valueOf(i));
	}

}
