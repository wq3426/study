package com.enation.app.b2b2c.core.service.note.impl;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.note.NoteServer;
import com.enation.app.b2b2c.core.service.note.INoteServerManager;
import com.enation.app.base.SaleTypeSetting;
import com.enation.app.base.core.model.Store;
import com.enation.app.base.core.service.IStoreCostManager;
import com.enation.eop.processor.MobileContent;
import com.enation.eop.processor.MobileMessageHttpSend;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
@Component
public class NoteServerManager extends BaseSupport implements INoteServerManager{
	
	private IStoreCostManager storeCostManager;
	
	@Override
	public Page getNoteServerList(int pageNo, int pageSize, Integer store_id, Map map) {
		String creat_time=String.valueOf(map.get("creat_time")); 
		String noteName = String.valueOf(map.get("note_name")); 
		String status= String.valueOf(map.get("status")); 
		
		StringBuffer sql =new StringBuffer("select * from es_note_server where store_id= "+ store_id);
		
		Page rpage =  this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, NoteServer.class);
		
		return rpage;

	}

	@Override
	public void add_noteInfo(NoteServer noteServer) {
		this.baseDaoSupport.insert("es_note_server", noteServer);
	}

	@Override
	public NoteServer getNoteServerByNoteId(Integer noteId) {
		String sql ="select * from es_note_server where note_id=?";
		NoteServer noteServer = (NoteServer) this.daoSupport.queryForObject(sql, NoteServer.class, noteId);
		return noteServer;
	}

	@Override
	public void edit_noteInfo(NoteServer noteServer) {
		this.daoSupport.update("es_note_server", noteServer, "note_id="+noteServer.getNote_id());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void send_noteInfo(String[] usernames, Integer noteId,Integer memberId) {
		String sql ="select * from es_note_server where note_id=?";
		NoteServer noteServer = (NoteServer) this.daoSupport.queryForObject(sql, NoteServer.class, noteId);
		String sqlStore ="select *  from es_store where member_id=?";
		Store store = (Store) this.daoSupport.queryForObject(sqlStore, Store.class, memberId);
		if(!noteServer.toString().isEmpty()&& !store.toString().isEmpty()){
			 //短信内容
			String content;
			String noteDetail="尊敬的用户，"+noteServer.getNote_detail().replaceAll(" ", "")+"退订回复TD【"+store.getStore_name()+"】";
			try {
				String usernames_str = StringUtil.arrayToString(usernames, ",");
				content = MobileMessageHttpSend.paraTo16(noteDetail);
				String strSmsParam = "reg=" + MobileContent.strRegExtend + "&pwd=" + MobileContent.strPwdExtend + "&sourceadd=" + MobileContent.strSourceAdd + "&phone=" + usernames_str + "&content=" + content;
				MobileMessageHttpSend.postSend(MobileContent.strSmsUrl,strSmsParam);
				
				String sql0 = "update es_note_server set send_count = ? where note_id = ?";
				this.baseDaoSupport.execute(sql0,noteServer.getSend_count()+usernames.length,noteId);
				
				storeCostManager.updateNoteStoreCost(store.getStore_id(),SaleTypeSetting.NOTE_NUM_TYPE,usernames.length);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}      
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addAndSendNoteInfo(NoteServer noteServer, String[] usernames,Integer memberId) {
		// TODO Auto-generated method stub
		this.baseDaoSupport.insert("es_note_server", noteServer);
		String sqlStore ="select *  from es_store where member_id=?";
		Store store = (Store) this.daoSupport.queryForObject(sqlStore, Store.class, memberId);
		String content;
		String noteDetail="尊敬的用户，"+noteServer.getNote_detail().replaceAll(" ", "")+"退订回复TD【"+store.getStore_name()+"】";
		try {
			String usernames_str = StringUtil.arrayToString(usernames, ",");
			content = MobileMessageHttpSend.paraTo16(noteDetail);
			String strSmsParam = "reg=" + MobileContent.strRegExtend + "&pwd=" + MobileContent.strPwdExtend + "&sourceadd=" + MobileContent.strSourceAdd + "&phone=" + usernames_str + "&content=" + content;
			MobileMessageHttpSend.postSend(MobileContent.strSmsUrl,strSmsParam);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}      
	}

	public IStoreCostManager getStoreCostManager() {
		return storeCostManager;
	}

	public void setStoreCostManager(IStoreCostManager storeCostManager) {
		this.storeCostManager = storeCostManager;
	}
	
	
}
