package com.antonkulyk.wallet.domain

class Player {

	static constraints = {
		id(unique: ["userName", "balVer"])
	}
	
	String userName
	Long balVer
	java.math.BigDecimal bal
	
}
