package com.enation.app.b2b2c.core.action.backend.bill;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.bill.BillDetail;
import com.enation.app.b2b2c.core.model.bill.BillStatusEnum;
import com.enation.app.b2b2c.core.service.bill.IBillManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Store;
import com.enation.framework.action.WWAction;

/**
 * 结算管理
 * @author fenlongli
 *
 */
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/b2b2c/admin/bill/list.html"),
	 @Result(name="detail_list",type="freemarker", location="/b2b2c/admin/bill/detail_list.html"),
	 @Result(name="detail",type="freemarker", location="/b2b2c/admin/bill/detail.html")
})
@Action("storeBill")
public class StoreBillAction extends WWAction{
	private Integer bill_id;
	private String sn;
	private Store store;
	private Integer status;
	private BillDetail billDetail;
	private IBillManager billManager;
	private IStoreManager storeManager;
	private String keyword;
	/**
	 * 结算单列表
	 * @return
	 */
	public String list(){
		return "list";
	}
	/**
	 * 获取结算列表JSON
	 * @return
	 */
	public String list_json(){
		this.webpage=billManager.bill_list(this.getPage(), this.getPageSize());
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	/**
	 * 结算详细列表
	 * @return 结算详细列表页
	 */
	public String detail_list(){
		return "detail_list";
	}
	/**
	 * 获取结算详细列表JSON
	 * @param bill_id 结算单Id
	 * @param keyword 店铺名称关键字
	 * @param webpage 结算详细列表
	 * @return 结算详细列表页JSON
	 */
	public String detail_list_json(){
		this.webpage=billManager.bill_detail_list(this.getPage(), this.getPageSize(), bill_id, keyword);
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	/**
	 * 获取结算单详细
	 * @param id 结算详细单Id
	 * @param billDetail 结算详细单
	 * @param store 店铺信息
	 * @return
	 */
	public String detail(){
		billDetail=this.billManager.get_bill_detail(bill_id);
		store=storeManager.getStore(billDetail.getStore_id());
		return "detail";
	}
	/**
	 * 修改结算详细状态
	 * @param status 结算单详细状态
	 * @param bill_id 结算详细单状态
	 * @return
	 */
	public String edit_bill_detail(){
		try {
			if(status==BillStatusEnum.COMPLETE.getIndex()){
				status=BillStatusEnum.PASS.getIndex();
			}else if(status==BillStatusEnum.PASS.getIndex()){
				status=BillStatusEnum.PAY.getIndex();
			}
			billManager.edit_billdetail_status(bill_id, status);
			this.showSuccessJson("更改状态成功");
		} catch (Exception e) {
			this.showErrorJson("更改状态失败，请重试");
			this.logger.error("更改结算单状态失败",e);
		}
		return this.JSON_MESSAGE;
		
	}
	/**
	 * 订单结算列表JSON
	 * @param page 分页页码
	 * @param pageSize 分页
	 * @param sn 结算单编号
	 * @return
	 */
	public String bill_order_list_json(){
		this.webpage=billManager.bill_order_list(this.getPage(), this.getPageSize(), sn);
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	/**
	 * 结算退货结算列表JSON
	 * @param page 分页页码
	 * @param pageSize 分页
	 * @param sn 结算单编号
	 */
	public String bill_sellback_list_json(){
		this.webpage=billManager.bill_sell_back_list(this.getPage(), this.getPageSize(), sn);
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	
	
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getBill_id() {
		return bill_id;
	}
	public void setBill_id(Integer bill_id) {
		this.bill_id = bill_id;
	}
	public IBillManager getBillManager() {
		return billManager;
	}
	public void setBillManager(IBillManager billManager) {
		this.billManager = billManager;
	}
	public BillDetail getBillDetail() {
		return billDetail;
	}
	public void setBillDetail(BillDetail billDetail) {
		this.billDetail = billDetail;
	}
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	public IStoreManager getStoreManager() {
		return storeManager;
	}
	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
}
