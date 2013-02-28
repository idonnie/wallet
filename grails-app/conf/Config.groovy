import org.apache.log4j.LogManager
import org.apache.log4j.spi.RepositorySelector

import com.antonkulyk.wallet.log.NubLogger
import com.antonkulyk.wallet.log.StacktraceRepositorySelector
import com.antonkulyk.wallet.util.IO

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

environments {

	try {

		// Used to access root application folder from inside app
		root_path = IO.getContainerPath()

		// Used to access logs folder from inside app
		log_path = IO.getLogsPath()

		def f = new File(IO.getLogsPath())
		if (f.exists()) {
			if (! f.isDirectory()) {
				f.delete()
				f = new File(IO.getLogsPath())
				if (! f.mkdirs()) {
					throw new IOException("Could not create logs directory")
				}
			}
		} else {
			if (! new File(IO.getLogsPath()).mkdirs()) {
				throw new IOException("Could not create logs directory")
			}
		}

		development {
			grails.logging.jul.usebridge = true
		}
		test {
			grails.logging.jul.usebridge = true
		}
		production {
			grails.logging.jul.usebridge = false
			// TODO: grails.serverURL = "http://www.changeme.com"
		}

	} catch (Throwable t) {
		try {
			IO.writeString(new File(IO.getLogsPath() + "/config_groovy_environments.log"), NubLogger.toStringWithTimestamp(t))
		} finally {
			NubLogger.handle(t);
		}
	}
}

// log4j configuration
log4j = {

	try {

		// Used by Log4J appenders
		log_path = IO.getLogsPath()

		appenders {

			rollingFile name:'file', file: log_path + "/monitor.log", threshold: org.apache.log4j.Level.ALL, maxFileSize:"128MB", maxBackupIndex: 100, "append": true, layout:pattern(conversionPattern: '%d [%t] %-5p %c{2} %x - %m%n')
			// file name: 'file', file: log_path + "/adsmon.log"

			rollingFile name: "stacktrace",
					file: log_path + "/stacktrace.log",
					threshold: org.apache.log4j.Level.ERROR,
					maxFileSize: "128MB",
					maxBackupIndex: 100,
					"append": true,
					layout:pattern(conversionPattern: '%d [%t] %-5p %c{2} %x - %m%n')

		}

		// Change actual log levels for dev/test/prod, for particular appenders, here:
		environments {
			development {
				root {
					info "stdout","stderr","file","stacktrace", //,"smtp"
							'org.codehaus.groovy.grails.web.servlet',  //  controllers
							'org.codehaus.groovy.grails.web.pages', //  GSP
							'org.codehaus.groovy.grails.web.sitemesh', //  layouts
							'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
							'org.codehaus.groovy.grails.web.mapping', // URL mapping
							'org.codehaus.groovy.grails.commons', // core / classloading
							'org.codehaus.groovy.grails.plugins', // plugins
							'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
							'org.springframework',
							'org.hibernate',
							'net.sf.ehcache.hibernate',
							'com.directv'

					additivity = true
				}
			}
			test {
				root {
					info "stdout","stderr","file","stacktrace", //,"smtp",
							'org.codehaus.groovy.grails.web.servlet',  //  controllers
							'org.codehaus.groovy.grails.web.pages', //  GSP
							'org.codehaus.groovy.grails.web.sitemesh', //  layouts
							'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
							'org.codehaus.groovy.grails.web.mapping', // URL mapping
							'org.codehaus.groovy.grails.commons', // core / classloading
							'org.codehaus.groovy.grails.plugins', // plugins
							'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
							'org.springframework',
							'org.hibernate',
							'net.sf.ehcache.hibernate',
							'com.directv'

					additivity = true
				}
			}
			production {
				root {					
					info "stdout","stderr","file","stacktrace", //,"smtp",
							'org.codehaus.groovy.grails.web.servlet',  //  controllers
							'org.codehaus.groovy.grails.web.pages', //  GSP
							'org.codehaus.groovy.grails.web.sitemesh', //  layouts
							'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
							'org.codehaus.groovy.grails.web.mapping', // URL mapping
							'org.codehaus.groovy.grails.commons', // core / classloading
							'org.codehaus.groovy.grails.plugins', // plugins
							'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
							'org.springframework',
							'org.hibernate',
							'net.sf.ehcache.hibernate',
							'com.directv'

					additivity = true
				}
			}

			LogManager.setRepositorySelector((RepositorySelector) (new StacktraceRepositorySelector()), (Object) null)

		}

		/*
		 root {
		 info 'file'
		 error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
		 'org.codehaus.groovy.grails.web.pages', //  GSP
		 'org.codehaus.groovy.grails.web.sitemesh', //  layouts
		 'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
		 'org.codehaus.groovy.grails.web.mapping', // URL mapping
		 'org.codehaus.groovy.grails.commons', // core / classloading
		 'org.codehaus.groovy.grails.plugins', // plugins
		 'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
		 'org.springframework',
		 'org.hibernate',
		 'net.sf.ehcache.hibernate'
		 }
		 */

	} catch (Throwable t) {
		try {
			IO.writeString(new File(IO.getLogsPath() + "/config_groovy_log4j.log"), NubLogger.toStringWithTimestamp(t))
		} finally {
			NubLogger.handle(t);
		}
	}
}
