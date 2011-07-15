package dipper.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class PropertyUtils {
	private PropertyUtils() {
	}
	
	public static Properties loadProperties(File file) {
		Properties properties = new Properties();
		loadProperties(properties, file);
		
		return properties;
	}
	
	public static void loadProperties(Properties target, File file) {
		try {
			FileInputStream stream = new FileInputStream(file);
			target.load(new BufferedInputStream(stream));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static int getInt(Properties props, String name) {
		String val = (String)props.get(name);
		if (val == null) {
			throw new IllegalArgumentException("Property " + name + " doesn't exist.");
		}
		
		return Integer.parseInt(val);
	}
	
	public static int getInt(Properties props, String name, int defaultVal) {
		String val = (String)props.get(name);
		if (val == null) {
			return defaultVal;
		}
		
		return Integer.parseInt(val);
	}
	
	public static String getString(Properties props, String name) {
		String val = (String)props.get(name);
		if (val == null) {
			throw new IllegalArgumentException("Property " + name + " doesn't exist.");
		}
		
		return val;
	}
	
	public static String getString(Properties props, String name, String defaultVal) {
		String val = (String)props.get(name);
		if (val == null) {
			return defaultVal;
		}
		
		return val;
	}
}