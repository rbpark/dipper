package dipper.webapp;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dipper.webapp.common.AbstractServlet;
import dipper.webapp.common.Page;


public class IndexServlet extends AbstractServlet {
	private static final long serialVersionUID = -4688702005332106156L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		checkSession(request);

	//	response.addCookie(cookie);
		Page page = newPage(request, response, "dipper/webapp/desktop.vm");
		page.render();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
}