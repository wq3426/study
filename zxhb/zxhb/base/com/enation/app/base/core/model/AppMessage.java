package com.enation.app.base.core.model;

/**
 *   app 消息
 * @author chopper
 *
 */
public class AppMessage {

	/**
	 * id
	 */
	public int 	id;	
	/**
	 * 标题
	 */
	public String title;
	/**
	 * 消息分类id
	 */
	public int type_id;
	/**
	 * 简介
	 */
	public String synopsis;
	/**
	 * 作者
	 */
	public String author;
	/**
	 * 详细内容
	 */
	public String detail;
	/**
	 * 图片
	 */
	public String image;
	/**
	 * 链接
	 */
	public String href;
	/**
	 * 创建时间
	 */
	public String create_time;
	
	/**
	 * 消息状态
	 */
	public int status;
	
	/**
	 * 点击次数
	 */
	public int clickcount;
	/**
	 * 店铺id
	 */
	public int store_id;
	//商品链接goods_id
	private int linkGoods_id;//链接商品id
	
	//商品produc_id;
	private Integer product_id;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getType_id() {
		return type_id;
	}
	public void setType_id(int type_id) {
		this.type_id = type_id;
	}
	public String getSynopsis() {
		return synopsis;
	}
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getClickcount() {
		return clickcount;
	}
	public void setClickcount(int clickcount) {
		this.clickcount = clickcount;
	}
	public int getStore_id() {
		return store_id;
	}
	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}
	public int getLinkGoods_id() {
		return linkGoods_id;
	}
	public void setLinkGoods_id(int linkGoods_id) {
		this.linkGoods_id = linkGoods_id;
	}
	public Integer getProduct_id() {
		return product_id;
	}
	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}
}
