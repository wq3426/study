package com.enation.app.b2b2c.core.model.zxhb;

/***
 * @Description 预约用户实体类
 *
 * @createTime 2016年12月7日 下午9:07:12
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
public class OrderUser {
	private Integer user_id;//用户id
	private String username;//用户姓名
	private String password;//密码
	private String user_telephone;//联系电话
	private Long register_time;//注册时间
	
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
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
	public String getUser_telephone() {
		return user_telephone;
	}
	public void setUser_telephone(String user_telephone) {
		this.user_telephone = user_telephone;
	}
	public Long getRegister_time() {
		return register_time;
	}
	public void setRegister_time(Long register_time) {
		this.register_time = register_time;
	}
}
