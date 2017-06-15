package com.enation.eop.sdk.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.enation.framework.image.ThumbnailCreatorFactory;
import com.enation.framework.util.StringUtil;

public class EopSetting {
	
	public static String VERSION="";				//版本号配置
	
	public static String DBTYPE ="1" ; 				//数据库类型配置（1:mysql, 2:oracle, 3:sqlserver）
	
	public static String  FILE_STORE_PREFIX ="fs:"; //本地文件存储前缀
	
	public static String INSTALL_LOCK ="NO"; 		//是否已经安装数据库
	
	public static boolean IS_DEMO_SITE=false; 		//是否是演示站
	
	public static String PRODUCT="b2b2c";			//产品类型配置
	
	public static final String DEMO_SITE_TIP="为保证示例站点完整性，禁用此功能，请下载war包试用完整功能。";
	
	public static String  THUMBNAILCREATOR ="javaimageio";//缩略图组件配置
	
	
	/**
	 * 从配置文件中读取相关配置<br/>
	 * 如果没有相关配置则使用默认
	 */
	 static {
		 try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	
	 /**
	  * @description 初始化方法
	  * @date 2016年9月7日 上午10:51:54
	  * @throws Exception
	  */
	 public static void init() throws Exception {

		 //加载配置文件
		 String path = StringUtil.getRootPath();
		 path = path+"/config/eop.properties";
		
		 InputStream in  = new FileInputStream(new File(path));
		 Properties props = new Properties();
		 props.load(in);
		
		 //是否为演示站
		 String is_demo_site =  props.getProperty("is_demo_site");
		 IS_DEMO_SITE = "yes".equals(is_demo_site) ? true : false;	
		
		 //数据库类型配置
		 String dbtype = props.getProperty("dbtype");
		 DBTYPE=  StringUtil.isEmpty(dbtype) ? DBTYPE : dbtype;
		
		 //版本号配置
		 VERSION = props.getProperty("version");
		 if(VERSION==null) VERSION="";
		
		 //产品配置
		 PRODUCT = props.getProperty("product");
		 if(PRODUCT==null) {
			 PRODUCT="b2c";
		 }
		 if(PRODUCT.equals("b2b2c")) {
			 PRODUCT="b2b2c";
		 }

		 //设置缩略图组件
		 String thumbnailcreator = props.getProperty("thumbnailcreator");
		 THUMBNAILCREATOR = StringUtil.isEmpty(thumbnailcreator)?THUMBNAILCREATOR:thumbnailcreator;
		 ThumbnailCreatorFactory.CREATORTYPE = THUMBNAILCREATOR;
		
		 //是否已经安装数据库
		 File installLockFile = new File(StringUtil.getRootPath()+"/install/install.lock");
		 INSTALL_LOCK = installLockFile.exists() ? "YES" : "NO"; //如果存在则不能安装,如果不存在，则认为是全新的，跳到install页
			
	 }
	
	
	
//	/**
//	 * 初始化安全url
//	 * 这些url不用包装 safeRequestWrapper
//	 */
//	private static void initSafeUrl(){
//		
//		try{
//			//加载url xml 配置文档
//			DocumentBuilderFactory factory = 
//		    DocumentBuilderFactory.newInstance();
//		    DocumentBuilder builder = factory.newDocumentBuilder();
//		    Document document = builder.parse(FileUtil.getResourceAsStream("safeurl.xml"));
//		    NodeList urlNodeList = document.getElementsByTagName("urls").item(0).getChildNodes();
//		    safeUrlList = new ArrayList<String>();
//		    for( int i=0;i<urlNodeList.getLength();i++){
//		    	Node node =urlNodeList.item(i); 
//		    	safeUrlList.add(node.getTextContent() );
//		    }
//		    
//		}catch(IOException e){
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		} catch (SAXException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}
	 	
}
