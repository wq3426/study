package com.enation.app.b2b2c.component.plugin.member;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.Store;
import com.enation.app.shop.core.plugin.member.IMemberRegisterEvent;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
@Component
/**
 * 会员注册后标示为未申请店铺
 * @author LiFenLong
 *
 */
public class B2b2cMemberRegisterPlugin extends AutoRegisterPlugin implements IMemberRegisterEvent{
	private IDaoSupport daoSupport;
	private IStoreMemberManager storeMemberManager;
	private IStoreManager storeManager;
	@Override
	public void onRegister(Member member) {
		Map map=new HashMap();
		map.put("is_store", 0);
		map.put("store_id", 0);
		daoSupport.update("es_member",map , "member_id="+member.getMember_id());
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String isStore = request.getParameter("is_store");
		
		// 注册的是商家。 就新增好店铺信息
		if("yes".equals(isStore)) {
			String brandId = request.getParameter("brand_id");
			Store store = new Store();
			store.setMember_id(member.getMember_id());
			store.setMember_name(member.getName());
			store.setDisabled(1);
			store.setEmail(member.getEmail());
			store.setBrand_id(Integer.parseInt(brandId));
			store.setStore_provinceid(0);
			store.setStore_cityid(0);
			store.setStore_regionid(0);
			store.setStore_auth(0);
			store.setName_auth(0);
			
			storeManager.save(store);
			int storeId = store.getStore_id();
			map = new HashMap();
			map.put("is_store", 1);
			map.put("store_id", storeId);
			daoSupport.update("es_member",map , "member_id="+member.getMember_id());
			
		}
		
		//登陆店铺会员
		ThreadContextHolder.getSessionContext().setAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY, storeMemberManager.getMember(member.getMember_id()));
	}
	
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
}
