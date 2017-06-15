package com.enation.app.sign.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.sign.model.MemberSign;
import com.enation.app.sign.service.IMemberSignManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonMessageUtil;

import net.sf.json.JSONObject;
/**
 * @Description 用户签到Api
 * @createTime 2016年9月20日 上午10:00:23
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/sign/admin")
@Action("sign")
@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })
public class MemberSignAction extends WWAction {
	
	private IStoreMemberManager storeMemberManager;
	private IMemberSignManager memberSignManager;
	private List<MemberSign> memberSignList;
	private MemberSign memberSign;
	
	/**
	 * @description获取用户当月签到信息 
	 * @date 2016年9月21日 下午4:07:10
	 * @return
	 * @return String
	 */
	public String getMemberSignList(){
		JSONObject data = new JSONObject();
		StoreMember storeMember =storeMemberManager.getStoreMember();
		if(storeMember.getMember_id() != null){
			memberSignList = this.memberSignManager.getMemberSignList(storeMember.getMember_id());
			data.put("memberSignList", memberSignList);
			this.json = JsonMessageUtil.getObjectJson(data);
		}
		return JSON_MESSAGE;
		
	}
	
	/**
	 * @description用户签到 
	 * @date 2016年9月22日 下午3:10:25
	 * @return
	 * @return String
	 */
	public String memberSign(){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String newtime = dateFormat.format(new Date());//当前日期         
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);
		String beforeDay = dateFormat.format(cal.getTime());//前一天
		try {
			StoreMember storeMember =storeMemberManager.getStoreMember();
			this.memberSignManager.sign(newtime,beforeDay,storeMember);
			this.showSuccessJson("签到成功");
		} catch (Exception e) {
			this.showSuccessJson("签到失败");
		}
		return JSON_MESSAGE;
	}

	
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public IMemberSignManager getMemberSignManager() {
		return memberSignManager;
	}

	public void setMemberSignManager(IMemberSignManager memberSignManager) {
		this.memberSignManager = memberSignManager;
	}

	public MemberSign getMemberSign() {
		return memberSign;
	}

	public void setMemberSign(MemberSign memberSign) {
		this.memberSign = memberSign;
	}

	public void setMemberSignList(List<MemberSign> memberSignList) {
		this.memberSignList = memberSignList;
	}
}
