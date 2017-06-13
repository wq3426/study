package com.wq.spring.basic.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect //1 声明一个切面
@Component //2 将这个Bean注入Spring容器
public class LogAspect {

	@Pointcut("@annotation(com.wq.spring.basic.aop.Action)")//3 声明切点
	public void annotationPointCut(){}
	
	@After("annotationPointCut()")//4  通过@After声明一个建言，并使用@Pointcut定义的切点
	public void after(JoinPoint joinPoint){
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		Action action = method.getAnnotation(Action.class);
		
		//5 通过反射获取注解上的属性，然后做日志记录相关的操作
		System.out.println("注解式拦截："+action.name());
	}
	
	//6 通过@Before声明一个建言，此建言直接使用拦截规则作为参数(此处*后面要加空格)
	@Before("execution(* com.wq.spring.basic.aop.DemoMethodService.*(..))")
	public void before(JoinPoint joinPoint){
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		System.out.println("方法规则式拦截："+method.getName());
	}
}
