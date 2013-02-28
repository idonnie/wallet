package wallet.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.CodeSource;

import wallet.conf.Def$;


public final class IO {
	
	public static final int HDD_BUFF_SIZE_BYTE = 1024 << 2; // 4K
	public static final int HDD_BUFF_SIZE_CHAR = HDD_BUFF_SIZE_BYTE >> 1; // 4K
	
	private IO() {
		throw new UnsupportedOperationException();		
	}
	
	public static void writeString(final File file, final String data) {
		writeString(file, data, Def$.MODULE$.CHARSET());
	}
	
	public static void writeString(final File file, final String data, final Charset charset) {
		writeString(file, data, charset, true);
	}	
	
	public static void writeString(final File file, final String data, final Charset charset, final boolean append) {
		Fix.require(file != null, "file");
		Writer out = null;
		try {
			out = new PrintWriter(
				new BufferedWriter(
					new OutputStreamWriter(
						new FileOutputStream(file, append // append?
						), charset // encoding
					), HDD_BUFF_SIZE_CHAR // buffer size
				), true // autoflush?
			);
			out.write(data);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}				
				out = null;
			}
		}
	}
	
	/**
	 * Returns path to a class, for example: 
	 * "/opt/apache-tomcat-6.0.35/webapps/adm/WEB-INF/classes/com/directv/ads/monitoring/core"
	 */
	public static String getPath(Class<?> clazz) {
		Fix.require(clazz != null, "clazz");		
		CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
		if (codeSource == null) {
			throw new RuntimeException("No CodeSource associated with protection domain of class \"" + clazz + "\" (" + clazz.getClassLoader() + ")");
		}
		URL url = codeSource.getLocation();
		if (url == null) {
			throw new RuntimeException("No URL associated with CodeSource for protection domain of class \"" + clazz + "\" (" + clazz.getClassLoader() + ")");
		}
		String urlFileName = url.getFile();
		
		// Try remove class name from inside class file URL
		int pos = urlFileName.lastIndexOf("/" + clazz.getSimpleName() + ".class");
		if (pos == -1) {			
						
			// Already removed (for example in grails run-app), try remove trailing slash
			if (urlFileName.endsWith("/") && urlFileName.length() > 0) {				
				urlFileName = urlFileName.substring(0, urlFileName.length() - 1);
			}
			pos = urlFileName.length();
		}
		
		return urlFileName.substring(0, pos);
	}
	
	/**
	 * Returns path to a class minus package directories, for example:
	 * "/opt/apache-tomcat-6.0.35/webapps/adm/WEB-INF/classes"  
	 */
	public static String getClassesPath(Class<?> clazz) {
	    String fullPath = getPath(clazz);
	    String className = clazz.getName();
	    int pos = className.lastIndexOf('.');	    
		pos = fullPath.lastIndexOf("/" + (pos != -1 ? className.substring(0, pos) : "").replaceAll("\\.", "/"));
		
		// Try remove class package name from inside class file URL
		if (pos == -1) {
			
			// Already removed (for example in grails run-app), try remove trailing slash
			if (fullPath.endsWith("/") && fullPath.length() > 0) {				
				fullPath = fullPath.substring(0, fullPath.length() - 1);
			}
			pos = fullPath.length();
		}	    
		
	    return fullPath.substring(0, pos);
	}

	/**
	 * Returns path to a class <code>IO</code>, minus package directories, for example:
	 * "/opt/apache-tomcat-6.0.35/webapps/adm/WEB-INF/classes"  
	 */	
	public static String getClassesPath() {
		return getClassesPath(IO.class);
	}
	
	/**
	 * Returns path to a root directory, where web app containing particular class is deployed, for example:
	 * "/opt/apache-tomcat-6.0.35/webapps/adm" (Tomcat deployment)  
	 * "/home/akulyk/workspace/adm/monitoring_tools/core-prototype/com.directv.ads.monitoring.core" (grails run-app)
	 */		
	public static String getContainerPath(Class<?> clazz) {
		String classesPath = IO.getClassesPath(clazz);
		
		// Try find deployed web app root path
		int pos = classesPath.lastIndexOf("/WEB-INF/classes");
		if (pos == -1) {
			
			// Try find grails project of web app root path
			pos = classesPath.lastIndexOf("/target/classes");
			if (pos == -1) {
			    throw new RuntimeException("Invalid container path: \"" + classesPath + "\"");
			}
		}
		
		return classesPath.substring(0, pos);		
	}

	/**
	 * Returns path to a root directory, where web app containing class <code>IO</code> is deployed, for example:
	 * "/opt/apache-tomcat-6.0.35/webapps/adm"  
	 */			
	public static String getContainerPath() {
		return getContainerPath(IO.class);
	}
	
	/**
	 * Returns path to a logs directory
	 */
	public static String getLogsPath() {
		return getContainerPath() + Def$.MODULE$.LOGS_PATH();
	}
	
	/**
	 * Returns path to a logs directory
	 */
	public static String getDbPath() {
		return getContainerPath() + Def$.MODULE$.LOGS_PATH();
	}	
	
	/**
	 * Determine if particular class was deployed to container (otherwise, we are running from a Grails DEV environment)
	 */
	public static boolean isDeployed(Class<?> clazz) {
		return IO.getClassesPath(clazz).indexOf("WEB-INF") != -1;
	}

	/**
	 * Determine if application was deployed to container (otherwise, we are running from a Grails DEV environment)
	 */	
	public static boolean isDeployed() {
		return isDeployed(IO.class);
	}	
	
}