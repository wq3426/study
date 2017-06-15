package com.enation.test.comments.test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.enation.app.shop.core.model.Comments;
import com.enation.app.shop.core.service.ICommentsManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.database.Page;
import com.enation.framework.test.SpringTestSupport;

 
public class CommentsTest extends SpringTestSupport {

	private ICommentsManager commentsManager;
	private ApplicationContext context;
    
	@Before
	public void mock() {
	 
		commentsManager =  getBean("commentsManager");

		
	 
	}
	
	@Test
	public void getComments(){
		Page page = commentsManager.pageComments(1, 15, "discuss");
		List<Map> list = (List<Map>) (page.getResult());
		for(Map map : list){
			//System.out.println(map.get("comment_id")+":main");
			for(Comments comments:(List<Comments>)map.get("list")){
				//System.out.println(comments.getComment_id()+":sub");
			}
		}
	}
	
	@Test
	public void getComments_display(){
		Page page = commentsManager.pageComments_Display(2, 3, 2, "discuss");
		List<Map> list = (List<Map>) (page.getResult());
		for(Map map : list){
			//System.out.println(map.get("comment_id")+":main");
			for(Comments comments:(List<Comments>)map.get("list")){
				//System.out.println(comments.getComment_id()+":sub");
			}
		}
	}
	
	@Test
	public void addComments(){
		Comments comments = new Comments();
		comments.setFor_comment_id(1);
		comments.setAuthor("王峰");
		comments.setTime((new Date()).getTime());
		comments.setTitle("测试");
		commentsManager.addComments(comments);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public ICommentsManager getCommentsManager() {
		return commentsManager;
	}

	public void setCommentsManager(ICommentsManager commentsManager) {
		this.commentsManager = commentsManager;
	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}
}
