
package validation.util;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
//import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.bind.ValidationException;

/** 
 * @ClassName: VlidationUtil
 * @Description: 校验工具类 
 * @author zhangyy 
 * @date 2015-7-31 上午10:28:48  
 */
public class ValidationUtil {

	private static Validator validator;
	
	static {
		ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
		validator = vf.getValidator();
	}
	

	/**
	 * @throws ValidationException 
	 * @throws ValidationException  
	 * @Description: 校验方法
	 * @param t 将要校验的对象
	 * @throws ValidationException 
	 * void
	 * @throws 
	 */ 
	public static <T> void validate(T t, Class<?>... groups) throws ValidationException{
		Set<ConstraintViolation<T>> set =  validator.validate(t, groups);
		if(set.size()>0){
			StringBuilder validateError = new StringBuilder();
			for(ConstraintViolation<T> val : set){
				validateError.append(val.getMessage() + " ;");
			}
			throw new ValidationException(validateError.toString());			
		}
	}
	
}
