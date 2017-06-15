package com.enation.test.eop.test.widget;

import net.sf.json.JSONObject;

import org.junit.Test;

public class WidgetContentBuilderTest {

	@Test
	public void testSimpleContentBuilder() {
		/*String json = "{'status':0,'type':1,'body':{\"className\":\"\",\"style\":\"\",\"target\":\"\",\"text\":\"abc\",\"url\":\"\"},'havetitle':'true',title:{\"className\":\"\",\"havemore\":false,\"more\":null,\"style\":\"height:30px;background-color:red\",\"target\":\"\",\"text\":\"最新商品\",\"url\":\"\"}}";
		IWidgetContentBuilder builder = new SimpleContentBuilder();
		//System.out.println(builder.build(json));*/
		
//		json ="{'status':0,'type':2,'body':[{\"className\":\"\",\"style\":\"\",\"target\":\"\",\"text\":\"新闻1\",\"url\":\"\"},{\"className\":\"\",\"style\":\"\",\"target\":\"\",\"text\":\"新闻2\",\"url\":\"\"},{\"className\":\"\",\"style\":\"\",\"target\":\"\",\"text\":\"新闻3\",\"url\":\"\"}],'havetitle':'false'}";
//		builder = new ListContentBuilder();
//		//System.out.println(builder.build(json));
	}

	//@Test
	public void testJson(){
		String json ="{'status':0,'type':2,'body':[{\"className\":\"\",\"style\":\"\",\"target\":\"\",\"text\":\"新闻1\",\"url\":\"\"},{\"className\":\"\",\"style\":\"\",\"target\":\"\",\"text\":\"新闻2\",\"url\":\"\"},{\"className\":\"\",\"style\":\"\",\"target\":\"\",\"text\":\"新闻3\",\"url\":\"\"}],'havetitle':'false'}";
		JSONObject jsonObject = JSONObject.fromObject(json);
		//System.out.println(jsonObject.get("ssdfsdfsdfsdfsdf"));
	}
}
