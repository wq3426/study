package com.enation.app.b2b2c.core.action.api.order;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.order.IStoreOrderPrintManager;
import com.enation.framework.action.WWAction;

/**
 * 店铺订单发货 API
 * @author fenlongli
 *
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/store")
@Action("storeOrderPrint")
@InterceptorRef(value="apiRightStack",params={"apiRightInterceptor.excludeMethods",""})
public class StoreOrderPrintApiAction extends WWAction{
	private IStoreOrderPrintManager storeOrderPrintManager;
	private Integer order_id;
	
	/**
	 * 打印发货单
	 * @param script 打印的script,String
	 * @return 发货单的script
	 */
	public String shipScript() {
		String script= storeOrderPrintManager.getShipScript(order_id);
		this.json=script;
		return this.JSON_MESSAGE;
	}

	public IStoreOrderPrintManager getStoreOrderPrintManager() {
		return storeOrderPrintManager;
	}

	public void setStoreOrderPrintManager(
			IStoreOrderPrintManager storeOrderPrintManager) {
		this.storeOrderPrintManager = storeOrderPrintManager;
	}

	public Integer getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
}
