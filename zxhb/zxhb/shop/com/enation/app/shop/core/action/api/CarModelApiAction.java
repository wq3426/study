/**
 * 版权：Copyright (C) 2016  中安信博（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：设备api  
 * 修改人：
 * 修改时间：2016-03-14
 * 修改内容: 
 */
package com.enation.app.shop.core.action.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.CarInfo;
import com.enation.app.shop.core.service.ICarManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * 车辆信息api
 * 
 * 实体类：CarModel
 * @author 
 * @date 2016-03-14
 * @version v0.1
 * @since 
 */
//curl -H 'Content-Type:application/json' -X PUT -d '[{"timestamp":"20160311"}, {"carNo": "888888"}]' 'http://localhost:8080/api/mobile/device!updateJSONArray.do'

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("carmodel")
@SuppressWarnings("serial")
public class CarModelApiAction extends WWAction {
	private ICarManager carManager;
	
	/**
	 * 获取汽车品牌列表
	 * @return
	 */
	public String getCarBrandList(){
		try {
			List carModelList = carManager.getCarBrandList();
			JSONArray resultArray = JSONArray.fromObject(carModelList);
			for(int i=0; i<resultArray.size(); i++){
				JSONObject obj = resultArray.getJSONObject(i);
				obj.put("brandimage", UploadUtil.replacePath(obj.getString("brandimage")));
			}
			this.json = "{\"result\":1,\"data\":"+resultArray+"}";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 根据品牌获取车系
	 * 
	 * 查询数据库返回：[{"series":"A3","type":"国产"},{"series":"A1","type":"进口"},{"series":"A5","type":"进口"},{"series":"Q3","type":"进口"}]
	 * 返给前端格式：[{"type":"国产", "series":["A3"]},{"type":"进口", "series":["A1","A5","Q3"]}]
	 * @return
	 */
	public String getCarSeries(){
		String brandName = getRequest().getParameter("brand");
		List carSeriesList = carManager.getCarSeries(brandName);
		JSONArray carArray = JSONArray.fromObject(carSeriesList);
		String temp = "";
		JSONArray returnJson = new JSONArray();
		JSONObject tempObj = new JSONObject();
		List<String> tempList = new ArrayList<String>();
		for(int i=0; i<carArray.size(); i++){
			JSONObject obj = carArray.getJSONObject(i);
			String type = obj.getString("type");
			String series = obj.getString("series");
			if("".equals(temp)){
				temp = type;
			}
			if(!temp.equals(type)){
				tempObj.put("type", temp);
				tempObj.put("series", tempList);
				returnJson.add(tempObj);
				temp = type;
				tempObj = new JSONObject();
				tempList = new ArrayList<String>();
			}
			tempList.add(series);
			if(i == carArray.size()-1){
				tempObj.put("type", temp);
				tempObj.put("series", tempList);
				returnJson.add(tempObj);
			}
		}
		this.json = "{\"result\":1,\"data\":"+returnJson+"}";
		return JSON_MESSAGE;
	}
	
	/**
	 * 根据品牌brand、类型type、车系series获取车款
	 * 
	 * 查询数据库返回：[{"id":"1","model":"a"},{"id":"2","model":"b"},{"id":"3","model":"c"}]
	 * 返给前端格式：[{"id":"1","model":"a"},{"id":"2","model":"b"},{"id":"3","model":"c"}]
	 * @return
	 */
	public String getCarModels(){
		String brand = getRequest().getParameter("brand");
		String type = getRequest().getParameter("type");
		String series = getRequest().getParameter("series");
		List carModelList = carManager.getCarModelsList(brand, type, series);
		JSONArray carmodelArray = JSONArray.fromObject(carModelList);
		this.json = "{\"result\":1,\"data\":"+carmodelArray+"}";
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取车辆使用性质和种类的json
	 * @date 2016年9月13日 下午8:47:21
	 * @return
	 */
	public String getCarUseTypeAndKind(){
		JSONObject obj = new JSONObject();
		
		obj.put("car_use_types", CarInfo.getCarUseTypeJson());
		obj.put("car_kinds", CarInfo.getCarKindMap());
		
		this.json = "{\"result\":1,\"data\":"+obj.toString()+"}";
		
		return JSON_MESSAGE;
	}
		
	/**
	 * 给请求返回JSON对象作为响应
	 * @return
	public void returnJson(){
		JSONObject obj = new JSONObject();
		obj.put("username", "wq");
		obj.put("password", "123456");
		this.render(obj.toString(), "text/x-json;charset=UTF-8");
	}
	*/
	
	public ICarManager getCarManager() {
		return carManager;
	}

	public void setCarManager(ICarManager carManager) {
		this.carManager = carManager;
	}
}
