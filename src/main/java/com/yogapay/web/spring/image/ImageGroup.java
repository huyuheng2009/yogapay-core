package com.yogapay.web.spring.image;

import com.yogapay.core.ErrorCodeException;
import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public class ImageGroup implements Serializable {

	private static final long serialVersionUID = 20160818L;
	private final String key;
	private final int maxLength;
	private final boolean append;
	private final List<Image> images = new ArrayList<Image>();

	public ImageGroup(String key, int maxLength, boolean append) {
		this.key = key;
		this.maxLength = maxLength;
		this.append = append;
	}

	public synchronized void add(String uri) {
		Image img = new Image();
		img.setExists(true);
		img.setUri(uri);
		images.add(img);
	}

	public synchronized void add(MultipartFile[] imgs) throws IOException, ErrorCodeException {
		if (!append) {
			images.clear();
		}
		String prefix = Long.toHexString(System.currentTimeMillis()) + "_";
		int index = 0;
		for (MultipartFile f : imgs) {
			f.getOriginalFilename();
			if (images.size() >= maxLength) {
				throw new ErrorCodeException(1, String.format("最多只能传 %d 张图", maxLength));
			}
			Image img = new Image();
			img.setFlag(prefix + index++);
			img.setUri("upload_img?_preview&flag=" + img.getFlag() + "&key=" + URLEncoder.encode(key, "UTF-8"));
			img.setContentType(f.getContentType());
			img.setBytes(f.getBytes());
			images.add(img);
		}
	}

	public synchronized Image get(String flag) {
		for (Image t : images) {
			if (flag.equals(t.getFlag())) {
				return t;
			}
		}
		return null;
	}

	public synchronized void remove(String flag) {
		for (Iterator<Image> i = images.iterator(); i.hasNext();) {
			Image next = i.next();
			if (flag.equals(next.getFlag())) {
				i.remove();
				return;
			}
		}
	}

	public synchronized List<Map<String, Object>> getUris() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (Image t : images) {
			if (t == null) {
				list.add(null);
				continue;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("uri", t.getUri());
			map.put("flag", t.getFlag());
			list.add(map);
		}
		return list;
	}

	public String getKey() {
		return key;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public boolean isAppend() {
		return append;
	}

	public synchronized List<Image> getImages() {
		return new ArrayList<Image>(images);
	}

}
