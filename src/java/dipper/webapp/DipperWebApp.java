package dipper.webapp;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.Log4JLogChute;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.ServletHolder;

import dipper.app.DipperApp;
import dipper.utils.PropertyUtils;

public class DipperWebApp {
	public static final String APP_SERVLET_CONTEXT_KEY = "dipper-web-app";
	
	private static final int DEFAULT_PORT = 9021;
	private static String staticDir = "./web";
	private Logger logger = Logger.getLogger(DipperWebApp.class);

	private VelocityEngine velocityEngine;
	private DipperApp dipperApp;
	
	public static void main(String[] args) {
		Properties props = null;
		if (args.length == 0) {
			System.err
					.println("The only argument must be the config property file.");
			props = new Properties();
		} else {
			props = PropertyUtils.loadProperties(new File(args[0]));
			if (args.length == 2) {
				staticDir = args[1];
			}
		}

		new DipperWebApp(props);
	}

	public DipperWebApp(Properties props) {
		int portNumber = PropertyUtils.getInt(props, "port", DEFAULT_PORT);
		logger.info("Binding to port " + portNumber);

		dipperApp = new DipperApp();
		
		final Server server = new Server(portNumber);
		Context root = new Context(server, "/", Context.SESSIONS);
		root.setResourceBase(staticDir);
		root.addServlet(new ServletHolder(new IndexServlet()), "/");
		root.addServlet(new ServletHolder(new DefaultServlet()), "/static/*");
		root.setAttribute(APP_SERVLET_CONTEXT_KEY, this);

		try {
			server.start();
			velocityEngine = configureVelocityEngine(false);
		} catch (Exception e) {
			logger.error("Error starting server.", e);
			throw new RuntimeException(e);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.info("Shutting down.");
				try {
					server.stop();
					shutdown();
					server.destroy();
				} catch (Exception e) {
					logger.error("Error occured in closing", e);
				}
			}
		});
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}
	
	public DipperApp getDipperApp() {
		return dipperApp;
	}
	
	private VelocityEngine configureVelocityEngine(boolean devMode)
			throws Exception {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty("resource.loader", "classpath");
		engine.setProperty("classpath.resource.loader.class",
				ClasspathResourceLoader.class.getName());
		engine.setProperty("classpath.resource.loader.cache", !devMode);
		engine.setProperty(
				"classpath.resource.loader.modificationCheckInterval", 5L);
		engine.setProperty("resource.manager.logwhenfound", false);
		engine.setProperty("input.encoding", "UTF-8");
		engine.setProperty("output.encoding", "UTF-8");
		engine.setProperty("directive.foreach.counter.name", "idx");
		engine.setProperty("directive.foreach.counter.initial.value", 0);
		// engine.setProperty("runtime.references.strict", true);
		engine.setProperty("directive.set.null.allowed", true);
		engine.setProperty("resource.manager.logwhenfound", false);
		engine.setProperty("velocimacro.permissions.allow.inline", true);
		engine.setProperty("velocimacro.library.autoreload", devMode);
		engine.setProperty(
				"velocimacro.permissions.allow.inline.to.replace.global", true);
		engine.setProperty("velocimacro.context.localscope", true);
		engine.setProperty("velocimacro.arguments.strict", true);
		engine.setProperty("runtime.log.invalid.references", devMode);
		engine.setProperty("runtime.log.logsystem.class", Log4JLogChute.class);
		engine.setProperty("runtime.log.logsystem.log4j.logger", Logger
				.getLogger("org.apache.velocity.Logger"));
		engine.setProperty("parser.pool.size", 3);

		engine.init();
		return engine;
	}

	public void shutdown() {

	}
}