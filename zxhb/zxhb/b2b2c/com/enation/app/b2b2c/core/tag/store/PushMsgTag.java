package com.enation.app.b2b2c.core.tag.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.PushMsgTypeContent;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IPushMsgManager;
import com.enation.app.base.core.model.Member;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * @Description 
 *
 * @createTime 2016年11月2日 下午12:30:15
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
@Component
public class PushMsgTag extends BaseFreeMarkerTag {

	private IPushMsgManager pushMsgManager;
	private IStoreMemberManager storeMemberManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		StoreMember storeMember = storeMemberManager.getStoreMember();
		Integer store_id = storeMember.getStore_id();
		Map result=new HashMap();
		List list = this.pushMsgManager.getPushMsgByMemberId(store_id);
		Integer count  = this.pushMsgManager.getCountPushMsgByMemberId(store_id);
		result.put("totalCount", count);
		result.put("PINGTAI_MSG",PushMsgTypeContent.PINGTAI_MSG);
		result.put("SYSTEM_MSG",PushMsgTypeContent.SYSTEM_MSG);
		result.put("ORDER_MSG",PushMsgTypeContent.ORDER_MSG);
		result.put("CARERROR_CODE",PushMsgTypeContent.CARERROR_CODE);
		result.put("list", list);
		return result;
	}

	public IPushMsgManager getPushMsgManager() {
		return pushMsgManager;
	}

	public void setPushMsgManager(IPushMsgManager pushMsgManager) {
		this.pushMsgManager = pushMsgManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
}
