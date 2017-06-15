package com.enation.newtest;

import com.enation.framework.test.SpringTestSupport;


public class DataImportTest extends SpringTestSupport {
	
// 
//	private void importTask(Element mEl){
//		
//		List<Element> taskElList =mEl.elements();
//		
//		for(Element taskEl:taskElList){
//			String beanid  = taskEl.attributeValue("id");
//			ITask importTask = this.getBean(beanid);
//			importTask.execute(taskEl );
//		}
//	}
//	
//	
//	
//	@Test
//	public void testImport1() throws DocumentException{
//		SourceDaoSupportHolder.createDaoSupport("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/wine?useUnicode=true&characterEncoding=utf8", "root", "kingapex");
//	 
//		String xml = FileUtil.readFile("com/enation/app/shop/component/importer/ecshop.xml");
//       Document doc = DocumentHelper.parseText(xml);           
//       Element root = doc.getRootElement();
//       List<Element> moduleList =  root.elements("module");
//       
//       for(Element mEl:moduleList){
//    	   String id =mEl.attribute("id").getValue();
//    	   if("order".equals(id)){
//    		   importTask(mEl);
//       }       
// 
//			
//	}
//	
//	
//	//@Test
//	public void test2(){
//		GoodsCatPathCreateTask categoryPathCreateTask = this.getBean("ecShopCategoryPathCreateTask") ;
//		categoryPathCreateTask.execute(null);
//	}
//	
	
}
