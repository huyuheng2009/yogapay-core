package com.yogapay.core;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtils {

	public static String getUrl(HttpServletRequest request, String path) throws Exception {
		String url = new URL(request.getRequestURL().toString()).toURI().resolve(path).toURL().toString();
		return url;
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Real-IP");
		if ((ip == null) || (ip.length() == 0)
				|| "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if ((ip == null) || (ip.length() == 0)
				|| "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if ((ip == null) || (ip.length() == 0)
				|| "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static String sign(Map<String, Object> param, String salt) {
		if (salt == null) {
			return null;
		}
		Map<String, Object> map = (param instanceof NavigableMap) ? param : new TreeMap<String, Object>(param);
		try {
			Logger log = LoggerFactory.getLogger(WebUtils.class);
			if (log.isDebugEnabled()) {
				log.debug("sign: " + map.keySet() + " salt:" + salt);
			}
			MessageDigest md = MessageDigest.getInstance("MD5");
			for (Object v : map.values()) {
				if (v.getClass().isArray()) {
					for (int i = 0, n = Array.getLength(v); i < n; i++) {
						md.update(Array.get(v, i).toString().getBytes("UTF-8"));
					}
				} else {
					md.update(v.toString().getBytes("UTF-8"));
				}
			}
			md.update(salt.getBytes("UTF-8"));
			String sign = Hex.encodeHexString(md.digest());
			return sign;
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static boolean checkSign(HttpServletRequest request, String salt) {
		if (salt == null) {
			return true;
		}
		String sign = request.getParameter("sign");
		if (sign == null) {
			return false;
		}
		Map<String, Object> param = new TreeMap<String, Object>(request.getParameterMap());
		param.remove("sign");
		String _sign = sign(param, salt);
		if (sign.equalsIgnoreCase(_sign)) {
			return true;
		} else {
			Logger log = LoggerFactory.getLogger(WebUtils.class);
			if (log.isDebugEnabled()) {
				log.debug(String.format("checkSign\r\nremote: %s\r\nlocale: %s", sign, _sign));
			}
			return false;
		}
	}
}
