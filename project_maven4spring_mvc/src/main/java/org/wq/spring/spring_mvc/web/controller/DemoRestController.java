package org.wq.spring.spring_mvc.web.controller;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wq.spring.spring_mvc.domain.DemoObj;

/**
 * 1.@RestController 使用该注解声明控制器类时，不需要再添加@ResponseBody注解指定返回的response对象
 */
@RestController // 1
@RequestMapping("/rest")
public class DemoRestController {

	/**
	 * 2.application/json 指定返回值数据的媒体类型为json 请求方式为：/rest/getjson?id=1&name=wq
	 * 3.直接返回对象，对象会自动转换成json
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/getjson", produces="application/json;charset=UTF-8")// 2
	public DemoObj getJson(DemoObj obj){
		return new DemoObj(obj.getId(), obj.getName()+"yy");// 3
	}
	
	/**
	 * 4.application/xml 指定返回值数据的媒体类型为xml 请求方式为：/rest/getxml?id=1&name=wq
	 * 5.直接返回对象，对象会自动转换成xml
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/getxml", produces="application/xml;charset=UTF-8")// 4
	public DemoObj getXml(DemoObj obj){
		return new DemoObj(obj.getId(), obj.getName()+"yy");// 5
	}
	
	@RequestMapping("/second")
    public @Length Object second(@Length(min=6, message="密码长度不能小于6位") String password) {
        return "second controller";
    }

    @RequestMapping("/third")
    public @Length Object third(@Range(min=6, max=10, message="数据需要大于6小于10") int num, @Length(min=6, message="密码长度不能小于6位") String password) {
        return "third controller";
    }
}
