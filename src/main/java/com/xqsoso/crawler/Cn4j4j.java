package com.xqsoso.crawler;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import com.xqsoso.base.Constants;
import com.xqsoso.base.util.ImagesUtil;

/**
 * 一键下载www.4j4j.cn的壁纸
 * @author yejq
 */
public class Cn4j4j implements PageProcessor {

	static Logger logger = LoggerFactory.getLogger(Cn4j4j.class);
	private final static String PATH_BASE = "G:/img/";

	public void process(Page page) {
		String url = page.getUrl().toString();
		Html html = page.getHtml();
		if (url.startsWith("http://www.4j4j.cn/zmbz/mnbz")) {
			List<String> links = html.xpath("//*[@class=\"mimglist\"]/li/p/a/@href").all();
			for (String link : links) {
				logger.info("link:{}", link);
				page.addTargetRequest(link);
			}
		}else {
			String title = html.xpath("//*[@class=\"ulBigPic\"]/li[1]/img/@title").toString();
			logger.info("title:{}", title);
			String path = PATH_BASE + title + "/";
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			List<String> imgs = html.xpath("//*[@class=\"ulBigPic\"]/li/img/@src").all();
			for (String img : imgs) {
				String imgPath = path + getImgName(img);
				File file2 = new File(imgPath);
				if (file2.exists()) {
					logger.info("down {} to {}, skip", img, imgPath);
				}else {
					boolean isSuccess = ImagesUtil.down(imgPath, img);
					logger.info("down {} to {}, {}", img, imgPath, isSuccess);
				}
			}
		}
	}
	
	public static String getImgName(String img){
		String result = new Date().getTime() + "";
		Pattern pattern = Pattern.compile("http://pic.4j4j.cn/upload/pic/(.*?)/(.*?).jpg");
		Matcher matcher = pattern.matcher(img);
		if (matcher.find()) {
			result = matcher.group(1) + matcher.group(2);
		}
		return result + ".jpg";
	}
	
	@Test
	public void getImgNameTest(){
		System.out.println(getImgName("http://pic.4j4j.cn/upload/pic/20140402/f6ad425521.jpg"));
	}

	public Site getSite() {
		return Constants.getSite();
	}

	public static void main(String[] args) {
		// int page = 77;
		int page = 77;
		for (int i = 3; i <= page; i++) {
			try {
				String url = "http://www.4j4j.cn/zmbz/mnbz%s.html";
				url = String.format(url, i);
				logger.info("this url:{}", url);
				Spider.create(new Cn4j4j()).addUrl(url).run();
				Thread.sleep(3000);
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("now page is:{}", i);
				throw new RuntimeException(e.getMessage());
			}
		}
	}
}
