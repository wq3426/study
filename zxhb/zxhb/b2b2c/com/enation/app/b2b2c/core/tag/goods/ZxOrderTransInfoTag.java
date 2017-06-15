package com.enation.app.b2b2c.core.tag.goods;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IZxhbOrderManager;
import com.enation.eop.sdk.utils.KdniaoTrackQueryAPI;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.taglib.BaseFreeMarkerTag;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

import freemarker.template.TemplateModelException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @Description 订单详情获取
 *
 * @createTime 2016年12月6日 下午9:06:09
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */

@Component
public class ZxOrderTransInfoTag extends BaseFreeMarkerTag {
	
	private IStoreMemberManager storeMemberManager;

	private Map orderMap;
    
    private IZxhbOrderManager zxhbOrderManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		JSONObject returnObj = new JSONObject();
		try {
			String order_sn = (String) params.get("order_sn");
			
			JSONObject trans_info = null;
			
			orderMap = zxhbOrderManager.getSpecificsBySn(order_sn);
			orderMap.put("spec_image", UploadUtil.replacePath((String)orderMap.get("spec_image")));
			orderMap.put("create_time_date",DateUtil.toString((long)orderMap.get("create_time"), "yyyy-MM-dd hh:mm:ss"));
			String shipping_type = (String)orderMap.get("shipping_type");
			String shipping_no = (String)orderMap.get("shipping_no");
			if(!StringUtil.isNull(shipping_type)&&!StringUtil.isNull(shipping_no) ){
				KdniaoTrackQueryAPI kdniaoTrackQueryAPI = new KdniaoTrackQueryAPI();
				String result = kdniaoTrackQueryAPI.getOrderTracesByJson(shipping_type, shipping_no);
				JSONObject shippingResult = JSONObject.fromObject(result);
				boolean success = (boolean)shippingResult.get("Success");
				if(success){//成功
					orderMap.put("shippingList",shippingResult.getJSONArray("Traces"));
					JSONArray shippingResultArray = shippingResult.getJSONArray("Traces");
					trans_info = shippingResultArray.getJSONObject(shippingResultArray.size()-1);
					
					returnObj.put("result", 1);
					returnObj.put("data", trans_info);
				}else{
					returnObj.put("result", 0);
					returnObj.put("message", "没有查询到物流信息");
				}
			}else{
				returnObj.put("result", 0);
				returnObj.put("message", "没有物流信息");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return returnObj;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public Map getOrderMap() {
		return orderMap;
	}

	public void setOrderMap(Map orderMap) {
		this.orderMap = orderMap;
	}

	public IZxhbOrderManager getZxhbOrderManager() {
		return zxhbOrderManager;
	}

	public void setZxhbOrderManager(IZxhbOrderManager zxhbOrderManager) {
		this.zxhbOrderManager = zxhbOrderManager;
	}
}
