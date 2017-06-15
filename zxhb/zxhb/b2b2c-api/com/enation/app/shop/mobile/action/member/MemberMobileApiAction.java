/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：会员api
 * 修改人：Sylow  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.member;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.eop.processor.MobileContent;
import com.enation.eop.processor.MobileMessageHttpSend;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.eop.sdk.utils.ValidCodeServlet;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.HttpUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;

/**
 * 会员api
 * @author Sylow
 * @version v1.0 , 2015-08-24
 * @since v1.0
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("member")
public class MemberMobileApiAction extends WWAction {

    private IMemberManager memberManager;

    private String username;
    private String password;
    private String validcode;

    /**
     * 更改密码
     */
    private String oldpassword;
    private String re_passwd;
    
    private String mobile;//手机号码
    
    private String mobileTwo;//验证手机号码
    private String mobileCodeTwo;//验证短信验证码
    
    private String passWord;//重置密码
    private String passWordSub;//重置密码确认
    /**
     * 会员登录
     *
     * @return json字串
     * result  为1表示登录成功，0表示失败 ，int型
     * message 为提示信息 ，String型
     */
    public String login() {
    	try {
			String s = getBody(getRequest());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	try {
//    		if (this.validcode(validcode,"memberlogin") == 1) {
    	        if (memberManager.login(username, password) == 0) {
    	            this.showErrorJson("账号密码错误");
    	            return WWAction.JSON_MESSAGE;
    	        }else if(memberManager.login(username, password) == 2){
    	        	 this.showErrorJson("该用户尚未注册");
     	            return WWAction.JSON_MESSAGE;
    	        }
    	        String cookieValue = EncryptionUtil1.authcode(
    	                "{username:\"" + username + "\",password:\"" + StringUtil.md5(password) + "\"}",
    	                "ENCODE", "", 0);
    	        HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser", cookieValue, 60 * 60 * 24 * 30);
    	
    	        Member member =UserConext.getCurrentMember();
    	
    	        HashMap<String, Object> map = new HashMap<String, Object>();
    	        map.put("username", member.getUsername());
    	        map.put("face", com.enation.eop.sdk.utils.UploadUtil.replacePath(member.getFace()));
    	        map.put("level", member.getLvname());
    	        map.put("idno", member.getIdno());
    	        map.put("fullname", member.getFullname());
    	        map.put("nickname", member.getNickname());
    	        map.put("region", member.getRegion());
    	        map.put("annualincome", member.getAnnualincome());
    	        map.put("drivingyears", member.getDrivingyears());
    	        map.put("email", member.getEmail());
    	        map.put("gesturelock", member.getGesturelock());
    	        map.put("rankingflag", member.getRankingflag());
    	        this.json = JsonMessageUtil.getObjectJson(map);
    	} catch(RuntimeException e) {
    		e.printStackTrace();
    		this.logger.error("登录出错", e);
			this.showErrorJson("登录出错[" + e.getMessage() + "]");
    	}
    	
        return WWAction.JSON_MESSAGE;
    }

    /**
     * 是否已登录
     * @return
     */
    public String isLogin(){
        Member member = UserConext.getCurrentMember();

        if (member == null) {
            this.showErrorJson("尚未登录");
        }else {
            //this.showSuccessJson("已经登录");
        	this.json = JsonMessageUtil.getObjectJson(member);
        }
        return WWAction.JSON_MESSAGE;
    }

    /**
     * 注销会员登录
     *
     * @return json字串
     * result  为1表示注销成功，0表示失败 ，int型
     * message 为提示信息 ，String型
     */
    public String logout() {
		try {
			this.memberManager.logout();
			//设置cookie有效时间为0 即删除
			HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser", "", 0);
			this.showSuccessJson("注销成功");
		} catch (RuntimeException e) {
			this.logger.error("退出登录出错", e);
			this.showErrorJson("退出出错[" + e.getMessage() + "]");
		}
		return WWAction.JSON_MESSAGE;
    }

    /**
     * 修改会员密码
     *
     * @return json字串
     * result  为1表示修改成功，0表示失败 ，int型
     * message 为提示信息 ，String型
     */
    public String changePassword() {
        Member member = UserConext.getCurrentMember();
        try {
        	if (member == null) {//未登录状态修改密码
        		int result = this.memberManager.checkMobile(mobile);
        		if(result != 1){
        			 this.showErrorJson("修改失败！该手机号码不存在");
        	         return WWAction.JSON_MESSAGE;
        		}else{
        			String mobileCode=(String) ThreadContextHolder.getSessionContext().getAttribute("mobileCode");
        			String mobile=(String) ThreadContextHolder.getSessionContext().getAttribute("mobile");
        			if(mobile.equals(mobileTwo) && mobileCode.equals(mobileCodeTwo) && passWord.equals(passWordSub)){
        				memberManager.updatePassword(mobile,passWord);
        				//设置cookie有效时间为0 即删除
        				HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser", "", 0);
        	            this.showSuccessJson("修改密码成功");
        	            return WWAction.JSON_MESSAGE;
        			}else{
        				this.showErrorJson("修改密码失败");
        			}
        		}
        	}else{//已登录状态修改密码
        		oldpassword = oldpassword == null ? "" : StringUtil.md5(oldpassword);
        		
        		String mobileCode=(String) ThreadContextHolder.getSessionContext().getAttribute("mobileCode");
        		String mobile=(String) ThreadContextHolder.getSessionContext().getAttribute("mobile");
        		
    		    if (!oldpassword.equals(member.getPassword())) {
    	            this.showErrorJson("修改失败！原始密码不符");
    	            return WWAction.JSON_MESSAGE;
    	        }
    			if(!mobile.equals(mobileTwo)){
    				this.showErrorJson("修改失败！手机号码错误");
     	            return WWAction.JSON_MESSAGE;
    			}
    			if(!mobileCode.equals(mobileCodeTwo)){
    				this.showErrorJson("修改失败！验证码 错误");
     	            return WWAction.JSON_MESSAGE;
    			}
    		    if (!passWord.equals(passWordSub)) {
    	            this.showErrorJson("修改失败！两次输入的密码不一致");
    	            return WWAction.JSON_MESSAGE;
    	        }
    		    memberManager.updatePassword(mobile,passWord);
    		   //注销
                this.memberManager.logout();
    			//设置cookie有效时间为0 即删除
    			HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser", "", 0);
                this.showSuccessJson("修改密码成功");
                return WWAction.JSON_MESSAGE;
    		    
        	}
		} catch (Exception e) {
			this.showErrorJson("修改密码错误");
		}
        return WWAction.JSON_MESSAGE;
    }

    /**
     * 会员注册
     */
    public String register() {
    	try {
//    		if (this.validcode(validcode, "memberreg") == 1) {
    			String idno, nickname, fullname;
				Member member = new Member();
				HttpServletRequest request = ThreadContextHolder.getHttpRequest();
				String registerip = request.getRemoteAddr();
				idno = request.getParameter("idno");
				nickname = request.getParameter("nickname");
				fullname = request.getParameter("fullname");

				if (StringUtil.isEmpty(username)) {
					this.showErrorJson("用户名不能为空！");
					return WWAction.JSON_MESSAGE;
				}
//				if (StringUtil.isEmpty(idno)) {
//					this.showErrorJson("身份证不能为空！");
//					return WWAction.JSON_MESSAGE;
//				}
//				if (StringUtil.isEmpty(nickname)) {
//					this.showErrorJson("昵称不能为空！");
//					return WWAction.JSON_MESSAGE;
//				}
//				if (StringUtil.isEmpty(fullname)) {
//					this.showErrorJson("用户真实姓名不能为空！");
//					return WWAction.JSON_MESSAGE;
//				}
				if (username.length() < 4 || username.length() > 20) {
					this.showErrorJson("用户名的长度为4-20个字符！");
					return WWAction.JSON_MESSAGE;
				}
				if (username.contains("@")) {
					this.showErrorJson("用户名中不能包含@等特殊字符！");
					return WWAction.JSON_MESSAGE;
				}
				if (StringUtil.isEmpty(password)) {
					this.showErrorJson("密码不能为空！");
					return WWAction.JSON_MESSAGE;
				}
				if (memberManager.checkname(username) > 0) {
					this.showErrorJson("此用户名已经存在，请您选择另外的用户名!");
					return WWAction.JSON_MESSAGE;
				}

				member.setMobile("");
				member.setUsername(username);
				member.setPassword(password);
				member.setEmail("");
				member.setRegisterip(registerip);
				member.setIdno(idno);
				member.setNickname(nickname);
				member.setFullname(fullname);
				
				if (memberManager.register(member) != 1) {
					this.showErrorJson("用户名[" + member.getUsername() + "]已存在!");
					return WWAction.JSON_MESSAGE;
				}

				this.memberManager.login(username, password);
				String cookieValue = EncryptionUtil1.authcode("{username:\""
						+ username + "\",password:\"" + StringUtil.md5(password)
						+ "\"}", "ENCODE", "", 0);
				HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(),
						"JavaShopUser", cookieValue, 60 * 24 * 14);
				//this.showSuccessJson("注册成功");
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("username", member.getUsername());
    	        map.put("idno", member.getIdno());
    	        map.put("fullname", member.getFullname());
    	        map.put("nickname", member.getNickname());
				this.json = JsonMessageUtil.getObjectJson(map);
//    		} else {
//        		this.showErrorJson("验证码错误");
//        	}
    	} catch(RuntimeException e) {
    		this.logger.error("注册出错", e);
			this.showErrorJson("注册出错[" + e.getMessage() + "]");
			e.printStackTrace();
		}

		return WWAction.JSON_MESSAGE;
    }
    
    /**
     * 会员信息修改
     */
    public String updateUserInfo() {
		String face = "";//头像
		String uname = "";//手机号
		String nickname = "";//昵称
		String fullname = "";//姓名
		String sex = "";//性别
		String idno = "";//身份证号
		String birthday = "";//生日
		String drivingyear = "";//驾龄
		String region = "";//地区
		String email = "";//邮箱
		String annualincome = "";//年收入
		try {
			String userJson = getBody(getRequest());
			JSONObject userObj = new JSONObject(userJson);
			if(userJson.contains("idno")){
				idno = userObj.getString("idno");
//				if (StringUtil.isEmpty(idno)) {
//					this.showErrorJson("身份证不能为空！");
//					return WWAction.JSON_MESSAGE;
//				}
				if (idno.length() != 18) {
					this.showErrorJson("请输入18位身份证号码！");
					return WWAction.JSON_MESSAGE;
				}
			}
			
			List userInfo = memberManager.updateUserInfo(userObj);
			net.sf.json.JSONObject obj = JSONArray.fromObject(userInfo).getJSONObject(0);
			Member member = memberManager.get(obj.getInt("member_id"));
			ThreadContextHolder.getSessionContext().setAttribute(UserConext.CURRENT_MEMBER_KEY, member);
			if(userInfo.size() > 0){
				String info = UploadUtil.replacePath(obj.toString());
				this.json = "{\"result\":1,\"data\":"+info+"}";
			} else {
				this.json = "{\"result\":0,\"message\":"+"修改失败"+"}";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			this.logger.error("修改用户信息出错", e);
			this.showErrorJson("修改用户信息出错[" + e.getMessage() + "]");
		}

		return WWAction.JSON_MESSAGE;
    }
    

	/**
	 * 校验验证码
	 * 
	 * @param validcode
	 * @param name (1、memberlogin:会员登录  2、memberreg:会员注册)
	 * @return 1成功 0失败
	 */
	private int validcode(String validcode,String name) {

		if (validcode == null) {
			return 0;
		}

		String code = (String) ThreadContextHolder.getSessionContext().getAttribute(ValidCodeServlet.SESSION_VALID_CODE + name);
		if (code == null) {
			return 0;
		} else {
			if (!code.equalsIgnoreCase(validcode)) {
				return 0;
			}
		}
		return 1;
	}
    /**
     * 注册时发送短信验证码
     * @return
     */
	public String sendCode(){
		try {
			String mobileCode=""+(int)((Math.random()*9+1)*100000);
			ThreadContextHolder.getSessionContext().setAttribute("mobileCode", mobileCode);
			ThreadContextHolder.getSessionContext().setAttribute("mobile", mobile);
			String content =  MobileMessageHttpSend.paraTo16("尊敬的客户您好，感谢您注册(盈驾)。验证码："+mobileCode+"，请于1分钟内填写。"+
							"(工作人员不会向您索取信息，请勿泄漏，以免造成账户或资金损失)如非本人操作请忽略或致电客服电话：010-64860393。【盈驾】");       //短信内容
			
			int result = this.memberManager.checkMobile(mobile);
			if(result==0){
				String strSmsParam = "reg=" + MobileContent.strReg + "&pwd=" + MobileContent.strPwd + "&sourceadd=" + MobileContent.strSourceAdd + "&phone=" + mobile + "&content=" + content;
				MobileMessageHttpSend.postSend(MobileContent.strSmsUrl,strSmsParam);
				this.showSuccessJson("发送成功");
			}else{
				this.showErrorJson("该手机号已被注册！");
			}
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			this.showErrorJson("发送失败");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			this.showErrorJson("发送失败");
		}
		return this.JSON_MESSAGE;
	}
	 /**
     * 修改密码时发送短信验证码
     * @return
     */
	public String sendCodeCPasswod(){
		try {
			String mobileCode=""+(int)((Math.random()*9+1)*100000);
			ThreadContextHolder.getSessionContext().setAttribute("mobileCode", mobileCode);
			ThreadContextHolder.getSessionContext().setAttribute("mobile", mobile);
			String content =  MobileMessageHttpSend.paraTo16("尊敬的盈驾客户，("+mobileCode+")为您的(修改账户密码)验证码，请于1分钟内填写。"+
						"(工作人员不会向您索取信息，请勿泄漏，以免造成账户或资金损失)如非本人操作请忽略或致电客服电话：010-64860393。【盈驾】");
			
			int result = this.memberManager.checkMobile(mobile);
			if(result==0){
				this.showErrorJson("该手机号尚未注册！");
			}else{
				String strSmsParam = "reg=" + MobileContent.strReg + "&pwd=" + MobileContent.strPwd + "&sourceadd=" + MobileContent.strSourceAdd + "&phone=" + mobile + "&content=" + content;
				MobileMessageHttpSend.postSend(MobileContent.strSmsUrl,strSmsParam);
				this.showSuccessJson("发送成功");
			}
		} catch (RuntimeException e) {
			System.out.println(e.getMessage());
			this.showErrorJson("发送失败");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			this.showErrorJson("发送失败");
		}
		return this.JSON_MESSAGE;
	}
	 /**
     * 校验手机号码和短信验证码
     * @return
     */
	public String validMobileAndCode(){
		String mobileCode=(String) ThreadContextHolder.getSessionContext().getAttribute("mobileCode");
		String mobile=(String) ThreadContextHolder.getSessionContext().getAttribute("mobile");
		if(mobile.equals(mobileTwo) && mobileCode.equals(mobileCodeTwo)){
			this.showSuccessJson("检验成功");
		}else{
			this.showErrorJson("检验失败");
		}
		return  this.JSON_MESSAGE;
		
	}
	
	/**
	 * @description 校验会员登录密码是否正确
	 * @date 2016年10月13日 下午5:52:25
	 * @return String
	 */
	public String checkPassword() {
		Member member = UserConext.getCurrentMember();
		String old = oldpassword;
		String oldPassword = StringUtil.md5(old);
		if (oldPassword.equals(member.getPassword())){
			this.showSuccessJson("正确");
		}else{
			this.showErrorJson("原密码错误");
		}
		return JSON_MESSAGE;
	}
	
	

    public IMemberManager getMemberManager() {
        return memberManager;
    }

    public void setMemberManager(IMemberManager memberManager) {
        this.memberManager = memberManager;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldpassword() {
        return oldpassword;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public String getRe_passwd() {
        return re_passwd;
    }

    public void setRe_passwd(String re_passwd) {
        this.re_passwd = re_passwd;
    }

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getValidcode() {
		return validcode;
	}

	public void setValidcode(String validcode) {
		this.validcode = validcode;
	}

	public String getMobileTwo() {
		return mobileTwo;
	}

	public void setMobileTwo(String mobileTwo) {
		this.mobileTwo = mobileTwo;
	}

	public String getMobileCodeTwo() {
		return mobileCodeTwo;
	}

	public void setMobileCodeTwo(String mobileCodeTwo) {
		this.mobileCodeTwo = mobileCodeTwo;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getPassWordSub() {
		return passWordSub;
	}

	public void setPassWordSub(String passWordSub) {
		this.passWordSub = passWordSub;
	}
	
}
