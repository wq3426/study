package com.enation.test.fastdfs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.FileInfo;
import org.junit.Test;

import com.enation.app.fastdfsclient.utils.FastDfsClientUtils;
import com.enation.framework.util.StringUtil;

/**
 * @Description FastDFS分布式文件系统--使用JavaClient测试类
 *
 * @createTime 2016年8月18日 下午6:43:32
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public class FastDfsClientTest{
	
    //本地文件，要上传的文件
	public String local_filename = "F:\\download\\824622923181355917.png";
	
	/**
	 * @description 使用文件绝对路径上传文件--不设置属性信息    
	 * @date 2016年8月18日 下午8:06:07
	 */
	@Test
	public void testUploadFile() {	
		try {
			FastDfsClientUtils clientUtils = FastDfsClientUtils.getInstance();
			String fileId = clientUtils.uploadFile(local_filename);
			System.out.println(fileId);//  group1/M00/00/00/wKgByFe2a7KAJk4AAAAkrVsG3sc552.jpg
						
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * @description 使用文件绝对路径上传文件--设置属性信息
	 * @date 2016年8月18日 下午8:07:05
	 */
	@Test
    public void testUploadFileWithMetas() {	
    	try {		
			//设置文件属性
			NameValuePair nvp [] = new NameValuePair[] { 
			     new NameValuePair("name", "defualtImg") //group1/M00/00/00/ezk5kVft4AGAJ6EQAAAU083oZ9M566.png
			     //new NameValuePair("width", "80"),
			     //new NameValuePair("height", "90")
			 }; 
			
			//上传文件
			FastDfsClientUtils clientUtils = FastDfsClientUtils.getInstance();
			String fileId = clientUtils.uploadFile(local_filename, nvp);		
			System.out.println(fileId);//  group1/M00/00/00/wKgByFe2a8-AdnEsAAAkrVsG3sc620.jpg
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * @description 直接使用File类上传文件--不设置属性值
	 * @date 2016年8月18日 下午8:08:36
	 */
	@Test
	public void testUploadFile_byte() {
		try {			
			File file = new File(local_filename);		
			String fileName = "Audi.jpg";		
			
			//上传文件
			FastDfsClientUtils clientUtils = FastDfsClientUtils.getInstance();
	        String fileId = clientUtils.uploadFile(file, fileName);
	        System.out.println(fileId);//  group1/M00/00/00/wKgByFe2a-CACzNIAAAkrVsG3sc554.jpg
	        
		} catch (IOException e) {
		    e.printStackTrace();   
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * @description 直接使用File类上传文件--设置属性值
	 * @date 2016年8月18日 下午8:10:51
	 */
	@Test
	public void testUploadFile_byte_withMatas() {
		try {			
			File file = new File(local_filename);	//文件类，由前端直接传递	
			String fileName = "Audi.jpg";			//文件名，由前端直接传递
			
			
			//设置文件属性
			NameValuePair nvp [] = new NameValuePair[] { 
			     new NameValuePair("test2", "002"), 
			     new NameValuePair("width", "100"),
			     new NameValuePair("height", "80")
			 }; 
			
			//上传文件
			FastDfsClientUtils clientUtils = FastDfsClientUtils.getInstance();
	        String fileId = clientUtils.uploadFile(file, fileName, nvp);
	        System.out.println(fileId);//  group1/M00/00/00/wKgByFe2a_CADl7xAAAkrVsG3sc937.jpg
	        
		} catch (IOException e) {
			    e.printStackTrace();   
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @description 下载文件到本地磁盘
	 * @date 2016年8月19日 上午10:31:59
	 */
	@Test
	public void testDownloadFile_0() {
		String fileId = "group1/M00/00/00/wKgByFe2a_CADl7xAAAkrVsG3sc937.jpg";
		String path = "F:\\download\\";
		String fileName = "奥迪";
				
        String extName = fileId.substring(fileId.lastIndexOf("."));//获取文件扩展名，包含.	
		FileOutputStream fileOutputStream = null;
		try {
			//下载文件
			FastDfsClientUtils clientUtils = FastDfsClientUtils.getInstance();
			byte[] fileContent = clientUtils.downloadFile(fileId);
			
			File file = new File(path + fileName + extName);
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(fileContent);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				fileOutputStream.close();//关闭流
			} catch (IOException e) {
				e.printStackTrace();
			}		
		}
		
		/*HttpServletResponse response  = ThreadContextHolder.getHttpResponse();		
		OutputStream toClient = null;
		try {
			//下载文件
			FastDfsClientUtils clientUtils = new FastDfsClientUtils();
			byte[] fileContent = clientUtils.downloadFile(fileId);
			
			toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(fileContent);
            toClient.flush();        			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			toClient.close();
		}*/
	}
	
	/**
	 * @description 下载文件
	 * @date 2016年8月19日 上午10:32:29
	 */
	@Test
	public void testDownloadFile_1() {
		String fileId = "group1/M00/00/00/wKgByFe2a_CADl7xAAAkrVsG3sc937.jpg";
		String localFileName = "F:\\download\\AudiLogo.jpg";
		
		try {
			//下载文件
			FastDfsClientUtils clientUtils = FastDfsClientUtils.getInstance();
			int result = clientUtils.downloadFile(fileId, localFileName);
			System.out.println(result);	
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * @description 获取文件信息
	 * @date 2016年8月19日 上午11:01:39
	 */
	@Test
	public void testGetFileInfo() {
		String fileId = "group1/M00/00/00/wKgByFe2a8-AdnEsAAAkrVsG3sc620.jpg";
		
		try {
			//获取文件信息
			FastDfsClientUtils clientUtils = FastDfsClientUtils.getInstance();
			FileInfo fileInfo = clientUtils.getFileInfo(fileId);
			
			System.out.println(fileInfo.getSourceIpAddr());   //192.168.1.200
			System.out.println(fileInfo.getFileSize());       //9389
			System.out.println(fileInfo.getCreateTimestamp());//Fri Aug 19 10:16:16 CST 2016
			System.out.println(fileInfo.getCrc32());          //1527176903
				
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @description 获取文件自定义的mate信息
	 * @date 2016年8月19日 上午11:04:04
	 */
	@Test
	public void testGetMataData() {
		String fileId = "group1/M00/00/00/wKgByFe2a_CADl7xAAAkrVsG3sc937.jpg";
		
		try {
			//获取文件属性信息
			FastDfsClientUtils clientUtils = FastDfsClientUtils.getInstance();
			NameValuePair[] metadatas = clientUtils.getMetadata(fileId);
			
			if(metadatas!=null){
            	for(NameValuePair metadata : metadatas){ 
                    System.out.println(metadata.getName() + ":" + metadata.getValue()); 
                    // height:80
                    // test2:002
                    // width:100
                } 
            }
				
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
	/**
	 * @description 删除文件
	 * @date 2016年8月23日 下午3:26:09
	 */
	@Test
	public void testDeleteFile() {
		String fileId = "group1/M00/00/00/wKgByFe2a8-AdnEsAAAkrVsG3sc620.jpg";
		
		try {
			//删除文件
			FastDfsClientUtils clientUtils = FastDfsClientUtils.getInstance();
			int result = clientUtils.deleteFile(fileId);		
			System.out.println(result);
				
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
	
	@Test
	public void testPath(){
		String path  = StringUtil.getRootPath().replace("/", "\\");
		path = path+"\\WebContent\\WEB-INF\\config\\fdfs_client.conf";
		System.err.println(path);
	}
	
}
