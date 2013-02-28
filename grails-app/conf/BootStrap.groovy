import com.antonkulyk.wallet.log.NubLogger
import com.antonkulyk.wallet.util.IO

class BootStrap {

	def init = { servletContext ->
		try {
			log.info "><>  ><>  ><>  ><>  ><>  ><>  Application name: " + grails.util.Metadata.current.'app.name' + ". Application version: " + grails.util.Metadata.current.'app.version'
		} catch (Throwable t) {
			try {
				IO.writeString(new File(IO.getLogsPath() + "/boot_strap_init_groovy.log"), NubLogger.toStringWithTimestamp(t))
			} finally {
				NubLogger.handle(t);
			}
		}
	}
	
    def destroy = {
    }
}
