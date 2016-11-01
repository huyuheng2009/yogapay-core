package com.yogapay.web.spring;

import com.yogapay.core.Result;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

public class UploadImageController {

	protected int maxLength = 16;

	private static String key(HttpServletRequest req) {
		String path = req.getRequestURI();
		path = path.substring(0, path.lastIndexOf('/') + 1);
		return "uploads_imgs#" + path;
	}

	public static List<PreviewImage> init(HttpServletRequest req) {
		List<PreviewImage> imgs = new ArrayList<PreviewImage>();
		req.getSession().setAttribute(key(req), imgs);
		return imgs;
	}

	public static List<PreviewImage> init(HttpServletRequest req, String uri) {
		List<PreviewImage> imgs = init(req);
		if (uri != null) {
			PreviewImage img = new PreviewImage();
			img.setUri(uri);
			img.setExists(true);
			imgs.add(img);
		}
		return imgs;
	}

	public static List<PreviewImage> add(HttpServletRequest req, List<String> uris) {
		List<PreviewImage> imgs = get(req);
		if (!CollectionUtils.isEmpty(uris)) {
			for (String uri : uris) {
				if (uri == null) {
					imgs.add(null);
				} else {
					PreviewImage img = new PreviewImage();
					img.setUri(uri);
					img.setExists(true);
					imgs.add(img);
				}
			}
		}
		return imgs;
	}

	public static List<PreviewImage> get(HttpServletRequest req) {
		return (List<PreviewImage>) req.getSession().getAttribute(key(req));
	}

	protected static List<String> createUploadImgURIs(List<PreviewImage> imgs) {
		List<String> list = new ArrayList<String>();
		int index = 0;
		for (PreviewImage t : imgs) {
			if (t == null) {
				list.add(null);
			} else if (t.isExists()) {
				list.add(t.getUri());
			} else {
				t.setFlag(Long.toHexString(System.currentTimeMillis()) + "_" + index);
				t.setUri("upload_img?_preview&flag=" + t.getFlag());
				list.add(t.getUri());
			}
			index++;
		}
		return list;
	}

	private void addImage(List<PreviewImage> imgs, MultipartFile f, int index) throws IOException {
		PreviewImage p = null;
		if (f != null) {
			p = new PreviewImage();
			p.setContentType(f.getContentType());
			p.setBytes(f.getBytes());
		}
		if (index >= 0) {
			imgs.set(index, p);
		} else {
			imgs.add(p);
		}
	}

	@RequestMapping(value = "upload_img", headers = "Content-Type=multipart/form-data")
	@ResponseBody
	public Object upload_img(
			@RequestParam(required = false) int[] index,
			@RequestParam(required = false, defaultValue = "0") boolean override,
			@RequestParam("img") MultipartFile[] _imgs,
			HttpServletRequest req) {
		Result ret = new Result();
		a:
		try {
			List<PreviewImage> imgs = get(req);
			if (imgs == null) {
				ret.setErrorCode(2);
				ret.setMessage("初始化错误，请重新登录。");
				break a;
			}
			if (index == null) {
				index = new int[Math.min(maxLength, _imgs.length)];
				for (int i = 0; i < index.length; i++) {
					index[i] = i;
				}
			}
//			if (imgs.size() >= maxLength) {
//				ret.setErrorCode(2);
//				ret.setMessage("最多只能传" + maxLength + "张图片。");
//				break a;
//			}
			int min = index[0], max = min;
			for (int i = 1; i < index.length; i++) {
				min = Math.min(min, index[i]);
				max = Math.max(min, index[i]);
			}
			if (min < 0 || max >= maxLength) {
				ret.setErrorCode(2);
				ret.setMessage("图片索引最大只能是" + maxLength);
				break a;
			}
			for (int i = 0, n = max + 1 - imgs.size(); i < n; i++) {
				imgs.add(null);
			}
			if ((index.length == 1 && index[0] == 0) || override) {
				for (int i = 0; i < index.length; i++) {
					if (i >= _imgs.length) {
						addImage(imgs, null, index[i]);
					} else {
						addImage(imgs, _imgs[i], index[i]);
					}
				}
			} else {
				for (int i = 0, i2 = 0; i < index.length; i++) {
					PreviewImage t = imgs.get(index[i]);
					if (t == null) {
						if (i2 >= _imgs.length) {
							addImage(imgs, null, index[i]);
						} else {
							addImage(imgs, _imgs[i2++], index[i]);
						}
					}
				}
			}
			ret.setValue(createUploadImgURIs(imgs));
		} catch (IOException ex) {
			ret.setErrorCode(1);
			ret.setValue("上传出错");
			LogFactory.getLog(getClass()).error(null, ex);
		}
		return ret;
	}

	@RequestMapping(value = "upload_img", params = "_preview")
	public void upload_img(
			@RequestParam String flag,
			HttpServletResponse res, HttpServletRequest req) throws IOException {
		List<PreviewImage> imgs = get(req);
		if (imgs == null) {
			return;
		}
		PreviewImage p = null;
		for (PreviewImage t : imgs) {
			if (t != null && flag.equals(t.getFlag())) {
				p = t;
				break;
			}
		}
		if (p == null) {
			return;
		}
		if (p.getContentType() != null) {
			res.setContentType(p.getContentType());
			res.setContentLength(p.getBytes().length);
		}
		ServletOutputStream out = res.getOutputStream();
		try {
			out.write(p.getBytes());
			out.flush();
		} finally {
			out.close();
		}
	}

	@RequestMapping(value = "upload_img", params = "_del")
	@ResponseBody
	public Object upload_img_del(
			@RequestParam int index,
			@RequestParam(defaultValue = "0") boolean keep,
			HttpServletResponse res, HttpServletRequest req) throws IOException {
		Result ret = new Result();
		List<PreviewImage> imgs = get(req);
		if (imgs == null) {
			ret.setValue(new String[0]);
		} else {
			if (index >= 0 && index < imgs.size()) {
				if (keep) {
					imgs.set(index, null);
				} else {
					imgs.remove(index);
				}
			}
			ret.setValue(createUploadImgURIs(imgs));
		}
		return ret;
	}
}
