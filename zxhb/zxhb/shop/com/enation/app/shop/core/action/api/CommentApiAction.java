package com.enation.app.shop.core.action.api;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.tools.ant.types.CommandlineJava.SysProperties;

import com.enation.app.shop.core.service.ICommentManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;
import com.enation.framework.pager.impl.SimplePageHtmlBuilder;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONObject;

/**
 * 评论api
 * 
 * @author kingapex 2013-8-7下午5:41:07
 */
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("commentApi")
@InterceptorRef(value = "apiRightStack")
public class CommentApiAction extends WWAction {

	private int goods_id;

	private ICommentManager commentManager;

	private int pageNo;

	private int pageSize;

	private String level;

	public String getGoodsCommentJson() {
		try{
			HttpServletRequest request = this.getRequest();
			pageNo = pageNo == 0 ? 1 : pageNo;
			level = (StringUtil.isNull(level) ? "1" : level);
			pageSize = 10;
			Page page = commentManager.listGoodsCommentByLevel(pageNo, pageSize, goods_id, level);
			page.setCurrentPageNo(pageNo);
			String data = UploadUtil.replacePath(JSONObject.fromObject(page).toString());
			this.json = "{\"result\" : \"1\",\"data\" : " + data +"}"; 
		}catch(Exception e){
			showErrorJson("评价列表出错");
			e.printStackTrace(); 
		}
		return JSON_MESSAGE;
	}

	public int getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(int goods_id) {
		this.goods_id = goods_id;
	}

	public ICommentManager getCommentManager() {
		return commentManager;
	}

	public void setCommentManager(ICommentManager commentManager) {
		this.commentManager = commentManager;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

}
