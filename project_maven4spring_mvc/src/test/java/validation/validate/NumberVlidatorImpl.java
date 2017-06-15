
package validation.validate;

import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/** 
 * @ClassName: NumberVlidatorImpl
 * @Description: 约束验证器 
 * @author zhangyy 
 * @date 2015-7-31 上午10:14:44  
 */
public class NumberVlidatorImpl implements ConstraintValidator<NumberVlidator, String> {

	private boolean isNumber;
	
	/** 
	* <p>Title: 对验证器进行实例化</p>  
	* @param constraintAnnotation  
	*/ 
	@Override
	public void initialize(NumberVlidator constraintAnnotation) {  //初始化
		isNumber = constraintAnnotation.isNumber();
	}
 
	/**  
	* <p>Description: 校验的方法</p> 
	* @param value  需要验证的实例
	* @param context 约束执行的上下文环境
	* @return  
	*/ 
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {  
		if(value==null || value.length()<=0){
			return true;
		}else{
			try {
				if(isNumber){
					Long.parseLong(value);
				}else{
					new BigDecimal(value);
				}
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
	}

}
