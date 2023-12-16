package com.uiys.extra.lazycatch;

import com.uiys.extra.lazycatch.support.LazyCatchFactoryDefault;
import com.uiys.extra.lazycatch.support.LazyFieldLoaderFactory;
import com.uiys.extra.lazycatch.support.LazyTypeInterceptorFactory;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author uiys
 */
@SpringBootTest(classes = LazyCatchFactoryTest.class)
@ComponentScan(basePackages = "com.uiys.extra.lazycatch")
class LazyCatchFactoryTest implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Test
	public void testLazyCatch() {
		LazyCatchFactory lazyCatchFactory =
		  new LazyCatchFactoryDefault(new LazyTypeInterceptorFactory(new LazyFieldLoaderFactory(applicationContext)),
		    applicationContext);
		ObjectLazy objectLazy = new ObjectLazy();
		objectLazy.setLinkId("linkId");
		ObjectLazy objectLazy1 = lazyCatchFactory.lazyCatch(objectLazy);
		List<LinkObjOne> linkObjOnes = objectLazy1.getLinkObjOnes();
		System.out.println("linkObjOnes = " + linkObjOnes);
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}