package com.xqsoso.base;

import java.lang.reflect.Field;

import us.codecraft.webmagic.Site;

public class Constants {

	public static final String Spider_UserAgent1 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36";
	public static final String Spider_UserAgent2 = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.137 Safari/537.36 LBBROWSER";
	public static final String Spider_UserAgent3 = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)";
	public static Site site = null;
	
	@SuppressWarnings({ "static-access", "rawtypes" })
	public static Site getSite(){
		if (site == null) {
			try {
				int n = (int)(Math.random() * 3) + 1;
				String name = "Spider_UserAgent"+n;
				Class clazz = Constants.class;
				Field field = clazz.getField(name);
				Object object = field.get(clazz);
				site = site.me().setUserAgent(object.toString());
			} catch (Exception e) {
				site = site.me().setUserAgent(Spider_UserAgent2);
			}
		}
		return site;
	}
	
}
