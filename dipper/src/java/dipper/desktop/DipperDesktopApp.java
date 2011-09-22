package dipper.desktop;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

public class DipperDesktopApp {
	private static final String CONFIG_FILE_DIR = "./conf";
	private static final String CONFIG_FILE = "dipper-desktop.conf";
	private static Logger logger = Logger.getLogger(DipperDesktopApp.class);
	private static File propertiesFile;

	public static void main(String[] args) {
		OptionParser parser = new OptionParser();
		OptionSpec<String> confDirOpt = parser.accepts("conf")
				.withRequiredArg().ofType(String.class).defaultsTo(
						CONFIG_FILE_DIR);

		OptionSet options = parser.parse(args);

		String confDirStr = options.valueOf(confDirOpt);
		File confDir = new File(confDirStr);
		if (!confDir.exists()) {
			logger.error("Conf directory " + confDir.getAbsolutePath()
					+ " doesn't exist.");
			System.exit(-1);
		}

		Properties props = new Properties();
		propertiesFile = new File(confDir, CONFIG_FILE);
		if (!propertiesFile.exists()) {
			logger.error("Conf file " + propertiesFile.getAbsolutePath()
					+ " doesn't exist.");
		}
		else {
			try {
				props.load(new BufferedInputStream(new FileInputStream(
						propertiesFile)));
			} catch (IOException e) {
				logger.error("Error reading properties file "
						+ propertiesFile.getAbsolutePath() + ". " + e.getMessage());
				System.exit(-1);
			}
		}

		final DipperDesktopApp desktop = new DipperDesktopApp(props);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.info("Saving settings");

				Properties props = new Properties();
				desktop.fillProperties(props);

				try {
					props.store(new BufferedOutputStream(new FileOutputStream(
							propertiesFile)), "Dipper Application Setting");					
				} catch (IOException e) {
					logger.error("Could not write to file " + propertiesFile);
				}
			}
		});
	}

	private DipperFrame frame;

	public DipperDesktopApp(Properties props) {
		frame = new DipperFrame(props);
	}

	public void fillProperties(Properties props) {
		frame.fillProperties(props);
	}
}