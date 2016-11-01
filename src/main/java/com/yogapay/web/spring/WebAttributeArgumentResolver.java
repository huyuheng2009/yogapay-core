package com.yogapay.web.spring;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class WebAttributeArgumentResolver implements HandlerMethodArgumentResolver {

	@Autowired
	private ServletContext sc;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		WebAttribute a = parameter.getParameterAnnotation(WebAttribute.class);
		return a != null;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		WebAttribute a = parameter.getParameterAnnotation(WebAttribute.class);
		String name = a.value();
		if (name.isEmpty()) {
			name = parameter.getParameterName();
		}
		Object value;
		HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
		if ((value = req.getAttribute(name)) != null) {
			return value;
		}
		HttpSession session = req.getSession(false);
		if (session != null && (value = session.getAttribute(name)) != null) {
			return value;
		}
		if ((value = sc.getAttribute(name)) != null) {
			return value;
		}
		return value;
	}

}
