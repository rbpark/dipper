package dipper.workspace;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import dipper.utils.Log;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class WorkspaceRegistry {
	public static WorkspaceRegistry instance = new WorkspaceRegistry();
	private static String WORKSPACE = "workspace";
	private static String NAME = "name";
	private static String VERSION = "version";
	private static String DESCRIPTION = "description";
	private static String LIBRARY = "library";
	private static String PATH = "path";
	private static String XML_FILE_NAME = "workspace.xml";
	private HashMap<String, WorkspaceEntry> registry = new HashMap<String, WorkspaceEntry>();
	
	private WorkspaceRegistry() {		
	}
	
	public static WorkspaceRegistry get() {
		return instance;
	}
	
	public void registerWorkspace(String directory) {
		File dir = new File(directory);
		JarFilter filter = new JarFilter();
		
		if (!dir.exists()) {
			Log.error("Plugin dir " + dir.toString() + " doesn't exist.");
		}
		else {
			for (File file : dir.listFiles()) {
				if (!file.isDirectory()) {
					continue;
				}
				
				File workspaceFile = new File(file, XML_FILE_NAME);
				if (workspaceFile.exists()) {
					Log.info("Loading from workflow " + workspaceFile.toString());
					
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					try {
						DocumentBuilder db = dbf.newDocumentBuilder();
						Document doc = db.parse(workspaceFile);
						
						Element workspace = doc.getDocumentElement();
			
						String name = workspace.getAttribute(NAME);
						String version = workspace.getAttribute(VERSION);
						String description = workspace.getAttribute(DESCRIPTION);
						Log.info("Loading " + name + " version: " + version);
						
						WorkspaceEntry entry = new WorkspaceEntry(name);
						if (version != null) {
							entry.setVersion(version);
						}
						else if (description != null){
							entry.setDescription(description);
						}
						
						NodeList list = workspace.getElementsByTagName(LIBRARY);
						for (int i = 0; i < list.getLength(); ++i) {
							Element library = (Element)list.item(i);
							NodeList pathList = library.getElementsByTagName(PATH);
							
							for (int j = 0; j < pathList.getLength(); ++j) {
								Element pathElement = (Element)pathList.item(j);
								String pathStr = pathElement.getTextContent();
								
								File path = new File(file, pathStr);
								if(!path.exists()) {
									Log.error("Path " + path + " doesn't exist.");
								}
								else if (path.isDirectory()) {
									for(File subFile : path.listFiles(filter)) {
										Log.info(name + ": Adding jar " + subFile);
										entry.addClasspathResource(subFile);
									}
								}
								else {
									if( path.toString().toLowerCase().endsWith(".jar") ) {
										Log.info(name + ": Adding jar " + path);
										entry.addClasspathResource(path);
									}
									else {
										Log.error("Library path " + path.toString() + " is not a jar file.");
									}
								}
							}
						}
						
					} catch (ParserConfigurationException e) {
						Log.error("Could not parse " + file.getName() + ". " + e.getMessage());
						e.printStackTrace();
					} catch (SAXException e) {
						Log.error("Could not parse " + file.getName() + ". " + e.getMessage());
						e.printStackTrace();
					} catch (IOException e) {
						Log.error("Could not open " + file.getName() + ". " + e.getMessage());
						e.printStackTrace();
					}
				}
				else {
					Log.error("Could not find " + XML_FILE_NAME + " in " + file.getName());
				}
			}
		}
	}
	
	public class JarFilter implements FilenameFilter {
		public JarFilter() {
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".jar");
		}
	}
}