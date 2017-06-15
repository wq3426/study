package com.enation.app.base.core.service.impl;

import org.springframework.stereotype.Component; 
import com.enation.app.base.core.model.MemberMessage;
import com.enation.app.base.core.service.IMemberMessageManager;
import com.enation.framework.database.IDaoSupport;
@Component
public class MemberMessageManager implements IMemberMessageManager {

	private IDaoSupport daoSupport;
	
	@Override
	public void sendmessage(int amid, int member_id) {  
		this.daoSupport.insert("es_member_message", new MemberMessage(member_id,amid, 0));
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	
}
