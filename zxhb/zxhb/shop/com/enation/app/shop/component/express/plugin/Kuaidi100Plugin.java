package com.enation.app.shop.component.express.plugin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.express.AbstractExpressPlugin;
import com.enation.app.base.core.plugin.express.IExpressEvent;
import com.enation.eop.processor.core.RemoteRequest;
import com.enation.eop.processor.core.Request;
import com.enation.eop.processor.core.Response;
import com.enation.framework.util.JsonUtil;

/**
 * 快递100插件接口
 * @author xulipeng
 */

@Component
public class Kuaidi100Plugin extends AbstractExpressPlugin implements IExpressEvent {

	@Override
	public 	Map getExpressDetail(String com, String nu, Map params) {
		String keyid = (String) params.get("keyid");
		Map map = new HashMap();
		try {
			Request remoteRequest  = new RemoteRequest();
			String kuaidiurl="http://api.kuaidi100.com/api?id="+keyid+"&nu="+nu+"&com="+com+"&muti=1&order=asc";
			//System.out.println(kuaidiurl);
			Response remoteResponse = remoteRequest.execute(kuaidiurl);
			String content  = remoteResponse.getContent();
			
			map = JsonUtil.toMap(content);
			if(map.get("status").equals("1")){
				map.put("message", "ok");
			}
		} catch (Exception e) {
			map.put("message", "快递100接口出现错误，请稍后重试！");
		}
		
		return map;
	}
	
	@Override
	public String getId() {
		return "kuaidi100Plugin";
	}

	@Override
	public String getName() {
		return "快递100接口插件";
	}

	

}
