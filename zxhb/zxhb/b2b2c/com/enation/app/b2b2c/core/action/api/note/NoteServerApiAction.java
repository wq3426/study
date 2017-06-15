package com.enation.app.b2b2c.core.action.api.note;

import java.util.Calendar;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.note.NoteServer;
import com.enation.app.b2b2c.core.model.store.StoreLevel;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.note.INoteServerManager;
import com.enation.app.b2b2c.core.service.store.IStoreLevelManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("noteserver")
public class NoteServerApiAction extends WWAction{

	private INoteServerManager noteServerManager;
	
	private IStoreMemberManager storeMemberManager;
	
	private NoteServer noteServer;
	
	private String[] username;
	
	private Integer note_id;
	
	/**
	 * 添加短信
	 * @param member 店铺会员,StoreMember
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String add_noteInfo(){
		StoreMember member= this.storeMemberManager.getStoreMember();
		noteServer.setStore_id(member.getStore_id());
		noteServer.setCreate_time(DateUtil.getDateline());
		noteServer.setStatus("0");
		try {
			this.noteServerManager.add_noteInfo(noteServer);
			this.showSuccessJson("添加成功");
		} catch (Exception e) {
			this.showErrorJson("添加失败");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 修改短信服务
	 * @return
	 */
	public String edit_noteInfo(){
		try {
			this.noteServerManager.edit_noteInfo(noteServer);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 发送短信给会员
	 * @return
	 */
	public String send_noteInfo(){
		StoreMember member= this.storeMemberManager.getStoreMember();
		if(username != null && note_id !=null){
			this.noteServerManager.send_noteInfo(username,note_id,member.getMember_id());
			this.showSuccessJson("短信发送成功！");
		}else{
			this.showSuccessJson("数据异常,短信发送失败！");
		}
		return JSON_MESSAGE;
		
	}
	/**
	 * 添加短信后发送短信给会员
	 * @return
	 */
	public String addAndSendNoteInfo(){
		StoreMember member= this.storeMemberManager.getStoreMember();
		noteServer.setStore_id(member.getStore_id());
		noteServer.setCreate_time(DateUtil.getDateline());
		noteServer.setStatus("0");
		try {
			if(username != null){
				noteServer.setSend_count(username.length);
			}
			this.noteServerManager.addAndSendNoteInfo(noteServer,username,member.getMember_id());
			this.showSuccessJson("短信发送成功！");
		} catch (Exception e) {
			this.showErrorJson("短信发送失败！");
		}
		return JSON_MESSAGE;
	}
	
	public INoteServerManager getNoteServerManager() {
		return noteServerManager;
	}

	public void setNoteServerManager(INoteServerManager noteServerManager) {
		this.noteServerManager = noteServerManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public NoteServer getNoteServer() {
		return noteServer;
	}

	public void setNoteServer(NoteServer noteServer) {
		this.noteServer = noteServer;
	}

	public String[] getUsername() {
		return username;
	}

	public void setUsername(String[] username) {
		this.username = username;
	}

	public Integer getNote_id() {
		return note_id;
	}

	public void setNote_id(Integer note_id) {
		this.note_id = note_id;
	}

}
