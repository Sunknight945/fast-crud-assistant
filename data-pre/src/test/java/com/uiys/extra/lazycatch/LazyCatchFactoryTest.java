package com.uiys.extra.lazycatch;

import com.uiys.extra.lazycatch.support.LazyCatchFactoryDefault;
import com.uiys.extra.lazycatch.support.LazyFieldLoaderFactory;
import com.uiys.extra.lazycatch.support.LazyTypeInterceptorFactory;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author uiys
 */
@SpringBootTest(classes = LazyCatchFactoryTest.class)
class LazyCatchFactoryTest {
	@Test
	public void testLazyCatch() {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(LinkOneService.class);
		applicationContext.register(LinkTwoService.class);
		applicationContext.refresh();
		LazyCatchFactory lazyCatchFactory =
		  new LazyCatchFactoryDefault(new LazyTypeInterceptorFactory(new LazyFieldLoaderFactory(applicationContext)),
			applicationContext);

		ObjectLazy objectLazy = new ObjectLazy();
		objectLazy.setLinkId("linkId");

		ObjectLazy objectLazy1 = lazyCatchFactory.lazyCatch(objectLazy);

		List<LinkObjOne> linkObjOnes = objectLazy1.getLinkObjOnes();

		System.out.println("linkObjOnes = " + linkObjOnes);
	}


}