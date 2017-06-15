package com.enation.app.b2b2c.core.action.api.member;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.model.AppMessage;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IAppMessageManager;
import com.enation.app.shop.core.model.CarInfo;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.service.ICarInfoManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.TestUtil;
import com.google.gson.JsonObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * app消息api
 * 
 * @author chopper
 *
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("appMessageAction")
public class AppMessageApiAction extends WWAction {

	private IAppMessageManager appMessageManager;
	private ICarInfoManager carInfoManager;
	private IProductManager productManager;
	private AppMessage ms;
	private Integer msid;
	private File file; 
	private String fileFileName;
	private Integer status;
	private String carplate;
	private String mobilePage;
	private int flag;
	private int messageid;
	private String filePath;
	/**
	 * 需要登录
	 * @return
	 */
	public String list(){
		try {
			
			int storeId = UserConext.getCurrentMember().getBelong_store_id();
			Page page = this.appMessageManager.listPage(this.getPage(), this.getPageSize(), storeId);
			this.json = JsonMessageUtil.getObjectJson(page);
		} catch(RuntimeException e) {
			this.showErrorJson("获取列表出错");
		}
		return this.JSON_MESSAGE;
	}
	
	public String add() {
		ms.setStore_id(((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id());
		
		try {
			if(file!=null){
				 
				//判断文件类型
				String allowTYpe = "gif,jpg,bmp,png";
				if (!fileFileName.trim().equals("") && fileFileName.length() > 0) {
					String ex = fileFileName.substring(fileFileName.lastIndexOf(".") + 1, fileFileName.length());
					if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0){
						this.showErrorJson("对不起,只能上传gif,jpg,bmp,png格式的图片！");
						return this.JSON_MESSAGE;
					}
				}
				
				//判断文件大小
				
				if(file.length() > 2000 * 1024){
					this.showErrorJson("对不起,图片不能大于2000K！");
					return this.JSON_MESSAGE;
				}
				
				String imgPath=	UploadUtil.uploadFile(file, fileFileName, "faceFile");
				ms.setImage(imgPath);
			}
			ms.setCreate_time(String.valueOf(DateUtil.getDateline()));
			appMessageManager.add(ms);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showSuccessJson("发生异常，请重试");
		}
		return this.JSON_MESSAGE;
	}

	public String edit() {
		try {
			ms.setStore_id(((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id());

			if(file!=null){
				 
				//判断文件类型
				String allowTYpe = "gif,jpg,bmp,png";
				if (!fileFileName.trim().equals("") && fileFileName.length() > 0) {
					String ex = fileFileName.substring(fileFileName.lastIndexOf(".") + 1, fileFileName.length());
					if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0){
						this.showErrorJson("对不起,只能上传gif,jpg,bmp,png格式的图片！");
						return this.JSON_MESSAGE;
					}
				}
				
				//判断文件大小
				
				if(file.length() > 2000 * 1024){
					this.showErrorJson("对不起,图片不能大于2000K！");
					return this.JSON_MESSAGE;
				}
				
				String imgPath=	UploadUtil.uploadFile(file, fileFileName, "faceFile");
				ms.setImage(imgPath);
			}
			appMessageManager.edit(ms);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showSuccessJson("发生异常，请重试");
		}
		return this.JSON_MESSAGE;

	}
	/**
	 * 删除信息服务
	 * @return
	 */
	public String del() {
		try {
			appMessageManager.delete(msid);
			this.showSuccessJson("删除成功");
		} catch (Exception e) {
			this.showSuccessJson("发生异常，请重试");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 发布或者撤回信息服务
	 * @param aid 广告ID
	 * @param isClose 广告状态
	 * @return
	 */
	public String changeStatus() {
		ms = this.appMessageManager.getMessage(msid);
		if(ms != null || !ms.equals("")){
			if(status == 1){
				ms.setStatus(0);
			}else if(status == 0){
				ms.setStatus(1);
				ms.setCreate_time(String.valueOf(DateUtil.getDateline()));
			}else{
				this.showErrorJson("操作失败");
			}
		}
		try {
			this.appMessageManager.edit(ms);
			this.showSuccessJson("操作成功");
		} catch (RuntimeException e) {
			this.showErrorJson("操作失败");
			logger.error("开启广告失败", e);
		} catch (Exception e) {
			this.showErrorJson("操作失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	
	/**
	 * @param acid
	 * @return Map信息服务数据
	 */
	@SuppressWarnings("unchecked")
	public String appMessageList() {
		Map<String, Object> data = new HashMap<String,Object>();
		List<AppMessage> appMessageList = null;
		int storeId ;
		try {
			
			List<CarInfo> carInfoList=carInfoManager.getCarInfoByCarplate(carplate);
			if(carInfoList.size() > 0){
				net.sf.json.JSONObject carInfo = net.sf.json.JSONObject.fromObject(carInfoList.get(0));
				storeId = carInfo.getInt("repair4sstoreid");
				appMessageList=appMessageManager.getMessageListByStoreId(storeId);
			}else{
				appMessageList=appMessageManager.getMessageListByStoreId(1);
			}
			appMessageList = appMessageList == null ? new ArrayList<AppMessage>() : appMessageList;
			data.put("appMessageList", appMessageList);// 信息服务列表
			this.json = JsonMessageUtil.getObjectJson(data);
		} catch (RuntimeException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.error(e.getStackTrace());
			}
		}
		return WWAction.JSON_MESSAGE;
	}
	/**
     * 所有信息服务数据
     * @return
     */
    public String listAllAppMessage(){
		try {
			JSONObject data = new JSONObject();
			mobilePage = (mobilePage == null || mobilePage.equals("")) ? "1" : mobilePage;
			int pageSize = 10;
			int storeId ;
			Page AppMessagePage;
			List<CarInfo> carInfoList=carInfoManager.getCarInfoByCarplate(carplate);
			if(!carInfoList.isEmpty() || carInfoList.size() > 0){
				net.sf.json.JSONObject carInfo = net.sf.json.JSONObject.fromObject(carInfoList.get(0));
				storeId = carInfo.getInt("repair4sstoreid");
				AppMessagePage=appMessageManager.getAllMessageList(Integer.valueOf(mobilePage), pageSize, storeId);
			}else{
				AppMessagePage=appMessageManager.getAllMessageList(Integer.valueOf(mobilePage), pageSize, 1);
			}
			Long totalCount = AppMessagePage.getTotalCount();
			data.put("totalCount", totalCount);
			data.put("pageSize", pageSize);
			data.put("page", mobilePage);
			String appMessageList = UploadUtil.replacePath(JSONObject.fromObject(AppMessagePage).getString("result"));
			JSONArray array = JSONArray.fromObject(appMessageList);
			Map<String, JSONArray> map =new TreeMap<String, JSONArray>().descendingMap();
			List<JSONObject> appMessgelist= new ArrayList<JSONObject>();
			for (int i = 0; i < array.size(); i++) {   
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				JSONObject object = (JSONObject)array.get(i); 
				long createTime =  Long.parseLong(object.get("create_time").toString());
				String time = sdf.format(new Date(createTime*1000));
				
				JSONArray  obj= map.get(time);
				if(obj == null){
					 obj = new JSONArray();
					 JSONArray aaa =new JSONArray();
					 aaa.add(object);
			         map.put(time,aaa);
			     }
				obj.add(object);
	         }
			 
			 for (String key : map.keySet()) {  
				 	JSONObject jo = new JSONObject();
		            jo.put("dateTime", key);
		            jo.put("appMessage", map.get(key));
		            appMessgelist.add(jo);
		        }
			 data.put("appMessgelist", appMessgelist);
			//data.put("map", map);
			//data.put("appMessageList", appMessageList);
			this.json = "{result : 1,data : " + data + "}";
		} catch (RuntimeException e) {
			TestUtil.print(e);
			this.logger.error("获取信息服务信息出错", e);
			this.showErrorJson(e.getMessage());
		}

		return WWAction.JSON_MESSAGE;
    }
    
    /**
     * 根据id获取该条信息的详情
     * @return
     */
    public String getAppMessageById(){
			AppMessage appMessage = null;
			Product product = null;
			try {
				appMessage=appMessageManager.getMessage(messageid);
				if(appMessage.getImage() != null){
					String pic  = appMessage.getImage();
					if(pic!=null){
						pic  =UploadUtil.replacePath(pic); 
						appMessage.setImage(pic);
					}
				}
				if(appMessage.getDetail() != null){
					String detail = "<!DOCTYPE html>"+
					            	 "<html>"+
					            	 "<head lang='en'>"+
					            	 "<meta http-equiv='content-type' content='text/html;charset=utf-8'/>"+
					            	 "<meta content='width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no' name='viewport'>"+
					            	 "<style>"+
					            	 	"html {height: 100%;  width: 100%;}"+
						           		"body, button, input, textarea { font:14px, Tahoma, Helvetica, Arial, sans-serif;}"+
						           		"img {width: 100%;border: none;  vertical-align: middle; }"+
					            	 "</style>"+
					            	 "</head>"+
					            	 "<body style='margin: auto;'>"+
						             "<div style='margin: 10px'>"+
						            	"<div style='margin-bottom: 5px;text-align: center;'><b>"+appMessage.getTitle()+"</b></div>"+
						            	"<hr style='border:  1px solid #e7e7e7'>"+
						            	"<div style='line-height: 1.2em;'>"+
						            	appMessage.getDetail()+
						            	"</div>"+
						             "</div>";
					if(appMessage.getLinkGoods_id() != 0){
						detail += "<script>"+  
								    "function goodsIdHref(){"+  
										  "obdpay.toShopDetail("+appMessage.getLinkGoods_id()+")"+  
								    "}"+  
								  "</script>"+  
					              "</body>"+
					              "</html>";
						product = productManager.getByGoodsId(appMessage.getLinkGoods_id());
						if(product != null){
							appMessage.setProduct_id(product.getProduct_id());
						}
					}else{
						detail +=  "</body></html>";
					}
					appMessage.setDetail(detail);
				}
				this.json = JsonMessageUtil.getObjectJson(appMessage);
			} catch (RuntimeException e) {
				if (this.logger.isDebugEnabled()) {
					this.logger.error(e.getStackTrace());
				}
			}
			return WWAction.JSON_MESSAGE;
    }
    
    
    /**
	 * 上传图片
	 * @return
	 */
	public String fileUploadImg(){
		try {
			if(file!=null){
				 
				//判断文件类型
				String allowTYpe = "gif,jpg,bmp,png";
				if (!fileFileName.trim().equals("") && fileFileName.length() > 0) {
					String ex = fileFileName.substring(fileFileName.lastIndexOf(".") + 1, fileFileName.length());
					if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0){
						this.showErrorJson("对不起,只能上传gif,jpg,bmp,png格式的图片！");
						return this.JSON_MESSAGE;
					}
				}
				
				//判断文件大小
				if(file.length() > 2000 * 1024){
					this.showErrorJson("对不起,图片不能大于2000K！");
					return this.JSON_MESSAGE;
				}
				
			}
			String path = UploadUtil.uploadFile(file, fileFileName, "faceFile");
			//System.out.println(UploadUtil.replacePath(path));
			
			Map imgMap = new HashMap();
			imgMap.put("state", "SUCCESS");
			imgMap.put("url", UploadUtil.replacePath(path));
			imgMap.put("title", "show.jpg");
			imgMap.put("original", "show.jpg");
			
			String con = JSONArray.fromObject(imgMap).toString();
		    String configJson = con.substring(1, con.length()-1);
			
			return configJson;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		
	}
    /**
     * 删除上传图片
     * @return
     */
	public void deleteFile(){
		 UploadUtil.deleteFile(filePath);
	}
	
	public IAppMessageManager getAppMessageManager() {
		return appMessageManager;
	}

	public void setAppMessageManager(IAppMessageManager appMessageManager) {
		this.appMessageManager = appMessageManager;
	}

	public AppMessage getMs() {
		return ms;
	}

	public void setMs(AppMessage ms) {
		this.ms = ms;
	}

	public Integer getMsid() {
		return msid;
	}

	public void setMsid(Integer msid) {
		this.msid = msid;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCarplate() {
		return carplate;
	}

	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}

	public ICarInfoManager getCarInfoManager() {
		return carInfoManager;
	}

	public void setCarInfoManager(ICarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}

	public String getMobilePage() {
		return mobilePage;
	}

	public void setMobilePage(String mobilePage) {
		this.mobilePage = mobilePage;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getMessageid() {
		return messageid;
	}

	public void setMessageid(int messageid) {
		this.messageid = messageid;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}
}
