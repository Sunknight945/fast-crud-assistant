package com.uiys.extra.lazycatch;

import org.springframework.stereotype.Service;

@Service
public class LinkTwoService {

	public LinkObjTwo findLinkTwoByLinkId(String linkId) {
		LinkObjTwo linkObjTwo = new LinkObjTwo();
		linkObjTwo.setLinkId(linkId);
		linkObjTwo.setName("linkId:"+linkId);
		return linkObjTwo;
	}

}


