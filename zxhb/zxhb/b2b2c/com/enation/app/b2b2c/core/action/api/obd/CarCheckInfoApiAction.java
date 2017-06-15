package com.enation.app.b2b2c.core.action.api.obd;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.CarCheckInfo;
import com.enation.app.shop.core.service.ICarCheckInfoManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;

@Component
@SuppressWarnings("serial")
@Scope("prototype")
@Namespace("/api/mobile")
@ParentPackage("eop_default")
@Action("carCheck")
public class CarCheckInfoApiAction extends WWAction{
	
	private ICarCheckInfoManager carCheckInfomanager;
	private String codes;
	private int sum;
	private String carplate;
	
	/**
	 * 插入汽车体检有问题的信息
	 * result 	为1表示调用成功0表示失败
	 */
	public String add_carCheckInfo(){
		try {
			CarCheckInfo carCheckInfo=this.setParam();
			carCheckInfomanager.addCarCheckInfo(carCheckInfo);
			this.showSuccessJson("上传成功");
		} catch (Exception e) {
			this.showSuccessJson("上传成功");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 设置优惠卷参数
	 * @return
	 */
	private CarCheckInfo setParam(){
		
		CarCheckInfo carCheckInfo=new CarCheckInfo();
		carCheckInfo.setCodes(codes);
		carCheckInfo.setSum(sum);
		carCheckInfo.setCarplate(carplate);
		carCheckInfo.setCheck_time(DateUtil.getDateline());
		return carCheckInfo;
	}

	public ICarCheckInfoManager getCarCheckInfomanager() {
		return carCheckInfomanager;
	}

	public void setCarCheckInfomanager(ICarCheckInfoManager carCheckInfomanager) {
		this.carCheckInfomanager = carCheckInfomanager;
	}

	public String getCodes() {
		return codes;
	}

	public void setCodes(String codes) {
		this.codes = codes;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public String getCarplate() {
		return carplate;
	}

	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}
	
}
