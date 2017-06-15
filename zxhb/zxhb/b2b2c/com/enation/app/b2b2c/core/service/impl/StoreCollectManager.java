package com.enation.app.b2b2c.core.service.impl;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.MemberCollect;
import com.enation.app.b2b2c.core.service.IStoreCollectManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;

/**
 * 收藏店铺	manager
 * @author xulipeng
 *
 */
@Component
public class StoreCollectManager extends BaseSupport implements IStoreCollectManager {

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.IStoreCollectManager#addCollect(com.enation.app.b2b2c.core.model.MemberCollect)
	 */
	@Override
	public void addCollect(MemberCollect collect) {
		Integer num = this.baseDaoSupport.queryForInt("select count(0) from es_member_collect where member_id=? and store_id=?", collect.getMember_id(),collect.getStore_id());
		if(num!=0){
			throw new RuntimeException("店铺已收藏！");
		}else{
			collect.setCreate_time(DateUtil.getDateline());
			this.baseDaoSupport.insert("es_member_collect", collect);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.IStoreCollectManager#delCollect(java.lang.Integer)
	 */
	@Override
	public void delCollect(Integer collect_id) {
		this.baseDaoSupport.execute("delete from es_member_collect where id=?",collect_id);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.IStoreCollectManager#getList(java.lang.Integer, int, int)
	 */
	@Override
	public Page getList(Integer memberid,int page,int pageSize) {
		String sql = "select s.store_logo,s.store_name,s.store_province,s.store_city,s.store_region,s.tel,s.store_id,m.id,m.create_time,m.member_id" +
				" from es_member_collect m INNER JOIN es_store s  ON s.store_id=m.store_id" +
				" where s.store_id in (select store_id from es_member_collect where member_id=?) and m.member_id=?";
		Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize, memberid, memberid);
		return webpage;
	}
	
	@Override
	public boolean isCollect(int memberId, int storeId){
		return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM es_member_collect WHERE store_id=? AND member_id=?", storeId,memberId) > 0;
	} 

}
