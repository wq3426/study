package com.enation.app.b2b2c.core.service.store.impl;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.store.PushMsg;
import com.enation.app.b2b2c.core.model.store.PushMsgType;
import com.enation.app.b2b2c.core.service.store.IPushMsgManager;
import com.enation.app.base.SaleTypeSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

public class PushMsgManager extends BaseSupport implements IPushMsgManager{

	@Override
	public Page pushMsg_list(Map other, int pageNo, int pageSize) {
		String name = other.get("name") == null ? "" : other.get("name")
				.toString();
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT  epm.*,es.store_name,ept.push_type FROM  es_push_msg epm left join es_store es on epm.storeId=es.store_id left join es_pushmsg_type ept on epm.push_id=ept.push_id WHERE 1=1 ");   

		if(!StringUtil.isEmpty(name)) {
			sql.append(" AND epm.store_name like '%" + name.trim() + "%'");
		}
		Page pushMsgPage = this.daoSupport.queryForPage(sql.toString(),pageNo, pageSize);
		return pushMsgPage;
	}

	@Override
	public void addPushMsg(PushMsg pushMsg) {
		this.daoSupport.insert("es_push_msg", pushMsg);
	}

	@Override
	public PushMsg getPushMsgDetail(Integer id) {
		String sql="select * from es_push_msg where id=?";
		return (PushMsg) this.baseDaoSupport.queryForObject(sql,PushMsg.class, id);
	}

	@Override
	public void updatePushMsg(PushMsg pushMsg) {
		this.daoSupport.update("es_push_msg", pushMsg, "id="+pushMsg.getId());
	}

	@Override
	public void delPushMsg(Integer id) {
		String sql="delete from es_push_msg where id=?";
		this.daoSupport.execute(sql, id);
	}

	@Override
	public void edit(PushMsg pushMsg) {
		this.daoSupport.update("es_push_msg", pushMsg,"id = " + pushMsg.getId());
	}

	@Override
	public List<PushMsgType> pushMsgType_list() {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT  epm.* FROM  es_pushmsg_type epm WHERE 1=1 ");   
		List<PushMsgType> pushMsgTypeList = this.daoSupport.queryForList(sql.toString());
		return pushMsgTypeList;
	}

	@Override
	public void addPushMsgType(PushMsgType pushMsgType) {
		this.daoSupport.insert("es_pushmsg_type", pushMsgType);
	}

	@Override
	public List getPushMsgByMemberId(Integer storeId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select  epm.*,ept.push_type from  es_push_msg epm left join es_pushmsg_type ept on epm.push_id= ept.push_id "
				+  " where 1=1 and epm.status = '1' and epm.storeid = 0 or epm.storeid = "+ storeId);   
		List  pushMsgTypeList = this.daoSupport.queryForList(sql.toString());
		return pushMsgTypeList;
	}

	@Override
	public Integer getCountPushMsgByMemberId(Integer storeId) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select  count(*) from  es_push_msg epm where 1=1 and epm.status = '1' and epm.flag='0' and epm.storeid = 0 or epm.storeid = "+ storeId);   
		Integer  countNum = this.daoSupport.queryForInt(sql.toString());
		return countNum;
	}

}
