package com.xqsoso.crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xqsoso.base.util.FileUtils;
import com.xqsoso.base.util.WebDriverUtils;

/**
 * 百度网盘工具：批量保存分享到我的网盘
 * @author yejq
 */
public class PanBaiduBatchSave {

	public static void main(String[] args) {
		PanBaiduBatchSave baiduBatchSave = new PanBaiduBatchSave();
		baiduBatchSave.save();
	}
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	//需要修改的常量
	private final static String URL_TARGET = "http://pan.baidu.com/share/home?uk=741026074#category/type=0"; //百度分享资源链接
	private final static String USER_NAME = "xxxx";
	private final static String USER_PASSWORD = "xxxx";
	private final static String PATH = "开源力量"; //要保存到的路径
	
	
	private final static String DONE_PATH = "/src/main/java/com/xqsoso/crawler/PanBaiduBatchSaveDone.txt";
	
	private static List<String> skip_files = null; //不保存的文件
	int timeout = 5; //多少秒超时
	
	public void save(){
		
		init();
		
		WebDriverUtils.setProperty();
		WebDriver driver = new ChromeDriver();
		
		try {
			driver.manage().window().maximize();
			driver.manage().timeouts().pageLoadTimeout(timeout, TimeUnit.SECONDS); // 页面加载时的超时时间
			driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS); // 识别元素时的超时时间
			driver.manage().timeouts().setScriptTimeout(timeout, TimeUnit.SECONDS); // 异步脚本的超时时间
			driver.get(URL_TARGET);
			
			Thread.sleep(5000);
			
			//未登录
			try {
				WebElement loginElement = driver.findElement(By.id("netdisk_login"));
				if (loginElement != null) {
					logger.debug("没有登陆啊！");
					loginElement.click();
					
					driver.findElement(By.id("TANGRAM__PSP_8__userName")).sendKeys(USER_NAME);
					driver.findElement(By.id("TANGRAM__PSP_8__password")).sendKeys(USER_PASSWORD);
					
					Thread.sleep(1000);
					driver.findElement(By.id("TANGRAM__PSP_8__submit")).click();
					logger.debug("登陆成功！");
				}
			} catch (Exception e) {
			}
			
			Thread.sleep(5000);
			
			boolean hasNextPage = false;
			
			do {
				List<WebElement> fileWebElements = driver.findElements(By.className("file-handler"));
				for (WebElement fileWebElement : fileWebElements) {
					String fileName = fileWebElement.getText();
					boolean isHas = false;
					for (String skip_file : skip_files) {
						if (fileName.equals(skip_file)) {
							isHas = true;
							logger.debug("已处理：{}", fileName);
							break;
						}
					}
					if (isHas) {
						continue;
					}
					String fileHref = fileWebElement.getAttribute("href");
					logger.debug("待处理：fileName:{},fileHref:{}", fileName, fileHref);
					
					driver = doSaveOne(driver, fileHref);
					skip_files.add(fileName);
				}

				WebElement nextPageElement = driver.findElement(By.className("page-next"));
				if (nextPageElement.getAttribute("class").indexOf("disabled") == -1) {
					logger.debug("处理下一页....");
					nextPageElement.click();
					hasNextPage = true;
					
					Thread.sleep(5000);
				}else {
					hasNextPage = false;
					logger.debug("没有下一页了。。。");
				}
			} while (hasNextPage);
			
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			saveDone();
			driver.close();
			driver.quit();
		}
	}

	private void saveDone() {
		try {
			String content = null;
			for (int i = 0; i < skip_files.size(); i++) {
				String string = skip_files.get(i);
				if (StringUtils.isNotBlank(string) && !"null".equals(string)) {
					content += string;
					if (i < skip_files.size() - 1) {
						content += ",";
					}
				}
			}
			File pathFile = new File("");
			String path = pathFile.getAbsolutePath() + DONE_PATH;
			@SuppressWarnings("resource")
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write(content);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
		skip_files = new ArrayList<String>();
		try {
			String content = FileUtils.readFile(DONE_PATH);
			String[] contentArr = content.split(",");
			for (String string : contentArr) {
				if (StringUtils.isNotBlank(string)) {
					skip_files.add(string);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private WebDriver doSaveOne(WebDriver driver, String fileHref) throws InterruptedException {
		((JavascriptExecutor) driver).executeScript("window.open('" + fileHref + "');");
		String currentWindow = driver.getWindowHandle();

		Iterator<String> windowHandleIterator = driver.getWindowHandles().iterator();
		while (windowHandleIterator.hasNext()) {
			String handel = windowHandleIterator.next();
			if (currentWindow.equals(handel)) {
				continue;
			}
			WebDriver fileDriver = driver.switchTo().window(handel);

			Thread.sleep(1000);
			
			try {
				WebElement notfoundElement = driver.findElement(By.id("share_nofound_des"));
				if (notfoundElement != null) {
					logger.debug(notfoundElement.getText());
					fileDriver.close();
					Thread.sleep(1000);
					driver = driver.switchTo().window(currentWindow);
					break;
				}
			} catch (Exception e) {
			}
			
			try {
				WebElement icoElement = fileDriver.findElements(By.className("chk-ico")).get(0);
				icoElement.click();
				
				Thread.sleep(2000);
				
				fileDriver.findElement(By.id("emphasizeButton")).click();
				
				Thread.sleep(5000);
			} catch (Exception e) {
				fileDriver.findElement(By.className("okay")).click();
			}
			
			try {
				fileDriver.findElement(By.className("save-path-item")).click();
			} catch (Exception e) {
				logger.debug("没有最新保存路径~~~~~");
				
				WebElement pathElement = null;
				List<WebElement> treeElements = fileDriver.findElements(By.className("treeview-txt"));
				for (WebElement treeElement : treeElements) {
					if (treeElement.getText().equals(PATH)) {
						pathElement = treeElement;
						break;
					}
				}
				if (pathElement == null) {
					logger.debug("找不到保存路径[{}], 我新建一个吧。", PATH);
					fileDriver.findElement(By.id("_disk_id_13")).click();
					Thread.sleep(1000);
					WebElement newElement = fileDriver.findElement(By.className("_disk_id_17"));
					newElement.clear();
					Thread.sleep(1000);
					newElement.sendKeys(PATH);
					Thread.sleep(1000);
					fileDriver.findElement(By.className("_disk_id_15")).click();
					logger.debug("新建[{}]成功！", PATH);
				}else {
					((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", pathElement);
					Thread.sleep(1000);
					pathElement.click();
				}
			}
			
			Thread.sleep(1000);
			
			try {
				fileDriver.findElement(By.id("_disk_id_12")).click();
			} catch (Exception e) {
				fileDriver.findElement(By.id("_disk_id_6")).click();
			}
			
			Thread.sleep(5000);
			
			String msg = fileDriver.findElement(By.id("_disk_id_1")).getText();
			
			logger.debug("{}", msg);

			fileDriver.close();
			Thread.sleep(1000);
			driver = driver.switchTo().window(currentWindow);
			break;
		}
		return driver;
	}
	
	
	
}
