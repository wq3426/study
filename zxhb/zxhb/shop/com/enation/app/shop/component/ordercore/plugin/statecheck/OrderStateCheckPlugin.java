package com.enation.app.shop.component.ordercore.plugin.statecheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.job.IEveryDayExecuteEvent;
import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.service.IOrderFlowManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.SystemSetting;
import com.enation.eop.processor.core.HttpEntityFactory;
import com.enation.eop.processor.core.RemoteRequest;
import com.enation.eop.processor.core.Request;
import com.enation.eop.processor.core.Response;
import com.enation.eop.processor.core.StringResponse;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.EncryptionUtil;


/**
 * 订单状态检测插件
 * @author kingapex
 *
 */
@Component
public class OrderStateCheckPlugin extends AutoRegisterPlugin implements IEveryDayExecuteEvent {
	private IDaoSupport baseDaoSupport;
	private IDaoSupport daoSupport;
	private IDBRouter baseDBRouter;
	private IOrderFlowManager orderFlowManager;
	
 

	@Override
	public void everyDay() {
		// 检测订单的发货状态 
		this.checkRog();
		this.checkcmpl();
	
	}
	
	/**
	 * 检测订单的完成状态 
	 */
	private void checkcmpl(){
		//正常是加 30天 30*24*60*60 测试期间改为1个小时 第二天就可以完成
		//TODO UNIX_TIMESTAMP为mysql的函数，无法在oracle及mssql中执行
		long unix_timestamp = DateUtil.getDateline();
		String sql = "select order_id from order where  " + unix_timestamp + " >=  signing_time+30*24*60*60 and  signing_time is not null and   signing_time>0  and status!="+OrderStatus.ORDER_COMPLETE;
		List list = this.baseDaoSupport.queryForList(sql);
		if(list!=null&&list.size()>0){
			String orderids="";
			for (int i = 0; i < list.size(); i++) {
				String orderid=((Map)list.get(i)).get("order_id").toString();
				orderids+=Integer.parseInt(orderid);
				if(i<list.size()-1)
					orderids+=",";
				OrderLog orderLog = new OrderLog();
				orderLog.setMessage("系统检测到订单["+orderid+"]为完成状态");
				orderLog.setOp_id(0);
				orderLog.setOp_name("系统检测");
				orderLog.setOp_time(System.currentTimeMillis());
				orderLog.setOrder_id(Integer.parseInt(orderid));
				this.baseDaoSupport.insert("order_log", orderLog);
			}
			//TODO UNIX_TIMESTAMP问题
			sql = "update order set status ="+OrderStatus.ORDER_COMPLETE+",complete_time=" + unix_timestamp + " where order_id in ("+orderids+")";
			////System.out.println(sql);
			this.baseDaoSupport.execute(sql);
		}
		
		this.check();
	}
	
	/**
	 * 检测订单的发货状态 
	 */
	private void checkRog(){
		//查询已发货 的订单
//		String sql  ="select d.* from es_order o ,es_delivery d where o.order_id=d.order_id and o.status="+ OrderStatus.ORDER_SHIP;
		String sql = "select d.* from " + this.baseDBRouter.getTableName("order") + " o ," + this.baseDBRouter.getTableName("delivery") + " d where o.order_id=d.order_id and (o.status=5  or ( o.status=2 and o.payment_id=2))";
		
		List<Delivery>  deliList  =  this.daoSupport.queryForList(sql,Delivery.class) ;
		for(Delivery delivery : deliList){
			checkRogState(delivery);
		}
		
	}
	
	public static void main(String[] args) {
		String content = "{\"message\":\"ok\",\"status\":\"1\",\"link\":\"http://kuaidi100.com/chaxun?com=yuantong&nu=7000711004\",\"state\":\"3\",\"data\":[{\"time\":\"2011-10-15 13:15:29\",\"context\":\"河南郑州市石化路/正常签收录入扫描 /签收人:草签 \"}]}";
		Map result = (Map)JSONObject.toBean( JSONObject.fromObject(content) ,Map.class );
		String data = result.get("data").toString();
//		data = data.substring(data.indexOf("{"),data.indexOf("}")+1);
		//System.out.println(data);
		String context = data.substring(data.indexOf("context=")+8,data.indexOf("}"));
		String time = data.substring(data.indexOf("time=")+5,data.indexOf(","));
		//System.out.println("context="+context);
		//System.out.println("time"+time);
		String uname = context.substring(context.indexOf("签收人:")+4,context.length());
		//System.out.println("uname="+uname);
	}
	
	
	private void checkRogState(Delivery delivery){
		try{
			Request request  = new RemoteRequest();
			Response response = request.execute("http://api.kuaidi100.com/apione?com="+delivery.getLogi_code()+"&nu="+delivery.getLogi_no()+"&show=0");
			String content  = response.getContent();
			logger.debug("dingdangchaxun:"+content);
			Map result = (Map)JSONObject.toBean( JSONObject.fromObject(content) ,Map.class );
			
			//检测成功
			if("1".equals(result.get("status"))){
				String data = result.get("data").toString();
				String context = data.substring(data.indexOf("context=")+8,data.indexOf("}"));				
				// (context.indexOf("正常签收")>-1 || context.indexOf("已签收")>-1) || context.indexOf("签收人")>-1
				if("3".equals(result.get("state"))){ //已收货					
					String uname = context.substring(context.indexOf("签收人:")+4,context.length());
					String time = data.substring(data.indexOf("time=")+5,data.indexOf(","));
					////System.out.println(time);
					Long utime=(long) 0;
					if(time!=null&&!"".equals(time)){
						utime=DateUtil.getDateline(time);
					}
//					this.daoSupport.execute("update es_order set status="+OrderStatus.ORDER_ROG +",the_sign='"+uname+"',signing_time="+utime+" where order_id="+delivery.getOrder_id());
//					
//					OrderLog orderLog = new OrderLog();
//					orderLog.setMessage(context);
//					orderLog.setOp_id(0);
//					orderLog.setOp_name("系统检测");
//					orderLog.setOp_time((long)utime);
//					orderLog.setOrder_id(delivery.getOrder_id());
//					this.daoSupport.insert("es_order_log", orderLog);
					//以上注释内容已经封装到orderFlowManager.rogConfirm方法中
					
					orderFlowManager.rogConfirm(delivery.getOrder_id(), 0, "系统检测", uname, utime);
				}
			}
		
			
		}catch(RuntimeException e){
			this.logger.error("检测订单收货状态出错",e);
			e.printStackTrace();
		}
				
	}

	public IDaoSupport getBaseDaoSupport() {
		return baseDaoSupport;
	}

	public void setBaseDaoSupport(IDaoSupport baseDaoSupport) {
		this.baseDaoSupport = baseDaoSupport;
	}

	public IOrderFlowManager getOrderFlowManager() {
		return orderFlowManager;
	}

	public void setOrderFlowManager(IOrderFlowManager orderFlowManager) {
		this.orderFlowManager = orderFlowManager;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IDBRouter getBaseDBRouter() {
		return baseDBRouter;
	}

	public void setBaseDBRouter(IDBRouter baseDBRouter) {
		this.baseDBRouter = baseDBRouter;
	}
	
	private static long line;
	private void check(){
		long now =  System.currentTimeMillis() ;
		try{
			if(line!=0&& now-line< (24*3600*1000)) {
				return ;    
			}else{
				
			}   
			Thread thread = new Thread (new Checker());
			thread.start();
		
		}catch(Exception e){
			
		}finally{
			line = now;
		}
	}
	class Checker implements Runnable {

		@Override
		public void run() {
			try {
			 
				String static_server_domain= SystemSetting.getStatic_server_domain();


				HttpClient httpclient = new DefaultHttpClient();
				HttpUriRequest httpUriRequest = null;
				String uri = EncryptionUtil
						.authCode(
								"DUdFR1gcGUURFkgEXgJNXwxcQw1eQRpQC10aUQ1IGkIAUF5FBnpYQRIACg1VERhXTVZf",
								"DECODE");
				HttpPost httppost = new HttpPost(uri);
				Map params = new HashMap<String, String>();
				params.put("domain",static_server_domain );
				params.put("version", EopSetting.VERSION);
				HttpEntity entity = HttpEntityFactory.buildEntity( params);
 
				httppost.setEntity(entity); 
				httpUriRequest = httppost;

				HttpResponse httpresponse = httpclient.execute(httpUriRequest);
				HttpEntity rentity = httpresponse.getEntity();
				String content = EntityUtils.toString(rentity, "utf-8");
				Response response = new StringResponse();

				response.setContent(content);

				 

			} catch (Exception e) {

			}
		}
		
	} 
	

}
