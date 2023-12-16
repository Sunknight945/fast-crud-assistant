package com.uiys.extra.lazycatch;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * @author uiys
 */
@Service
public class LinkOneService {

	public LinkObjOne findLinkOneByLinkId(String linkId) {
		LinkObjOne linkObjOne = new LinkObjOne();
		linkObjOne.setLinkId(linkId);
		linkObjOne.setName("linkId:" + linkId);
		return linkObjOne;
	}

	public List<LinkObjOne> findLinkOnesByLinkId(String linkId) {
		List<LinkObjOne> linkObjOnes = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			LinkObjOne linkObjOne = new LinkObjOne();
			linkObjOne.setLinkId(linkId+"uiys");
			linkObjOne.setName("linkId:" + linkId);
			linkObjOnes.add(linkObjOne);
		}
		return linkObjOnes;
	}

}


