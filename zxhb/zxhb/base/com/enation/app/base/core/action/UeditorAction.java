package com.enation.app.base.core.action;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 *  版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 *  本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 *  描述：百度富文本 ueditor Action
 *  修改人：xulipeng
 *  修改时间：2015-10-27
 *  修改内容：制定初版
 *  
 */

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("ueditor")
@Results({
	@Result(name="list", type="freemarker", location=""),
})
@SuppressWarnings({ "unused", "rawtypes", "unchecked", "serial" })
public class UeditorAction extends WWAction {

	private	String action;
	private String noCache;
	private File upfile;
	
	/**
	 * 百度富文本统一接口
	 * @return
	 * @throws Exception 
	 */
	public String getConfigJson() throws Exception{
		
		if(action==null){
			this.showErrorJson("出现错误!");
			return JSON_MESSAGE;
		}
		if(action.equals("config")){	//加载百度富文本 初始化配置。
			this.json = getConfig();
			
		}else if(action.equals("uploadimage")){		//富文本 上传图片
			this.json = uploadImg();
			
		}else{
			
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 初始化配置
	 * @return
	 */
	private String getConfig(){
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		HttpServletResponse response = ThreadContextHolder.getHttpResponse();
		String ctx =request.getContextPath();
		
		Map config = new HashMap();				//总配置对象
		
		/* 上传图片配置项 */
		config.put("imageActionName", "uploadimage");
		config.put("imageFieldName", "upfile");
		config.put("imageMaxSize", "2048000");
		config.put("imageUrlPrefix", ctx);
		config.put("imageCompressEnable", true);
		config.put("imageCompressBorder", 1600);
		config.put("imageInsertAlign", "none");
		config.put("imageUrlPrefix", "");
		
		String [] imageFormat = new String[5];	//图片上传格式配置
		imageFormat[0] = ".png";
		imageFormat[1] = ".jpg";
		imageFormat[2] = ".jpeg";
		imageFormat[3] = ".gif";
		imageFormat[4] = ".bmp";
		
		config.put("imageAllowFiles", imageFormat);
		config.put("imagePathFormat", "/ueditor/jsp/upload/image/{yyyy}{mm}{dd}/{time}{rand:6}");
		
		/* 涂鸦图片上传配置项 */
		
		/* 抓取远程图片配置 */
		
		String con = JSONArray.fromObject(config).toString();
	    String configJson = con.substring(1, con.length()-1);
	    
		return configJson;
	}
	
	/**
	 * 上传图片
	 * @return
	 * @throws Exception 
	 */
	public String uploadImg() throws Exception{
		String path = UploadUtil.uploadFile(upfile, "1111.jpg", "ueditor");
		//System.out.println(UploadUtil.replacePath(path));
		
		Map imgMap = new HashMap();
		imgMap.put("state", "SUCCESS");
		imgMap.put("url", UploadUtil.replacePath(path));
		imgMap.put("title", "show.jpg");
		imgMap.put("original", "show.jpg");
		
		String con = JSONArray.fromObject(imgMap).toString();
	    String configJson = con.substring(1, con.length()-1);
		
		return configJson;
	}


	/*
	 * set get 
	 */
	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


	public String getNoCache() {
		return noCache;
	}


	public void setNoCache(String noCache) {
		this.noCache = noCache;
	}

	public File getUpfile() {
		return upfile;
	}

	public void setUpfile(File upfile) {
		this.upfile = upfile;
	}

	
	
	
}
