package com.yogapay.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.springframework.beans.factory.access.el.SimpleSpringBeanELResolver;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class Startup extends ContextLoaderListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ServletContext sc = event.getServletContext();
		super.contextInitialized(event);
		javax.servlet.jsp.JspFactory.getDefaultFactory().getJspApplicationContext(sc)
				.addELResolver(new SimpleSpringBeanELResolver(WebApplicationContextUtils.getRequiredWebApplicationContext(sc)));
		sc.setAttribute("cp", sc.getContextPath());
	}

}
