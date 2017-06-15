package com.enation.app.fastdfsclient.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

import com.enation.app.fastdfsclient.config.FastDfsClientConfig;

/**
 * @Description FastDFS分布式文件系统Java-Client
 *
 * @createTime 2016年8月18日 下午2:17:04
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */

public class FastDfsClientUtils {

	protected final Logger logger = Logger.getLogger(getClass());
	
	private TrackerClient trackerClient = null;
	private TrackerServer trackerServer = null;
	private StorageServer storageServer = null;
	private StorageClient1 storageClient = null;
		
    private FastDfsClientUtils() {
		String conf = FastDfsClientConfig.CONF_FILENAME;
		if (conf.contains("classpath:")) {
			conf = conf.replace("classpath:", this.getClass().getResource("/").getPath());
		}
		
		try{
			ClientGlobal.init(conf);
			trackerClient = new TrackerClient();
			trackerServer = trackerClient.getConnection();
			storageClient = new StorageClient1(trackerServer, storageServer);
			
		}	
		catch (FileNotFoundException e) {
			logger.error("配置文件未找到");
			e.printStackTrace();
		 
		} catch (IOException e) {
			logger.error("连接服务器失败");
			e.printStackTrace();
			
		} catch (MyException e) {
			logger.error("初始化全局客户端失败");
			e.printStackTrace();
		} 
				
	}

    /**
     * 创建单实例
     */
    private static FastDfsClientUtils clientUtils = new FastDfsClientUtils();	
	public static FastDfsClientUtils getInstance()
	{
		return clientUtils;
	}
	
	/**
	 * @description 上传文件
	 * @date 2016年8月18日 下午2:54:27
	 * @param fileName 文件名（绝对路径）
	 * @return String
	 * @throws IOException
	 * @throws Exception
	 */
	public String uploadFile(String fileName) throws IOException, Exception {	
		String extName = fileName.substring(fileName.lastIndexOf(".")+1);//获取文件扩展名		
		String result = storageClient.upload_file1(FastDfsClientConfig.GROUPNAME, fileName, extName, null);//上传文件
		return result;
	}
	
	/**
	 * @description 上传文件
	 * @date 2016年8月18日 下午2:22:54
	 * @param fileName 文件名（带绝对路径）
	 * @param metas	      文件属性信息
	 * @throws Exception
	 * @throws IOException
	 * @return String
	 */
	public String uploadFile(String fileName, NameValuePair[] metas) throws IOException, Exception {	
		String extName = fileName.substring(fileName.lastIndexOf(".")+1);//获取文件扩展名		
		String result = storageClient.upload_file1(FastDfsClientConfig.GROUPNAME,fileName, extName, metas);//上传文件
		return result;
	}
	
	/**
	 * @description 上传文件
	 * @date 2016年8月18日 下午2:55:24
	 * @param fileContent 文件内容，字节数组
	 * @param extName 文件扩展名
	 * @param metas   文件属性信息
	 * @return String 
	 * @throws IOException
	 * @throws Exception
	 */
	public String uploadFile(File file, String fileName, NameValuePair[] metas) throws IOException, Exception {	
		String extName = fileName.substring(fileName.lastIndexOf(".")+1);//获取文件扩展名	
		byte[] fileContent = getContent(file);//获取文件内容
		String result = storageClient.upload_file1(FastDfsClientConfig.GROUPNAME, fileContent, extName, metas);
		return result;
	}
	
	/**
	 * @description 上传文件
	 * @date 2016年8月18日 下午3:09:52
	 * @param fileContent 文件内容，字节数组
	 * @param extName 文件扩展名
	 * @return String
	 * @throws IOException
	 * @throws Exception
	 */
	public String uploadFile(File file, String fileName) throws IOException, Exception {	
		String extName = fileName.substring(fileName.lastIndexOf(".")+1);//获取文件扩展名
		byte[] fileContent = getContent(file);//获取文件内容
		String result = storageClient.upload_file1(FastDfsClientConfig.GROUPNAME, fileContent, extName, null);
		return result;
	}
	
	/**
	 * @description 下载文件
	 * @date 2016年8月18日 下午4:16:35
	 * @param fileId 文件名（包含组名）
	 * @param path 文件保存路径
	 * @param fileName 文件名（不包含扩展名）
	 * @return int （0：success; !0:fail）
	 * @throws IOException
	 * @throws Exception
	 */
	public byte[] downloadFile(String fileId) throws IOException, Exception
	{					
		byte[] fileContent = storageClient.download_file1(fileId);
		return fileContent;
	}
	
	/**
	 * @description 下载文件
	 * @date 2016年8月18日 下午4:19:57
	 * @param fileId 文件id（包含组名）
	 * @param localFilename 本地文件名（文件的保存路径+文件名，文件名包含扩展名）
	 * @return int （0：success, !0：fail）
	 * @throws IOException
	 * @throws Exception
	 */
	public int downloadFile(String fileId, String localFilename) throws IOException, Exception
	{		
		int result = storageClient.download_file1(fileId, localFilename);
		return result;	
	}
	
	/**
	 * @description 获取文件信息
	 * @date 2016年8月18日 下午4:51:33
	 * @param fileId 文件id（包含组名）
	 * @return FileInfo 文件信息封装类 
	 *    source_ip_addr:   文件存储的IP地址
	 *    file_size:        文件大小
	 *    create_timestamp: 文件上传时间
	 *    crc32:            crc32 签名信息
	 * @throws IOException
	 * @throws Exception
	 */
	public FileInfo getFileInfo(String fileId) throws IOException, Exception{
		FileInfo fileInfo = storageClient.get_file_info1(fileId);
		return fileInfo;
	}
	
	/**
	 * @description 获取文件自定义的mate信息
	 * @date 2016年8月18日 下午5:17:58
	 * @param fileId 文件Id（包含组名）
	 * @return NameValuePair[] 文件属性数组(key、value)
	 * @throws IOException
	 * @throws Exception
	 */
	public NameValuePair[] getMetadata(String fileId) throws IOException, Exception{
		NameValuePair[] metadatas = storageClient.get_metadata1(fileId);
		return metadatas;
	}
	
	/**
	 * @description 删除文件
	 * @date 2016年8月18日 下午3:40:38
	 * @param file_id 文件Id（包含组名）
	 * @return int （0:success; !0:fail）
	 * @throws IOException
	 * @throws Exception
	 */
	public int deleteFile(String fileId) throws IOException, Exception {
		int result = storageClient.delete_file1(fileId);
		return result;	
	}
	
	/**
	 * @description 获取文件内容
	 * @date 2016年8月18日 下午7:47:26
	 * @param file 文件类
	 * @return byte[] 字节数组
	 * @throws IOException
	 */
	public byte[] getContent(File file) throws IOException {  		 
        long fileSize = file.length();  
        if (fileSize > Integer.MAX_VALUE) {  
            System.out.println("file too big...");  
            return null;  
        }  
        
        byte[] buffer = new byte[(int) fileSize]; 
		int offset = 0;  
		int numRead = 0;
        FileInputStream fileInputStream = null;
        
		try {
			fileInputStream = new FileInputStream(file);  		        
			while (offset < buffer.length  && (numRead = fileInputStream.read(buffer, offset, buffer.length - offset)) >= 0) {  
			    offset += numRead;  
			}  
			
			//确保所有数据均被读取  
			if (offset != buffer.length) {  
			    throw new IOException("Could not completely read file " + file.getName());  
			}  
			
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			fileInputStream.close();//关闭流
		}
        return buffer;  
	}  
	    
}
