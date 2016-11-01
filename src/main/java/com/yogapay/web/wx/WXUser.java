package com.yogapay.web.wx;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public class WXUser implements Serializable {

	private static final long serialVersionUID = 20151022L;
	private static final Pattern P_HEADIMAGEURL = Pattern.compile("^(.*)/\\d+$");
	public String openid;
	public String nickname;
	public int sex;
	public String language;
	public String country;
	public String province;
	public String city;
	public String headimgurl;
	public String[] privilege;
	public String unionid;
	//
	public int errcode;
	public String errmsg;
	//
	public String userinfo;

	public String getHeadimgurl_132() {
		if (StringUtils.isBlank(headimgurl)) {
			return "";
		}
		Matcher m = P_HEADIMAGEURL.matcher(headimgurl);
		return m.matches() ? m.replaceAll("$1/132") : headimgurl;
	}

	public String getSexText() {
		switch (sex) {
			case 1:
				return "男";
			case 2:
				return "女";
			default:
				return "";
		}
	}

	public String getCity2() {
		List<String> list = new ArrayList<String>(2);
		if (!StringUtils.isBlank(province)) {
			list.add(province);
		}
		if (!StringUtils.isBlank(city)) {
			list.add(city);
		}
		return StringUtils.join(list, '-');
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String[] getPrivilege() {
		return privilege;
	}

	public void setPrivilege(String[] privilege) {
		this.privilege = privilege;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

}
