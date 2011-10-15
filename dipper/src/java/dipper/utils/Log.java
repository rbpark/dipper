package dipper.utils;

import java.util.LinkedList;

/**
 * Created to display Logs in the internal console or any external 
 * logging source.
 * 
 * @author rpark
 *
 */
public class Log { 
	public enum Level {
		ERROR,
		INFO
	}
	
	public static void error(String msg) {
		instance.internalLog(msg, Level.ERROR);
		System.err.println(msg);
	}
	
	public static void info(String msg) {
		instance.internalLog(msg, Level.INFO);
		System.out.println(msg);
	}
	
	private static Log instance = new Log();

	private Log() {
	}
	
	private void internalLog(String msg, Level level) {
		
	}
}