package com.enation.eop.sdk.utils;

import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enation.app.base.core.plugin.fdfs.FastdfsBundle;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.image.IThumbnailCreator;
import com.enation.framework.image.ThumbnailCreatorFactory;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

public class UploadUtil {
	
	private UploadUtil(){
		
	}
  
	private static FastdfsBundle fastdfsBundle = SpringContextHolder.getBean("fastdfsBundle");
	
	/**
	 * 上传图片<br/>
	 * 图片会被上传至用户上下文的attacment文件夹的subFolder子文件夹中<br/>
	 * 
	 * @param file  图片file对象
	 * @param fileFileName 上传的图片原名
	 * @param subFolder  子文件夹名
	 * @return 
	 * @since 上传后的本地路径，如:fs:/attachment/goods/2001010101030.jpg<br/>
	 * 
	 */
	public static String uploadFile(File file,String fileFileName,String subFolder ) throws Exception {
		if(file==null || fileFileName==null) throw new IllegalArgumentException("file or filename object is null");
		if(subFolder == null)throw new IllegalArgumentException("subFolder is null");
		
		if(!FileUtil.isAllowUpImg(fileFileName)){
			throw new IllegalArgumentException("不被允许的上传文件类型");
		}
		
		String fileName = null;
		String filePath = "";
 
		String ext = FileUtil.getFileExt(fileFileName);
		fileName = DateUtil.toString(new Date(), "yyyyMMddHHmmss") + StringUtil.getRandStr(4) + "." + ext;
		
		String file_path = SystemSetting.getFile_path();
		
		if(subFolder != null){
			filePath += file_path + "/" + subFolder +"/";
		}
		
		String path = EopSetting.FILE_STORE_PREFIX+ "/files/" +(subFolder==null?"":subFolder)+ "/"+fileName;
	 
		filePath += fileName;
		FileUtil.createFile(file, filePath);
		
		return path;
	}
	
	/**
	 * 上传图片<br/>
	 * 图片会被上传至用户上下文的attacment文件夹的subFolder子文件夹中<br/>
	 * 
	 * @param file  图片file对象
	 * @param fileFileName 上传的图片原名
	 * @param subFolder  子文件夹名
	 * @return 
	 * @since 上传后的本地路径，如:fs:/attachment/goods/2001010101030.jpg<br/>
	 * 
	 */
	public static String upload(File file,String fileFileName,String subFolder ) {
		if(file==null || fileFileName==null) throw new IllegalArgumentException("file or filename object is null");
		if(subFolder == null)throw new IllegalArgumentException("subFolder is null");
		
		if(!FileUtil.isAllowUpImg(fileFileName)){
			throw new IllegalArgumentException("不被允许的上传文件类型");
		}
		
		
		String fileName = null;
		String filePath = "";
 
		String ext = FileUtil.getFileExt(fileFileName);
		fileName = DateUtil.toString(new Date(), "yyyyMMddHHmmss") + StringUtil.getRandStr(4) + "." + ext;
		
		String static_server_path = SystemSetting.getStatic_server_path();
		
		filePath = static_server_path + "/attachment/";
		if(subFolder!=null){
			filePath+=subFolder +"/";
		}
		
		String path  = EopSetting.FILE_STORE_PREFIX+ "/attachment/" +(subFolder==null?"":subFolder)+ "/"+fileName;
	 
		filePath += fileName;
		FileUtil.createFile(file, filePath);
		//如果开启文件分发
		if (SystemSetting.getFdfs_open() == 1) { 
			String filepath = fastdfsBundle.BeAfterUpload(new File(filePath));
			//两两对应
			fastdfsBundle.getCache().put(filepath, filePath); 
			fastdfsBundle.getCache().put(filePath, filepath); 
			if(filepath!=null){
				return filepath;
			}else{
				throw new RuntimeException("文件上传异常");
			}
		}
		
		return path;
	}
	
	/**
	 * 上传App文件
	 * 图片会被上传至用户上下文的attacment文件夹的subFolder子文件夹中<br/>
	 * 
	 * @param file  file对象
	 * @param fileFileName 上传的文件原名
	 * @param subFolder  子文件夹名
	 * @return 
	 * @since 上传后的本地路径，如:fs:/attachment/app_file/2001010101030.apk<br/>
	 * 
	 */
	public static String uploadAppFile(File file,String fileFileName,String subFolder, String vNumber) throws Exception {
		if(file==null || fileFileName==null) throw new IllegalArgumentException("file or filename object is null");
		if(subFolder == null)throw new IllegalArgumentException("subFolder is null");
		
		if(!FileUtil.isAllowUpImg(fileFileName)){
			throw new IllegalArgumentException("不被允许的上传文件类型");
		}
		
		
		String fileName = null;
		String filePath = SystemSetting.getFile_path();
 
		String ext = FileUtil.getFileExt(fileFileName);
		fileName = "yingjia_" + vNumber + "." + ext;
		
		if(subFolder!=null){
			filePath += "/" + subFolder +"/";
		}
	
		String path = EopSetting.FILE_STORE_PREFIX + "/files/" +(subFolder==null?"":subFolder)+ "/"+fileName;
		filePath += fileName;
		FileUtil.createFile(file, filePath);
		//如果开启文件分发
		if (SystemSetting.getFdfs_open() == 1) { 
			String filepath = fastdfsBundle.BeAfterUpload(new File(filePath));
			//两两对应
			fastdfsBundle.getCache().put(filepath, filePath); 
			fastdfsBundle.getCache().put(filePath, filepath); 
			if(filepath!=null){
				return filepath;
			}else{
				throw new RuntimeException("文件上传异常");
			}
		}
		
		return path;
	}
	
	public static String replacePath(String path){
		
		if(StringUtil.isEmpty(path)) return path;
		//if(!path.startsWith(EopSetting.FILE_STORE_PREFIX)) return path;
		String static_server_domain = SystemSetting.getStatic_server_domain();
		return	path.replaceAll(EopSetting.FILE_STORE_PREFIX, static_server_domain);
	}
	
	/**
	 * @description 将图片的服务器请求路径转换为
	 * @date 2016年10月10日 下午3:47:29
	 * @param path
	 * @return
	 */
	public static String replacePathTo(String path){
		if(StringUtil.isEmpty(path)) {
			return path;
		}
		String static_server_domain = SystemSetting.getStatic_server_domain();
		path = path.replace(static_server_domain, EopSetting.FILE_STORE_PREFIX );
		return	path;
	}
	
	/**
	 * 上传图片并生成缩略图
	 * 图片会被上传至用户上下文的attacment文件夹的subFolder子文件夹中<br/>
	 * 
	 * @param file  图片file对象
	 * @param fileFileName 上传的图片原名
	 * @param subFolder  子文件夹名
	 * @param width 缩略图的宽
	 * @param height 缩略图的高
	 * @return 上传后的图版全路径，如:http://static.eop.com/user/1/1/attachment/goods/2001010101030.jpg<br/>
	 * 返回值为大小为2的String数组，第一个元素为上传后的原图全路径，第二个为缩略图全路径
	 */
	public static String[] upload(File file,String fileFileName,String subFolder,int width,int height ){
		if(file==null || fileFileName==null) throw new IllegalArgumentException("file or filename object is null");
		if(subFolder == null)throw new IllegalArgumentException("subFolder is null");
		String fileName = null;
		String filePath = "";
		String [] path = new String[2];
		if(!FileUtil.isAllowUpImg(fileFileName)){
			throw new IllegalArgumentException("不被允许的上传文件类型");
		}
		String ext = FileUtil.getFileExt(fileFileName);
		fileName = DateUtil.toString(new Date(), "yyyyMMddHHmmss") + StringUtil.getRandStr(4) + "." + ext;
		String static_server_path = SystemSetting.getStatic_server_path();
		
		filePath = static_server_path+ "/attachment/";
		if(subFolder!=null){
			filePath+=subFolder +"/";
		}
		
		path[0]  = static_server_path+ "/attachment/" +(subFolder==null?"":subFolder)+ "/"+fileName;
		filePath += fileName;
		FileUtil.createFile(file, filePath);
		String thumbName= getThumbPath(filePath,"_thumbnail");
		 
		IThumbnailCreator thumbnailCreator = ThumbnailCreatorFactory.getCreator( filePath, thumbName);
		thumbnailCreator.resize(width, height);	
		path[1] = getThumbPath(path[0], "_thumbnail");
		return path;
	}
	
	
	
	
	/**
	 * 删除某个上传的文件
	 * @param filePath 文件全路径如：http://static.eop.com/user/1/1/attachment/goods/2001010101030.jpg
	 */
	public static void deleteFile(String filePath){
		String static_server_path = SystemSetting.getStatic_server_path();
		String static_server_domain = SystemSetting.getStatic_server_domain();
		
		filePath =filePath.replaceAll(static_server_domain,static_server_path);
		FileUtil.delete(filePath);		
	}
	
	
	
	/**
	 * 删除某个上传的文件
	 * @param filePath 文件全路径如：http://static.eop.com/user/1/1/attachment/goods/2001010101030.jpg
	 */
	public static void deleteFileAndThumb(String filePath){
		String static_server_path = SystemSetting.getStatic_server_path();
		String static_server_domain = SystemSetting.getStatic_server_domain();

		filePath =filePath.replaceAll( static_server_domain, static_server_path);
		FileUtil.delete(filePath);		
		FileUtil.delete(getThumbPath(filePath,"_thumbnail"));		
	}
	
	

	
	/**
	 * 转换图片的名称
	 * @param filePath  如：http://static.eop.com/user/1/1/attachment/goods/2001010101030.jpg
	 * @param shortName 
	 * @return
	 */
	public static String getThumbPath(String filePath, String shortName) {
		String pattern = "(.*)([\\.])(.*)";
		String thumbPath = "$1" + shortName + "$2$3";

		Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
		Matcher m = p.matcher(filePath);
		if (m.find()) {
			String s = m.replaceAll(thumbPath);

			return s;
		}
		return null;
	}	
	
	
	public static void main(String[] args){
	}
	public static FastdfsBundle getFastdfsBundle() {
		return fastdfsBundle;
	}
	public static void setFastdfsBundle(FastdfsBundle fastdfsBundle) {
		UploadUtil.fastdfsBundle = fastdfsBundle;
	}
	
}
