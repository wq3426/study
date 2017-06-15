package com.enation.app.fastdfsclient.sync;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.enation.app.fastdfsclient.config.FastDfsClientConfig;
import com.enation.app.fastdfsclient.utils.FastDfsClientUtils;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.GetBean;

/**
 * @Description 文件上传至服务器处理类(servlet)
 * 
 * 请求url: http://localhost:8080/mall/servlet/syncHandle.servlet
 *
 * @createTime 2016年8月23日 下午3:24:50
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public class SyncHandleServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	
	protected final Logger logger = Logger.getLogger(getClass());
	
	private SyncService sysnService = GetBean.getBeanByClass(SyncService.class);
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		//初始化表和字段
		List<String[]> relationList = sysnService.init();
		
		//按照表操作
		int count = 0;
		for (String[] relations : relationList) {		
			
			//获取表名和字段名
			String tableName = relations[0];
			String id_name = relations[1];
			String url_name = relations[2];
			
			Map<Integer,String> updateMap = new HashMap<Integer,String>();
			
			//根据表名和字段名查询记录信息
			List<Map<String,Object>> imageUrlList = sysnService.queryImageUrl(tableName, id_name, url_name);
			if(imageUrlList.isEmpty()){
				continue ;
			}
			for (Map<String,Object> imageUrlMap : imageUrlList) {
				
				int id = (int) imageUrlMap.get(id_name);
				String imageUrl = (String) imageUrlMap.get(url_name);
				if(imageUrl == null || imageUrl.equals("")) {
					continue ;
				}
				if(imageUrl.contains(FastDfsClientConfig.GROUPNAME)) {
					continue ;
				}
								
				try {
					
					//打印日志
					logger.info("tableName: " + tableName + " url_name: " + url_name + "/r/n");
					logger.info("id: " + id + " imageUrl: " + imageUrl + "/r/n");
					logger.info("开始上传");
					
					//将imageUrl转换为图片存放的绝对路径
					String file_path = SystemSetting.getFile_path();
					file_path = file_path.substring(0, file_path.lastIndexOf("/"));
					imageUrl = imageUrl.replace(EopSetting.FILE_STORE_PREFIX+"/files", file_path).replace("/", "\\");
					
					//上传图片到分布式文件系统
					FastDfsClientUtils client = FastDfsClientUtils.getInstance();
					imageUrl = client.uploadFile(imageUrl, null);
					
					//将数据暂存到map
					updateMap.put(id, imageUrl);
					
					//打印日志
					logger.info("tableName: " + tableName + "url_name: " + url_name + "/r/n");
					logger.info("id: " + id + "imageUrl: " + imageUrl + "/r/n");
					logger.info("上传结束");
				
				} catch (FileNotFoundException e) {
					logger.error("文件未找到");
					e.printStackTrace();
				} catch (Exception e) {
					logger.error("路径转换失败");
					e.printStackTrace();
				}
			}
			
			//更新数据表中的url
			int resultCount = sysnService.updateImageUrl(updateMap,tableName, id_name, url_name);
			if(resultCount != updateMap.size()) {
				logger.error("表 "+tableName+":"+url_name+"字段更新数据出错");
			}
			count++;
		}	
		
		//判断是否所有的表都操作完成
		boolean isEnd = relationList.size() == count ? true:false;	
		logger.info("是否所有表都已更新完毕： " + isEnd);
		System.out.println(isEnd);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req,resp);
	}
}
