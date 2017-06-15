package com.enation.app.base.core.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import net.sf.json.JSONArray;

import com.enation.app.base.core.model.ShortMsg;
import com.enation.app.base.core.service.IShortMsgManager;
import com.enation.framework.action.WWAction;

/**
 * 短消息action
 * @author kingapex
 * @author Kanon 2015-10-14 version 1.1 添加注释
 *
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("shortMsg")
@Results({
	@Result(name="msglist", type="freemarker", location="/core/admin/shortmsg/msglist.html"),
	@Result(name="detail", type="freemarker", location="/core/admin/shortmsg/detail.html") 
})
public class ShortMsgAction extends WWAction {
	
	private IShortMsgManager shortMsgManager ;
	private List<ShortMsg> msgList;
	
	
	/**
	 * 读取所有未读消息
	 * @param msgList 未读的消息列表
	 * @return 未读的消息列表
	 */
	public String listNew(){
		msgList = this.shortMsgManager.listNotReadMessage();
		this.json = JSONArray.fromObject(JSONArray.fromObject(msgList)).toString();
		return this.JSON_MESSAGE;
	}

	//get set
	public IShortMsgManager getShortMsgManager() {
		return shortMsgManager;
	}

	public void setShortMsgManager(IShortMsgManager shortMsgManager) {
		this.shortMsgManager = shortMsgManager;
	}

	public List<ShortMsg> getMsgList() {
		return msgList;
	}

	public void setMsgList(List<ShortMsg> msgList) {
		this.msgList = msgList;
	}
	
}
