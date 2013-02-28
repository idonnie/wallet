package wallet.model;

import java.math.BigDecimal;

import wallet.ServerController;

import com.antonkulyk.wallet.domain.Player;

public final class BalanceUpdate {
	
	/**
	 * Its more convenient to use these 6 fields instead of 2 Player instances,
	 * because of Grails -> Java -> Scala, 
	 * and also we need immutable class to send messages between threads
	 */
    private final String from_userName;
    private final long from_balVer;
    private final java.math.BigDecimal from_bal;
    
    private final String to_userName;
    private final long to_balVer;
    private final java.math.BigDecimal to_bal;	    
	
	public BalanceUpdate(
	    String from_userName
	    ,long from_balVer
	    ,java.math.BigDecimal from_bal
	    ,String to_userName
	    ,long to_balVer
	    ,java.math.BigDecimal to_bal	    
	) {
		this.from_userName = from_userName;
		this.from_balVer = from_balVer;
		this.from_bal = from_bal;
		this.to_userName = to_userName;
		this.to_balVer = to_balVer;
		this.to_bal = to_bal;	
	}
	
	public BalanceUpdate() {
		this(null, 0L, null, null, 0L, null);
	}	
	
	public static BalanceUpdate balanceUpdate(String userName, BigDecimal balDelta) {
		return ServerController.balanceUpdate(userName, balDelta);
	}
	
	@Override
	public String toString() {
		return "(userName=" + from_userName +
		        ",balVer=" + from_balVer +
		        ",balance=" + from_bal + ")" +
		        " => " +
		        "(userName=" + to_userName +
		        ",balVer=" + to_balVer +
		        ",balance=" + to_bal + ")";
	}

	public String getFromUserName() {
		return from_userName;
	}

	public long getFromBalVer() {
		return from_balVer;
	}
	
	public java.math.BigDecimal getFromBal() {
		return from_bal;
	}

	public String getToUserName() {
		return to_userName;
	}

	public long getToBalVer() {
		return to_balVer;
	}
	
	public java.math.BigDecimal getToBal() {
		return to_bal;
	}
	
}
