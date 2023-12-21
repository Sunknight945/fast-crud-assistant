package com.uiys.jpa.globalLogAndExeception;

import java.util.UUID;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @author uiys
 */
@Aspect
@Component
public class LogConfig {


	@Before(value = "@within(org.springframework.web.bind.annotation.RestController)")
	public void addTraceId(JoinPoint joinPoint) {
		ThreadContext.put("traceId", UUID.randomUUID().toString());
	}


}


