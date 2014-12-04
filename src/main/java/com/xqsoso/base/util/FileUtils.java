package com.xqsoso.base.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileUtils {
	
	static Logger logger = LoggerFactory.getLogger(FileUtils.class);

	public static String readFile(String path) throws Exception{
		File file = new File("");
		path = file.getAbsolutePath() + path;
		StringBuilder builder = new StringBuilder();
		String line = null;
		BufferedReader reader = new BufferedReader(new FileReader(path));
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		reader.close();
		
		//logger.debug(builder.toString());
		
		return builder.toString();
	}
}
