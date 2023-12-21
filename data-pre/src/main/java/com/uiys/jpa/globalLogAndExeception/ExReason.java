package com.uiys.jpa.globalLogAndExeception;

import cn.hutool.json.JSONUtil;
import java.io.Serializable;

/**
 * @author uiys
 */
public class ExReason implements Serializable {

	private String str;
	private Object eR;


	private ExReason(Object er) {
		this.str = "错误原因";
		this.eR = JSONUtil.parse(er);
	}


	public static String in(Object eR){
		return new ExReason(eR).toString();
	}

	@Override
	public String toString() {
		return "ExReason{" +
		  "str='" + str + '\'' +
		  ", eR=" + eR +
		  '}';
	}
}


