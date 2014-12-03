package com.xqsoso.base.util;

public class WebDriverUtils {

	public static void setProperty(){
		System.getProperties().setProperty("webdriver.chrome.driver", BaseUtils.getPropertyString("webdriver.chrome.driver"));
	}
	
}
