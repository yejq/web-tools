package com.xqsoso.base.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class ImagesUtil {

	public static boolean down(String destPath, String origUrl) {
		boolean isSuccess = false;
		OutputStream os = null;
		try {
			URL url = new URL(origUrl);
			InputStream is = url.openStream();
			os = new FileOutputStream(destPath);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			isSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return isSuccess;
	}

}
