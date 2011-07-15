package dipper.webapp;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dipper.webapp.DipperWebApp;

public abstract class AbstractServlet extends HttpServlet {
	private static final long serialVersionUID = 2254914605389316822L;

	private DipperWebApp app;

	@Override
	public void init(ServletConfig config) throws ServletException {
		app = (DipperWebApp) config.getServletContext().getAttribute(
				DipperWebApp.APP_SERVLET_CONTEXT_KEY);

		if (app == null) {
			throw new IllegalStateException("No dipper app defined.");
		}
	}

	public boolean hasParam(HttpServletRequest request, String param) {
		return request.getParameter(param) != null;
	}

	public String getParam(HttpServletRequest request, String name) throws ServletException {
		String param = request.getParameter(name);
		if (param == null || param.equals("")) {
			throw new ServletException("Missing required parameter '" + name+ "'.");
		} else {
			return param;
		}
	}

	public int getIntParam(HttpServletRequest request, String name) throws ServletException {
		String p = getParam(request, name);
		return Integer.parseInt(p);
	}

	protected void setSessionValue(HttpServletRequest request, String key, Object value) {
		request.getSession(true).setAttribute(key, value);
	}

	@SuppressWarnings( { "unchecked" })
	protected void addSessionValue(HttpServletRequest request, String key, Object value) {
		List list = (List) request.getSession(true).getAttribute(key);
		
		if (list == null) {
			list = new ArrayList();
		}
		
		list.add(value);
		request.getSession(true).setAttribute(key, list);
	}

	protected void addError(HttpServletRequest request, String message) {
		addSessionValue(request, "errors", message);
	}

	protected void addMessage(HttpServletRequest request, String message) {
		addSessionValue(request, "messages", message);
	}

	protected Page newPage(HttpServletRequest req, HttpServletResponse resp, String template) {
		Page page = new Page(req, resp, app.getVelocityEngine(), template);
		return page;
	}
}