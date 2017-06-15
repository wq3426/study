package com.enation.app.b2b2c.core.action.backend.store;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.store.PushMsg;
import com.enation.app.b2b2c.core.model.store.PushMsgType;
import com.enation.app.b2b2c.core.service.store.IPushMsgManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Store;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;

/**
 * @Description 消息中心
 *
 * @createTime 2016年10月26日 下午2:28:34
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("pushMsgType")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/pushMsg/pushMsgType_list.html"),
	@Result(name="add", type="freemarker", location="/core/admin/pushMsg/pushMsgType_input.html")
})
public class PushMessageTypeAction extends WWAction {

	private IPushMsgManager pushMsgManager;
	private PushMsgType pushMsgType;
	private List<PushMsgType> pushMsgType_list;
	/**
	 * 跳转至消息类型列表
	 * @return 消息类型列表页面
	 */
	public String list() {
		return "list";
	}
	
	/**
	 * 消息类型列表JSON
	 * @param adColumnList消息类型列表
	 */
	public String listJson() {
		pushMsgType_list = pushMsgManager.pushMsgType_list();
		this.showGridJson(pushMsgType_list);
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 跳转至消息类型新增页面
	 * @date 2016年10月26日 下午3:24:19
	 * @return
	 * @return IPushMsgManager
	 */
	public String add() {
		return "add";
	}
	
	/**
	 * @description 新增消息类型
	 * @date 2016年11月1日 下午5:34:40
	 * @return
	 * @return String
	 */
	public String addPushMsgType() {
		try {
			this.pushMsgManager.addPushMsgType(pushMsgType);
			this.showSuccessJson("新增消息类型成功");
		} catch (RuntimeException e) {
			this.showErrorJson("新增消息类型失败");
			logger.error("新增消息类型失败", e);
		}
		return JSON_MESSAGE;
	}
	public IPushMsgManager getPushMsgManager() {
		return pushMsgManager;
	}

	public void setPushMsgManager(IPushMsgManager pushMsgManager) {
		this.pushMsgManager = pushMsgManager;
	}

	public PushMsgType getPushMsgType() {
		return pushMsgType;
	}

	public void setPushMsgType(PushMsgType pushMsgType) {
		this.pushMsgType = pushMsgType;
	}

	public List<PushMsgType> getPushMsgType_list() {
		return pushMsgType_list;
	}

	public void setPushMsgType_list(List<PushMsgType> pushMsgType_list) {
		this.pushMsgType_list = pushMsgType_list;
	}
	
}

