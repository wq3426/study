package com.enation.app.b2b2c.core.tag.note;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
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
public class NoteServerListTag extends BaseFreeMarkerTag{
	
	private INoteServerManager noteServerManager;
	private IStoreMemberManager storeMemberManager;
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		StoreMember storeMember = storeMemberManager.getStoreMember();
		Integer store_id = storeMember.getStore_id();
		int pageSize=10;
		String page = request.getParameter("page")==null?"1":request.getParameter("page");
		
		String creat_time=request.getParameter("creat_time");
		String noteName = request.getParameter("note_name");
		String status=request.getParameter("status");
		
		Map result=new HashMap();
		result.put("creat_time", creat_time);
		result.put("note_name", noteName);
		result.put("status", status);
		
		Page noteList=noteServerManager.getNoteServerList(Integer.parseInt(page),pageSize,store_id,result);
		Long totalCount = noteList.getTotalCount();
		
		result.put("page", page);
		result.put("pageSize", pageSize);
		result.put("totalCount", totalCount);
		result.put("noteList", noteList);
		return result;
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

	
}
