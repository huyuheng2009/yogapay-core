package com.yogapay.web.spring.image;

import com.yogapay.core.ErrorCodeException;
import com.yogapay.core.Result;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

public class UploadImageController {

	public static final String EMPTY_IMAGE_DATA = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mNgAAIAAAUAAen63NgAAAAASUVORK5CYII=";
	public static final String IMAGE_ACCEPT = ".jpg, .png, .gif, .bmp";

	private static String pathKey(HttpServletRequest req) {
		String path = req.getRequestURI();
		path = path.substring(0, path.lastIndexOf('/') + 1);
		return "uploads_imgs#" + path;
	}

	public void init(HttpServletRequest req, ImageGroup imageGroup) {
		final HttpSession session = req.getSession();
		synchronized (session) {
			String pathKey = pathKey(req);
			Map<String, ImageGroup> map = (Map<String, ImageGroup>) session.getAttribute(pathKey);
			if (map == null) {
				map = new HashMap<String, ImageGroup>();
				session.setAttribute(pathKey, map);
			}
			map.put(imageGroup.getKey(), imageGroup);
		}
	}

	public ImageGroup get(HttpServletRequest req, String key) {
		final HttpSession session = req.getSession();
		synchronized (session) {
			String pathKey = pathKey(req);
			Map<String, ImageGroup> map = (Map<String, ImageGroup>) session.getAttribute(pathKey);
			if (map == null) {
				return null;
			}
			return map.get(key);
		}
	}

	public void remove(HttpServletRequest req, String key) {
		final HttpSession session = req.getSession();
		synchronized (session) {
			String pathKey = pathKey(req);
			Map<String, ImageGroup> map = (Map<String, ImageGroup>) session.getAttribute(pathKey);
			if (map != null) {
				map.remove(key);
			}
		}
	}

	@RequestMapping(value = "upload_img", headers = "Content-Type=multipart/form-data")
	@ResponseBody
	public Object upload_img(
			@RequestParam String key,
			@RequestParam("img") MultipartFile[] _imgs,
			HttpServletRequest req) {
		Result ret = new Result();
		a:
		try {
			ImageGroup imageGroup = get(req, key);
			if (imageGroup == null) {
				ret.setErrorCode(2);
				ret.setMessage("尚未初始化，请重试。");
				break a;
			}
			try {
				imageGroup.add(_imgs);
			} catch (ErrorCodeException ex) {
				ret.setErrorCode(ex.getErrorCode() + 2);
				ret.setMessage(ex.getMessage());
				if (ex.getErrorCode() == 1) {
					ret.setValue(imageGroup.getUris());
				}
			}
			ret.setValue(imageGroup.getUris());
		} catch (IOException ex) {
			ret.setErrorCode(1);
			ret.setMessage("上传出错");
			LogFactory.getLog(getClass()).error(null, ex);
		}
		return ret;
	}

	@RequestMapping(value = "upload_img", params = "_preview")
	public void upload_img(
			@RequestParam String key,
			@RequestParam String flag,
			HttpServletRequest req, HttpServletResponse res) throws IOException {
		ImageGroup group = get(req, key);
		if (group == null) {
			return;
		}
		Image img = group.get(flag);
		if (img == null) {
			return;
		}
		if (img.getContentType() != null) {
			res.setContentType(img.getContentType());
		}
		byte[] bytes = img.getBytes();
		res.setContentLength(bytes.length);
		ServletOutputStream out = res.getOutputStream();
		try {
			out.write(bytes);
			out.flush();
		} finally {
			out.close();
		}
	}

	@RequestMapping(value = "upload_img", params = "_del")
	@ResponseBody
	public Object upload_img_del(
			@RequestParam String key,
			@RequestParam String flag,
			HttpServletResponse res, HttpServletRequest req) throws IOException {
		Result ret = new Result();
		ImageGroup group = get(req, key);
		if (group == null) {
			ret.setValue(new String[]{});
		} else {
			group.remove(flag);
			ret.setValue(group.getUris());
		}
		return ret;
	}
}
