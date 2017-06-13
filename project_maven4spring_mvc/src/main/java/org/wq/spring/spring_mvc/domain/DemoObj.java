package org.wq.spring.spring_mvc.domain;

public class DemoObj {
	private Long id;
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
