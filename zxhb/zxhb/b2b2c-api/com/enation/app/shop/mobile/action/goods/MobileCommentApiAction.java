package com.enation.app.shop.mobile.action.goods;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.ICommentManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @Description 评价api
 *
 * @createTime 2016年8月25日 下午4:36:12
 *
 * @author <a href="mailto:huashixin@trans-it.cn">huashixin</a>
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("comment")
public class MobileCommentApiAction extends WWAction {

	private ICommentManager commentManager;

	public String goodsCommentList() {
		try {
			HttpServletRequest request = this.getRequest();
			String page = request.getParameter("page");
			String store_id = request.getParameter("store_id");
			String goods_id = request.getParameter("goods_id");
			String level = request.getParameter("level");
			if (StringUtil.isNull(goods_id)) {
				showErrorJson("参数错误");
				return this.json;
			}
			page = (page == null || page.equals("")) ? "1" : page;
			int pageSize = 10;
			Page goodsCommentPage = commentManager.listGoodsCommentByLevel(Integer.parseInt(page), pageSize,
					Integer.parseInt(goods_id),level);
			goodsCommentPage.setCurrentPageNo(Integer.parseInt(page));
			String commentList = UploadUtil.replacePath(JSONObject.fromObject(goodsCommentPage).toString());
			JSONObject obj = JSONObject.fromObject(commentList);
			int[] levels = { 1, 2, 3, 4 };// 评价程度 全部、好评、中评、差评
			String []levelsName = {"全部","好评","中评","差评"};
			JSONArray levelArr = new JSONArray();
			for (int i = 0; i < levels.length; i++) {
				JSONObject object = new JSONObject();
				object.put("goods_id", goods_id);
				object.put("level", levels[i]);
				object.put("levelName", levelsName[i]);
				object.put("count", commentManager.count(levels[i], goods_id));
				levelArr.add(object);
			}
			obj.put("commentLevels", levelArr);
			this.json = "{result : 1,data : " + obj + "}";
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("获取数据错误");
		}
		return JSON_MESSAGE;
	}

	public ICommentManager getCommentManager() {
		return commentManager;
	}

	public void setCommentManager(ICommentManager commentManager) {
		this.commentManager = commentManager;
	}

}