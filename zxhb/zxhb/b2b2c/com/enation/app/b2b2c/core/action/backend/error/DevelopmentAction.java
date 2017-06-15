package com.enation.app.b2b2c.core.action.backend.error;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IB2b2cGoodsTagManager;
import com.enation.framework.action.WWAction;

/** @Description (页面建设中)
 *
 * @createTime 2016年10月21日 下午3:16:52
 *
 * @author <a href="mailto:huashixin@trans-it.cn">huashixin</a>
 */
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="error",type="freemarker", location="/admin/5002.html")
})
@Action("development")
public class DevelopmentAction extends WWAction{
	/**
	 * 商品标签列表
	 * @return
	 */
	public String error(){
		return "error";
	}
	
}
