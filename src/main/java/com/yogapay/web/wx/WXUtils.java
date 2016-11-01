package com.yogapay.web.wx;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.commons.io.IOUtils;

public class WXUtils {

	public static String userinfo(String access_token, String openid) throws IOException {
		StringBuilder buff = new StringBuilder("https://api.weixin.qq.com/sns/userinfo");
		buff.append("?access_token=").append(URLEncoder.encode(access_token, "UTF-8"));
		buff.append("&openid=").append(URLEncoder.encode(openid, "UTF-8"));
		buff.append("&lang=zh_CN");
		return IOUtils.toString(new URL(buff.toString()), "UTF-8");
	}
}
