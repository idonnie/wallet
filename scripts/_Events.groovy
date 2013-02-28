
/**
 * Copy java.util.Logger logging.properties file to /WEB-INF/classes/ when WAR finishes packaging
 *
 */
eventPackagingEnd = {warName ->
	
	def classIo
	def classNubLogger
	def writeStringMethod
	def getLogsPathMethod
	
	try {
		println "Copying logging.properties to ${classesDirPath} ( /WEB-INF/classes )"
				
		// Load dynamically - workaround Grails does not compile Java files here
		classIo = classLoader.loadClass("com.antonkulyk.wallet.util.IO")
		classNubLogger = classLoader.loadClass("com.antonkulyk.wallet.log.NubLogger")

		ant.copy(toDir: "${classesDirPath}", filtering: true, overwrite: true) {
			fileset(file: "${basedir}/grails-app/conf/java.util.Logger/logging.properties")
		}
		println "Done"
		println "Put application.conf into place"
		ant.copy(file: "${basedir}/grails-app/conf/akka/application.conf",
			 toDir: "${classesDirPath}")
		println "Done"
		println "Put logback.xml into place"
		ant.copy(file: "${basedir}/grails-app/conf/akka/logback.xml",
			 toDir: "${classesDirPath}")
		println "Done"
		println "Put logback.properties into place"
		writeStringMethod = classIo.getMethod("writeString", (Class[])[File.class, String.class])
		getLogsPathMethod = classIo.getMethod("getLogsPath", (Class[])[])
		writeStringMethod.invoke(null, (Object[])[
			new File("${classesDirPath}/logback.properties")
			, "LOGS_HOME=" + getLogsPathMethod.invoke(null, (Object[])[])
		])
		println "Done"
		println "<><  <><  <><  <><  <><  <><  Application name: " + grails.util.Metadata.current.'app.name' + ". Application version: " + grails.util.Metadata.current.'app.version'
	} catch (Throwable t) {
		try {
			if (writeStringMethod == null) {
			    writeStringMethod = classIo.getMethod("writeString", (Class[])[File.class, String.class])
			}
			if (getLogsPathMethod == null) {
			    getLogsPathMethod = classIo.getMethod("getLogsPath", (Class[])[])
			}
			def toStringMethod = classNubLogger.getMethod("toStringWithTimestamp", (Class[])[Object.class])
			writeStringMethod.invoke(null, (Object[])[
				new File(getLogsPathMethod.invoke(null, (Object[])[]) + "/_events_groovy.log")
				, toStringMethod.invoke(null, (Object[])[t])
			])
			
		} catch (Throwable x) {
		
			println x.fillInStackTrace()
			
		} finally {
		
			// Dynamic
			classNubLogger.getMethod("handle", (Class[])[Throwable.class]).invoke(null, (Object[])[t])
			
		}
	}
	
}
