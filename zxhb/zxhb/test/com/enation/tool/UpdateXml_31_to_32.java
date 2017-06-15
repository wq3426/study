package com.enation.tool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

public class UpdateXml_31_to_32 {
	private final String xmlIn = "D:\\shares\\example_data.xml";
	private final String xmlOut = "D:\\shares\\example_new.xml";
	
	private Document xmlDoc;
	
	private class KeyValueBean {
		private String key;
		private String value;
		
		public KeyValueBean(){
			
		}
		
		public KeyValueBean(String key,String value) {
			this.key = key;
			this.value = value;
		}
		
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
	public String encode(String text) {
		text = text.replaceAll("&", "&amp;");
		text = text.replaceAll("<", "&lt;");
		text = text.replaceAll(">", "&gt;");
		return text;
	}
	
	private String printList(List list,String table){
		String fields = "";
		String values = "";
		
		StringBuilder outXml = new StringBuilder();
                
        for (int i=0;i<list.size();i++) {
        	KeyValueBean bean = (KeyValueBean)list.get(i);
            fields = fields + bean.getKey() + ",";
            values = values + bean.getValue() + ",";
        }
        fields = fields.substring(0,fields.length()-1);
        values = values.substring(0, values.length()-1);
        
        outXml.append("<action>\n");
        outXml.append("\t<command>insert</command>\n");
        outXml.append("\t<table>" + table + "</table>\n");
        outXml.append("\t<fields>" + encode(fields) + "</fields>\n");
        outXml.append("\t<values>" + encode(values) + "</values>\n");
        outXml.append("</action>\n");
        
        return outXml.toString();
	}
	
	private void parseIt(){
		xmlDoc.getRootElement().elements("action");
	
		StringBuilder outXml = new StringBuilder();
		List<Element> actions = xmlDoc.getRootElement().elements("action");
		for (Element action : actions) {
			String table = action.elementText("table");
			if("es_goods".equals(table)) {
				String[] fields = action.elementText("fields").split(",");
				String[] values = action.elementText("values").split(",");
				List list  = new ArrayList();

				String image_default = "";
				String image_file = "";
				
				for(int i=0;i<fields.length;i++) {
					if("image_default".equals(fields[i])){
						image_default = values[i];
						////System.out.println(image_default);
					} else if("image_file".equals(fields[i])) {
						image_file = values[i];
					} else if("isgroup".equals(fields[i]) || "islimit".equals(fields[i]) || "istejia".equals(fields[i]) || "no_discount".equals(fields[i])) {
						//	删除字段
					} else {
						list.add(new KeyValueBean(fields[i],values[i]));
					} 
				}
				
				list.add(new KeyValueBean("thumbnail",image_default.replaceAll(".jpg", "_thumbnail.jpg")));
				list.add(new KeyValueBean("big",image_default.replaceAll(".jpg", "_big.jpg")));
				list.add(new KeyValueBean("small",image_default.replaceAll(".jpg", "_small.jpg")));
				list.add(new KeyValueBean("original",image_default));
				
				outXml.append(printList(list,table));
			}
		}
		
		writeOut(outXml.toString());
	}
	
	private void writeOut(String out) {
		BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(new File(xmlOut)));
			output.write(out);
			output.close();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void doUpdate(){
		SAXReader saxReader=new SAXReader();
		try {
			xmlDoc = saxReader.read(new FileInputStream(new File(xmlIn)));
			parseIt();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
