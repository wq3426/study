package com.enation.app.b2b2c.core.action.backend.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.store.StoreLevel;
import com.enation.app.b2b2c.core.model.store.StoreRate;
import com.enation.app.b2b2c.core.service.store.IStoreLevelManager;
import com.enation.app.b2b2c.core.service.store.IStoreRateManager;
import com.enation.app.shop.core.service.OrderType;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;

@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({ @Result(name = "list", type = "freemarker", location = "/b2b2c/admin/store/store_rate_list.html"),
		@Result(name = "add", type = "freemarker", location = "/b2b2c/admin/store/store_rate_add.html"),
		@Result(name = "editPage", type = "freemarker", location = "/b2b2c/admin/store/store_rate_edit.html") })
@Action("storerate")
public class StoreRateAction extends WWAction {

	private Integer rate_id;

	private Integer order_type;

	private Integer level_id;

	private Double original_service_rate;

	private Double flow_service_rate;

	private Double original_handling_rate;

	private Double flow_handling_rate;

	private String order_type_name;
	
	private String store_level_name;
	
	private StoreRate storeRate;

	private IStoreLevelManager storeLevelManager;

	private IStoreRateManager storeRateManager;

	private List<StoreLevel> storeLevels;

	private StoreLevel storeLevel;

	private List<StoreRate> storeRates;
	

	private Map other;

	public String add() {
		storeLevels = storeLevelManager.storeLevelList();
		return "add";
	}

	public String save() {
		try {

			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String order_type = request.getParameter("order_type");
			String level_id = request.getParameter("level_id");
			String original_service_rate = request.getParameter("original_service_rate");
			String original_handling_rate = request.getParameter("original_handling_rate");
//			String flow_service_rate = request.getParameter("flow_service_rate");
//			String flow_handling_rate = request.getParameter("flow_handling_rate");
			String flow_service_rate = "0";
			String flow_handling_rate = "0";
			if (StringUtil.isNull(level_id)) {
				showErrorJson("没有获取到店铺等级");
				return JSON_MESSAGE;
			}
			if (StringUtil.isNull(order_type)) {
				showErrorJson("没有获取到店铺等级");
				return JSON_MESSAGE;
			}
			storeRate = storeRateManager.getStoreRate(Integer.parseInt(order_type), Integer.parseInt(level_id));
			if (storeRate != null) {
				showErrorJson("该费率已经存在！");
				return JSON_MESSAGE;
			}

			storeRate = new StoreRate();
			storeRate.setOriginal_service_rate(CurrencyUtil.div(Double.parseDouble(original_service_rate), 100, 2));
			storeRate.setFlow_service_rate(CurrencyUtil.div(Double.parseDouble(flow_service_rate), 100, 2));
			storeRate.setOriginal_handling_rate(CurrencyUtil.div(Double.parseDouble(original_handling_rate), 100, 2));
			storeRate.setFlow_handling_rate(CurrencyUtil.div(Double.parseDouble(flow_handling_rate), 100, 2));
			storeRate.setOrder_type(Integer.parseInt(order_type));
			storeRate.setLevel_id(Integer.parseInt(level_id));
			storeRateManager.save(storeRate);

			showSuccessJson("成功");
		} catch (Exception e) {
			e.printStackTrace();
			showErrorJson("保存费率错误！");
		}
		return JSON_MESSAGE;
	}

	public String orderTypeListJson() {
		// 获取订单状态
		String s = OrderType.getOrderTypeJson();
		this.json = s.replace("name", "text").replace("orderType", "id");
		return JSON_MESSAGE;
	}

	public String storeLvListJson() {
		// 获取店铺等级列表
		storeLevels = storeLevelManager.storeLevelList();
		String s = JSONArray.fromObject(storeLevels).toString();
		this.json = s.replace("level_name", "text").replace("level_id", "id");
		return JSON_MESSAGE;
	}

	public String edit() {

		return "edit";
	}

	public String list() {
		storeLevels = storeLevelManager.storeLevelList();
		return "list";
	}
	
	public String rate_listJson(){
		other = new HashMap();
		other.put("level_id", level_id);
		other.put("order_type", order_type);
		this.showGridJson(storeRateManager.rate_list(other, this.getPage(), this.getPageSize(),this.getSort(),this.getOrder()));
		return JSON_MESSAGE;
	}
	
	public String delStoreRate(){
		try {
			storeRateManager.delStoreRate(rate_id);
			this.showSuccessJson("删除费率选项成功");
		} catch (Exception e) {
			this.showErrorJson("删除费率选项失败");
			this.logger.error("删除费率选项失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	
	public String editStoreRate(){
		try{
			StoreRate storeRate = storeRateManager.getStoreRate(rate_id);
			storeRate.setOriginal_service_rate(CurrencyUtil.div(original_service_rate, 100, 2));
			storeRate.setOriginal_handling_rate(CurrencyUtil.div(original_handling_rate, 100, 2));
			
			storeRate.setFlow_service_rate(CurrencyUtil.div(/*flow_service_rate*/0, 100, 2));
			storeRate.setFlow_handling_rate(CurrencyUtil.div(/*flow_handling_rate*/0, 100, 2));
			storeRateManager.editStoreRate(storeRate);
			this.showSuccessJson("修改成功");
		}catch(Exception e){
			this.showErrorJson("修改失败");
			this.logger.error("修改费率选项失败:"+e);
		}
		return JSON_MESSAGE;
	}
	
	public String editPage(){
		storeRate = storeRateManager.getStoreRate(rate_id);
		storeRate.setFlow_handling_rate(CurrencyUtil.mul(storeRate.getFlow_handling_rate(), 100));
		storeRate.setFlow_service_rate(CurrencyUtil.mul(storeRate.getFlow_service_rate(), 100));
		storeRate.setOriginal_handling_rate(CurrencyUtil.mul(storeRate.getOriginal_handling_rate(), 100));
		storeRate.setOriginal_service_rate(CurrencyUtil.mul(storeRate.getOriginal_service_rate(), 100));
		//获取订单类型
		order_type_name = OrderType.getOrderTypeTest(storeRate.getOrder_type());
		store_level_name = storeLevelManager.getStoreLevel(storeRate.getLevel_id()).getLevel_name();
		return "editPage";
		
	}
	public Integer getRate_id() {
		return rate_id;
	}

	public void setRate_id(Integer rate_id) {
		this.rate_id = rate_id;
	}

	public Integer getOrder_type() {
		return order_type;
	}

	public void setOrder_type(Integer order_type) {
		this.order_type = order_type;
	}

	public Integer getLevel_id() {
		return level_id;
	}

	public void setLevel_id(Integer level_id) {
		this.level_id = level_id;
	}

	public StoreRate getStoreRate() {
		return storeRate;
	}

	public void setStoreRate(StoreRate storeRate) {
		this.storeRate = storeRate;
	}

	public IStoreLevelManager getStoreLevelManager() {
		return storeLevelManager;
	}

	public void setStoreLevelManager(IStoreLevelManager storeLevelManager) {
		this.storeLevelManager = storeLevelManager;
	}

	public List<StoreLevel> getStoreLevels() {
		return storeLevels;
	}

	public void setStoreLevels(List<StoreLevel> storeLevels) {
		this.storeLevels = storeLevels;
	}

	public StoreLevel getStoreLevel() {
		return storeLevel;
	}

	public void setStoreLevel(StoreLevel storeLevel) {
		this.storeLevel = storeLevel;
	}

	public IStoreRateManager getStoreRateManager() {
		return storeRateManager;
	}

	public void setStoreRateManager(IStoreRateManager storeRateManager) {
		this.storeRateManager = storeRateManager;
	}

	public List<StoreRate> getStoreRates() {
		return storeRates;
	}

	public void setStoreRates(List<StoreRate> storeRates) {
		this.storeRates = storeRates;
	}

	public Map getOther() {
		return other;
	}

	public void setOther(Map other) {
		this.other = other;
	}

	public Double getOriginal_service_rate() {
		return original_service_rate;
	}

	public void setOriginal_service_rate(Double original_service_rate) {
		this.original_service_rate = original_service_rate;
	}

	public Double getFlow_service_rate() {
		return flow_service_rate;
	}

	public void setFlow_service_rate(Double flow_service_rate) {
		this.flow_service_rate = flow_service_rate;
	}

	public Double getOriginal_handling_rate() {
		return original_handling_rate;
	}

	public void setOriginal_handling_rate(Double original_handling_rate) {
		this.original_handling_rate = original_handling_rate;
	}

	public Double getFlow_handling_rate() {
		return flow_handling_rate;
	}

	public void setFlow_handling_rate(Double flow_handling_rate) {
		this.flow_handling_rate = flow_handling_rate;
	}

	public String getOrder_type_name() {
		return order_type_name;
	}

	public void setOrder_type_name(String order_type_name) {
		this.order_type_name = order_type_name;
	}

	public String getStore_level_name() {
		return store_level_name;
	}

	public void setStore_level_name(String store_level_name) {
		this.store_level_name = store_level_name;
	}
	
}
