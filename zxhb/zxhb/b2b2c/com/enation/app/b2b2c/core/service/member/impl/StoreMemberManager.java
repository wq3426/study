package com.enation.app.b2b2c.core.service.member.impl;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.zxhb.OrderAddress;
import com.enation.app.b2b2c.core.model.zxhb.OrderDetail;
import com.enation.app.b2b2c.core.model.zxhb.OrderUser;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component
public class StoreMemberManager extends BaseSupport implements IStoreMemberManager{
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.member.IStoreMemberManager#edit(com.enation.app.b2b2c.core.model.member.StoreMember)
	 */
	public void edit(StoreMember member) {
		this.baseDaoSupport.update("member", member, "member_id=" + member.getMember_id());
		ThreadContextHolder.getSessionContext().setAttribute(this.CURRENT_STORE_MEMBER_KEY, member);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.member.IStoreMemberManager#getMember(java.lang.Integer)
	 */
	@Override
	public StoreMember getMember(Integer member_id) {
		String sql="select * from es_member where member_id=?";
		return (StoreMember) this.daoSupport.queryForObject(sql, StoreMember.class, member_id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.member.IB2b2cMemberManager#getStoreMember()
	 */
	@Override
	public StoreMember getStoreMember() {
		StoreMember member=(StoreMember) ThreadContextHolder.getSessionContext().getAttribute(this.CURRENT_STORE_MEMBER_KEY);
		return member;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.member.IStoreMemberManager#getMember(java.lang.String)
	 */
	@Override
	public StoreMember getMember(String member_name) {
		String sql="select * from es_member where username=?";
		return (StoreMember) this.daoSupport.queryForObject(sql, StoreMember.class,member_name );
	}
	@Override
	public StoreMember getStoreMemberByStoreId(Integer store_id) {
		String sql = "select *from es_member where store_id = ? and is_store=1";
		return (StoreMember)daoSupport.queryForObject(sql, StoreMember.class, store_id);
	}
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateUserLoginPassword(String user_password, int store_id) {
		String sql = 
				" UPDATE es_member t                       "+
				" SET t.password = '"+user_password+"'     "+
				" WHERE 1=1                                "+
				" AND t.store_id = "+store_id+"            ";
		daoSupport.execute(sql);
		
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject createUserAndOrder(OrderUser user, OrderDetail order_detail) throws RuntimeException, Exception {
		JSONObject obj = new JSONObject();
		try {
			//获取邀请码的联系人
			String referee = "";
			if(!"".equals(order_detail.getReferee_code())){
				String resql = "select *from referee_code where referee_code = ?";
				List<Map<String, String>> refereeList = daoSupport.queryForList(resql, order_detail.getReferee_code());
				if(refereeList!=null && refereeList.size()>0){
					referee = refereeList.get(0).get("referee");
				}
			}
			order_detail.setReferee(referee);
			//创建用户
			String sql = "select user_id from order_user where user_telephone='"+ user.getUser_telephone() +"'";
			List userList = daoSupport.queryForList(sql);
			int user_id = 0;

			if(userList.size() == 0){
				daoSupport.insert("order_user", user);
				user_id = daoSupport.getLastId("order_user");
			}else{
				user_id = JSONObject.fromObject(userList.get(0)).getInt("user_id");
			}
			user.setUser_id(user_id);
			
			//查询商品价格
			sql = "SELECT card_price FROM spec_value_id_level WHERE spec_value_id=? AND parent_spec_value_id=?";
			List priceList = daoSupport.queryForList(sql, order_detail.getTa_spec_value_id(), order_detail.getGg_spec_value_id());
			double unit_price = 0.0;
			double total_price = 0.0;
			if(priceList.size() > 0){
				unit_price = JSONObject.fromObject(priceList.get(0)).getDouble("card_price");
				total_price = unit_price * order_detail.getOrder_count();
			}
			DecimalFormat df = new DecimalFormat("0.00");
			order_detail.setUnit_price(Double.valueOf(df.format(unit_price)));
			order_detail.setTotal_price(Double.valueOf(df.format(total_price)));
			
			//生成预约订单
			order_detail.setUser_id(user_id);
			order_detail.setOrder_sn(createSn());
			daoSupport.insert("order_detail", order_detail);
			int order_id = daoSupport.getLastId("order_detail");
			order_detail.setOrder_id(order_id);
			
			obj.put("result", 1);
			obj.put("message", "预约成功");
			obj.put("mobile", user.getUser_telephone());
			obj.put("order_id", order_id);
			
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		return obj;
	}
	
	@Override
	public JSONObject getCardOrderList(String mobile) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "SELECT user_id, user_telephone FROM order_user WHERE user_telephone=?";
			List user_list = daoSupport.queryForList(sql, mobile);
			if(user_list.size() > 0){
				JSONObject user_obj = JSONObject.fromObject(user_list.get(0));
				int user_id = user_obj.getInt("user_id");
				String user_telephone = user_obj.getString("user_telephone");
				user_telephone = user_telephone.replace(user_telephone.substring(4, 8), "****");
				
				sql = "SELECT *, FROM_UNIXTIME(create_time/1000, '%Y-%m-%d') order_date FROM order_detail WHERE user_id=? ORDER BY create_time DESC";
				List order_list = daoSupport.queryForList(sql, user_id);
				if(order_list.size() > 0){
					JSONArray new_order_array = new JSONArray();
					JSONArray order_array = JSONArray.fromObject(order_list);
					for(int i=0; i<order_array.size(); i++){
						JSONObject order_obj = order_array.getJSONObject(i);
						order_obj.put("user_telephone", user_telephone);
						
						int goods_id = order_obj.getInt("goods_id");
						int gg_spec_value_id = order_obj.getInt("gg_spec_value_id");
						int ta_spec_value_id = order_obj.getInt("ta_spec_value_id");
						
						String goodsSql = "SELECT NAME, intro FROM es_goods WHERE goods_id=?";
						List goods_list = daoSupport.queryForList(goodsSql, goods_id);
						if(goods_list.size() > 0){
							JSONObject goods_obj = JSONObject.fromObject(goods_list.get(0));
							String goods_name = goods_obj.getString("name");
							String intro = goods_obj.getString("intro");
							intro = intro.replace("<p>", "").replace("</p>", "");
							order_obj.put("goods_name", goods_name);
							order_obj.put("intro", intro);
						}
						String gg_spec_sql = "SELECT spec_value FROM es_spec_values WHERE spec_value_id="+gg_spec_value_id;
						String gg_spec_value = daoSupport.queryForString(gg_spec_sql);
						
						order_obj.put("gg_spec_value", gg_spec_value);
						
						String ta_spec_sql = "SELECT spec_value, REPLACE(spec_image, ?, ?) spec_image FROM es_spec_values WHERE spec_value_id="+ta_spec_value_id;
						List ta_spec_list = daoSupport.queryForList(ta_spec_sql, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_domain());
						if(ta_spec_list.size() > 0){
							JSONObject ta_obj = JSONObject.fromObject(ta_spec_list.get(0));
							String ta_spec_value = ta_obj.getString("spec_value");
							String ta_spec_image = ta_obj.getString("spec_image");
							
							order_obj.put("ta_spec_value", ta_spec_value);
							order_obj.put("ta_spec_image", ta_spec_image);
						}
						
						new_order_array.add(order_obj);
					}
					
					obj.put("result", 1);
					obj.put("data", new_order_array);
				}else{
					obj.put("result", 0);
					obj.put("message", "该手机号没有预约过");
				}
			}else{
				obj.put("result", 0);
				obj.put("message", "该手机号没有预约过");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject editZxOrderAddress(String order_id, String address) throws Exception {
		JSONObject obj = new JSONObject();
		try {
			String sql = "UPDATE order_detail SET address=? WHERE order_id=?";
			daoSupport.execute(sql, address, order_id);
			
			obj.put("result", 1);
			obj.put("message", "修改成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void updateOrderStatus(Integer status, String trade_no, String pay_type, String out_trade_no) throws Exception {
		try {
			String sql = "UPDATE order_detail SET STATUS=?, trade_no=?, pay_type=? WHERE order_sn=?";
			daoSupport.execute(sql, status, trade_no, pay_type, out_trade_no);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
	}
	
	@Override
	public JSONObject getCardOrderInfo(String out_trade_no) {
		JSONObject obj = null;
		try {
			String sql = "SELECT t1.status, t2.user_telephone, t1.order_sn, t1.receive_user, t1.total_price FROM order_detail t1, order_user t2 WHERE t1.user_id=t2.user_id AND t1.order_sn=?";
			List dataList = daoSupport.queryForList(sql, out_trade_no);
			if(dataList.size() > 0){
				obj = JSONObject.fromObject(dataList.get(0));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}
	
	public String createSn() {
		boolean isHave = true;  //数据库中是否存在该订单
		String sn = "";			//订单号
		
		//如果存在当前订单
		while(isHave) {
			StringBuffer  snSb = new StringBuffer(DateUtil.getDateline()+"") ;
			snSb.append(getRandom());
			String sql = "SELECT count(order_id) FROM order_detail WHERE order_sn = '" + snSb.toString() + "'";
			int count = daoSupport.queryForInt(sql);
			if(count == 0) {
				sn = snSb.toString();
				isHave = false;
			}
		}
		

		return sn;
	}
	
	/**
	 * 获取随机数
	 * @return
	 */
	public  int getRandom(){
		Random random=new Random();
		int num=Math.abs(random.nextInt())%100;
		if(num<10){
			num=getRandom();
		}
		return num;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void saveWishes(String wisher, String wisher_telephone, String wish_items) throws Exception{
		try {
			String sql = "SELECT wisher_telephone FROM wish_statistics WHERE wisher_telephone=?";
			List list = daoSupport.queryForList(sql, wisher_telephone);
			if(list.size() > 0){
				sql = "UPDATE wish_statistics SET wisher=?, wish_items=? WHERE wisher_telephone=?";
			}else{
				sql = "INSERT INTO wish_statistics SET wisher=?, wish_items=?, wisher_telephone=?";
			}
			
			daoSupport.execute(sql, wisher, wish_items, wisher_telephone);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
	}
	
}

