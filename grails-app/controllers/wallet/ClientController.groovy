package wallet

import org.apache.log4j.Logger

import com.antonkulyk.wallet.client.ClientRoot
import com.antonkulyk.wallet.util.Str


class ClientController {
	
	private static Logger logger = Logger.getLogger(ClientController.class);
	
    def start() {
        try {
		  logger.info("------------- CLIENT START --------")
		  ClientRoot.start(Str.nil(params.username))
		  render(view: "/manager/index")
		} catch (Throwable t) {
		    logger.error("Error in [C][START]: ", t)
		    throw t
		}
    }

	def stop() {
	    try {
		  logger.info("------------- CLIENT STOP --------")
		  ClientRoot.stop(Str.nil(params.username))
		  render(view: "/manager/index")
		} catch (Throwable t) {
		    logger.error("Error in [C][STOP]: ", t)
		    throw t
		}		  
	}

}
