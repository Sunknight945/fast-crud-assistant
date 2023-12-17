package com.uiys.jpa.globalExceptionHandler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author uiys
 */
@Slf4j
@Aspect
@Component
public class GEHLog {
	@SneakyThrows
	@Around(value = "execution(* com.uiys.jpa.globalExceptionHandler.GlobalExceptionHandler.*(..))")
	public Object log(ProceedingJoinPoint pjp) {
		Object[] exType = pjp.getArgs();
		Object exReason = pjp.proceed(exType);
		log.error(ExReason.in(exReason));
		return exReason;
	}

}

