package com.enation.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.enation.framework.test.SpringTestSupport;

public class UpdateData extends SpringTestSupport {
	public static final String table = "es_car_article_13_54";
	public static final String path = "C:\\Users\\liuzy\\Desktop\\hxstock.sql";

	public void insertIntoDB() {
		File file = new File(path);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			int count = 0;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				if (tempString.startsWith("INSERT INTO " + table)) {
					tempString = tempString.replaceAll("_<userid>", "");
					tempString = tempString.replaceAll("_<siteid>", "");
					jdbcTemplate.execute(tempString);
					//System.out.println("正在插入记录，行号： " + line);
					count++;
				}
				line++;
			}
			reader.close();
			//System.out.println("共插入记录：" + count + "条");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {

				}
			}
		}
	}

	public void addFields() {
		jdbcTemplate.execute("alter table " + table + " add n_add_time int(11)");
		jdbcTemplate.execute("alter table " + table + " add n_lastmodified int(11)");
	}

	public void updateFields() {
		List list = jdbcTemplate.queryForList("select * from " + table);
		String ext_sql = "";
		for (int i = 0; i < list.size(); i++) {
			Map map = (Map)list.get(i);
			
			Timestamp date = (Timestamp)map.get("add_time");
			long addtime = date.getTime() / 1000;

			Timestamp date2 = (Timestamp)map.get("lastmodified");
			if(date2!=null){
				long lastmodified = date2.getTime() / 1000;
				ext_sql = ",n_lastmodified=" + lastmodified;
			} else
				ext_sql = "";

			jdbcTemplate.update("update " + table + " set n_add_time=" + addtime + ext_sql + " where id=" + map.get("id"));
			//System.out.println("正在更新第" + i + "条，共计：" + list.size() + "条，日期：[" + date + "]，数字日期：[" + addtime + "]");
			
		}
	}

	@Test
	public void doUpdate() {
		//insertIntoDB();		// 第一步：读取数据文件，将相关数据插入指定表中
		addFields();		// 第二步：增加所需字段
		updateFields();		// 第三步：更新新增字段
		/*
		 * List list = this.jdbcTemplate.queryForList("select * from es_goods");
		 * for (int i = 0; i < list.size(); i++) { Map map = (Map) list.get(i);
		 * //System.out.println(map.get("name")); }
		 */
	}
}
