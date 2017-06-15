package com.enation.app.shop.core.model;

/**
 * 热门商品
 * @author Sylow
 * @version v1.0, 2016年2月28日
 * @since v5.2
 */
public class HotGoods {
	
	private int id;
	/**位置*/
	private int site;
	/**商品编号*/
	private String goods_sn;
	/**url*/
	private String url;
	/**点击量*/
	private int click_count;
	/**商家id*/
	private int store_id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSite() {
		return site;
	}
	public void setSite(int site) {
		this.site = site;
	}
	public String getGoods_sn() {
		return goods_sn;
	}
	public void setGoods_sn(String goods_sn) {
		this.goods_sn = goods_sn;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getClick_count() {
		return click_count;
	}
	public void setClick_count(int click_count) {
		this.click_count = click_count;
	}
	public int getStore_id() {
		return store_id;
	}
	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}
	
}
