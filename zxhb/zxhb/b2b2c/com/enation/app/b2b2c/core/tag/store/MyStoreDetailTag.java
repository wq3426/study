package com.enation.app.b2b2c.core.tag.store;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;


import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Store;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import cn.jiguang.commom.utils.StringUtils;
import freemarker.template.TemplateModelException;
@Component
/**
 * 店铺信息Tag
 * @author LiFenLong
 *
 */
public class MyStoreDetailTag extends BaseFreeMarkerTag{
	private IStoreManager storeManager;
	private IStoreMemberManager storeMemberManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		String ctx = this.getRequest().getContextPath();
		HttpServletResponse response= ThreadContextHolder.getHttpResponse();
		Store store = new Store();
		try {
			if(params.get("type") != null){
				store = storeManager.getStore(Integer.parseInt(params.get("store_id").toString()));
				String id_img = store.getId_img();
				String corporation_id_img = store.getCorporation_id_img();
				String bank_license_img = store.getBank_license_img();
				String license_img = store.getLicense_img();
				String store_logo = store.getStore_logo();
				String contact_idimg_back = store.getContact_idimg_back();
				String corporate_idimg_back = store.getCorporate_idimg_back();
				
				if(StringUtils.isNotEmpty(bank_license_img)) {
					bank_license_img = UploadUtil.replacePath(bank_license_img);
					store.setBank_license_img(bank_license_img);
				}
				if(StringUtils.isNotEmpty(store_logo)) {
					store_logo = UploadUtil.replacePath(store_logo);
					store.setStore_logo(store_logo);
				}
				if(StringUtils.isNotEmpty(id_img)) {
					id_img = UploadUtil.replacePath(id_img);
					store.setId_img(id_img);
				}
				if(StringUtils.isNotEmpty(corporation_id_img)) {
					corporation_id_img = UploadUtil.replacePath(corporation_id_img);
					store.setCorporation_id_img(corporation_id_img);
				}
				if(StringUtils.isNotEmpty(license_img)) {
					license_img = UploadUtil.replacePath(license_img);
					store.setLicense_img(license_img);
				}
				if(StringUtils.isNotEmpty(corporate_idimg_back)) {
					corporate_idimg_back = UploadUtil.replacePath(corporate_idimg_back);
					store.setCorporate_idimg_back(corporate_idimg_back);
				}
				if(StringUtils.isNotEmpty(contact_idimg_back)) {
					contact_idimg_back = UploadUtil.replacePath(contact_idimg_back);
					store.setContact_idimg_back(contact_idimg_back);
				}
				
			}else{
				//session中获取会员信息,判断用户是否登陆
				StoreMember member = storeMemberManager.getStoreMember();
				if(member == null){
					response.sendRedirect(ctx+"/store/login.html");
				}
				//重新申请店铺时使用
				else if(member.getStore_id() == null){
					 store = storeManager.getStoreByMember(member.getMember_id());
				}else{
					 store = storeManager.getStore(member.getStore_id());
					 String id_img = store.getId_img();
					 String corporation_id_img = store.getCorporation_id_img();
					 String bank_license_img = store.getBank_license_img();
					 String license_img = store.getLicense_img();
					 String store_logo = store.getStore_logo();
					 String contact_idimg_back = store.getContact_idimg_back();
					 String corporate_idimg_back = store.getCorporate_idimg_back();
					 
					if(StringUtils.isNotEmpty(bank_license_img)) {
						bank_license_img = UploadUtil.replacePath(bank_license_img);
						store.setBank_license_img(bank_license_img);
					}
					if(StringUtils.isNotEmpty(store_logo)) {
						store_logo = UploadUtil.replacePath(store_logo);
						store.setStore_logo(store_logo);
					}
					if(StringUtils.isNotEmpty(id_img)) {
						id_img = UploadUtil.replacePath(id_img);
						store.setId_img(id_img);
					}
					if(StringUtils.isNotEmpty(corporation_id_img)) {
						corporation_id_img = UploadUtil.replacePath(corporation_id_img);
						store.setCorporation_id_img(corporation_id_img);
					}
					if(StringUtils.isNotEmpty(license_img)) {
						license_img = UploadUtil.replacePath(license_img);
						store.setLicense_img(license_img);
					}
					if(StringUtils.isNotEmpty(corporate_idimg_back)) {
						corporate_idimg_back = UploadUtil.replacePath(corporate_idimg_back);
						store.setCorporate_idimg_back(corporate_idimg_back);
					}
					if(StringUtils.isNotEmpty(contact_idimg_back)) {
						contact_idimg_back = UploadUtil.replacePath(contact_idimg_back);
						store.setContact_idimg_back(contact_idimg_back);
					}
				}
			}
			if(store.getDisabled()==2){
				response.sendRedirect(ctx+"/store/dis_store.html");
			}
		} catch (IOException e) {
			throw new UrlNotFoundException();
		}
		
		return store;
	}
	public IStoreManager getStoreManager() {
		return storeManager;
	}
	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
