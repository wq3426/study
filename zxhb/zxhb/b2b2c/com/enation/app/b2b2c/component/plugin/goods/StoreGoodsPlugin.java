package com.enation.app.b2b2c.component.plugin.goods;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.shop.ShopApp;
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.core.plugin.goods.IGoodsAfterEditEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsBeforeAddEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsBeforeEditEvent;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
/**
 * 店铺商品Plugin
 * @author LiFenLong
 *
 */
@Component
public class StoreGoodsPlugin extends AutoRegisterPlugin implements IGoodsAfterEditEvent,IGoodsBeforeAddEvent,IGoodsBeforeEditEvent {
	private IGoodsGalleryManager goodsGalleryManager;
	private IStoreMemberManager storeMemberManager;
	private IProductManager productManager;
	private IGoodsManager goodsManager;
	private IDaoSupport daoSupport;
	@Override
	/**
	 * 店铺商品修改后修改商品相册内容
	 */
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request) {
		String[] imgFs=request.getParameterValues("del_pic");
		if(imgFs!=null){
			for (int i = 0; i < imgFs.length; i++) {
				goodsGalleryManager.delete(imgFs[i]);
			}
		}
	}
	/**
	 * 修改商品相册内容
	 * @param gallery
	 */
	private void edit(GoodsGallery gallery){
		daoSupport.update("es_goods_gallery", gallery, "img_id="+gallery.getImg_id());
	}

	/**
	 * 如果未上架改为已上架增加店铺商品数量
	 * 如果已上架改为不上架则减少商品数量
	 */
	@Override
	public void onBeforeGoodsEdit(Map goods, HttpServletRequest request) {
		Map map=goodsManager.get(Integer.valueOf(goods.get("goods_id").toString()));
		if(goods.get("market_enable").equals("1")){
			if(map.get("market_enable").toString().equals("0")){
				StoreMember member= storeMemberManager.getStoreMember();
				String sql="update es_store set goods_num=goods_num+1 where store_id=?";
				this.daoSupport.execute(sql, member.getStore_id());
			}
		}else if(goods.get("market_enable").equals("0")){
			if(map.get("market_enable").toString().equals("1")){
				StoreMember member= storeMemberManager.getStoreMember();
				String sql="update es_store set goods_num=goods_num-1 where store_id=?";
				this.daoSupport.execute(sql, member.getStore_id());
			}
		}
		//设置商品列表图片
		String[] status=request.getParameterValues("status");
		String[] imgFs=request.getParameterValues("goods_fs");
		if(status!=null&&imgFs!=null){
			if(status[0].equals("3")||status[0].equals("1")){
				goods.put("thumbnail", imgFs[0]);
			}
		}
	}
	@Override
	/**
	 * 添加商品修改店铺商品数量
	 */
	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request) {
		
		//如果商品上架则更改店铺商品数量
		if(goods.get("market_enable").equals("1")){
			
			StoreMember member=storeMemberManager.getStoreMember();
			String sql="update es_store set goods_num=goods_num+1 where store_id=?";
			if(member!=null){
				//会员不为null说明是商家中心在保存，用会员的storeid
				this.daoSupport.execute(sql, member.getStore_id());
			}else{
				//会员为null，说明是自营店在保存，则使用自营店的storeid
				this.daoSupport.execute(sql,ShopApp.self_storeid);
			}
			
		}
		
		//添加商品列表图片
		String[] status=request.getParameterValues("status");
		String[] imgFs=request.getParameterValues("goods_fs");
		if(status!=null&&imgFs!=null){
			if(status[0].equals("1")){
				goods.put("thumbnail", imgFs[0]);
			}
		}
		//添加商品购买次数以及评论次数
		goods.put("buy_num", 0) ;	//购买数量
		goods.put("comment_num", 0);	//评论次数
		
	}

	public IGoodsGalleryManager getGoodsGalleryManager() {
		return goodsGalleryManager;
	}
	public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
		this.goodsGalleryManager = goodsGalleryManager;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	public IProductManager getProductManager() {
		return productManager;
	}
	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	
}
