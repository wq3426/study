
package validation;


import javax.xml.bind.ValidationException;

import validation.util.VlidationUtil;
import validation.vo.Person;

/** 
 * @ClassName: 测试类
 * @Description: TODO 
 * @author zhangyy 
 * @date 2015-7-30 上午11:44:15  
 */
public class Test1 {

	public static void main(String [] args ){
		Person person = new Person();
		try {
			VlidationUtil.validate(person);
		} catch (ValidationException e) {
			System.out.println(e.getMessage());
		}
		
	}

}

