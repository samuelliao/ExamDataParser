import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.w3c.dom.*;
import javax.xml.parsers.*;

public class SysProperty {
	public static String SourceFolderPath;
	public static String DestinationFolderPath;
	public static int FileWaitTime;
	public static String DBConnectString;
	public static int TimerPeriod;
	public static DbUtil _dbUtil;

	private static final String LOG_PROP = "./config/log.properties";
	private static final String CONFIG_PROP = "./config/Config.xml";
	private static Logger _log = Logger.getLogger(SysProperty.class.getName());

	public static void initialDbUtil() {
		try {
			_dbUtil = new DbUtil();
		} catch (Exception ex) {
			_log.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	public static void loadConfig() {
		try {
			File f = new File(CONFIG_PROP);
			if (!f.exists())
				throw new Exception("File not exist");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(f);
			SourceFolderPath = doc.getElementsByTagName("sourcePath").item(0).getFirstChild().getNodeValue();
			DestinationFolderPath = doc.getElementsByTagName("destPath").item(0).getFirstChild().getNodeValue();
			DBConnectString = doc.getElementsByTagName("dBConnectString").item(0).getFirstChild().getNodeValue();
			FileWaitTime = Integer
					.parseInt(doc.getElementsByTagName("fileWaitTime").item(0).getFirstChild().getNodeValue());
			TimerPeriod = Integer
					.parseInt(doc.getElementsByTagName("timerPeriod").item(0).getFirstChild().getNodeValue());
		} catch (Exception ex) {
			_log.log(Level.SEVERE, "Read " + CONFIG_PROP + " file failed.", ex);
			SourceFolderPath = "";
			DestinationFolderPath = "";
			DBConnectString = "";
			FileWaitTime = 10;
			TimerPeriod = 5;
		}
	}

	public static void logInitial() {
		File folder = new File("log");
		if (!folder.exists()) {
			folder.mkdir();
		}
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(LOG_PROP));
			LogManager.getLogManager().readConfiguration(is);
			is.close();
		} catch (IOException e) {
			_log.log(Level.SEVERE, "Read " + LOG_PROP + " file failed.", e);
			System.exit(0);
		}
		//_log.info("test");
	}
}
