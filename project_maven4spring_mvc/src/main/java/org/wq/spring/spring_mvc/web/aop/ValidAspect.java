package org.wq.spring.spring_mvc.web.aop;

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;

/**
 * 参考资料链接：http://blog.csdn.net/tianyaleixiaowu/article/details/71173059
 * @author wq3426
 *
 */

@Aspect
@Component
@EnableAspectJAutoProxy //启用AspectJ自动代理
public class ValidAspect {
	
	private ObjectError error;

    @Pointcut("execution(public * org.wq.spring.spring_mvc.web.controller.*.*(..))")
    public void valid() {}

    //环绕通知,环绕增强，相当于MethodInterceptor
    @Around("valid()")
    public Object arround(ProceedingJoinPoint pjp) {
        System.out.println("方法环绕start.....");
        try {
            //取参数，如果没参数，那肯定不校验了
            Object[] objects = pjp.getArgs();
            if (objects.length == 0) {
                return pjp.proceed();
            }
            /**************************校验封装好的javabean**********************/
            //寻找带BindingResult参数的方法，然后判断是否有error，如果有则是校验不通过
//            for (Object object : objects) {
//                if (object instanceof BeanPropertyBindingResult) {
//                    //有校验
//                    BeanPropertyBindingResult result = (BeanPropertyBindingResult) object;
//                    if (result.hasErrors()) {
//                        List<ObjectError> list = result.getAllErrors();
//                        for (ObjectError error : list) {
//                            System.out.println(error.getCode() + "---" + error.getArguments() + "--" + error.getDefaultMessage());
//                            //返回第一条校验失败信息。也可以拼接起来返回所有的
//                            return error.getDefaultMessage();
//                        }
//                    }
//                }
//            }

            /**************************校验普通参数*************************/
            //  获得切入目标对象
            Object target = pjp.getThis();
            // 获得切入的方法
            Method method = ((MethodSignature) pjp.getSignature()).getMethod();
            // 执行校验，获得校验结果
            Set<ConstraintViolation<Object>> validResult = validMethodParams(target, method, objects);
            //如果有校验不通过的
            if (!validResult.isEmpty()) {
//                String[] parameterNames = parameterNameDiscoverer.getParameterNames(method); // 获得方法的参数名称

//                for(ConstraintViolation<Object> constraintViolation : validResult) {
//                    PathImpl pathImpl = (PathImpl) constraintViolation.getPropertyPath();  // 获得校验的参数路径信息
//                    int paramIndex = pathImpl.getLeafNode().getParameterIndex(); // 获得校验的参数位置
//                    String paramName = parameterNames[paramIndex];  // 获得校验的参数名称

//                    System.out.println("paramName---"+paramName);
                    //校验信息
//                    System.out.println("message---"+constraintViolation.getMessage());
//                }
                
                //返回第一条
                String json = "{\"result\": 0, \"msg\": \""+ validResult.iterator().next().getMessage() +"\"}";
                
                return json;
            }

            return pjp.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final ExecutableValidator validator = factory.getValidator().forExecutables();

    private <T> Set<ConstraintViolation<T>> validMethodParams(T obj, Method method, Object[] params) {
        return validator.validateParameters(obj, method, params);
    }
}
