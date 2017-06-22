package org.wq.spring.spring_mvc.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.wq.util.validator.AllValidator;
import org.wq.util.validator.UpdateValidator;

public class DemoObj {
	
	@NotNull(message="id不能为空", groups={UpdateValidator.class})
	@Min(value=1, message="最小为1", groups={AllValidator.class})
	private Long id;
	
	@NotNull(message="name不能为空", groups={AllValidator.class})
	@Length(min=1, max=20, message="name长度在1-20之间", groups={AllValidator.class})
	@Pattern(regexp="(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{1,20}", message="name必须是1~20位数字和字母的组合", groups={AllValidator.class})
	private String name;
	
	//jackson对对象进行json转换时需要类拥有无参构造函数
	public DemoObj() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public DemoObj(Long id, String name){
		super();
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
