
package validation.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import validation.validate.NumberVlidator;


/** 
 * @ClassName: Person
 * @Description: TODO 
 * @author zhangyy 
 * @date 2015-7-30 上午11:46:37  
 */
public class Person {

	@NotNull(message = "用户ID不能为空")
	private Integer id;		//应为包装类型，否则不能检测到
	
	@NotNull(message = "test不能为空")
	private String test;
	
	@NumberVlidator(message= "体重必须为数字")
	private String weight;
	
	@NotNull(message = "用户姓名不能为空dd")
	@Size(min=1, max=10, message="用户姓名必须是1-10位之间")
	private String username;
	
	private Date birthday;

	
	
	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	
	
	
}
