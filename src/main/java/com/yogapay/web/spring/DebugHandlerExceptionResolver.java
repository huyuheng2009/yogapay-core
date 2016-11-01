package com.yogapay.web.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

public class DebugHandlerExceptionResolver extends DefaultHandlerExceptionResolver {

	private static final Logger LOG = LoggerFactory.getLogger(DebugHandlerExceptionResolver.class);

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(null, ex);
		}
		return super.doResolveException(request, response, handler, ex);
	}

}
