package wallet

import java.math.BigDecimal
import java.math.BigDecimal
import java.math.BigDecimal
import java.util.List

import com.antonkulyk.wallet.server.ServerRoot

import org.apache.log4j.Logger
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.dao.DataAccessResourceFailureException
import org.springframework.orm.hibernate4.SessionHolder

import org.hibernate.FlushMode;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.antonkulyk.wallet.domain.Player
import com.antonkulyk.wallet.model.BalanceUpdate

class ServerController {

	private static Logger logger = Logger.getLogger(ServerController.class)
	
    def start() {
        try {
		  logger.info("------------- SERVER START --------")
		  ServerRoot.start()
		  render(view: "/manager/index")
		} catch (Throwable t) {
		    logger.error("Error in [S][START]: ", t)
		    throw t
		}		  
		  
    }

	def stop() {
	    try {
		  logger.info("------------- SERVER STOP --------")
		  ServerRoot.stop()
		  render(view: "/manager/index")
		} catch (Throwable t) {
		    logger.error("Error in [C][STOP]: ", t)
		    throw t
		}		  
		  
	}
	
	
	static def BalanceUpdate balanceUpdate(final String userName, final java.math.BigDecimal balDelta) {	
		boolean participate = false;
		def session = null
		def sessionFactory = null
		try {
			sessionFactory = new Player().domainClass.grailsApplication.mainContext.sessionFactory
			if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
				// Do not modify the Session: just set the participate flag.				
				session = sessionFactory.openSession()
				participate = true;
			} else {
				//log.debug("Opening single Hibernate Session in AbstractTransactionAwareMessageListener");
				session = getSession(sessionFactory);
				TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
			}
			return balanceUpdateWithSession(userName, balDelta, session)
		} finally {
			if (participate) {
				if (session != null) {
					session.close()
				}
		    } else {
			    if (sessionFactory != null) {
				    // single session mode
				    SessionHolder sessionHolder =
						(SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
				    //log.debug("Closing single Hibernate Session in AbstractTransactionAwareMessageListener");
				    SessionFactoryUtils.closeSession(sessionHolder.getSession());
			    }
			}
		}
	}
	
	static def Session getSession(SessionFactory sessionFactory) throws DataAccessResourceFailureException {
		def session = SessionFactoryUtils.getSession(sessionFactory, true);
		session.setFlushMode(FlushMode.COMMIT);
		return session;
	}

	static def BalanceUpdate balanceUpdateWithSession(String userName, java.math.BigDecimal balDelta, Session session) {
		def succ = false
		def tx = null
		try {
			
		    tx = session.beginTransaction()
			def next = new Player()
		    List players = session.createQuery("from Player p where p.userName = \'" + userName + "\'").list()
			def last = next
			for (Player player in players) {
				if (player.balVer > last.balVer) {
					last = player
				}
			}
			
			next.properties = ["userName": userName
				, "balVer": (last.balVer ? last.balVer : 0L) + 1L
				, "bal": (last.bal ? last.bal : new BigDecimal(0)) + balDelta]
			def balanceUpdate = null
			if (next.bal >= 0) {
			    System.out.println("SAVE SAVE SAVE " + next.userName + "," + next.balVer + "," + next.bal)				
			    next.save(flush: true)
				balanceUpdate = new BalanceUpdate(
					last.userName, last.balVer, last.bal,
					next.userName, next.balVer, next.bal
				)
			} else {
			
			    /*
			     * Client will be notified:
			     * next.balVer lasts the same
				 * next.bal NEGATIVE
				 */
			    balanceUpdate = new BalanceUpdate(
				    last.userName, last.balVer, last.bal,
			     	next.userName, last.balVer, next.bal
			    )
			}

			tx.commit()
			succ = true
			return balanceUpdate
			
		} finally {
		    if (!succ && tx != null) {
			    tx.rollback()
		    }
		}
		
	}
	
}
