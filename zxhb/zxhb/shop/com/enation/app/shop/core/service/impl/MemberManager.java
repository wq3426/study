package com.enation.app.shop.core.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.plugin.member.MemberPluginBundle;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.core.service.OrderType;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.ValidateUtils;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;

/**
 * 会员管理
 * 
 * @author kingapex 2010-4-30上午10:07:24
 */
@SuppressWarnings({ "rawtypes", "unchecked"})
public class MemberManager extends BaseSupport<Member> implements IMemberManager {

	protected IMemberLvManager memberLvManager;
	private IMemberPointManger memberPointManger;
	private MemberPluginBundle memberPluginBundle;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void logout() {
		//this.daoSupport.insert(table, fields);
		//this.daoSupport.update(table, fields, where);
		//this.daoSupport.queryForInt(sql, args)
		
		//this.daoSupport.execute(sql, args);  es_member
		//this.baseDaoSupport				   member
		
		Member member = UserConext.getCurrentMember();
		member = this.get(member.getMember_id());
		ThreadContextHolder.getSessionContext().removeAttribute(UserConext.CURRENT_MEMBER_KEY);
		this.memberPluginBundle.onLogout(member);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int register(Member member) {
		int result = add(member);
		try {
			this.memberPluginBundle.onRegister(member);
			
		} catch (Exception e) {
			System.out.println(e);
		}

		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int add(Member member) {
		if (member == null)
			throw new IllegalArgumentException("member is null");
		if (member.getUsername() == null)
			throw new IllegalArgumentException("member' username is null");
		if (member.getPassword() == null)
			throw new IllegalArgumentException("member' password is null");
//		if (member.getEmail() == null)
//			throw new IllegalArgumentException("member'email is null");

		if (this.checkname(member.getUsername()) == 1) {
			return 0;
		}
		if(member.getLv_id()==null){
			Integer lvid = memberLvManager.getDefaultLv();
			member.setLv_id(lvid);
		}
		
		//如果会员昵称为空，就将会员登陆用户名设置为昵称	修改人:DMRain 2015-12-16
		if(member.getNickname() == null){
			member.setNickname(member.getUsername());
		}
			
		member.setPoint(0);
		member.setAdvance(0D);
		
		if(member.getRegtime()==null){
			member.setRegtime(DateUtil.getDateline());
		}
		
		member.setLastlogin(DateUtil.getDateline());
		member.setLogincount(0);
		member.setPassword(StringUtil.md5(member.getPassword()));

		// Dawei Add
		member.setMp(0);
		member.setFace("");
		member.setMidentity(0);
		member.setSex(1);	//新注册用户性别默认为'男'
 

		this.baseDaoSupport.insert("member", member);
		int memberid = this.baseDaoSupport.getLastId("member");
		member.setMember_id(memberid);

		return 1;
	}

	@Override
	public void checkEmailSuccess(Member member) {
		int memberid = member.getMember_id();
		String sql = "update member set is_cheked = 1 where member_id =  " + memberid;
		this.baseDaoSupport.execute(sql);
		this.memberPluginBundle.onEmailCheck(member);
	}

	@Override
	public Member get(Integer memberId) {
		String sql = "select m.*,l.name as lvname from "
				+ this.getTableName("member") + " m left join "
				+ this.getTableName("member_lv")
				+ " l on m.lv_id = l.lv_id where member_id=?";
		Member m = this.daoSupport.queryForObject(sql, Member.class, memberId);
		return m;
	}

	@Override
	public Member getMemberByUname(String username) {
		String sql = "select * from es_member where username=?";
		List list = this.baseDaoSupport.queryForList(sql, Member.class, username);
		Member m = null;
		if (list != null && list.size() > 0) {
			m = (Member) list.get(0);
		}
		return m;
	}

	@Override
	public Member getMemberByEmail(String email) {
		String sql = "select * from member where email=?";
		List list = this.baseDaoSupport.queryForList(sql, Member.class, email);
		Member m = null;
		if (list != null && list.size() > 0) {
			m = (Member) list.get(0);
		}
		return m;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Member edit(Member member) {
		// 前后台用到的是一个edit方法，请在action处理好
		this.baseDaoSupport.update("member", member, "member_id=" + member.getMember_id());
		Integer memberpoint = member.getPoint();
		
		//改变会员等级
		if(memberpoint!=null ){
			MemberLv lv =  this.memberLvManager.getByPoint(memberpoint);
			if(lv!=null ){
				if((member.getLv_id()==null ||lv.getLv_id().intValue()>member.getLv_id().intValue())){
					this.updateLv(member.getMember_id(), lv.getLv_id());
				} 
			}
		}
		ThreadContextHolder.getSessionContext().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);
		return null;
	}

	@Override
	public int checkname(String name) {
		String sql = "select count(0) from member where username=?";
		int count = this.baseDaoSupport.queryForInt(sql, name);
		count = count > 0 ? 1 : 0;
		return count;
	}

	@Override
	public int checkemail(String email) {
		String sql = "select count(0) from member where email=?";
		int count = this.baseDaoSupport.queryForInt(sql, email);
		count = count > 0 ? 1 : 0;
		return count;
	}
	
	@Override
	public int checkMobile(String mobile) {
		String sql = "select count(0) from es_member where username=?";
		int count = this.daoSupport.queryForInt(sql, mobile);
		count = count > 0 ? 1 : 0;
		return count;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer[] id) {
		if (id == null || id.equals(""))
			return;
		String id_str = StringUtil.arrayToString(id, ",");
		String sql = "delete from member where member_id in (" + id_str + ")";
		this.baseDaoSupport.execute(sql);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePassword(String mobile,String password) {
		Member member = UserConext.getCurrentMember();
		if(member == null ){
			Member member1=this.selectMemberByMobile(mobile);
			this.updatePassword(member1.getMember_id(), password);
		}else{
			this.updatePassword(member.getMember_id(), password);
			member.setPassword(StringUtil.md5(password));
			ThreadContextHolder.getSessionContext().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);
		}
	}
	
	/**
	 * @description 根据手机号码查询会员
	 * @date 2016年10月17日 下午3:45:39
	 * @param mobile
	 * @return Member
	 */
	public Member selectMemberByMobile(String mobile){
		String sql = "select * from es_member where username=?";
		Member member=this.daoSupport.queryForObject(sql, Member.class, mobile);
		return member;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePassword(Integer memberid, String password) {
		String md5password = password == null ? StringUtil.md5("") : StringUtil.md5(password);
		String sql = "update member set password = ? where member_id =? ";
		this.baseDaoSupport.execute(sql, md5password, memberid);
		this.memberPluginBundle.onUpdatePassword(password, memberid);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateFindCode(Integer memberid, String code) {
		String sql = "update member set find_code = ? where member_id =? ";
		this.baseDaoSupport.execute(sql, code, memberid);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int login(String username, String password) {
		String sql1="select COUNT(0) FROM " + this.getTableName("member")+" WHERE username=?";
		int count = this.baseDaoSupport.queryForInt(sql1, username);
		if(count == 0){
			return 2;
		}else{
			String sql = "select m.*,l.name as lvname from "
					+ this.getTableName("member") + " m left join "
					+ this.getTableName("member_lv")
					+ " l on m.lv_id = l.lv_id where m.username=? and password=?";
			// 用户名中包含@，说明是用邮箱登录的
			if (username.contains("@")) {
				sql = "select m.*,l.name as lvname from "
						+ this.getTableName("member") + " m left join "
						+ this.getTableName("member_lv")
						+ " l on m.lv_id = l.lv_id where m.email=? and password=?";
			}
			
			String pwdmd5 = com.enation.framework.util.StringUtil.md5(password);
			List<Member> list = this.daoSupport.queryForList(sql, Member.class, username, pwdmd5);
			if (list == null || list.isEmpty()) {
				return 0;
			}
			
			Member member = list.get(0);
			long ldate = ((long) member.getLastlogin()) * 1000;
			Date date = new Date(ldate);
			Date today = new Date();
			int logincount = member.getLogincount();
			if (DateUtil.toString(date, "yyyy-MM").equals(DateUtil.toString(today, "yyyy-MM"))) {// 与上次登录在同一月内
				logincount++;
			} else {
				logincount = 1;
			}
			Long upLogintime = member.getLastlogin();// 登录积分使用
			member.setLastlogin(DateUtil.getDateline());
			member.setLogincount(logincount);
			this.edit(member);
			ThreadContextHolder.getSessionContext().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);
			this.memberPluginBundle.onLogin(member, upLogintime);
			return 1;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int loginWithCookie(String username, String password) {
		String sql = "select m.*,l.name as lvname from "
				+ this.getTableName("member") + " m left join "
				+ this.getTableName("member_lv")
				+ " l on m.lv_id = l.lv_id where m.username=? and password=?";
		// 用户名中包含@，说明是用邮箱登录的
		if (username.contains("@")) {
			sql = "select m.*,l.name as lvname from "
					+ this.getTableName("member") + " m left join "
					+ this.getTableName("member_lv")
					+ " l on m.lv_id = l.lv_id where m.email=? and password=?";
		}
		List<Member> list = this.daoSupport.queryForList(sql, Member.class,	username, password);
		if (list == null || list.isEmpty()) {
			return 0;
		}

		Member member = list.get(0);
		long ldate = ((long) member.getLastlogin()) * 1000;
		Date date = new Date(ldate);
		Date today = new Date();
		int logincount = member.getLogincount();
		if (DateUtil.toString(date, "yyyy-MM").equals(DateUtil.toString(today, "yyyy-MM"))) {// 与上次登录在同一月内
			logincount++;
		} else {
			logincount = 1;
		}
		Long upLogintime = member.getLastlogin();// 登录积分使用
		member.setLastlogin(DateUtil.getDateline());
		member.setLogincount(logincount);
		this.edit(member);
		ThreadContextHolder.getSessionContext().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);

		this.memberPluginBundle.onLogin(member, upLogintime);

		return 1;
	}

	/**
	 * 系统管理员作为某个会员登录
	 */
	public int loginbysys(String username) {
		 
		if (UserConext.getCurrentAdminUser()==null) {
			throw new RuntimeException("您无权进行此操作，或者您的登录已经超时");
		}

		String sql = "select m.*,l.name as lvname from "
				+ this.getTableName("member") + " m left join "
				+ this.getTableName("member_lv")
				+ " l on m.lv_id = l.lv_id where m.username=?";
		List<Member> list = this.daoSupport.queryForList(sql, Member.class,	username);
		if (list == null || list.isEmpty()) {
			return 0;
		}

		Member member = list.get(0);
		ThreadContextHolder.getSessionContext().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);
//		HttpCacheManager.sessionChange();
		return 1;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addMoney(Integer memberid, Double num) {
		String sql = "update member set advance=advance+? where member_id=?";
		this.baseDaoSupport.execute(sql, num, memberid);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void cutMoney(Integer memberid, Double num) {
		Member member = this.get(memberid);
		if (member.getAdvance() < num) {
			throw new RuntimeException("预存款不足:需要[" + num + "],剩余["
					+ member.getAdvance() + "]");
		}
		String sql = "update member set advance=advance-? where member_id=?";
		this.baseDaoSupport.execute(sql, num, memberid);
	}
	
	@Override
	public Page searchMember(Map memberMap, Integer page, Integer pageSize,String other,String order) {
		String sql = createTemlSql(memberMap);
		sql+=" order by "+other+" "+order;
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}
	
	@Override
	public Page searchMemberNoShop(Map memberMap, Integer page,
			Integer pageSize, String other, String order) {
		String sql = createTemlSqlNoShop(memberMap);
		sql+=" order by "+other+" "+order;
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);
		return webpage;
	}
	
	@Override
	public List<Member> search(Map memberMap) {
		String sql = createTemlSql(memberMap);
		return this.baseDaoSupport.queryForList(sql, Member.class);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateLv(int memberid, int lvid) {
		String sql = "update member set lv_id=? where member_id=?";
		this.baseDaoSupport.execute(sql, lvid, memberid);
	}
	
	/**
	 * @description 
	 * @date 2016年10月17日 下午3:43:55
	 * @param memberMap
	 * @return String
	 */
	private String createTemlSql(Map memberMap){

		Integer stype = (Integer) memberMap.get("stype");
		String keyword = (String) memberMap.get("keyword");
		String username =(String) memberMap.get("username");
		String mobile = (String) memberMap.get("mobile");
		Integer  lv_id = (Integer) memberMap.get("lvId");
		String email = (String) memberMap.get("email");
		String start_time = (String) memberMap.get("start_time");
		String end_time = (String) memberMap.get("end_time");
		Integer sex = (Integer) memberMap.get("sex");
	
		Integer province_id = (Integer) memberMap.get("province_id");
		Integer city_id = (Integer) memberMap.get("city_id");
		Integer region_id = (Integer) memberMap.get("region_id");
		
		String sql = "select m.*,lv.name as lv_name from "
			+ this.getTableName("member") + " m left join "
			+ this.getTableName("member_lv")
			+ " lv on m.lv_id = lv.lv_id where 1=1 ";
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql+=" and (m.username like '%"+keyword+"%'";
				sql+=" or m.mobile like '%"+keyword+"%'";
				sql+=" or m.name like '%"+keyword+"%')";
			}
		}
		
		if(lv_id!=null && lv_id!=0){
			sql+=" and m.lv_id="+lv_id;
		}
		
		if (username != null && !username.equals("")) {
			sql += " and m.name like '%" + username + "%'";
			sql += " or m.username like '%" + username + "%'";
		}
		if(mobile!=null){
			sql += " and m.mobile like '%" + mobile + "%'";
		}
		
		if(email!=null && !StringUtil.isEmpty(email)){
			sql+=" and m.email = '"+email+"'";
		}
		
		if(sex!=null&&sex!=2){
			sql+=" and m.sex = "+sex;
		}
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.getDateline(start_time+" 00:00:00");
			sql+=" and m.regtime>"+stime;
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql+=" and m.regtime<"+etime;
		}
		if(province_id!=null&&province_id!=0){
			sql+=" and province_id="+province_id;
		}
		if(city_id!=null&&city_id!=0){
			sql+=" and city_id="+city_id;
		}
		if(region_id!=null&&region_id!=0){
			sql+=" and region_id="+region_id;
		}
		
		return sql;
	}
	
	/**
	 * @description 
	 * @date 2016年10月17日 下午3:43:35
	 * @param memberMap
	 * @return String
	 */
	private String createTemlSqlNoShop(Map memberMap){
		Integer stype = (Integer) memberMap.get("stype");
		String keyword = (String) memberMap.get("keyword");
		String username =(String) memberMap.get("username");
		String mobile = (String) memberMap.get("mobile");
		Integer  lv_id = (Integer) memberMap.get("lvId");
		String email = (String) memberMap.get("email");
		String start_time = (String) memberMap.get("start_time");
		String end_time = (String) memberMap.get("end_time");
		Integer sex = (Integer) memberMap.get("sex");
	
		Integer province_id = (Integer) memberMap.get("province_id");
		Integer city_id = (Integer) memberMap.get("city_id");
		Integer region_id = (Integer) memberMap.get("region_id");
		
		String sql = "select m.*,lv.name as lv_name from "
			+ this.getTableName("member") + " m left join "
			+ this.getTableName("member_lv")
			+ " lv on m.lv_id = lv.lv_id where 1=1 ";
		  
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql+=" and (m.username like '%"+keyword+"%'";
				sql+=" or m.name like '%"+keyword+"%'";
				sql+=" or m.mobile like '%"+keyword+"%')";
			}
		}
		
		if(lv_id!=null && lv_id!=0){
			sql+=" and m.lv_id="+lv_id;
		}
		
		if (username != null && !username.equals("")) {
			sql += " and m.name like '%" + username + "%'";
			sql += " or m.username like '%" + username + "%'";
		}
		if(mobile!=null){
			sql += " and m.mobile like '%" + mobile + "%'";
		}
		
		if(email!=null && !StringUtil.isEmpty(email)){
			sql+=" and m.email = '"+email+"'";
		}
		
		if(sex!=null&&sex!=2){
			sql+=" and m.sex = "+sex;
		}
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = DateUtil.getDateline(start_time+" 00:00:00");
			sql+=" and m.regtime>"+stime;
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql+=" and m.regtime<"+etime;
		}
		if(province_id!=null&&province_id!=0){
			sql+=" and province_id="+province_id;
		}
		if(city_id!=null&&city_id!=0){
			sql+=" and city_id="+city_id;
		}
		if(region_id!=null&&region_id!=0){
			sql+=" and region_id="+region_id;
		}
		sql+=" and m.member_id NOT IN(select member_id from es_store s)";
		return sql;
	}

	@Override
	public Member getMemberByMobile(String mobile) {
		String sql = "select * from es_member where mobile=?";
		List list = this.baseDaoSupport.queryForList(sql, Member.class, mobile);
		Member m = null;
		if (list != null && list.size() > 0) {
			m = (Member) list.get(0);
		}
		return m;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List updateUserInfo(JSONObject userObj) {
		/**
		 * face（头像），fullname（姓名），nickname（昵称），sex（性别），
			username（手机号），drivingyear（驾龄），birthday（生日），region（地区），
			email（邮箱），idno（身份证号），annualincome（年收入）
		 */
		String username = userObj.getString("username");
		String sql;
		if (username != null){
			sql = "update es_member set ";
			Iterator<String> keys = userObj.keys();
			while(keys.hasNext()) {
				String key = keys.next();
				if(!"username".equals(key) && !StringUtil.isNull(userObj.getString(key))){
					sql += key + "='" + userObj.get(key).toString() + "'";
					if (keys.hasNext())
						sql += ",";
				}
			}
			if(sql.length()-1 == sql.lastIndexOf(",")){
				sql = sql.substring(0, sql.lastIndexOf(","));
			}
			sql += " where username='"+username+"'";
			this.logger.debug("updateUserInfo: " + sql);
			this.baseDaoSupport.execute(sql);
		}
		
		sql = "SELECT * FROM es_member c WHERE username=? ORDER BY c.member_id";
		List returnList = this.daoSupport.queryForList(sql, username);
		return returnList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateUserFace(String username, String imgUrl) {
		String sql = "update es_member set face=? where username=?";
		this.baseDaoSupport.execute(sql, imgUrl, username);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateStatus(Integer[] memberids, Integer status) {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < memberids.length; i++){
			this.daoSupport.execute("update es_member set status = ?,last_belong_store_id = belong_store_id,belong_store_id = 0 where member_id = ?", status,memberids[i]);
			
		} 
	}	 
	
	@Override
	public Page MemberList(Integer page,Integer pageSize,Integer storeid, Map map) {
		String time_list = (String) map.get("time_list");
		String cost = (String) map.get("cost");
		String startTime = (String) map.get("startTime");
		String endTime = (String) map.get("endTime");

		String amountSql = "SELECT b.`carplate`, b.store_id, SUM(b.need_pay_money) amount "
						 + "FROM es_carinfo a, es_order b "
						 + "WHERE a.`carplate`=b.carplate AND b.`parent_id` IS NOT NULL AND b.`pay_status`=2 AND b.`status`=7 "
						 + "GROUP BY b.carplate, b.`store_id`";
		
		String sql = "SELECT t2.`member_id`, t2.fullname, t2.username, t3.brand, t3.series, t3.model, t1.id car_id, UPPER(t1.`carplate`) carplate, FROM_UNIXTIME(t1.`contracttime`/1000,'%Y-%m-%d %H:%i:%s') contracttime, t1.`origin`, t1.`contract_name`, t1.`repair4sstoreid` store_id, IFNULL(t4.amount,0.0) amount "
				   + "FROM es_carinfo t1 "
				   + "INNER JOIN es_member t2 ON t1.`carowner`=t2.`username` AND t2.is_store=0 AND t1.`repair4sstoreid`=?";
		
		if(!StringUtil.isEmpty(startTime)){
			sql += " and UNIX_TIMESTAMP(FROM_UNIXTIME(t1.`contracttime`/1000,'%Y-%m-%d'))*1000 >="+DateUtil.toDate(startTime, "yyyy-MM-dd").getTime();
		}
		if(!StringUtil.isEmpty(endTime)){
			sql += " and UNIX_TIMESTAMP(FROM_UNIXTIME(t1.`contracttime`/1000,'%Y-%m-%d'))*1000 <="+DateUtil.toDate(endTime, "yyyy-MM-dd").getTime();
		}
		sql += " INNER JOIN es_carmodels t3 ON t1.`carmodelid`=t3.`id`"
			 + " LEFT JOIN ("+amountSql+") t4 ON t1.`carplate`=t4.carplate AND t1.`repair4sstoreid`=t4.store_id";
		
		if("0".equals(time_list) || StringUtil.isNull(time_list)){
			sql = "select * from ("+sql+") tb order by tb.contracttime desc";
		}else{
			sql = "select * from ("+sql+") tb order by tb.contracttime";
		}

		Page pageObj = this.daoSupport.queryForPage(sql, page, pageSize ,storeid);

		String sql2 = "SELECT SUM(t4.amount) totalamount "
				    + "FROM es_carinfo t1 "
				    + "INNER JOIN es_member t2 ON t1.`carowner`=t2.`username` AND t2.is_store=0 AND t1.`repair4sstoreid`=? "
				    + "INNER JOIN es_carmodels t3 ON t1.`carmodelid`=t3.`id` "
				    + "LEFT JOIN ("+amountSql+") t4 ON t1.`carplate`=t4.carplate AND t1.`repair4sstoreid`=t4.store_id";

		List list = daoSupport.queryForList(sql2, storeid);
		net.sf.json.JSONObject obj = net.sf.json.JSONObject.fromObject(list.get(0));
		String totalamount = obj.getString("totalamount");
		pageObj.setTotalamount(Double.valueOf(totalamount));
		return pageObj;
	}
	
	@Override
	public Page discontractMemberList(Integer page,Integer pageSize,Integer storeid, Map map) {
		String time_list = (String) map.get("time_list");
		String cost = (String) map.get("cost");
		String startTime = (String) map.get("startTime");
		String endTime = (String) map.get("endTime");
		
		String amountSql = "SELECT b.`carplate`, b.store_id, SUM(b.need_pay_money) amount "
						 + "FROM es_carinfo a, es_order b "
						 + "WHERE a.`carplate`=b.carplate AND b.`parent_id` IS NOT NULL AND b.`pay_status`=2 AND b.`status`=7 "
						 + "GROUP BY b.carplate, b.`store_id`";
		
		String disconcractSql = "SELECT tb2.id, tb2.discontract_store_id, tb2.carplate, tb2.discontract_reason, "
							  + "FROM_UNIXTIME(tb2.`contract_time`/1000,'%Y-%m-%d %H:%i:%s') contract_time, "
							  + "FROM_UNIXTIME(tb2.`discontract_time`/1000,'%Y-%m-%d %H:%i:%s') discontract_time "
							  + "FROM (SELECT carplate, MAX(contract_time) contract_time, MAX(discontract_time) discontract_time "
							  + "FROM es_discontract_record WHERE discontract_store_id = ?";
		if(!StringUtil.isEmpty(startTime)){
			disconcractSql += " AND UNIX_TIMESTAMP(FROM_UNIXTIME(discontract_time/1000,'%Y-%m-%d'))*1000 >="+DateUtil.toDate(startTime, "yyyy-MM-dd").getTime();
		}
		if(!StringUtil.isEmpty(endTime)){
			disconcractSql += " AND UNIX_TIMESTAMP(FROM_UNIXTIME(discontract_time/1000,'%Y-%m-%d'))*1000 <="+DateUtil.toDate(endTime, "yyyy-MM-dd").getTime();
		}
		disconcractSql += " AND carplate NOT IN (SELECT carplate FROM es_carinfo t WHERE t.repair4sstoreid = ?)"
					   + " GROUP BY carplate) tb1 INNER JOIN es_discontract_record tb2 ON tb1.carplate=tb2.carplate"
					   + " AND tb1.contract_time=tb2.contract_time AND tb1.discontract_time=tb2.discontract_time";
		
		String sql = "SELECT t4.`member_id`, t4.fullname, t4.username, t3.brand, t3.`series`, t3.model, UPPER(t1.carplate) carplate, t1.contract_time, t1.discontract_time, IFNULL(t5.amount,0.0) amount, t1.discontract_reason, t1.discontract_store_id "
				   + "FROM ("+disconcractSql+") t1 "
				   + "INNER JOIN es_carinfo t2 ON t1.carplate=t2.`carplate` "
		           + "INNER JOIN es_carmodels t3 ON t2.carmodelid=t3.id "
		           + "INNER JOIN es_member t4 ON t2.`carowner`=t4.username "
		           + "LEFT JOIN ("+amountSql+") t5 ON t1.carplate=t5.carplate AND t1.discontract_store_id=t5.store_id ";
		
		if("0".equals(time_list) || StringUtil.isNull(time_list)){
			sql = "select * from ("+sql+") tb order by tb.discontract_time desc";
		}else{
			sql = "select * from ("+sql+") tb order by tb.discontract_time";
		}

		Page pageObj = this.daoSupport.queryForPage(sql, page, pageSize, storeid, storeid);
		return pageObj;
	}

	@Override
	public Page MemberInsureList(Integer page,Integer pageSize,Integer storeid, Map map) {
		String time_list = (String) map.get("time_list");
		String startTime = (String) map.get("startTime");
		String endTime = (String) map.get("endTime");
		
		String sql = "SELECT t2.`member_id`, t2.fullname, t2.username,t2.sex,t2.address, t3.brand, t3.series, t3.model, t1.id car_id, UPPER(t1.`carplate`) carplate, t1.insureremindcontent, t1.`insure_source`, t1.contract_name, "
				   + "FROM_UNIXTIME(t1.`insurenextbuytime`/1000,'%Y-%m-%d') insurenextbuytime, t1.insureestimatedfee, t1.`repair4sstoreid` store_id "
				   + "FROM es_carinfo t1 "
				   + "INNER JOIN es_member t2 ON t1.`carowner`=t2.`username` AND t2.is_store=0 AND t1.`repair4sstoreid`=? "
				   + "INNER JOIN es_carmodels t3 ON t1.`carmodelid`=t3.`id` ";
		
		if(!StringUtil.isEmpty(startTime)){
			sql += " and t1.insurenextbuytime >="+DateUtil.toDate(startTime, "yyyy-MM-dd").getTime();
		}
		if(!StringUtil.isEmpty(endTime)){
			sql += " and t1.insurenextbuytime <="+DateUtil.toDate(endTime, "yyyy-MM-dd").getTime();
		}

		if("0".equals(time_list) || StringUtil.isNull(time_list)){
			sql = "select * from ("+sql+") tb order by tb.insurenextbuytime";
		}else{
			sql = "select * from ("+sql+") tb order by tb.insurenextbuytime desc";
		}
		
		Page pageObj = this.daoSupport.queryForPage(sql, page, pageSize, storeid);
		pageObj = getStatusFlag(pageObj, 1);
		return pageObj;
	}

	@Override
	public Page MemberRepairList(Integer page,Integer pageSize,Integer storeid, Map map) {
		String time_list = (String) map.get("time_list");
		String contract_status = (String) map.get("contract_status");//0 已预约 1 未预约
		String startTime = (String) map.get("startTime");
		String endTime = (String) map.get("endTime");
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT t2.`member_id`, t2.fullname, t2.username, t3.series, t3.model, t3.brand, t1.id car_id, UPPER(t1.`carplate`) carplate, t1.repair_source, ");
		sql.append("FROM_UNIXTIME(t1.`repairnexttime`/1000,'%Y-%m-%d') repairnexttime, t1.`repairestimatedfee`, t1.`repairtrack` ");
		if("0".equals(contract_status) || StringUtil.isNull(contract_status)){
			sql.append(",t4.order_date, t4.starttime, t4.endtime ");
		}
		sql.append("FROM es_carinfo t1 ");
		sql.append("INNER JOIN es_member t2 ON t1.`carowner`=t2.`username` AND t2.is_store=0 AND t1.`repair4sstoreid`=? ");
		sql.append("INNER JOIN es_carmodels t3 ON t1.`carmodelid`=t3.`id` ");
		if("0".equals(contract_status) || StringUtil.isNull(contract_status)){
			String tb = "SELECT t1.carplate, FROM_UNIXTIME(t1.order_date/1000, '%Y-%m-%d') order_date, t2.starttime, t2.endtime FROM es_repair_order_spec t1, es_repair_timeregion t2 "
					  + "WHERE t1.time_region_id=t2.time_region_id "
					  + "AND t1.order_id IN (SELECT MAX(order_id) order_id FROM es_order WHERE parent_id IS NOT NULL AND order_type = ? AND STATUS IN (?,?,?,?) GROUP BY carplate)";
			sql.append("INNER JOIN ("+ tb +") t4 ON t1.carplate=t4.carplate");
			
			String querySql = sql.toString();
			
			if(!StringUtil.isEmpty(startTime)){
				sql.append(" and t4.order_date >="+DateUtil.toDate(startTime, "yyyy-MM-dd").getTime());
			}
			if(!StringUtil.isEmpty(endTime)){
				sql.append(" and t4.order_date <="+DateUtil.toDate(endTime, "yyyy-MM-dd").getTime());
			}
			
			if("0".equals(time_list) || StringUtil.isNull(time_list)){
				querySql = "select * from ("+querySql+") tb order by tb.order_date desc";
			}else{
				querySql = "select * from ("+querySql+") tb order by tb.order_date";
			}

			Page pageObj = this.daoSupport.queryForPage(querySql.toString(), page, pageSize, storeid, OrderType.REPAIR, OrderStatus.ORDER_NOT_PAY, OrderStatus.ORDER_PAY_CONFIRM, OrderStatus.ORDER_SERVECE, OrderStatus.ORDER_APPRAISE);
			pageObj = getStatusFlag(pageObj, 2);

			return pageObj;
		}
		
		if("1".equals(contract_status) || StringUtil.isNull(contract_status)){
			sql.append("AND t1.carplate NOT IN (SELECT carplate FROM es_order WHERE parent_id IS NOT NULL AND order_type = "+ OrderType.REPAIR +" AND STATUS IN (?,?,?,?) GROUP BY carplate)");
			
			String querySql = sql.toString();
			
			if(!StringUtil.isEmpty(startTime)){
				sql.append(" and t1.repairnexttime >="+DateUtil.toDate(startTime, "yyyy-MM-dd").getTime());
			}
			if(!StringUtil.isEmpty(endTime)){
				sql.append(" and t1.repairnexttime <="+DateUtil.toDate(endTime, "yyyy-MM-dd").getTime());
			}
			
			if("0".equals(time_list) || StringUtil.isNull(time_list)){
				querySql = "select * from ("+sql.toString()+") tb order by tb.repairnexttime desc";
			}else{
				querySql = "select * from ("+sql.toString()+") tb order by tb.repairnexttime";
			}

			Page pageObj = this.daoSupport.queryForPage(querySql.toString(), page, pageSize, storeid, OrderStatus.ORDER_NOT_PAY, OrderStatus.ORDER_PAY_CONFIRM, OrderStatus.ORDER_SERVECE, OrderStatus.ORDER_APPRAISE);
			pageObj = getStatusFlag(pageObj, 2);

			return pageObj;
		}
		return null;
	}

	/**
	 * 获取用户续保状态
	 * @param pageObj
	 * @return Page
	 */
	private Page getStatusFlag(Page pageObj, int type) {
		try {
			JSONArray jsonArray = JSONArray.fromObject(pageObj.getResult());
			String nextbuytime = "";
			for(int i=0; i<jsonArray.size(); i++){
				net.sf.json.JSONObject json = jsonArray.getJSONObject(i);
				if(type == 1){//保险状态
					nextbuytime = json.getString("insurenextbuytime");
				}
				if(type == 2){//保养状态
					nextbuytime = json.getString("repairnexttime");//查询未预约的车辆
					if(json.has("order_date")){//查询已预约的车辆
						nextbuytime = json.getString("order_date");
					}
				}
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Date date = format.parse(nextbuytime);
				Date currtime = format.parse(format.format(new Date()));
				if(currtime.getTime() > date.getTime()){//过保
					json.put("flag", 2);
				}else{
					Calendar ntime = Calendar.getInstance();
					ntime.setTime(date);
					ntime.set(Calendar.DATE, -30);
					if(currtime.getTime() >= ntime.getTimeInMillis()){//30天内到期
						json.put("flag", 1);
					}else{//未到期
						json.put("flag", 0);
					}
				}
			}
			pageObj.setResult(JSONArray.toList(jsonArray));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pageObj;
	}

	@Override
	public net.sf.json.JSONObject getUserContractInfo(Integer store_id) {
		net.sf.json.JSONObject obj = new net.sf.json.JSONObject();
		try {
			//签约用户总数
			String sql = "select count(*) from es_carinfo where repair4sstoreid=?";
			int carTotalCount = daoSupport.queryForInt(sql, store_id);
			obj.put("carTotalCount", carTotalCount);
			
			//本周签约用户
			sql = "select count(*) from es_carinfo where repair4sstoreid=? and contracttime between ? and ?";
			int contractCurrentWeek = daoSupport.queryForInt(sql, store_id, DateUtil.getCurrentWeekTime()[0], DateUtil.getCurrentWeekTime()[1]);
			obj.put("contractCurrentWeek", contractCurrentWeek);
			
			//本周解约用户
			sql = "select count(distinct(carplate)) from es_discontract_record where discontract_store_id=? "
				+ "and carplate not in (select carplate from es_carinfo where repair4sstoreid=?) "
				+ "and discontract_time between ? and ?";
			int discontractCurrentWeek = daoSupport.queryForInt(sql, store_id, store_id, DateUtil.getCurrentWeekTime()[0], DateUtil.getCurrentWeekTime()[1]);
			obj.put("discontractCurrentWeek", discontractCurrentWeek);
			
			//用户消费率
			sql = "SELECT count(carplate) FROM es_carinfo WHERE repair4sstoreid=? AND carplate IN (SELECT DISTINCT(carplate) FROM es_order)";
			int carConsumeCount = daoSupport.queryForInt(sql, store_id);
			String consumeRatio = "";
			if(carTotalCount > 0 && carConsumeCount > 0){
				consumeRatio = Math.round(((double)carConsumeCount / (double)carTotalCount)*100) + "%";
			}else{
				consumeRatio = "0%";
			}
			obj.put("consumeRatio", consumeRatio);
			
			//用户活跃率
			Calendar carlendar = Calendar.getInstance();
			carlendar.add(Calendar.WEEK_OF_YEAR, -2);
			sql = "select count(*) from es_member where is_store=0 and username in (select distinct(carowner) from es_carinfo where repair4sstoreid=?)";
//			sql = "select count(*) from es_carinfo where repair4sstoreid=?";
			int userCount = daoSupport.queryForInt(sql, store_id);
			sql = "select count(*) from es_member where is_store=0 and username in (select distinct(carowner) from es_carinfo where repair4sstoreid=?) and lastlogin*1000 between ? and ?";
//			sql = "select count(distinct(carplate)) from es_hodometer where carplate in (select carplate from es_carinfo where repair4sstoreid=?) and starttime between ? and ?";
			int activeUserCount = daoSupport.queryForInt(sql, store_id, carlendar.getTimeInMillis(), new Date().getTime());
			String activeRatio = "";
			if(userCount > 0 && activeUserCount > 0){
				activeRatio = Math.round(((double)activeUserCount / (double)userCount)*100) + "%";
			}else{
				activeRatio = "0%";
			}
			obj.put("activeRatio", activeRatio);
			
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Page MemberListBreak(Integer page,Integer pageSize,Integer storeid) {
		return this.daoSupport.queryForPage("select * from es_member  where last_belong_store_id = ? ", page,pageSize,storeid);
	}	 

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void maintain(double price, long time, String detail, int distance,int member_id) {
		String sql = "update es_member set consume=consume+?,detail=?,lastdate=?,distance=? where member_id = ?";
		this.daoSupport.execute(sql, price,detail,time,distance,member_id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void insurance(String insuranceName, double price, Long outtime,int member_id) {  
		String sql = "update es_member set consume=consume+?,insuranceprice=?,insurance=?,outtime =? where member_id = ?";
		this.daoSupport.execute(sql, price, price,insuranceName,outtime,member_id);
	}
	
	@Override
	public List<Member> getRepairMemberByStoreId(Map map) {
		Integer storeId=Integer.valueOf(map.get("storeId").toString());
		String userName=String.valueOf(map.get("userName"));
		
		StringBuffer sql=new StringBuffer("SELECT em.*,FROM_UNIXTIME(em.regtime,'%Y-%m-%d') regitTime FROM es_carinfo ec INNER JOIN es_member em ON ec.`carowner`=em.`username` AND em.is_store=0 AND ec.`repair4sstoreid`=?");
		
		if(!StringUtil.isEmpty(userName)&&!StringUtil.equals(userName, "null")) {
			sql.append(" and (em.username like '%"+userName+"%')");
		}
		List<Member> memberList = this.daoSupport.queryForList(sql.toString(), Member.class, storeId);
		return memberList;
	}
	
	@Override
	public String queryMemberPasswordByUsername(String username) throws Exception {
		String sql =
			" SELECT                               "+
			" 	t.password                         "+
			" FROM                                 "+
			" 	es_member t                        "+
			" WHERE 1=1                            "+
			" AND t.username = '"+username+"'      ";
			                                       
		Map<String,String> passwordMap = daoSupport.queryForMap(sql);
		if(ValidateUtils.isEmpty(passwordMap)) {
			throw new RuntimeException("数据异常");
		}
		return passwordMap.get("password");
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int updateMemberPassword(String newPassword, String username) throws Exception {
		String sql = 
				" UPDATE es_member t            				"+
				" SET t.password = '"+newPassword+"'         	"+
				" WHERE 1=1                     				"+
				" AND t.username = '"+username+"'          	    "; 
		daoSupport.execute(sql);                
		return 1;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateRankingFlag(String userName, String rankingFlag) throws Exception {
		String sql = 
				" UPDATE es_member t            				"+
				" SET t.rankingflag = '"+rankingFlag+"'         "+
				" WHERE 1=1                     				"+
				" AND t.username = '"+userName+"'          	    "; 
		daoSupport.execute(sql);                
	}

	@Override
	public boolean checkOldGestureLockIsCorrect(String userName, String oldGestureLock) {
		String sql = 
			" SELECT                                             "+
			" 	t.member_id                                      "+
			" FROM                                               "+
			" 	es_member t                                      "+
			" WHERE                                              "+
			" 	1 = 1                                            "+
			" AND t.username = '"+userName+"'                    "+
			" AND t.gesturelock = '"+oldGestureLock+"'           ";
		List<Map> resultList = daoSupport.queryForList(sql);
		if(ValidateUtils.isEmpty(resultList)) {
			return false;
		}
		return true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateGestureLock(String userName, String newGestureLock) throws Exception{
		String sql = 
				" UPDATE es_member t            				"+
				" SET t.gesturelock = '"+newGestureLock+"'      "+
				" WHERE 1=1                     				"+
				" AND t.username = '"+userName+"'          	    "; 
		daoSupport.execute(sql);                
	}
	
	@Override
	public Page queryMemberInfoByPage(int pageNum, int pageSize, Map<String, String> conditions) {
		String searchType = conditions.get("searchType");//查询类型
		
		//拼装sql
		StringBuffer sql = new StringBuffer();
		sql.append(
			" SELECT                             "+
			" 	t.member_id,                     "+
			" 	t.username,                      "+
			" 	t.email,                         "+
			" 	t.regtime,                       "+
			" 	t.lastlogin,                     "+
			" 	t.logincount,                    "+
			" 	t.nickname                     	 "+
			" FROM es_member t                   "+
			" WHERE 1=1                          "+
			" AND t.is_store = 0                 ");
		
		if(!StringUtil.isEmpty(searchType)) {
			//简单条件查询
			if(searchType.equals("0")) {
				String keyword = (String) conditions.get("searchKeyword");
				if(!StringUtils.isEmpty(keyword)) {
					keyword = keyword.trim();
					sql.append(" AND ( t.username like '%" + keyword + "%'       "+
							   		" OR t.email like '%" + keyword + "%'        "+
							   		" OR t.nickname like '%" + keyword + "%' )   ");
				}
			}
			
			//复杂条件查询
			if(searchType.equals("1")) {
				//获取复杂查询条件
				String username = conditions.get("username");
				String nickname =  conditions.get("nickname");
				String email = conditions.get("email");
				String startTime = conditions.get("start_time");
				String endTime = conditions.get("end_time");
				String start_time_login = conditions.get("start_time_login");
				String end_time_login = conditions.get("end_time_login");
				
				if(!StringUtil.isEmpty(username)) {
					sql.append(" AND t.username like '%" + username.trim() + "%'");
				}
				if(!StringUtil.isEmpty(nickname)) {
					sql.append(" AND t.nickname like '%" + nickname.trim() + "%'");
				}
				if(!StringUtil.isEmpty(email)) {
					sql.append(" AND t.email like '%" + email.trim() + "%'");
				}
				if(!StringUtil.isEmpty(startTime)) {
					sql.append(" AND t.regtime >="+DateUtil.getDateline(startTime.trim()));
				}
				if(!StringUtil.isEmpty(endTime)) {
					endTime = endTime.trim() + " 23:59:59";
					sql.append(" AND t.regtime <="+DateUtil.getDateline(endTime, "yyyy-MM-dd HH:mm:ss"));
				}
				if(!StringUtil.isEmpty(start_time_login)) {
					sql.append(" AND t.lastlogin >="+DateUtil.getDateline(start_time_login.trim()));
				}
				if(!StringUtil.isEmpty(end_time_login)) {
					endTime = endTime.trim() + " 23:59:59";
					sql.append(" AND t.lastlogin <="+DateUtil.getDateline(end_time_login, "yyyy-MM-dd HH:mm:ss"));
				}
			}
		}	
		sql.append (" ORDER BY t.regtime DESC");
		Page memberInfoPage = this.daoSupport.queryForPage(sql.toString(),pageNum, pageSize);
		return memberInfoPage;
	}
	
	@Override
	public List<Map> queryMemberCarInfo(int memberId) {
		String sql =                                                                           
			" SELECT                                                                           "+
			" 	UPPER(t1.carplate) as carplate,                                                "+
			" 	IFNULL(t1.totalgain,0.00) as totalgain,                                        "+
			" 	t3.brand,                                                                      "+
			" 	t3.brand_id,                                                                   "+
			" 	t3.series,                                                                     "+
			" 	t3.nk,                                                                         "+
			" 	t1.carengineno,                                                                "+
			" 	t1.carvin,                                                                     "+
			" 	t3.seats,                                                                      "+
			" 	t3.price,                                                                      "+
			" 	t3.discharge,                                                                  "+
			" 	FROM_UNIXTIME(t1.create_time/1000,'%Y-%m-%d %h:%i:%s') as create_time,         "+
			" 	t1.caroiltype,                                                                 "+
			" 	t4.type_name,                                                                  "+
			" 	t1.caravgyearmile,                                                             "+
			" 	t1.cardriveregion                                                              "+
			" FROM                                                                             "+
			" 	es_carinfo t1 LEFT JOIN es_car_usetype t4 ON t1.car_use_type = t4.type_value,  "+
			" 	es_member t2,                                                                  "+
			" 	es_carmodels t3                                                                "+
			" WHERE 1=1                                                                        "+
			" AND t1.carowner = t2.username                                                    "+
			" AND t1.carmodelid = t3.id                                                        "+
			" AND t2.member_id = "+memberId+"                                                  "+
			" ORDER BY t1.create_time DESC                                                     ";
		List<Map> queryForList = daoSupport.queryForList(sql);
		return queryForList;
	}	
	
	@Override
	public List<Map> queryCarUseType() {
		String sql = "select * from es_car_usetype where 1=1 "; 
		List<Map> carUseTypeList = daoSupport.queryForList(sql);
		return carUseTypeList;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCarInfoAndCarModel(int carinfoid, int carmodelid, int brand_id, Map<String, String> params) throws Exception {
		//处理参数
		String carplate = params.get("carplate");
		String carvin = params.get("carvin");
		String carengineno = params.get("carengineno");
		String car_use_type_String = params.get("car_use_type_String");

		StringBuilder sql = new StringBuilder();
		sql.append(
				" UPDATE es_carinfo t              				"+
				" SET                              				"+
				" 	t.carplate = '"+carplate+"',               	"+
				" 	t.carvin = '"+carvin+"',                 	"+
				" 	t.carengineno = '"+carengineno+"'           ");
		
		if(!StringUtils.isEmpty(car_use_type_String)) {
			int car_use_type = Integer.parseInt(car_use_type_String);
			sql.append(" ,t.car_use_type = "+car_use_type+" ");
		}
		sql.append(" WHERE 1=1 AND t.id = "+carinfoid+"");
		daoSupport.execute(sql.toString());
		
		//处理参数
		String brand = params.get("brand");
		String series = params.get("series");
		String nk = params.get("nk");
		
		String sql_model=
			" UPDATE es_carmodels t                 "+
			" SET                                   "+
			" 	t.brand ='"+brand+"',               "+
			" 	t.brand_id = "+brand_id+",          "+
			" 	t.series = '"+series+"',            "+
			" 	t.nk = '"+nk+"'                     "+
			" WHERE 1=1                             "+
			" AND t.id = "+carmodelid+"             ";
		daoSupport.execute(sql_model);
	}
	
	@Override
	public String queryBrandName(int brand_id) {
		String sql = 
			" SELECT                              "+
			" 	t.name                            "+
			" FROM                                "+
			" 	es_brand t                        "+
			" WHERE 1=1                           "+
			" AND t.brand_id = "+brand_id+"       ";
		String brand = daoSupport.queryForString(sql);
		return brand;
	}
	
	@Override
	public List<Map> queryBrandList() {
		String sql = "SELECT * FROM es_brand t WHERE 1=1 AND t.type = 0 and t.disabled=0";
		List<Map> brandList = daoSupport.queryForList(sql);
		return brandList;
	}
	
	@Override
	public Page queryCarIncomeDetail(int pageNum, int pageSize, Map<String, String> conditions) {
		String carplate = conditions.get("carplate");
		String startTime = conditions.get("start_time");
		String endTime = conditions.get("end_time");
		
		StringBuilder sql = new StringBuilder();
		sql.append(
			" SELECT                                           "+
			" 	t.id,                                          "+
			" 	t.reward,                                      "+
			" 	CASE t.type                                    "+
			" 		WHEN 1 THEN '驾驶收益'                      "+
			" 		WHEN 2 THEN '订单支付使用收益'               "+
			" 		WHEN 3 THEN '保险订单支付后获得的保养币'      "+     
			" 		WHEN 4 THEN '订单支付使用保养币'             "+
			" 	END AS type,                                   "+
			" 	t.timeline,                                    "+
			" 	t.detail                                       "+
			" FROM                                             "+
			" 	es_car_gain_history t                          "+
			" WHERE                                            "+
			" 	t.carplate = '"+carplate+"'                    ");
		
		if(!StringUtil.isEmpty(startTime)) {
			sql.append(" AND t.timeline >="+DateUtil.getDateline(startTime.trim())*1000);
		}
		if(!StringUtil.isEmpty(endTime)) {
			endTime = endTime.trim() + " 23:59:59";
			sql.append(" AND t.timeline <="+DateUtil.getDateline(endTime, "yyyy-MM-dd HH:mm:ss")*1000);
		}
			
		sql.append (" ORDER BY t.timeline DESC");
		Page incomeInfoPage = this.daoSupport.queryForPage(sql.toString(),pageNum, pageSize);
		return incomeInfoPage;
	}

	@Override
	public Page queryCarBonusDetail(int pageNum, int pageSize, Map<String, String> conditions) {
		String carplate = conditions.get("carplate");
		String startTime = conditions.get("start_time");
		String endTime = conditions.get("end_time");
		
		StringBuilder sql = new StringBuilder();
		sql.append(                                                      
			" SELECT                                                            "+
			" 	t10.*                                                           "+
			" FROM                                                              "+
			" 	(SELECT                                                         "+
			" 		t6.type_id                                                  "+
			" 	FROM                                                            "+
			" 		(SELECT                                                     "+
			" 			t2.repair4sstoreid                                      "+
			" 		FROM                                                        "+
			" 			es_carinfo t2                                           "+
			" 		WHERE 1=1                                                   "+
			" 		AND t2.carplate='"+carplate+"') t4,                         "+
			" 		es_bonus_type t6                                            "+
			" 	WHERE 1=1                                                       "+
			" 	AND t4.repair4sstoreid = t6.store_id) t8,                       "+
			" 	(SELECT                                                         "+
			" 		t7.bonus_type_id                                            "+
			" 	FROM                                                            "+
			" 		(SELECT                                                     "+
			" 			t3.member_id                                            "+
			" 		FROM                                                        "+
			" 			es_carinfo t1,                                          "+
			" 			es_member t3                                            "+
			" 		WHERE 1=1                                                   "+
			" 		AND t1.carowner = t3.username                               "+
			" 		AND t1.carplate='"+carplate+"') t5,                         "+
			" 		es_member_bonus t7                                          "+
			" 	WHERE 1=1                                                       "+
			" 	AND t5.member_id = t7.member_id) t9,                            "+
			" 	es_bonus_type t10                                               "+
			" WHERE 1=1                                                         "+
			" AND t8.type_id = t9.bonus_type_id                                 "+
			" AND t10.type_id = t8.type_id 										");
		
		if(!StringUtil.isEmpty(startTime)) {
			sql.append(" AND t10.use_start_date >="+DateUtil.getDateline(startTime.trim())*1000);
		}
		if(!StringUtil.isEmpty(endTime)) {
			endTime = endTime.trim() + " 23:59:59";
			sql.append(" AND t10.use_end_date <="+DateUtil.getDateline(endTime, "yyyy-MM-dd HH:mm:ss")*1000);
		}
		
		sql.append (" ORDER BY t10.use_start_date ");
		Page incomeInfoPage = this.daoSupport.queryForPage(sql.toString(),pageNum, pageSize);
		return incomeInfoPage;
	}

	@Override
	public Map queryCarInfoByCarplate(String carplate) {
		String sql = 
			" SELECT                           "+
			" 	t1.id as carinfoid ,           "+
			" 	t2.id as carmodelid,           "+
			" 	UPPER(t1.carplate) AS carplate,"+
			" 	t2.brand,                      "+
			" 	t2.brand_id,                   "+
			" 	t2.series,                     "+
			" 	t2.nk,                         "+
			" 	t1.carengineno,                "+
			" 	t1.carvin,                     "+
			" 	t1.car_use_type                "+
			" FROM                             "+
			" 	es_carinfo t1,                 "+
			" 	es_carmodels t2                "+
			" WHERE 1=1                        "+
			" AND t1.carmodelid=t2.id          "+
			" AND t1.carplate = '"+carplate+"' ";
		Map carInforMap = daoSupport.queryForMap(sql);
		return carInforMap;
	}
	
	@Override
	public int queryBonusCount(String carplate) {
		String sql = 
			" SELECT                                                      "+
			" 	COUNT(*) AS bonusCount                                    "+
			" FROM                                                        "+
			" 	(SELECT                                                   "+
			" 		t7.bonus_type_id                                      "+
			" 	FROM                                                      "+
			" 		(SELECT                                               "+
			" 			t3.member_id                                      "+
			" 		FROM                                                  "+
			" 			es_carinfo t1,                                    "+
			" 			es_member t3                                      "+
			" 		WHERE 1=1                                             "+
			" 		AND t1.carowner = t3.username                         "+
			" 		AND t1.carplate='"+carplate+"') t5,                   "+
			" 		es_member_bonus t7                                    "+
			" 	WHERE 1=1                                                 "+
			" 	AND t5.member_id = t7.member_id) t9,                      "+
			" 	(SELECT                                                   "+
			" 			t6.type_id                                        "+
			" 	FROM                                                      "+
			" 		(select                                               "+
			" 			t2.repair4sstoreid                                "+
			" 		FROM                                                  "+
			" 			es_carinfo t2                                     "+
			" 		WHERE 1=1                                             "+
			" 		AND t2.carplate='"+carplate+"') t4,                   "+
			" 		es_bonus_type t6                                      "+
			" 	WHERE 1=1                                                 "+
			" 	AND t4.repair4sstoreid = t6.store_id) t10                 "+
			" WHERE 1=1                                                   "+
			" AND t9.bonus_type_id = t10.type_id                          ";
		int bonusCount = daoSupport.queryForInt(sql);
		return bonusCount;
	}
	
	@Override
	public List<String> queryCarSeriesOrNKList(String key, Object... args) {
		String sql = "";
		List resultList = new ArrayList<>();

		if("brand_id".equals(key)){
			sql = "select distinct(series) from es_carmodels where brand_id=? ";
			resultList = daoSupport.queryForList(sql, args[0]);
		}	
		if("series".equals(key)){
			sql = "select distinct(nk) from es_carmodels where brand_id=? and series=?";
			resultList = daoSupport.queryForList(sql, args[0], args[1]);
		}
		return resultList;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * ============================================================================================
	 * SETTER AND GETTER
	 */
	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

	public MemberPluginBundle getMemberPluginBundle() {
		return memberPluginBundle;
	}

	public void setMemberPluginBundle(MemberPluginBundle memberPluginBundle) {
		this.memberPluginBundle = memberPluginBundle;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}

	

	

	

	
	

	

	

	

	

	

	
	

	

	

	

}
