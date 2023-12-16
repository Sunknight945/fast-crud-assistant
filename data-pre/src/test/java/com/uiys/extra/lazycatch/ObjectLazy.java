package com.uiys.extra.lazycatch;

import com.uiys.extra.lazycatch.annotation.LazyCatch;
import java.util.List;
import lombok.Data;

/**
 * @author uiys
 */
@Data
public class ObjectLazy {

	private String linkId;

	@LazyCatch(loader = "#{@linkOneService.findLinkOneByLinkId(linkId)}")
	private LinkObjOne linkObjOne;

	@LazyCatch(loader = "#{@linkTwoService.findLinkTwoByLinkId(linkId)}")
	private LinkObjTwo linkObjTwo;

	@LazyCatch(loader = "#{@linkOneService.findLinkOnesByLinkId(linkObjOne.linkId)}")
	private List<LinkObjOne> linkObjOnes;

}


