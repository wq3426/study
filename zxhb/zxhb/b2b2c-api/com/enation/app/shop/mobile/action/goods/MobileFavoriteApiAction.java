/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：收藏api  
 * 修改人：  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.goods;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Favorite;
import com.enation.app.shop.core.service.IFavoriteManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.TestUtil;

/**
 * 收藏API 提供收藏 增删改查
 * 
 * @author Sylow
 * @version v1.0 , 2015-08-24
 * @since v1.0
 */
@SuppressWarnings("serial")
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("favorite")
public class MobileFavoriteApiAction extends WWAction {


	private IFavoriteManager favoriteManager;
								
	private final int PAGE_SIZE = 20;

	/**
	 * 添加收藏
	 * @param id 商品id  必填
	 * @return
	 */
	public String add() {
		try {
			Member member = UserConext.getCurrentMember();
			if (member == null) {
				this.showErrorJson("未登录不能进行此操作！");
				return JSON_MESSAGE;
			}

			int goods_id = NumberUtils.toInt(getRequest().getParameter("id"));
			favoriteManager.add(goods_id);
			this.showSuccessJson("收藏成功！");

		} catch (RuntimeException e) {
			TestUtil.print(e);
			this.logger.error("添加收藏出错", e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 删除一个会员收藏
	 * 
	 * get参数favoriteid ：要删除的会员收藏地址id,String型 返回的json : result 为1表示添加成功，0表示失败
	 * ，int型 返回的json : message 为提示信息 ，String型
	 */
	public String delete() {
		try {
			Member member = UserConext.getCurrentMember();
			if (member == null) {
				this.showErrorJson("未登录不能进行此操作！");
				return WWAction.JSON_MESSAGE;
			}

			HttpServletRequest request = getRequest();

			int favorite_id = NumberUtils.toInt(
					request.getParameter("favoriteId"), 0);
			if (favorite_id > 0) {
				Favorite favorite = favoriteManager.get(favorite_id);
				if (favorite.getMember_id() != member.getMember_id()) {
					this.showErrorJson("您没有权限进行此项操作！");
					return JSON_MESSAGE;
				}

				try {
					this.favoriteManager.delete(favorite_id);
					this.showSuccessJson("删除成功");
				} catch (Exception e) {
					if (this.logger.isDebugEnabled()) {
						logger.error(e.getStackTrace());
					}
					this.showErrorJson("删除失败！");
				}
				return JSON_MESSAGE;
			}

			int goods_id = NumberUtils.toInt(request.getParameter("id"), 0);
			if (goods_id > 0) {
				Favorite favorite = favoriteManager.get(goods_id,
						member.getMember_id());
				if (favorite == null) {
					this.showErrorJson("您没有权限进行此项操作！");
					return JSON_MESSAGE;
				}

				try {
					this.favoriteManager.delete(goods_id,
							member.getMember_id());
					this.showSuccessJson("删除成功");
				} catch (Exception e) {
					if (this.logger.isDebugEnabled()) {
						logger.error(e.getStackTrace());
					}
					this.showErrorJson("删除失败！");
				}
				return WWAction.JSON_MESSAGE;
			}

		} catch (RuntimeException e) {
			this.logger.error("删除出错", e);
			this.showErrorJson(e.getMessage() + "");
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 所有收藏列表
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String list() {
		try {
			Member member = UserConext.getCurrentMember();
			if (member == null) {
				this.showErrorJson("未登录不能进行此操作！");
				return JSON_MESSAGE;
			}
			//HttpServletRequest request = getRequest();
			//int page = NumberUtils.toInt(request.getParameter("page"), 1);
			List list = favoriteManager.listGoods();
			this.json = JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			this.logger.error("获取收藏列表出错", e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	public IFavoriteManager getFavoriteManager() {
		return favoriteManager;
	}

	public void setFavoriteManager(IFavoriteManager favoriteManager) {
		this.favoriteManager = favoriteManager;
	}

}
