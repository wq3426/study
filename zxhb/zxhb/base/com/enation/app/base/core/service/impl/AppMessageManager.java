package com.enation.app.base.core.service.impl;
 
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.SaleTypeSetting;
import com.enation.app.base.core.model.AppMessage;
import com.enation.app.base.core.service.IAppMessageManager;
import com.enation.app.base.core.service.IStoreCostManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.FileUtil;

@Component
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AppMessageManager  implements IAppMessageManager {

	private IDaoSupport daoSupport;
	private IStoreCostManager storeCostManager;
	
	@Override
	public AppMessage getMessage(int messageid) {
		return (AppMessage) this.daoSupport.queryForObject("select * from es_app_message where id = ?", AppMessage.class, messageid);
	}
	
	@Override
	public Map get(Integer amid) {
		String sql = "select * from es_app_message where id = ?";
		
		Map appMessage = this.daoSupport.queryForMap(sql, amid);
		String image=(String) appMessage.get("image");
		if(image!=null){
			image  =UploadUtil.replacePath(image); 
			appMessage.put("image", image);
		}
		return appMessage;
	}

	@Override
	public void add(AppMessage ms) {
		 this.daoSupport.insert("es_app_message", ms);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void edit(AppMessage ms) throws Exception {
		if(ms.getStatus() == 1){
			storeCostManager.updateStoreCost(ms.getStore_id(),SaleTypeSetting.APP_MESPUBLISH_NUM_TYPE);
		}
		 this.daoSupport.update("es_app_message", ms,"id = " + ms.getId());
	}

	@Override
	public Page listPage(int page, int pagesize,int store_id) { 
		Page page1=this.daoSupport.queryForPage("select * from es_app_message where store_id = ?", page, pagesize,store_id);
		List list = (List) page1.getResult();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = (Map<String, Object>) list.get(i);
			if (map.containsKey("image") && map.get("image") != null) {
				String pic  =UploadUtil.replacePath(map.get("image").toString()); 
				map.put("image", pic);
			}
		}
		page1.setResult(list);
		return page1;
	}

	@Override
	public void delete(int messageid) {
		try {
			AppMessage appMessage = (AppMessage) this.daoSupport.queryForObject("SELECT * FROM es_app_message WHERE id = ?", AppMessage.class, messageid);
			String pic  = appMessage.getImage();
			if(pic!=null){
				String file_path = SystemSetting.getFile_path();
				String file = pic.replace(EopSetting.FILE_STORE_PREFIX+"/files", file_path);
				FileUtil.delete(file);
			}
			this.daoSupport.execute("delete from es_app_message where id = ? ", messageid);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	public List<AppMessage> getMessageListByStoreId(int storeId) {
		
		List<AppMessage> appMessageList =  new LinkedList<AppMessage>();
		if(storeId == 1 || storeId == 0){
			String sql2 = "SELECT * FROM es_app_message WHERE store_id = 1 and status = 1  ORDER BY create_time DESC";
			List<AppMessage> appMessage2 = this.daoSupport.queryForList(sql2, AppMessage.class);
			if(appMessage2.size()>2){
				for(int i=0;i<=2;i++){
					appMessageList.add(appMessage2.get(i));
				}
			}else{
				for(AppMessage appMessage: appMessage2){
					appMessageList.add(appMessage);
				}
			}
		}else{
			String sql = "SELECT * FROM es_app_message WHERE store_id = ? and status = 1 ORDER BY create_time DESC";
			List<AppMessage> appMessages = this.daoSupport.queryForList(sql, AppMessage.class, storeId);
			String sql2 = "SELECT * FROM es_app_message WHERE store_id = 1 and  status = 1 ORDER BY create_time DESC";
			List<AppMessage> appMessages2 = this.daoSupport.queryForList(sql2, AppMessage.class);
			if(appMessages.size()>0){
				if(appMessages2.size()>2){
					for(int i=0;i<2;i++){
						appMessageList.add(appMessages2.get(i));
					}
				}else{
					for(AppMessage appMessage: appMessages2){
						appMessageList.add(appMessage);
					}
				}
				appMessageList.add(appMessages.get(0));
			}else{
				if(appMessages2.size()>2){
					for(int i=0;i<=2;i++){
						appMessageList.add(appMessages2.get(i));
					}
				}else{
					for(AppMessage appMessage: appMessages2){
						appMessageList.add(appMessage);
					}
				}
			}
		}
		if(appMessageList.size() >0){
			for(AppMessage appMessage : appMessageList){
				String pic  = appMessage.getImage();
				String detail = appMessage.getDetail();
				if(pic!=null){
					pic  =UploadUtil.replacePath(pic); 
					appMessage.setImage(pic);
				}
				/*if(detail != null){
					appMessage.setDetail("<!DOCTYPE html>"+
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
							             "</div>"+
							             "<script>"+  
										    "function goodsIdHref(){"+  
												  "obdpay.toShopDetail("+appMessage.getLinkGoods_id()+")"+  
										    "}"+  
										 "</script>"+  
							             "</body>"+
							             "</html>");
				}*/
			}
		}
		return appMessageList;
	}
	@Override
	public Page getAllMessageList(Integer page, int pageSize, int storeId) {
		StringBuffer sql;
		Page rpage;
		if(storeId == 1 || storeId == 0){
			sql =new StringBuffer("SELECT eam.id,eam.title,eam.image,eam.synopsis,eam.create_time FROM es_app_message eam "
								+ "WHERE eam.store_id = 1  and eam.status = 1 order by create_time desc");
			rpage = this.daoSupport.queryForPage(sql.toString(),page, pageSize);
		}else{
			sql =new StringBuffer("SELECT eam.id,eam.title,eam.image,eam.synopsis,eam.create_time FROM es_app_message eam "
								+ "where eam.status = 1 and (eam.store_id = 1 or eam.store_id = "+storeId +") order by create_time desc");
			rpage = this.daoSupport.queryForPage(sql.toString(),page, pageSize);
		}
		
		return rpage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAppMessageManager#listPageByMemberId(int, int, int)
	 */
	@Override
	public Page listPageByMemberId(int page, int pageSize, int memberId) {
		String sql = "SELET * FROM es_app_message WHERE member_id = ?";
		return null;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IStoreCostManager getStoreCostManager() {
		return storeCostManager;
	}

	public void setStoreCostManager(IStoreCostManager storeCostManager) {
		this.storeCostManager = storeCostManager;
	}

}
