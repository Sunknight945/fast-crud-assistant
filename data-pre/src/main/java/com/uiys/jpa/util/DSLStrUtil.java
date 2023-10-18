package com.uiys.jpa.util;


public class DSLStrUtil {

	public static String bLike(String str) {
		return "%" + str + "%";
	}

	public static String lLike(String str) {
		return "%" + str;
	}

	public static String rLike(String str) {
		return str + "%";
	}


}


