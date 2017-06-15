package com.enation.app.b2b2c.core.action.api.member;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.member.IInviteCodeManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.base.core.service.ISmsManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.eop.SystemSetting;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.eop.sdk.utils.ValidCodeServlet;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.jms.EmailModel;
import com.enation.framework.jms.EmailProducer;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.HttpUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.RequestUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONObject;

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("member")
@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })
@InterceptorRef(value="apiRightStack",params={"apiRightInterceptor.excludeMethods","login,checkname,checkemail,sendCode,regMobile,register,reSendRegMail,changeGestureLock,changeRankingFlag"})
public class MemberApiAction extends WWAction {

	private IMemberManager memberManager;
	private IAdminUserManager adminUserManager;
	private IRegionsManager regionsManager;
	private IInviteCodeManager inviteCodeManager;
	
	private String username;
	private String password;
	private String validcode;//验证码
	private String remember;//两周内免登录
	private Map memberMap;
	private String license;//同意注册协议
	private String email;
	private String friendid;

	/**
	 * 上传头像变量
	 */
	private File faceFile ;
	private File face ;         //633行有单独的上传头像，没动，酌情处理，需要这里，
	private String faceFileName;
	
	private String faceFileFileName;

	private String photoServer;
	private String photoId;
	private String type;
	private String truename;	//会员真实姓名

	/**
	 * 更改密码
	 */
	private String oldpassword;
	private String newpassword;
	private String re_passwd;
	
	
	private Integer province_id;
	private Integer city_id;
	private Integer region_id;
	private String province;
	private String city;
	private String region;
	private String address;
	private String zip;
	private String mobile;
	private String tel;
	private String nickname;
	private String sex;
	private String mybirthday;
	/**
	 * 搜索
	 */
	private Integer lvid; //会员级别id
	private String keyword; //关键词
	/**
	 * 重新发送激活邮件
	 */
	private JavaMailSender mailSender;
	private EmailProducer mailMessageProducer;
	private IMemberPointManger memberPointManger;

	
	private File file ;
	private String fileFileName;
	
	private ISmsManager smsManager;
	
	/**
	 * 会员登录
	 * @param username:用户名,String型
	 * @param password:密码,String型
	 * @param validcode:验证码,String型
	 * @param remember:两周内免登录,String型
	 * @return json字串
	 * result  为1表示登录成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	public String login() {
		try {
			if (this.validcode(validcode,"memberlogin") == 1) {
				int result = this.memberManager.login(username, password);
				if (result == 1) {
					// 两周内免登录
					if (remember != null && remember.equals("1")) {
						String cookieValue = EncryptionUtil1.authcode(
								"{username:\"" + username + "\",password:\"" + StringUtil.md5(password) + "\"}",
								"ENCODE", "", 0);
						HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser", cookieValue, 60 * 60 * 24 * 14);
					}
					this.showSuccessJson("登录成功");
				}else{
					this.showErrorJson("账号密码错误");
				}
			} else {
				this.showErrorJson("验证码错误！");
			}
		}catch(RuntimeException e) {
			e.printStackTrace();
			this.showErrorJson("系统错误");
		}
		
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 注销会员登录
	 * @param 无
	 * @return json字串
	 * result  为1表示注销成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	public String logout() {
		this.showSuccessJson("注销成功");
		//如果用户注销异常 不返回首页问题
		try {
			HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser", null, 0);
			this.memberManager.logout();
		} catch (Exception e) {
			
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 修改会员密码
	 * @param oldpassword:原密码,String类型
	 * @param newpassword:新密码,String类型
	 * @return json字串
	 * result  为1表示修改成功，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	public String changePassword() {
		Member member = UserConext.getCurrentMember();
		if(member==null){
			this.showErrorJson("尚未登录，无权使用此api");
			return this.JSON_MESSAGE;
		}
		String oldPassword = this.getOldpassword();
		oldPassword = oldPassword == null ? "" : StringUtil.md5(oldPassword);
		if (oldPassword.equals(member.getPassword())) {
			String password = this.getNewpassword();
			String passwd_re = this.getRe_passwd();

			if (passwd_re.equals(password)) {
				try {
					memberManager.updatePassword(mobile,password);
					this.showSuccessJson("修改密码成功");
				} catch (Exception e) {
					if (this.logger.isDebugEnabled()) {
						logger.error(e.getStackTrace());
					}
					this.showErrorJson("修改密码失败");
				}
			} else {
				this.showErrorJson("修改失败！两次输入的密码不一致");
			}
		} else {
			this.showErrorJson("修改失败！原始密码不符");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	
	/**
	 * 验证原密码输入是否正确
	 * @param oldpassword:密码，String型
	 * @return json字串
	 * result  为1表示原密码正确，0表示失败 ，int型
	 * message 为提示信息 ，String型
	 */
	public String password(){
		Member member = UserConext.getCurrentMember();
		String old = oldpassword;
		String oldPassword = StringUtil.md5(old);
		if (oldPassword.equals(member.getPassword())){
			this.showSuccessJson("正确");
		}else{
			this.showErrorJson("原密码错误");
		}
		return WWAction.JSON_MESSAGE;
	}
	
	
	
	/**
	 * 搜索会员(要求管理员身份)
	 * @param lvid:会员级别id，如果为空则搜索全部会员级别，int型
	 * @param keyword:搜索关键字,可为空，String型
	 * @return json字串
	 * result  为1表示搜索成功，0表示失败 ，int型
	 * data: 会员列表
	 * {@link Member}
	 */
	public String search(){
		try{
			if(UserConext.getCurrentAdminUser()==null){
				this.showErrorJson("无权访问此api");
				return this.JSON_MESSAGE;
			}
			memberMap = new HashMap();
			memberMap.put("lvId", lvid);
			memberMap.put("keyword", keyword);
			memberMap.put("stype", 0);
			List memberList  =this.memberManager.search(memberMap);
			this.json = JsonMessageUtil.getListJson(memberList);
		}catch(Throwable e){
			this.logger.error("搜索会员出错", e);
			this.showErrorJson("搜索会员出错");
			
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 检测username是否存在，并生成json返回给客户端
	 */
	public String checkname() {
		int result = this.memberManager.checkname(username);
		if(result==0){
			this.showSuccessJson("会员名称可以使用！");
		}else{
			this.showErrorJson("该会员名称已经存在！");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 检测email是否存在，并生成json返回给客户端
	 */
	public String checkemail() {
		int result = this.memberManager.checkemail(email);
		if(result==0){
			this.showSuccessJson("邮箱不存在，可以使用");
		}else{
			this.showErrorJson("该邮箱已经存在！");
		}
		return this.JSON_MESSAGE;
	}
	
	
	public String sendCode(){
		try {
			String mobileCode=""+(int)((Math.random()*9+1)*100000);
			ThreadContextHolder.getSessionContext().setAttribute("mobileCode", mobileCode);
			String content = "验证码为：【"+mobileCode+"】";
			
			Map param = new HashMap();
			param.put("code", mobileCode);
			
			int result = this.memberManager.checkMobile(mobile);
			if(result==0){
				this.smsManager.send(mobile, content,param);
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
	
	
	
	
	public String regMobile(){
		if(this.validmobile_code(validcode, mobile)==0){
			this.showErrorJson("验证码输入错误!");
			return this.JSON_MESSAGE;
		}
		
		if(this.memberManager.checkMobile(mobile)==1){
			this.showErrorJson("此手机号已注册，请更换手机号!");
			return this.JSON_MESSAGE;
		}
		
		if (!"agree".equals(license)) {
			this.showErrorJson("同意注册协议才可以注册!");
			return this.JSON_MESSAGE;
		}
		
		if (StringUtil.isEmpty(password)) {
			this.showErrorJson("密码不能为空！");
			return this.JSON_MESSAGE;
		}
		
		Member member = new Member();
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String registerip = request.getRemoteAddr();
		
		member.setMobile("");
		member.setUsername(mobile);
		member.setMobile(mobile);
		member.setPassword(password);
		member.setEmail("");
		member.setRegisterip(registerip);
		
		int result = memberManager.register(member);
		if (result == 1) { // 添加成功
			this.memberManager.login(mobile, password);
			this.showSuccessJson("注册成功");
		} else {
			this.showErrorJson("用户名[" + member.getUsername() + "]已存在!");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 会员注册
	 * @version v1.1,2015-12-17 whj   增加会员注册时的手机项，但不是必填项，用来以后手机找回使用
	 * @version v1.2,2015-12-17 whj   会员的uname及name分不清楚，暂时这2个字段在注册的时候使用同一个值
	 * @version v1.3,2015-12-22 whj   会员的uname及name分别为会员注册时名称及真实姓名，将上次修改的改回。
	 * @version v1.3,2016-01-07 whj   注册时将会员的uname及name值同时注册，防止登录报错，用户可在会员中心修改详细中单独修改name字段。
	 */
	public String register() {
		
		try {
			
		
			if (this.validcode(validcode,"memberreg") == 0) {
				this.showErrorJson("验证码输入错误!");
				return this.JSON_MESSAGE;
			}
			if (!"agree".equals(license)) {
				this.showErrorJson("同意注册协议才可以注册!");
				return this.JSON_MESSAGE;
			}
			
	
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String isStore = request.getParameter("is_store");
			
			// 注册的是商家。 就检查邀请码
			if("yes".equals(isStore)) {
				String inviteCode = request.getParameter("invite_code");
				boolean result = this.inviteCodeManager.useCode(inviteCode);
				
				// 邀请码无效
				if(!result) {
					this.showErrorJson("该邀请码无效！");
					return this.JSON_MESSAGE;
				}
			}
	
			Member member = new Member();
			String registerip = request.getRemoteAddr();
	
			if (StringUtil.isEmpty(username)) {
				this.showErrorJson("用户名不能为空！");
				return this.JSON_MESSAGE;
			}
			if (username.length() < 4 || username.length() > 20) {
				this.showErrorJson("用户名的长度为4-20个字符！");
				return this.JSON_MESSAGE;
			}
			if (username.contains("@")) {
				this.showErrorJson("用户名中不能包含@等特殊字符！");
				return this.JSON_MESSAGE;
			}
			if (StringUtil.isEmpty(email)) {
				this.showErrorJson("注册邮箱不能为空！");
				return this.JSON_MESSAGE;
			}
			if (!StringUtil.validEmail(email)) {
				this.showErrorJson("注册邮箱格式不正确！");
				return this.JSON_MESSAGE;
			}
			if (StringUtil.isEmpty(password)) {
				this.showErrorJson("密码不能为空！");
				return this.JSON_MESSAGE;
			}
			if (memberManager.checkname(username) > 0) {
				this.showErrorJson("此用户名已经存在，请您选择另外的用户名!");
				return this.JSON_MESSAGE;
			}
			if (memberManager.checkemail(email) > 0) {
				this.showErrorJson("此邮箱已经注册过，请您选择另外的邮箱!");
				return this.JSON_MESSAGE;
			}
	
			member.setMobile(mobile);
			member.setUsername(username);
			member.setName(username);       
			member.setPassword(password);
			member.setEmail(email);
			member.setRegisterip(registerip);
			
			if (!StringUtil.isEmpty(friendid)) {
				Member parentMember = this.memberManager.get(Integer.parseInt(friendid));
				if (parentMember != null) {
					member.setParentid(parentMember.getMember_id());
				}
			} else {
				// 不是推荐链接 检测是否有填写推荐人
				String reg_Recomm = request.getParameter("reg_Recomm");
				if (!StringUtil.isEmpty(reg_Recomm)	&& reg_Recomm.trim().equals(email.trim())) {
					this.showErrorJson("推荐人的邮箱请不要填写自己的邮箱!");
					return this.JSON_MESSAGE;
				}
				if (!StringUtil.isEmpty(reg_Recomm)	&& StringUtil.validEmail(reg_Recomm)) {
					Member parentMember = this.memberManager.getMemberByEmail(reg_Recomm);
					if (parentMember == null) {
						this.showErrorJson("您填写的推荐人不存在!");
						return this.JSON_MESSAGE;
					} else {
						member.setParentid(parentMember.getMember_id());
					}
				}
			}
	
			int result = memberManager.register(member);
			if (result == 1) { // 添加成功
				this.memberManager.login(username, password);
				
				String mailurl = "http://mail." + StringUtils.split(member.getEmail(), "@")[1];
			
				this.json = JsonMessageUtil.getStringJson("mailurl", mailurl);
			} else {
				this.showErrorJson("用户名[" + member.getUsername() + "]已存在!");
			}
		}catch(RuntimeException e) {
			e.printStackTrace();
			this.showErrorJson("" + e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 重新发送激活邮件
	 */
	public String reSendRegMail(){
		try{
			//重新发送激活邮件
			Member member = UserConext.getCurrentMember();
			if(member == null){
				this.showErrorJson("请您先登录再重新发送激活邮件!");
				return this.JSON_MESSAGE;
			}
			member = memberManager.get(member.getMember_id());
			if(member == null){
				this.showErrorJson("用户不存在,请您先登录再重新发送激活邮件!");
				return this.JSON_MESSAGE;
			}
			if(member.getLast_send_email() != null && System.currentTimeMillis() / 1000 - member.getLast_send_email().intValue() < 2 * 60 * 60){
				this.showErrorJson("对不起，两小时之内只能重新发送一次激活邮件!");
				return this.JSON_MESSAGE;
			}
	
			EopSite site  = EopSite.getInstance();
			String domain =RequestUtil.getDomain();
			String checkurl  = domain+"/memberemailcheck.html?s="+ EncryptionUtil1.authcode(member.getMember_id()+","+member.getRegtime(), "ENCODE","",0);
			EmailModel emailModel = new EmailModel();
			emailModel.getData().put("username", member.getUsername());
			emailModel.getData().put("checkurl", checkurl);
			emailModel.getData().put("sitename", site.getSitename());
			emailModel.getData().put("logo", site.getLogofile());
			emailModel.getData().put("domain", domain);
			if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_EMIAL_CHECK) ){
				int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_EMIAL_CHECK+"_num");
				int mp =memberPointManger.getItemPoint(IMemberPointManger.TYPE_EMIAL_CHECK+"_num_mp");
				emailModel.getData().put("point", point);
				emailModel.getData().put("mp", mp);
			}
			emailModel.setTitle(member.getUsername()+"您好，"+site.getSitename()+"会员注册成功!");
			emailModel.setEmail(member.getEmail());
			emailModel.setTemplate("reg_email_template.html");
			emailModel.setEmail_type("邮箱激活");
			mailMessageProducer.send(emailModel);
			member.setLast_send_email(DateUtil.getDateline());
			memberManager.edit(member);
			this.showSuccessJson("激活邮件发送成功，请登录您的邮箱 " + member.getEmail() + " 进行查收！");
		}catch(RuntimeException e){
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	
	public String saveInfo(){
		Member member = UserConext.getCurrentMember();
 
		member = memberManager.get(member.getMember_id());
		
	 
		//先上传图片
		String faceField = "faceFile";
		
		if(file!=null){
		
			//判断文件类型
			String allowTYpe = "gif,jpg,bmp,png";
			if (!fileFileName.trim().equals("") && fileFileName.length() > 0) {
				String ex = fileFileName.substring(fileFileName.lastIndexOf(".") + 1, fileFileName.length());
				if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0){
					this.showErrorJson("对不起,只能上传gif,jpg,bmp,png格式的图片！");
					return this.JSON_MESSAGE;
				}
			}
			
			//判断文件大小
			
			if(file.length() > 200 * 1024){
				this.showErrorJson("'对不起,图片不能大于200K！");
				return this.JSON_MESSAGE;
			}
			
			String imgPath=	UploadUtil.upload(file, fileFileName, faceField);
			member.setFace(imgPath);
		}
		
		HttpServletRequest request =  ThreadContextHolder.getHttpRequest();
		
		if(StringUtil.isEmpty(mybirthday)){
			member.setBirthday(0L);
		}else{
			member.setBirthday(DateUtil.getDateline(mybirthday));
		}

		
		member.setProvince_id(province_id);
		member.setCity_id(city_id);
		member.setRegion_id(region_id);
		member.setProvince(province);
		member.setCity(city);
		member.setRegion(region);
		member.setAddress(address);
		member.setZip(zip);
		if(mobile!=null){
			member.setMobile(mobile);
		}
		member.setTel(tel);
		
		//如果会员真实姓名不为空
		if(truename != null){
			member.setName(truename);
		}
		
		member.setSex(Integer.valueOf(sex));

	
		// 身份
		String midentity = request.getParameter("member.midentity");
		if (!StringUtil.isEmpty(midentity)) {
			member.setMidentity(StringUtil.toInt(midentity));
		} else {
			member.setMidentity(0);
		}
		// String pw_question = request.getParameter("member.pw_question");
		// member.setPw_question(pw_question);
		// String pw_answer = request.getParameter("member.pw_answer");
		// member.setPw_answer(pw_answer);
		try {
			// 判断否需要增加积分
			boolean addPoint = false;
			if (member.getInfo_full() == 0 && !StringUtil.isEmpty(member.getName())
					&& !StringUtil.isEmpty(member.getNickname()) && !StringUtil.isEmpty(member.getProvince())
					&& !StringUtil.isEmpty(member.getCity()) && !StringUtil.isEmpty(member.getRegion())
					&& (!StringUtil.isEmpty(member.getMobile()) || !StringUtil.isEmpty(member.getTel()))) {
				addPoint = true;
			}
			// 增加积分
			if (addPoint) {
				member.setInfo_full(1);
				memberManager.edit(member);
				if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_FINISH_PROFILE)) {
					int point = memberPointManger.getItemPoint(IMemberPointManger.TYPE_FINISH_PROFILE + "_num");
					int mp = memberPointManger.getItemPoint(IMemberPointManger.TYPE_FINISH_PROFILE + "_num_mp");
					memberPointManger.add(member.getMember_id(), point,	"完善个人资料", null, mp);
				}
			} else {
				memberManager.edit(member);
			}
			this.showSuccessJson("编辑个人资料成功！");
			return this.JSON_MESSAGE;
		} catch (Exception e) {
			if (this.logger.isDebugEnabled()) {
				logger.error(e.getStackTrace());
			}
			this.showErrorJson("编辑个人资料失！");
			
			return this.JSON_MESSAGE;
		}
	} 


	//************to宏俊：以api先不用书写文档****************/
	protected String toUrl(String path) {
		String static_server_domain= SystemSetting.getStatic_server_domain();
		String urlBase = static_server_domain;
		return path.replaceAll("fs:", urlBase);
	}

	protected String makeFilename(String subFolder) {
		String ext = FileUtil.getFileExt(photoServer);
		String fileName = photoId + "_" + type + "." + ext;
		String static_server_path= SystemSetting.getStatic_server_path();

		String filePath = static_server_path + "/attachment/";
		if (subFolder != null) {
			filePath += subFolder + "/";
		}

		filePath += fileName;
		return filePath;
	}

	/**
	 * 保存从Flash编辑后返回的头像，保存二次，一大一小两个头像
	 * 
	 * @return
	 */
	public String saveAvatar() {
		String targetFile = makeFilename("avatar");

		int potPos = targetFile.lastIndexOf('/') + 1;
		String folderPath = targetFile.substring(0, potPos);
		FileUtil.createFolder(folderPath);

		try {
			File file = new File(targetFile);

			if (!file.exists()) {
				file.createNewFile();
			}

			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			FileOutputStream dos = new FileOutputStream(file);
			int x = request.getInputStream().read();
			while (x > -1) {
				dos.write(x);
				x = request.getInputStream().read();
			}
			dos.flush();
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if ("big".equals(type)) {
			Member member = UserConext.getCurrentMember();
			member.setFace("fs:/attachment/avatar/" + photoId + "_big."
					+ FileUtil.getFileExt(photoServer));
			memberManager.edit(member);
		}

		json = "{\"data\":{\"urls\":[\"" + targetFile
				+ "\"]},\"status\":1,\"statusText\":\"保存存成功\"}";

		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 上传头像文件
	 * 
	 * @return
	 */
	public String uploadAvatar() {
		JSONObject jsonObject = new JSONObject();
		try {
			if (faceFile != null) {
				String file = UploadUtil.upload(face, faceFileName, "avatar");
				Member member = UserConext.getCurrentMember();
				jsonObject.put("result", 1);
				jsonObject.put("member_id", member.getMember_id());
				jsonObject.put("url", toUrl(file));
				jsonObject.put("message", "操作成功！");
			}
		} catch (Exception e) {
			jsonObject.put("result", 0);
			jsonObject.put("message", "操作失败！");
		}

		this.json = jsonObject.toString();

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
	
	private int validmobile_code(String validcode,String mobile){
		if (validcode == null) {
			return 0;
		}
		String code = (String) ThreadContextHolder.getSessionContext().getAttribute("mobileCode");
		if (code == null) {
			return 0;
		} else {
			if (!code.equalsIgnoreCase(validcode)) {
				return 0;
			}
		}
		return 1;
	}
	
	public String getRepairMemberByStoreId(){
		Map result = new HashMap();
		try {
			HttpServletRequest request=ThreadContextHolder.getHttpRequest();
			Member member = UserConext.getCurrentMember();
			int storeId = member.getStore_id();
			String userName=request.getParameter("userName");
			
			result.put("storeId", storeId);
			result.put("userName", userName);
			
			List<Member> memberList = this.memberManager.getRepairMemberByStoreId(result);
			result.put("memberList", memberList);
			this.json = JsonMessageUtil.getObjectJson(result);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * @description 检验用户的原密码是否正确
	 * @date 2016年9月24日 下午3:17:13
	 * @return String
	 */
	public String checkOldPassword() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String oldPassword = request.getParameter("oldPassword");//原密码
		String username = request.getParameter("username");//会员名（用户名）
		if(StringUtils.isEmpty(oldPassword)) {
			this.showErrorJson("原密码不能为空！");
			return JSON_MESSAGE;	
		}
		
		try {
			oldPassword = StringUtil.md5(oldPassword);
			String originalPassword = memberManager.queryMemberPasswordByUsername(username);
			if(originalPassword.equals(oldPassword)) {
				this.showSuccessJson("原密码正确");
			} else {
				this.showErrorJson("原密码错误");
			}
			
		} catch (Exception e) {
			this.showErrorJson("系统错误");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 用户密码修改
	 * @date 2016年9月14日 下午3:56:34
	 * @return String
	 */
	public String changeMemberPassword() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String oldPassword = request.getParameter("oldPassword");//原密码
		String newPassword = request.getParameter("newPassword");//新密码
		String username = request.getParameter("username");//会员名（用户名）
		
		try {
			//数据校验
			if(StringUtil.isEmpty(oldPassword)) {
				this.showErrorJson("原密码不能为空！");
				return JSON_MESSAGE;
			}	
			if(StringUtil.isEmpty(newPassword)) {
				this.showErrorJson("新密码不能为空！");
				return JSON_MESSAGE;
			}
			if(newPassword.equals(oldPassword)) {
				this.showErrorJson("新密码与旧密码一样，请重新填写新密码");
				return JSON_MESSAGE;
			}
			if (newPassword.length() < 6 || newPassword.length() > 16) {
				this.showErrorJson("新密码的长度为4-20个字符！");
				return JSON_MESSAGE;
			}
				
			//更新密码
			newPassword = StringUtil.md5(newPassword);
			int result = memberManager.updateMemberPassword(newPassword,username);
			if(result ==1) {
				this.showSuccessJson("尊敬的盈驾客户，您已成功修改【登录密码】，请勿泄漏，如有疑问请致电客服电话：010-64860393。【盈驾】");
			} else {
				this.showErrorJson("密码修改失败");
			}
			
		} catch (RuntimeException e) {
			this.showErrorJson(e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e) {
			this.showErrorJson("密码修改失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 修改排名开关
	 * @date 2016年9月23日 下午5:12:20
	 * @return String
	 */
	public String changeRankingFlag() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String userName = request.getParameter("userName");
		String rankingFlag = request.getParameter("rankingFlag");
		
		if(StringUtils.isEmpty(userName)) {
			this.showErrorJson("用户名不能为空");
		}
		if(StringUtils.isEmpty(rankingFlag)) {
			this.showErrorJson("排名标识不能为空");
		}
		
		try {
			//更新排名标识
			memberManager.updateRankingFlag(userName,rankingFlag);
			this.showSuccessJson("排名开关设置成功");
		} catch (Exception e) {
			this.showErrorJson("排名开关设置失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
		
	}
	
	/**
	 * @description 更新手势锁
	 * @date 2016年9月24日 上午10:35:50
	 * @return String
	 */
	public String changeGestureLock() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String userName = request.getParameter("userName");
		String oldGestureLock = request.getParameter("oldGestureLock");
		String newGestureLock = request.getParameter("newGestureLock");
		
		if(StringUtils.isEmpty(userName)) {
			this.showErrorJson("用户名不能为空");
		}
		
		if(StringUtils.isNotEmpty(oldGestureLock)) {
			//校验旧手势锁是否正确
			boolean isCorrect = memberManager.checkOldGestureLockIsCorrect(userName, oldGestureLock);
			if(!isCorrect) {
				this.showErrorJson("原手势锁不正确");
			}
		}
		
		try {
			//更新手势锁
			memberManager.updateGestureLock(userName, newGestureLock);
			this.showSuccessJson("手势锁设置成功");
		} catch (Exception e) {
			this.showErrorJson("手势锁设置成功");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * ==================================================================================
	 * GETTER AND SETTER
	 */
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

	public File getFace() {
		return face;
	}

	public void setFace(File face) {
		this.face = face;
	}

	public String getFaceFileName() {
		return faceFileName;
	}

	public void setFaceFileName(String faceFileName) {
		this.faceFileName = faceFileName;
	}

	public String getPhotoServer() {
		return photoServer;
	}

	public void setPhotoServer(String photoServer) {
		this.photoServer = photoServer;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOldpassword() {
		return oldpassword;
	}

	public void setOldpassword(String oldpassword) {
		this.oldpassword = oldpassword;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public String getRe_passwd() {
		return re_passwd;
	}

	public void setRe_passwd(String re_passwd) {
		this.re_passwd = re_passwd;
	}

	public Integer getLvid() {
		return lvid;
	}

	public void setLvid(Integer lvid) {
		this.lvid = lvid;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public String getValidcode() {
		return validcode;
	}

	public void setValidcode(String validcode) {
		this.validcode = validcode;
	}

	public String getRemember() {
		return remember;
	}

	public void setRemember(String remember) {
		this.remember = remember;
	}

	public Map getMemberMap() {
		return memberMap;
	}

	public void setMemberMap(Map memberMap) {
		this.memberMap = memberMap;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFriendid() {
		return friendid;
	}

	public void setFriendid(String friendid) {
		this.friendid = friendid;
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public EmailProducer getMailMessageProducer() {
		return mailMessageProducer;
	}

	public void setMailMessageProducer(EmailProducer mailMessageProducer) {
		this.mailMessageProducer = mailMessageProducer;
	}

	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}

	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}

	public File getFaceFile() {
		return faceFile;
	}

	public void setFaceFile(File faceFile) {
		this.faceFile = faceFile;
	}

	public String getTruename() {
		return truename;
	}
	
	public void setTruename(String truename) {
		this.truename = truename;
	}

	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

	public String getFaceFileFileName() {
		return faceFileFileName;
	}

	public void setFaceFileFileName(String faceFileFileName) {
		this.faceFileFileName = faceFileFileName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public Integer getProvince_id() {
		return province_id;
	}

	public void setProvince_id(Integer province_id) {
		this.province_id = province_id;
	}

	public Integer getCity_id() {
		return city_id;
	}

	public void setCity_id(Integer city_id) {
		this.city_id = city_id;
	}

	public Integer getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Integer region_id) {
		this.region_id = region_id;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getMybirthday() {
		return mybirthday;
	}

	public void setMybirthday(String mybirthday) {
		this.mybirthday = mybirthday;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public ISmsManager getSmsManager() {
		return smsManager;
	}

	public void setSmsManager(ISmsManager smsManager) {
		this.smsManager = smsManager;
	}

	public IInviteCodeManager getInviteCodeManager() {
		return inviteCodeManager;
	}

	public void setInviteCodeManager(IInviteCodeManager inviteCodeManager) {
		this.inviteCodeManager = inviteCodeManager;
	}



	
}
