
package validation.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


/** 
 * @ClassName: NumberVlidator
 * @Description: 约束定义 
 * @author zhangyy 
 * @date 2015-7-31 上午10:11:14  
 */
@Target({ElementType.ANNOTATION_TYPE,ElementType.METHOD,ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME) 
@Documented 
@Constraint(validatedBy = {NumberVlidatorImpl.class}) 
public @interface NumberVlidator {

	boolean isNumber () default false;
	
	String message() default "该值应该为数字";   // 约束注解验证时的输出消息
	
	Class<?>[] groups() default { };  // 约束注解在验证时所属的组别
	
	Class<? extends Payload>[] payload() default { }; // 约束注解的有效负载
	
}
