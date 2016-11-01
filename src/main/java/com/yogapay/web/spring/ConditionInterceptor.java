package com.yogapay.web.spring;

import com.yogapay.core.BeanUtils;
import com.yogapay.sql.QueryCondition;
import com.yogapay.sql.QueryMoreCondition;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class ConditionInterceptor implements HandlerInterceptor {

	private final ObjectMapper om = new ObjectMapper();
	private final BeanUtils beanUtils = new BeanUtils();
	private final Pattern p = Pattern.compile("cdt\\.(\\w+)");

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return true;
	}

	private void postHandle(HttpServletRequest request, ModelAndView modelAndView, QueryCondition cdt) throws Exception {
		{
			Map<String, String[]> map = request.getParameterMap();
			Map<String, Object> cdtObject = new HashMap<String, Object>();
			for (Map.Entry<String, String[]> e : map.entrySet()) {
				String k = e.getKey();
				if (k.startsWith("sort_")) {
					continue;
				}
				if (beanUtils.isExistsProperty(cdt, k)) {
					cdtObject.put(k, beanUtils.getProperty(cdt, k));
				}
			}

			for (Map.Entry<String, Object> e : modelAndView.getModel().entrySet()) {
				Matcher m = p.matcher(e.getKey());
				if (!m.matches()) {
					continue;
				}
				String key = m.group(1);
				if (!cdtObject.containsKey(key)) {
					cdtObject.put(key, e.getValue());
				}
			}
			request.setAttribute("cdtObject", om.writeValueAsString(cdtObject));
		}
		{
			try {
				Map<String, Object> sortObject = new HashMap<String, Object>();
				for (Field f : cdt.sortFieldMap.values()) {
					sortObject.put(f.getName(), f.get(cdt));
				}
				request.setAttribute("sortObject", om.writeValueAsString(sortObject));
			} catch (Exception ex) {
				LoggerFactory.getLogger(ConditionInterceptor.class).error(null, ex);
				request.setAttribute("sortObject", "{}");
			}
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if (modelAndView == null) {
			return;
		}
		Object cdt = modelAndView.getModel().get("cdt");
		if (cdt instanceof QueryCondition) {
			postHandle(request, modelAndView, (QueryCondition) cdt);
		} else if (cdt instanceof QueryMoreCondition) {
			QueryMoreCondition _cdt = (QueryMoreCondition) cdt;
			boolean hasMore = _cdt.isHasMore();
			if (hasMore) {
				response.setHeader("HasMore", _cdt.getPage() + "/" + _cdt.getPageSize());
			}
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}

}
