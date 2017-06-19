
package validation;


import java.util.regex.Pattern;

import javax.xml.bind.ValidationException;

import org.wq.spring.spring_mvc.util.RegularExpressionUtils;

import validation.util.ValidationUtil;
import validation.vo.Person;

/** 
 * @ClassName: 测试类
 * @Description: TODO 
 * @author zhangyy 
 * @date 2015-7-30 上午11:44:15  
 */
public class Test1 {

	public static void main(String [] args ){
//		Person person = new Person();
//		try {
//			ValidationUtil.validate(person);
//		} catch (ValidationException e) {
//			System.out.println(e.getMessage());
//		}
		
		System.out.println(RegularExpressionUtils.matches("2012-02-09 23:18:43", RegularExpressionUtils.timePattern));
	}

}

