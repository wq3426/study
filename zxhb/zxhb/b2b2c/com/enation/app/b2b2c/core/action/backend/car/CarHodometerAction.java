package com.enation.app.b2b2c.core.action.backend.car;

import java.net.URLDecoder;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.ICarHodometerManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

import edu.emory.mathcs.backport.java.util.TreeMap;
import net.sf.json.JSONObject;

/** @Description admin显示行程信息
 *
 * @createTime 2016年8月22日 上午10:35:47
 *
 * @author <a href="mailto:huashixin@trans-it.cn">huashixin</a>
 */

@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="list",type="freemarker", location="/b2b2c/admin/car/hodometerList.html"),
	 @Result(name="show_gps_img",type="freemarker", location="/b2b2c/admin/car/showGpsImg.html")
})
@Action("carHodometer")
public class CarHodometerAction extends WWAction{
	
	private ICarHodometerManager carHodometerManager;
	
	private String gps_imgurl;
	
	/**@description  展示初始数据
	 * @date 2016年8月22日 上午11:01:48
	 * @return String
	 */
	public String list(){	
		return "list";
	}
	
	/** @description  table列表数据
	 * @date 2016年8月22日 上午11:06:18
	 * @return String
	 */
	public String list_json(){
		
		String  keyword = this.getRequest().getParameter("keyword");
		Map paramMap = new TreeMap();
		paramMap.put("order",this.getOrder());
		paramMap.put("pageSize",this.getPageSize());
		paramMap.put("pageNo", this.getPage());
		paramMap.put("sort", this.getSort());
		paramMap.put("keyword",keyword);
		Page page = carHodometerManager.getCarHodometerList(paramMap);
		this.showGridJson(page);
		JSONObject object = JSONObject.fromObject(json);
		json = UploadUtil.replacePath(object.toString());
		return this.JSON_MESSAGE;
	}
	
	/** @description 显示gps图片
	 * @date 2016年8月22日 下午4:11:22
	 * @return String
	 */
	public String showGpsImg(){
		gps_imgurl = this.getRequest().getParameter("gps_imgurl");
		if(!StringUtil.isNull(gps_imgurl)){
			gps_imgurl = URLDecoder.decode(gps_imgurl);
		}
		return "show_gps_img";
	}

	public ICarHodometerManager getCarHodometerManager() {
		return carHodometerManager;
	}

	public void setCarHodometerManager(ICarHodometerManager carHodometerManager) {
		this.carHodometerManager = carHodometerManager;
	}

	public String getGps_imgurl() {
		return gps_imgurl;
	}

	public void setGps_imgurl(String gps_imgurl) {
		this.gps_imgurl = gps_imgurl;
	}
	
	
	
}
