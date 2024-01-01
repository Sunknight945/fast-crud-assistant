package com.uiys.util;

import com.uiys.gen.DefaultNameContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author uiys
 */
@SpringBootTest(classes = TxENameTest.class)
class TxENameTest {

	@Test
	public void testUFT() {
		String fasdf = TorE.name(DefaultNameContext.class, "fasdf");
		System.out.println("fasdf = " + fasdf);
	}

}