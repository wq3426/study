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
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
@Action("pushMsg")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/pushMsg/pushMsg_list.html"),
	@Result(name="add", type="freemarker", location="/core/admin/pushMsg/pushMsg_input.html"), 
	@Result(name="edit", type="freemarker", location="/core/admin/pushMsg/pushMsg_edit.html") 
})
public class PushMessageAction extends WWAction {

	private IPushMsgManager pushMsgManager;
	private IStoreManager storeManager;
	private List<PushMsg> pushMsgList;
	private List<PushMsgType> pushMsgTypeList;
	private List<Store> storeList;
	private PushMsg pushMsg;
	private Map other;
	private String name;
	private Integer id;
	private Integer status;
	
	/**
	 * 跳转至消息中心列表
	 * @return 消息中心列表页面
	 */
	public String list() {
		return "list";
	}
	
	/**
	 * 消息中心列表JSON
	 * @param adColumnList消息中心列表
	 */
	public String listJson() {
		other = new HashMap<>();
		other.put("name", name);
		Page pushMsg_list = pushMsgManager.pushMsg_list(other,this.getPage(),this.getPageSize());
		this.showGridJson(pushMsg_list);
		return JSON_MESSAGE;
	}
	/**
	 * @description 跳转至消息新增页面
	 * @date 2016年10月26日 下午3:24:19
	 * @return
	 * @return IPushMsgManager
	 */
	public String add() {
		storeList = this.storeManager.queryStoreList();
		pushMsgTypeList = this.pushMsgManager.pushMsgType_list();
		return "add";
	}
	
	/**
	 * @description 跳转至消息修改页面
	 * @date 2016年10月26日 下午6:03:56
	 * @return
	 * @return String
	 */
	public String edit() {
		storeList = this.storeManager.queryStoreList();
		pushMsgTypeList = this.pushMsgManager.pushMsgType_list();
		pushMsg = this.pushMsgManager.getPushMsgDetail(id);
		return "edit";
	}
	/**
	 * @description 新增消息内容
	 * @date 2016年10月26日 下午5:29:21
	 * @return
	 * @return String
	 */
	public String addPushMsg() {
		pushMsg.setCreate_time(DateUtil.toString(new Date(), "yyyy-MM-dd hh:mm:ss"));
		pushMsg.setMsg(StringUtil.getTxtWithoutHTMLElement(pushMsg.getMsg()));
		try {
			this.pushMsgManager.addPushMsg(pushMsg);
			this.showSuccessJson("新增消息成功");
		} catch (RuntimeException e) {
			this.showErrorJson("新增消息失败");
			logger.error("新增消息失败", e);
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 保存修改消息
	 * @date 2016年10月26日 下午6:46:17
	 * @return
	 * @return String
	 */
	public String editPushMsg() {
		net.sf.json.JSONObject dataobj = new net.sf.json.JSONObject();
		pushMsg.setCreate_time(DateUtil.toString(new Date(), "yyyy-MM-dd hh:mm:ss"));
		try {
			this.pushMsgManager.updatePushMsg(pushMsg);
			this.showSuccessJson("修改消息成功");
		} catch (Exception e) {
			this.showErrorJson("修改消息失败");
			logger.error("修改消息失败", e);
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 消息删除
	 * @date 2016年10月26日 下午7:11:34
	 * @return
	 * @return String
	 */
	public String delete() {
		try {
			this.pushMsgManager.delPushMsg(id);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败");
			logger.error("广告删除失败", e);
		}
		
		return this.JSON_MESSAGE;
	}
	
	/**
	 * @description消息推送和撤回 
	 * @date 2016年10月31日 上午9:42:56
	 * @return void
	 */
	public String changeStatus(){
		pushMsg = this.pushMsgManager.getPushMsgDetail(id);
		if(pushMsg != null || !pushMsg.equals("")){
			if(status == 1){
				pushMsg.setStatus('0');
				this.pushMsgManager.edit(pushMsg);
				this.showSuccessJson("消息撤回成功");
			}else if(status == 0){
				pushMsg.setStatus('1');
				this.pushMsgManager.edit(pushMsg);
				this.showSuccessJson("消息推送成功");
			}else{
				this.showErrorJson("操作失败");
			}
		}else{
			this.showErrorJson("操作失败");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 根据id查询消息详情
	 * @date 2016年11月4日 上午10:59:12
	 * @return
	 * @return IPushMsgManager
	 */
	public String queryPushMsgById(){
		pushMsg = this.pushMsgManager.getPushMsgDetail(id);
		if(pushMsg != null || !pushMsg.equals("")){
			this.json = JsonMessageUtil.getObjectJson(pushMsg);
		}else{
			this.showErrorJson("操作失败");
		}
		return this.JSON_MESSAGE;
	}
	
	
	public IPushMsgManager getPushMsgManager() {
		return pushMsgManager;
	}

	public void setPushMsgManager(IPushMsgManager pushMsgManager) {
		this.pushMsgManager = pushMsgManager;
	}

	public List<PushMsg> getPushMsgList() {
		return pushMsgList;
	}

	public void setPushMsgList(List<PushMsg> pushMsgList) {
		this.pushMsgList = pushMsgList;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}

	public List<Store> getStoreList() {
		return storeList;
	}

	public void setStoreList(List<Store> storeList) {
		this.storeList = storeList;
	}

	public PushMsg getPushMsg() {
		return pushMsg;
	}

	public void setPushMsg(PushMsg pushMsg) {
		this.pushMsg = pushMsg;
	}

	public Map getOther() {
		return other;
	}

	public void setOther(Map other) {
		this.other = other;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public List<PushMsgType> getPushMsgTypeList() {
		return pushMsgTypeList;
	}

	public void setPushMsgTypeList(List<PushMsgType> pushMsgTypeList) {
		this.pushMsgTypeList = pushMsgTypeList;
	}
}

