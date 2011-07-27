package dipper.webapp.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;


public class Page {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final VelocityEngine engine;
    private final VelocityContext context;
    private final String template;

    public Page(HttpServletRequest request,
                HttpServletResponse response,
                VelocityEngine engine,
                String template) {
    	response.setContentType("application/xhtml+xml");
        this.request = request;
        this.response = response;
        this.engine = engine;
        this.template = template;
        this.context = new VelocityContext();
        this.context.put("session", this.request.getSession(true));
        this.context.put("context", this.request.getContextPath());
    }

    public void render() {
        try {
            engine.mergeTemplate(template, "UTF-8", context, response.getWriter());
        } catch(Exception e) {
            throw new PageRenderException(e);
        }
    }

    public void add(String name, Object value) {
        context.put(name, value);
    }
}
