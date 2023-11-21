package com.uiys.jpa.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.ToString;
import org.junit.jupiter.api.Test;

/**
 * @author uiys
 * @date 2023/11/11
 */
class AssembleUtilsTest {

	@Test
	public void testMarge() {
		List<AAAA> out = new ArrayList<>();
		AAAA aaaa1 = new AAAA();
		aaaa1.setId("DF1");
		aaaa1.setName("A1");
		AAAA aaaa2 = new AAAA();
		aaaa2.setId("DF1");
		aaaa2.setName("A2");
		out.add(aaaa1);
		out.add(aaaa2);


		List<Content> contents = new ArrayList<>();
		List<Content> contents2 = new ArrayList<>();
		Content content1 = new Content();
		content1.setOutId("DF1");
		content1.setName("嗯1");
		content1.setId(1);

		Content content2 = new Content();
		content2.setOutId("DF1");
		content2.setName("嗯2");
		content2.setId(7);
		contents.add(content1);
		contents.add(content2);
		contents.sort(Content::compareTo);

		AssembleUtils.assembleList(out, contents, Content::getOutId, AAAA::getId, AAAA::setContents);
		System.out.println("aaaas.get() = " + out);

		List<BBB> outb = new ArrayList<>();
		BBB bbb1 = new BBB();
		bbb1.setId("DF1");
		bbb1.setName("A1");
		BBB bbb2 = new BBB();
		bbb2.setId("DF1");
		bbb2.setName("A2");
		outb.add(bbb1);
		outb.add(bbb2);

		AssembleUtils.assembleSingle(outb, contents, Content::getOutId, BBB::getId, BBB::setContent);
		outb.forEach(System.out::println);

	}


	@ToString
	@Data
	static class AAAA {

		private String id;
		private String name;
		private List<Content> contents;
	}

	@ToString
	@Data
	static class BBB {

		private String id;
		private String name;
		private Content content;
	}


	@ToString
	@Data
	static class Content implements Comparable<Content> {
		private Integer id;
		private String outId;
		private String name;



		@Override
		public int compareTo(Content o) {
			return this.id > o.id ? this.id - o.id : this.hashCode() - o.hashCode();
		}
	}

	private final Map<String, Say> cache = new ConcurrentHashMap<>();
	@Test
	public void testMap() {
		Map<String, Say> map = new HashMap<>();
		map.computeIfAbsent("uiys", Say::new).say();

		cache.computeIfAbsent("uiiys",this::getSay).say();

		cache.computeIfAbsent("uiiys", this::getSay)
		  .say();
		cache.computeIfAbsent("uiiys", this::getSay)
		  .say();
		cache.computeIfAbsent("uiiys2", this::getSay)
		  .say();
		cache.computeIfAbsent("uiiys2", this::getSay)
		  .say();
	}

	public Say getSay(String s) {
		Say say = new Say(s);
		return say;
	}

	@Data
	class Say {
		private String name;

		public Say(String name) {
			this.name = name;
		}

		public void say() {
			System.out.println("the name is " + this.name);
		}
	}


}
