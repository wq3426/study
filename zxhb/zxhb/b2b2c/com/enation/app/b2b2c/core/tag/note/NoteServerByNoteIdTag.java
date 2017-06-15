package com.enation.app.b2b2c.core.tag.note;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.note.NoteServer;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.note.INoteServerManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 短信服务
 * @author yunbs
 */
@Component
public class NoteServerByNoteIdTag extends BaseFreeMarkerTag{
	
	private INoteServerManager noteServerManager;
	@Override
	protected Object exec(Map param) throws TemplateModelException {
		Integer noteId = (Integer) param.get("noteId");
		NoteServer noteServer = noteServerManager.getNoteServerByNoteId(noteId);
		return noteServer;
	}

	public INoteServerManager getNoteServerManager() {
		return noteServerManager;
	}

	public void setNoteServerManager(INoteServerManager noteServerManager) {
		this.noteServerManager = noteServerManager;
	}
}
