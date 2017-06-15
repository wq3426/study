/**
 * 
 */
package com.enation.app.base.core.action;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;

/**
 * 检测某组件Action
 * @author kingapex
 *2015-5-7
 * @author kanon 2015-9-24 version1.1 添加注释
 */
@ParentPackage("eop_default")
@Namespace("/core/admin")
public class CheckComponentAction extends WWAction {
 
	private String id;
	/**
	 * 根据bean id检测组件是否存在
	 * @param id bean id
	 * @return 组件是否存在 
	 */
	public String execute(){
		try {
			Object obj = SpringContextHolder.getBean(id);
			if(obj==null){
				this.showErrorJson("不存在");
			}else{
				this.showSuccessJson("存在");
			}
		} catch (Exception e) {
			this.showErrorJson("不存在");
		}
		return this.JSON_MESSAGE;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
