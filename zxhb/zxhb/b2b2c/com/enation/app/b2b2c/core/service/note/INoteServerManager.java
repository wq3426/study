package com.enation.app.b2b2c.core.service.note;

import java.util.Map;

import com.enation.app.b2b2c.core.model.note.NoteServer;
import com.enation.framework.database.Page;

/**
 * 短信服务管理类
 * @author LiFenLong
 *
 */
public interface INoteServerManager {

	/**
	 * 获取短信列表
	 * @return List
	 */
	public Page getNoteServerList(int parseInt, int pageSize, Integer store_id, Map result);

	/**
	 * 增加短信
	 * @param noteServer
	 */
	public void add_noteInfo(NoteServer noteServer);
	/**
	 * 根据noteId获取短信列表
	 * @return
	 */
	public NoteServer getNoteServerByNoteId(Integer noteId);
	/**
	 * 修改短信
	 * @param noteServer
	 */
	public void edit_noteInfo(NoteServer noteServer);
	/**
	 * 
	 * 发送短信
	 * @param userName
	 * @param note_id
	 * @param integer 
	 */
	public void send_noteInfo(String[] userName, Integer note_id, Integer memberId);
	/**
	 * 添加短信后发送短信给会员
	 * @param integer 
	 * @return
	 */
	public void addAndSendNoteInfo(NoteServer noteServer, String[] username, Integer memberId);
}
